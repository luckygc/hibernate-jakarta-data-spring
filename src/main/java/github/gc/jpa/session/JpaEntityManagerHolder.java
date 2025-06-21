package github.gc.jpa.session;

import jakarta.persistence.EntityManager;
import org.springframework.transaction.support.ResourceHolderSupport;
import org.springframework.util.Assert;

/**
 * JPA EntityManager 持有者
 * 用于在事务中管理 EntityManager 的生命周期
 */
public class JpaEntityManagerHolder extends ResourceHolderSupport {

    private final EntityManager entityManager;

    public JpaEntityManagerHolder(EntityManager entityManager) {
        Assert.notNull(entityManager, "参数entityManager不能为null");
        this.entityManager = entityManager;
    }

    public EntityManager getEntityManager() {
        return entityManager;
    }
}
