package github.gc.jakartadata.proxy;

import github.gc.jakartadata.utils.HibernateRepositoryUtils;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;

import javax.sql.DataSource;
import java.lang.reflect.Proxy;

/**
 * Hibernate Data Repository 代理工厂
 * 负责创建 Repository 接口的动态代理实例
 */
public class HibernateDataRepositoryProxyFactory<T> {

    private static final Logger log = LoggerFactory.getLogger(HibernateDataRepositoryProxyFactory.class);

    private final Class<T> repositoryInterface;
    private final SessionFactory sessionFactory;
    private final DataSource dataSource;

    public HibernateDataRepositoryProxyFactory(@NonNull Class<T> repositoryInterface,
                                             @NonNull SessionFactory sessionFactory,
                                             @NonNull DataSource dataSource) {
        this.repositoryInterface = repositoryInterface;
        this.sessionFactory = sessionFactory;
        this.dataSource = dataSource;
    }

    /**
     * 创建 Repository 代理实例
     */
    @SuppressWarnings("unchecked")
    public T createRepository() {
        try {
            // 获取 Hibernate 生成的实现类
            Class<? extends T> implementationClass = HibernateRepositoryUtils
                .getRepositoryImplementationClass(repositoryInterface);
            
            // 创建调用处理器
            HibernateDataRepositoryInvocationHandler<T> handler = 
                new HibernateDataRepositoryInvocationHandler<>(
                    repositoryInterface, implementationClass, sessionFactory, dataSource);
            
            // 创建代理实例
            T proxy = (T) Proxy.newProxyInstance(
                repositoryInterface.getClassLoader(),
                new Class<?>[]{repositoryInterface},
                handler
            );
            
            log.debug("Created proxy for repository interface: {}", repositoryInterface.getName());
            
            return proxy;
            
        } catch (Exception e) {
            log.error("Failed to create repository proxy for interface: {}", 
                     repositoryInterface.getName(), e);
            throw new RuntimeException("Failed to create repository proxy", e);
        }
    }

    // Getters for testing
    public Class<T> getRepositoryInterface() {
        return repositoryInterface;
    }

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public DataSource getDataSource() {
        return dataSource;
    }
}
