package github.gc.jakartadata;

import org.hibernate.SessionFactory;
import org.hibernate.resource.jdbc.spi.StatementInspector;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.support.DaoSupport;
import org.springframework.util.Assert;

import javax.sql.DataSource;

public class RepositoryFactoryBean<T> extends DaoSupport implements FactoryBean<T> {

	private final Class<T> repositoryInterface;

	@Autowired
	private SessionFactory sessionFactory;
	@Autowired
	private DataSource dataSource;
	@Autowired(required = false)
	private StatementInspector statementInspector;

	public RepositoryFactoryBean(Class<T> repositoryInterface) {
		this.repositoryInterface = repositoryInterface;
	}

	@Override
	public T getObject() {
		return null;
	}

	@Override
	public Class<?> getObjectType() {
		return this.repositoryInterface;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

	@Override
	protected void checkDaoConfig() throws IllegalArgumentException {
		Assert.notNull(this.repositoryInterface, "Property 'repositoryInterface' is required");
		Assert.notNull(this.sessionFactory, "Property 'sessionFactory' is required");
		Assert.notNull(this.dataSource, "Property 'dataSource' is required");

		Class<? extends T> ignore = RepositoryFactoryUtils.getRepositoryImplClass(repositoryInterface);
	}
}