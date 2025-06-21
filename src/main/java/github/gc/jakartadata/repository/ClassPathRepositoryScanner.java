package github.gc.jakartadata.repository;

import jakarta.data.repository.Repository;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.env.Environment;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.util.Assert;

import java.util.Set;

public class ClassPathRepositoryScanner extends ClassPathBeanDefinitionScanner {

	private static final Logger log = LoggerFactory.getLogger(ClassPathRepositoryScanner.class);

	private String basePackages;

	@Override
	protected void registerDefaultFilters() {
		addIncludeFilter(new AnnotationTypeFilter(Repository.class));
	}

	public ClassPathRepositoryScanner(BeanDefinitionRegistry registry, Environment environment) {
		super(registry, true, environment);
	}

	@NonNull
	@Override
	protected Set<BeanDefinitionHolder> doScan(@NonNull String... basePackages) {
		Assert.notNull(basePackages, "basePackages must not be null");

		Set<BeanDefinitionHolder> beanDefinitionHolders = super.doScan(basePackages);
		if (!beanDefinitionHolders.isEmpty()) {
			processBeanDefinitions(beanDefinitionHolders);
		}

		return beanDefinitionHolders;
	}

	@Override
	protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
		return beanDefinition.getMetadata().isInterface() && beanDefinition.getMetadata().isIndependent();
	}

	private void processBeanDefinitions(Set<BeanDefinitionHolder> beanDefinitions) {
		AbstractBeanDefinition definition;
		for (BeanDefinitionHolder holder : beanDefinitions) {
			definition = (AbstractBeanDefinition) holder.getBeanDefinition();

			var beanClassName = definition.getBeanClassName();
			Assert.notNull(beanClassName, "beanClassName must not be null");

			if (log.isDebugEnabled()) {
				log.debug("Creating RepositoryFactoryBean with name '{}' and '{}' repositoryInterface",
						holder.getBeanName(), beanClassName);
			}

			ConstructorArgumentValues constructorArgumentValues = definition.getConstructorArgumentValues();
			constructorArgumentValues.addGenericArgumentValue(beanClassName);

			definition.setBeanClass(JpaRepositoryBean.class);
			definition.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_TYPE);
		}
	}

	public String getBasePackages() {
		return basePackages;
	}

	public void setBasePackages(String basePackages) {
		this.basePackages = basePackages;
	}
}
