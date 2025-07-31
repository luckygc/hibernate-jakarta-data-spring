package github.luckygc.jakartadata.annotation;

import github.luckygc.jakartadata.DataRepositoryRegistrar;
import org.springframework.context.annotation.Import;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 启用 Jakarta Data Repository 支持的注解
 *
 * @author luckygc
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(DataRepositoryRegistrar.class)
public @interface EnableDataRepositories {

    /** 要扫描的基础包路径 */
    String[] basePackages() default {};
}
