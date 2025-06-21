package github.gc.jakartadata.repository;

import github.gc.jpa.session.JpaEntityManagerUtils;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Query;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * 基于 JPA EntityManager 的 Repository 代理工厂
 * 替代基于 Hibernate StatelessSession 的实现
 */
public class JpaRepositoryProxyFactory<T> {

    private static final Logger log = LoggerFactory.getLogger(JpaRepositoryProxyFactory.class);

    private final Class<T> repositoryInterface;
    private final EntityManagerFactory entityManagerFactory;

    public JpaRepositoryProxyFactory(@NonNull Class<T> repositoryInterface, 
                                     @NonNull EntityManagerFactory entityManagerFactory) {
        this.repositoryInterface = repositoryInterface;
        this.entityManagerFactory = entityManagerFactory;
    }

    @SuppressWarnings("unchecked")
    public T newInstance() {
        Class<? extends T> repositoryImplClass = RepositoryUtils.getRepositoryImplClass(repositoryInterface);
        
        return (T) Proxy.newProxyInstance(
                repositoryInterface.getClassLoader(),
                new Class<?>[]{repositoryInterface},
                new JpaRepositoryInvocationHandler<>(repositoryInterface, repositoryImplClass, entityManagerFactory)
        );
    }

    private static class JpaRepositoryInvocationHandler<T> implements InvocationHandler {

        private final Class<T> repositoryInterface;
        private final Class<? extends T> repositoryImplClass;
        private final EntityManagerFactory entityManagerFactory;

        public JpaRepositoryInvocationHandler(Class<T> repositoryInterface, 
                                              Class<? extends T> repositoryImplClass,
                                              EntityManagerFactory entityManagerFactory) {
            this.repositoryInterface = repositoryInterface;
            this.repositoryImplClass = repositoryImplClass;
            this.entityManagerFactory = entityManagerFactory;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            // 处理Object类的方法
            if (Object.class.equals(method.getDeclaringClass())) {
                return method.invoke(this, args);
            }

            // 获取EntityManager
            EntityManager entityManager = JpaEntityManagerUtils.doGetTransactionalEntityManager(entityManagerFactory);
            boolean isNewEntityManager = false;

            if (entityManager == null) {
                entityManager = entityManagerFactory.createEntityManager();
                isNewEntityManager = true;
                if (log.isDebugEnabled()) {
                    log.debug("Created new EntityManager for repository method: {}.{}",
                             repositoryInterface.getSimpleName(), method.getName());
                }
            }

            try {
                // 创建Repository实现实例
                T repositoryImpl = repositoryImplClass.getDeclaredConstructor(EntityManager.class)
                        .newInstance(entityManager);

                // 调用方法
                Object result = method.invoke(repositoryImpl, args);

                // 如果返回值是Query类型，需要包装以延迟关闭EntityManager
                if (isQueryType(method.getReturnType()) && isNewEntityManager) {
                    return new JpaQueryWrapper(result, entityManager, entityManagerFactory);
                }

                return result;
            } catch (Exception e) {
                if (log.isErrorEnabled()) {
                    log.error("Error executing repository method: {}.{}", 
                             repositoryInterface.getSimpleName(), method.getName(), e);
                }
                throw e;
            } finally {
                // 只有在非Query返回值且是新EntityManager时才立即关闭
                if (isNewEntityManager && !isQueryType(method.getReturnType())) {
                    JpaEntityManagerUtils.closeEntityManager(entityManager);
                    if (log.isDebugEnabled()) {
                        log.debug("Closed EntityManager for repository method: {}.{}",
                                 repositoryInterface.getSimpleName(), method.getName());
                    }
                }
            }
        }

        private boolean isQueryType(Class<?> returnType) {
            return Query.class.isAssignableFrom(returnType) ||
                   jakarta.persistence.TypedQuery.class.isAssignableFrom(returnType);
        }
    }

    /**
     * JPA Query 包装器，用于延迟关闭 EntityManager
     */
    private static class JpaQueryWrapper implements InvocationHandler {

        private final Object query;
        private final EntityManager entityManager;
        private final EntityManagerFactory entityManagerFactory;
        private boolean entityManagerClosed = false;

        public JpaQueryWrapper(Object query, EntityManager entityManager, EntityManagerFactory entityManagerFactory) {
            this.query = query;
            this.entityManager = entityManager;
            this.entityManagerFactory = entityManagerFactory;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            try {
                Object result = method.invoke(query, args);
                
                // 在执行查询后关闭EntityManager（如果需要）
                if (isExecutionMethod(method.getName())) {
                    closeEntityManagerIfNeeded();
                }
                
                return result;
            } catch (Exception e) {
                closeEntityManagerIfNeeded();
                throw e;
            }
        }

        private boolean isExecutionMethod(String methodName) {
            return "getResultList".equals(methodName) || 
                   "getSingleResult".equals(methodName) ||
                   "executeUpdate".equals(methodName) ||
                   "getResultStream".equals(methodName);
        }

        /**
         * 关闭EntityManager（如果需要）
         */
        private void closeEntityManagerIfNeeded() {
            if (!entityManagerClosed && !TransactionSynchronizationManager.isSynchronizationActive()) {
                // 只有在非事务环境下才关闭EntityManager
                JpaEntityManagerUtils.closeEntityManager(entityManager);
                entityManagerClosed = true;
                if (log.isDebugEnabled()) {
                    log.debug("Closed EntityManager after query execution in non-transactional context");
                }
            }
        }
    }
}
