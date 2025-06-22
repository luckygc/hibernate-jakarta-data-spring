package github.gc.jakartadata.repository;

import jakarta.data.repository.Repository;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.lang.NonNull;
import org.springframework.util.ClassUtils;

/**
 * Jakarta Data Repository Bean 定义注册器
 * 负责扫描和注册 Repository 接口为 Spring Bean
 * 
 * @author gc
 */
public class JakartaDataRepositoryBeanDefinitionRegistrar implements ImportBeanDefinitionRegistrar {

    private static final Logger log = LoggerFactory.getLogger(JakartaDataRepositoryBeanDefinitionRegistrar.class);
    private static final String RESOURCE_PATTERN = "/**/*.class";

    private final ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();

    private List<String> basePackages;

    @Override
    public void registerBeanDefinitions(@NonNull AnnotationMetadata importingClassMetadata, 
                                      @NonNull BeanDefinitionRegistry registry) {
        
        if (basePackages == null || basePackages.isEmpty()) {
            log.warn("No base packages specified for Jakarta Data Repository scanning");
            return;
        }

        try {
            scanAndRegisterRepositories(registry);
        } catch (Exception e) {
            log.error("Failed to scan and register Jakarta Data Repositories", e);
            throw new RuntimeException("Repository registration failed", e);
        }
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

    // Setters
    public void setBasePackages(List<String> basePackages) {
        this.basePackages = basePackages;
    }
}
