package github.gc.hibernate.session.proxy.impl;

import github.gc.hibernate.session.proxy.QueryProxySupport;
import jakarta.persistence.*;
import org.hibernate.*;
import org.hibernate.graph.GraphSemantic;
import org.hibernate.graph.RootGraph;
import org.hibernate.query.Query;
import org.hibernate.query.*;
import org.hibernate.query.restriction.Restriction;
import org.hibernate.query.spi.QueryOptions;
import org.hibernate.transform.ResultTransformer;
import org.jspecify.annotations.NonNull;
import org.springframework.util.Assert;

import java.time.Instant;
import java.util.*;
import java.util.stream.Stream;

public class QueryProxy<R> extends QueryProxySupport implements Query<R> {

	private final Query<R> delegate;

	public QueryProxy(@NonNull Query<R> delegate, @NonNull StatelessSession session) {
		super(session);

		Assert.notNull(delegate, "delegate must not be null");
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
		return execute(this.delegate::getResultStream);
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
	public int executeUpdate() {
		return execute(this.delegate::executeUpdate);
	}

	@Override
	public SharedSessionContract getSession() {
		return this.delegate.getSession();
	}

	@Override
	public String getQueryString() {
		return this.delegate.getQueryString();
	}

	@Override
	public Query<R> applyGraph(RootGraph graph, GraphSemantic semantic) {
		throw new UnsupportedOperationException("Deprecated");
	}

	@Override
	public Query<R> applyFetchGraph(RootGraph graph) {
		throw new UnsupportedOperationException("Deprecated");
	}

	@Override
	public Query<R> applyLoadGraph(RootGraph graph) {
		throw new UnsupportedOperationException("Deprecated");
	}

	@Override
	public String getComment() {
		return this.delegate.getComment();
	}

	@Override
	public Query<R> setComment(String comment) {
		this.delegate.setComment(comment);
		return this;
	}

	@Override
	public Integer getFetchSize() {
		return this.delegate.getFetchSize();
	}

	@Override
	public Query<R> addQueryHint(String hint) {
		this.delegate.addQueryHint(hint);
		return this;
	}

	@Override
	public LockOptions getLockOptions() {
		return this.delegate.getLockOptions();
	}

	@Override
	public Query<R> setLockOptions(LockOptions lockOptions) {
		this.delegate.setLockOptions(lockOptions);
		return this;
	}

	@Override
	public LockModeType getLockMode() {
		return this.delegate.getLockMode();
	}

	@Override
	public Query<R> setLockMode(String alias, LockMode lockMode) {
		this.delegate.setLockMode(alias, lockMode);
		return this;
	}

	@Override
	public Query<R> setResultListTransformer(ResultListTransformer<R> transformer) {
		this.delegate.setResultListTransformer(transformer);
		return this;
	}

	@Override
	public QueryOptions getQueryOptions() {
		return this.delegate.getQueryOptions();
	}

	@Override
	public ParameterMetadata getParameterMetadata() {
		return this.delegate.getParameterMetadata();
	}

	@Override
	public Query<R> setParameter(String parameter, Object argument) {
		this.delegate.setParameter(parameter, argument);
		return this;
	}

	@Override
	public Query<R> setParameter(String parameter, Instant argument, TemporalType temporalType) {
		throw new UnsupportedOperationException("Deprecated");
	}

	@Override
	public Query<R> setParameter(String parameter, Calendar argument, TemporalType temporalType) {
		throw new UnsupportedOperationException("Deprecated");
	}

	@Override
	public Query<R> setParameter(String parameter, Date argument, TemporalType temporalType) {
		throw new UnsupportedOperationException("Deprecated");
	}

	@Override
	public Query<R> setParameter(int parameter, Object argument) {
		this.delegate.setParameter(parameter, argument);
		return this;
	}

	@Override
	public Query<R> setParameter(int parameter, Instant argument, TemporalType temporalType) {
		throw new UnsupportedOperationException("Deprecated");
	}

	@Override
	public Query<R> setParameter(int parameter, Date argument, TemporalType temporalType) {
		throw new UnsupportedOperationException("Deprecated");
	}

	@Override
	public Set<Parameter<?>> getParameters() {
		return this.delegate.getParameters();
	}

	@Override
	public Parameter<?> getParameter(String s) {
		return this.delegate.getParameter(s);
	}

	@Override
	public <T> Parameter<T> getParameter(String s, Class<T> aClass) {
		return this.delegate.getParameter(s, aClass);
	}

	@Override
	public Parameter<?> getParameter(int i) {
		return this.delegate.getParameter(i);
	}

	@Override
	public <T> Parameter<T> getParameter(int i, Class<T> aClass) {
		return this.delegate.getParameter(i, aClass);
	}

	@Override
	public boolean isBound(Parameter<?> parameter) {
		return this.delegate.isBound(parameter);
	}

	@Override
	public <T> T getParameterValue(Parameter<T> parameter) {
		return this.delegate.getParameterValue(parameter);
	}

	@Override
	public Object getParameterValue(String s) {
		return this.delegate.getParameterValue(s);
	}

	@Override
	public Object getParameterValue(int i) {
		return this.delegate.getParameterValue(i);
	}

	@Override
	public Query<R> setParameter(int parameter, Calendar argument, TemporalType temporalType) {
		throw new UnsupportedOperationException("Deprecated");
	}

	@Override
	public Query<R> setParameterList(String parameter, Collection arguments) {
		this.delegate.setParameterList(parameter, arguments);
		return this;
	}

	@Override
	public Query<R> setParameterList(String parameter, Object[] values) {
		this.delegate.setParameterList(parameter, values);
		return this;
	}

	@Override
	public Query<R> setParameterList(int parameter, Collection arguments) {
		this.delegate.setParameterList(parameter, arguments);
		return this;
	}

	@Override
	public Query<R> setParameterList(int parameter, Object[] arguments) {
		this.delegate.setParameterList(parameter, arguments);
		return this;
	}

	@Override
	public Query<R> setProperties(Object bean) {
		this.delegate.setProperties(bean);
		return this;
	}

	@Override
	public Query<R> setProperties(Map bean) {
		this.delegate.setProperties(bean);
		return this;
	}

	@Override
	public Query<R> setHibernateFlushMode(FlushMode flushMode) {
		throw new UnsupportedOperationException("Deprecated");
	}

	@Override
	public Integer getTimeout() {
		return this.delegate.getTimeout();
	}

	@Override
	public <T> T unwrap(Class<T> aClass) {
		return this.delegate.unwrap(aClass);
	}

	@Override
	public QueryFlushMode getQueryFlushMode() {
		return this.delegate.getQueryFlushMode();
	}

	@Override
	public Query<R> setQueryFlushMode(QueryFlushMode queryFlushMode) {
		this.delegate.setQueryFlushMode(queryFlushMode);
		return this;
	}

	@Override
	public FlushModeType getFlushMode() {
		throw new UnsupportedOperationException("Deprecated");
	}

	@Override
	public Query<R> setCacheable(boolean cacheable) {
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
	public Query<R> setCacheRegion(String cacheRegion) {
		this.delegate.setCacheRegion(cacheRegion);
		return this;
	}

	@Override
	public Query<R> setCacheMode(CacheMode cacheMode) {
		this.delegate.setCacheMode(cacheMode);
		return this;
	}

	@Override
	public Query<R> setCacheStoreMode(CacheStoreMode cacheStoreMode) {
		this.delegate.setCacheStoreMode(cacheStoreMode);
		return this;
	}

	@Override
	public TypedQuery<R> setTimeout(Integer integer) {
		this.delegate.setTimeout(integer);
		return this;
	}

	@Override
	public Query<R> setCacheRetrieveMode(CacheRetrieveMode cacheRetrieveMode) {
		this.delegate.setCacheRetrieveMode(cacheRetrieveMode);
		return this;
	}

	@Override
	public boolean isCacheable() {
		return this.delegate.isCacheable();
	}

	@Override
	public Query<R> setTimeout(int timeout) {
		this.delegate.setTimeout(timeout);
		return this;
	}

	@Override
	public Query<R> setFetchSize(int fetchSize) {
		this.delegate.setFetchSize(fetchSize);
		return this;
	}

	@Override
	public boolean isReadOnly() {
		return this.delegate.isReadOnly();
	}

	@Override
	public Query<R> setReadOnly(boolean readOnly) {
		this.delegate.setReadOnly(readOnly);
		return this;
	}

	@Override
	public int getMaxResults() {
		return this.delegate.getMaxResults();
	}

	@Override
	public Query<R> setMaxResults(int maxResults) {
		this.delegate.setMaxResults(maxResults);
		return this;
	}

	@Override
	public int getFirstResult() {
		return this.delegate.getFirstResult();
	}

	@Override
	public Query<R> setFirstResult(int startPosition) {
		this.delegate.setFirstResult(startPosition);
		return this;
	}

	@Override
	public Query<R> setPage(Page page) {
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
	public Query<R> setHint(String hintName, Object value) {
		this.delegate.setHint(hintName, value);
		return this;
	}

	@Override
	public Map<String, Object> getHints() {
		return this.delegate.getHints();
	}

	@Override
	public Query<R> setEntityGraph(EntityGraph<R> graph, GraphSemantic semantic) {
		this.delegate.setEntityGraph(graph, semantic);
		return this;
	}

	@Override
	public Query<R> enableFetchProfile(String profileName) {
		this.delegate.enableFetchProfile(profileName);
		return this;
	}

	@Override
	public Query<R> disableFetchProfile(String profileName) {
		this.delegate.disableFetchProfile(profileName);
		return this;
	}

	@Override
	public Query<R> setFlushMode(FlushModeType flushMode) {
		throw new UnsupportedOperationException("Deprecated");
	}

	@Override
	public FlushMode getHibernateFlushMode() {
		throw new UnsupportedOperationException("Deprecated");
	}

	@Override
	public Query<R> setLockMode(LockModeType lockMode) {
		this.delegate.setLockMode(lockMode);
		return this;
	}

	@Override
	public <T> Query<T> setResultTransformer(ResultTransformer<T> transformer) {
		throw new UnsupportedOperationException("Deprecated");
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
	public Query<R> setOrder(Order<? super R> order) {
		this.delegate.setOrder(order);
		return this;
	}

	@Override
	public Query<R> addRestriction(Restriction<? super R> restriction) {
		this.delegate.addRestriction(restriction);
		return this;
	}

	@Override
	public SelectionQuery<R> setFollowOnLocking(boolean enable) {
		this.delegate.setFollowOnLocking(enable);
		return this;
	}

	@Override
	public Query<R> setOrder(List<? extends Order<? super R>> orderList) {
		this.delegate.setOrder(orderList);
		return this;
	}

	@Override
	public <P> Query<R> setParameterList(QueryParameter<P> parameter, P[] arguments, BindableType<P> type) {
		this.delegate.setParameterList(parameter, arguments, type);
		return this;
	}

	@Override
	public <P> Query<R> setParameterList(QueryParameter<P> parameter, P[] arguments, Class<P> javaType) {
		this.delegate.setParameterList(parameter, arguments, javaType);
		return this;
	}

	@Override
	public <P> Query<R> setParameterList(QueryParameter<P> parameter, P[] arguments) {
		this.delegate.setParameterList(parameter, arguments);
		return this;
	}

	@Override
	public <P> Query<R> setParameterList(QueryParameter<P> parameter, Collection<? extends P> arguments,
			BindableType<P> type) {
		this.delegate.setParameterList(parameter, arguments, type);
		return this;
	}

	@Override
	public <P> Query<R> setParameterList(QueryParameter<P> parameter, Collection<? extends P> arguments,
			Class<P> javaType) {
		this.delegate.setParameterList(parameter, arguments, javaType);
		return this;
	}

	@Override
	public <P> Query<R> setParameterList(QueryParameter<P> parameter, Collection<? extends P> arguments) {
		this.delegate.setParameterList(parameter, arguments);
		return this;
	}

	@Override
	public <P> Query<R> setParameterList(int parameter, P[] arguments, BindableType<P> type) {
		this.delegate.setParameterList(parameter, arguments, type);
		return this;
	}

	@Override
	public <P> Query<R> setParameterList(int parameter, P[] arguments, Class<P> javaType) {
		this.delegate.setParameterList(parameter, arguments, javaType);
		return this;
	}

	@Override
	public <P> Query<R> setParameterList(int parameter, Collection<? extends P> arguments, BindableType<P> type) {
		this.delegate.setParameterList(parameter, arguments, type);
		return this;
	}

	@Override
	public <P> Query<R> setParameterList(int parameter, Collection<? extends P> arguments, Class<P> javaType) {
		this.delegate.setParameterList(parameter, arguments, javaType);
		return this;
	}

	@Override
	public <P> Query<R> setParameterList(String parameter, P[] arguments, BindableType<P> type) {
		this.delegate.setParameterList(parameter, arguments, type);
		return this;
	}

	@Override
	public <P> Query<R> setParameterList(String parameter, P[] arguments, Class<P> javaType) {
		this.delegate.setParameterList(parameter, arguments, javaType);
		return this;
	}

	@Override
	public <P> Query<R> setParameterList(String parameter, Collection<? extends P> arguments, BindableType<P> type) {
		this.delegate.setParameterList(parameter, arguments, type);
		return this;
	}

	@Override
	public <P> Query<R> setParameterList(String parameter, Collection<? extends P> arguments, Class<P> javaType) {
		this.delegate.setParameterList(parameter, arguments, javaType);
		return this;
	}

	@Override
	public Query<R> setParameter(Parameter<Date> parameter, Date argument, TemporalType temporalType) {
		throw new UnsupportedOperationException("Deprecated");
	}

	@Override
	public Query<R> setParameter(Parameter<Calendar> parameter, Calendar argument, TemporalType temporalType) {
		throw new UnsupportedOperationException("Deprecated");
	}

	@Override
	public <T> Query<R> setParameter(Parameter<T> parameter, T argument) {
		this.delegate.setParameter(parameter, argument);
		return this;
	}

	@Override
	public <P> Query<R> setParameter(QueryParameter<P> parameter, P argument, BindableType<P> type) {
		this.delegate.setParameter(parameter, argument, type);
		return this;
	}

	@Override
	public <P> Query<R> setParameter(QueryParameter<P> parameter, P argument, Class<P> type) {
		this.delegate.setParameter(parameter, argument, type);
		return this;
	}

	@Override
	public <T> Query<R> setParameter(QueryParameter<T> parameter, T argument) {
		this.delegate.setParameter(parameter, argument);
		return this;
	}

	@Override
	public <P> Query<R> setParameter(int parameter, P argument, BindableType<P> type) {
		this.delegate.setParameter(parameter, argument, type);
		return this;
	}

	@Override
	public <P> Query<R> setParameter(int parameter, P argument, Class<P> type) {
		this.delegate.setParameter(parameter, argument, type);
		return this;
	}

	@Override
	public <P> Query<R> setParameter(String parameter, P argument, BindableType<P> type) {
		this.delegate.setParameter(parameter, argument, type);
		return this;
	}

	@Override
	public <P> Query<R> setParameter(String parameter, P argument, Class<P> type) {
		this.delegate.setParameter(parameter, argument, type);
		return this;
	}

	@Override
	public <T> Query<T> setTupleTransformer(TupleTransformer<T> transformer) {
		this.delegate.setTupleTransformer(transformer);
		return (Query<T>) this;
	}
}