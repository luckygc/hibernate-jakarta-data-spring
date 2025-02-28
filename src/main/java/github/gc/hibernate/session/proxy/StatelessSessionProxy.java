package github.gc.hibernate.session.proxy;

import org.hibernate.StatelessSession;

public interface StatelessSessionProxy  extends StatelessSession{
	StatelessSession getCurrentSession();
}
