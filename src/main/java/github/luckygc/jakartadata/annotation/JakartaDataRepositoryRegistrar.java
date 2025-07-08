package github.luckygc.jakartadata.annotation;

import github.luckygc.jakartadata.repository.JakartaDataRepositoryFactoryBean;

import jakarta.data.repository.Repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.lang.NonNull;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Jakarta Data Repository 配置扩展 负责解析 @EnableJakartaDataRepositories 注解并注册相关 Bean
 *
 * @author luckygc
 */
public class JakartaDataRepositoryRegistrar implements ImportBeanDefinitionRegistrar {

    private static final Logger log = LoggerFactory.getLogger(JakartaDataRepositoryRegistrar.class);

    private static final String RESOURCE_PATTERN = "/**/*.class";

    private final ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();

    private List<String> basePackages;

    @Override
    public void registerBeanDefinitions(@NonNull AnnotationMetadata importingClassMetadata,
        @NonNull BeanDefinitionRegistry registry) {

        AnnotationAttributes attributes = AnnotationAttributes.fromMap(
            importingClassMetadata.getAnnotationAttributes(EnableJakartaDataRepositories.class.getName()));

        if (attributes == null) {
            log.warn("No @EnableJakartaDataRepositories annotation found");
            return;
        }

        // 获取要扫描的包路径
        basePackages = getBasePackages(importingClassMetadata, attributes);

        if (basePackages.isEmpty()) {
            log.warn("No base packages specified for Jakarta Data Repository scanning");
            return;
        }

        try {
            scanAndRegisterRepositories(registry);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        log.info("Registered Jakarta Data Repository scanner for packages: {}", basePackages);
    }

    /**
     * 获取要扫描的基础包路径
     */
    private List<String> getBasePackages(AnnotationMetadata importingClassMetadata, AnnotationAttributes attributes) {

        // 从 basePackages 属性获取
        List<String> basePackages = new ArrayList<>(Arrays.asList(attributes.getStringArray("basePackages")));

        // 如果没有指定包路径，使用注解所在类的包路径
        if (basePackages.isEmpty()) {
            basePackages.add(ClassUtils.getPackageName(importingClassMetadata.getClassName()));
        }

        // 过滤空字符串
        basePackages.removeIf(pkg -> !StringUtils.hasText(pkg));

        return basePackages;
    }

    /**
     * 扫描并注册 Repository 接口
     */
    private void scanAndRegisterRepositories(BeanDefinitionRegistry registry) throws Exception {
        MetadataReaderFactory readerFactory = new CachingMetadataReaderFactory(resourcePatternResolver);

        for (String basePackage : basePackages) {
            String pattern = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX +
                ClassUtils.convertClassNameToResourcePath(basePackage) + RESOURCE_PATTERN;

            Resource[] resources = resourcePatternResolver.getResources(pattern);

            for (Resource resource : resources) {
                if (resource.isReadable()) {
                    try {
                        MetadataReader reader = readerFactory.getMetadataReader(resource);
                        if (isRepositoryInterface(reader)) {
                            registerRepositoryBean(registry, reader.getClassMetadata().getClassName());
                        }
                    } catch (Exception e) {
                        log.warn("Failed to read resource: {}", resource, e);
                    }
                }
            }
        }
    }

    /**
     * 判断是否为 Repository 接口
     */
    private boolean isRepositoryInterface(MetadataReader reader) {
        try {
            // 检查是否为接口
            if (!reader.getClassMetadata().isInterface()) {
                return false;
            }

            // 检查是否有 @Repository 注解
            return reader.getAnnotationMetadata().hasAnnotation(Repository.class.getName());

        } catch (Exception e) {
            log.debug("Failed to check if class is repository interface: {}",
                reader.getClassMetadata().getClassName(), e);
            return false;
        }
    }

    /**
     * 注册 Repository Bean 定义
     */
    private void registerRepositoryBean(BeanDefinitionRegistry registry, String className) {
        try {
            Class<?> repositoryInterface = Class.forName(className);

            BeanDefinitionBuilder builder = BeanDefinitionBuilder
                .genericBeanDefinition(JakartaDataRepositoryFactoryBean.class);

            builder.addPropertyValue("repositoryInterface", repositoryInterface);
            // 设置为 INFRASTRUCTURE 角色，避免被某些 BeanPostProcessor 处理
            builder.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);

            String beanName = generateBeanName(repositoryInterface);
            registry.registerBeanDefinition(beanName, builder.getBeanDefinition());

            log.debug("Registered Repository bean: {} -> {}", beanName, className);

        } catch (Exception e) {
            log.error("Failed to register Repository bean for class: {}", className, e);
        }
    }

    /**
     * 生成 Bean 名称
     */
    private String generateBeanName(Class<?> repositoryInterface) {
        String simpleName = repositoryInterface.getSimpleName();
        return Character.toLowerCase(simpleName.charAt(0)) + simpleName.substring(1);
    }
}
