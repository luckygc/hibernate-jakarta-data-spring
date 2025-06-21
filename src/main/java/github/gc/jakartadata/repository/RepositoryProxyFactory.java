package github.gc.jakartadata.repository;

import github.gc.hibernate.session.StatelessSessionUtils;
import org.hibernate.SessionFactory;
import org.hibernate.StatelessSession;
import org.hibernate.query.MutationQuery;
import org.hibernate.query.NativeQuery;
import org.hibernate.query.Query;
import org.hibernate.query.SelectionQuery;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.Assert;

import javax.sql.DataSource;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Set;

/**
 * Repository代理工厂，参考MyBatis MapperProxyFactory的设计
 */
public class RepositoryProxyFactory<T> {

    private static final Logger log = LoggerFactory.getLogger(RepositoryProxyFactory.class);

    // Query执行方法，需要在执行时设置超时和关闭Session
    private static final Set<String> QUERY_EXECUTION_METHODS = Set.of(
            "list", "getResultList", "scroll", "getResultStream", "stream",
            "uniqueResult", "getSingleResult", "getSingleResultOrNull",
            "uniqueResultOptional", "getResultCount", "getKeyedResultList",
            "executeUpdate", "executeQuery"
    );

    private final Class<T> repositoryInterface;
    private final SessionFactory sessionFactory;
    private final DataSource dataSource;

    public RepositoryProxyFactory(@NonNull Class<T> repositoryInterface,
                                  @NonNull SessionFactory sessionFactory,
                                  @NonNull DataSource dataSource) {
        Assert.notNull(repositoryInterface, "repositoryInterface must not be null");
        Assert.notNull(sessionFactory, "sessionFactory must not be null");
        Assert.notNull(dataSource, "dataSource must not be null");

        this.repositoryInterface = repositoryInterface;
        this.sessionFactory = sessionFactory;
        this.dataSource = dataSource;
    }

    @SuppressWarnings("unchecked")
    public T newInstance() {
        RepositoryInvocationHandler handler = new RepositoryInvocationHandler();
        return (T) Proxy.newProxyInstance(
            repositoryInterface.getClassLoader(),
            new Class<?>[]{repositoryInterface},
            handler
        );
    }

    /**
     * Repository调用处理器，类似MyBatis的MapperProxy
     */
    private class RepositoryInvocationHandler implements InvocationHandler {

        private final Constructor<?> repositoryConstructor;

        public RepositoryInvocationHandler() {
            try {
                Class<?> repositoryImplClass = RepositoryUtils.getRepositoryImplClass(repositoryInterface);
                this.repositoryConstructor = repositoryImplClass.getConstructor(StatelessSession.class);
            } catch (Exception e) {
                throw new IllegalStateException(
                    "Failed to find constructor for repository implementation: " + repositoryInterface.getName(), e);
            }
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            // 处理Object类的方法
            if (Object.class.equals(method.getDeclaringClass())) {
                return method.invoke(this, args);
            }

            // 获取StatelessSession
            StatelessSession session = StatelessSessionUtils.doGetTransactionalStatelessSession(sessionFactory, dataSource);
            boolean isNewSession = false;

            if (session == null) {
                session = sessionFactory.openStatelessSession();
                isNewSession = true;
                if (log.isDebugEnabled()) {
                    log.debug("Created new StatelessSession for repository method: {}.{}",
                             repositoryInterface.getSimpleName(), method.getName());
                }
            }

            try {
                // 创建Repository实现实例并调用方法
                Object repositoryImpl = repositoryConstructor.newInstance(session);
                Object result = method.invoke(repositoryImpl, args);

                if (log.isTraceEnabled()) {
                    log.trace("Successfully executed repository method: {}.{}",
                             repositoryInterface.getSimpleName(), method.getName());
                }

                // 检查返回值是否是Query类型，需要延迟关闭Session
                if (isNewSession && isQueryType(result)) {
                    return wrapQueryWithDeferredClose(result, session);
                }

                return result;
            } catch (Exception e) {
                if (log.isErrorEnabled()) {
                    log.error("Error executing repository method: {}.{}",
                             repositoryInterface.getSimpleName(), method.getName(), e);
                }
                throw e;
            } finally {
                // 只有在非Query返回值且是新Session时才立即关闭
                if (isNewSession && !isQueryType(method.getReturnType())) {
                    StatelessSessionUtils.closeStatelessSession(session);
                    if (log.isDebugEnabled()) {
                        log.debug("Closed StatelessSession for repository method: {}.{}",
                                 repositoryInterface.getSimpleName(), method.getName());
                    }
                }
            }
        }
    }

