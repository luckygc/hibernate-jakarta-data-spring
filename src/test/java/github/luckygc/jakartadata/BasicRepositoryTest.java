package github.luckygc.jakartadata;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import github.luckygc.jakartadata.annotation.EnableDataRepositories;
import github.luckygc.jakartadata.provider.hibernate.SessionFactoryBean;

import org.hibernate.SessionFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import javax.sql.DataSource;

/**
 * 基本Repository测试
 * 只测试基本的CRUD操作
 *
 * @author luckygc
 */
@SpringJUnitConfig(classes = BasicRepositoryTest.TestConfig.class)
@Transactional
class BasicRepositoryTest {

    @Autowired
    private BasicRepository basicRepository;

    @Test
    void testBasicCrudOperations() {
        // 验证Repository已正确注入
        assertNotNull(basicRepository);
        
        // 创建测试实体
        User entity = new User();
        entity.setName("测试实体");
        entity.setEmail("这是一个测试实体");
        
        // 保存实体
        User saved = basicRepository.save(entity);
        assertNotNull(saved.getId());
        assertEquals("测试实体", saved.getName());
        assertEquals("这是一个测试实体", saved.getEmail());
        
        // 查找实体
        Optional<User> found = basicRepository.findById(saved.getId());
        assertTrue(found.isPresent());
        assertEquals(saved.getName(), found.get().getName());
        
        // 更新实体
        saved.setEmail("更新后的描述");
        User updated = basicRepository.save(saved);
        assertEquals("更新后的描述", updated.getEmail());
        
        // 删除实体
        basicRepository.delete(saved);
        assertFalse(basicRepository.findById(saved.getId()).isPresent());
    }

    @Test
    void testSaveAndFind() {
        // 创建测试实体
        User entity = new User();
        entity.setName("保存测试");
        entity.setEmail("用于测试保存和查找");
        
        // 保存实体
        User saved = basicRepository.save(entity);
        assertNotNull(saved.getId());
        
        // 查找实体
        Optional<User> found = basicRepository.findById(saved.getId());
        assertTrue(found.isPresent());
        assertEquals("保存测试", found.get().getName());
    }

    /**
     * 测试配置类
     */
    @Configuration
    @EnableDataRepositories(basePackages = "github.luckygc.jakartadata")
    static class TestConfig {

        @Bean
        public DataSource dataSource() {
            return new EmbeddedDatabaseBuilder()
                    .setType(EmbeddedDatabaseType.H2)
                    .addScript("classpath:schema.sql")
                    .build();
        }

        @Bean
        public PlatformTransactionManager transactionManager(DataSource dataSource) {
            return new DataSourceTransactionManager(dataSource);
        }

        @Bean
        public SessionFactory sessionFactory(DataSource dataSource) {
            SessionFactoryBean sessionFactoryBean = new SessionFactoryBean();
            sessionFactoryBean.setDataSource(dataSource);
            sessionFactoryBean.setPackagesToScan(new String[]{"github.luckygc.jakartadata"});
            return sessionFactoryBean.getObject();
        }
    }

} 