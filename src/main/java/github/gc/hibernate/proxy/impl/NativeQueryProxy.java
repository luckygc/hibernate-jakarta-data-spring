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

@SuppressWarnings("deprecation")
public class NativeQueryProxy<T> extends QueryProxySupport implements NativeQuery<T> {

	private final NativeQuery<T> delegate;

	public NativeQueryProxy(@NonNull NativeQuery<T> delegate, @NonNull StatelessSession session) {
		super(session);

		Assert.notNull(delegate, "delegate must not be null");
		this.delegate = delegate;
	}

	@Override
	public NativeQuery<T> addScalar(String columnAlias) {
		return this.delegate.addScalar(columnAlias);
	}

	@Override
	public NativeQuery<T> addScalar(String columnAlias, BasicTypeReference type) {
		return this.delegate.addScalar(columnAlias, type);
	}

	@Override
	public NativeQuery<T> addScalar(String columnAlias, BasicDomainType type) {
		return this.delegate.addScalar(columnAlias, type);
	}

	@Override
	public NativeQuery<T> addScalar(String columnAlias, Class javaType) {
		return this.delegate.addScalar(columnAlias, javaType);
	}

	@Override
	public <C> NativeQuery<T> addScalar(String columnAlias, Class<C> relationalJavaType,
			AttributeConverter<?, C> converter) {
		return this.delegate.addScalar(columnAlias, relationalJavaType, converter);
	}

	@Override
	public <O, R> NativeQuery<T> addScalar(String columnAlias, Class<O> domainJavaType, Class<R> jdbcJavaType,
			AttributeConverter<O, R> converter) {
		return this.delegate.addScalar(columnAlias, domainJavaType, jdbcJavaType, converter);
	}

	@Override
	public <C> NativeQuery<T> addScalar(String columnAlias, Class<C> relationalJavaType,
			Class<? extends AttributeConverter<?, C>> converter) {
		return this.delegate.addScalar(columnAlias, relationalJavaType, converter);
	}

	@Override
	public <O, R> NativeQuery<T> addScalar(String columnAlias, Class<O> domainJavaType, Class<R> jdbcJavaType,
			Class<? extends AttributeConverter<O, R>> converter) {
		return this.delegate.addScalar(columnAlias, domainJavaType, jdbcJavaType, converter);
	}

	@Override
	public <J> InstantiationResultNode<J> addInstantiation(Class<J> targetJavaType) {
		return this.delegate.addInstantiation(targetJavaType);
	}

	@Override
	public NativeQuery<T> addAttributeResult(String columnAlias, Class entityJavaType, String attributePath) {
		return this.delegate.addAttributeResult(columnAlias, entityJavaType, attributePath);
	}

	@Override
	public NativeQuery<T> addAttributeResult(String columnAlias, String entityName, String attributePath) {
		return this.delegate.addAttributeResult(columnAlias, entityName, attributePath);
	}

	@Override
	public NativeQuery<T> addAttributeResult(String columnAlias, SingularAttribute attribute) {
		return this.delegate.addAttributeResult(columnAlias, attribute);
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
		return this.delegate.addEntity(entityName);
	}

	@Override
	public NativeQuery<T> addEntity(String tableAlias, String entityName) {
		return this.delegate.addEntity(tableAlias, entityName);
	}

	@Override
	public NativeQuery<T> addEntity(String tableAlias, String entityName, LockMode lockMode) {
		return this.delegate.addEntity(tableAlias, entityName, lockMode);
	}

	@Override
	public NativeQuery<T> addEntity(Class entityType) {
		return this.delegate.addEntity(entityType);
	}

	@Override
	public NativeQuery<T> addEntity(String tableAlias, Class entityType) {
		return this.delegate.addEntity(tableAlias, entityType);
	}

	@Override
	public NativeQuery<T> addEntity(String tableAlias, Class entityClass, LockMode lockMode) {
		return this.delegate.addEntity(tableAlias, entityClass, lockMode);
	}

	@Override
	public FetchReturn addFetch(String tableAlias, String ownerTableAlias, String joinPropertyName) {
		return this.delegate.addFetch(tableAlias, ownerTableAlias, joinPropertyName);
	}

	@Override
	public NativeQuery<T> addJoin(String tableAlias, String path) {
		return this.delegate.addJoin(tableAlias, path);
	}

