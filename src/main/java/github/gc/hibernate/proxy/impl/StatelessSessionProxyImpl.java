package github.gc.hibernate.proxy.impl;

import github.gc.hibernate.StatelessSessionUtils;
import github.gc.hibernate.proxy.StatelessSessionProxy;
import jakarta.persistence.EntityGraph;
import jakarta.persistence.TypedQueryReference;
import jakarta.persistence.criteria.CriteriaDelete;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.CriteriaUpdate;
import org.hibernate.*;
import org.hibernate.graph.GraphSemantic;
import org.hibernate.graph.RootGraph;
import org.hibernate.jdbc.ReturningWork;
import org.hibernate.jdbc.Work;
import org.hibernate.procedure.ProcedureCall;
import org.hibernate.query.MutationQuery;
import org.hibernate.query.NativeQuery;
import org.hibernate.query.Query;
import org.hibernate.query.SelectionQuery;
import org.hibernate.query.criteria.HibernateCriteriaBuilder;
import org.hibernate.query.criteria.JpaCriteriaInsert;
import org.hibernate.query.criteria.JpaCriteriaInsertSelect;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.sql.DataSource;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * 注入到repository的StatelessSession代理，不使用jdk代理，防止接口方法变更无法及时发现。
 * 线程安全，事务内使用线程绑定的session，常规操作操作完直接关闭。
 * StatelessSession默认连接管理策略是需要时获取连接，事务后释放，通过事务后
 */
public class StatelessSessionProxyImpl implements StatelessSession, StatelessSessionProxy {

	private final SessionFactory sessionFactory;
	private final DataSource dataSource;

	public StatelessSessionProxyImpl(SessionFactory sessionFactory, DataSource dataSource) {
		this.sessionFactory = sessionFactory;
		this.dataSource = dataSource;
	}

	private <T> T doInvoke(Function<StatelessSession, T> function) {
		StatelessSession statelessSession = StatelessSessionUtils.doGetTransactionalStatelessSession(sessionFactory,
				dataSource);
		boolean isNewSession = false;
		if (statelessSession == null) {
			statelessSession = sessionFactory.openStatelessSession();
			isNewSession = true;
		}

		try {
			return function.apply(statelessSession);
		} finally {
			if (isNewSession) {
				StatelessSessionUtils.closeStatelessSession(statelessSession);
			}
		}
	}

	private void doInvoke(Consumer<StatelessSession> consumer) {
		StatelessSession statelessSession = StatelessSessionUtils.doGetTransactionalStatelessSession(sessionFactory,
				dataSource);
		boolean isNewSession = false;
		if (statelessSession == null) {
			statelessSession = sessionFactory.openStatelessSession();
			isNewSession = true;
		}

		try {
			consumer.accept(statelessSession);
		} finally {
			if (isNewSession) {
				StatelessSessionUtils.closeStatelessSession(statelessSession);
			}
		}
	}

	private <T> void doInvoke(BiConsumer<StatelessSession, T> biConsumer, T t) {
		StatelessSession statelessSession = StatelessSessionUtils.doGetTransactionalStatelessSession(sessionFactory,
				dataSource);
		boolean isNewSession = false;
		if (statelessSession == null) {
			statelessSession = sessionFactory.openStatelessSession();
			isNewSession = true;
		}

		try {
			biConsumer.accept(statelessSession, t);
		} finally {
			if (isNewSession) {
				StatelessSessionUtils.closeStatelessSession(statelessSession);
			}
		}
	}

	@Override
	public String getTenantIdentifier() {
		return doInvoke(StatelessSession::getTenantIdentifier);
	}

	@Override
	public Object getTenantIdentifierValue() {
		return doInvoke(StatelessSession::getTenantIdentifierValue);
	}

	@Override
	public CacheMode getCacheMode() {
		return doInvoke(StatelessSession::getCacheMode);
	}

	@Override
	public void setCacheMode(CacheMode cacheMode) {
		doInvoke(StatelessSession::setCacheMode, cacheMode);
	}

	@Override
	public void close() {
		// 不需要关闭
	}

	@Override
	public boolean isOpen() {
		return true;
	}

	@Override
	public boolean isConnected() {
		return false;
	}

	@Override
	public Transaction beginTransaction() {
		return null;
	}

