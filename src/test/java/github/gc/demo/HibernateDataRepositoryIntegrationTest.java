package github.gc.demo;

import github.gc.demo.config.TestHibernateDataConfiguration;
import github.gc.demo.model.TestModel;
import github.gc.demo.repository.TestRepository;
import github.gc.demo.service.TestService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Hibernate Data Repository 集成测试
 */
@SpringBootTest
@ContextConfiguration(classes = TestHibernateDataConfiguration.class)
public class HibernateDataRepositoryIntegrationTest {

    @Autowired
    private TestRepository testRepository;

    @Autowired
    private TestService testService;

    @Test
    @Transactional
    public void testRepositoryBasicOperations() {
        // 创建测试数据
        TestModel testModel = new TestModel();
        testModel.setId(1L);
        testModel.setName("Test User");
        testModel.setAge(25);

        // 保存
        TestModel saved = testRepository.save(testModel);
        assertNotNull(saved);
        assertEquals("Test User", saved.getName());

        // 查找
        Optional<TestModel> found = testRepository.findById(1L);
        assertTrue(found.isPresent());
        assertEquals("Test User", found.get().getName());

        // 删除
        testRepository.deleteById(1L);
        Optional<TestModel> deleted = testRepository.findById(1L);
        assertFalse(deleted.isPresent());
    }

    @Test
    @Transactional
    public void testCustomQueries() {
        // 创建测试数据
        TestModel testModel1 = new TestModel();
        testModel1.setId(1L);
        testModel1.setName("Alice");
        testModel1.setAge(25);

        TestModel testModel2 = new TestModel();
        testModel2.setId(2L);
        testModel2.setName("Bob");
        testModel2.setAge(30);

        testRepository.save(testModel1);
        testRepository.save(testModel2);

        // 测试自定义查询
        List<TestModel> aliceResults = testRepository.byName("Alice");
        assertEquals(1, aliceResults.size());
        assertEquals("Alice", aliceResults.get(0).getName());

        // 测试 SelectionQuery
        List<TestModel> allResults = testRepository.selectionQuery().getResultList();
        assertEquals(2, allResults.size());
    }

    @Test
    @Transactional
    public void testServiceLayer() {
        // 创建测试数据
        TestModel testModel = new TestModel();
        testModel.setId(1L);
        testModel.setName("Service Test");
        testModel.setAge(35);

        // 通过服务层保存
        TestModel saved = testService.save(testModel);
        assertNotNull(saved);

        // 通过服务层查找
        Optional<TestModel> found = testService.findById(1L);
        assertTrue(found.isPresent());
        assertEquals("Service Test", found.get().getName());

        // 通过服务层检查存在性
        boolean exists = testService.existsById(1L);
        assertTrue(exists);

        // 通过服务层删除
        testService.deleteById(1L);
        boolean existsAfterDelete = testService.existsById(1L);
        assertFalse(existsAfterDelete);
    }
}
