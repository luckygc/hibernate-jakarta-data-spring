package github.gc.hibernate.session.proxy.impl;

import github.gc.hibernate.session.proxy.QueryProxySupport;
import jakarta.persistence.*;
import org.hibernate.*;
import org.hibernate.graph.GraphSemantic;
import org.hibernate.query.*;
import org.hibernate.query.restriction.Restriction;
import org.jspecify.annotations.NonNull;
import org.springframework.util.Assert;

import java.time.Instant;
import java.util.*;
import java.util.stream.Stream;

@SuppressWarnings("deprecation")
public class SelectionQueryProxy<R> extends QueryProxySupport implements SelectionQuery<R> {

	private final SelectionQuery<R> delegate;

	public SelectionQueryProxy(@NonNull SelectionQuery<R> delegate, @NonNull StatelessSession session) {
		super(session);

		Assert.notNull(delegate, "No delegate specified");
		this.delegate = delegate;
	}

	@Override
	public List<R> list() {
		return execute(this.delegate::list);
	}

	@Override
	public List<R> getResultList() {
		return execute(this.delegate::getResultList);
	}

	@Override
	public ScrollableResults<R> scroll() {
		return execute(this.delegate::scroll);
	}

	@Override
	public ScrollableResults<R> scroll(ScrollMode scrollMode) {
		return execute(() -> this.delegate.scroll(scrollMode));
	}

	@Override
	public Stream<R> getResultStream() {
		return execute(this.delegate::getResultStream);
	}

	@Override
	public Stream<R> stream() {
		return execute(this.delegate::stream);
	}

	@Override
	public R uniqueResult() {
		return execute(this.delegate::uniqueResult);
	}

	@Override
	public R getSingleResult() {
		return execute(this.delegate::getSingleResult);
	}

	@Override
	public R getSingleResultOrNull() {
		return execute(this.delegate::getSingleResultOrNull);
	}

	@Override
	public Optional<R> uniqueResultOptional() {
		return execute(this.delegate::uniqueResultOptional);
	}

	@Override
	public long getResultCount() {
		return execute(this.delegate::getResultCount);
	}

	@Override
	public KeyedResultList<R> getKeyedResultList(KeyedPage<R> page) {
		return execute(() -> this.delegate.getKeyedResultList(page));
	}

	@Override
	public SelectionQuery<R> setHint(String hintName, Object value) {
		this.delegate.setHint(hintName, value);
		return this;
	}

	@Override
	public SelectionQuery<R> setEntityGraph(EntityGraph<R> graph, GraphSemantic semantic) {
		this.delegate.setEntityGraph(graph, semantic);
		return this;
	}

	@Override
	public SelectionQuery<R> enableFetchProfile(String profileName) {
		this.delegate.enableFetchProfile(profileName);
		return this;
	}

	@Override
	public SelectionQuery<R> disableFetchProfile(String profileName) {
		this.delegate.disableFetchProfile(profileName);
		return this;
	}

	@Override
	public SelectionQuery<R> setFlushMode(FlushModeType flushMode) {
		throw new UnsupportedOperationException("Deprecated");
	}

	@Override
	public FlushMode getHibernateFlushMode() {
		throw new UnsupportedOperationException("Deprecated");
	}

	@Override
	public SelectionQuery<R> setHibernateFlushMode(FlushMode flushMode) {
		throw new UnsupportedOperationException("Deprecated");
	}

	@Override
	public Integer getTimeout() {
		return this.delegate.getTimeout();
	}

	@Override
	public QueryFlushMode getQueryFlushMode() {
		return this.delegate.getQueryFlushMode();
	}

	@Override
	public SelectionQuery<R> setQueryFlushMode(QueryFlushMode queryFlushMode) {
		this.delegate.setQueryFlushMode(queryFlushMode);
		return this;
	}

	@Override
	public FlushModeType getFlushMode() {
		throw new UnsupportedOperationException("Deprecated");
	}

	@Override
	public SelectionQuery<R> setTimeout(int timeout) {
		this.delegate.setTimeout(timeout);
		return this;
	}

	@Override
	public String getComment() {
		return this.delegate.getComment();
	}

	@Override
	public SelectionQuery<R> setComment(String comment) {
		this.delegate.setComment(comment);
		return this;
	}

	@Override
	public Integer getFetchSize() {
		return this.delegate.getFetchSize();
	}

	@Override
	public SelectionQuery<R> setFetchSize(int fetchSize) {
		this.delegate.setFetchSize(fetchSize);
		return this;
	}

	@Override
	public boolean isReadOnly() {
		return this.delegate.isReadOnly();
	}

	@Override
	public SelectionQuery<R> setReadOnly(boolean readOnly) {
		this.delegate.setReadOnly(readOnly);
		return this;
	}

	@Override
	public int getMaxResults() {
		return this.delegate.getMaxResults();
	}

	@Override
	public SelectionQuery<R> setMaxResults(int maxResults) {
		this.delegate.setMaxResults(maxResults);
		return this;
	}

	@Override
	public int getFirstResult() {
		return this.delegate.getFirstResult();
	}

