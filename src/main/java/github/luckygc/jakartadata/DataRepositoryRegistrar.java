package github.luckygc.jakartadata;

import github.luckygc.jakartadata.annotation.EnableDataRepositories;

import jakarta.data.repository.Repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
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
import org.jspecify.annotations.NonNull;
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
public class DataRepositoryRegistrar implements ImportBeanDefinitionRegistrar {

    private static final Logger log = LoggerFactory.getLogger(DataRepositoryRegistrar.class);

    private static final String RESOURCE_PATTERN = "/**/*.class";

    private final ResourcePatternResolver resourcePatternResolver =
            new PathMatchingResourcePatternResolver();

    private List<String> basePackages;

    @Override
    public void registerBeanDefinitions(
            @NonNull AnnotationMetadata importingClassMetadata,
            @NonNull BeanDefinitionRegistry registry) {

        AnnotationAttributes attributes =
                AnnotationAttributes.fromMap(
                        importingClassMetadata.getAnnotationAttributes(
                                EnableDataRepositories.class.getName()));

        if (attributes == null) {
            log.warn("未找到 @EnableDataRepositories 注解，跳过Repository扫描");
            return;
        }

        // 获取要扫描的包路径
        basePackages = getBasePackages(importingClassMetadata, attributes);

        if (basePackages.isEmpty()) {
            log.warn("未指定要扫描的基础包路径，跳过Jakarta Data Repository扫描");
            return;
        }

        try {
            scanAndRegisterRepositories(registry);
        } catch (Exception e) {
            throw new IllegalStateException(
                String.format("扫描和注册Jakarta Data Repository失败，包路径: %s", basePackages), e);
        }

        log.info("已完成Jakarta Data Repository扫描注册，包路径: {}", basePackages);
    }

    /** 获取要扫描的基础包路径 */
    private List<String> getBasePackages(
            AnnotationMetadata importingClassMetadata, AnnotationAttributes attributes) {

        // 从 basePackages 属性获取
        List<String> basePackages =
                new ArrayList<>(Arrays.asList(attributes.getStringArray("basePackages")));

        // 如果没有指定包路径，使用注解所在类的包路径
        if (basePackages.isEmpty()) {
            basePackages.add(ClassUtils.getPackageName(importingClassMetadata.getClassName()));
        }

        // 过滤空字符串
        basePackages.removeIf(pkg -> !StringUtils.hasText(pkg));

        return basePackages;
    }

    /** 扫描并注册 Repository 接口 */
    private void scanAndRegisterRepositories(BeanDefinitionRegistry registry) throws Exception {
        MetadataReaderFactory readerFactory =
                new CachingMetadataReaderFactory(resourcePatternResolver);

        for (String basePackage : basePackages) {
            String pattern =
                    ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX
                            + ClassUtils.convertClassNameToResourcePath(basePackage)
                            + RESOURCE_PATTERN;

            Resource[] resources = resourcePatternResolver.getResources(pattern);

            for (Resource resource : resources) {
                if (resource.isReadable()) {
                    try {
                        MetadataReader reader = readerFactory.getMetadataReader(resource);
                        if (isRepositoryInterface(reader)) {
                            registerRepositoryBean(
                                    registry, reader.getClassMetadata().getClassName());
                        }
                    } catch (Exception e) {
                        log.warn("Failed to read resource: {}", resource, e);
                    }
                }
            }
        }
    }

    /** 判断是否为 Repository 接口 */
    private boolean isRepositoryInterface(MetadataReader reader) {
        try {
            // 检查是否为接口
            if (!reader.getClassMetadata().isInterface()) {
                return false;
            }

            // 检查是否有 @Repository 注解
            return reader.getAnnotationMetadata().hasAnnotation(Repository.class.getName());

        } catch (Exception e) {
            log.debug(
                    "Failed to check if class is repository interface: {}",
                    reader.getClassMetadata().getClassName(),
                    e);
            return false;
        }
    }

    /** 注册 Repository Bean 定义 */
    private void registerRepositoryBean(BeanDefinitionRegistry registry, String className) {
        try {
            Class<?> repositoryInterface = Class.forName(className);

            // 获取Repository注解的provider属性值
            String provider = getRepositoryProvider(repositoryInterface);

            BeanDefinitionBuilder builder =
                    BeanDefinitionBuilder.genericBeanDefinition(DataRepositoryFactoryBean.class)
                            .setRole(BeanDefinition.ROLE_INFRASTRUCTURE)
                            .addConstructorArgValue(repositoryInterface);

            // 如果provider不为空，添加到bean definition中
            if (StringUtils.hasText(provider)) {
                builder.addPropertyValue("provider", provider);
            }

            AbstractBeanDefinition beanDefinition = builder.getBeanDefinition();

            String beanName = generateBeanName(repositoryInterface);
            if (registry.containsBeanDefinition(beanName)) {
                return;
            }

            registry.registerBeanDefinition(beanName, beanDefinition);

            log.debug(
                    "Registered Repository bean: {} -> {} with provider: {}",
                    beanName,
                    className,
                    provider);

        } catch (Exception e) {
            log.error("Failed to register Repository bean for class: {}", className, e);
        }
    }

    /** 获取Repository注解的provider属性值 */
    private String getRepositoryProvider(Class<?> repositoryInterface) {
        try {
            Repository repositoryAnnotation = repositoryInterface.getAnnotation(Repository.class);
            if (repositoryAnnotation != null) {
                return repositoryAnnotation.provider();
            }
        } catch (Exception e) {
            log.debug(
                    "Failed to get provider from Repository annotation for class: {}",
                    repositoryInterface.getName(),
                    e);
        }
        return "";
    }

    /** 生成 Bean 名称 */
    private String generateBeanName(Class<?> repositoryInterface) {
        String simpleName = repositoryInterface.getSimpleName();
        return Character.toLowerCase(simpleName.charAt(0)) + simpleName.substring(1);
    }
}
