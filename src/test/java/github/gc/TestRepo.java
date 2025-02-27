package github.gc;

import org.hibernate.StatelessSession;

public class TestRepo {
	private StatelessSession session;

	public TestRepo(StatelessSession session) {
		this.session = session;
	}

	public StatelessSession getSession() {
		return session;
	}
}
