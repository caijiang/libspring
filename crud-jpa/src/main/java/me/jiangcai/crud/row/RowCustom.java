package me.jiangcai.crud.row;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 可定制Row
 *
 * @author CJ
 */
@Documented
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RowCustom {
    /**
     * @return 装饰器
     */
    Class<? extends RowDramatizer> dramatizer() default DefaultRowDramatizer.class;

    /**
     * @return 是否排序重复结果
     */
    boolean distinct();

    /**
     * 在被定义为true只有，支持分页结果的装饰器工作会较为奇怪，具体将表现在缺席渲染分页信息上。
     *
     * @return 不会分页直接返回可以获得的所有结果
     * @since 4.0
     */
    boolean fetchAll() default false;
}
