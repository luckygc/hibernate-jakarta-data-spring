package github.gc.hibernate.session.proxy;

import github.gc.hibernate.session.StatelessSessionUtils;
import org.hibernate.StatelessSession;
import org.jspecify.annotations.NonNull;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.Assert;

import java.util.function.Supplier;

public abstract class QueryProxySupport {

	protected final StatelessSession session;

	public QueryProxySupport(@NonNull StatelessSession session) {
		Assert.notNull(session, "StatelessSession must not be null");
		this.session = session;
	}

        protected <T> T execute(Supplier<T> supplier) {
                boolean transactional = TransactionSynchronizationManager.hasResource(this.session.getSessionFactory());
                try {
                        return supplier.get();
                } finally {
                        if (!transactional) {
                                StatelessSessionUtils.closeStatelessSession(this.session);
                        }
                }
        }
}
