package me.jiangcai.lib.thread;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 表明该方法会在线程安全下运行
 * 它的第一个参数需为{@link ThreadLocker}
 *
 * @author CJ
 * @since 3.0
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface ThreadSafe {
}
