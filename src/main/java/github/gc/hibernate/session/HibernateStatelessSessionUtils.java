package github.gc.hibernate.session;

import org.hibernate.SessionFactory;
import org.hibernate.StatelessSession;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.Assert;

import javax.sql.DataSource;
import java.sql.Connection;

public final class HibernateStatelessSessionUtils {

	private HibernateStatelessSessionUtils() {
	}

	private static final Logger log = LoggerFactory.getLogger(HibernateStatelessSessionUtils.class);

	@Nullable
	public static StatelessSession doGetTransactionalStatelessSession(@NonNull SessionFactory sessionFactory,
			@NonNull DataSource dataSource) {
		Assert.notNull(sessionFactory, "No SessionFactory specified");
		Assert.notNull(dataSource, "No DataSource specified");

		var sessionHolder = (HibernateStatelessSessionHolder) TransactionSynchronizationManager.getResource(sessionFactory);

		if (sessionHolder != null) {
			sessionHolder.requested();
			return sessionHolder.getStatelessSession();
		} else if (!TransactionSynchronizationManager.isSynchronizationActive()) {
			return null;
		}

		log.debug("Opening Transactional Hibernate StatelessSession");
		Connection connection = DataSourceUtils.getConnection(dataSource);
		StatelessSession ss = sessionFactory.openStatelessSession(connection);

		try {
			sessionHolder = new HibernateStatelessSessionHolder(ss);
			var sessionSynchronization = new HibernateStatelessSessionSynchronization(sessionHolder, sessionFactory);
			TransactionSynchronizationManager.registerSynchronization(sessionSynchronization);
			sessionHolder.setSynchronizedWithTransaction(true);

			TransactionSynchronizationManager.bindResource(sessionFactory, sessionHolder);
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
