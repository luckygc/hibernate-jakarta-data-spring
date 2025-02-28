package github.gc.hibernate.proxy.impl;

import github.gc.hibernate.proxy.QueryProxySupport;
import jakarta.persistence.*;
import jakarta.persistence.metamodel.SingularAttribute;
import org.hibernate.*;
import org.hibernate.graph.GraphSemantic;
import org.hibernate.graph.RootGraph;
import org.hibernate.metamodel.model.domain.BasicDomainType;
import org.hibernate.query.Query;
import org.hibernate.query.*;
import org.hibernate.query.restriction.Restriction;
import org.hibernate.query.spi.QueryOptions;
import org.hibernate.transform.ResultTransformer;
import org.hibernate.type.BasicTypeReference;
import org.jspecify.annotations.NonNull;
import org.springframework.util.Assert;

import java.time.Instant;
import java.util.*;
import java.util.stream.Stream;

public class NativeQueryProxy<T> extends QueryProxySupport implements NativeQuery<T> {

	private final NativeQuery<T> delegate;

	public NativeQueryProxy(@NonNull NativeQuery<T> delegate, @NonNull StatelessSession session) {
		super(session);

		Assert.notNull(delegate, "delegate must not be null");
		this.delegate = delegate;
	}

	@Override
	public NativeQuery<T> addScalar(String columnAlias) {
		this.delegate.addScalar(columnAlias);
		return this;
	}

	@Override
	public NativeQuery<T> addScalar(String columnAlias, BasicTypeReference type) {
		this.delegate.addScalar(columnAlias, type);
		return this;
	}

	@Override
	public NativeQuery<T> addScalar(String columnAlias, BasicDomainType type) {
		this.delegate.addScalar(columnAlias, type);
		return this;
	}

	@Override
	public NativeQuery<T> addScalar(String columnAlias, Class javaType) {
		this.delegate.addScalar(columnAlias, javaType);
		return this;
	}

	@Override
	public <C> NativeQuery<T> addScalar(String columnAlias, Class<C> relationalJavaType,
			AttributeConverter<?, C> converter) {
		this.delegate.addScalar(columnAlias, relationalJavaType, converter);
		return this;
	}

	@Override
	public <O, R> NativeQuery<T> addScalar(String columnAlias, Class<O> domainJavaType, Class<R> jdbcJavaType,
			AttributeConverter<O, R> converter) {
		this.delegate.addScalar(columnAlias, domainJavaType, jdbcJavaType, converter);
		return this;
	}

	@Override
	public <C> NativeQuery<T> addScalar(String columnAlias, Class<C> relationalJavaType,
			Class<? extends AttributeConverter<?, C>> converter) {
		this.delegate.addScalar(columnAlias, relationalJavaType, converter);
		return this;
	}

	@Override
	public <O, R> NativeQuery<T> addScalar(String columnAlias, Class<O> domainJavaType, Class<R> jdbcJavaType,
			Class<? extends AttributeConverter<O, R>> converter) {
		this.delegate.addScalar(columnAlias, domainJavaType, jdbcJavaType, converter);
		return this;
	}

	@Override
	public <J> InstantiationResultNode<J> addInstantiation(Class<J> targetJavaType) {
		return this.delegate.addInstantiation(targetJavaType);
	}

	@Override
	public NativeQuery<T> addAttributeResult(String columnAlias, Class entityJavaType, String attributePath) {
		this.delegate.addAttributeResult(columnAlias, entityJavaType, attributePath);
		return this;
	}

	@Override
	public NativeQuery<T> addAttributeResult(String columnAlias, String entityName, String attributePath) {
		this.delegate.addAttributeResult(columnAlias, entityName, attributePath);
		return this;
	}

	@Override
	public NativeQuery<T> addAttributeResult(String columnAlias, SingularAttribute attribute) {
		this.delegate.addAttributeResult(columnAlias, attribute);
		return this;
	}

	@Override
	public RootReturn addRoot(String tableAlias, String entityName) {
		return this.delegate.addRoot(tableAlias, entityName);
	}

	@Override
	public RootReturn addRoot(String tableAlias, Class entityType) {
		return this.delegate.addRoot(tableAlias, entityType);
	}

	@Override
	public NativeQuery<T> addEntity(String entityName) {
		this.delegate.addEntity(entityName);
		return this;
	}

	@Override
	public NativeQuery<T> addEntity(String tableAlias, String entityName) {
		this.delegate.addEntity(tableAlias, entityName);
		return this;
	}

