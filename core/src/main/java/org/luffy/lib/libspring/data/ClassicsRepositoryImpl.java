package org.luffy.lib.libspring.data;

import org.springframework.data.jpa.repository.support.SimpleJpaRepository;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.CriteriaUpdate;
import java.io.Serializable;
import java.util.Map;

/**
 * Created by luffy on 2015/6/17.
 *
 * @author luffy luffy.ja at gmail.com
 */
public class ClassicsRepositoryImpl<T, ID extends Serializable> extends SimpleJpaRepository<T, ID> implements ClassicsRepository<T> {

    private final EntityManager entityManager;

    public ClassicsRepositoryImpl(Class<T> domainClass, EntityManager entityManager) {
        super(domainClass, entityManager);

        // Keep the EntityManager around to used from the newly introduced methods.
        this.entityManager = entityManager;
    }

    @Override
    public void lock(T entity, LockModeType lockMode) {
        entityManager.lock(entity,lockMode);
    }

    @Override
    public void lock(T entity, LockModeType lockMode, Map<String, Object> properties) {
        entityManager.lock(entity, lockMode, properties);
    }

    @Override
    public void refresh(T entity) {
        entityManager.refresh(entity);
    }

    @Override
    public void refresh(T entity, Map<String, Object> properties) {
        entityManager.refresh(entity,properties);
    }

    @Override
    public void refresh(T entity, LockModeType lockMode) {
        entityManager.refresh(entity,lockMode);
    }

    @Override
    public void refresh(T entity, LockModeType lockMode, Map<String, Object> properties) {
        entityManager.refresh(entity, lockMode, properties);
    }

    @Override
    public void clear() {
        entityManager.clear();
    }

    @Override
    public void detach(T entity) {
        entityManager.detach(entity);
    }

    @Override
    public boolean contains(T entity) {
        return entityManager.contains(entity);
    }

    @Override
    public Query createQuery(String qlString) {
        return entityManager.createQuery(qlString);
    }

    @Override
    public <T1> TypedQuery<T1> createQuery(CriteriaQuery<T1> criteriaQuery) {
        return entityManager.createQuery(criteriaQuery);
    }

    @Override
    public Query createQuery(CriteriaUpdate updateQuery) {
        return entityManager.createQuery(updateQuery);
    }

    @Override
    public Query createQuery(CriteriaDelete deleteQuery) {
        return entityManager.createQuery(deleteQuery);
    }

    @Override
    public <T1> TypedQuery<T1> createQuery(String qlString, Class<T1> resultClass) {
        return entityManager.createQuery(qlString,resultClass);
    }

    @Override
    public CriteriaBuilder getCriteriaBuilder() {
        return entityManager.getCriteriaBuilder();
    }

}
