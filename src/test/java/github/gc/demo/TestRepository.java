package github.gc.demo;

import jakarta.data.repository.CrudRepository;
import jakarta.data.repository.Repository;

@Repository
public interface TestRepository extends CrudRepository<TestModel, Long> {
}
