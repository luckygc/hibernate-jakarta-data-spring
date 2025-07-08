# Hibernate Jakarta Data Spring Integration

**数据访问层解决方案** - Hibernate Data Repository 与 Spring 框架的集成

[![Java](https://img.shields.io/badge/Java-17+-orange.svg)](https://openjdk.java.net/)
[![Hibernate](https://img.shields.io/badge/Hibernate-7.0.5.Final-green.svg)](https://hibernate.org/)
[![Spring](https://img.shields.io/badge/Spring-6.x-brightgreen.svg)](https://spring.io/)
[![Jakarta Data](https://img.shields.io/badge/Jakarta%20Data-1.0.1-blue.svg)](https://jakarta.ee/specifications/data/)

## Hibernate Data Repositories特性

- **安全** 编译时检查，避免运行时错误
- **HQL** - 比JPQL更简洁强大，Record投影无需new关键字
- **方法参数派生查询** - 比基于方法名的查询更简洁
- **动态查询DSL** - 使用Hibernate 7.x的全新Restriction DSL实现动态查询
- **无状态** - 没有级联操作，没有懒加载，没有只读事务，同步操作，没有事务后写


## 🚀 快速开始

### 1. 添加依赖,添加注解处理器

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>com.github.luckygc</groupId>
        <artifactId>hibernate-jakarta-data-spring</artifactId>
        <version>1.0.0</version>
    </dependency>
</dependencies>

<build>
<plugins>
    <plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-compiler-plugin</artifactId>
        <version>${maven-compiler-plugin.version}</version>
        <configuration>
            <compilerArgs>
                <arg>-parameters</arg>
            </compilerArgs>
            <source>${java.version}</source>
            <target>${java.version}</target>
            <encoding>${project.build.sourceEncoding}</encoding>
            <annotationProcessorPaths>
                <path>
                    <groupId>org.projectlombok</groupId>
                    <artifactId>lombok</artifactId>
                    <version>${lombok.version}</version>
                </path>
                <path>
                    <groupId>org.hibernate.orm</groupId>
                    <artifactId>hibernate-processor</artifactId>
                    <version>${hibernate.version}</version>
                </path>
            </annotationProcessorPaths>
        </configuration>
    </plugin>
    
</plugins>
</build>
```

### 2. 配置类

```java
@Configuration
@EnableJakartaDataRepositories(basePackages = "com.example.repository")
public class HibernateDataConfiguration {

    @Bean
    public SessionFactory sessionFactory(DataSource dataSource) {
        SessionFactoryBean sessionFactoryBean = new SessionFactoryBean();
        sessionFactoryBean.setDataSource(dataSource);
        sessionFactoryBean.setPackagesToScan("com.example.entity");
        return sessionFactoryBean.getObject();
    }
}
```

---

**📖 更多信息**
- [Jakarta Data 规范](https://jakarta.ee/specifications/data/)
- [Hibernate Data Repositories 文档](https://hibernate.org/repositories/)
- [Spring Framework 文档](https://docs.spring.io/spring-framework/docs/current/reference/html/)
