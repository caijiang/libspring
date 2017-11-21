package me.jiangcai.crud.row;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.function.BiFunction;

/**
 * @author CJ
 */
public interface RowService {

    /**
     * @param definition 数据定义
     * @param <T>        实体类型
     * @return 根据查询定义，获取所有的实体
     */
    @Transactional(readOnly = true)
    <T> List<T> queryAllEntity(RowDefinition<T> definition);

    /**
     * @param definition 数据定义
     * @param <T>        实体类型
     * @return 根据查询定义，获取所有的实体
     */
    @Transactional(readOnly = true)
    <T> Page<T> queryEntity(RowDefinition<T> definition, Pageable pageable);

    /**
     * @param rowDefinition       数据定义
     * @param distinct            是否唯一
     * @param customOrderFunction 可选的自定义排序
     * @param pageable            分页
     * @return 获取相关的字段
     */
    Page<?> queryFields(RowDefinition rowDefinition, boolean distinct
            , BiFunction<CriteriaBuilder, Root, List<Order>> customOrderFunction, Pageable pageable);

    /**
     * @param rowDefinition       数据定义
     * @param distinct            是否唯一
     * @param customOrderFunction 可选的自定义排序
     * @return 获取相关的字段
     */
    List<?> queryFields(RowDefinition rowDefinition, boolean distinct
            , BiFunction<CriteriaBuilder, Root, List<Order>> customOrderFunction);
}
