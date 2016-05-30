package me.jiangcai.lib.spring.data;

import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Created by luffy on 2015/6/17.
 *
 * @author luffy luffy.ja at gmail.com
 */
public class ClassicsRepositoryImpl<T, ID extends Serializable> extends SimpleJpaRepository<T, ID> implements ClassicsRepository<T> {

    private final EntityManager entityManager;

    public ClassicsRepositoryImpl(JpaEntityInformation<T, ?> entityInformation, EntityManager entityManager) {
        super(entityInformation, entityManager);
        this.entityManager = entityManager;
    }

    public ClassicsRepositoryImpl(Class<T> domainClass, EntityManager entityManager) {
        super(domainClass, entityManager);

        // Keep the EntityManager around to used from the newly introduced methods.
        this.entityManager = entityManager;
    }

    //    @Override
    public void lock(T entity, LockModeType lockMode) {
        entityManager.lock(entity, lockMode);
    }

    //    @Override
    public void lock(T entity, LockModeType lockMode, Map<String, Object> properties) {
        entityManager.lock(entity, lockMode, properties);
    }

//    @Override
//    public T refresh(T entity) {
//        if (entityManager.contains(entity)){
//            entityManager.refresh(entity);
//            return entity;
//        }
//        if (entity)
//
//    }

    //    @Override
    public void refresh(T entity, Map<String, Object> properties) {
        entityManager.refresh(entity, properties);
    }

    //    @Override
    public void refresh(T entity, LockModeType lockMode) {
        entityManager.refresh(entity, lockMode);
    }

    //    @Override
    public void refresh(T entity, LockModeType lockMode, Map<String, Object> properties) {
        entityManager.refresh(entity, lockMode, properties);
    }

    @Override
    @Transactional
    public void requestEntityManager(Consumer<EntityManager> handler) {
        if (handler != null) {
            handler.accept(entityManager);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public T refresh(T entity, Serializable id) {
        if (entityManager.contains(entity)) {
            entityManager.refresh(entity);
            return entity;
        }
        T t = this.getOne((ID) id);
        entityManager.refresh(t);
        return t;
    }

    @Override
    @Transactional
    public void clear() {
        entityManager.clear();
    }

    //    @Override
    public void detach(T entity) {
        entityManager.detach(entity);
    }

    //    @Override
    public boolean contains(T entity) {
        return entityManager.contains(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public List queryHql(String hqlString, Consumer<Query> processor) {
        Query query = entityManager.createQuery(hqlString);
        if (processor != null) {
            processor.accept(query);
        }
        return query.getResultList();
    }

    @Override
    @Transactional(readOnly = true)
    public <B> List<B> queryHql(String hqlString, Class<B> resultType, Consumer<TypedQuery<B>> processor) {
        TypedQuery<B> query = entityManager.createQuery(hqlString, resultType);
        if (processor != null) {
            processor.accept(query);
        }
        return query.getResultList();
    }

    @Override
    @Transactional
    public int executeHql(String hqlString, Consumer<Query> processor) {
        Query query = entityManager.createQuery(hqlString);
        if (processor != null) {
            processor.accept(query);
        }
        return query.executeUpdate();
    }

}
