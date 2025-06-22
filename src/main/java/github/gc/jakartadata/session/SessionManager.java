package github.gc.jakartadata.session;

import org.hibernate.SessionFactory;
import org.hibernate.StatelessSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.lang.NonNull;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.sql.DataSource;
import java.sql.Connection;

/**
 * Session 管理器
 * 负责管理 StatelessSession 的生命周期，处理事务情况
 * 
 * @author gc
 */
public class SessionManager {

    private static final Logger log = LoggerFactory.getLogger(SessionManager.class);

    private final SessionFactory sessionFactory;
    private final DataSource dataSource;

    public SessionManager(@NonNull SessionFactory sessionFactory, @NonNull DataSource dataSource) {
        this.sessionFactory = sessionFactory;
        this.dataSource = dataSource;
    }

    /**
     * 获取 StatelessSession
     * 如果在事务中，返回绑定到事务的 Session；否则创建新的 Session
     */
    public StatelessSession getSession() {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            return getTransactionalSession();
        } else {
            return createNewSession();
        }
    }

    /**
     * 判断是否应该关闭 Session
     */
    public boolean shouldCloseSession() {
        // 如果在事务中，不需要手动关闭 Session，由事务同步器处理
        return !TransactionSynchronizationManager.isSynchronizationActive();
    }

    /**
     * 关闭 Session
     */
    public void closeSession(StatelessSession session) {
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
    private StatelessSession getTransactionalSession() {
        String resourceKey = generateResourceKey();
        
        // 检查是否已经存在绑定的 Session
        StatelessSession session = (StatelessSession) TransactionSynchronizationManager.getResource(resourceKey);
        
        if (session != null && session.isConnected()) {
            log.debug("Found existing transactional StatelessSession");
            return session;
        }

        // 创建新的事务性 Session
        try {
            Connection connection = DataSourceUtils.getConnection(dataSource);
            session = sessionFactory.openStatelessSession(connection);
            
            // 绑定到事务
            TransactionSynchronizationManager.bindResource(resourceKey, session);
            
            // 注册事务同步回调
            TransactionSynchronizationManager.registerSynchronization(
                new StatelessSessionTransactionSynchronization(session, resourceKey));
            
            log.debug("Created and bound new transactional StatelessSession");
            return session;
            
        } catch (Exception e) {
            log.error("Failed to create transactional StatelessSession", e);
            throw new RuntimeException("Failed to create transactional session", e);
        }
    }

    /**
     * 创建新的 StatelessSession
     */
    private StatelessSession createNewSession() {
        try {
            StatelessSession session = sessionFactory.openStatelessSession();
            log.debug("Created new StatelessSession");
            return session;
        } catch (Exception e) {
            log.error("Failed to create StatelessSession", e);
            throw new RuntimeException("Failed to create session", e);
        }
    }

    /**
     * 生成资源键
     */
    private String generateResourceKey() {
        return sessionFactory + "_" + dataSource;
    }

    /**
         * 事务同步回调，用于在事务结束时清理 Session 资源
         */
        private record StatelessSessionTransactionSynchronization(StatelessSession session, String resourceKey) implements
        TransactionSynchronization {

        @Override
            public void afterCompletion(int status) {
                try {
                    TransactionSynchronizationManager.unbindResource(resourceKey);
                    if (session != null && session.isConnected()) {
                        session.close();
                    }
                    log.debug("Cleaned up transactional StatelessSession after transaction completion");
                } catch (Exception e) {
                    log.warn("Failed to clean up transactional StatelessSession", e);
                }
            }
        }
}
