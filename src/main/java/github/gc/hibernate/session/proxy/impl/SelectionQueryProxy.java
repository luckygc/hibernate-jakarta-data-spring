package github.gc.hibernate.session.proxy.impl;

import org.hibernate.StatelessSession;
import org.hibernate.query.SelectionQuery;
import org.jspecify.annotations.NonNull;

/**
 * JDK dynamic proxy for {@link SelectionQuery}.
 */
public class SelectionQueryProxy<R> extends QueryInvocationHandler<SelectionQuery<R>> {

    public SelectionQueryProxy(@NonNull SelectionQuery<R> delegate, @NonNull StatelessSession session) {
        super(delegate, SelectionQuery.class, session);
    }

    public SelectionQuery<R> getProxy() {
        return super.getProxy();
    }
}
