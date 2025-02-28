package github.gc.demo;

import jakarta.data.repository.CrudRepository;
import jakarta.data.repository.Query;
import jakarta.data.repository.Repository;
import org.hibernate.query.SelectionQuery;

@Repository
public interface TestRepository extends CrudRepository<TestModel, Long> {

	@Query("from TestModel")
	SelectionQuery<TestModel> returnSelectionQuery();
}