	@Override
	public Transaction getTransaction() {
		throw new IllegalStateException("不允许在StatelessSessionProxy上调用getTransaction - 使用Spring事务代替");
	}

	@Override
	public void joinTransaction() {
		throw new IllegalStateException("不允许在StatelessSessionProxy上调用joinTransaction - 自动加入Spring事务");
	}

	@Override
	public boolean isJoinedToTransaction() {
		return TransactionSynchronizationManager.isActualTransactionActive();
	}

	@Override
	public ProcedureCall getNamedProcedureCall(String name) {
		// TODO
		return null;
	}

	@Override
	public ProcedureCall createStoredProcedureCall(String procedureName) {
		// TODO
		return null;
	}

	@Override
	public ProcedureCall createStoredProcedureCall(String procedureName, Class<?>... resultClasses) {
		// TODO
		return null;
	}

	@Override
	public ProcedureCall createStoredProcedureCall(String procedureName, String... resultSetMappings) {
		// TODO
		return null;
	}

	@Override
	public ProcedureCall createNamedStoredProcedureQuery(String name) {
		// TODO
		return null;
	}

	@Override
	public ProcedureCall createStoredProcedureQuery(String procedureName) {
		// TODO
		return null;
	}

	@Override
	public ProcedureCall createStoredProcedureQuery(String procedureName, Class<?>... resultClasses) {
		// TODO
		return null;
	}

	@Override
	public ProcedureCall createStoredProcedureQuery(String procedureName, String... resultSetMappings) {
		// TODO
		return null;
	}

	@Override
	public Integer getJdbcBatchSize() {
		return doInvoke(StatelessSession::getJdbcBatchSize);
	}

	@Override
	public void setJdbcBatchSize(Integer jdbcBatchSize) {
		doInvoke(StatelessSession::setJdbcBatchSize, jdbcBatchSize);
	}

	@Override
	public HibernateCriteriaBuilder getCriteriaBuilder() {
		return this.sessionFactory.getCriteriaBuilder();
	}

	@Override
	public void doWork(Work work) throws HibernateException {

	}

	@Override
	public <T> T doReturningWork(ReturningWork<T> work) {
		return null;
	}

	@Override
	public <T> RootGraph<T> createEntityGraph(Class<T> rootType) {
		return null;
	}

	@Override
	public RootGraph<?> createEntityGraph(String graphName) {
		return null;
	}

	@Override
	public <T> RootGraph<T> createEntityGraph(Class<T> rootType, String graphName) {
		return null;
	}

	@Override
	public RootGraph<?> getEntityGraph(String graphName) {
		return null;
	}

	@Override
	public <T> List<EntityGraph<? super T>> getEntityGraphs(Class<T> entityClass) {
		return List.of();
	}

	@Override
	public Filter enableFilter(String filterName) {
		return null;
	}

	@Override
	public Filter getEnabledFilter(String filterName) {
		return null;
	}

	@Override
	public void disableFilter(String filterName) {

	}

	@Override
	public SessionFactory getFactory() {
		return null;
	}

	@Override
	public Object insert(Object entity) {
		return null;
	}

	@Override
	public void insertMultiple(List<?> entities) {

	}

	@Override
	public Object insert(String entityName, Object entity) {
		return null;
	}

	@Override
	public void update(Object entity) {

	}

	@Override
	public void updateMultiple(List<?> entities) {

	}

	@Override
	public void update(String entityName, Object entity) {

	}

	@Override
	public void delete(Object entity) {

	}

	@Override
	public void deleteMultiple(List<?> entities) {

	}

	@Override
	public void delete(String entityName, Object entity) {

	}

	@Override
	public void upsert(Object entity) {

	}

	@Override
	public void upsertMultiple(List<?> entities) {

	}

	@Override
	public void upsert(String entityName, Object entity) {

	}

	@Override
	public Object get(String entityName, Object id) {
		return null;
	}

	@Override
	public <T> T get(Class<T> entityClass, Object id) {
		return null;
	}

	@Override
	public Object get(String entityName, Object id, LockMode lockMode) {
		return null;
	}

	@Override
	public <T> T get(Class<T> entityClass, Object id, LockMode lockMode) {
		return null;
	}

	@Override
	public <T> T get(EntityGraph<T> graph, GraphSemantic graphSemantic, Object id) {
		return null;
	}

