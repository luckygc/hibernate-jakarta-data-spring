package github.gc.jakartadata.proxy;

import github.gc.jakartadata.session.HibernateSessionUtils;
import github.gc.jakartadata.wrapper.QueryWrapper;
import org.hibernate.SessionFactory;
import org.hibernate.StatelessSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;

import javax.sql.DataSource;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * Hibernate Data Repository 调用处理器
 * 负责处理 Repository 接口方法的调用，管理 Hibernate Session 生命周期
 */
public class HibernateDataRepositoryInvocationHandler<T> implements InvocationHandler {

    private static final Logger log = LoggerFactory.getLogger(HibernateDataRepositoryInvocationHandler.class);

    private final Class<T> repositoryInterface;
    private final Class<? extends T> implementationClass;
    private final SessionFactory sessionFactory;
    private final DataSource dataSource;

    public HibernateDataRepositoryInvocationHandler(@NonNull Class<T> repositoryInterface,
                                                   @NonNull Class<? extends T> implementationClass,
                                                   @NonNull SessionFactory sessionFactory,
                                                   @NonNull DataSource dataSource) {
        this.repositoryInterface = repositoryInterface;
        this.implementationClass = implementationClass;
        this.sessionFactory = sessionFactory;
        this.dataSource = dataSource;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // 处理 Object 类的方法
        if (Object.class.equals(method.getDeclaringClass())) {
            return handleObjectMethod(proxy, method, args);
        }

        // 获取或创建 StatelessSession
        StatelessSession session = HibernateSessionUtils
            .getTransactionalStatelessSession(sessionFactory, dataSource);
        
        boolean isNewSession = (session == null);
        
        if (isNewSession) {
            session = sessionFactory.openStatelessSession();
            log.debug("Created new StatelessSession for repository method: {}.{}",
                     repositoryInterface.getSimpleName(), method.getName());
        }

        try {
            // 创建 Repository 实现实例
            T repositoryImpl = createRepositoryImplementation(session);
            
            // 调用实际方法
            Object result = method.invoke(repositoryImpl, args);
            
            log.trace("Successfully executed repository method: {}.{}",
                     repositoryInterface.getSimpleName(), method.getName());

            // 如果是新创建的 Session 且返回值是 Query 类型，需要包装以延迟关闭
            if (isNewSession && isQueryType(result)) {
                return QueryWrapper.wrapWithDeferredClose(result, session);
            }

            return result;
            
        } catch (Exception e) {
            log.error("Error executing repository method: {}.{}", 
                     repositoryInterface.getSimpleName(), method.getName(), e);
            throw e;
        } finally {
            // 如果是新创建的 Session 且返回值不是 Query 类型，立即关闭
            if (isNewSession && session != null) {
                Object result = null;
                try {
                    // 重新获取结果以检查类型
                    T repositoryImpl = createRepositoryImplementation(session);
                    result = method.invoke(repositoryImpl, args);
                } catch (Exception ignored) {
                    // 忽略异常，因为主要目的是检查返回类型
                }
                
                if (!isQueryType(result)) {
                    HibernateSessionUtils.closeStatelessSession(session);
                    log.debug("Closed StatelessSession for repository method: {}.{}",
                             repositoryInterface.getSimpleName(), method.getName());
                }
            }
        }
    }

    /**
     * 处理 Object 类的方法
     */
    private Object handleObjectMethod(Object proxy, Method method, Object[] args) throws Throwable {
        String methodName = method.getName();
        
        switch (methodName) {
            case "equals":
                return proxy == args[0];
            case "hashCode":
                return System.identityHashCode(proxy);
            case "toString":
                return repositoryInterface.getName() + " proxy";
            default:
                return method.invoke(this, args);
        }
    }

    /**
     * 创建 Repository 实现实例
     */
    private T createRepositoryImplementation(StatelessSession session) throws Exception {
        return implementationClass.getDeclaredConstructor(StatelessSession.class)
                                 .newInstance(session);
    }

    /**
     * 检查返回值是否为 Query 类型
     */
    private boolean isQueryType(Object result) {
        if (result == null) {
            return false;
        }
        
        Class<?> resultClass = result.getClass();
        
        // 检查是否为 Hibernate Query 相关类型
        return org.hibernate.query.Query.class.isAssignableFrom(resultClass) ||
               org.hibernate.query.SelectionQuery.class.isAssignableFrom(resultClass) ||
               org.hibernate.query.MutationQuery.class.isAssignableFrom(resultClass);
    }
}
