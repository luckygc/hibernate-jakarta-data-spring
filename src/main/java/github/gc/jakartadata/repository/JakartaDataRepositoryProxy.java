package github.gc.jakartadata.repository;

import github.gc.jakartadata.ExceptionUtil;
import github.gc.jakartadata.session.StatelessSessionUtils;
import java.io.Serial;
import java.io.Serializable;
import java.lang.invoke.CallSite;
import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.function.Function;
import javax.sql.DataSource;
import org.hibernate.SessionFactory;
import org.hibernate.StatelessSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * Jakarta Data Repository 代理类 负责拦截 Repository 方法调用并委托给实际的实现
 *
 * @author gc
 */
public class JakartaDataRepositoryProxy<T, I extends T> implements InvocationHandler, Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private static final Logger log = LoggerFactory.getLogger(JakartaDataRepositoryProxy.class);

    private static final ThreadLocal<StatelessSession> threadLocalSession = new ThreadLocal<>();

    private final Function<StatelessSession, I> constructorInvoker;
    private final SessionFactory sessionFactory;
    private final DataSource dataSource;

    public JakartaDataRepositoryProxy(@NonNull Class<T> repositoryInterface, @NonNull SessionFactory sessionFactory,
        @NonNull DataSource dataSource) {
        this.sessionFactory = sessionFactory;
        this.dataSource = dataSource;

        var implementationClass = getImplementationClass(repositoryInterface);
        this.constructorInvoker = createConstructorInvoker(implementationClass);
    }

    /**
     * 获取 Hibernate 生成的实现类
     */
    @SuppressWarnings("unchecked")
    private Class<I> getImplementationClass(Class<T> repositoryInterface) {
        try {
            String implementationClassName = repositoryInterface.getName() + "_";
            return (Class<I>) Class.forName(implementationClassName);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Cannot find implementation class for repository: " +
                repositoryInterface.getName(), e);
        }
    }

    /**
     * 使用 LambdaMetafactory 创建构造函数调用器
     */
    private Function<StatelessSession, I> createConstructorInvoker(Class<I> implementationClass) {
        try {
            // 获取 MethodHandles.Lookup
            MethodHandles.Lookup lookup = MethodHandles.lookup();

            // 创建构造函数的 MethodHandle
            MethodHandle constructorHandle = lookup.findConstructor(implementationClass,
                MethodType.methodType(void.class, StatelessSession.class));

            // 使用 LambdaMetafactory 创建 Function
            CallSite callSite = LambdaMetafactory.metafactory(
                lookup,
                "apply",                                                    // 函数式接口方法名
                MethodType.methodType(Function.class),                     // 调用点类型
                MethodType.methodType(Object.class, Object.class),         // 函数式接口方法类型 (Function.apply的签名)
                constructorHandle,                                          // 实际实现的方法句柄
                MethodType.methodType(implementationClass, StatelessSession.class) // 实际方法类型
            );

            @SuppressWarnings("unchecked")
            Function<StatelessSession, I> invoker = (Function<StatelessSession, I>) callSite.getTarget()
                .invokeExact();

            return invoker;

        } catch (Throwable e) {
            throw new RuntimeException("Cannot create constructor invoker for class: " +
                implementationClass.getName(), e);
        }
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        StatelessSession session = null;
        boolean isSynchronizationActive = TransactionSynchronizationManager.isSynchronizationActive();

        try {
            // 处理 Object 类的方法
            if (Object.class.equals(method.getDeclaringClass())) {
                return method.invoke(this, args);
            }

            if (isSynchronizationActive) {
                session = StatelessSessionUtils.getTransactionalSession(sessionFactory, dataSource);
            } else if (threadLocalSession.get() != null) {
                session = threadLocalSession.get();
            } else {
                session = sessionFactory.openStatelessSession();
                threadLocalSession.set(session);
            }

            Object repositoryImpl = constructorInvoker.apply(session);

            return method.invoke(repositoryImpl, args);
        } catch (Throwable t) {
            throw ExceptionUtil.unwrapThrowable(t);
        } finally {
            if (session != null && !isSynchronizationActive) {
                StatelessSessionUtils.closeSession(session);
                threadLocalSession.remove();
            }
        }
    }
}
