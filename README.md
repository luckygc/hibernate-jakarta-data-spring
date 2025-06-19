# hibernate-jakarta-data-spring
spring集成jakarta data,底层基于hibernate

参考 mybatis-spring,spring-data-jpa,spring-orm

暂时不支持多数据源，只支持本地事务，自动参与spring事务。使用DatasourceTransactionManager即可管理事务

repository方法通过代理管理StatelessSession，有事务时由事务同步关闭session，无事务则在方法结束后立即关闭，返回Query时会延迟关闭。


```java
@Configuration
@RepositoryScan(basePackages = "xxx.xxx")
public class DataConfiguration {

	@Bean
	public SessionFactory sessionFactory(DataSource dataSource) {
		SessionFactoryBean sessionFactoryBean = new SessionFactoryBean();
		sessionFactoryBean.setDataSource(dataSource);
		sessionFactoryBean.setPackagesToScan(new String[]{"github.gc.demo"});
		Properties properties = sessionFactoryBean.getHibernateProperties();
		properties.put(AvailableSettings.SHOW_SQL, "true");
		properties.put(AvailableSettings.FORMAT_SQL, "true");
		properties.put(AvailableSettings.HBM2DDL_AUTO, "update");
		properties.put(AvailableSettings.USE_SQL_COMMENTS, "true");
		return sessionFactoryBean.getObject();
	}

}
```

TODO
- [ ] 异常翻译
- [ ] jta
