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

import static org.junit.jupiter.api.Assertions.assertFalse;
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
import jakarta.data.exceptions.OptimisticLockingFailureException;

import javax.sql.DataSource;

/**
 * 异常处理测试 测试各种异常情况的处理
 *
 * @author luckygc
 */
@SpringJUnitConfig(classes = ExceptionHandlingTest.TestConfig.class)
class ExceptionHandlingTest {

    @Autowired
    private BasicRepository basicRepository;

    @Test
    void testRepositoryInjection() {
        // 验证Repository已正确注入
        assertNotNull(basicRepository);
        assertNotNull(basicRepository.getClass());
        assertTrue(basicRepository.getClass().getName().contains("Proxy"));
    }

    @Test
    void testFindNonExistentEntity() {
        // 测试查找不存在的实体
        var result = basicRepository.findById(999L);
        assertFalse(result.isPresent());
    }

    @Test
    void testSaveNullEntity() {
        // 测试保存null实体
        assertThrows(Exception.class, () -> {
            basicRepository.save(null);
        });
    }

    @Test
    void testDeleteNonExistentEntity() {
        // 测试删除不存在的实体
        User nonExistentUser = new User();
        nonExistentUser.setId(999L);
        nonExistentUser.setName("不存在的用户");
        nonExistentUser.setEmail("nonexistent@example.com");

        assertThrows(OptimisticLockingFailureException.class, () -> {
            basicRepository.delete(nonExistentUser);
        });
    }

    @Test
    void testInvalidEmailConstraint() {
        // 测试违反唯一约束
        User user1 = new User();
        user1.setName("用户1");
        user1.setEmail("duplicate@example.com");

        User user2 = new User();
        user2.setName("用户2");
        user2.setEmail("duplicate@example.com");

        // 保存第一个用户应该成功
        User saved1 = basicRepository.save(user1);
        assertNotNull(saved1.getId());

        // 保存第二个用户应该失败（邮箱重复）
        assertThrows(Exception.class, () -> {
            basicRepository.save(user2);
        });
    }

    /**
     * 测试配置类
     */
    @Configuration
    @EnableDataRepositories(basePackages = "github.luckygc.jakartadata")
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
