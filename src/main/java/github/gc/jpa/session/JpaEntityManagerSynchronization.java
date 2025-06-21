package github.gc.jpa.session;

import jakarta.persistence.EntityManagerFactory;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.core.Ordered;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.transaction.support.ResourceHolderSynchronization;

/**
 * JPA EntityManager 事务同步器
 * 负责在事务结束时清理 EntityManager 资源
 */
public class JpaEntityManagerSynchronization
        extends ResourceHolderSynchronization<JpaEntityManagerHolder, EntityManagerFactory> implements Ordered {

    public JpaEntityManagerSynchronization(JpaEntityManagerHolder resourceHolder, EntityManagerFactory resourceKey) {
        super(resourceHolder, resourceKey);
    }

    @Override
    public int getOrder() {
        return DataSourceUtils.CONNECTION_SYNCHRONIZATION_ORDER - 200;
    }

    @Override
    protected void releaseResource(@NonNull JpaEntityManagerHolder resourceHolder,
            @Nullable EntityManagerFactory resourceKey) {
        JpaEntityManagerUtils.closeEntityManager(resourceHolder.getEntityManager());
    }
}
