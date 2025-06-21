package github.gc.jakartadata.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.util.Assert;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Hibernate Repository 工具类
 * 提供 Repository 相关的实用方法
 */
public final class HibernateRepositoryUtils {

    private static final Logger log = LoggerFactory.getLogger(HibernateRepositoryUtils.class);

    /**
     * Repository 实现类缓存
     */
    private static final ConcurrentMap<Class<?>, Class<?>> IMPLEMENTATION_CLASS_CACHE = 
        new ConcurrentHashMap<>();

    private HibernateRepositoryUtils() {
        // 工具类，禁止实例化
    }

    /**
     * 获取 Repository 接口对应的 Hibernate 生成的实现类
     * 
     * @param repositoryInterface Repository 接口类
     * @return Hibernate 生成的实现类
     * @throws RuntimeException 如果找不到实现类
     */
    @SuppressWarnings("unchecked")
    public static <R, I extends R> Class<I> getRepositoryImplementationClass(@NonNull Class<R> repositoryInterface) {
        Assert.notNull(repositoryInterface, "Repository interface must not be null");

        return (Class<I>) IMPLEMENTATION_CLASS_CACHE.computeIfAbsent(
            repositoryInterface, HibernateRepositoryUtils::loadImplementationClass);
    }

    /**
     * 加载 Repository 实现类
     */
    private static Class<?> loadImplementationClass(Class<?> repositoryInterface) {
        String interfaceName = repositoryInterface.getName();
        
        // Hibernate Processor 生成的实现类命名规则：接口名 + "_"
        String implementationClassName = interfaceName + "_";
        
        try {
            Class<?> implementationClass = Class.forName(implementationClassName);
            
            log.debug("Loaded repository implementation class: {} for interface: {}", 
                     implementationClassName, interfaceName);
            
            return implementationClass;
            
        } catch (ClassNotFoundException e) {
            String errorMessage = String.format(
                "Failed to load Hibernate generated repository implementation class: %s for interface: %s. " +
                "Make sure the Hibernate Processor is properly configured and the implementation class is generated.",
                implementationClassName, interfaceName);
            
            log.error(errorMessage, e);
            throw new RuntimeException(errorMessage, e);
        }
    }

    /**
     * 检查类是否为 Repository 接口
     */
    public static boolean isRepositoryInterface(@NonNull Class<?> clazz) {
        Assert.notNull(clazz, "Class must not be null");
        
        if (!clazz.isInterface()) {
            return false;
        }
        
        // 检查是否继承自 Jakarta Data Repository 相关接口
        return jakarta.data.repository.Repository.class.isAssignableFrom(clazz) ||
               jakarta.data.repository.CrudRepository.class.isAssignableFrom(clazz) ||
               jakarta.data.repository.DataRepository.class.isAssignableFrom(clazz);
    }

    /**
     * 生成 Repository Bean 名称
     */
    public static String generateRepositoryBeanName(@NonNull Class<?> repositoryInterface) {
        Assert.notNull(repositoryInterface, "Repository interface must not be null");
        
        String simpleName = repositoryInterface.getSimpleName();
        return Character.toLowerCase(simpleName.charAt(0)) + simpleName.substring(1);
    }

    /**
     * 清除实现类缓存（主要用于测试）
     */
    public static void clearImplementationClassCache() {
        IMPLEMENTATION_CLASS_CACHE.clear();
        log.debug("Cleared repository implementation class cache");
    }

    /**
     * 获取缓存大小（主要用于监控和测试）
     */
    public static int getCacheSize() {
        return IMPLEMENTATION_CLASS_CACHE.size();
    }
}
