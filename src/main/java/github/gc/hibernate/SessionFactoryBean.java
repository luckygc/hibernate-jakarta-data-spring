package github.gc.hibernate;

import org.hibernate.Interceptor;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

import javax.sql.DataSource;
import java.util.Properties;

public class SessionFactoryBean implements FactoryBean<SessionFactory>, InitializingBean, DisposableBean {

	private DataSource dataSource;

	private Interceptor entityInterceptor;

	private Properties hibernateProperties;

	private String[] packagesToScan;

	private Configuration configuration;

	private SessionFactory sessionFactory;

	@Override
	public void afterPropertiesSet() {
		SessionFactoryBuilder sfb = new SessionFactoryBuilder(this.dataSource);

		if (this.entityInterceptor != null) {
			sfb.setInterceptor(this.entityInterceptor);
		}

		if (this.hibernateProperties != null) {
			sfb.addProperties(this.hibernateProperties);
		}

		if (this.packagesToScan != null) {
			sfb.scanPackages(this.packagesToScan);
		}

		this.configuration = sfb;
		this.sessionFactory = sfb.buildSessionFactory();
	}

	@Override
	public SessionFactory getObject() {
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

	public final Configuration getConfiguration() {
		if (this.configuration == null) {
			throw new IllegalStateException("Configuration not initialized yet");
		}
		return this.configuration;
	}

	public Properties getHibernateProperties() {
		if (this.hibernateProperties == null) {
			this.hibernateProperties = new Properties();
		}
		return this.hibernateProperties;
	}

	public DataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public Interceptor getEntityInterceptor() {
		return entityInterceptor;
	}

	public void setEntityInterceptor(Interceptor entityInterceptor) {
		this.entityInterceptor = entityInterceptor;
	}

	public void setHibernateProperties(Properties hibernateProperties) {
		this.hibernateProperties = hibernateProperties;
	}

	public String[] getPackagesToScan() {
		return packagesToScan;
	}

	public void setPackagesToScan(String[] packagesToScan) {
		this.packagesToScan = packagesToScan;
	}

	public void setConfiguration(Configuration configuration) {
		this.configuration = configuration;
	}

	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
}
