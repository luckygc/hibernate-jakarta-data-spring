package github.gc.hibernate.proxy.impl;

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

import java.time.Instant;
import java.util.*;
import java.util.stream.Stream;

@SuppressWarnings("deprecation")
public class NativeQueryProxy<T> implements NativeQuery<T> {

	private final NativeQuery<T> delegate;
	private final StatelessSession session;

	public NativeQueryProxy(NativeQueryProxy<T> delegate, StatelessSession session) {
		this.delegate = delegate;
		this.session = session;
	}

	@Override
	public NativeQuery<T> addScalar(String columnAlias) {
		return null;
	}

	@Override
	public NativeQuery<T> addScalar(String columnAlias, BasicTypeReference type) {
		return null;
	}

	@Override
	public NativeQuery<T> addScalar(String columnAlias, BasicDomainType type) {
		return null;
	}

	@Override
	public NativeQuery<T> addScalar(String columnAlias, Class javaType) {
		return null;
	}

	@Override
	public <C> NativeQuery<T> addScalar(String columnAlias, Class<C> relationalJavaType,
			AttributeConverter<?, C> converter) {
		return null;
	}

	@Override
	public <O, R> NativeQuery<T> addScalar(String columnAlias, Class<O> domainJavaType, Class<R> jdbcJavaType,
			AttributeConverter<O, R> converter) {
		return null;
	}

	@Override
	public <C> NativeQuery<T> addScalar(String columnAlias, Class<C> relationalJavaType,
			Class<? extends AttributeConverter<?, C>> converter) {
		return null;
	}

	@Override
	public <O, R> NativeQuery<T> addScalar(String columnAlias, Class<O> domainJavaType, Class<R> jdbcJavaType,
			Class<? extends AttributeConverter<O, R>> converter) {
		return null;
	}

	@Override
	public <J> InstantiationResultNode<J> addInstantiation(Class<J> targetJavaType) {
		return null;
	}

	@Override
	public NativeQuery<T> addAttributeResult(String columnAlias, Class entityJavaType, String attributePath) {
		return null;
	}

	@Override
	public NativeQuery<T> addAttributeResult(String columnAlias, String entityName, String attributePath) {
		return null;
	}

	@Override
	public NativeQuery<T> addAttributeResult(String columnAlias, SingularAttribute attribute) {
		return null;
	}

	@Override
	public RootReturn addRoot(String tableAlias, String entityName) {
		return null;
	}

	@Override
	public RootReturn addRoot(String tableAlias, Class entityType) {
		return null;
	}

	@Override
	public NativeQuery<T> addEntity(String entityName) {
		return null;
	}

	@Override
	public NativeQuery<T> addEntity(String tableAlias, String entityName) {
		return null;
	}

	@Override
	public NativeQuery<T> addEntity(String tableAlias, String entityName, LockMode lockMode) {
		return null;
	}

	@Override
	public NativeQuery<T> addEntity(Class entityType) {
		return null;
	}

	@Override
	public NativeQuery<T> addEntity(String tableAlias, Class entityType) {
		return null;
	}

	@Override
	public NativeQuery<T> addEntity(String tableAlias, Class entityClass, LockMode lockMode) {
		return null;
	}

	@Override
	public FetchReturn addFetch(String tableAlias, String ownerTableAlias, String joinPropertyName) {
		return null;
	}

	@Override
	public NativeQuery<T> addJoin(String tableAlias, String path) {
		return null;
	}

	@Override
	public NativeQuery<T> addJoin(String tableAlias, String ownerTableAlias, String joinPropertyName) {
		return null;
	}

	@Override
	public NativeQuery<T> addJoin(String tableAlias, String path, LockMode lockMode) {
		return null;
	}

	@Override
	public Collection<String> getSynchronizedQuerySpaces() {
		return List.of();
	}

	@Override
	public NativeQuery<T> addSynchronizedQuerySpace(String querySpace) {
		return null;
	}

	@Override
	public NativeQuery<T> addSynchronizedEntityName(String entityName) throws MappingException {
		return null;
	}

	@Override
	public NativeQuery<T> addSynchronizedEntityClass(Class entityClass) throws MappingException {
		return null;
	}

