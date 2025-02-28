package github.gc.demo;

import github.gc.hibernate.factory.SessionFactoryBean;
import github.gc.hibernate.session.proxy.StatelessSessionProxy;
import github.gc.hibernate.session.proxy.impl.StatelessSessionProxyImpl;
import github.gc.jakartadata.annotation.RepositoryScan;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AvailableSettings;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@RepositoryScan(basePackages = "github.gc.demo")
public class DataConfiguration {

	@Bean
	public SessionFactory sessionFactory(DataSource dataSource) {
		SessionFactoryBean sessionFactoryBean = new SessionFactoryBean();
		sessionFactoryBean.setDataSource(dataSource);
		sessionFactoryBean.setPackagesToScan(new String[]{"github.gc.demo"});
		Properties properties = sessionFactoryBean.getHibernateProperties();
		properties.put(AvailableSettings.SHOW_SQL, "true");
		properties.put(AvailableSettings.FORMAT_SQL, "true");
		properties.put(AvailableSettings.HBM2DDL_AUTO, "update");
		return sessionFactoryBean.getObject();
	}

	@Bean
	public StatelessSessionProxy statelessSessionProxy(SessionFactory sessionFactory) {
		return new StatelessSessionProxyImpl(sessionFactory);
	}
}
