package github.gc.hibernate.session.proxy.impl;

import github.gc.hibernate.session.StatelessSessionUtils;
import github.gc.hibernate.session.proxy.StatelessSessionProxy;
import jakarta.persistence.EntityGraph;
import jakarta.persistence.TypedQueryReference;
import jakarta.persistence.criteria.CriteriaDelete;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.CriteriaUpdate;
import org.hibernate.*;
import org.hibernate.cfg.AvailableSettings;
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
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.Assert;

import javax.sql.DataSource;
import java.util.List;
import java.util.function.Function;

/**
 * 注入到repository的StatelessSession代理
 * 线程安全，事务内使用线程绑定的session，常规操作新建session操作完关闭。
 */
@SuppressWarnings({"deprecation", "unchecked"})
public class StatelessSessionProxyImpl implements StatelessSessionProxy {

	private final Logger log = LoggerFactory.getLogger(getClass());

	private final SessionFactory sessionFactory;
	private final DataSource dataSource;

	public StatelessSessionProxyImpl(@NonNull SessionFactory sessionFactory) {
		Assert.notNull(sessionFactory, "SessionFactory must not be null");
		this.sessionFactory = sessionFactory;
		Object datasourceObj = this.sessionFactory.getProperties().get(AvailableSettings.JAKARTA_NON_JTA_DATASOURCE);
		if (datasourceObj instanceof DataSource ds) {
			this.dataSource = ds;
		} else {
			throw new IllegalArgumentException("SessionFactory must have a DataSource");
		}
	}

	@Override
	public String getTenantIdentifier() {
		return execute(StatelessSession::getTenantIdentifier);
	}

	@Override
	public Object getTenantIdentifierValue() {
		return execute(StatelessSession::getTenantIdentifierValue);
	}

	@Override
	public CacheMode getCacheMode() {
		return execute(StatelessSession::getCacheMode);
	}

	@Override
	public void setCacheMode(CacheMode cacheMode) {
		execute(session -> {
			session.setCacheMode(cacheMode);
			return null;
		});
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
		throw new UnsupportedOperationException("不允许在StatelessSessionProxy上调用beginTransaction - 使用Spring事务");
	}

	@Override
	public Transaction getTransaction() {
		throw new UnsupportedOperationException("不允许在StatelessSessionProxy上调用getTransaction - 使用Spring事务");
	}

	@Override
	public void joinTransaction() {
		log.debug("自动加入Spring事务，不需要手动调用joinTransaction");
	}

	@Override
	public boolean isJoinedToTransaction() {
		return TransactionSynchronizationManager.isActualTransactionActive();
	}

	@Override
	public ProcedureCall getNamedProcedureCall(String name) {
		throw new UnsupportedOperationException("不支持存储过程调用");
	}

	@Override
	public ProcedureCall createStoredProcedureCall(String procedureName) {
		throw new UnsupportedOperationException("不支持存储过程调用");
	}

	@Override
	public ProcedureCall createStoredProcedureCall(String procedureName, Class<?>... resultClasses) {
		throw new UnsupportedOperationException("不支持存储过程调用");
	}

	@Override
	public ProcedureCall createStoredProcedureCall(String procedureName, String... resultSetMappings) {
		throw new UnsupportedOperationException("不支持存储过程调用");
	}

	@Override
	public ProcedureCall createNamedStoredProcedureQuery(String name) {
		throw new UnsupportedOperationException("不支持存储过程调用");
	}

	@Override
	public ProcedureCall createStoredProcedureQuery(String procedureName) {
		throw new UnsupportedOperationException("不支持存储过程调用");
	}

	@Override
	public ProcedureCall createStoredProcedureQuery(String procedureName, Class<?>... resultClasses) {
		throw new UnsupportedOperationException("不支持存储过程调用");
	}

	@Override
	public ProcedureCall createStoredProcedureQuery(String procedureName, String... resultSetMappings) {
		throw new UnsupportedOperationException("不支持存储过程调用");
	}

