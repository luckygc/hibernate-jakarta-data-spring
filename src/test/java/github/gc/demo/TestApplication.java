package github.gc.demo;

import jakarta.data.Order;
import jakarta.data.page.Page;
import jakarta.data.page.PageRequest;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
		transactionTemplate.executeWithoutResult(status -> {
			Page<TestModel> all = testRepository.findAll(PageRequest.ofPage(1), Order.by(_TestModel.id.asc()));
			System.out.println(all);
		});

		return "test";
	}
}
