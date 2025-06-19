package github.gc.jakartadata.repository;

import github.gc.hibernate.session.StatelessSessionUtils;
import github.gc.hibernate.session.proxy.impl.StatelessSessionProxyImpl;
import org.hibernate.StatelessSession;
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
                    @NonNull StatelessSession statelessSession) {
        Assert.notNull(repositoryInterfaceClass, "repositoryInterfaceClass must not be null");
        Assert.notNull(statelessSession, "statelessSession must not be null");

        Class<R> repositoryImplClass = getRepositoryImplClass(repositoryInterfaceClass);

        try {
            Constructor<R> constructor = repositoryImplClass.getConstructor(StatelessSession.class);
            R target = constructor.newInstance(statelessSession);

            StatelessSessionProxyImpl handler =
                    (StatelessSessionProxyImpl) Proxy.getInvocationHandler(statelessSession);

            InvocationHandler repoHandler = new RepositoryInvocationHandler<>(target, handler);

            return (R) Proxy.newProxyInstance(repositoryInterfaceClass.getClassLoader(),
                    new Class<?>[]{repositoryInterfaceClass}, repoHandler);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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

        private static class RepositoryInvocationHandler<T> implements InvocationHandler {
                private final T target;
                private final StatelessSessionProxyImpl sessionHandler;

                RepositoryInvocationHandler(T target, StatelessSessionProxyImpl sessionHandler) {
                        this.target = target;
                        this.sessionHandler = sessionHandler;
                }

                @Override
                public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        var sf = sessionHandler.getSessionFactory();
                        var ds = sessionHandler.getDataSource();
                        StatelessSession session = StatelessSessionUtils.doGetTransactionalStatelessSession(sf, ds);
                        boolean isNew = false;
                        if (session == null) {
                                session = sf.openStatelessSession();
                                isNew = true;
                        }

                        sessionHandler.bind(session);
                        try {
                                return method.invoke(target, args);
                        } finally {
                                sessionHandler.unbind();
                                if (isNew) {
                                        StatelessSessionUtils.closeStatelessSession(session);
                                }
                        }
                }
        }
}