	@Override
	public NativeQuery<T> addEntity(String tableAlias, String entityName, LockMode lockMode) {
		this.delegate.addEntity(tableAlias, entityName, lockMode);
		return this;
	}

	@Override
	public NativeQuery<T> addEntity(Class entityType) {
		this.delegate.addEntity(entityType);
		return this;
	}

	@Override
	public NativeQuery<T> addEntity(String tableAlias, Class entityType) {
		this.delegate.addEntity(tableAlias, entityType);
		return this;
	}

	@Override
	public NativeQuery<T> addEntity(String tableAlias, Class entityClass, LockMode lockMode) {
		this.delegate.addEntity(tableAlias, entityClass, lockMode);
		return this;
	}

	@Override
	public FetchReturn addFetch(String tableAlias, String ownerTableAlias, String joinPropertyName) {
		return this.delegate.addFetch(tableAlias, ownerTableAlias, joinPropertyName);
	}

	@Override
	public NativeQuery<T> addJoin(String tableAlias, String path) {
		this.delegate.addJoin(tableAlias, path);
		return this;
	}

	@Override
	public NativeQuery<T> addJoin(String tableAlias, String ownerTableAlias, String joinPropertyName) {
		this.delegate.addJoin(tableAlias, ownerTableAlias, joinPropertyName);
		return this;
	}

	@Override
	public NativeQuery<T> addJoin(String tableAlias, String path, LockMode lockMode) {
		this.delegate.addJoin(tableAlias, path, lockMode);
		return this;
	}

	@Override
	public Collection<String> getSynchronizedQuerySpaces() {
		return this.delegate.getSynchronizedQuerySpaces();
	}

	@Override
	public NativeQuery<T> addSynchronizedQuerySpace(String querySpace) {
		this.delegate.addSynchronizedQuerySpace(querySpace);
		return this;
	}

	@Override
	public NativeQuery<T> addSynchronizedEntityName(String entityName) throws MappingException {
		this.delegate.addSynchronizedEntityName(entityName);
		return this;
	}

	@Override
	public NativeQuery<T> addSynchronizedEntityClass(Class entityClass) throws MappingException {
		this.delegate.addSynchronizedEntityClass(entityClass);
		return this;
	}

