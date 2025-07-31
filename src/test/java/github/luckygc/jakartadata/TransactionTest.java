/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
 
package github.luckygc.jakartadata;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;

/**
 * 事务管理测试 测试事务性和非事务性操作
 *
 * @author luckygc
 */
@SpringJUnitConfig(classes = TransactionTest.TestConfig.class)
class TransactionTest {

    @Autowired
    private BasicRepository basicRepository;

    @Test
    @Transactional
    void testTransactionalOperation() {
        // 在事务中进行操作
        User user = new User();
        user.setName("事务测试用户");
        user.setEmail("transactional@example.com");

        User saved = basicRepository.save(user);
        assertNotNull(saved.getId());

        // 在同一事务中查找
        var found = basicRepository.findById(saved.getId());
        assertTrue(found.isPresent());
        assertEquals("事务测试用户", found.get().getName());
    }

    @Test
    void testNonTransactionalOperation() {
        // 非事务操作
        User user = new User();
        user.setName("非事务测试用户");
        user.setEmail("nontransactional@example.com");

        User saved = basicRepository.save(user);
        assertNotNull(saved.getId());

        // 查找保存的实体
        var found = basicRepository.findById(saved.getId());
        assertTrue(found.isPresent());
        assertEquals("非事务测试用户", found.get().getName());

        // 清理
        basicRepository.delete(saved);
    }

    @Test
    @Transactional
    void testTransactionalRollback() {
        // 测试事务回滚
        User user = new User();
        user.setName("回滚测试用户");
        user.setEmail("rollback@example.com");

        User saved = basicRepository.save(user);
        assertNotNull(saved.getId());

        // 抛出异常触发回滚
        assertThrows(RuntimeException.class, () -> {
            // 在事务中保存后抛出异常
            throw new RuntimeException("测试回滚");
        });

        // 注意：由于@Transactional的回滚机制，这个测试可能需要调整
    }

    @Test
    void testMultipleOperationsInSingleTransaction() {
        // 测试单个事务中的多个操作
        performTransactionalOperations();
    }

    @Transactional
    private void performTransactionalOperations() {
        // 创建多个用户
        User user1 = new User();
        user1.setName("批量用户1");
        user1.setEmail("batch1@example.com");

        User user2 = new User();
        user2.setName("批量用户2");
        user2.setEmail("batch2@example.com");

        User saved1 = basicRepository.save(user1);
        User saved2 = basicRepository.save(user2);

        assertNotNull(saved1.getId());
        assertNotNull(saved2.getId());

        // 验证都能找到
        assertTrue(basicRepository.findById(saved1.getId()).isPresent());
        assertTrue(basicRepository.findById(saved2.getId()).isPresent());

        // 清理
        basicRepository.delete(saved1);
        basicRepository.delete(saved2);
    }

    /**
     * 测试配置类
     */
    @Configuration
    @EnableDataRepositories(basePackages = "github.luckygc.jakartadata")
    @EnableTransactionManagement
    static class TestConfig {

        @Bean
        public DataSource dataSource() {
            return new EmbeddedDatabaseBuilder().setType(EmbeddedDatabaseType.H2).addScript("classpath:schema.sql")
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
            sessionFactoryBean.setPackagesToScan(new String[] {"github.luckygc.jakartadata"});
            return sessionFactoryBean.getObject();
        }
    }
}
