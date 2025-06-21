package github.gc.jakartadata.repository;

import org.hibernate.SessionFactory;
import org.jspecify.annotations.NonNull;
import org.springframework.util.Assert;

import javax.sql.DataSource;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Repository代理缓存管理器，避免重复创建代理工厂
 * 参考MyBatis的MapperRegistry设计
 */
public class RepositoryProxyCache {

    private final ConcurrentMap<Class<?>, RepositoryProxyFactory<?>> proxyFactoryCache = new ConcurrentHashMap<>();
    private final SessionFactory sessionFactory;
    private final DataSource dataSource;

    public RepositoryProxyCache(@NonNull SessionFactory sessionFactory,
                               @NonNull DataSource dataSource) {
        Assert.notNull(sessionFactory, "sessionFactory must not be null");
        Assert.notNull(dataSource, "dataSource must not be null");
        this.sessionFactory = sessionFactory;
        this.dataSource = dataSource;
    }

    /**
     * 获取Repository实例，如果不存在则创建
     */
    @SuppressWarnings("unchecked")
    public <T> T getRepository(@NonNull Class<T> repositoryInterface) {
        Assert.notNull(repositoryInterface, "repositoryInterface must not be null");
        
        RepositoryProxyFactory<T> proxyFactory = (RepositoryProxyFactory<T>) proxyFactoryCache.computeIfAbsent(
                repositoryInterface,
                key -> new RepositoryProxyFactory<>(repositoryInterface, sessionFactory, dataSource)
        );
        
        return proxyFactory.newInstance();
    }

    /**
     * 检查Repository是否已注册
     */
    public boolean hasRepository(@NonNull Class<?> repositoryInterface) {
        Assert.notNull(repositoryInterface, "repositoryInterface must not be null");
        return proxyFactoryCache.containsKey(repositoryInterface);
    }

    /**
     * 获取已注册的Repository数量
     */
    public int getRepositoryCount() {
        return proxyFactoryCache.size();
    }

    /**
     * 清空缓存
     */
    public void clear() {
        proxyFactoryCache.clear();
    }
}
