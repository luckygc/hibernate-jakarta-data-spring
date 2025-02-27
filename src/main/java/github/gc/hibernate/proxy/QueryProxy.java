package github.gc.hibernate.proxy;

import github.gc.hibernate.StatelessSessionUtils;
import jakarta.persistence.*;
import org.hibernate.*;
import org.hibernate.graph.GraphSemantic;
import org.hibernate.graph.RootGraph;
import org.hibernate.query.Query;
import org.hibernate.query.*;
import org.hibernate.query.restriction.Restriction;
import org.hibernate.query.spi.QueryOptions;
import org.hibernate.transform.ResultTransformer;

import java.time.Instant;
import java.util.*;
import java.util.function.Function;
import java.util.function.IntSupplier;
import java.util.function.LongSupplier;
import java.util.function.Supplier;
import java.util.stream.Stream;

@SuppressWarnings("deprecation")
public class QueryProxy<R> implements Query<R> {

	private final Query<R> target;
	private final StatelessSession session;

	public QueryProxy(Query<R> target, StatelessSession session) {
		this.target = target;
		this.session = session;
	}

	@Override
	public List<R> list() {
		return doInvoke(this.target::list);
	}

	@Override
	public List<R> getResultList() {
		return doInvoke(this.target::getResultList);
	}

	@Override
	public ScrollableResults<R> scroll() {
		return doInvoke(() -> this.target.scroll());
	}

	@Override
	public ScrollableResults<R> scroll(ScrollMode scrollMode) {
		return doInvoke(() -> this.target.scroll(scrollMode));
	}

	@Override
	public Stream<R> getResultStream() {
		return doInvoke(this.target::getResultStream);
	}

	@Override
	public Stream<R> stream() {
		return doInvoke(this.target::getResultStream);
	}

	@Override
	public R uniqueResult() {
		return doInvoke(this.target::uniqueResult);
	}

	@Override
	public R getSingleResult() {
		return doInvoke(this.target::getSingleResult);
	}

	@Override
	public R getSingleResultOrNull() {
		return doInvoke(this.target::getSingleResultOrNull);
	}

	@Override
	public Optional<R> uniqueResultOptional() {
		return doInvoke(this.target::uniqueResultOptional);
	}

	@Override
	public long getResultCount() {
		return doInvoke(this.target::getResultCount);
	}

	@Override
	public KeyedResultList<R> getKeyedResultList(KeyedPage page) {
		return doInvoke(this.target::getKeyedResultList, page);
	}

	@Override
	public int executeUpdate() {
		return doInvoke(this.target::executeUpdate);
	}

	@Override
	public SharedSessionContract getSession() {
		return this.target.getSession();
	}

	@Override
	public String getQueryString() {
		return this.target.getQueryString();
	}

	@Override
	public Query<R> applyGraph(RootGraph graph, GraphSemantic semantic) {
		return this.target.applyGraph(graph, semantic);
	}

	@Override
	public Query<R> applyFetchGraph(RootGraph graph) {
		return this.target.applyFetchGraph(graph);
	}

	@Override
	public Query<R> applyLoadGraph(RootGraph graph) {
		return this.target.applyLoadGraph(graph);
	}

	@Override
	public String getComment() {
		return this.target.getComment();
	}

	@Override
	public Query<R> setComment(String comment) {
		return this.target.setComment(comment);
	}

	@Override
	public Integer getFetchSize() {
		return this.target.getFetchSize();
	}

	@Override
	public Query<R> addQueryHint(String hint) {
		return this.target.addQueryHint(hint);
	}

	@Override
	public LockOptions getLockOptions() {
		return this.target.getLockOptions();
	}

	@Override
	public Query<R> setLockOptions(LockOptions lockOptions) {
		return this.target.setLockOptions(lockOptions);
	}

	@Override
	public LockModeType getLockMode() {
		return this.target.getLockMode();
	}

	@Override
	public Query<R> setLockMode(String alias, LockMode lockMode) {
		return this.target.setLockMode(alias, lockMode);
	}

	@Override
	public Query<R> setResultListTransformer(ResultListTransformer<R> transformer) {
		return this.target.setResultListTransformer(transformer);
	}

	@Override
	public QueryOptions getQueryOptions() {
		return this.target.getQueryOptions();
	}

	@Override
	public ParameterMetadata getParameterMetadata() {
		return this.target.getParameterMetadata();
	}

	@Override
	public Query<R> setParameter(String parameter, Object argument) {
		return this.target.setParameter(parameter, argument);
	}

