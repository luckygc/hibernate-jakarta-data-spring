package github.gc.jakartadata.factory;

import github.gc.jakartadata.proxy.JakartaDataRepositoryProxy;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.util.Assert;

import javax.sql.DataSource;
import java.lang.reflect.Proxy;

/**
 * Jakarta Data Repository 工厂 Bean
 * 负责创建 Repository 接口的代理实例
 *
 * @author gc
 */
public class JakartaDataRepositoryFactoryBean<T> implements FactoryBean<T>, InitializingBean {

    private static final Logger log = LoggerFactory.getLogger(JakartaDataRepositoryFactoryBean.class);

    private Class<T> repositoryInterface;

    @Autowired
    private SessionFactory sessionFactory;

    @Autowired
    private DataSource dataSource;

    private T repositoryProxy;

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(repositoryInterface, "Property 'repositoryInterface' is required");
        Assert.notNull(sessionFactory, "Property 'sessionFactory' is required");
        Assert.notNull(dataSource, "Property 'dataSource' is required");

        // 创建 Repository 代理实例
        createRepositoryProxy();

        log.debug("Initialized JakartaDataRepositoryFactoryBean for interface: {}",
                 repositoryInterface.getName());
    }

    @Override
    public T getObject() throws Exception {
        return repositoryProxy;
    }

    @Override
    public Class<T> getObjectType() {
        return repositoryInterface;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    /**
     * 创建 Repository 代理实例
     */
    @SuppressWarnings("unchecked")
    private void createRepositoryProxy() {
        JakartaDataRepositoryProxy<T> invocationHandler =
            new JakartaDataRepositoryProxy<>(repositoryInterface, sessionFactory, dataSource);

        repositoryProxy = (T) Proxy.newProxyInstance(
            repositoryInterface.getClassLoader(),
            new Class<?>[]{repositoryInterface},
            invocationHandler
        );

        log.debug("Created proxy for repository interface: {}", repositoryInterface.getName());
    }

    // Getters and Setters
    public Class<T> getRepositoryInterface() {
        return repositoryInterface;
    }

    public void setRepositoryInterface(@NonNull Class<T> repositoryInterface) {
        this.repositoryInterface = repositoryInterface;
    }
}
