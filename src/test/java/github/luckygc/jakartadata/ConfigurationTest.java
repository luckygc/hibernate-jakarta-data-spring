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

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import github.luckygc.jakartadata.annotation.EnableDataRepositories;
import github.luckygc.jakartadata.provider.hibernate.SessionFactoryBean;

import org.hibernate.SessionFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

/**
 * 配置测试 测试Spring配置和Bean注册
 *
 * @author luckygc
 */
@SpringJUnitConfig(classes = ConfigurationTest.TestConfig.class)
class ConfigurationTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private BasicRepository basicRepository;

    @Test
    void testApplicationContextLoaded() {
        // 验证应用上下文已正确加载
        assertNotNull(applicationContext);
    }

    @Test
    void testRepositoryBeanRegistered() {
        // 验证Repository Bean已注册
        assertTrue(applicationContext.containsBean("basicRepository"));

        BasicRepository bean = applicationContext.getBean("basicRepository", BasicRepository.class);
        assertNotNull(bean);
        assertSame(basicRepository, bean);
    }

    @Test
    void testSessionFactoryBeanRegistered() {
        // 验证SessionFactory Bean已注册
        assertTrue(applicationContext.containsBean("sessionFactory"));

        SessionFactory sessionFactory = applicationContext.getBean("sessionFactory", SessionFactory.class);
        assertNotNull(sessionFactory);
        assertFalse(sessionFactory.isClosed());
    }

    @Test
    void testDataSourceBeanRegistered() {
        // 验证DataSource Bean已注册
        assertTrue(applicationContext.containsBean("dataSource"));

        DataSource dataSource = applicationContext.getBean("dataSource", DataSource.class);
        assertNotNull(dataSource);
    }

    @Test
    void testTransactionManagerBeanRegistered() {
        // 验证事务管理器Bean已注册
        assertTrue(applicationContext.containsBean("transactionManager"));

        PlatformTransactionManager transactionManager = applicationContext.getBean("transactionManager",
                PlatformTransactionManager.class);
        assertNotNull(transactionManager);
        assertTrue(transactionManager instanceof DataSourceTransactionManager);
    }

    @Test
    void testRepositoryProxyCreation() {
        // 验证Repository代理创建
        assertNotNull(basicRepository);

        // 验证这是一个代理对象
        String className = basicRepository.getClass().getName();
        assertTrue(className.contains("Proxy") || className.contains("$"));

        // 验证代理实现了正确的接口
        assertTrue(BasicRepository.class.isAssignableFrom(basicRepository.getClass()));
    }

    @Test
    void testRepositoryFunctionality() {
        // 验证Repository基本功能
        assertNotNull(basicRepository);

        // 测试基本操作不抛出异常
        assertDoesNotThrow(() -> {
            var result = basicRepository.findById(1L);
            assertNotNull(result);
        });
    }

    @Test
    void testBeanScopes() {
        // 验证Bean作用域
        BasicRepository repo1 = applicationContext.getBean("basicRepository", BasicRepository.class);
        BasicRepository repo2 = applicationContext.getBean("basicRepository", BasicRepository.class);

        // Repository应该是单例
        assertSame(repo1, repo2);

        SessionFactory sf1 = applicationContext.getBean("sessionFactory", SessionFactory.class);
        SessionFactory sf2 = applicationContext.getBean("sessionFactory", SessionFactory.class);

        // SessionFactory应该是单例
        assertSame(sf1, sf2);
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
