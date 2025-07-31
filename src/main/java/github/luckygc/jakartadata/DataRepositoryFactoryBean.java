package github.luckygc.jakartadata;

import github.luckygc.jakartadata.provider.hibernate.HibernateRepositoryProxy;

import jakarta.data.repository.Repository;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.AbstractFactoryBean;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

/**
 * Jakarta Data Repository 工厂Bean
 *
 * <p>负责创建Repository接口的代理实例，将方法调用委托给相应的provider实现。
 * 目前支持Hibernate作为默认provider。
 *
 * @param <T> Repository接口类型
 * @author luckygc
 */
@Repository
public class DataRepositoryFactoryBean<T> extends AbstractFactoryBean<T> {

    /** Repository接口类型 */
    protected final Class<T> repositoryInterface;

    /** Repository provider类型，默认为hibernate */
    private String provider;

    /**
     * 构造函数
     *
     * @param repositoryInterface Repository接口类型
     */
    public DataRepositoryFactoryBean(Class<T> repositoryInterface) {
        this.repositoryInterface = repositoryInterface;
    }

    /**
     * 设置Repository provider
     *
     * @param provider provider名称，目前支持"hibernate"或null(默认使用hibernate)
     */
    public void setProvider(String provider) {
        this.provider = provider;
    }

    @Override
    public Class<?> getObjectType() {
        return repositoryInterface;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected T createInstance() throws Exception {
        BeanFactory beanFactory = getBeanFactory();
        Assert.notNull(beanFactory, "beanFactory is null");

        InvocationHandler invocationHandler;
        if (!StringUtils.hasText(provider) || "hibernate".equals(provider)) {
            invocationHandler = new HibernateRepositoryProxy<>(repositoryInterface, beanFactory);
        } else {
            throw new IllegalArgumentException(
                String.format("不支持的repository provider: '%s'。当前仅支持 'hibernate' 或留空默认使用hibernate", provider));
        }

        return (T)
                Proxy.newProxyInstance(
                        repositoryInterface.getClassLoader(),
                        new Class<?>[] {repositoryInterface},
                        invocationHandler);
    }
}
