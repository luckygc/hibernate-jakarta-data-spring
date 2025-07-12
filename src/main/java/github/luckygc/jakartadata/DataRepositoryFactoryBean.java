package github.luckygc.jakartadata;

import github.luckygc.jakartadata.provider.hibernate.HibernateRepositoryProxy;

import jakarta.data.repository.Repository;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.AbstractFactoryBean;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

@Repository
public class DataRepositoryFactoryBean<T> extends AbstractFactoryBean<T> {

    protected final Class<T> repositoryInterface;

    public DataRepositoryFactoryBean(Class<T> repositoryInterface) {
        this.repositoryInterface = repositoryInterface;
    }

    private String provider;

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
            throw new RuntimeException("暂不支持的repository provider: %s".formatted(provider));
        }

        return (T)
                Proxy.newProxyInstance(
                        repositoryInterface.getClassLoader(),
                        new Class<?>[] {repositoryInterface},
                        invocationHandler);
    }
}
