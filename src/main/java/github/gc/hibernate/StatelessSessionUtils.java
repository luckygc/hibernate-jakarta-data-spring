package github.gc.hibernate;

import org.hibernate.SessionFactory;
import org.hibernate.StatelessSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.sql.DataSource;
import java.sql.Connection;

public final class StatelessSessionUtils {

	private StatelessSessionUtils() {
	}

	private static final Logger log = LoggerFactory.getLogger(StatelessSessionUtils.class);

	public static StatelessSession doGetTransactionalStatelessSession(SessionFactory sessionFactory,
			DataSource dataSource) {
		StatelessSessionHolder ssHolder = (StatelessSessionHolder) TransactionSynchronizationManager.getResource(
				sessionFactory);

		if (ssHolder != null) {
			ssHolder.requested();
			return ssHolder.getStatelessSession();
		} else if (!TransactionSynchronizationManager.isSynchronizationActive()) {
			return null;
		}

		log.debug("Opening Hibernate StatelessSession");
		Connection connection = DataSourceUtils.getConnection(dataSource);
		StatelessSession ss = sessionFactory.openStatelessSession(connection);

		try {
			ssHolder = new StatelessSessionHolder(ss);
			TransactionSynchronizationManager.registerSynchronization(
					new StatelessSessionSynchronization(ssHolder, sessionFactory));
			ssHolder.setSynchronizedWithTransaction(true);

			TransactionSynchronizationManager.bindResource(sessionFactory, ssHolder);
		} catch (Throwable ex) {
			closeStatelessSession(ss);
			throw ex;
		}

		return ss;
	}

	public static void closeStatelessSession(StatelessSession session) {
		if (session != null) {
			try {
				if (session.isOpen()) {
					session.close();
				}
			} catch (Throwable ex) {
				log.error("Failed to release Hibernate StatelessSession", ex);
			}
		}
	}
}
