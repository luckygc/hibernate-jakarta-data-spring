package github.gc.jakartadata.annotation;

import github.gc.jakartadata.config.JakartaDataRepositoryConfigurationExtension;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 启用 Jakarta Data Repository 支持的注解
 *
 * @author gc
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(JakartaDataRepositoryConfigurationExtension.class)
public @interface EnableJakartaDataRepositories {

    /**
     * 要扫描的基础包路径
     */
    String[] basePackages() default {};
}
