package github.gc.jakartadata.annotation;

import github.gc.jakartadata.repository.RepositoryScannerConfigurer;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class RepositoryScannerRegistry implements ImportBeanDefinitionRegistrar {

	@Override
	public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata,
			@NonNull BeanDefinitionRegistry registry) {
		var repositoryScanAttrs = AnnotationAttributes.fromMap(
				importingClassMetadata.getAnnotationAttributes(RepositoryScan.class.getName()));
		if (repositoryScanAttrs != null) {
			registerBeanDefinitions(importingClassMetadata, repositoryScanAttrs, registry,
					generateBaseBeanName(importingClassMetadata));
		}
	}

	void registerBeanDefinitions(AnnotationMetadata annoMeta, AnnotationAttributes annoAttrs,
			BeanDefinitionRegistry registry, String beanName) {

		var builder = BeanDefinitionBuilder.genericBeanDefinition(RepositoryScannerConfigurer.class);

		List<String> basePackages = Arrays.stream(annoAttrs.getStringArray("basePackages"))
				.filter(StringUtils::hasText)
				.collect(Collectors.toList());

		if (basePackages.isEmpty()) {
			basePackages.add(getDefaultBasePackage(annoMeta));
		}

		builder.addPropertyValue("basePackage", StringUtils.collectionToCommaDelimitedString(basePackages));

		registry.registerBeanDefinition(beanName, builder.getBeanDefinition());
	}

	private static String generateBaseBeanName(AnnotationMetadata importingClassMetadata) {
		return importingClassMetadata.getClassName() + "#" + RepositoryScannerRegistry.class.getSimpleName() + "#"
				+ 0;
	}

	private static String getDefaultBasePackage(AnnotationMetadata importingClassMetadata) {
		return ClassUtils.getPackageName(importingClassMetadata.getClassName());
	}
}