	@Override
	public SelectionQuery<R> setFirstResult(int startPosition) {
		this.delegate.setFirstResult(startPosition);
		return this;
	}

	@Override
	public SelectionQuery<R> setPage(Page page) {
		this.delegate.setPage(page);
		return this;
	}

	@Override
	public CacheMode getCacheMode() {
		return this.delegate.getCacheMode();
	}

	@Override
	public CacheStoreMode getCacheStoreMode() {
		return this.delegate.getCacheStoreMode();
	}

	@Override
	public CacheRetrieveMode getCacheRetrieveMode() {
		return this.delegate.getCacheRetrieveMode();
	}

	@Override
	public SelectionQuery<R> setCacheMode(CacheMode cacheMode) {
		this.delegate.setCacheMode(cacheMode);
		return this;
	}

	@Override
	public SelectionQuery<R> setCacheStoreMode(CacheStoreMode cacheStoreMode) {
		this.delegate.setCacheStoreMode(cacheStoreMode);
		return this;
	}

	@Override
	public SelectionQuery<R> setCacheRetrieveMode(CacheRetrieveMode cacheRetrieveMode) {
		this.delegate.setCacheRetrieveMode(cacheRetrieveMode);
		return this;
	}

	@Override
	public boolean isCacheable() {
		return this.delegate.isCacheable();
	}

	@Override
	public SelectionQuery<R> setCacheable(boolean cacheable) {
		this.delegate.setCacheable(cacheable);
		return this;
	}

	@Override
	public boolean isQueryPlanCacheable() {
		return this.delegate.isQueryPlanCacheable();
	}

	@Override
	public SelectionQuery<R> setQueryPlanCacheable(boolean queryPlanCacheable) {
		this.delegate.setQueryPlanCacheable(queryPlanCacheable);
		return this;
	}

	@Override
	public String getCacheRegion() {
		return this.delegate.getCacheRegion();
	}

	@Override
	public SelectionQuery<R> setCacheRegion(String cacheRegion) {
		this.delegate.setCacheRegion(cacheRegion);
		return this;
	}

	@Override
	public LockOptions getLockOptions() {
		return this.delegate.getLockOptions();
	}

	@Override
	public LockModeType getLockMode() {
		return this.delegate.getLockMode();
	}

	@Override
	public SelectionQuery<R> setLockMode(LockModeType lockMode) {
		this.delegate.setLockMode(lockMode);
		return this;
	}

	@Override
	public LockMode getHibernateLockMode() {
		return this.delegate.getHibernateLockMode();
	}

	@Override
	public SelectionQuery<R> setHibernateLockMode(LockMode lockMode) {
		this.delegate.setHibernateLockMode(lockMode);
		return this;
	}

	@Override
	public SelectionQuery<R> setLockMode(String alias, LockMode lockMode) {
		this.delegate.setLockMode(alias, lockMode);
		return this;
	}

	@Override
	public SelectionQuery<R> setOrder(List<? extends Order<? super R>> orderList) {
		this.delegate.setOrder(orderList);
		return this;
	}

	@Override
	public SelectionQuery<R> setOrder(Order<? super R> order) {
		this.delegate.setOrder(order);
		return this;
	}

	@Override
	public SelectionQuery<R> addRestriction(Restriction<? super R> restriction) {
		this.delegate.addRestriction(restriction);
		return this;
	}

	@Override
	public SelectionQuery<R> setFollowOnLocking(boolean enable) {
		this.delegate.setFollowOnLocking(enable);
		return this;
	}

	@Override
	public SelectionQuery<R> setParameter(String name, Object value) {
		this.delegate.setParameter(name, value);
		return this;
	}

	@Override
	public <P> SelectionQuery<R> setParameter(String name, P value, Class<P> type) {
		this.delegate.setParameter(name, value, type);
		return this;
	}

	@Override
	public <P> SelectionQuery<R> setParameter(String name, P value, BindableType<P> type) {
		this.delegate.setParameter(name, value, type);
		return this;
	}

	@Override
	public SelectionQuery<R> setParameter(String name, Instant value, TemporalType temporalType) {
		throw new UnsupportedOperationException("Deprecated");
	}

	@Override
	public SelectionQuery<R> setParameter(String name, Calendar value, TemporalType temporalType) {
		throw new UnsupportedOperationException("Deprecated");
	}

	@Override
	public SelectionQuery<R> setParameter(String name, Date value, TemporalType temporalType) {
		throw new UnsupportedOperationException("Deprecated");
	}

	@Override
	public SelectionQuery<R> setParameter(int position, Object value) {
		this.delegate.setParameter(position, value);
		return this;
	}

	@Override
	public <P> SelectionQuery<R> setParameter(int position, P value, Class<P> type) {
		this.delegate.setParameter(position, value, type);
		return this;
	}

	@Override
	public <P> SelectionQuery<R> setParameter(int position, P value, BindableType<P> type) {
		this.delegate.setParameter(position, value, type);
		return this;
	}

	@Override
	public SelectionQuery<R> setParameter(int position, Instant value, TemporalType temporalType) {
		throw new UnsupportedOperationException("Deprecated");
	}

