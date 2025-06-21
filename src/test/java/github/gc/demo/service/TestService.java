package github.gc.demo.service;

import github.gc.demo.model.TestModel;
import github.gc.demo.repository.TestRepository;
import org.hibernate.query.SelectionQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * 测试服务类
 * 展示如何使用新的 Hibernate Data Repository 集成
 */
@Service
public class TestService {

    @Autowired
    private TestRepository testRepository;

    /**
     * 查找所有实体
     */
    @Transactional(readOnly = true)
    public List<TestModel> findAll() {
        return testRepository.findAll().toList();
    }

    /**
     * 根据 ID 查找实体
     */
    @Transactional(readOnly = true)
    public Optional<TestModel> findById(Long id) {
        return testRepository.findById(id);
    }

    /**
     * 根据名称查找实体
     */
    @Transactional(readOnly = true)
    public List<TestModel> findByName(String name) {
        return testRepository.byName(name);
    }

    /**
     * 使用 SelectionQuery 进行查询
     */
    @Transactional(readOnly = true)
    public List<TestModel> findWithSelectionQuery() {
        SelectionQuery<TestModel> query = testRepository.selectionQuery();
        return query.getResultList();
    }

    /**
     * 保存实体
     */
    @Transactional
    public TestModel save(TestModel testModel) {
        return testRepository.save(testModel);
    }

    /**
     * 批量保存实体
     */
    @Transactional
    public List<TestModel> saveAll(List<TestModel> testModels) {
        return testRepository.saveAll(testModels);
    }

    /**
     * 删除实体
     */
    @Transactional
    public void deleteById(Long id) {
        testRepository.deleteById(id);
    }

    /**
     * 检查实体是否存在
     */
    @Transactional(readOnly = true)
    public boolean existsById(Long id) {
        return testRepository.findById(id).isPresent();
    }

    /**
     * 统计实体数量
     */
    @Transactional(readOnly = true)
    public long count() {
        return testRepository.findAll().toList().size();
    }
}
