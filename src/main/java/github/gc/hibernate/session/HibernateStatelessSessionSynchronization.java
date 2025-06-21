package github.gc.hibernate.session;

import org.hibernate.SessionFactory;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.core.Ordered;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.transaction.support.ResourceHolderSynchronization;

public class HibernateStatelessSessionSynchronization
		extends ResourceHolderSynchronization<HibernateStatelessSessionHolder, SessionFactory> implements Ordered {

	public HibernateStatelessSessionSynchronization(HibernateStatelessSessionHolder resourceHolder, SessionFactory resourceKey) {
		super(resourceHolder, resourceKey);
	}

	@Override
	public int getOrder() {
		return DataSourceUtils.CONNECTION_SYNCHRONIZATION_ORDER - 200;
	}

	@Override
	protected void releaseResource(@NonNull HibernateStatelessSessionHolder resourceHolder,
			@Nullable SessionFactory resourceKey) {
		HibernateStatelessSessionUtils.closeStatelessSession(resourceHolder.getStatelessSession());
	}
}
