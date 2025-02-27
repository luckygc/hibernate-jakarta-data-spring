package github.gc.hibernate.proxy.impl;

import github.gc.hibernate.proxy.QueryProxySupport;
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

@SuppressWarnings("deprecation")
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
		return this.delegate.applyGraph(graph, semantic);
	}

	@Override
	public Query<R> applyFetchGraph(RootGraph graph) {
		return this.delegate.applyFetchGraph(graph);
	}

	@Override
	public Query<R> applyLoadGraph(RootGraph graph) {
		return this.delegate.applyLoadGraph(graph);
	}

	@Override
	public String getComment() {
		return this.delegate.getComment();
	}

	@Override
	public Query<R> setComment(String comment) {
		return this.delegate.setComment(comment);
	}

	@Override
	public Integer getFetchSize() {
		return this.delegate.getFetchSize();
	}

	@Override
	public Query<R> addQueryHint(String hint) {
		return this.delegate.addQueryHint(hint);
	}

	@Override
	public LockOptions getLockOptions() {
		return this.delegate.getLockOptions();
	}

	@Override
	public Query<R> setLockOptions(LockOptions lockOptions) {
		return this.delegate.setLockOptions(lockOptions);
	}

	@Override
	public LockModeType getLockMode() {
		return this.delegate.getLockMode();
	}

	@Override
	public Query<R> setLockMode(String alias, LockMode lockMode) {
		return this.delegate.setLockMode(alias, lockMode);
	}

	@Override
	public Query<R> setResultListTransformer(ResultListTransformer<R> transformer) {
		return this.delegate.setResultListTransformer(transformer);
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
		return this.delegate.setParameter(parameter, argument);
	}

	@Override
	public Query<R> setParameter(String parameter, Instant argument, TemporalType temporalType) {
		return this.delegate.setParameter(parameter, argument, temporalType);
	}

	@Override
	public Query<R> setParameter(String parameter, Calendar argument, TemporalType temporalType) {
		return this.delegate.setParameter(parameter, argument, temporalType);
	}

	@Override
	public Query<R> setParameter(String parameter, Date argument, TemporalType temporalType) {
		return this.delegate.setParameter(parameter, argument, temporalType);
	}

	@Override
	public Query<R> setParameter(int parameter, Object argument) {
		return this.delegate.setParameter(parameter, argument);
	}

	@Override
	public Query<R> setParameter(int parameter, Instant argument, TemporalType temporalType) {
		return this.delegate.setParameter(parameter, argument, temporalType);
	}

	@Override
	public Query<R> setParameter(int parameter, Date argument, TemporalType temporalType) {
		return this.delegate.setParameter(parameter, argument, temporalType);
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
		return this.delegate.setParameter(parameter, argument, temporalType);
	}

	@Override
	public Query<R> setParameterList(String parameter, Collection arguments) {
		return this.delegate.setParameterList(parameter, arguments);
	}

	@Override
	public Query<R> setParameterList(String parameter, Object[] values) {
		return this.delegate.setParameterList(parameter, values);
	}

	@Override
	public Query<R> setParameterList(int parameter, Collection arguments) {
		return this.delegate.setParameterList(parameter, arguments);
	}

	@Override
	public Query<R> setParameterList(int parameter, Object[] arguments) {
		return this.delegate.setParameterList(parameter, arguments);
	}

	@Override
	public Query<R> setProperties(Object bean) {
		return this.delegate.setProperties(bean);
	}

	@Override
	public Query<R> setProperties(Map bean) {
		return this.delegate.setProperties(bean);
	}

	@Override
	public Query<R> setHibernateFlushMode(FlushMode flushMode) {
		return this.delegate.setHibernateFlushMode(flushMode);
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
		return this.delegate.setQueryFlushMode(queryFlushMode);
	}

	@Override
	public FlushModeType getFlushMode() {
		return this.delegate.getFlushMode();
	}

	@Override
	public Query<R> setCacheable(boolean cacheable) {
		return this.delegate.setCacheable(cacheable);
	}

	@Override
	public boolean isQueryPlanCacheable() {
		return this.delegate.isQueryPlanCacheable();
	}

	@Override
	public SelectionQuery<R> setQueryPlanCacheable(boolean queryPlanCacheable) {
		return this.delegate.setQueryPlanCacheable(queryPlanCacheable);
	}

	@Override
	public String getCacheRegion() {
		return this.delegate.getCacheRegion();
	}

	@Override
	public Query<R> setCacheRegion(String cacheRegion) {
		return this.delegate.setCacheRegion(cacheRegion);
	}

	@Override
	public Query<R> setCacheMode(CacheMode cacheMode) {
		return this.delegate.setCacheMode(cacheMode);
	}

	@Override
	public Query<R> setCacheStoreMode(CacheStoreMode cacheStoreMode) {
		return this.delegate.setCacheStoreMode(cacheStoreMode);
	}

	@Override
	public TypedQuery<R> setTimeout(Integer integer) {
		return this.delegate.setTimeout(integer);
	}

	@Override
	public Query<R> setCacheRetrieveMode(CacheRetrieveMode cacheRetrieveMode) {
		return this.delegate.setCacheRetrieveMode(cacheRetrieveMode);
	}

	@Override
	public boolean isCacheable() {
		return this.delegate.isCacheable();
	}

	@Override
	public Query<R> setTimeout(int timeout) {
		return this.delegate.setTimeout(timeout);
	}

	@Override
	public Query<R> setFetchSize(int fetchSize) {
		return this.delegate.setFetchSize(fetchSize);
	}

	@Override
	public boolean isReadOnly() {
		return this.delegate.isReadOnly();
	}

	@Override
	public Query<R> setReadOnly(boolean readOnly) {
		return this.delegate.setReadOnly(readOnly);
	}

	@Override
	public int getMaxResults() {
		return this.delegate.getMaxResults();
	}

	@Override
	public Query<R> setMaxResults(int maxResults) {
		return this.delegate.setMaxResults(maxResults);
	}

	@Override
	public int getFirstResult() {
		return this.delegate.getFirstResult();
	}

	@Override
	public Query<R> setFirstResult(int startPosition) {
		return this.delegate.setFirstResult(startPosition);
	}

	@Override
	public Query<R> setPage(Page page) {
		return this.delegate.setPage(page);
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
		return this.delegate.setHint(hintName, value);
	}

	@Override
	public Map<String, Object> getHints() {
		return this.delegate.getHints();
	}

	@Override
	public Query<R> setEntityGraph(EntityGraph<R> graph, GraphSemantic semantic) {
		return this.delegate.setEntityGraph(graph, semantic);
	}

	@Override
	public Query<R> enableFetchProfile(String profileName) {
		return this.delegate.enableFetchProfile(profileName);
	}

	@Override
	public Query<R> disableFetchProfile(String profileName) {
		return this.delegate.disableFetchProfile(profileName);
	}

	@Override
	public Query<R> setFlushMode(FlushModeType flushMode) {
		return this.delegate.setFlushMode(flushMode);
	}

	@Override
	public FlushMode getHibernateFlushMode() {
		return this.delegate.getHibernateFlushMode();
	}

	@Override
	public Query<R> setLockMode(LockModeType lockMode) {
		return this.delegate.setLockMode(lockMode);
	}

	@Override
	public <T> Query<T> setResultTransformer(ResultTransformer<T> transformer) {
		return this.delegate.setResultTransformer(transformer);
	}

	@Override
	public LockMode getHibernateLockMode() {
		return this.delegate.getHibernateLockMode();
	}

	@Override
	public SelectionQuery<R> setHibernateLockMode(LockMode lockMode) {
		return this.delegate.setHibernateLockMode(lockMode);
	}

	@Override
	public Query<R> setOrder(Order<? super R> order) {
		return this.delegate.setOrder(order);
	}

	@Override
	public Query<R> addRestriction(Restriction<? super R> restriction) {
		return this.delegate.addRestriction(restriction);
	}

	@Override
	public SelectionQuery<R> setFollowOnLocking(boolean enable) {
		return this.delegate.setFollowOnLocking(enable);
	}

	@Override
	public Query<R> setOrder(List<? extends Order<? super R>> orderList) {
		return this.delegate.setOrder(orderList);
	}

	@Override
	public <P> Query<R> setParameterList(QueryParameter<P> parameter, P[] arguments, BindableType<P> type) {
		return this.delegate.setParameterList(parameter, arguments, type);
	}

	@Override
	public <P> Query<R> setParameterList(QueryParameter<P> parameter, P[] arguments, Class<P> javaType) {
		return this.delegate.setParameterList(parameter, arguments, javaType);
	}

	@Override
	public <P> Query<R> setParameterList(QueryParameter<P> parameter, P[] arguments) {
		return this.delegate.setParameterList(parameter, arguments);
	}

	@Override
	public <P> Query<R> setParameterList(QueryParameter<P> parameter, Collection<? extends P> arguments,
			BindableType<P> type) {
		return this.delegate.setParameterList(parameter, arguments, type);
	}

	@Override
	public <P> Query<R> setParameterList(QueryParameter<P> parameter, Collection<? extends P> arguments,
			Class<P> javaType) {
		return this.delegate.setParameterList(parameter, arguments, javaType);
	}

	@Override
	public <P> Query<R> setParameterList(QueryParameter<P> parameter, Collection<? extends P> arguments) {
		return this.delegate.setParameterList(parameter, arguments);
	}

	@Override
	public <P> Query<R> setParameterList(int parameter, P[] arguments, BindableType<P> type) {
		return this.delegate.setParameterList(parameter, arguments, type);
	}

	@Override
	public <P> Query<R> setParameterList(int parameter, P[] arguments, Class<P> javaType) {
		return this.delegate.setParameterList(parameter, arguments, javaType);
	}

	@Override
	public <P> Query<R> setParameterList(int parameter, Collection<? extends P> arguments, BindableType<P> type) {
		return this.delegate.setParameterList(parameter, arguments, type);
	}

	@Override
	public <P> Query<R> setParameterList(int parameter, Collection<? extends P> arguments, Class<P> javaType) {
		return this.delegate.setParameterList(parameter, arguments, javaType);
	}

	@Override
	public <P> Query<R> setParameterList(String parameter, P[] arguments, BindableType<P> type) {
		return this.delegate.setParameterList(parameter, arguments, type);
	}

	@Override
	public <P> Query<R> setParameterList(String parameter, P[] arguments, Class<P> javaType) {
		return this.delegate.setParameterList(parameter, arguments, javaType);
	}

	@Override
	public <P> Query<R> setParameterList(String parameter, Collection<? extends P> arguments, BindableType<P> type) {
		return this.delegate.setParameterList(parameter, arguments, type);
	}

	@Override
	public <P> Query<R> setParameterList(String parameter, Collection<? extends P> arguments, Class<P> javaType) {
		return this.delegate.setParameterList(parameter, arguments, javaType);
	}

	@Override
	public Query<R> setParameter(Parameter<Date> parameter, Date argument, TemporalType temporalType) {
		return this.delegate.setParameter(parameter, argument, temporalType);
	}

	@Override
	public Query<R> setParameter(Parameter<Calendar> parameter, Calendar argument, TemporalType temporalType) {
		return this.delegate.setParameter(parameter, argument, temporalType);
	}

	@Override
	public <T> Query<R> setParameter(Parameter<T> parameter, T argument) {
		return this.delegate.setParameter(parameter, argument);
	}

	@Override
	public <P> Query<R> setParameter(QueryParameter<P> parameter, P argument, BindableType<P> type) {
		return this.delegate.setParameter(parameter, argument, type);
	}

	@Override
	public <P> Query<R> setParameter(QueryParameter<P> parameter, P argument, Class<P> type) {
		return this.delegate.setParameter(parameter, argument, type);
	}

	@Override
	public <T> Query<R> setParameter(QueryParameter<T> parameter, T argument) {
		return this.delegate.setParameter(parameter, argument);
	}

	@Override
	public <P> Query<R> setParameter(int parameter, P argument, BindableType<P> type) {
		return this.delegate.setParameter(parameter, argument, type);
	}

	@Override
	public <P> Query<R> setParameter(int parameter, P argument, Class<P> type) {
		return this.delegate.setParameter(parameter, argument, type);
	}

	@Override
	public <P> Query<R> setParameter(String parameter, P argument, BindableType<P> type) {
		return this.delegate.setParameter(parameter, argument, type);
	}

	@Override
	public <P> Query<R> setParameter(String parameter, P argument, Class<P> type) {
		return this.delegate.setParameter(parameter, argument, type);
	}

	@Override
	public <T> Query<T> setTupleTransformer(TupleTransformer<T> transformer) {
		return this.delegate.setTupleTransformer(transformer);
	}
}