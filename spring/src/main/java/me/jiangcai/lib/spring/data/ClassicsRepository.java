package me.jiangcai.lib.spring.data;

import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.Query;
import javax.persistence.TransactionRequiredException;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.function.Consumer;

/**
 * Created by luffy on 2015/6/17.
 * <p>
 * <p>每一个单元方法必须在一个session周期内完成</p>
 *
 * @author luffy luffy.ja at gmail.com
 */
public interface ClassicsRepository<T> {

    /**
     * 请求EntityManager并进行一些操作
     *
     * @param handler
     */
    @Transactional
    void requestEntityManager(Consumer<EntityManager> handler);

    /**
     * Refresh the state of the instance from the database,
     * overwriting changes made to the entity, if any.
     *
     * @param entity entity instance
     * @param id     id
     * @return new Entity
     * @throws IllegalArgumentException     if the instance is not
     *                                      an entity or the entity is not managed
     * @throws TransactionRequiredException if there is no
     *                                      transaction when invoked on a container-managed
     *                                      entity manager of type <code>PersistenceContextType.TRANSACTION</code>
     * @throws EntityNotFoundException      if the entity no longer
     *                                      exists in the database
     */
    @Transactional(readOnly = true)
    T refresh(T entity, java.io.Serializable id);

    /**
     * Clear the persistence context, causing all managed
     * entities to become detached. Changes made to entities that
     * have not been flushed to the database will not be
     * persisted.
     */
    @Transactional
    void clear();

    /**
     * 执行一段HQL并且返回结果
     *
     * @param hqlString HQL语句
     * @param processor query处理器
     * @return 结果集
     * @see Query#getResultList()
     */
    @Transactional(readOnly = true)
    List queryHql(String hqlString, Consumer<Query> processor);

    /**
     * 指定结果集的HQL
     *
     * @param hqlString  语句
     * @param resultType 指定的类型
     * @param processor  处理器
     * @param <B>        指定的类型
     * @return 结果集
     * @see TypedQuery#getResultList()
     */
    @Transactional(readOnly = true)
    <B> List<B> queryHql(String hqlString, Class<B> resultType, Consumer<TypedQuery<B>> processor);

    /**
     * 执行一个更新语句(update or delete)
     *
     * @param hqlString 语句
     * @param processor 处理器
     * @return 更新的记录数
     * @see Query#executeUpdate()
     */
    @Transactional
    int executeHql(String hqlString, Consumer<Query> processor);
}
