package github.gc.jakartadata.registry;

import github.gc.jakartadata.factory.HibernateDataRepositoryFactoryBean;
import jakarta.data.repository.Repository;
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

import java.util.List;

/**
 * Hibernate Data Repository Bean 定义注册器
 * 负责扫描和注册 Repository 接口的 Bean 定义
 */
public class HibernateDataRepositoryBeanDefinitionRegistrar implements ImportBeanDefinitionRegistrar {

    private static final Logger log = LoggerFactory.getLogger(HibernateDataRepositoryBeanDefinitionRegistrar.class);
    
    private static final String RESOURCE_PATTERN = "/**/*.class";
    
    private List<String> basePackages;
    private String repositoryImplementationPostfix = "Repository";
    private boolean considerNestedRepositories = false;
    
    private final ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();

    @Override
    public void registerBeanDefinitions(@NonNull AnnotationMetadata importingClassMetadata, 
                                      @NonNull BeanDefinitionRegistry registry) {
        
        if (basePackages == null || basePackages.isEmpty()) {
            log.warn("No base packages specified for Hibernate Data Repository scanning");
            return;
        }

        try {
            scanAndRegisterRepositories(registry);
        } catch (Exception e) {
            log.error("Failed to scan and register Hibernate Data Repositories", e);
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
            if (reader.getAnnotationMetadata().hasAnnotation(Repository.class.getName())) {
                return true;
            }
            
            // 检查是否继承自 Jakarta Data Repository 接口
            String className = reader.getClassMetadata().getClassName();
            Class<?> clazz = Class.forName(className);
            return isJakartaDataRepository(clazz);
            
        } catch (Exception e) {
            log.debug("Failed to check if class is repository interface: {}", 
                     reader.getClassMetadata().getClassName(), e);
            return false;
        }
    }

    /**
     * 检查是否为 Jakarta Data Repository 接口
     */
    private boolean isJakartaDataRepository(Class<?> clazz) {
        // 检查是否直接或间接继承自 Jakarta Data Repository 相关接口
        return jakarta.data.repository.Repository.class.isAssignableFrom(clazz) ||
               jakarta.data.repository.CrudRepository.class.isAssignableFrom(clazz) ||
               jakarta.data.repository.DataRepository.class.isAssignableFrom(clazz);
    }

    /**
     * 注册 Repository Bean 定义
     */
    private void registerRepositoryBean(BeanDefinitionRegistry registry, String className) {
        try {
            Class<?> repositoryInterface = Class.forName(className);

            String beanName = generateBeanName(repositoryInterface);

            // 检查是否已经注册过
            if (registry.containsBeanDefinition(beanName)) {
                log.debug("Repository bean already registered: {}", beanName);
                return;
            }

            BeanDefinitionBuilder builder = BeanDefinitionBuilder
                .genericBeanDefinition(HibernateDataRepositoryFactoryBean.class);

            builder.addConstructorArgValue(repositoryInterface);
            builder.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);

            registry.registerBeanDefinition(beanName, builder.getBeanDefinition());

            log.debug("Registered Hibernate Data Repository: {} as bean: {}", className, beanName);

        } catch (Exception e) {
            log.error("Failed to register repository bean for class: {}", className, e);
        }
    }

    /**
     * 生成 Bean 名称
     */
    private String generateBeanName(Class<?> repositoryInterface) {
        String simpleName = repositoryInterface.getSimpleName();
        return Character.toLowerCase(simpleName.charAt(0)) + simpleName.substring(1);
    }

    // Getters and Setters
    public void setBasePackages(List<String> basePackages) {
        this.basePackages = basePackages;
    }

    public void setRepositoryImplementationPostfix(String repositoryImplementationPostfix) {
        this.repositoryImplementationPostfix = repositoryImplementationPostfix;
    }

    public void setConsiderNestedRepositories(boolean considerNestedRepositories) {
        this.considerNestedRepositories = considerNestedRepositories;
    }
}
