package org.springframework.stereotype;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * created by yaozou on 2018/5/7
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface Service {
    @AliasFor(annotation = Component.class)
    String value() default "";
}
