package me.jiangcai.poi.template.crud;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * execl报表
 *
 * @author CJ
 */
@Documented
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ExeclReport {

    /**
     * @return 报表模板的resourceLocation {@link org.springframework.context.ApplicationContext#getResource(String)}
     */
    String value();
}
