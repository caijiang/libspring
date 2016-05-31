package me.jiangcai.lib.jdbc;

import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;

/**
 * @author CJ
 */
public interface JdbcService {

    /**
     * 执行一些jdbc操作.
     * <p>
     * tip:操作数据表比如create,drop,alert将直接提交事务.
     * </p>
     *
     * @param connectionConsumer 操作者
     * @throws SQLException
     */
    @Transactional
    void runJdbcWork(ConnectionConsumer connectionConsumer) throws SQLException;

    /**
     * 修改一个现有表,并且增加一个字段
     * <p>
     * tip:操作数据表比如create,drop,alert将直接提交事务.
     * </p>
     *
     * @param entityClass  目标JPA类
     * @param field        字段名称
     * @param defaultValue 默认值,可以为null
     * @throws SQLException         执行时发生SQL问题
     * @throws NoSuchFieldException 没有找到相关的字段
     * @since 1.4
     */
    @Transactional
    void tableAlterAddColumn(Class entityClass, String field, String defaultValue) throws SQLException, NoSuchFieldException;

    /**
     * 修改一个现有表,并且修改一个字段
     * <p>
     * tip:操作数据表比如create,drop,alert将直接提交事务.
     * </p>
     *
     * @param entityClass  目标JPA类
     * @param field        字段名称
     * @param defaultValue 默认值,可以为null
     * @throws SQLException         执行时发生SQL问题
     * @throws NoSuchFieldException 没有找到相关的字段
     * @since 1.4
     */
    @Transactional
    void tableAlterModifyColumn(Class entityClass, String field, String defaultValue) throws SQLException, NoSuchFieldException;

    /**
     * 执行一些jdbc操作.
     * <p>
     * tip:操作数据表比如create,drop,alert将直接提交事务.
     * </p>
     * 跟{@link #runJdbcWork(ConnectionConsumer)}不同的是,该方法并没有声明使用事务,也就是它将依赖当前线程已开启的事务
     *
     * @param connectionConsumer 操作者
     * @throws SQLException
     */
    void runStandaloneJdbcWork(ConnectionConsumer connectionConsumer) throws SQLException;


}
