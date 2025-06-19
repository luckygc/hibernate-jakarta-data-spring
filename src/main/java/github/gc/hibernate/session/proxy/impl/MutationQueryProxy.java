package github.gc.hibernate.session.proxy.impl;

import org.hibernate.StatelessSession;
import org.hibernate.query.MutationQuery;
import org.jspecify.annotations.NonNull;

/**
 * JDK dynamic proxy for {@link MutationQuery}.
 */
public class MutationQueryProxy extends QueryInvocationHandler<MutationQuery> {

    public MutationQueryProxy(@NonNull MutationQuery delegate, @NonNull StatelessSession session) {
        super(delegate, MutationQuery.class, session);
    }

    public MutationQuery getProxy() {
        return super.getProxy();
    }
}
