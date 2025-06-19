package github.gc.jakartadata.repository;

import org.hibernate.SessionFactory;
import javax.sql.DataSource;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.support.DaoSupport;
import org.springframework.util.Assert;

public class RepositoryBean<T> extends DaoSupport implements FactoryBean<T> {

	private final Class<T> repositoryInterface;

    @Autowired
    private SessionFactory sessionFactory;

    @Autowired
    private DataSource dataSource;

	public RepositoryBean(@NonNull Class<T> repositoryInterface) {
		this.repositoryInterface = repositoryInterface;
	}

	@Override
	public T getObject() {
        return RepositoryUtils.createRepository(this.repositoryInterface, this.sessionFactory, this.dataSource);
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

		Class<? extends T> ignore = RepositoryUtils.getRepositoryImplClass(repositoryInterface);
	}

	public Class<T> getRepositoryInterface() {
		return repositoryInterface;
	}

        public SessionFactory getSessionFactory() {
                return sessionFactory;
        }

        public void setSessionFactory(@NonNull SessionFactory sessionFactory) {
                this.sessionFactory = sessionFactory;
        }

        public DataSource getDataSource() {
                return dataSource;
        }

        public void setDataSource(@NonNull DataSource dataSource) {
                this.dataSource = dataSource;
        }
}