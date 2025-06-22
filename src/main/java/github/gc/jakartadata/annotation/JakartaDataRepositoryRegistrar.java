package github.gc.jakartadata.annotation;

import github.gc.jakartadata.repository.JakartaDataRepositoryBeanDefinitionRegistrar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.lang.NonNull;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Jakarta Data Repository 配置扩展
 * 负责解析 @EnableJakartaDataRepositories 注解并注册相关 Bean
 *
 * @author gc
 */
public class JakartaDataRepositoryRegistrar implements ImportBeanDefinitionRegistrar {

    private static final Logger log = LoggerFactory.getLogger(JakartaDataRepositoryRegistrar.class);

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
        List<String> basePackages = getBasePackages(importingClassMetadata, attributes);

        if (basePackages.isEmpty()) {
            log.warn("No base packages specified for Jakarta Data Repository scanning");
            return;
        }

        // 创建并配置 Repository Bean 定义注册器
        JakartaDataRepositoryBeanDefinitionRegistrar registrar =
            new JakartaDataRepositoryBeanDefinitionRegistrar();

        registrar.setBasePackages(basePackages);

        // 注册 Repository Bean 定义
        registrar.registerBeanDefinitions(importingClassMetadata, registry);

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
}