	@Override
	public NativeQuery<T> setHibernateFlushMode(FlushMode flushMode) {
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
	public NativeQuery<T> setQueryFlushMode(QueryFlushMode queryFlushMode) {
		this.delegate.setQueryFlushMode(queryFlushMode);
		return this;
	}

	@Override
	public FlushModeType getFlushMode() {
		throw new UnsupportedOperationException("Deprecated");
	}

	@Override
	public NativeQuery<T> setFlushMode(FlushModeType flushMode) {
		throw new UnsupportedOperationException("Deprecated");
	}

	@Override
	public FlushMode getHibernateFlushMode() {
		throw new UnsupportedOperationException("Deprecated");
	}

	@Override
	public NativeQuery<T> setCacheMode(CacheMode cacheMode) {
		this.delegate.setCacheMode(cacheMode);
		return this;
	}

	@Override
	public NativeQuery<T> setCacheStoreMode(CacheStoreMode cacheStoreMode) {
		this.delegate.setCacheStoreMode(cacheStoreMode);
		return this;
	}

	@Override
	public TypedQuery<T> setTimeout(Integer integer) {
		this.delegate.setTimeout(integer);
		return this;
	}

	@Override
	public <R> R unwrap(Class<R> aClass) {
		return this.delegate.unwrap(aClass);
	}

	@Override
	public NativeQuery<T> setCacheRetrieveMode(CacheRetrieveMode cacheRetrieveMode) {
		this.delegate.setCacheRetrieveMode(cacheRetrieveMode);
		return this;
	}

	@Override
	public boolean isCacheable() {
		return this.delegate.isCacheable();
	}

	@Override
	public NativeQuery<T> setCacheable(boolean cacheable) {
		this.delegate.setCacheable(cacheable);
		return this;
	}

	@Override
	public boolean isQueryPlanCacheable() {
		return this.delegate.isQueryPlanCacheable();
	}

	@Override
	public SelectionQuery<T> setQueryPlanCacheable(boolean queryPlanCacheable) {
		this.delegate.setQueryPlanCacheable(queryPlanCacheable);
		return this;
	}

	@Override
	public String getCacheRegion() {
		return this.delegate.getCacheRegion();
	}

	@Override
	public NativeQuery<T> setCacheRegion(String cacheRegion) {
		this.delegate.setCacheRegion(cacheRegion);
		return this;
	}

	@Override
	public NativeQuery<T> setTimeout(int timeout) {
		this.delegate.setTimeout(timeout);
		return this;
	}

	@Override
	public NativeQuery<T> setFetchSize(int fetchSize) {
		this.delegate.setFetchSize(fetchSize);
		return this;
	}

	@Override
	public boolean isReadOnly() {
		return this.delegate.isReadOnly();
	}

	@Override
	public NativeQuery<T> setReadOnly(boolean readOnly) {
		this.delegate.setReadOnly(readOnly);
		return this;
	}

	@Override
	public int getMaxResults() {
		return this.delegate.getMaxResults();
	}

	@Override
	public LockOptions getLockOptions() {
		return this.delegate.getLockOptions();
	}

	@Override
	public NativeQuery<T> setLockOptions(LockOptions lockOptions) {
		this.delegate.setLockOptions(lockOptions);
		return this;
	}

	@Override
	public NativeQuery<T> setLockMode(String alias, LockMode lockMode) {
		this.delegate.setLockMode(alias, lockMode);
		return this;
	}

	@Override
	public List<T> list() {
		return execute(this.delegate::list);
	}

	@Override
	public List<T> getResultList() {
		return execute(this.delegate::getResultList);
	}

	@Override
	public ScrollableResults<T> scroll() {
		return execute(this.delegate::scroll);
	}

	@Override
	public ScrollableResults<T> scroll(ScrollMode scrollMode) {
		return execute(() -> this.delegate.scroll(scrollMode));
	}

	@Override
	public Stream<T> getResultStream() {
		return execute(this.delegate::getResultStream);
	}

	@Override
	public Stream<T> stream() {
		return execute(this.delegate::stream);
	}

	@Override
	public T uniqueResult() {
		return execute(this.delegate::uniqueResult);
	}

	@Override
	public T getSingleResult() {
		return execute(this.delegate::getSingleResult);
	}

	@Override
	public T getSingleResultOrNull() {
		return execute(this.delegate::getSingleResultOrNull);
	}

	@Override
	public Optional<T> uniqueResultOptional() {
		return execute(this.delegate::uniqueResultOptional);
	}

	@Override
	public long getResultCount() {
		return execute(this.delegate::getResultCount);
	}

	@Override
	public KeyedResultList<T> getKeyedResultList(KeyedPage<T> page) {
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
	public Query<T> applyGraph(RootGraph graph, GraphSemantic semantic) {
		throw new UnsupportedOperationException("Deprecated");
	}

	@Override
	public Query<T> applyFetchGraph(RootGraph graph) {
		throw new UnsupportedOperationException("Deprecated");
	}

	@Override
	public Query<T> applyLoadGraph(RootGraph graph) {
		throw new UnsupportedOperationException("Deprecated");
	}

	@Override
	public String getComment() {
		return this.delegate.getComment();
	}

	@Override
	public NativeQuery<T> setComment(String comment) {
		this.delegate.setComment(comment);
		return this;
	}

	@Override
	public Integer getFetchSize() {
		return this.delegate.getFetchSize();
	}

	@Override
	public NativeQuery<T> addQueryHint(String hint) {
		this.delegate.addQueryHint(hint);
		return this;
	}

	@Override
	public NativeQuery<T> setMaxResults(int maxResults) {
		this.delegate.setMaxResults(maxResults);
		return this;
	}

	@Override
	public int getFirstResult() {
		return this.delegate.getFirstResult();
	}

	@Override
	public NativeQuery<T> setFirstResult(int startPosition) {
		this.delegate.setFirstResult(startPosition);
		return this;
	}

	@Override
	public Query<T> setPage(Page page) {
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
	public NativeQuery<T> setHint(String hintName, Object value) {
		this.delegate.setHint(hintName, value);
		return this;
	}

	@Override
	public Map<String, Object> getHints() {
		return this.delegate.getHints();
	}

	@Override
	public Query<T> setEntityGraph(EntityGraph<T> graph, GraphSemantic semantic) {
		this.delegate.setEntityGraph(graph, semantic);
		return this;
	}

	@Override
	public Query<T> enableFetchProfile(String profileName) {
		this.delegate.enableFetchProfile(profileName);
		return this;
	}

	@Override
	public Query<T> disableFetchProfile(String profileName) {
		this.delegate.disableFetchProfile(profileName);
		return this;
	}

	@Override
	public LockModeType getLockMode() {
		return this.delegate.getLockMode();
	}

	@Override
	public LockMode getHibernateLockMode() {
		return this.delegate.getHibernateLockMode();
	}

	@Override
	public NativeQuery<T> setLockMode(LockModeType lockMode) {
		this.delegate.setLockMode(lockMode);
		return this;
	}

	@Override
	public Query<T> setOrder(List<? extends Order<? super T>> orderList) {
		this.delegate.setOrder(orderList);
		return this;
	}

	@Override
	public Query<T> setOrder(Order<? super T> order) {
		this.delegate.setOrder(order);
		return this;
	}

	@Override
	public Query<T> addRestriction(Restriction<? super T> restriction) {
		this.delegate.addRestriction(restriction);
		return this;
	}

	@Override
	public SelectionQuery<T> setFollowOnLocking(boolean enable) {
		this.delegate.setFollowOnLocking(enable);
		return this;
	}

	@Override
	public NativeQuery<T> setHibernateLockMode(LockMode lockMode) {
		this.delegate.setHibernateLockMode(lockMode);
		return this;
	}

	@Override
	public <R> NativeQuery<R> setTupleTransformer(TupleTransformer<R> transformer) {
		this.delegate.setTupleTransformer(transformer);
		return (NativeQuery<R>) this;
	}

	@Override
	public NativeQuery<T> setResultListTransformer(ResultListTransformer<T> transformer) {
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
	public <S> NativeQuery<S> setResultTransformer(ResultTransformer<S> transformer) {
		throw new UnsupportedOperationException("Deprecated");
	}

	@Override
	public NativeQuery<T> setParameter(String name, Object value) {
		this.delegate.setParameter(name, value);
		return this;
	}

	@Override
	public <P> NativeQuery<T> setParameter(String name, P val, Class<P> type) {
		this.delegate.setParameter(name, val, type);
		return this;
	}

	@Override
	public <P> NativeQuery<T> setParameter(String name, P val, BindableType<P> type) {
		this.delegate.setParameter(name, val, type);
		return this;
	}

	@Override
	public NativeQuery<T> setParameter(String name, Instant value, TemporalType temporalType) {
		this.delegate.setParameter(name, value, temporalType);
		return this;
	}

	@Override
	public NativeQuery<T> setParameter(String name, Calendar value, TemporalType temporalType) {
		this.delegate.setParameter(name, value, temporalType);
		return this;
	}

	@Override
	public NativeQuery<T> setParameter(String name, Date value, TemporalType temporalType) {
		this.delegate.setParameter(name, value, temporalType);
		return this;
	}

	@Override
	public NativeQuery<T> setParameter(int position, Object value) {
		this.delegate.setParameter(position, value);
		return this;
	}

	@Override
	public <P> NativeQuery<T> setParameter(int position, P val, Class<P> type) {
		this.delegate.setParameter(position, val, type);
		return this;
	}

	@Override
	public <P> NativeQuery<T> setParameter(int position, P val, BindableType<P> type) {
		this.delegate.setParameter(position, val, type);
		return this;
	}

	@Override
	public NativeQuery<T> setParameter(int position, Instant value, TemporalType temporalType) {
		this.delegate.setParameter(position, value, temporalType);
		return this;
	}

	@Override
	public NativeQuery<T> setParameter(int position, Calendar value, TemporalType temporalType) {
		this.delegate.setParameter(position, value, temporalType);
		return this;
	}

	@Override
	public NativeQuery<T> setParameter(int position, Date value, TemporalType temporalType) {
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
	public <P> Parameter<P> getParameter(String s, Class<P> aClass) {
		return this.delegate.getParameter(s, aClass);
	}

	@Override
	public Parameter<?> getParameter(int i) {
		return this.delegate.getParameter(i);
	}

	@Override
	public <P> Parameter<P> getParameter(int i, Class<P> aClass) {
		return this.delegate.getParameter(i, aClass);
	}

	@Override
	public boolean isBound(Parameter<?> parameter) {
		return this.delegate.isBound(parameter);
	}

	@Override
	public <P> P getParameterValue(Parameter<P> parameter) {
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
	public <P> NativeQuery<T> setParameter(QueryParameter<P> parameter, P val) {
		this.delegate.setParameter(parameter, val);
		return this;
	}

	@Override
	public <P> NativeQuery<T> setParameter(QueryParameter<P> parameter, P val, Class<P> type) {
		this.delegate.setParameter(parameter, val, type);
		return this;
	}

	@Override
	public <P> NativeQuery<T> setParameter(QueryParameter<P> parameter, P val, BindableType<P> type) {
		this.delegate.setParameter(parameter, val, type);
		return this;
	}

	@Override
	public <P> NativeQuery<T> setParameter(Parameter<P> param, P value) {
		this.delegate.setParameter(param, value);
		return this;
	}

	@Override
	public NativeQuery<T> setParameter(Parameter<Calendar> param, Calendar value, TemporalType temporalType) {
		this.delegate.setParameter(param, value, temporalType);
		return this;
	}

	@Override
	public NativeQuery<T> setParameter(Parameter<Date> param, Date value, TemporalType temporalType) {
		this.delegate.setParameter(param, value, temporalType);
		return this;
	}

	@Override
	public NativeQuery<T> setParameterList(String name, Collection values) {
		this.delegate.setParameterList(name, values);
		return this;
	}

	@Override
	public <P> NativeQuery<T> setParameterList(String name, Collection<? extends P> values, Class<P> type) {
		this.delegate.setParameterList(name, values, type);
		return this;
	}

	@Override
	public <P> NativeQuery<T> setParameterList(String name, Collection<? extends P> values, BindableType<P> type) {
		this.delegate.setParameterList(name, values, type);
		return this;
	}

	@Override
	public NativeQuery<T> setParameterList(String name, Object[] values) {
		this.delegate.setParameterList(name, values);
		return this;
	}

	@Override
	public <P> NativeQuery<T> setParameterList(String name, P[] values, Class<P> type) {
		this.delegate.setParameterList(name, values, type);
		return this;
	}

	@Override
	public <P> NativeQuery<T> setParameterList(String name, P[] values, BindableType<P> type) {
		this.delegate.setParameterList(name, values, type);
		return this;
	}

	@Override
	public NativeQuery<T> setParameterList(int position, Collection values) {
		this.delegate.setParameterList(position, values);
		return this;
	}

	@Override
	public <P> NativeQuery<T> setParameterList(int position, Collection<? extends P> values, Class<P> type) {
		this.delegate.setParameterList(position, values, type);
		return this;
	}

	@Override
	public <P> NativeQuery<T> setParameterList(int position, Collection<? extends P> values, BindableType<P> javaType) {
		this.delegate.setParameterList(position, values, javaType);
		return this;
	}

	@Override
	public NativeQuery<T> setParameterList(int position, Object[] values) {
		this.delegate.setParameterList(position, values);
		return this;
	}

	@Override
	public <P> NativeQuery<T> setParameterList(int position, P[] values, Class<P> javaType) {
		this.delegate.setParameterList(position, values, javaType);
		return this;
	}

	@Override
	public <P> NativeQuery<T> setParameterList(int position, P[] values, BindableType<P> javaType) {
		this.delegate.setParameterList(position, values, javaType);
		return this;
	}

	@Override
	public <P> NativeQuery<T> setParameterList(QueryParameter<P> parameter, Collection<? extends P> values) {
		this.delegate.setParameterList(parameter, values);
		return this;
	}

	@Override
	public <P> NativeQuery<T> setParameterList(QueryParameter<P> parameter, Collection<? extends P> values,
			Class<P> javaType) {
		this.delegate.setParameterList(parameter, values, javaType);
		return this;
	}

	@Override
	public <P> NativeQuery<T> setParameterList(QueryParameter<P> parameter, Collection<? extends P> values,
			BindableType<P> type) {
		this.delegate.setParameterList(parameter, values, type);
		return this;
	}

	@Override
	public <P> NativeQuery<T> setParameterList(QueryParameter<P> parameter, P[] values) {
		this.delegate.setParameterList(parameter, values);
		return this;
	}

	@Override
	public <P> NativeQuery<T> setParameterList(QueryParameter<P> parameter, P[] values, Class<P> javaType) {
		this.delegate.setParameterList(parameter, values, javaType);
		return this;
	}

	@Override
	public <P> NativeQuery<T> setParameterList(QueryParameter<P> parameter, P[] values, BindableType<P> type) {
		this.delegate.setParameterList(parameter, values, type);
		return this;
	}

	@Override
	public NativeQuery<T> setProperties(Object bean) {
		this.delegate.setProperties(bean);
		return this;
	}

	@Override
	public NativeQuery<T> setProperties(Map bean) {
		this.delegate.setProperties(bean);
		return this;
	}
}