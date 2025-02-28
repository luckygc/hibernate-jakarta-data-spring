package github.gc.jakartadata.repository;

import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

public class RepositoryScannerConfigurer
		implements BeanDefinitionRegistryPostProcessor, InitializingBean, ApplicationContextAware {

	private static final Logger log = LoggerFactory.getLogger(RepositoryScannerConfigurer.class);

	private ApplicationContext applicationContext;
	private String basePackage;

	@Override
	public void postProcessBeanDefinitionRegistry(@NonNull BeanDefinitionRegistry registry) throws BeansException {
		Environment environment = getEnvironment();
		var scanner = new ClassPathRepositoryScanner(registry, environment);
		scanner.registerDefaultFilters();
		scanner.scan(StringUtils.tokenizeToStringArray(this.basePackage,
				ConfigurableApplicationContext.CONFIG_LOCATION_DELIMITERS));
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		Assert.notNull(this.basePackage, "Property 'basePackage' is required");
	}

	@Override
	public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	private Environment getEnvironment() {
		return this.applicationContext.getEnvironment();
	}

	public ApplicationContext getApplicationContext() {
		return applicationContext;
	}

	public String getBasePackage() {
		return basePackage;
	}

	public void setBasePackage(String basePackage) {
		this.basePackage = basePackage;
	}
}
