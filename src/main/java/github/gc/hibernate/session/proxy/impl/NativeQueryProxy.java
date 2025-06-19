package github.gc.hibernate.session.proxy.impl;

import org.hibernate.StatelessSession;
import org.hibernate.query.NativeQuery;
import org.jspecify.annotations.NonNull;

/**
 * JDK dynamic proxy for {@link NativeQuery}.
 */
public class NativeQueryProxy<T> extends QueryInvocationHandler<NativeQuery<T>> {

    public NativeQueryProxy(@NonNull NativeQuery<T> delegate, @NonNull StatelessSession session) {
        super(delegate, (Class<NativeQuery<T>>) (Class<?>) NativeQuery.class, session);
    }

    public NativeQuery<T> getProxy() {
        return super.getProxy();
    }
}
