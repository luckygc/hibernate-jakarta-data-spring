package github.gc.demo.config;

import github.gc.jakartadata.annotation.EnableJakartaDataRepositories;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.cfg.Configuration;
import org.hibernate.tool.schema.Action;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * Hibernate Data Repository 配置示例
 * 展示如何配置和使用新的集成方案
 */
@org.springframework.context.annotation.Configuration
@EnableJakartaDataRepositories(basePackages = "github.gc.demo.repository")
@EnableTransactionManagement
@ComponentScan(basePackages = "github.gc.demo")
public class HibernateDataConfiguration {

    /**
     * 配置数据源
     */
    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.postgresql.Driver");
        dataSource.setUrl("jdbc:postgresql://localhost:5432/test");
        dataSource.setUsername("test");
        dataSource.setPassword("test");
        return dataSource;
    }

    /**
     * 配置 Hibernate SessionFactory
     */
    @Bean
    public SessionFactory sessionFactory(DataSource dataSource) {
        Configuration configuration = new Configuration();
        
        // 基本配置
        Properties properties = new Properties();
        properties.setProperty(AvailableSettings.DIALECT, "org.hibernate.dialect.PostgreSQLDialect");
        properties.setProperty(AvailableSettings.HBM2DDL_AUTO, Action.UPDATE.getExternalHbm2ddlName());
        properties.setProperty(AvailableSettings.SHOW_SQL, "true");
        properties.setProperty(AvailableSettings.FORMAT_SQL, "true");
        properties.setProperty(AvailableSettings.USE_SQL_COMMENTS, "true");

        // 数据源配置
        properties.setProperty(AvailableSettings.URL, "jdbc:postgresql://localhost:5432/test");
        properties.setProperty(AvailableSettings.USER, "test");
        properties.setProperty(AvailableSettings.PASS, "test");
        properties.setProperty(AvailableSettings.DRIVER, "org.postgresql.Driver");

        // 连接池配置 - 使用简单的连接池
        properties.setProperty(AvailableSettings.CONNECTION_PROVIDER,
            "org.hibernate.engine.jdbc.connections.internal.DriverManagerConnectionProviderImpl");
        
        // 性能优化
        properties.setProperty(AvailableSettings.STATEMENT_BATCH_SIZE, "25");
        properties.setProperty(AvailableSettings.ORDER_INSERTS, "true");
        properties.setProperty(AvailableSettings.ORDER_UPDATES, "true");
        
        configuration.setProperties(properties);
        
        // 扫描实体类
        configuration.addPackage("github.gc.demo.model");
        
        // 手动添加实体类（如果需要）
        // configuration.addAnnotatedClass(TestModel.class);
        
        return configuration.buildSessionFactory();
    }
}