	@Override
	public NativeQuery<T> setHibernateFlushMode(FlushMode flushMode) {
		return null;
	}

	@Override
	public Integer getTimeout() {
		return 0;
	}

	@Override
	public QueryFlushMode getQueryFlushMode() {
		return null;
	}

	@Override
	public NativeQuery<T> setQueryFlushMode(QueryFlushMode queryFlushMode) {
		return null;
	}

	@Override
	public FlushModeType getFlushMode() {
		return null;
	}

	@Override
	public NativeQuery<T> setFlushMode(FlushModeType flushMode) {
		return null;
	}

	@Override
	public FlushMode getHibernateFlushMode() {
		return null;
	}

	@Override
	public NativeQuery<T> setCacheMode(CacheMode cacheMode) {
		return null;
	}

	@Override
	public NativeQuery<T> setCacheStoreMode(CacheStoreMode cacheStoreMode) {
		return null;
	}

	@Override
	public TypedQuery<T> setTimeout(Integer integer) {
		return null;
	}

	@Override
	public <R> R unwrap(Class<R> aClass) {
		return null;
	}

	@Override
	public NativeQuery<T> setCacheRetrieveMode(CacheRetrieveMode cacheRetrieveMode) {
		return null;
	}

	@Override
	public boolean isCacheable() {
		return false;
	}

	@Override
	public NativeQuery<T> setCacheable(boolean cacheable) {
		return null;
	}

	@Override
	public boolean isQueryPlanCacheable() {
		return false;
	}

	@Override
	public SelectionQuery<T> setQueryPlanCacheable(boolean queryPlanCacheable) {
		return null;
	}

	@Override
	public String getCacheRegion() {
		return "";
	}

	@Override
	public NativeQuery<T> setCacheRegion(String cacheRegion) {
		return null;
	}

	@Override
	public NativeQuery<T> setTimeout(int timeout) {
		return null;
	}

	@Override
	public NativeQuery<T> setFetchSize(int fetchSize) {
		return null;
	}

	@Override
	public boolean isReadOnly() {
		return false;
	}

	@Override
	public NativeQuery<T> setReadOnly(boolean readOnly) {
		return null;
	}

	@Override
	public int getMaxResults() {
		return 0;
	}

	@Override
	public LockOptions getLockOptions() {
		return null;
	}

	@Override
	public NativeQuery<T> setLockOptions(LockOptions lockOptions) {
		return null;
	}

	@Override
	public NativeQuery<T> setLockMode(String alias, LockMode lockMode) {
		return null;
	}

	@Override
	public List<T> list() {
		return List.of();
	}

	@Override
	public List<T> getResultList() {
		return NativeQuery.super.getResultList();
	}

	@Override
	public ScrollableResults<T> scroll() {
		return null;
	}

	@Override
	public ScrollableResults<T> scroll(ScrollMode scrollMode) {
		return null;
	}

	@Override
	public Stream<T> getResultStream() {
		return NativeQuery.super.getResultStream();
	}

	@Override
	public Stream<T> stream() {
		return NativeQuery.super.stream();
	}

	@Override
	public T uniqueResult() {
		return null;
	}

	@Override
	public T getSingleResult() {
		return null;
	}

	@Override
	public T getSingleResultOrNull() {
		return null;
	}

	@Override
	public Optional<T> uniqueResultOptional() {
		return Optional.empty();
	}

	@Override
	public long getResultCount() {
		return 0;
	}

	@Override
	public KeyedResultList<T> getKeyedResultList(KeyedPage<T> page) {
		return null;
	}

	@Override
	public int executeUpdate() {
		return 0;
	}

	@Override
	public SharedSessionContract getSession() {
		return null;
	}

	@Override
	public String getQueryString() {
		return "";
	}

	@Override
	public Query<T> applyGraph(RootGraph graph, GraphSemantic semantic) {
		return null;
	}

	@Override
	public Query<T> applyFetchGraph(RootGraph graph) {
		return NativeQuery.super.applyFetchGraph(graph);
	}

	@Override
	public Query<T> applyLoadGraph(RootGraph graph) {
		return NativeQuery.super.applyLoadGraph(graph);
	}

	@Override
	public String getComment() {
		return "";
	}

