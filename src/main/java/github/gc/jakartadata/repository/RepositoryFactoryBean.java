package github.gc.jakartadata.repository;

import github.gc.hibernate.session.proxy.StatelessSessionProxy;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.dao.support.DaoSupport;
import org.springframework.util.Assert;

public class RepositoryFactoryBean<T> extends DaoSupport implements FactoryBean<T> {

	private final Class<T> repositoryInterface;

	private final StatelessSessionProxy sessionProxy;

	public RepositoryFactoryBean(@NonNull Class<T> repositoryInterface, @NonNull StatelessSessionProxy sessionProxy) {
		this.repositoryInterface = repositoryInterface;
		this.sessionProxy = sessionProxy;
	}

	@Override
	public T getObject() {
		return RepositoryFactoryUtils.createRepository(this.repositoryInterface, this.sessionProxy);
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
		Assert.notNull(this.sessionProxy, "Property 'sessionProxy' is required");

		Class<? extends T> ignore = RepositoryFactoryUtils.getRepositoryImplClass(repositoryInterface);
	}
}