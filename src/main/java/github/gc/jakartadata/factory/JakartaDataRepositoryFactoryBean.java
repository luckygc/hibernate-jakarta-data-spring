package github.gc.jakartadata.factory;

import github.gc.jakartadata.proxy.JakartaDataRepositoryProxy;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
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
public class JakartaDataRepositoryFactoryBean<T> implements FactoryBean<T>, InitializingBean, BeanFactoryAware {

    private static final Logger log = LoggerFactory.getLogger(JakartaDataRepositoryFactoryBean.class);

    private Class<T> repositoryInterface;
    private BeanFactory beanFactory;
    private T repositoryProxy;

    @Override
    public void setBeanFactory(@NonNull BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(repositoryInterface, "Property 'repositoryInterface' is required");
        Assert.notNull(beanFactory, "BeanFactory is required");

        log.debug("Initialized JakartaDataRepositoryFactoryBean for interface: {}",
                 repositoryInterface.getName());
    }

    @Override
    public T getObject() throws Exception {
        if (repositoryProxy == null) {
            // 延迟创建代理实例，避免早期初始化问题
            createRepositoryProxy();
        }
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
     * 延迟获取依赖，避免早期初始化问题
     */
    @SuppressWarnings("unchecked")
    private void createRepositoryProxy() {
        // 延迟获取 SessionFactory 和 DataSource，避免早期初始化
        SessionFactory sessionFactory = beanFactory.getBean(SessionFactory.class);
        DataSource dataSource = beanFactory.getBean(DataSource.class);

        JakartaDataRepositoryProxy<T,? extends T> invocationHandler =
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
