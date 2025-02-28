package github.gc.demo;

import github.gc.demo.model.TestModel;
import github.gc.demo.model.TestModel_;
import github.gc.demo.repository.TestRepository;
import org.hibernate.SessionFactory;
import org.hibernate.query.SelectionQuery;
import org.hibernate.query.range.Range;
import org.hibernate.query.restriction.Restriction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@SpringBootApplication
@RestController
@RequestMapping("/")
public class TestApplication {

	@Autowired
	private TestRepository testRepository;
	@Autowired
	private TransactionTemplate transactionTemplate;
	@Autowired
	private SessionFactory sessionFactory;

	public static void main(String[] args) {
		SpringApplication.run(TestApplication.class, args);
	}

	@GetMapping(value = "test")
	public String test() {
		TestModel testModel = new TestModel();
		testModel.setName("test");
		//		testRepository.insert(testModel);
		//		transactionTemplate.executeWithoutResult(status -> {
		//			List<TestModel> resultList = testRepository.returnSelectionQuery().getResultList();
		//			System.out.println(resultList);
		//		});

		//		sessionFactory.inStatelessSession(session -> {
		//			SelectionQuery<TestModel> testModelSelectionQuery =session.createSelectionQuery("from TestModel ", TestModel.class);
		//			testModelSelectionQuery.setFetchSize(100);
		//			testModelSelectionQuery.setComment("我是测试注释");
		//			List<TestModel> resultList = testModelSelectionQuery.getResultList();
		//			for (int i = 0; i < 20; i++) {
		//				testModelSelectionQuery.getResultList();
		//			}
		//			System.out.println(resultList);
		//		});

		transactionTemplate.executeWithoutResult(status -> {
			SelectionQuery<TestModel> testModelSelectionQuery = testRepository.selectionQuery();
			testModelSelectionQuery.setFetchSize(100);
			testModelSelectionQuery.setComment("我是测试注释");
			List<TestModel> resultList = testModelSelectionQuery.getResultList();
			System.out.println(resultList);

			List<TestModel> test = testRepository.byName(Range.prefix("tes"));
			System.out.println(test);

			List<TestModel> test2 = testRepository.byIdRange(Range.greaterThan(0L));
			System.out.println(test2);

			List<TestModel> test3 = testRepository.byIdRange(Range.singleValue(1L));
			System.out.println(test3);

			List<TestRepository.ByNameDto> byNameDtos = testRepository.dTobyName();
			System.out.println(byNameDtos);
		});

		return "test";
	}
}
