package me.jiangcai.crud.row;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

}
