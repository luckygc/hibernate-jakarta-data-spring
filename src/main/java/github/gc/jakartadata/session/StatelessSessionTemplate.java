package github.gc.jakartadata.session;

import github.gc.jakartadata.wrapper.QueryWrapper;
import org.hibernate.SessionFactory;
import org.hibernate.StatelessSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.support.DaoSupport;
import org.springframework.lang.NonNull;

import javax.sql.DataSource;

/**
 * StatelessSession 模板类
 * 参考 MyBatis SqlSessionTemplate 的设计，提供线程安全的 StatelessSession 操作
 * 负责管理 session 生命周期和事务集成
 */
public class StatelessSessionTemplate extends DaoSupport {

    private static final Logger log = LoggerFactory.getLogger(StatelessSessionTemplate.class);

    private final SessionFactory sessionFactory;
    private final DataSource dataSource;

    public StatelessSessionTemplate(@NonNull SessionFactory sessionFactory, @NonNull DataSource dataSource) {
        this.sessionFactory = sessionFactory;
        this.dataSource = dataSource;
    }

    /**
     * 执行 session 操作的回调接口
     */
    @FunctionalInterface
    public interface SessionCallback<T> {
        T doInSession(StatelessSession session) throws Exception;
    }

    /**
     * 执行 session 操作
     * 参考 MyBatis SqlSessionTemplate.execute() 方法
     */
    public <T> T execute(SessionCallback<T> callback) {
        StatelessSession session = getSqlSession();
        boolean isNewSession = false;
        T result = null;

        try {
            // 如果没有现有的事务性 session，创建新的
            if (session == null) {
                session = sessionFactory.openStatelessSession();
                isNewSession = true;
                log.debug("Created new StatelessSession");
            }

            result = callback.doInSession(session);

            // 如果返回值是 Query 类型且是新 session，需要延迟关闭（参考 Spring Data JPA）
            if (isNewSession && isQueryType(result)) {
                return (T) QueryWrapper.wrapWithDeferredClose(result, session);
            }

            return result;

        } catch (Exception e) {
            log.error("Error executing session callback", e);
            // 发生异常时立即关闭新创建的 session
            if (isNewSession && session != null) {
                closeSqlSession(session);
            }
            throw new RuntimeException("Session execution failed", e);
        } finally {
            // 如果是新创建的 session 且不是 Query 类型，立即关闭
            if (isNewSession && session != null && !isQueryType(result)) {
                closeSqlSession(session);
                log.debug("Closed StatelessSession");
            }
        }
    }

    /**
     * 获取 StatelessSession
     * 参考 MyBatis 的事务集成方式
     */
    private StatelessSession getSqlSession() {
        // 使用 HibernateSessionUtils 获取事务性 session
        return HibernateSessionUtils.getTransactionalStatelessSession(sessionFactory, dataSource);
    }

    /**
     * 关闭 StatelessSession
     */
    private void closeSqlSession(StatelessSession session) {
        HibernateSessionUtils.closeStatelessSession(session);
    }

    /**
     * 检查返回值是否为 Query 类型
     * 只有 Query 类型需要延迟关闭 session（参考 Spring Data JPA）
     */
    private boolean isQueryType(Object result) {
        if (result == null) {
            return false;
        }
        
        Class<?> resultClass = result.getClass();
        
        // 检查是否为 Hibernate Query 相关类型
        return org.hibernate.query.Query.class.isAssignableFrom(resultClass) ||
               org.hibernate.query.SelectionQuery.class.isAssignableFrom(resultClass) ||
               org.hibernate.query.MutationQuery.class.isAssignableFrom(resultClass);
    }

    @Override
    protected void checkDaoConfig() throws IllegalArgumentException {
        if (sessionFactory == null) {
            throw new IllegalArgumentException("SessionFactory is required");
        }
        if (dataSource == null) {
            throw new IllegalArgumentException("DataSource is required");
        }
    }

    // Getters
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public DataSource getDataSource() {
        return dataSource;
    }
}
