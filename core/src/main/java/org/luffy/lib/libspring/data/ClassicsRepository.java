package org.luffy.lib.libspring.data;

import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.CriteriaUpdate;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Created by luffy on 2015/6/17.
 *
 * <p>每一个单元方法必须在一个session周期内完成</p>
 *
 * @author luffy luffy.ja at gmail.com
 */
public interface ClassicsRepository<T> {

    /**
     * Lock an entity instance that is contained in the persistence
     * context with the specified lock mode type.
     * <p>If a pessimistic lock mode type is specified and the entity
     * contains a version attribute, the persistence provider must
     * also perform optimistic version checks when obtaining the
     * database lock.  If these checks fail, the
     * <code>OptimisticLockException</code> will be thrown.
     * <p>If the lock mode type is pessimistic and the entity instance
     * is found but cannot be locked:
     * <ul>
     * <li> the <code>PessimisticLockException</code> will be thrown if the database
     *    locking failure causes transaction-level rollback
     * <li> the <code>LockTimeoutException</code> will be thrown if the database
     *    locking failure causes only statement-level rollback
     * </ul>
     * @param entity  entity instance
     * @param lockMode  lock mode
     * @throws IllegalArgumentException if the instance is not an
     *         entity or is a detached entity
     * @throws TransactionRequiredException if there is no
     *         transaction or if invoked on an entity manager which
     *         has not been joined to the current transaction
     * @throws EntityNotFoundException if the entity does not exist
     *         in the database when pessimistic locking is
     *         performed
     * @throws OptimisticLockException if the optimistic version
     *         check fails
     * @throws PessimisticLockException if pessimistic locking fails
     *         and the transaction is rolled back
     * @throws LockTimeoutException if pessimistic locking fails and
     *         only the statement is rolled back
     * @throws PersistenceException if an unsupported lock call
     *         is made
     */
    void lock(T entity, LockModeType lockMode);

    /**
     * Lock an entity instance that is contained in the persistence
     * context with the specified lock mode type and with specified
     * properties.
     * <p>If a pessimistic lock mode type is specified and the entity
     * contains a version attribute, the persistence provider must
     * also perform optimistic version checks when obtaining the
     * database lock.  If these checks fail, the
     * <code>OptimisticLockException</code> will be thrown.
     * <p>If the lock mode type is pessimistic and the entity instance
     * is found but cannot be locked:
     * <ul>
     * <li> the <code>PessimisticLockException</code> will be thrown if the database
     *    locking failure causes transaction-level rollback
     * <li> the <code>LockTimeoutException</code> will be thrown if the database
     *    locking failure causes only statement-level rollback
     * </ul>
     * <p>If a vendor-specific property or hint is not recognized,
     * it is silently ignored.
     * <p>Portable applications should not rely on the standard timeout
     * hint. Depending on the database in use and the locking
     * mechanisms used by the provider, the hint may or may not
     * be observed.
     * @param entity  entity instance
     * @param lockMode  lock mode
     * @param properties  standard and vendor-specific properties
     *        and hints
     * @throws IllegalArgumentException if the instance is not an
     *         entity or is a detached entity
     * @throws TransactionRequiredException if there is no
     *         transaction or if invoked on an entity manager which
     *         has not been joined to the current transaction
     * @throws EntityNotFoundException if the entity does not exist
     *         in the database when pessimistic locking is
     *         performed
     * @throws OptimisticLockException if the optimistic version
     *         check fails
     * @throws PessimisticLockException if pessimistic locking fails
     *         and the transaction is rolled back
     * @throws LockTimeoutException if pessimistic locking fails and
     *         only the statement is rolled back
     * @throws PersistenceException if an unsupported lock call
     *         is made
     * @since Java Persistence 2.0
     */
    void lock(T entity, LockModeType lockMode,
                     Map<String, Object> properties);

    /**
     * Refresh the state of the instance from the database,
     * overwriting changes made to the entity, if any.
     * @param entity  entity instance
     * @throws IllegalArgumentException if the instance is not
     *         an entity or the entity is not managed
     * @throws TransactionRequiredException if there is no
     *         transaction when invoked on a container-managed
     *         entity manager of type <code>PersistenceContextType.TRANSACTION</code>
     * @throws EntityNotFoundException if the entity no longer
     *         exists in the database
     */
    void refresh(T entity);

    /**
     * Refresh the state of the instance from the database, using
     * the specified properties, and overwriting changes made to
     * the entity, if any.
     * <p> If a vendor-specific property or hint is not recognized,
     * it is silently ignored.
     * @param entity  entity instance
     * @param properties  standard and vendor-specific properties
     *        and hints
     * @throws IllegalArgumentException if the instance is not
     *         an entity or the entity is not managed
     * @throws TransactionRequiredException if there is no
     *         transaction when invoked on a container-managed
     *         entity manager of type <code>PersistenceContextType.TRANSACTION</code>
     * @throws EntityNotFoundException if the entity no longer
     *         exists in the database
     * @since Java Persistence 2.0
     */
    void refresh(T entity,
                        Map<String, Object> properties);

