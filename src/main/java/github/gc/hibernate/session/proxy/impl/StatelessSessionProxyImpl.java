package github.gc.hibernate.session.proxy.impl;

import github.gc.hibernate.session.proxy.StatelessSessionProxy;
import org.hibernate.SessionFactory;
import org.hibernate.StatelessSession;
import org.hibernate.cfg.AvailableSettings;
import org.jspecify.annotations.NonNull;
import org.springframework.util.Assert;

import javax.sql.DataSource;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * StatelessSession proxy based on JDK dynamic proxies. The actual
 * {@link StatelessSession} is bound to the current thread for the
 * duration of a repository method invocation.
 */
public class StatelessSessionProxyImpl implements InvocationHandler {

    private static final ThreadLocal<StatelessSession> SESSION_HOLDER = new ThreadLocal<>();

    private final SessionFactory sessionFactory;
    private final DataSource dataSource;
    private final StatelessSessionProxy proxy;

    public StatelessSessionProxyImpl(@NonNull SessionFactory sessionFactory) {
        Assert.notNull(sessionFactory, "SessionFactory must not be null");
        this.sessionFactory = sessionFactory;
        Object dsObj = sessionFactory.getProperties().get(AvailableSettings.JAKARTA_NON_JTA_DATASOURCE);
        if (dsObj instanceof DataSource ds) {
            this.dataSource = ds;
        } else {
            throw new IllegalArgumentException("SessionFactory must have a DataSource");
        }
        this.proxy = (StatelessSessionProxy) Proxy.newProxyInstance(
                StatelessSessionProxy.class.getClassLoader(),
                new Class<?>[]{StatelessSessionProxy.class}, this);
    }

    public StatelessSession getProxy() {
        return this.proxy;
    }

    SessionFactory getSessionFactory() {
        return this.sessionFactory;
    }

    DataSource getDataSource() {
        return this.dataSource;
    }

    void bind(StatelessSession session) {
        SESSION_HOLDER.set(session);
    }

    void unbind() {
        SESSION_HOLDER.remove();
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if ("getCurrentSession".equals(method.getName()) && method.getParameterCount() == 0) {
            return SESSION_HOLDER.get();
        }
        StatelessSession session = SESSION_HOLDER.get();
        if (session == null) {
            throw new IllegalStateException("No StatelessSession bound to current thread");
        }
        return method.invoke(session, args);
    }
}
