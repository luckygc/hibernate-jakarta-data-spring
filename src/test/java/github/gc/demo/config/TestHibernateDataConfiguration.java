package github.gc.demo.config;

import github.gc.jakartadata.annotation.EnableHibernateDataRepositories;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.cfg.Configuration;
import org.hibernate.tool.schema.Action;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * 测试用的 Hibernate Data Repository 配置
 * 使用 H2 内存数据库
 */
@org.springframework.context.annotation.Configuration
@EnableHibernateDataRepositories(basePackages = "github.gc.demo.repository")
@EnableTransactionManagement
@ComponentScan(basePackages = "github.gc.demo")
public class TestHibernateDataConfiguration {

    /**
     * 配置测试数据源 - H2 内存数据库
     */
    @Bean
    @Primary
    public DataSource testDataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.h2.Driver");
        dataSource.setUrl("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE");
        dataSource.setUsername("sa");
        dataSource.setPassword("");
        return dataSource;
    }

    /**
     * 配置测试用的 Hibernate SessionFactory
     */
    @Bean
    @Primary
    public SessionFactory testSessionFactory(DataSource testDataSource) {
        Configuration configuration = new Configuration();

        // 基本配置
        Properties properties = new Properties();
        properties.setProperty(AvailableSettings.DIALECT, "org.hibernate.dialect.H2Dialect");
        properties.setProperty(AvailableSettings.HBM2DDL_AUTO, Action.CREATE_DROP.getExternalHbm2ddlName());
        properties.setProperty(AvailableSettings.SHOW_SQL, "true");
        properties.setProperty(AvailableSettings.FORMAT_SQL, "true");
        properties.setProperty(AvailableSettings.USE_SQL_COMMENTS, "true");

        // 数据源配置
        properties.setProperty(AvailableSettings.URL, "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE");
        properties.setProperty(AvailableSettings.USER, "sa");
        properties.setProperty(AvailableSettings.PASS, "");
        properties.setProperty(AvailableSettings.DRIVER, "org.h2.Driver");

        // 测试环境优化
        properties.setProperty(AvailableSettings.STATEMENT_BATCH_SIZE, "10");
        properties.setProperty(AvailableSettings.ORDER_INSERTS, "true");
        properties.setProperty(AvailableSettings.ORDER_UPDATES, "true");

        configuration.setProperties(properties);

        // 扫描实体类
        configuration.addPackage("github.gc.demo.model");

        // 手动添加实体类
        configuration.addAnnotatedClass(github.gc.demo.model.TestModel.class);

        return configuration.buildSessionFactory();
    }
}