	@Override
	public <T> T get(EntityGraph<T> graph, GraphSemantic graphSemantic, Object id, LockMode lockMode) {
		return null;
	}

	@Override
	public <T> List<T> getMultiple(Class<T> entityClass, List<Object> ids) {
		return List.of();
	}

	@Override
	public void refresh(Object entity) {

	}

	@Override
	public void refresh(String entityName, Object entity) {

	}

	@Override
	public void refresh(Object entity, LockMode lockMode) {

	}

	@Override
	public void refresh(String entityName, Object entity, LockMode lockMode) {

	}

	@Override
	public void fetch(Object association) {

	}

	@Override
	public Object getIdentifier(Object entity) {
		return null;
	}

	@Override
	public Query createQuery(String queryString) {
		return null;
	}

	@Override
	public <R> Query<R> createQuery(String queryString, Class<R> resultClass) {
		return null;
	}

	@Override
	public <R> Query<R> createQuery(TypedQueryReference<R> typedQueryReference) {
		return null;
	}

	@Override
	public <R> Query<R> createQuery(CriteriaQuery<R> criteriaQuery) {
		return null;
	}

	@Override
	public Query createQuery(CriteriaUpdate<?> updateQuery) {
		return null;
	}

	@Override
	public Query createQuery(CriteriaDelete<?> deleteQuery) {
		return null;
	}

	@Override
	public NativeQuery createNativeQuery(String sqlString) {
		return null;
	}

	@Override
	public <R> NativeQuery<R> createNativeQuery(String sqlString, Class<R> resultClass) {
		return null;
	}

	@Override
	public <R> NativeQuery<R> createNativeQuery(String sqlString, Class<R> resultClass, String tableAlias) {
		return null;
	}

	@Override
	public NativeQuery createNativeQuery(String sqlString, String resultSetMappingName) {
		return null;
	}

	@Override
	public <R> NativeQuery<R> createNativeQuery(String sqlString, String resultSetMappingName, Class<R> resultClass) {
		return null;
	}

	@Override
	public SelectionQuery<?> createSelectionQuery(String hqlString) {
		return null;
	}

	@Override
	public <R> SelectionQuery<R> createSelectionQuery(String hqlString, Class<R> resultType) {
		return null;
	}

	@Override
	public <R> SelectionQuery<R> createSelectionQuery(CriteriaQuery<R> criteria) {
		return null;
	}

	@Override
	public MutationQuery createMutationQuery(String hqlString) {
		return null;
	}

	@Override
	public MutationQuery createMutationQuery(CriteriaUpdate<?> updateQuery) {
		return null;
	}

	@Override
	public MutationQuery createMutationQuery(CriteriaDelete<?> deleteQuery) {
		return null;
	}

	@Override
	public MutationQuery createMutationQuery(JpaCriteriaInsertSelect<?> insertSelect) {
		return null;
	}

	@Override
	public MutationQuery createMutationQuery(JpaCriteriaInsert<?> insert) {
		return null;
	}

	@Override
	public MutationQuery createNativeMutationQuery(String sqlString) {
		return null;
	}

	@Override
	public Query createNamedQuery(String name) {
		return null;
	}

	@Override
	public <R> Query<R> createNamedQuery(String name, Class<R> resultClass) {
		return null;
	}

	@Override
	public SelectionQuery<?> createNamedSelectionQuery(String name) {
		return null;
	}

	@Override
	public <R> SelectionQuery<R> createNamedSelectionQuery(String name, Class<R> resultType) {
		return null;
	}

	@Override
	public MutationQuery createNamedMutationQuery(String name) {
		return null;
	}

	@Override
	public Query getNamedQuery(String queryName) {
		return null;
	}

	@Override
	public NativeQuery getNamedNativeQuery(String name) {
		return null;
	}

	@Override
	public NativeQuery getNamedNativeQuery(String name, String resultSetMapping) {
		return null;
	}

	@Override
	public StatelessSession getTargetStatelessSession() {
		return null;
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof StatelessSessionProxyImpl that)) {
			return false;
		}

		return Objects.equals(sessionFactory, that.sessionFactory) && Objects.equals(dataSource, that.dataSource);
	}

	@Override
	public int hashCode() {
		return Objects.hash(sessionFactory, dataSource);
	}

	@Override
	public String toString() {
		return "StatelessSession proxy for sessionFactory [%s]".formatted(this.sessionFactory);
	}
}
