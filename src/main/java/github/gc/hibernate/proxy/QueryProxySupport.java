package github.gc.hibernate.proxy;

import github.gc.hibernate.StatelessSessionUtils;
import org.hibernate.StatelessSession;
import org.jspecify.annotations.NonNull;
import org.springframework.util.Assert;

import java.util.function.Supplier;

public abstract class QueryProxySupport {

	protected StatelessSession session;

	public QueryProxySupport(@NonNull StatelessSession session) {
		Assert.notNull(session, "StatelessSession must not be null");
		this.session = session;
	}

	protected <T> T execute(Supplier<T> supplier) {
		try {
			return supplier.get();
		} finally {
			StatelessSessionUtils.closeStatelessSession(this.session);
		}
	}
}
