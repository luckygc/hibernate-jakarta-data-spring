package github.gc.jakartadata.registry;

import github.gc.jakartadata.factory.HibernateDataRepositoryFactoryBean;
import jakarta.data.repository.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.lang.NonNull;

import java.util.List;

/**
 * Hibernate Data Repository Bean 定义注册器
 * 参考 MyBatis MapperScannerConfigurer 的简洁设计
 */
public class JakartaDataRepositoryBeanDefinitionRegistrar implements ImportBeanDefinitionRegistrar {

    private static final Logger log = LoggerFactory.getLogger(JakartaDataRepositoryBeanDefinitionRegistrar.class);

    private List<String> basePackages;

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
     * 参考 MyBatis 使用 ClassPathScanningCandidateComponentProvider 的方式
     */
    private void scanAndRegisterRepositories(BeanDefinitionRegistry registry) {
        // 创建组件扫描器
        ClassPathScanningCandidateComponentProvider scanner =
            new ClassPathScanningCandidateComponentProvider(false) {
                @Override
                protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
                    // 只扫描接口
                    return beanDefinition.getMetadata().isInterface() &&
                           beanDefinition.getMetadata().isIndependent();
                }
            };

        // 添加过滤器：扫描带有 @Repository 注解的接口
        scanner.addIncludeFilter(new AnnotationTypeFilter(Repository.class));

        // 扫描每个包
        for (String basePackage : basePackages) {
            for (BeanDefinition candidate : scanner.findCandidateComponents(basePackage)) {
                String className = candidate.getBeanClassName();
                if (className != null) {
                    try {
                        Class<?> repositoryInterface = Class.forName(className);
                        if (isJakartaDataRepository(repositoryInterface)) {
                            registerRepositoryBean(registry, repositoryInterface);
                        }
                    } catch (Exception e) {
                        log.warn("Failed to process repository interface: {}", className, e);
                    }
                }
            }
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
     * 参考 MyBatis 的简洁注册方式
     */
    private void registerRepositoryBean(BeanDefinitionRegistry registry, Class<?> repositoryInterface) {
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
        builder.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_TYPE);

        registry.registerBeanDefinition(beanName, builder.getBeanDefinition());

        log.debug("Registered Hibernate Data Repository: {} as bean: {}",
                 repositoryInterface.getName(), beanName);
    }

    /**
     * 生成 Bean 名称
     */
    private String generateBeanName(Class<?> repositoryInterface) {
        String simpleName = repositoryInterface.getSimpleName();
        return Character.toLowerCase(simpleName.charAt(0)) + simpleName.substring(1);
    }

    // Setter for base packages
    public void setBasePackages(List<String> basePackages) {
        this.basePackages = basePackages;
    }
}