	@Override
	public NativeQuery<T> setComment(String comment) {
		return null;
	}

	@Override
	public Integer getFetchSize() {
		return 0;
	}

	@Override
	public NativeQuery<T> addQueryHint(String hint) {
		return null;
	}

	@Override
	public NativeQuery<T> setMaxResults(int maxResults) {
		return null;
	}

	@Override
	public int getFirstResult() {
		return 0;
	}

	@Override
	public NativeQuery<T> setFirstResult(int startPosition) {
		return null;
	}

	@Override
	public Query<T> setPage(Page page) {
		return NativeQuery.super.setPage(page);
	}

	@Override
	public CacheMode getCacheMode() {
		return null;
	}

	@Override
	public CacheStoreMode getCacheStoreMode() {
		return null;
	}

	@Override
	public CacheRetrieveMode getCacheRetrieveMode() {
		return null;
	}

	@Override
	public NativeQuery<T> setHint(String hintName, Object value) {
		return null;
	}

	@Override
	public Map<String, Object> getHints() {
		return Map.of();
	}

	@Override
	public Query<T> setEntityGraph(EntityGraph<T> graph, GraphSemantic semantic) {
		return null;
	}

	@Override
	public Query<T> enableFetchProfile(String profileName) {
		return null;
	}

	@Override
	public Query<T> disableFetchProfile(String profileName) {
		return null;
	}

	@Override
	public LockModeType getLockMode() {
		return null;
	}

	@Override
	public LockMode getHibernateLockMode() {
		return null;
	}

	@Override
	public NativeQuery<T> setLockMode(LockModeType lockMode) {
		return null;
	}

	@Override
	public Query<T> setOrder(List<? extends Order<? super T>> orderList) {
		return null;
	}

	@Override
	public Query<T> setOrder(Order<? super T> order) {
		return null;
	}

	@Override
	public Query<T> addRestriction(Restriction<? super T> restriction) {
		return null;
	}

	@Override
	public SelectionQuery<T> setFollowOnLocking(boolean enable) {
		return null;
	}

	@Override
	public NativeQuery<T> setHibernateLockMode(LockMode lockMode) {
		return null;
	}

	@Override
	public <R> NativeQuery<R> setTupleTransformer(TupleTransformer<R> transformer) {
		return null;
	}

	@Override
	public NativeQuery<T> setResultListTransformer(ResultListTransformer<T> transformer) {
		return null;
	}

	@Override
	public QueryOptions getQueryOptions() {
		return null;
	}

	@Override
	public ParameterMetadata getParameterMetadata() {
		return null;
	}

	@Override
	public <S> NativeQuery<S> setResultTransformer(ResultTransformer<S> transformer) {
		return null;
	}

	@Override
	public NativeQuery<T> setParameter(String name, Object value) {
		return null;
	}

	@Override
	public <P> NativeQuery<T> setParameter(String name, P val, Class<P> type) {
		return null;
	}

	@Override
	public <P> NativeQuery<T> setParameter(String name, P val, BindableType<P> type) {
		return null;
	}

	@Override
	public NativeQuery<T> setParameter(String name, Instant value, TemporalType temporalType) {
		return null;
	}

	@Override
	public NativeQuery<T> setParameter(String name, Calendar value, TemporalType temporalType) {
		return null;
	}

	@Override
	public NativeQuery<T> setParameter(String name, Date value, TemporalType temporalType) {
		return null;
	}

	@Override
	public NativeQuery<T> setParameter(int position, Object value) {
		return null;
	}

	@Override
	public <P> NativeQuery<T> setParameter(int position, P val, Class<P> type) {
		return null;
	}

	@Override
	public <P> NativeQuery<T> setParameter(int position, P val, BindableType<P> type) {
		return null;
	}

	@Override
	public NativeQuery<T> setParameter(int position, Instant value, TemporalType temporalType) {
		return null;
	}

	@Override
	public NativeQuery<T> setParameter(int position, Calendar value, TemporalType temporalType) {
		return null;
	}

	@Override
	public NativeQuery<T> setParameter(int position, Date value, TemporalType temporalType) {
		return null;
	}

	@Override
	public Set<Parameter<?>> getParameters() {
		return Set.of();
	}

