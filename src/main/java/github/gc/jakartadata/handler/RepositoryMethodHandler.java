package github.gc.jakartadata.handler;

import github.gc.jakartadata.session.SessionManager;
import org.hibernate.SessionFactory;
import org.hibernate.StatelessSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;

import javax.sql.DataSource;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * Repository 方法处理器
 * 负责处理具体的 Repository 方法调用
 * 
 * @author gc
 */
public class RepositoryMethodHandler {

    private static final Logger log = LoggerFactory.getLogger(RepositoryMethodHandler.class);

    private final Class<?> repositoryInterface;
    private final Method method;
    private final SessionManager sessionManager;
    private final Class<?> implementationClass;
    private final Constructor<?> implementationConstructor;

    public RepositoryMethodHandler(@NonNull Class<?> repositoryInterface,
                                 @NonNull Method method,
                                 @NonNull SessionFactory sessionFactory,
                                 @NonNull DataSource dataSource) {
        this.repositoryInterface = repositoryInterface;
        this.method = method;
        this.sessionManager = new SessionManager(sessionFactory, dataSource);
        
        // 获取 Hibernate 生成的实现类
        this.implementationClass = getImplementationClass(repositoryInterface);
        this.implementationConstructor = getImplementationConstructor(implementationClass);
        
        log.debug("Created method handler for {}.{}, implementation: {}", 
                 repositoryInterface.getSimpleName(), method.getName(), implementationClass.getSimpleName());
    }

    /**
     * 执行方法
     */
    public Object execute(Object[] args) throws Throwable {
        // 获取或创建 StatelessSession
        StatelessSession session = sessionManager.getSession();
        boolean shouldCloseSession = sessionManager.shouldCloseSession();
        
        try {
            // 创建 Repository 实现实例
            Object repositoryImpl = implementationConstructor.newInstance(session);
            
            // 调用实际方法
            Object result = method.invoke(repositoryImpl, args);
            
            log.trace("Successfully executed repository method: {}.{}", 
                     repositoryInterface.getSimpleName(), method.getName());
            
            return result;
            
        } catch (Exception e) {
            log.error("Error executing repository method: {}.{}", 
                     repositoryInterface.getSimpleName(), method.getName(), e);
            throw e;
        } finally {
            // 如果需要关闭 Session
            if (shouldCloseSession) {
                sessionManager.closeSession(session);
            }
        }
    }

    /**
     * 获取 Hibernate 生成的实现类
     */
    private Class<?> getImplementationClass(Class<?> repositoryInterface) {
        try {
            String implementationClassName = repositoryInterface.getName() + "_";
            return Class.forName(implementationClassName);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Cannot find implementation class for repository: " + 
                                     repositoryInterface.getName(), e);
        }
    }

    /**
     * 获取实现类的构造函数
     */
    private Constructor<?> getImplementationConstructor(Class<?> implementationClass) {
        try {
            return implementationClass.getDeclaredConstructor(StatelessSession.class);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Cannot find constructor with StatelessSession parameter for class: " + 
                                     implementationClass.getName(), e);
        }
    }
}