	@Override
	public Integer getJdbcBatchSize() {
		return execute(StatelessSession::getJdbcBatchSize);
	}

	@Override
	public void setJdbcBatchSize(Integer jdbcBatchSize) {
		execute(session -> {
			session.setJdbcBatchSize(jdbcBatchSize);
			return null;
		});
	}

	@Override
	public HibernateCriteriaBuilder getCriteriaBuilder() {
		return this.sessionFactory.getCriteriaBuilder();
	}

	@Override
	public void doWork(Work work) throws HibernateException {
		execute(session -> {
			session.doWork(work);
			return null;
		});
	}

	@Override
	public <T> T doReturningWork(ReturningWork<T> work) {
		return execute(session -> session.doReturningWork(work));
	}

	@Override
	public <T> RootGraph<T> createEntityGraph(Class<T> rootType) {
		return execute(session -> session.createEntityGraph(rootType));
	}

	@Override
	public RootGraph<?> createEntityGraph(String graphName) {
		return execute(session -> session.createEntityGraph(graphName));
	}

	@Override
	public <T> RootGraph<T> createEntityGraph(Class<T> rootType, String graphName) {
		return execute(session -> session.createEntityGraph(rootType, graphName));
	}

	@Override
	public RootGraph<?> getEntityGraph(String graphName) {
		return execute(session -> session.getEntityGraph(graphName));
	}

	@Override
	public <T> List<EntityGraph<? super T>> getEntityGraphs(Class<T> entityClass) {
		return execute(session -> session.getEntityGraphs(entityClass));
	}

	@Override
	public Filter enableFilter(String filterName) {
		return execute(session -> session.enableFilter(filterName));
	}

	@Override
	public Filter getEnabledFilter(String filterName) {
		return execute(session -> session.getEnabledFilter(filterName));
	}

	@Override
	public void disableFilter(String filterName) {
		execute(session -> {
			session.disableFilter(filterName);
			return null;
		});
	}

	@Override
	public SessionFactory getFactory() {
		return this.sessionFactory;
	}

	@Override
	public Object insert(Object entity) {
		return execute(session -> session.insert(entity));
	}

	@Override
	public void insertMultiple(List<?> entities) {
		execute(session -> {
			session.insertMultiple(entities);
			return null;
		});
	}

	@Override
	public Object insert(String entityName, Object entity) {
		return execute(session -> session.insert(entityName, entity));
	}

	@Override
	public void update(Object entity) {
		execute(session -> {
			session.update(entity);
			return null;
		});
	}

	@Override
	public void updateMultiple(List<?> entities) {
		execute(session -> {
			session.updateMultiple(entities);
			return null;
		});
	}

	@Override
	public void update(String entityName, Object entity) {
		execute(session -> {
			session.update(entityName, entity);
			return null;
		});
	}

	@Override
	public void delete(Object entity) {
		execute(session -> {
			session.delete(entity);
			return null;
		});
	}

	@Override
	public void deleteMultiple(List<?> entities) {
		execute(session -> {
			session.deleteMultiple(entities);
			return null;
		});
	}

	@Override
	public void delete(String entityName, Object entity) {
		execute(session -> {
			session.delete(entityName, entity);
			return null;
		});
	}

	@Override
	public void upsert(Object entity) {
		execute(session -> {
			session.upsert(entity);
			return null;
		});
	}

	@Override
	public void upsertMultiple(List<?> entities) {
		execute(session -> {
			session.upsertMultiple(entities);
			return null;
		});
	}

	@Override
	public void upsert(String entityName, Object entity) {
		execute(session -> {
			session.upsert(entityName, entity);
			return null;
		});
	}

	@Override
	public Object get(String entityName, Object id) {
		return execute(session -> session.get(entityName, id));
	}

	@Override
	public <T> T get(Class<T> entityClass, Object id) {
		return execute(session -> session.get(entityClass, id));
	}

	@Override
	public Object get(String entityName, Object id, LockMode lockMode) {
		return execute(session -> session.get(entityName, id, lockMode));
	}

