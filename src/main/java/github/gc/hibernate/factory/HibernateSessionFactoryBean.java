package github.gc.hibernate.factory;

import org.hibernate.Interceptor;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

import javax.sql.DataSource;
import java.util.Properties;

public class HibernateSessionFactoryBean implements FactoryBean<SessionFactory>, InitializingBean, DisposableBean {

    private DataSource dataSource;

    private Interceptor entityInterceptor;

    private Properties hibernateProperties;

    private String[] packagesToScan;

    private SessionFactory sessionFactory;

    @Override
    public void afterPropertiesSet() {
        HibernateSessionFactoryBuilder sfb = new HibernateSessionFactoryBuilder(this.dataSource);

        if (this.entityInterceptor != null) {
            sfb.setInterceptor(this.entityInterceptor);
        }

        if (this.hibernateProperties != null) {
            sfb.addProperties(this.hibernateProperties);
        }

        if (this.packagesToScan != null) {
            sfb.scanPackages(this.packagesToScan);
        }

        this.sessionFactory = sfb.buildSessionFactory();
    }

    @Override
    public SessionFactory getObject() {
        if (this.sessionFactory == null) {
            afterPropertiesSet();
        }

        return this.sessionFactory;
    }

    @Override
    public Class<?> getObjectType() {
        return (this.sessionFactory != null ? this.sessionFactory.getClass() : SessionFactory.class);
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    public void destroy() {
        if (this.sessionFactory != null) {
            this.sessionFactory.close();
        }
    }

    public Properties getHibernateProperties() {
        if (this.hibernateProperties == null) {
            this.hibernateProperties = new Properties();
        }
        return this.hibernateProperties;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void setEntityInterceptor(Interceptor entityInterceptor) {
        this.entityInterceptor = entityInterceptor;
    }

    public void setHibernateProperties(Properties hibernateProperties) {
        this.hibernateProperties = hibernateProperties;
    }

    public void setPackagesToScan(String[] packagesToScan) {
        this.packagesToScan = packagesToScan;
    }
}
