package github.gc.demo.repository;

import github.gc.demo.model.TestModel;
import github.gc.demo.model._TestModel;
import jakarta.data.repository.*;
import org.hibernate.annotations.OptimisticLock;
import org.hibernate.query.SelectionQuery;
import org.hibernate.query.range.Range;

import java.util.List;

@Repository
public interface TestRepository extends CrudRepository<TestModel, Long> {

	@Query("from TestModel")
	SelectionQuery<TestModel> selectionQuery();

	@Find
	List<TestModel> byName(Range<String> name);

	@Find
	@OrderBy(_TestModel.ID)
	List<TestModel> byIdRange(Range<Long> id);

        record ByNameDto(Long primaryId, String modelName){}

	@Query(" select id, name from TestModel order by id asc ")
	List<ByNameDto> dTobyName();
}
