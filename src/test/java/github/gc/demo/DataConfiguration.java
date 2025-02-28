package github.gc.demo;

import github.gc.hibernate.factory.SessionFactoryBean;
import github.gc.hibernate.session.proxy.StatelessSessionProxy;
import github.gc.hibernate.session.proxy.impl.StatelessSessionProxyImpl;
import org.hibernate.SessionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class DataConfiguration {

	@Bean
	public SessionFactory sessionFactory(DataSource dataSource) {
		SessionFactoryBean sessionFactoryBean = new SessionFactoryBean();
		sessionFactoryBean.setDataSource(dataSource);
		return sessionFactoryBean.getObject();
	}

	@Bean
	public StatelessSessionProxy statelessSessionProxy(SessionFactory sessionFactory) {
		return new StatelessSessionProxyImpl(sessionFactory);
	}
}
