package github.gc.jakartadata.config;

import github.gc.jakartadata.annotation.EnableHibernateDataRepositories;
import github.gc.jakartadata.registry.HibernateDataRepositoryBeanDefinitionRegistrar;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.lang.NonNull;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Hibernate Data Repository 配置扩展
 * 负责解析 @EnableHibernateDataRepositories 注解并注册相应的 Bean 定义
 */
public class HibernateDataRepositoryConfigurationExtension implements ImportBeanDefinitionRegistrar {

    @Override
    public void registerBeanDefinitions(@NonNull AnnotationMetadata importingClassMetadata, 
                                      @NonNull BeanDefinitionRegistry registry) {
        
        AnnotationAttributes attributes = AnnotationAttributes.fromMap(
            importingClassMetadata.getAnnotationAttributes(EnableHibernateDataRepositories.class.getName()));
        
        if (attributes == null) {
            return;
        }

        // 获取要扫描的包路径
        List<String> basePackages = getBasePackages(importingClassMetadata, attributes);
        
        // 创建并配置 Repository Bean 定义注册器
        HibernateDataRepositoryBeanDefinitionRegistrar registrar = 
            new HibernateDataRepositoryBeanDefinitionRegistrar();
        
        registrar.setBasePackages(basePackages);
        registrar.setRepositoryImplementationPostfix(
            attributes.getString("repositoryImplementationPostfix"));
        registrar.setConsiderNestedRepositories(
            attributes.getBoolean("considerNestedRepositories"));
        
        // 注册 Repository Bean 定义
        registrar.registerBeanDefinitions(importingClassMetadata, registry);
    }

    /**
     * 获取要扫描的基础包路径
     */
    private List<String> getBasePackages(AnnotationMetadata importingClassMetadata, 
                                       AnnotationAttributes attributes) {
        List<String> basePackages = new ArrayList<>();
        
        // 从 basePackages 属性获取
        String[] packages = attributes.getStringArray("basePackages");
        if (packages.length > 0) {
            basePackages.addAll(Arrays.asList(packages));
        }
        
        // 从 basePackageClasses 属性获取
        Class<?>[] packageClasses = attributes.getClassArray("basePackageClasses");
        for (Class<?> packageClass : packageClasses) {
            basePackages.add(packageClass.getPackage().getName());
        }
        
        // 如果没有指定包路径，使用配置类所在的包
        if (basePackages.isEmpty()) {
            String className = importingClassMetadata.getClassName();
            String packageName = className.substring(0, className.lastIndexOf('.'));
            basePackages.add(packageName);
        }
        
        return basePackages;
    }
}
