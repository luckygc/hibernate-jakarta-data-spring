# Hibernate Jakarta Data Spring Integration

Hibernate Data Repository 与 Spring 框架集成的实现方案。该方案直接使用 Hibernate 的原生 API，不依赖 Spring Data JPA。

**Hibernate Data Repository介绍**
- 可以使用hql，比jpql语法更简洁强大，例如record投影不需要写new关键字。
- 基于方法参数的查询派生方法，比基于方法名称的查询派生方法更简洁。
- 可以使用hibernate 7.x版本 query包下的Restriction dsl实现动态查询。


## 使用方法

### 1. 配置类

```java
@Configuration
@EnableJakartaDataRepositories(basePackages = "github.gc.**.repository")
public class HibernateDataConfiguration {

    @Bean
    public SessionFactory sessionFactory(DataSource dataSource) {
        SessionFactoryBean sessionFactoryBean = new SessionFactoryBean();
        sessionFactoryBean.setDataSource(dataSource);
        sessionFactoryBean.setPackagesToScan(new String[]{"github.gc.**.entity"});
        return sessionFactoryBean.getObject();
    }
}
```