	@Override
	public Parameter<?> getParameter(String s) {
		return null;
	}

	@Override
	public <P> Parameter<P> getParameter(String s, Class<P> aClass) {
		return null;
	}

	@Override
	public Parameter<?> getParameter(int i) {
		return null;
	}

	@Override
	public <P> Parameter<P> getParameter(int i, Class<P> aClass) {
		return null;
	}

	@Override
	public boolean isBound(Parameter<?> parameter) {
		return false;
	}

	@Override
	public <P> P getParameterValue(Parameter<P> parameter) {
		return null;
	}

	@Override
	public Object getParameterValue(String s) {
		return null;
	}

	@Override
	public Object getParameterValue(int i) {
		return null;
	}

	@Override
	public <P> NativeQuery<T> setParameter(QueryParameter<P> parameter, P val) {
		return null;
	}

	@Override
	public <P> NativeQuery<T> setParameter(QueryParameter<P> parameter, P val, Class<P> type) {
		return null;
	}

	@Override
	public <P> NativeQuery<T> setParameter(QueryParameter<P> parameter, P val, BindableType<P> type) {
		return null;
	}

	@Override
	public <P> NativeQuery<T> setParameter(Parameter<P> param, P value) {
		return null;
	}

	@Override
	public NativeQuery<T> setParameter(Parameter<Calendar> param, Calendar value, TemporalType temporalType) {
		return null;
	}

	@Override
	public NativeQuery<T> setParameter(Parameter<Date> param, Date value, TemporalType temporalType) {
		return null;
	}

	@Override
	public NativeQuery<T> setParameterList(String name, Collection values) {
		return null;
	}

	@Override
	public <P> NativeQuery<T> setParameterList(String name, Collection<? extends P> values, Class<P> type) {
		return null;
	}

	@Override
	public <P> NativeQuery<T> setParameterList(String name, Collection<? extends P> values, BindableType<P> type) {
		return null;
	}

	@Override
	public NativeQuery<T> setParameterList(String name, Object[] values) {
		return null;
	}

	@Override
	public <P> NativeQuery<T> setParameterList(String name, P[] values, Class<P> type) {
		return null;
	}

	@Override
	public <P> NativeQuery<T> setParameterList(String name, P[] values, BindableType<P> type) {
		return null;
	}

	@Override
	public NativeQuery<T> setParameterList(int position, Collection values) {
		return null;
	}

	@Override
	public <P> NativeQuery<T> setParameterList(int position, Collection<? extends P> values, Class<P> type) {
		return null;
	}

	@Override
	public <P> NativeQuery<T> setParameterList(int position, Collection<? extends P> values, BindableType<P> javaType) {
		return null;
	}

	@Override
	public NativeQuery<T> setParameterList(int position, Object[] values) {
		return null;
	}

	@Override
	public <P> NativeQuery<T> setParameterList(int position, P[] values, Class<P> javaType) {
		return null;
	}

	@Override
	public <P> NativeQuery<T> setParameterList(int position, P[] values, BindableType<P> javaType) {
		return null;
	}

	@Override
	public <P> NativeQuery<T> setParameterList(QueryParameter<P> parameter, Collection<? extends P> values) {
		return null;
	}

	@Override
	public <P> NativeQuery<T> setParameterList(QueryParameter<P> parameter, Collection<? extends P> values,
			Class<P> javaType) {
		return null;
	}

	@Override
	public <P> NativeQuery<T> setParameterList(QueryParameter<P> parameter, Collection<? extends P> values,
			BindableType<P> type) {
		return null;
	}

	@Override
	public <P> NativeQuery<T> setParameterList(QueryParameter<P> parameter, P[] values) {
		return null;
	}

	@Override
	public <P> NativeQuery<T> setParameterList(QueryParameter<P> parameter, P[] values, Class<P> javaType) {
		return null;
	}

	@Override
	public <P> NativeQuery<T> setParameterList(QueryParameter<P> parameter, P[] values, BindableType<P> type) {
		return null;
	}

	@Override
	public NativeQuery<T> setProperties(Object bean) {
		return null;
	}

	@Override
	public NativeQuery<T> setProperties(Map bean) {
		return null;
	}
}
