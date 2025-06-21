package github.gc.hibernate.session;

import org.hibernate.StatelessSession;
import org.springframework.transaction.support.ResourceHolderSupport;
import org.springframework.util.Assert;

public class HibernateStatelessSessionHolder extends ResourceHolderSupport {

	private final StatelessSession statelessSession;

	public HibernateStatelessSessionHolder(StatelessSession statelessSession) {
		Assert.notNull(statelessSession, "参数statelessSession不能为null");
		this.statelessSession = statelessSession;
	}

	public StatelessSession getStatelessSession() {
		return statelessSession;
	}
}