	@Override
	public NativeQuery<T> addJoin(String tableAlias, String ownerTableAlias, String joinPropertyName) {
		return this.delegate.addJoin(tableAlias, ownerTableAlias, joinPropertyName);
	}

	@Override
	public NativeQuery<T> addJoin(String tableAlias, String path, LockMode lockMode) {
		return this.delegate.addJoin(tableAlias, path, lockMode);
	}

	@Override
	public Collection<String> getSynchronizedQuerySpaces() {
		return this.delegate.getSynchronizedQuerySpaces();
	}

	@Override
	public NativeQuery<T> addSynchronizedQuerySpace(String querySpace) {
		return this.delegate.addSynchronizedQuerySpace(querySpace);
	}

	@Override
	public NativeQuery<T> addSynchronizedEntityName(String entityName) throws MappingException {
		return this.delegate.addSynchronizedEntityName(entityName);
	}

	@Override
	public NativeQuery<T> addSynchronizedEntityClass(Class entityClass) throws MappingException {
		return this.delegate.addSynchronizedEntityClass(entityClass);
	}

	@Override
	public NativeQuery<T> setHibernateFlushMode(FlushMode flushMode) {
		return this.delegate.setHibernateFlushMode(flushMode);
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
		return this.delegate.setQueryFlushMode(queryFlushMode);
	}

	@Override
	public FlushModeType getFlushMode() {
		return this.delegate.getFlushMode();
	}

	@Override
	public NativeQuery<T> setFlushMode(FlushModeType flushMode) {
		return this.delegate.setFlushMode(flushMode);
	}

	@Override
	public FlushMode getHibernateFlushMode() {
		return this.delegate.getHibernateFlushMode();
	}

	@Override
	public NativeQuery<T> setCacheMode(CacheMode cacheMode) {
		return this.delegate.setCacheMode(cacheMode);
	}

	@Override
	public NativeQuery<T> setCacheStoreMode(CacheStoreMode cacheStoreMode) {
		return this.delegate.setCacheStoreMode(cacheStoreMode);
	}

	@Override
	public TypedQuery<T> setTimeout(Integer integer) {
		return this.delegate.setTimeout(integer);
	}

	@Override
	public <R> R unwrap(Class<R> aClass) {
		return this.delegate.unwrap(aClass);
	}

	@Override
	public NativeQuery<T> setCacheRetrieveMode(CacheRetrieveMode cacheRetrieveMode) {
		return this.delegate.setCacheRetrieveMode(cacheRetrieveMode);
	}

	@Override
	public boolean isCacheable() {
		return this.delegate.isCacheable();
	}

	@Override
	public NativeQuery<T> setCacheable(boolean cacheable) {
		return this.delegate.setCacheable(cacheable);
	}

	@Override
	public boolean isQueryPlanCacheable() {
		return this.delegate.isQueryPlanCacheable();
	}

	@Override
	public SelectionQuery<T> setQueryPlanCacheable(boolean queryPlanCacheable) {
		return this.delegate.setQueryPlanCacheable(queryPlanCacheable);
	}

	@Override
	public String getCacheRegion() {
		return this.delegate.getCacheRegion();
	}

	@Override
	public NativeQuery<T> setCacheRegion(String cacheRegion) {
		return this.delegate.setCacheRegion(cacheRegion);
	}

	@Override
	public NativeQuery<T> setTimeout(int timeout) {
		return this.delegate.setTimeout(timeout);
	}

	@Override
	public NativeQuery<T> setFetchSize(int fetchSize) {
		return this.delegate.setFetchSize(fetchSize);
	}

	@Override
	public boolean isReadOnly() {
		return this.delegate.isReadOnly();
	}

	@Override
	public NativeQuery<T> setReadOnly(boolean readOnly) {
		return this.delegate.setReadOnly(readOnly);
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
		return this.delegate.setLockOptions(lockOptions);
	}

	@Override
	public NativeQuery<T> setLockMode(String alias, LockMode lockMode) {
		return this.delegate.setLockMode(alias, lockMode);
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
		return this.delegate.applyGraph(graph, semantic);
	}

	@Override
	public Query<T> applyFetchGraph(RootGraph graph) {
		return this.delegate.applyFetchGraph(graph);
	}

	@Override
	public Query<T> applyLoadGraph(RootGraph graph) {
		return this.delegate.applyLoadGraph(graph);
	}

	@Override
	public String getComment() {
		return this.delegate.getComment();
	}

	@Override
	public NativeQuery<T> setComment(String comment) {
		return this.delegate.setComment(comment);
	}

