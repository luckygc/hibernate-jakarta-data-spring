package github.gc.jakartadata.wrapper;

import github.gc.jakartadata.session.HibernateSessionUtils;
import org.hibernate.StatelessSession;
import org.hibernate.query.MutationQuery;
import org.hibernate.query.Query;
import org.hibernate.query.SelectionQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Query 包装器
 * 为 Hibernate Query 对象提供延迟关闭 Session 的功能
 */
public final class QueryWrapper {

    private static final Logger log = LoggerFactory.getLogger(QueryWrapper.class);

    private QueryWrapper() {
        // 工具类，禁止实例化
    }

    /**
     * 包装 Query 对象以支持延迟关闭 Session
     * 
     * @param query 原始 Query 对象
     * @param session 关联的 StatelessSession
     * @return 包装后的 Query 对象
     */
    @SuppressWarnings("unchecked")
    public static <T> T wrapWithDeferredClose(@Nullable T query, @NonNull StatelessSession session) {
        if (query == null) {
            HibernateSessionUtils.closeStatelessSession(session);
            return null;
        }

        Class<?> queryInterface = determineQueryInterface(query);
        if (queryInterface == null) {
            log.warn("Unknown query type: {}, cannot wrap with deferred close", query.getClass());
            HibernateSessionUtils.closeStatelessSession(session);
            return query;
        }

        DeferredCloseInvocationHandler handler = new DeferredCloseInvocationHandler(query, session);
        
        return (T) Proxy.newProxyInstance(
            query.getClass().getClassLoader(),
            new Class<?>[]{queryInterface},
            handler
        );
    }

    /**
     * 确定 Query 对象实现的接口
     */
    @Nullable
    private static Class<?> determineQueryInterface(Object query) {
        Class<?> queryClass = query.getClass();
        
        // 检查 SelectionQuery 接口
        if (SelectionQuery.class.isAssignableFrom(queryClass)) {
            return SelectionQuery.class;
        }
        
        // 检查 MutationQuery 接口
        if (MutationQuery.class.isAssignableFrom(queryClass)) {
            return MutationQuery.class;
        }
        
        // 检查通用 Query 接口
        if (Query.class.isAssignableFrom(queryClass)) {
            return Query.class;
        }
        
        return null;
    }

    /**
     * 延迟关闭调用处理器
     */
    private static class DeferredCloseInvocationHandler implements InvocationHandler {
        
        private final Object targetQuery;
        private final StatelessSession session;
        private volatile boolean sessionClosed = false;

        public DeferredCloseInvocationHandler(Object targetQuery, StatelessSession session) {
            this.targetQuery = targetQuery;
            this.session = session;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            try {
                // 调用原始方法
                Object result = method.invoke(targetQuery, args);
                
                // 检查是否为终端操作（会消费查询结果的方法）
                if (isTerminalOperation(method)) {
                    closeSessionIfNeeded();
                }
                
                return result;
                
            } catch (Exception e) {
                // 发生异常时也要关闭 Session
                closeSessionIfNeeded();
                throw e;
            }
        }

        /**
         * 检查是否为终端操作
         */
        private boolean isTerminalOperation(Method method) {
            String methodName = method.getName();
            
            // 常见的终端操作方法
            return methodName.equals("list") ||
                   methodName.equals("getResultList") ||
                   methodName.equals("getSingleResult") ||
                   methodName.equals("getSingleResultOrNull") ||
                   methodName.equals("uniqueResult") ||
                   methodName.equals("uniqueResultOptional") ||
                   methodName.equals("executeUpdate") ||
                   methodName.equals("executeQuery") ||
                   methodName.equals("scroll") ||
                   methodName.equals("stream");
        }

        /**
         * 如果需要的话关闭 Session
         */
        private void closeSessionIfNeeded() {
            if (!sessionClosed && HibernateSessionUtils.isSessionValid(session)) {
                HibernateSessionUtils.closeStatelessSession(session);
                sessionClosed = true;
                log.debug("Closed StatelessSession after query execution");
            }
        }
    }
}