    /**
     * 检查类型是否是Query相关类型
     */
    private boolean isQueryType(Class<?> type) {
        return Query.class.isAssignableFrom(type) ||
               SelectionQuery.class.isAssignableFrom(type) ||
               MutationQuery.class.isAssignableFrom(type) ||
               NativeQuery.class.isAssignableFrom(type);
    }

    /**
     * 检查对象是否是Query相关类型
     */
    private boolean isQueryType(Object obj) {
        return obj instanceof Query<?> ||
               obj instanceof SelectionQuery<?> ||
               obj instanceof MutationQuery ||
               obj instanceof NativeQuery<?>;
    }

    /**
     * 为Query对象包装延迟关闭功能
     */
    @SuppressWarnings("unchecked")
    private <Q> Q wrapQueryWithDeferredClose(Q query, StatelessSession session) {
        if (!isQueryType(query)) {
            return query;
        }

        Class<?> queryInterface = determineQueryInterface(query);
        if (queryInterface == null) {
            log.warn("Unknown query type: {}, cannot wrap with deferred close", query.getClass());
            return query;
        }

        QueryDeferredCloseHandler handler = new QueryDeferredCloseHandler(query, session);
        return (Q) Proxy.newProxyInstance(
                query.getClass().getClassLoader(),
                new Class<?>[]{queryInterface},
                handler
        );
    }

    /**
     * 确定Query对象实现的主要接口
     */
    private Class<?> determineQueryInterface(Object query) {
        if (query instanceof SelectionQuery<?>) {
            return SelectionQuery.class;
        } else if (query instanceof MutationQuery) {
            return MutationQuery.class;
        } else if (query instanceof NativeQuery<?>) {
            return NativeQuery.class;
        } else if (query instanceof Query<?>) {
            return Query.class;
        }
        return null;
    }

    /**
     * Query延迟关闭处理器
     */
    private class QueryDeferredCloseHandler implements InvocationHandler {

        private final Object target;
        private final StatelessSession session;
        private volatile boolean sessionClosed = false;

        public QueryDeferredCloseHandler(Object target, StatelessSession session) {
            this.target = target;
            this.session = session;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            try {
                // 如果是执行方法，需要设置事务超时
                if (QUERY_EXECUTION_METHODS.contains(method.getName())) {
                    setTransactionTimeoutIfNeeded();
                }

                Object result = method.invoke(target, args);

                // 如果是执行方法，执行完毕后关闭Session
                if (QUERY_EXECUTION_METHODS.contains(method.getName())) {
                    closeSessionIfNeeded();
                }

                // 如果返回的是自身，返回代理对象
                if (result == target) {
                    return proxy;
                }

                return result;
            } catch (Exception e) {
                // 发生异常时也要关闭Session
                closeSessionIfNeeded();
                throw e;
            }
        }

        /**
         * 设置事务超时（如果在事务中）
         */
        private void setTransactionTimeoutIfNeeded() {
            if (TransactionSynchronizationManager.isSynchronizationActive()) {
                if (log.isDebugEnabled()) {
                    log.debug("Query executing within transaction context");
                }

                // 尝试设置查询超时，参考Spring Data JPA的做法
                try {
                    // 如果target是Query类型，尝试设置超时
                    if (target instanceof Query<?> query) {
                        // 可以从事务管理器获取超时设置
                        // 这里使用默认的查询超时时间
                        Integer timeout = getTransactionTimeout();
                        if (timeout != null && timeout > 0) {
                            query.setTimeout(timeout);
                            if (log.isDebugEnabled()) {
                                log.debug("Set query timeout to {} seconds", timeout);
                            }
                        }
                    }
                } catch (Exception e) {
                    // 设置超时失败不应该影响查询执行
                    log.warn("Failed to set query timeout", e);
                }
            }
        }

        /**
         * 获取事务超时时间
         */
        private Integer getTransactionTimeout() {
            // 这里可以实现获取当前事务超时的逻辑
            // 可以从TransactionSynchronizationManager或其他地方获取
            // 暂时返回默认值30秒
            return 30;
        }

        /**
         * 关闭Session（如果需要）
         */
        private void closeSessionIfNeeded() {
            if (!sessionClosed && !TransactionSynchronizationManager.isSynchronizationActive()) {
                // 只有在非事务环境下才关闭Session
                StatelessSessionUtils.closeStatelessSession(session);
                sessionClosed = true;
                if (log.isDebugEnabled()) {
                    log.debug("Closed StatelessSession after query execution in non-transactional context");
                }
            }
        }
    }
}