	@Override
	public Query<R> setParameter(String parameter, Instant argument, TemporalType temporalType) {
		return this.target.setParameter(parameter, argument, temporalType);
	}

	@Override
	public Query<R> setParameter(String parameter, Calendar argument, TemporalType temporalType) {
		return this.target.setParameter(parameter, argument, temporalType);
	}

	@Override
	public Query<R> setParameter(String parameter, Date argument, TemporalType temporalType) {
		return this.target.setParameter(parameter, argument, temporalType);
	}

	@Override
	public Query<R> setParameter(int parameter, Object argument) {
		return this.target.setParameter(parameter, argument);
	}

	@Override
	public Query<R> setParameter(int parameter, Instant argument, TemporalType temporalType) {
		return this.target.setParameter(parameter, argument, temporalType);
	}

	@Override
	public Query<R> setParameter(int parameter, Date argument, TemporalType temporalType) {
		return this.target.setParameter(parameter, argument, temporalType);
	}

	@Override
	public Set<Parameter<?>> getParameters() {
		return this.target.getParameters();
	}

	@Override
	public Parameter<?> getParameter(String s) {
		return this.target.getParameter(s);
	}

	@Override
	public <T> Parameter<T> getParameter(String s, Class<T> aClass) {
		return this.target.getParameter(s, aClass);
	}

	@Override
	public Parameter<?> getParameter(int i) {
		return this.target.getParameter(i);
	}

	@Override
	public <T> Parameter<T> getParameter(int i, Class<T> aClass) {
		return this.target.getParameter(i, aClass);
	}

	@Override
	public boolean isBound(Parameter<?> parameter) {
		return this.target.isBound(parameter);
	}

	@Override
	public <T> T getParameterValue(Parameter<T> parameter) {
		return this.target.getParameterValue(parameter);
	}

	@Override
	public Object getParameterValue(String s) {
		return this.target.getParameterValue(s);
	}

	@Override
	public Object getParameterValue(int i) {
		return this.target.getParameterValue(i);
	}

	@Override
	public Query<R> setParameter(int parameter, Calendar argument, TemporalType temporalType) {
		return this.target.setParameter(parameter, argument, temporalType);
	}

	@Override
	public Query<R> setParameterList(String parameter, Collection arguments) {
		return this.target.setParameterList(parameter, arguments);
	}

	@Override
	public Query<R> setParameterList(String parameter, Object[] values) {
		return this.target.setParameterList(parameter, values);
	}

	@Override
	public Query<R> setParameterList(int parameter, Collection arguments) {
		return this.target.setParameterList(parameter, arguments);
	}

	@Override
	public Query<R> setParameterList(int parameter, Object[] arguments) {
		return this.target.setParameterList(parameter, arguments);
	}

	@Override
	public Query<R> setProperties(Object bean) {
		return this.target.setProperties(bean);
	}

	@Override
	public Query<R> setProperties(Map bean) {
		return this.target.setProperties(bean);
	}

	@Override
	public Query<R> setHibernateFlushMode(FlushMode flushMode) {
		return this.target.setHibernateFlushMode(flushMode);
	}

	@Override
	public Integer getTimeout() {
		return this.target.getTimeout();
	}

	@Override
	public <T> T unwrap(Class<T> aClass) {
		return this.target.unwrap(aClass);
	}

	@Override
	public QueryFlushMode getQueryFlushMode() {
		return this.target.getQueryFlushMode();
	}

	@Override
	public Query<R> setQueryFlushMode(QueryFlushMode queryFlushMode) {
		return this.target.setQueryFlushMode(queryFlushMode);
	}

	@Override
	public FlushModeType getFlushMode() {
		return this.target.getFlushMode();
	}

	@Override
	public Query<R> setCacheable(boolean cacheable) {
		return this.target.setCacheable(cacheable);
	}

	@Override
	public boolean isQueryPlanCacheable() {
		return this.target.isQueryPlanCacheable();
	}

	@Override
	public SelectionQuery<R> setQueryPlanCacheable(boolean queryPlanCacheable) {
		return this.target.setQueryPlanCacheable(queryPlanCacheable);
	}

	@Override
	public String getCacheRegion() {
		return this.target.getCacheRegion();
	}

	@Override
	public Query<R> setCacheRegion(String cacheRegion) {
		return this.target.setCacheRegion(cacheRegion);
	}

	@Override
	public Query<R> setCacheMode(CacheMode cacheMode) {
		return this.target.setCacheMode(cacheMode);
	}

