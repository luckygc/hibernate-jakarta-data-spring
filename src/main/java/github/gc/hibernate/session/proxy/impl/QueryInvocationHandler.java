package github.gc.hibernate.session.proxy.impl;

import github.gc.hibernate.session.proxy.QueryProxySupport;
import org.hibernate.StatelessSession;
import org.jspecify.annotations.NonNull;
import org.springframework.util.Assert;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Set;

/**
 * Generic invocation handler for Hibernate Query proxies.
 */
class QueryInvocationHandler<T> extends QueryProxySupport implements InvocationHandler {

    private static final Set<String> EXECUTE_METHODS = Set.of(
            "list", "getResultList", "scroll", "getResultStream", "stream",
            "uniqueResult", "getSingleResult", "getSingleResultOrNull",
            "uniqueResultOptional", "getResultCount", "getKeyedResultList",
            "executeUpdate"
    );

    private final T delegate;
    private final Class<T> interfaceType;
    private final T proxy;

    @SuppressWarnings("unchecked")
    QueryInvocationHandler(@NonNull T delegate, @NonNull Class<T> interfaceType, @NonNull StatelessSession session) {
        super(session);
        Assert.notNull(delegate, "delegate must not be null");
        Assert.notNull(interfaceType, "interfaceType must not be null");
        this.delegate = delegate;
        this.interfaceType = interfaceType;
        this.proxy = (T) Proxy.newProxyInstance(interfaceType.getClassLoader(), new Class<?>[]{interfaceType}, this);
    }

    T getProxy() {
        return this.proxy;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (EXECUTE_METHODS.contains(method.getName())) {
            return execute(() -> invokeDelegate(method, args));
        }
        Object result = invokeDelegate(method, args);
        if (result == this.delegate && method.getReturnType().isAssignableFrom(this.interfaceType)) {
            return this.proxy;
        }
        return result;
    }

    private Object invokeDelegate(Method method, Object[] args) throws Throwable {
        try {
            return method.invoke(this.delegate, args);
        } catch (InvocationTargetException ex) {
            throw ex.getTargetException();
        }
    }
}
