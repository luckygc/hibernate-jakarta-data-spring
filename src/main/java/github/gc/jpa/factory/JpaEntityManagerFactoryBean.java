package github.gc.jpa.factory;

import jakarta.persistence.EntityManagerFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.service.ServiceRegistry;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * 基于 Hibernate 原生 JPA 的 EntityManagerFactory 工厂Bean
 * 不依赖 Spring ORM，直接使用 Hibernate JPA 实现
 */
public class JpaEntityManagerFactoryBean implements FactoryBean<EntityManagerFactory>, InitializingBean, DisposableBean {

    private DataSource dataSource;
    private Properties jpaProperties;
    private String[] packagesToScan;
    private EntityManagerFactory entityManagerFactory;
    private ServiceRegistry serviceRegistry;

    @Override
    public void afterPropertiesSet() {
        JpaEntityManagerFactoryBuilder builder = new JpaEntityManagerFactoryBuilder(this.dataSource);
        
        if (this.jpaProperties != null) {
            builder.addProperties(this.jpaProperties);
        }
        
        if (this.packagesToScan != null) {
            builder.scanPackages(this.packagesToScan);
        }
        
        this.entityManagerFactory = builder.buildEntityManagerFactory();
        this.serviceRegistry = builder.getServiceRegistry();
    }

    @Override
    public EntityManagerFactory getObject() {
        if (this.entityManagerFactory == null) {
            afterPropertiesSet();
        }
        return this.entityManagerFactory;
    }

    @Override
    public Class<?> getObjectType() {
        return EntityManagerFactory.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    public void destroy() {
        if (this.entityManagerFactory != null && this.entityManagerFactory.isOpen()) {
            this.entityManagerFactory.close();
        }
        if (this.serviceRegistry != null) {
            StandardServiceRegistryBuilder.destroy(this.serviceRegistry);
        }
    }

    public Properties getJpaProperties() {
        if (this.jpaProperties == null) {
            this.jpaProperties = new Properties();
        }
        return this.jpaProperties;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void setJpaProperties(Properties jpaProperties) {
        this.jpaProperties = jpaProperties;
    }

    public void setPackagesToScan(String[] packagesToScan) {
        this.packagesToScan = packagesToScan;
    }
}
