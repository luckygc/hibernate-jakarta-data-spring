package github.gc.jakartadata.factory;

import github.gc.jakartadata.proxy.HibernateDataRepositoryProxyFactory;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;

import javax.sql.DataSource;

/**
 * Hibernate Data Repository 工厂 Bean
 * 负责创建 Repository 接口的代理实例
 */
public class HibernateDataRepositoryFactoryBean<T> implements FactoryBean<T>, InitializingBean {

    private static final Logger log = LoggerFactory.getLogger(HibernateDataRepositoryFactoryBean.class);

    private final Class<T> repositoryInterface;
    
    @Autowired
    private SessionFactory sessionFactory;
    
    @Autowired
    private DataSource dataSource;
    
    private HibernateDataRepositoryProxyFactory<T> proxyFactory;

    public HibernateDataRepositoryFactoryBean(@NonNull Class<T> repositoryInterface) {
        this.repositoryInterface = repositoryInterface;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (sessionFactory == null) {
            throw new IllegalStateException("SessionFactory is required for HibernateDataRepositoryFactoryBean");
        }
        
        if (dataSource == null) {
            throw new IllegalStateException("DataSource is required for HibernateDataRepositoryFactoryBean");
        }
        
        this.proxyFactory = new HibernateDataRepositoryProxyFactory<>(
            repositoryInterface, sessionFactory, dataSource);
        
        log.debug("Initialized HibernateDataRepositoryFactoryBean for interface: {}", 
                 repositoryInterface.getName());
    }

    @Override
    public T getObject() throws Exception {
        if (proxyFactory == null) {
            throw new IllegalStateException("ProxyFactory not initialized. Call afterPropertiesSet() first.");
        }
        
        T repository = proxyFactory.createRepository();
        
        log.debug("Created repository instance for interface: {}", repositoryInterface.getName());
        
        return repository;
    }

    @Override
    public Class<?> getObjectType() {
        return repositoryInterface;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    // Getters for testing or manual configuration
    public Class<T> getRepositoryInterface() {
        return repositoryInterface;
    }

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public DataSource getDataSource() {
        return dataSource;
    }
    
    // Setters for manual configuration (if needed)
    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
    
    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }
}
