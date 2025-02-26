package github.gc;

import github.gc.util.VarHandleUtil;
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

import java.lang.invoke.VarHandle;
import java.util.List;

public class VarHandleTest {

	public static void main(String[] args) throws NoSuchFieldException, IllegalAccessException {
		// 示例类
		// 获取 Test 类中字段 "value" 的 VarHandle
		VarHandle vh = VarHandleUtil.getVarHandle(TestRepo.class, "session", StatelessSession.class);
		TestRepo test = new TestRepo(null);
		// 读取字段值
		StatelessSession session = (StatelessSession) vh.get(test);
		System.out.println("初始值：" + session);
		// 更新字段值
		vh.set(test, new StatelessSession() {
			@Override
			public void close() {

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
			public String getTenantIdentifier() {
				return "";
			}

			@Override
			public Object getTenantIdentifierValue() {
				return null;
			}

			@Override
			public CacheMode getCacheMode() {
				return null;
			}

			@Override
			public void setCacheMode(CacheMode cacheMode) {

			}

			@Override
			public boolean isOpen() {
				return false;
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
				return null;
			}

			@Override
			public void joinTransaction() {

			}

			@Override
			public boolean isJoinedToTransaction() {
				return false;
			}

			@Override
			public ProcedureCall getNamedProcedureCall(String name) {
				return null;
			}

			@Override
			public ProcedureCall createStoredProcedureCall(String procedureName) {
				return null;
			}

			@Override
			public ProcedureCall createStoredProcedureCall(String procedureName, Class<?>... resultClasses) {
				return null;
			}

			@Override
			public ProcedureCall createStoredProcedureCall(String procedureName, String... resultSetMappings) {
				return null;
			}

			@Override
			public ProcedureCall createNamedStoredProcedureQuery(String name) {
				return null;
			}

			@Override
			public ProcedureCall createStoredProcedureQuery(String procedureName) {
				return null;
			}

			@Override
			public ProcedureCall createStoredProcedureQuery(String procedureName, Class<?>... resultClasses) {
				return null;
			}

			@Override
			public ProcedureCall createStoredProcedureQuery(String procedureName, String... resultSetMappings) {
				return null;
			}

			@Override
			public Integer getJdbcBatchSize() {
				return 0;
			}

			@Override
			public void setJdbcBatchSize(Integer jdbcBatchSize) {

			}

			@Override
			public HibernateCriteriaBuilder getCriteriaBuilder() {
				return null;
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
			public <R> NativeQuery<R> createNativeQuery(String sqlString, String resultSetMappingName,
					Class<R> resultClass) {
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
		});
		System.out.println("更新后：" + test.getSession());
	}
}
