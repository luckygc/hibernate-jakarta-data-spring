package github.gc.jakartadata.repository;

import jakarta.persistence.EntityManagerFactory;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.support.DaoSupport;
import org.springframework.util.Assert;

/**
 * 基于JPA EntityManager的Repository Bean
 * 替代基于Hibernate SessionFactory的实现
 */
public class JpaRepositoryBean<T> extends DaoSupport implements FactoryBean<T> {

    private final Class<T> repositoryInterface;

    @Autowired
    private EntityManagerFactory entityManagerFactory;

    public JpaRepositoryBean(@NonNull Class<T> repositoryInterface) {
        this.repositoryInterface = repositoryInterface;
    }

    @Override
    public T getObject() {
        JpaRepositoryProxyFactory<T> proxyFactory = new JpaRepositoryProxyFactory<>(
                this.repositoryInterface, this.entityManagerFactory);
        return proxyFactory.newInstance();
    }

    @Override
    public Class<?> getObjectType() {
        return this.repositoryInterface;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    protected void checkDaoConfig() throws IllegalArgumentException {
        Assert.notNull(this.repositoryInterface, "Property 'repositoryInterface' is required");
        Assert.notNull(this.entityManagerFactory, "Property 'entityManagerFactory' is required");

        Class<? extends T> ignore = RepositoryUtils.getRepositoryImplClass(repositoryInterface);
    }

    public Class<T> getRepositoryInterface() {
        return repositoryInterface;
    }

    public EntityManagerFactory getEntityManagerFactory() {
        return entityManagerFactory;
    }

    public void setEntityManagerFactory(@NonNull EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }
}