	@Override
	public Query<R> setCacheStoreMode(CacheStoreMode cacheStoreMode) {
		return this.target.setCacheStoreMode(cacheStoreMode);
	}

	@Override
	public TypedQuery<R> setTimeout(Integer integer) {
		return this.target.setTimeout(integer);
	}

	@Override
	public Query<R> setCacheRetrieveMode(CacheRetrieveMode cacheRetrieveMode) {
		return this.target.setCacheRetrieveMode(cacheRetrieveMode);
	}

	@Override
	public boolean isCacheable() {
		return this.target.isCacheable();
	}

	@Override
	public Query<R> setTimeout(int timeout) {
		return this.target.setTimeout(timeout);
	}

	@Override
	public Query<R> setFetchSize(int fetchSize) {
		return this.target.setFetchSize(fetchSize);
	}

	@Override
	public boolean isReadOnly() {
		return this.target.isReadOnly();
	}

	@Override
	public Query<R> setReadOnly(boolean readOnly) {
		return this.target.setReadOnly(readOnly);
	}

	@Override
	public int getMaxResults() {
		return this.target.getMaxResults();
	}

	@Override
	public Query<R> setMaxResults(int maxResults) {
		return this.target.setMaxResults(maxResults);
	}

	@Override
	public int getFirstResult() {
		return this.target.getFirstResult();
	}

	@Override
	public Query<R> setFirstResult(int startPosition) {
		return this.target.setFirstResult(startPosition);
	}

	@Override
	public Query<R> setPage(Page page) {
		return this.target.setPage(page);
	}

	@Override
	public CacheMode getCacheMode() {
		return this.target.getCacheMode();
	}

	@Override
	public CacheStoreMode getCacheStoreMode() {
		return this.target.getCacheStoreMode();
	}

	@Override
	public CacheRetrieveMode getCacheRetrieveMode() {
		return this.target.getCacheRetrieveMode();
	}

	@Override
	public Query<R> setHint(String hintName, Object value) {
		return this.target.setHint(hintName, value);
	}

	@Override
	public Map<String, Object> getHints() {
		return this.target.getHints();
	}

	@Override
	public Query<R> setEntityGraph(EntityGraph<R> graph, GraphSemantic semantic) {
		return this.target.setEntityGraph(graph, semantic);
	}

	@Override
	public Query<R> enableFetchProfile(String profileName) {
		return this.target.enableFetchProfile(profileName);
	}

	@Override
	public Query<R> disableFetchProfile(String profileName) {
		return this.target.disableFetchProfile(profileName);
	}

	@Override
	public Query<R> setFlushMode(FlushModeType flushMode) {
		return this.target.setFlushMode(flushMode);
	}

	@Override
	public FlushMode getHibernateFlushMode() {
		return this.target.getHibernateFlushMode();
	}

	@Override
	public Query<R> setLockMode(LockModeType lockMode) {
		return this.target.setLockMode(lockMode);
	}

	@Override
	public <T> Query<T> setResultTransformer(ResultTransformer<T> transformer) {
		return this.target.setResultTransformer(transformer);
	}

	@Override
	public LockMode getHibernateLockMode() {
		return this.target.getHibernateLockMode();
	}

	@Override
	public SelectionQuery<R> setHibernateLockMode(LockMode lockMode) {
		return this.target.setHibernateLockMode(lockMode);
	}

	@Override
	public Query<R> setOrder(Order<? super R> order) {
		return this.target.setOrder(order);
	}

	@Override
	public Query<R> addRestriction(Restriction<? super R> restriction) {
		return this.target.addRestriction(restriction);
	}

	@Override
	public SelectionQuery<R> setFollowOnLocking(boolean enable) {
		return this.target.setFollowOnLocking(enable);
	}

	@Override
	public Query<R> setOrder(List<? extends Order<? super R>> orderList) {
		return this.target.setOrder(orderList);
	}

	@Override
	public <P> Query<R> setParameterList(QueryParameter<P> parameter, P[] arguments, BindableType<P> type) {
		return this.target.setParameterList(parameter, arguments, type);
	}

	@Override
	public <P> Query<R> setParameterList(QueryParameter<P> parameter, P[] arguments, Class<P> javaType) {
		return this.target.setParameterList(parameter, arguments, javaType);
	}

	@Override
	public <P> Query<R> setParameterList(QueryParameter<P> parameter, P[] arguments){
		return this.target.setParameterList(parameter, arguments);
	}

	@Override
	public <P> Query<R> setParameterList(QueryParameter<P> parameter, Collection<? extends P> arguments, BindableType<P> type) {
		return this.target.setParameterList(parameter, arguments, type);
	}

