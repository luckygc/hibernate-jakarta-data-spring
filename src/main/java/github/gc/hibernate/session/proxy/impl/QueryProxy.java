package github.gc.hibernate.session.proxy.impl;

import org.hibernate.StatelessSession;
import org.hibernate.query.Query;
import org.jspecify.annotations.NonNull;

/**
 * JDK dynamic proxy for {@link Query}.
 */
public class QueryProxy<R> extends QueryInvocationHandler<Query<R>> {

    public QueryProxy(@NonNull Query<R> delegate, @NonNull StatelessSession session) {
        super(delegate, Query.class, session);
    }

    public Query<R> getProxy() {
        return super.getProxy();
    }
}