	@Override
	public <T> T get(Class<T> entityClass, Object id, LockMode lockMode) {
		return execute(session -> session.get(entityClass, id, lockMode));
	}

	@Override
	public <T> T get(EntityGraph<T> graph, GraphSemantic graphSemantic, Object id) {
		return execute(session -> session.get(graph, graphSemantic, id));
	}

	@Override
	public <T> T get(EntityGraph<T> graph, GraphSemantic graphSemantic, Object id, LockMode lockMode) {
		return execute(session -> session.get(graph, graphSemantic, id, lockMode));
	}

	@Override
	public <T> List<T> getMultiple(Class<T> entityClass, List<Object> ids) {
		return execute(session -> session.getMultiple(entityClass, ids));
	}

	@Override
	public void refresh(Object entity) {
		execute(session -> {
			session.refresh(entity);
			return null;
		});
	}

	@Override
	public void refresh(String entityName, Object entity) {
		execute(session -> {
			session.refresh(entityName, entity);
			return null;
		});
	}

	@Override
	public void refresh(Object entity, LockMode lockMode) {
		execute(session -> {
			session.refresh(entity, lockMode);
			return null;
		});
	}

	@Override
	public void refresh(String entityName, Object entity, LockMode lockMode) {
		execute(session -> {
			session.refresh(entityName, entity, lockMode);
			return null;
		});
	}

	@Override
	public void fetch(Object association) {
		execute(session -> {
			session.fetch(association);
			return null;
		});
	}

	@Override
	public Object getIdentifier(Object entity) {
		return execute(session -> session.getIdentifier(entity));
	}

	@Override
	public Query<?> createQuery(String queryString) {
		return execute(session -> session.createQuery(queryString));
	}

	@Override
	public <R> Query<R> createQuery(String queryString, Class<R> resultClass) {
		return execute(session -> session.createQuery(queryString, resultClass));
	}

	@Override
	public <R> Query<R> createQuery(TypedQueryReference<R> typedQueryReference) {
		return execute(session -> session.createQuery(typedQueryReference));
	}

	@Override
	public <R> Query<R> createQuery(CriteriaQuery<R> criteriaQuery) {
		return execute(session -> session.createQuery(criteriaQuery));
	}

	@Override
	public Query<?> createQuery(CriteriaUpdate<?> updateQuery) {
		return execute(session -> session.createQuery(updateQuery));
	}

	@Override
	public Query<?> createQuery(CriteriaDelete<?> deleteQuery) {
		return execute(session -> session.createQuery(deleteQuery));
	}

	@Override
	public NativeQuery<?> createNativeQuery(String sqlString) {
		return execute(session -> session.createNativeQuery(sqlString));
	}

	@Override
	public <R> NativeQuery<R> createNativeQuery(String sqlString, Class<R> resultClass) {
		return execute(session -> session.createNativeQuery(sqlString, resultClass));
	}

	@Override
	public <R> NativeQuery<R> createNativeQuery(String sqlString, Class<R> resultClass, String tableAlias) {
		return execute(session -> session.createNativeQuery(sqlString, resultClass, tableAlias));
	}

	@Override
	public NativeQuery<?> createNativeQuery(String sqlString, String resultSetMappingName) {
		return execute(session -> session.createNativeQuery(sqlString, resultSetMappingName));
	}

	@Override
	public <R> NativeQuery<R> createNativeQuery(String sqlString, String resultSetMappingName, Class<R> resultClass) {
		return execute(session -> session.createNativeQuery(sqlString, resultSetMappingName, resultClass));
	}

	@Override
	public SelectionQuery<?> createSelectionQuery(String hqlString) {
		return execute(session -> session.createSelectionQuery(hqlString));
	}

	@Override
	public <R> SelectionQuery<R> createSelectionQuery(String hqlString, Class<R> resultType) {
		return execute(session -> session.createSelectionQuery(hqlString, resultType));
	}

