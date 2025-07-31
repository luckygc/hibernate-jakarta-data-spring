package github.luckygc.jakartadata.provider.hibernate;

import github.luckygc.jakartadata.ExceptionUtil;

import org.hibernate.SessionFactory;
import org.hibernate.StatelessSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.lang.NonNull;
import org.springframework.transaction.support.TransactionSynchronizationManager;

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

/**
 * Jakarta Data Repository 代理类
 *
 * <p>负责拦截Repository方法调用并委托给Hibernate生成的实际实现类。
 * 该代理类管理StatelessSession的生命周期，支持事务性和非事务性操作。
 *
 * <p>主要功能：
 * <ul>
 *   <li>动态创建Hibernate生成的Repository实现类实例</li>
 *   <li>管理StatelessSession的创建、绑定和释放</li>
 *   <li>支持Spring事务管理</li>
 *   <li>提供线程安全的Session访问</li>
 * </ul>
 *
 * @param <T> Repository接口类型
 * @param <I> Hibernate生成的实现类类型
 * @author luckygc
 */
public class HibernateRepositoryProxy<T, I extends T> implements InvocationHandler, Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private static final Logger log = LoggerFactory.getLogger(HibernateRepositoryProxy.class);

    /** 线程本地Session存储，用于非事务性操作 */
    private static final ThreadLocal<StatelessSession> threadLocalSession = new ThreadLocal<>();

    /** 构造函数调用器，用于创建Repository实现类实例 */
    private final Function<StatelessSession, I> constructorInvoker;

    /** Hibernate SessionFactory */
    private final SessionFactory sessionFactory;

    /** 数据源 */
    private final DataSource dataSource;

    /**
     * 构造函数
     *
     * @param repositoryInterface Repository接口类型
     * @param beanFactory Spring Bean工厂，用于获取SessionFactory和DataSource
     */
    public HibernateRepositoryProxy(@NonNull Class<T> repositoryInterface, @NonNull BeanFactory beanFactory) {
        this.sessionFactory = beanFactory.getBean(SessionFactory.class);
        this.dataSource = beanFactory.getBean(DataSource.class);

        var implementationClass = getImplementationClass(repositoryInterface);
        this.constructorInvoker = createConstructorInvoker(implementationClass);
    }

    /**
     * 获取Hibernate生成的实现类
     *
     * <p>Hibernate会为每个Repository接口生成一个以"_"结尾的实现类。
     *
     * @param repositoryInterface Repository接口类型
     * @return Hibernate生成的实现类
     * @throws IllegalStateException 如果找不到实现类
     */
    @SuppressWarnings("unchecked")
    private Class<I> getImplementationClass(Class<T> repositoryInterface) {
        try {
            String implementationClassName = repositoryInterface.getName() + "_";
            return (Class<I>) Class.forName(implementationClassName);
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException(
                String.format("无法找到Repository接口 '%s' 的Hibernate生成实现类 '%s_'。"
                    + "请确保已正确配置Hibernate注解处理器并重新编译项目。",
                    repositoryInterface.getName(), repositoryInterface.getName()), e);
        }
    }

    /**
     * 使用LambdaMetafactory创建构造函数调用器
     *
     * <p>通过方法句柄和Lambda表达式创建高性能的构造函数调用器，
     * 避免反射调用的性能开销。
     *
     * @param implementationClass Hibernate生成的实现类
     * @return 构造函数调用器
     * @throws IllegalStateException 如果无法创建调用器
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

            Function<StatelessSession, I> invoker = (Function<StatelessSession, I>) callSite.getTarget()
                .invokeExact();

            return invoker;

        } catch (Throwable e) {
            throw new IllegalStateException(
                String.format("无法为实现类 '%s' 创建构造函数调用器。"
                    + "这通常表示实现类缺少期望的构造函数 (StatelessSession session)。",
                    implementationClass.getName()), e);
        }
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // 处理Object类的方法，避免不必要的Session创建
        if (Object.class.equals(method.getDeclaringClass())) {
            return method.invoke(this, args);
        }

        StatelessSession session = null;
        boolean isSynchronizationActive = TransactionSynchronizationManager.isSynchronizationActive();
        boolean sessionCreatedHere = false;

        try {
            // 获取或创建Session
            if (isSynchronizationActive) {
                // 事务性操作：使用事务绑定的Session
                session = StatelessSessionUtils.getTransactionalSession(sessionFactory, dataSource);
            } else {
                // 非事务性操作：使用线程本地Session
                session = threadLocalSession.get();
                if (session == null || !session.isConnected()) {
                    session = sessionFactory.openStatelessSession();
                    threadLocalSession.set(session);
                    sessionCreatedHere = true;
                    log.debug("创建新的非事务性StatelessSession");
                }
            }

            // 创建Repository实现实例并调用方法
            Object repositoryImpl = constructorInvoker.apply(session);
            return method.invoke(repositoryImpl, args);

        } catch (Throwable t) {
            throw ExceptionUtil.unwrapThrowable(t);
        } finally {
            // 清理非事务性Session
            if (!isSynchronizationActive && sessionCreatedHere) {
                StatelessSessionUtils.closeSession(session);
                threadLocalSession.remove();
                log.debug("清理非事务性StatelessSession");
            }
        }
    }
}