	@Override
	public Integer getFetchSize() {
		return this.delegate.getFetchSize();
	}

	@Override
	public NativeQuery<T> addQueryHint(String hint) {
		return this.delegate.addQueryHint(hint);
	}

	@Override
	public NativeQuery<T> setMaxResults(int maxResults) {
		return this.delegate.setMaxResults(maxResults);
	}

	@Override
	public int getFirstResult() {
		return this.delegate.getFirstResult();
	}

	@Override
	public NativeQuery<T> setFirstResult(int startPosition) {
		return this.delegate.setFirstResult(startPosition);
	}

	@Override
	public Query<T> setPage(Page page) {
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
	public NativeQuery<T> setHint(String hintName, Object value) {
		return this.delegate.setHint(hintName, value);
	}

	@Override
	public Map<String, Object> getHints() {
		return this.delegate.getHints();
	}

	@Override
	public Query<T> setEntityGraph(EntityGraph<T> graph, GraphSemantic semantic) {
		return this.delegate.setEntityGraph(graph, semantic);
	}

	@Override
	public Query<T> enableFetchProfile(String profileName) {
		return this.delegate.enableFetchProfile(profileName);
	}

	@Override
	public Query<T> disableFetchProfile(String profileName) {
		return this.delegate.disableFetchProfile(profileName);
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
		return this.delegate.setLockMode(lockMode);
	}

	@Override
	public Query<T> setOrder(List<? extends Order<? super T>> orderList) {
		return this.delegate.setOrder(orderList);
	}

	@Override
	public Query<T> setOrder(Order<? super T> order) {
		return this.delegate.setOrder(order);
	}

	@Override
	public Query<T> addRestriction(Restriction<? super T> restriction) {
		return this.delegate.addRestriction(restriction);
	}

	@Override
	public SelectionQuery<T> setFollowOnLocking(boolean enable) {
		return this.delegate.setFollowOnLocking(enable);
	}

	@Override
	public NativeQuery<T> setHibernateLockMode(LockMode lockMode) {
		return this.delegate.setHibernateLockMode(lockMode);
	}

	@Override
	public <R> NativeQuery<R> setTupleTransformer(TupleTransformer<R> transformer) {
		return this.delegate.setTupleTransformer(transformer);
	}

	@Override
	public NativeQuery<T> setResultListTransformer(ResultListTransformer<T> transformer) {
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
	public <S> NativeQuery<S> setResultTransformer(ResultTransformer<S> transformer) {
		return this.delegate.setResultTransformer(transformer);
	}

	@Override
	public NativeQuery<T> setParameter(String name, Object value) {
		return this.delegate.setParameter(name, value);
	}

	@Override
	public <P> NativeQuery<T> setParameter(String name, P val, Class<P> type) {
		return this.delegate.setParameter(name, val, type);
	}

	@Override
	public <P> NativeQuery<T> setParameter(String name, P val, BindableType<P> type) {
		return this.delegate.setParameter(name, val, type);
	}

	@Override
	public NativeQuery<T> setParameter(String name, Instant value, TemporalType temporalType) {
		return this.delegate.setParameter(name, value, temporalType);
	}

	@Override
	public NativeQuery<T> setParameter(String name, Calendar value, TemporalType temporalType) {
		return this.delegate.setParameter(name, value, temporalType);
	}

	@Override
	public NativeQuery<T> setParameter(String name, Date value, TemporalType temporalType) {
		return this.delegate.setParameter(name, value, temporalType);
	}

	@Override
	public NativeQuery<T> setParameter(int position, Object value) {
		return this.delegate.setParameter(position, value);
	}

	@Override
	public <P> NativeQuery<T> setParameter(int position, P val, Class<P> type) {
		return this.delegate.setParameter(position, val, type);
	}

	@Override
	public <P> NativeQuery<T> setParameter(int position, P val, BindableType<P> type) {
		return this.delegate.setParameter(position, val, type);
	}

	@Override
	public NativeQuery<T> setParameter(int position, Instant value, TemporalType temporalType) {
		return this.delegate.setParameter(position, value, temporalType);
	}

	@Override
	public NativeQuery<T> setParameter(int position, Calendar value, TemporalType temporalType) {
		return this.delegate.setParameter(position, value, temporalType);
	}

	@Override
	public NativeQuery<T> setParameter(int position, Date value, TemporalType temporalType) {
		return this.delegate.setParameter(position, value, temporalType);
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
		return this.delegate.setParameter(parameter, val);
	}

	@Override
	public <P> NativeQuery<T> setParameter(QueryParameter<P> parameter, P val, Class<P> type) {
		return this.delegate.setParameter(parameter, val, type);
	}

	@Override
	public <P> NativeQuery<T> setParameter(QueryParameter<P> parameter, P val, BindableType<P> type) {
		return this.delegate.setParameter(parameter, val, type);
	}

	@Override
	public <P> NativeQuery<T> setParameter(Parameter<P> param, P value) {
		return this.delegate.setParameter(param, value);
	}

	@Override
	public NativeQuery<T> setParameter(Parameter<Calendar> param, Calendar value, TemporalType temporalType) {
		return this.delegate.setParameter(param, value, temporalType);
	}

	@Override
	public NativeQuery<T> setParameter(Parameter<Date> param, Date value, TemporalType temporalType) {
		return this.delegate.setParameter(param, value, temporalType);
	}

	@Override
	public NativeQuery<T> setParameterList(String name, Collection values) {
		return this.delegate.setParameterList(name, values);
	}

	@Override
	public <P> NativeQuery<T> setParameterList(String name, Collection<? extends P> values, Class<P> type) {
		return this.delegate.setParameterList(name, values, type);
	}

	@Override
	public <P> NativeQuery<T> setParameterList(String name, Collection<? extends P> values, BindableType<P> type) {
		return this.delegate.setParameterList(name, values, type);
	}

	@Override
	public NativeQuery<T> setParameterList(String name, Object[] values) {
		return this.delegate.setParameterList(name, values);
	}

	@Override
	public <P> NativeQuery<T> setParameterList(String name, P[] values, Class<P> type) {
		return this.delegate.setParameterList(name, values, type);
	}

	@Override
	public <P> NativeQuery<T> setParameterList(String name, P[] values, BindableType<P> type) {
		return this.delegate.setParameterList(name, values, type);
	}

	@Override
	public NativeQuery<T> setParameterList(int position, Collection values) {
		return this.delegate.setParameterList(position, values);
	}

	@Override
	public <P> NativeQuery<T> setParameterList(int position, Collection<? extends P> values, Class<P> type) {
		return this.delegate.setParameterList(position, values, type);
	}

	@Override
	public <P> NativeQuery<T> setParameterList(int position, Collection<? extends P> values, BindableType<P> javaType) {
		return this.delegate.setParameterList(position, values, javaType);
	}

	@Override
	public NativeQuery<T> setParameterList(int position, Object[] values) {
		return this.delegate.setParameterList(position, values);
	}

	@Override
	public <P> NativeQuery<T> setParameterList(int position, P[] values, Class<P> javaType) {
		return this.delegate.setParameterList(position, values, javaType);
	}

	@Override
	public <P> NativeQuery<T> setParameterList(int position, P[] values, BindableType<P> javaType) {
		return this.delegate.setParameterList(position, values, javaType);
	}

	@Override
	public <P> NativeQuery<T> setParameterList(QueryParameter<P> parameter, Collection<? extends P> values) {
		return this.delegate.setParameterList(parameter, values);
	}

	@Override
	public <P> NativeQuery<T> setParameterList(QueryParameter<P> parameter, Collection<? extends P> values,
			Class<P> javaType) {
		return this.delegate.setParameterList(parameter, values, javaType);
	}

	@Override
	public <P> NativeQuery<T> setParameterList(QueryParameter<P> parameter, Collection<? extends P> values,
			BindableType<P> type) {
		return this.delegate.setParameterList(parameter, values, type);
	}

	@Override
	public <P> NativeQuery<T> setParameterList(QueryParameter<P> parameter, P[] values) {
		return this.delegate.setParameterList(parameter, values);
	}

	@Override
	public <P> NativeQuery<T> setParameterList(QueryParameter<P> parameter, P[] values, Class<P> javaType) {
		return this.delegate.setParameterList(parameter, values, javaType);
	}

	@Override
	public <P> NativeQuery<T> setParameterList(QueryParameter<P> parameter, P[] values, BindableType<P> type) {
		return this.delegate.setParameterList(parameter, values, type);
	}

	@Override
	public NativeQuery<T> setProperties(Object bean) {
		return this.delegate.setProperties(bean);
	}

	@Override
	public NativeQuery<T> setProperties(Map bean) {
		return this.delegate.setProperties(bean);
	}
}