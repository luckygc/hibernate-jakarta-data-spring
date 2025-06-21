package github.gc.demo;

import github.gc.hibernate.factory.HibernateSessionFactoryBean;
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
		HibernateSessionFactoryBean sessionFactoryBean = new HibernateSessionFactoryBean();
		sessionFactoryBean.setDataSource(dataSource);
                sessionFactoryBean.setPackagesToScan(new String[]{"github.gc.**.model"});
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
}
