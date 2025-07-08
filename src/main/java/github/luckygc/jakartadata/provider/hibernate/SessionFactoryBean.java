package github.luckygc.jakartadata.provider.hibernate;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.jpa.HibernatePersistenceConfiguration;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

import javax.sql.DataSource;

public class SessionFactoryBean
        implements FactoryBean<SessionFactory>, InitializingBean, DisposableBean {

    private DataSource dataSource;

    private String[] packagesToScan;

    private SessionFactory sessionFactory;

    @Override
    public void afterPropertiesSet() {
        var configuration = new HibernatePersistenceConfiguration("data");
        configuration.property(AvailableSettings.JAKARTA_NON_JTA_DATASOURCE, this.dataSource);
        HibernateScanner.scan(configuration, packagesToScan);
        this.sessionFactory = configuration.createEntityManagerFactory();
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
        return (this.sessionFactory != null
                ? this.sessionFactory.getClass()
                : SessionFactory.class);
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

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public String[] getPackagesToScan() {
        return packagesToScan;
    }

    public void setPackagesToScan(String[] packagesToScan) {
        this.packagesToScan = packagesToScan;
    }

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
}
