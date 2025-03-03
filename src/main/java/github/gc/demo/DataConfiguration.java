package github.gc.demo;

import github.gc.hibernate.factory.SessionFactoryBean;
import github.gc.hibernate.session.proxy.StatelessSessionProxy;
import github.gc.hibernate.session.proxy.impl.StatelessSessionProxyImpl;
import github.gc.jakartadata.annotation.RepositoryScan;
import org.hibernate.SessionFactory;
import org.hibernate.cache.spi.access.AccessType;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.tool.schema.Action;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@RepositoryScan(basePackages = "github.gc.**.repository")
public class DataConfiguration {

	@Bean
	public SessionFactory sessionFactory(DataSource dataSource) {
		SessionFactoryBean sessionFactoryBean = new SessionFactoryBean();
		sessionFactoryBean.setDataSource(dataSource);
		sessionFactoryBean.setPackagesToScanEntity(new String[]{"github.gc.**.model"});
		Properties properties = sessionFactoryBean.getHibernateProperties();
		properties.put(AvailableSettings.SHOW_SQL, Boolean.FALSE);
		properties.put(AvailableSettings.FORMAT_SQL, Boolean.TRUE);
		properties.put(AvailableSettings.HBM2DDL_AUTO, Action.ACTION_UPDATE);
		properties.put(AvailableSettings.USE_SQL_COMMENTS, "true");
		properties.put(AvailableSettings.STATEMENT_FETCH_SIZE, Integer.MAX_VALUE);
		properties.put(AvailableSettings.DEFAULT_BATCH_FETCH_SIZE, Integer.MAX_VALUE);
		properties.put(AvailableSettings.DEFAULT_CACHE_CONCURRENCY_STRATEGY, AccessType.READ_WRITE.getExternalName());
		properties.put(AvailableSettings.USE_SECOND_LEVEL_CACHE, Boolean.FALSE);

		return sessionFactoryBean.getObject();
	}

	@Bean
	public StatelessSessionProxy statelessSessionProxy(SessionFactory sessionFactory) {
		return new StatelessSessionProxyImpl(sessionFactory);
	}
}
