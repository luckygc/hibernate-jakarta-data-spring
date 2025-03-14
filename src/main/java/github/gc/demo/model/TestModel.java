package github.gc.demo.model;

import jakarta.persistence.*;

@Entity
@Table(name = "data_test_model")
public class TestModel {

	@Id
//	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	private String name;

	private Integer age;

	public Integer getAge() {
		return age;
	}

	public void setAge(Integer age) {
		this.age = age;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "TestModel{" + "id=" + id + ", name='" + name + '\'' + '}';
	}
}
