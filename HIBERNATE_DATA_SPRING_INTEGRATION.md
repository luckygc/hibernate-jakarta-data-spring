# Hibernate Data Repository 与 Spring 集成方案

## 概述

这是一个全新的 Hibernate Data Repository 与 Spring 框架集成的实现方案。该方案直接使用 Hibernate 的原生 API，不依赖 Spring Data JPA，提供了更好的性能和更灵活的控制。

## 核心特性

### 1. 注解驱动的配置
- `@EnableHibernateDataRepositories` - 启用 Repository 扫描
- `@HibernateDataRepository` - 标记 Repository 接口
- 支持包扫描和自动注册

### 2. 原生 Hibernate 集成
- 直接使用 Hibernate SessionFactory 和 StatelessSession
- 支持 Hibernate 7.x 的最新特性
- 完全兼容 Jakarta Data API

### 3. Spring 事务集成
- 自动集成 Spring 事务管理
- 支持声明式事务（@Transactional）
- 智能的 Session 生命周期管理

### 4. 延迟资源关闭
- 对于返回 Query 类型的方法，实现延迟关闭机制
- 避免过早关闭 Session 导致的 LazyInitializationException
- 在查询执行完成后自动清理资源

## 使用方法

### 1. 配置类

```java
@Configuration
@EnableHibernateDataRepositories(basePackages = "com.example.repository")
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
    
    @Query("select count(*) from User")
    long countUsers();
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
    
    @Transactional
    public User save(User user) {
        return userRepository.save(user);
    }
}
```

## 架构设计

### 核心组件

1. **注解和配置**
   - `EnableHibernateDataRepositories` - 启用注解
   - `HibernateDataRepositoryConfigurationExtension` - 配置扩展
   - `HibernateDataRepositoryBeanDefinitionRegistrar` - Bean 定义注册器

2. **工厂和代理**
   - `HibernateDataRepositoryFactoryBean` - Repository 工厂 Bean
   - `HibernateDataRepositoryProxyFactory` - 代理工厂
   - `HibernateDataRepositoryInvocationHandler` - 调用处理器

3. **工具和辅助**
   - `HibernateRepositoryUtils` - Repository 工具类
   - `HibernateSessionUtils` - Session 管理工具
   - `QueryWrapper` - Query 包装器

### 工作流程

1. **启动时**：
   - 扫描指定包下的 Repository 接口
   - 为每个接口创建 FactoryBean
   - 注册到 Spring 容器

2. **运行时**：
   - 通过动态代理拦截方法调用
   - 获取或创建 Hibernate StatelessSession
   - 调用 Hibernate 生成的实现类
   - 管理 Session 生命周期

3. **事务集成**：
   - 检测 Spring 事务状态
   - 复用事务中的 Session
   - 在事务结束时清理资源

## 优势

### 1. 性能优势
- 直接使用 Hibernate 原生 API，减少抽象层开销
- 使用 StatelessSession，适合批量操作和只读查询
- 智能的资源管理，避免不必要的连接创建

### 2. 功能完整
- 完全支持 Jakarta Data API 规范
- 支持 Hibernate 的高级特性
- 与 Spring 事务无缝集成

### 3. 易于使用
- 注解驱动的配置，简化设置
- 与现有 Spring 应用无缝集成
- 清晰的错误处理和日志记录

### 4. 可扩展性
- 模块化设计，易于扩展
- 支持自定义配置和行为
- 良好的测试支持

## 注意事项

1. **Hibernate Processor**：确保正确配置 Hibernate Processor 以生成实现类
2. **事务管理**：建议在服务层使用 @Transactional 注解
3. **Query 对象**：返回 Query 类型的方法会延迟关闭 Session，注意内存使用
4. **错误处理**：注意捕获和处理 Hibernate 相关异常

## 与现有方案的区别

- **不使用 Spring Data JPA**：直接基于 Hibernate 实现
- **更好的性能**：减少抽象层，使用 StatelessSession
- **更灵活的控制**：可以直接访问 Hibernate 的高级特性
- **简化的配置**：不需要复杂的 ORM 配置

这个方案提供了一个现代化、高性能的 Hibernate Data Repository 与 Spring 集成解决方案，适合需要高性能和灵活性的企业级应用。
