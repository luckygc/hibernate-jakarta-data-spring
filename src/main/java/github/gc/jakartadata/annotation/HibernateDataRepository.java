package github.gc.jakartadata.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * 标记 Hibernate Data Repository 接口的注解
 * 继承自 Spring 的 @Component，使其能被 Spring 容器管理
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface HibernateDataRepository {

    /**
     * Bean 的名称
     */
    String value() default "";
}
