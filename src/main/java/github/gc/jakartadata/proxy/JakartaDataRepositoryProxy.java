package github.gc.jakartadata.proxy;

import github.gc.jakartadata.ExceptionUtil;
import github.gc.jakartadata.session.StatelessSessionUtils;
import java.lang.reflect.Constructor;
import org.hibernate.SessionFactory;
import org.hibernate.StatelessSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;

import javax.sql.DataSource;
import java.io.Serial;
import java.io.Serializable;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.invoke.MethodType;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Jakarta Data Repository 代理类 负责拦截 Repository 方法调用并委托给实际的实现
 *
 * @author gc
 */
public class JakartaDataRepositoryProxy<T> implements InvocationHandler, Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private static final Logger log = LoggerFactory.getLogger(JakartaDataRepositoryProxy.class);

    private static final Method privateLookupInMethod;

    private final Class<T> repositoryInterface;
    private final Constructor<?> implementationConstructor;
    private final SessionFactory sessionFactory;
    private final DataSource dataSource;

    // 缓存 default 方法的 MethodHandle
    private final ConcurrentHashMap<Method, MethodHandle> methodHandleCache = new ConcurrentHashMap<>();

    static {
        Method privateLookupIn;
        try {
            privateLookupIn = MethodHandles.class.getMethod("privateLookupIn", Class.class, MethodHandles.Lookup.class);
        } catch (NoSuchMethodException e) {
            privateLookupIn = null;
        }
        privateLookupInMethod = privateLookupIn;
    }

    public JakartaDataRepositoryProxy(@NonNull Class<T> repositoryInterface,
        @NonNull SessionFactory sessionFactory,
        @NonNull DataSource dataSource) {
        this.repositoryInterface = repositoryInterface;
        this.sessionFactory = sessionFactory;
        this.dataSource = dataSource;

        var implementationClass = getImplementationClass(repositoryInterface);
        this.implementationConstructor = getImplementationConstructor(implementationClass);
    }

    /**
     * 获取 Hibernate 生成的实现类
     */
    private Class<?> getImplementationClass(Class<?> repositoryInterface) {
        try {
            String implementationClassName = repositoryInterface.getName() + "_";
            return Class.forName(implementationClassName);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Cannot find implementation class for repository: " +
                repositoryInterface.getName(), e);
        }
    }

    /**
     * 获取实现类的构造函数
     */
    private Constructor<?> getImplementationConstructor(Class<?> implementationClass) {
        try {
            return implementationClass.getDeclaredConstructor(StatelessSession.class);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Cannot find constructor with StatelessSession parameter for class: " +
                implementationClass.getName(), e);
        }
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        try {
            // 处理 Object 类的方法
            if (Object.class.equals(method.getDeclaringClass())) {
                return method.invoke(this, args);
            }

            if (method.isDefault()) {
                return invokeDefaultMethod(proxy, method, args);
            } else {
                return invokePlainMethod(method, args);
            }
        } catch (Throwable t) {
            throw ExceptionUtil.unwrapThrowable(t);
        }
    }

    /**
     * 调用 default 方法
     */
    private Object invokeDefaultMethod(Object proxy, Method method, Object[] args) throws Throwable {
        try {
            MethodHandle methodHandle = methodHandleCache.computeIfAbsent(method, this::createMethodHandle);
            return methodHandle.bindTo(proxy).invokeWithArguments(args);
        } catch (Exception e) {
            log.error("Error invoking default method: {}.{}",
                repositoryInterface.getSimpleName(), method.getName(), e);
            throw e;
        }
    }

    private Object invokePlainMethod(Method method, Object[] args) throws Throwable {
        StatelessSession session = StatelessSessionUtils.getSession(sessionFactory, dataSource);
        boolean shouldCloseSession = StatelessSessionUtils.shouldCloseSession(sessionFactory, dataSource);

        try {
            // 创建 Repository 实现实例
            Object repositoryImpl = implementationConstructor.newInstance(session);

            // 调用实际方法
            return method.invoke(repositoryImpl, args);

        } finally {
            // 如果需要关闭 Session
            if (shouldCloseSession) {
                StatelessSessionUtils.closeSession(session);
            }
        }
    }

    /**
     * 创建 default 方法的 MethodHandle
     */
    private MethodHandle createMethodHandle(Method method) {
        try {
            return getMethodHandleJava9(method);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException("Failed to create MethodHandle for default method: " + method, e);
        }
    }

    /**
     * 获取 Java 9+ 的 MethodHandle
     */
    private MethodHandle getMethodHandleJava9(Method method)
        throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        final Class<?> declaringClass = method.getDeclaringClass();
        return ((Lookup) privateLookupInMethod.invoke(null, declaringClass, MethodHandles.lookup())).findSpecial(
            declaringClass, method.getName(), MethodType.methodType(method.getReturnType(), method.getParameterTypes()),
            declaringClass);
    }
}
