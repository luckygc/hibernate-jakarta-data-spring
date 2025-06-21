package github.gc.jpa.session;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.Assert;

/**
 * JPA EntityManager 工具类
 * 提供事务性EntityManager的获取和管理，不依赖Spring ORM
 */
public final class JpaEntityManagerUtils {

    private JpaEntityManagerUtils() {
    }

    private static final Logger log = LoggerFactory.getLogger(JpaEntityManagerUtils.class);

    /**
     * 获取事务性EntityManager
     * 如果当前存在事务，返回绑定到事务的EntityManager
     * 如果不存在事务，返回null
     */
    @Nullable
    public static EntityManager doGetTransactionalEntityManager(@NonNull EntityManagerFactory emf) {
        Assert.notNull(emf, "No EntityManagerFactory specified");

        JpaEntityManagerHolder emHolder = (JpaEntityManagerHolder) TransactionSynchronizationManager.getResource(emf);
        if (emHolder != null) {
            emHolder.requested();
            return emHolder.getEntityManager();
        }

        if (!TransactionSynchronizationManager.isSynchronizationActive()) {
            return null;
        }

        log.debug("Opening JPA EntityManager in transaction");
        EntityManager em = emf.createEntityManager();

        try {
            emHolder = new JpaEntityManagerHolder(em);
            JpaEntityManagerSynchronization synchronization = new JpaEntityManagerSynchronization(emHolder, emf);
            TransactionSynchronizationManager.registerSynchronization(synchronization);
            emHolder.setSynchronizedWithTransaction(true);

            TransactionSynchronizationManager.bindResource(emf, emHolder);
        } catch (Throwable ex) {
            closeEntityManager(em);
            throw ex;
        }

        return em;
    }

    /**
     * 获取EntityManager（事务性或非事务性）
     * 优先返回事务性EntityManager，如果不存在则创建新的EntityManager
     */
    @NonNull
    public static EntityManager getEntityManager(@NonNull EntityManagerFactory emf) {
        Assert.notNull(emf, "No EntityManagerFactory specified");

        EntityManager em = doGetTransactionalEntityManager(emf);
        if (em != null) {
            return em;
        }

        log.debug("Creating new JPA EntityManager");
        return emf.createEntityManager();
    }

    /**
     * 关闭EntityManager
     */
    public static void closeEntityManager(EntityManager em) {
        if (em != null) {
            try {
                if (em.isOpen()) {
                    em.close();
                    log.debug("Closed JPA EntityManager");
                }
            } catch (Throwable ex) {
                log.error("Failed to close JPA EntityManager", ex);
            }
        }
    }

    /**
     * 关闭EntityManager（如果需要）
     * 只关闭非事务性的EntityManager
     */
    public static void closeEntityManagerIfNeeded(EntityManager em, EntityManagerFactory emf) {
        if (em != null && !isTransactional(em, emf)) {
            closeEntityManager(em);
        }
    }

    /**
     * 检查EntityManager是否是事务性的
     */
    public static boolean isTransactional(EntityManager em, EntityManagerFactory emf) {
        JpaEntityManagerHolder emHolder = (JpaEntityManagerHolder) TransactionSynchronizationManager.getResource(emf);
        return emHolder != null && emHolder.getEntityManager() == em;
    }
}
