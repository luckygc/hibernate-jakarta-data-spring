package github.gc.jakartadata.proxy;

import github.gc.jakartadata.handler.RepositoryMethodHandler;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;

import javax.sql.DataSource;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Jakarta Data Repository 代理类
 * 负责拦截 Repository 方法调用并委托给实际的实现
 *
 * @author gc
 */
public class JakartaDataRepositoryProxy<T> implements InvocationHandler {

    private static final Logger log = LoggerFactory.getLogger(JakartaDataRepositoryProxy.class);

    private final Class<T> repositoryInterface;
    private final SessionFactory sessionFactory;
    private final DataSource dataSource;

    // 缓存方法处理器
    private final ConcurrentHashMap<Method, RepositoryMethodHandler> methodHandlerCache = new ConcurrentHashMap<>();

    public JakartaDataRepositoryProxy(@NonNull Class<T> repositoryInterface,
                                    @NonNull SessionFactory sessionFactory,
                                    @NonNull DataSource dataSource) {
        this.repositoryInterface = repositoryInterface;
        this.sessionFactory = sessionFactory;
        this.dataSource = dataSource;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // 处理 Object 类的方法
        if (Object.class.equals(method.getDeclaringClass())) {
            return handleObjectMethod(proxy, method, args);
        }

        // 获取或创建方法处理器
        RepositoryMethodHandler methodHandler = getMethodHandler(method);

        // 执行方法
        return methodHandler.execute(args);
    }

    /**
     * 获取方法处理器
     */
    private RepositoryMethodHandler getMethodHandler(Method method) {
        return methodHandlerCache.computeIfAbsent(method, m -> {
            log.debug("Creating method handler for: {}.{}",
                     repositoryInterface.getSimpleName(), m.getName());
            return new RepositoryMethodHandler(repositoryInterface, method, sessionFactory, dataSource);
        });
    }

    /**
     * 处理 Object 类的方法
     */
    private Object handleObjectMethod(Object proxy, Method method, Object[] args) throws Throwable {
        return switch (method.getName()) {
            case "equals" -> proxy == args[0];
            case "hashCode" -> System.identityHashCode(proxy);
            case "toString" ->
                repositoryInterface.getName() + "@" + Integer.toHexString(System.identityHashCode(proxy));
            default -> method.invoke(this, args);
        };
    }
}