	@Override
	public <P> Query<R> setParameterList(QueryParameter<P> parameter, Collection<? extends P> arguments, Class<P> javaType){
		return this.target.setParameterList(parameter, arguments, javaType);
	}

	@Override
	public <P> Query<R> setParameterList(QueryParameter<P> parameter, Collection<? extends P> arguments){
		return this.target.setParameterList(parameter, arguments);
	}

	@Override
	public <P> Query<R> setParameterList(int parameter, P[] arguments, BindableType<P> type) {
		return this.target.setParameterList(parameter, arguments, type);
	}

	@Override
	public <P> Query<R> setParameterList(int parameter, P[] arguments, Class<P> javaType){
		return this.target.setParameterList(parameter, arguments, javaType);
	}

	@Override
	public <P> Query<R> setParameterList(int parameter, Collection<? extends P> arguments, BindableType<P> type) {
		return this.target.setParameterList(parameter, arguments, type);
	}

	@Override
	public <P> Query<R> setParameterList(int parameter, Collection<? extends P> arguments, Class<P> javaType) {
		return this.target.setParameterList(parameter, arguments, javaType);
	}

	@Override
	public <P> Query<R> setParameterList(String parameter, P[] arguments, BindableType<P> type) {
		return this.target.setParameterList(parameter, arguments, type);
	}

	@Override
	public <P> Query<R> setParameterList(String parameter, P[] arguments, Class<P> javaType) {
		return this.target.setParameterList(parameter, arguments, javaType);
	}

	@Override
	public <P> Query<R> setParameterList(String parameter, Collection<? extends P> arguments, BindableType<P> type) {
		return this.target.setParameterList(parameter, arguments, type);
	}

	@Override
	public <P> Query<R> setParameterList(String parameter, Collection<? extends P> arguments, Class<P> javaType) {
		return this.target.setParameterList(parameter, arguments, javaType);
	}

	@Override
	public Query<R> setParameter(Parameter<Date> parameter, Date argument, TemporalType temporalType){
		return this.target.setParameter(parameter, argument, temporalType);
	}

	@Override
	public 	Query<R> setParameter(Parameter<Calendar> parameter, Calendar argument, TemporalType temporalType){
		return this.target.setParameter(parameter, argument, temporalType);
	}

	@Override
	public <T> Query<R> setParameter(Parameter<T> parameter, T argument){
		return this.target.setParameter(parameter, argument);
	}

	@Override
	public <P> Query<R> setParameter(QueryParameter<P> parameter, P argument, BindableType<P> type) {
		return this.target.setParameter(parameter, argument, type);
	}

	@Override
	public <P> Query<R> setParameter(QueryParameter<P> parameter, P argument, Class<P> type){
		return this.target.setParameter(parameter, argument, type);
	}

	@Override
	public <T> Query<R> setParameter(QueryParameter<T> parameter, T argument) {
		return this.target.setParameter(parameter, argument);
	}

	@Override
	public <P> Query<R> setParameter(int parameter, P argument, BindableType<P> type){
		return this.target.setParameter(parameter, argument, type);
	}

	@Override
	public <P> Query<R> setParameter(int parameter, P argument, Class<P> type) {
		return this.target.setParameter(parameter, argument, type);
	}

	@Override
	public <P> Query<R> setParameter(String parameter, P argument, BindableType<P> type) {
		return this.target.setParameter(parameter, argument, type);
	}

	@Override
	public <P> Query<R> setParameter(String parameter, P argument, Class<P> type) {
		return this.target.setParameter(parameter, argument, type);
	}

	@Override
	public <T> Query<T> setTupleTransformer(TupleTransformer<T> transformer) {
		return this.target.setTupleTransformer(transformer);
	}

	private <T> T doInvoke(Supplier<T> supplier) {
		try {
			return supplier.get();
		} finally {
			StatelessSessionUtils.closeStatelessSession(this.session);
		}
	}

	private long doInvoke(LongSupplier supplier) {
		try {
			return supplier.getAsLong();
		} finally {
			StatelessSessionUtils.closeStatelessSession(this.session);
		}
	}

	private int doInvoke(IntSupplier supplier) {
		try {
			return supplier.getAsInt();
		} finally {
			StatelessSessionUtils.closeStatelessSession(this.session);
		}
	}

	private <P, T> T doInvoke(Function<P, T> function, P param) {
		try {
			return function.apply(param);
		} finally {
			StatelessSessionUtils.closeStatelessSession(this.session);
		}
	}
}