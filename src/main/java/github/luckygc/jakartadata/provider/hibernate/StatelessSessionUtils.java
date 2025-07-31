package github.luckygc.jakartadata.provider.hibernate;

import org.hibernate.SessionFactory;
import org.hibernate.StatelessSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.transaction.support.ResourceHolderSupport;
import org.springframework.transaction.support.ResourceHolderSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.sql.Connection;

import javax.sql.DataSource;

/**
 * @author luckygc
 */
public abstract class StatelessSessionUtils {

    private static final Logger log = LoggerFactory.getLogger(StatelessSessionUtils.class);

    /**
     * 安全关闭StatelessSession
     *
     * @param session StatelessSession实例
     */
    public static void closeSession(@Nullable StatelessSession session) {
        if (session != null) {
            try {
                if (session.isConnected()) {
                    session.close();
                    log.debug("已关闭StatelessSession");
                }
            } catch (Exception e) {
                log.warn("关闭StatelessSession时发生异常", e);
            }
        }
    }

    /**
     * 获取事务性的StatelessSession
     *
     * <p>在事务环境中，Session会被绑定到当前事务，并在事务结束时自动清理。
     *
     * @param sessionFactory Hibernate SessionFactory
     * @param dataSource 数据源
     * @return 事务性StatelessSession
     * @throws RuntimeException 如果创建Session失败
     */
    @NonNull
    public static StatelessSession getTransactionalSession(@NonNull SessionFactory sessionFactory,
        @NonNull DataSource dataSource) {
        String resourceKey = generateResourceKey(sessionFactory, dataSource);

        // 检查是否已经存在绑定的Session
        StatelessSessionHolder holder = (StatelessSessionHolder)
            TransactionSynchronizationManager.getResource(resourceKey);

        if (holder != null && holder.getStatelessSession().isConnected()) {
            log.debug("复用现有的事务性StatelessSession");
            return holder.getStatelessSession();
        }

        // 创建新的事务性Session
        Connection connection = null;
        try {
            connection = DataSourceUtils.getConnection(dataSource);
            StatelessSession session = sessionFactory.openStatelessSession(connection);

            // 创建ResourceHolder
            holder = new StatelessSessionHolder(session);
            holder.setSynchronizedWithTransaction(true);

            // 绑定到事务
            TransactionSynchronizationManager.bindResource(resourceKey, holder);

            // 注册事务同步回调
            TransactionSynchronizationManager.registerSynchronization(
                new StatelessSessionResourceSynchronization(holder, resourceKey));

            log.debug("创建并绑定新的事务性StatelessSession");
            return session;

        } catch (Exception e) {
            // 清理资源
            if (connection != null) {
                DataSourceUtils.releaseConnection(connection, dataSource);
            }
            log.error("创建事务性StatelessSession失败", e);
            throw new RuntimeException("创建事务性Session失败", e);
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
    public static class StatelessSessionResourceSynchronization
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

    public static class StatelessSessionHolder extends ResourceHolderSupport {

        private final StatelessSession statelessSession;

        public StatelessSessionHolder(@NonNull StatelessSession statelessSession) {
            this.statelessSession = statelessSession;
        }

        @NonNull
        public StatelessSession getStatelessSession() {
            return this.statelessSession;
        }
    }
}
