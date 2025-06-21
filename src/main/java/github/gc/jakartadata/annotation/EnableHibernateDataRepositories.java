package github.gc.jakartadata.annotation;

import github.gc.jakartadata.config.HibernateDataRepositoryConfigurationExtension;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 启用 Hibernate Data Repository 支持的注解
 * 用于扫描和注册 Jakarta Data Repository 接口
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(HibernateDataRepositoryConfigurationExtension.class)
public @interface EnableHibernateDataRepositories {

    /**
     * 要扫描的基础包路径
     */
    String[] basePackages() default {};

    /**
     * 要扫描的基础包类
     */
    Class<?>[] basePackageClasses() default {};

    /**
     * Repository 接口的后缀，默认为 "Repository"
     */
    String repositoryImplementationPostfix() default "Repository";

    /**
     * 是否考虑嵌套的 Repository 接口
     */
    boolean considerNestedRepositories() default false;
}
