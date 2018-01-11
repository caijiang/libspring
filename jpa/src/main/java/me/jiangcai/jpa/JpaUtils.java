package me.jiangcai.jpa;

import java.io.Serializable;

/**
 * @deprecated 挪到 {@link me.jiangcai.crud.utils.JpaUtils}
 * @author CJ
 * @since 3.0
 */
@Deprecated
public class JpaUtils {
    /**
     * @param entityClass 实体类
     * @return 获取这个实体类的id class
     */
    public static Class<? extends Serializable> idClassForEntity(Class<?> entityClass) {
        return me.jiangcai.crud.utils.JpaUtils.idClassForEntity(entityClass);
    }

    /**
     * @param entityClass 实体类
     * @return 获取这个实体类的id 的名字
     */
    public static String idFieldNameForEntity(Class<?> entityClass) {
        return me.jiangcai.crud.utils.JpaUtils.idFieldNameForEntity(entityClass);
    }

}
