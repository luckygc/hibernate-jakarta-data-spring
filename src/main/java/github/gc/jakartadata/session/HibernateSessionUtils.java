package github.gc.jakartadata.session;

import org.hibernate.SessionFactory;
import org.hibernate.StatelessSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.sql.DataSource;
import java.sql.Connection;

/**
 * Hibernate Session 工具类
 * 提供 Session 管理和事务集成的实用方法
 */
public final class HibernateSessionUtils {

    private static final Logger log = LoggerFactory.getLogger(HibernateSessionUtils.class);

    /**
     * Session 绑定到事务的资源键前缀
     */
    private static final String SESSION_RESOURCE_KEY_PREFIX = "hibernate.stateless.session.";

    private HibernateSessionUtils() {
        // 工具类，禁止实例化
    }

    /**
     * 获取事务性的 StatelessSession
     * 如果当前存在事务，返回绑定到事务的 Session；否则返回 null
     * 
     * @param sessionFactory Hibernate SessionFactory
     * @param dataSource 数据源
     * @return 事务性的 StatelessSession，如果不在事务中则返回 null
     */
    @Nullable
    public static StatelessSession getTransactionalStatelessSession(@NonNull SessionFactory sessionFactory,
                                                                   @NonNull DataSource dataSource) {
        if (!TransactionSynchronizationManager.isSynchronizationActive()) {
            log.trace("No transaction synchronization active, returning null session");
            return null;
        }

        String resourceKey = generateResourceKey(sessionFactory);
        StatelessSession session = (StatelessSession) TransactionSynchronizationManager.getResource(resourceKey);

        if (session != null) {
            log.trace("Found existing transactional StatelessSession");
            return session;
        }

        // 尝试从数据源连接创建 Session
        try {
            Connection connection = getTransactionalConnection(dataSource);
            if (connection != null) {
                session = sessionFactory.openStatelessSession(connection);
                TransactionSynchronizationManager.bindResource(resourceKey, session);
                
                // 注册事务同步回调以在事务结束时清理资源
                TransactionSynchronizationManager.registerSynchronization(
                    new StatelessSessionTransactionSynchronization(session, resourceKey));
                
                log.debug("Created and bound new transactional StatelessSession");
                return session;
            }
        } catch (Exception e) {
            log.warn("Failed to create transactional StatelessSession", e);
        }

        return null;
    }

    /**
     * 获取事务性的数据库连接
     */
    @Nullable
    private static Connection getTransactionalConnection(DataSource dataSource) {
        try {
            // 尝试从 Spring 事务管理器获取连接
            Object connectionHolder = TransactionSynchronizationManager.getResource(dataSource);
            if (connectionHolder != null) {
                // 这里需要根据实际的 Spring 版本和配置来获取连接
                // 简化实现，实际项目中可能需要更复杂的逻辑
                log.trace("Found transactional connection from DataSource");
                return dataSource.getConnection();
            }
        } catch (Exception e) {
            log.debug("Failed to get transactional connection", e);
        }
        
        return null;
    }

    /**
     * 关闭 StatelessSession
     * 
     * @param session 要关闭的 Session
     */
    public static void closeStatelessSession(@Nullable StatelessSession session) {
        if (session != null) {
            try {
                session.close();
                log.trace("Closed StatelessSession");
            } catch (Exception e) {
                log.warn("Failed to close StatelessSession", e);
            }
        }
    }

    /**
     * 生成资源键
     */
    private static String generateResourceKey(SessionFactory sessionFactory) {
        return SESSION_RESOURCE_KEY_PREFIX + System.identityHashCode(sessionFactory);
    }

    /**
     * 检查 Session 是否仍然有效
     */
    public static boolean isSessionValid(@Nullable StatelessSession session) {
        if (session == null) {
            return false;
        }
        
        try {
            // 简单检查 Session 是否仍然可用
            return session.isConnected();
        } catch (Exception e) {
            log.debug("Session validation failed", e);
            return false;
        }
    }

    /**
     * 事务同步回调，用于在事务结束时清理 Session 资源
     */
    private static class StatelessSessionTransactionSynchronization 
            implements org.springframework.transaction.support.TransactionSynchronization {
        
        private final StatelessSession session;
        private final String resourceKey;

        public StatelessSessionTransactionSynchronization(StatelessSession session, String resourceKey) {
            this.session = session;
            this.resourceKey = resourceKey;
        }

        @Override
        public void afterCompletion(int status) {
            try {
                TransactionSynchronizationManager.unbindResource(resourceKey);
                closeStatelessSession(session);
                log.debug("Cleaned up transactional StatelessSession after transaction completion");
            } catch (Exception e) {
                log.warn("Failed to clean up transactional StatelessSession", e);
            }
        }
    }
}
