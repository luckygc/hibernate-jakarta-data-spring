package github.gc.hibernate;

import org.hibernate.StatelessSession;
import org.springframework.transaction.support.ResourceHolderSupport;
import org.springframework.util.Assert;

public class StatelessSessionHolder extends ResourceHolderSupport {

	private final StatelessSession statelessSession;

	public StatelessSessionHolder(StatelessSession statelessSession) {
		Assert.notNull(statelessSession, "参数statelessSession不能为null");
		this.statelessSession = statelessSession;
	}

	public StatelessSession getStatelessSession() {
		return statelessSession;
	}
}
