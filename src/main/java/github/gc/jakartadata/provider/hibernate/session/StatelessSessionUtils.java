package github.gc.jakartadata.provider.hibernate.session;

import java.sql.Connection;
import javax.sql.DataSource;
import org.hibernate.SessionFactory;
import org.hibernate.StatelessSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.transaction.support.ResourceHolderSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * StatelessSession 工具类 提供 StatelessSession 的获取、绑定和释放功能 遵循 Spring 的资源管理模式（类似 DataSourceUtils）
 *
 * @author gc
 */
public abstract class StatelessSessionUtils {

    private static final Logger log = LoggerFactory.getLogger(StatelessSessionUtils.class);

    /**
     * 关闭 Session
     *
     * @param session StatelessSession 实例
     */
    public static void closeSession(@Nullable StatelessSession session) {
        if (session != null && session.isConnected()) {
            try {
                session.close();
                log.debug("Closed StatelessSession");
            } catch (Exception e) {
                log.warn("Failed to close StatelessSession", e);
            }
        }
    }

    /**
     * 获取事务性的 StatelessSession
     */
    @NonNull
    public static StatelessSession getTransactionalSession(@NonNull SessionFactory sessionFactory,
        @NonNull DataSource dataSource) {
        String resourceKey = generateResourceKey(sessionFactory, dataSource);

        // 检查是否已经存在绑定的 Session
        StatelessSessionHolder holder = (StatelessSessionHolder)
            TransactionSynchronizationManager.getResource(resourceKey);

        if (holder != null && holder.hasValidSession()) {
            log.debug("Found existing transactional StatelessSession");
            return holder.getStatelessSession();
        }

        // 创建新的事务性 Session
        try {
            Connection connection = DataSourceUtils.getConnection(dataSource);
            StatelessSession session = sessionFactory.openStatelessSession(connection);

            // 创建 ResourceHolder
            holder = new StatelessSessionHolder(session);
            holder.setSynchronizedWithTransaction(true);

            // 绑定到事务
            TransactionSynchronizationManager.bindResource(resourceKey, holder);

            // 注册事务同步回调
            TransactionSynchronizationManager.registerSynchronization(
                new StatelessSessionResourceSynchronization(holder, resourceKey));

            log.debug("Created and bound new transactional StatelessSession");
            return session;

        } catch (Exception e) {
            log.error("Failed to create transactional StatelessSession", e);
            throw new RuntimeException("Failed to create transactional session", e);
        }
    }

    /**
     * 生成资源键
     */
    @NonNull
    private static String generateResourceKey(@NonNull SessionFactory sessionFactory,
        @NonNull DataSource dataSource) {
        return sessionFactory + "_" + dataSource;
    }

    /**
     * StatelessSession 资源同步器 用于在事务结束时清理 Session 资源
     */
    private static class StatelessSessionResourceSynchronization
        extends ResourceHolderSynchronization<StatelessSessionHolder, String> {

        public StatelessSessionResourceSynchronization(StatelessSessionHolder resourceHolder,
            String resourceKey) {
            super(resourceHolder, resourceKey);
        }

        @Override
        protected void releaseResource(StatelessSessionHolder resourceHolder, String resourceKey) {
            StatelessSession session = resourceHolder.getStatelessSession();
            closeSession(session);
            log.debug("Released transactional StatelessSession after transaction completion");
        }
    }
}