    /**
     * Refresh the state of the instance from the database,
     * overwriting changes made to the entity, if any, and
     * lock it with respect to given lock mode type.
     * <p>If the lock mode type is pessimistic and the entity instance
     * is found but cannot be locked:
     * <ul>
     * <li> the <code>PessimisticLockException</code> will be thrown if the database
     *    locking failure causes transaction-level rollback
     * <li> the <code>LockTimeoutException</code> will be thrown if the
     *    database locking failure causes only statement-level
     *    rollback.
     * </ul>
     * @param entity  entity instance
     * @param lockMode  lock mode
     * @throws IllegalArgumentException if the instance is not
     *         an entity or the entity is not managed
     * @throws TransactionRequiredException if invoked on a
     *         container-managed entity manager of type
     *         <code>PersistenceContextType.TRANSACTION</code> when there is
     *         no transaction; if invoked on an extended entity manager when
     *         there is no transaction and a lock mode other than <code>NONE</code>
     *         has been specified; or if invoked on an extended entity manager
     *         that has not been joined to the current transaction and a
     *         lock mode other than <code>NONE</code> has been specified
     * @throws EntityNotFoundException if the entity no longer exists
     *         in the database
     * @throws PessimisticLockException if pessimistic locking fails
     *         and the transaction is rolled back
     * @throws LockTimeoutException if pessimistic locking fails and
     *         only the statement is rolled back
     * @throws PersistenceException if an unsupported lock call
     *         is made
     * @since Java Persistence 2.0
     */
    void refresh(T entity, LockModeType lockMode);

    /**
     * Refresh the state of the instance from the database,
     * overwriting changes made to the entity, if any, and
     * lock it with respect to given lock mode type and with
     * specified properties.
     * <p>If the lock mode type is pessimistic and the entity instance
     * is found but cannot be locked:
     * <ul>
     * <li> the <code>PessimisticLockException</code> will be thrown if the database
     *    locking failure causes transaction-level rollback
     * <li> the <code>LockTimeoutException</code> will be thrown if the database
     *    locking failure causes only statement-level rollback
     * </ul>
     * <p>If a vendor-specific property or hint is not recognized,
     *    it is silently ignored.
     * <p>Portable applications should not rely on the standard timeout
     * hint. Depending on the database in use and the locking
     * mechanisms used by the provider, the hint may or may not
     * be observed.
     * @param entity  entity instance
     * @param lockMode  lock mode
     * @param properties  standard and vendor-specific properties
     *        and hints
     * @throws IllegalArgumentException if the instance is not
     *         an entity or the entity is not managed
     * @throws TransactionRequiredException if invoked on a
     *         container-managed entity manager of type
     *         <code>PersistenceContextType.TRANSACTION</code> when there is
     *         no transaction; if invoked on an extended entity manager when
     *         there is no transaction and a lock mode other than <code>NONE</code>
     *         has been specified; or if invoked on an extended entity manager
     *         that has not been joined to the current transaction and a
     *         lock mode other than <code>NONE</code> has been specified
     * @throws EntityNotFoundException if the entity no longer exists
     *         in the database
     * @throws PessimisticLockException if pessimistic locking fails
     *         and the transaction is rolled back
     * @throws LockTimeoutException if pessimistic locking fails and
     *         only the statement is rolled back
     * @throws PersistenceException if an unsupported lock call
     *         is made
     * @since Java Persistence 2.0
     */
    void refresh(T entity, LockModeType lockMode,
                        Map<String, Object> properties);

    /**
     * Clear the persistence context, causing all managed
     * entities to become detached. Changes made to entities that
     * have not been flushed to the database will not be
     * persisted.
     */
    void clear();

    /**
     * Remove the given entity from the persistence context, causing
     * a managed entity to become detached.  Unflushed changes made
     * to the entity if any (including removal of the entity),
     * will not be synchronized to the database.  Entities which
     * previously referenced the detached entity will continue to
     * reference it.
     * @param entity  entity instance
     * @throws IllegalArgumentException if the instance is not an
     *         entity
     * @since Java Persistence 2.0
     */
    void detach(T entity);

    /**
     * Check if the instance is a managed entity instance belonging
     * to the current persistence context.
     * @param entity  entity instance
     * @return boolean indicating if entity is in persistence context
     * @throws IllegalArgumentException if not an entity
     */
    boolean contains(T entity);

    /**
     * 执行一段HQL并且返回结果
     * @param hqlString HQL语句
     * @param processor query处理器
     * @return 结果集
     * @see Query#getResultList()
     */
    List queryHql(String hqlString,Consumer<Query> processor);

    /**
     * 指定结果集的HQL
     * @param hqlString 语句
     * @param resultType 指定的类型
     * @param processor 处理器
     * @param <B> 指定的类型
     * @return 结果集
     * @see Query#getResultList()
     */
    <B> List<B> queryHql(String hqlString,Class<B> resultType,Consumer<TypedQuery<B>> processor);

    /**
     * 执行一个更新语句(update or delete)
     * @param hqlString 语句
     * @param processor 处理器
     * @return 更新的记录数
     * @see Query#executeUpdate()
     */
    int executeHql(String hqlString,Consumer<Query> processor);
}
