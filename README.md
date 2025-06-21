# Hibernate Jakarta Data Spring Integration

这是一个全新的 Hibernate Data Repository 与 Spring 框架集成的实现方案。该方案直接使用 Hibernate 的原生 API，不依赖 Spring Data JPA，提供了更好的性能和更灵活的控制。

## 使用方法

### 1. 配置类

```java
@Configuration
@EnableHibernateDataRepositories(basePackages = "github.gc.demo.repository")
@EnableTransactionManagement
public class HibernateDataConfiguration {

    @Bean
    public DataSource dataSource() {
        // 配置数据源
    }

    @Bean
    public SessionFactory sessionFactory(DataSource dataSource) {
        // 配置 Hibernate SessionFactory
    }
}
```

### 2. Repository 接口

```java
@Repository
public interface UserRepository extends CrudRepository<User, Long> {

    @Find
    List<User> byName(String name);

    @Query("from User where age > :age")
    SelectionQuery<User> findByAgeGreaterThan(int age);
}
```

### 3. 服务类

```java
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<User> findByName(String name) {
        return userRepository.byName(name);
    }
}
```

## 核心特性

- **原生 Hibernate 集成** - 直接使用 Hibernate 7.x API
- **高性能** - 使用 StatelessSession，适合批量操作
- **事务集成** - 完全兼容 Spring 声明式事务管理
- **延迟资源关闭** - 智能的 Session 生命周期管理
- **注解驱动** - 简化配置，易于使用

详细文档请参考：[HIBERNATE_DATA_SPRING_INTEGRATION.md](HIBERNATE_DATA_SPRING_INTEGRATION.md)
