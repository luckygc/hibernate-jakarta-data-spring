package github.gc.jakartadata.repository;

import github.gc.hibernate.session.StatelessSessionUtils;
import github.gc.hibernate.session.proxy.impl.MutationQueryProxy;
import github.gc.hibernate.session.proxy.impl.NativeQueryProxy;
import github.gc.hibernate.session.proxy.impl.QueryProxy;
import github.gc.hibernate.session.proxy.impl.SelectionQueryProxy;
import org.hibernate.StatelessSession;
import org.hibernate.SessionFactory;
import org.hibernate.query.MutationQuery;
import org.hibernate.query.NativeQuery;
import org.hibernate.query.Query;
import org.hibernate.query.SelectionQuery;
import javax.sql.DataSource;
import org.jspecify.annotations.NonNull;
import org.springframework.util.Assert;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings("unchecked")
public final class RepositoryUtils {

	private RepositoryUtils() {
	}

	private static final Map<Class<?>, Class<?>> repositoryImplClasses = new ConcurrentHashMap<>(64);

    @NonNull
    public static <R> R createRepository(@NonNull Class<R> repositoryInterfaceClass,
                                         @NonNull SessionFactory sessionFactory,
                                         @NonNull DataSource dataSource) {
        Assert.notNull(repositoryInterfaceClass, "repositoryInterfaceClass must not be null");
        Assert.notNull(sessionFactory, "sessionFactory must not be null");
        Assert.notNull(dataSource, "dataSource must not be null");

        Class<R> repositoryImplClass = getRepositoryImplClass(repositoryInterfaceClass);

        InvocationHandler repoHandler = new RepositoryInvocationHandler<>(repositoryImplClass,
                sessionFactory, dataSource);

        return (R) Proxy.newProxyInstance(repositoryInterfaceClass.getClassLoader(),
                new Class<?>[]{repositoryInterfaceClass}, repoHandler);
    }

	public static <R, I extends R> Class<I> getRepositoryImplClass(@NonNull Class<R> repositoryInterface) {
		Assert.notNull(repositoryInterface, "repositoryInterface must not be null");

		return (Class<I>) repositoryImplClasses.computeIfAbsent(repositoryInterface,
				RepositoryUtils::doGetRepositoryImplClass);
	}

        private static Class<?> doGetRepositoryImplClass(Class<?> repositoryInterface) {
                String interfaceName = repositoryInterface.getName();
                // hibernate-processor生成的Repository实现类
                String implName = interfaceName + "_";
                try {
                        return Class.forName(implName);
                } catch (Exception e) {
                        throw new RuntimeException("加载Jakarta Data Repository实现类[%s]失败".formatted(implName), e);
                }
        }

        private static class RepositoryInvocationHandler<R> implements InvocationHandler {
                private final Class<R> implClass;
                private final SessionFactory sessionFactory;
                private final DataSource dataSource;
                private final Constructor<R> constructor;

                RepositoryInvocationHandler(Class<R> implClass, SessionFactory sessionFactory, DataSource dataSource) {
                        this.implClass = implClass;
                        this.sessionFactory = sessionFactory;
                        this.dataSource = dataSource;
                        try {
                                this.constructor = implClass.getConstructor(StatelessSession.class);
                        } catch (NoSuchMethodException e) {
                                throw new IllegalStateException("No constructor found taking StatelessSession", e);
                        }
                }

                @Override
                public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        StatelessSession session = StatelessSessionUtils.doGetTransactionalStatelessSession(sessionFactory, dataSource);
                        boolean transactional = session != null;
                        boolean isNew = false;
                        if (session == null) {
                                session = sessionFactory.openStatelessSession();
                                isNew = true;
                        }

                        R target = constructor.newInstance(session);
                        Object result;
                        try {
                                result = method.invoke(target, args);
                        } finally {
                                if (isNew && !(method.getReturnType().isAssignableFrom(StatelessSession.class)
                                        || Query.class.isAssignableFrom(method.getReturnType()))) {
                                        StatelessSessionUtils.closeStatelessSession(session);
                                }
                        }

                        if (isNew && Query.class.isAssignableFrom(method.getReturnType()) && result instanceof Query<?> q) {
                                return new QueryProxy<>(q, session).getProxy();
                        }
                        if (isNew && SelectionQuery.class.isAssignableFrom(method.getReturnType()) && result instanceof SelectionQuery<?> sq) {
                                return new SelectionQueryProxy<>(sq, session).getProxy();
                        }
                        if (isNew && MutationQuery.class.isAssignableFrom(method.getReturnType()) && result instanceof MutationQuery mq) {
                                return new MutationQueryProxy(mq, session).getProxy();
                        }
                        if (isNew && NativeQuery.class.isAssignableFrom(method.getReturnType()) && result instanceof NativeQuery<?> nq) {
                                return new NativeQueryProxy<>(nq, session).getProxy();
                        }
                        // if returns session and we created it, don't close; caller is responsible
                        return result;
                }
        }
}