	@Override
	public <R> SelectionQuery<R> createSelectionQuery(CriteriaQuery<R> criteria) {
		return execute(session -> session.createSelectionQuery(criteria));
	}

	@Override
	public MutationQuery createMutationQuery(String hqlString) {
		return execute(session -> session.createMutationQuery(hqlString));
	}

	@Override
	public MutationQuery createMutationQuery(CriteriaUpdate<?> updateQuery) {
		return execute(session -> session.createMutationQuery(updateQuery));
	}

	@Override
	public MutationQuery createMutationQuery(CriteriaDelete<?> deleteQuery) {
		return execute(session -> session.createMutationQuery(deleteQuery));
	}

	@Override
	public MutationQuery createMutationQuery(JpaCriteriaInsertSelect<?> insertSelect) {
		return execute(session -> session.createMutationQuery(insertSelect));
	}

	@Override
	public MutationQuery createMutationQuery(JpaCriteriaInsert<?> insert) {
		return execute(session -> session.createMutationQuery(insert));
	}

	@Override
	public MutationQuery createNativeMutationQuery(String sqlString) {
		return execute(session -> session.createNativeMutationQuery(sqlString));
	}

	@Override
	public Query<?> createNamedQuery(String name) {
		return execute(session -> session.createNamedQuery(name));
	}

	@Override
	public <R> Query<R> createNamedQuery(String name, Class<R> resultClass) {
		return execute(session -> session.createNamedQuery(name, resultClass));
	}

	@Override
	public SelectionQuery<?> createNamedSelectionQuery(String name) {
		return execute(session -> session.createNamedSelectionQuery(name));
	}

	@Override
	public <R> SelectionQuery<R> createNamedSelectionQuery(String name, Class<R> resultType) {
		return execute(session -> session.createNamedSelectionQuery(name, resultType));
	}

	@Override
	public MutationQuery createNamedMutationQuery(String name) {
		return execute(session -> session.createNamedMutationQuery(name));
	}

	@Override
	public Query<?> getNamedQuery(String queryName) {
		return execute(session -> session.getNamedQuery(queryName));
	}

	@Override
	public NativeQuery<?> getNamedNativeQuery(String name) {
		return execute(session -> session.getNamedNativeQuery(name));
	}

	@Override
	public NativeQuery<?> getNamedNativeQuery(String name, String resultSetMapping) {
		return execute(session -> session.getNamedNativeQuery(name, resultSetMapping));
	}

	@Nullable
	@Override
	public StatelessSession getCurrentSession() {
		return StatelessSessionUtils.doGetTransactionalStatelessSession(sessionFactory, dataSource);
	}

	@Override
	public String toString() {
		return "StatelessSession proxy for sessionFactory [%s]".formatted(this.sessionFactory);
	}

	private <R> R execute(Function<StatelessSession, R> function) {
		StatelessSession session = StatelessSessionUtils.doGetTransactionalStatelessSession(sessionFactory, dataSource);
		boolean needImmediatelyClose = false;
		if (session == null) {
			session = sessionFactory.openStatelessSession();
			needImmediatelyClose = true;
		}

		try {
			R result = function.apply(session);
			if (result instanceof NativeQuery<?> nativeQuery) {
				needImmediatelyClose = false;
				return ((R) new NativeQueryProxy<>(nativeQuery, session));
			}

			if (result instanceof Query<?> query) {
				needImmediatelyClose = false;
				return ((R) new QueryProxy<>(query, session));
			}

			if (result instanceof SelectionQuery<?> selectionQuery) {
				needImmediatelyClose = false;
				return ((R) new SelectionQueryProxy<>(selectionQuery, session));
			}

			if (result instanceof MutationQuery mutationQuery) {
				needImmediatelyClose = false;
				return ((R) new MutationQueryProxy(mutationQuery, session));
			}

			return result;
		} finally {
			if (needImmediatelyClose) {
				StatelessSessionUtils.closeStatelessSession(session);
			}
		}
	}
}
