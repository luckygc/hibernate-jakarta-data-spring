package github.gc.hibernate.proxy;

import org.hibernate.StatelessSession;

public interface StatelessSessionProxy {
	StatelessSession getTargetStatelessSession();
}