	@Override
	public SelectionQuery<R> setParameter(int position, Date value, TemporalType temporalType) {
		throw new UnsupportedOperationException("Deprecated");
	}

	@Override
	public SelectionQuery<R> setParameter(int position, Calendar value, TemporalType temporalType) {
		throw new UnsupportedOperationException("Deprecated");
	}

	@Override
	public <T> SelectionQuery<R> setParameter(QueryParameter<T> parameter, T value) {
		this.delegate.setParameter(parameter, value);
		return this;
	}

	@Override
	public <P> SelectionQuery<R> setParameter(QueryParameter<P> parameter, P value, Class<P> type) {
		this.delegate.setParameter(parameter, value, type);
		return this;
	}

	@Override
	public <P> SelectionQuery<R> setParameter(QueryParameter<P> parameter, P value, BindableType<P> type) {
		this.delegate.setParameter(parameter, value, type);
		return this;
	}

	@Override
	public <T> SelectionQuery<R> setParameter(Parameter<T> param, T value) {
		this.delegate.setParameter(param, value);
		return this;
	}

	@Override
	public SelectionQuery<R> setParameter(Parameter<Calendar> param, Calendar value, TemporalType temporalType) {
		throw new UnsupportedOperationException("Deprecated");
	}

	@Override
	public SelectionQuery<R> setParameter(Parameter<Date> param, Date value, TemporalType temporalType) {
		throw new UnsupportedOperationException("Deprecated");
	}

	@Override
	public SelectionQuery<R> setParameterList(String name, Collection values) {
		this.delegate.setParameterList(name, values);
		return this;
	}

	@Override
	public <P> SelectionQuery<R> setParameterList(String name, Collection<? extends P> values, Class<P> javaType) {
		this.delegate.setParameterList(name, values, javaType);
		return this;
	}

	@Override
	public <P> SelectionQuery<R> setParameterList(String name, Collection<? extends P> values, BindableType<P> type) {
		this.delegate.setParameterList(name, values, type);
		return this;
	}

	@Override
	public SelectionQuery<R> setParameterList(String name, Object[] values) {
		this.delegate.setParameterList(name, values);
		return this;
	}

	@Override
	public <P> SelectionQuery<R> setParameterList(String name, P[] values, Class<P> javaType) {
		this.delegate.setParameterList(name, values, javaType);
		return this;
	}

	@Override
	public <P> SelectionQuery<R> setParameterList(String name, P[] values, BindableType<P> type) {
		this.delegate.setParameterList(name, values, type);
		return this;
	}

	@Override
	public SelectionQuery<R> setParameterList(int position, Collection values) {
		this.delegate.setParameterList(position, values);
		return this;
	}

	@Override
	public <P> SelectionQuery<R> setParameterList(int position, Collection<? extends P> values, Class<P> javaType) {
		this.delegate.setParameterList(position, values, javaType);
		return this;
	}

	@Override
	public <P> SelectionQuery<R> setParameterList(int position, Collection<? extends P> values, BindableType<P> type) {
		this.delegate.setParameterList(position, values, type);
		return this;
	}

	@Override
	public SelectionQuery<R> setParameterList(int position, Object[] values) {
		this.delegate.setParameterList(position, values);
		return this;
	}

	@Override
	public <P> SelectionQuery<R> setParameterList(int position, P[] values, Class<P> javaType) {
		this.delegate.setParameterList(position, values, javaType);
		return this;
	}

	@Override
	public <P> SelectionQuery<R> setParameterList(int position, P[] values, BindableType<P> type) {
		this.delegate.setParameterList(position, values, type);
		return this;
	}

	@Override
	public <P> SelectionQuery<R> setParameterList(QueryParameter<P> parameter, Collection<? extends P> values) {
		this.delegate.setParameterList(parameter, values);
		return this;
	}

	@Override
	public <P> SelectionQuery<R> setParameterList(QueryParameter<P> parameter, Collection<? extends P> values,
			Class<P> javaType) {
		this.delegate.setParameterList(parameter, values, javaType);
		return this;
	}

	@Override
	public <P> SelectionQuery<R> setParameterList(QueryParameter<P> parameter, Collection<? extends P> values,
			BindableType<P> type) {
		this.delegate.setParameterList(parameter, values, type);
		return this;
	}

	@Override
	public <P> SelectionQuery<R> setParameterList(QueryParameter<P> parameter, P[] values) {
		this.delegate.setParameterList(parameter, values);
		return this;
	}

	@Override
	public <P> SelectionQuery<R> setParameterList(QueryParameter<P> parameter, P[] values, Class<P> javaType) {
		this.delegate.setParameterList(parameter, values, javaType);
		return this;
	}

	@Override
	public <P> SelectionQuery<R> setParameterList(QueryParameter<P> parameter, P[] values, BindableType<P> type) {
		this.delegate.setParameterList(parameter, values, type);
		return this;
	}

	@Override
	public SelectionQuery<R> setProperties(Object bean) {
		this.delegate.setProperties(bean);
		return this;
	}

	@Override
	public SelectionQuery<R> setProperties(Map bean) {
		this.delegate.setProperties(bean);
		return this;
	}
}