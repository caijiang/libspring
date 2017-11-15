package me.jiangcai.crud.row.bean;

import me.jiangcai.crud.row.RowDefinition;
import me.jiangcai.crud.row.RowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Root;
import java.util.List;

/**
 * @author CJ
 */
@Service
public class RowServiceImpl implements RowService {

    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    private EntityManager entityManager;

    @Override
    public <T> List<T> queryAllEntity(RowDefinition<T> definition) {
        definition.fields();
        final CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<T> cq = cb.createQuery(definition.entityClass());
        Root<T> root = cq.from(definition.entityClass());

        final Specification<T> specification = definition.specification();
        if (specification != null) {
            cq = cq.where(specification.toPredicate(root, cq, cb));
        }

        final List<Order> o = definition.defaultOrder(cb, root);
        if (o != null)
            cq = cq.orderBy(o);

        return entityManager.createQuery(cq).getResultList();
    }

    @Override
    public <T> Page<T> queryEntity(RowDefinition<T> definition, Pageable pageable) {
        definition.fields();
        final CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<T> cq = cb.createQuery(definition.entityClass());
        Root<T> root = cq.from(definition.entityClass());

        final Specification<T> specification = definition.specification();
        if (specification != null) {
            cq = cq.where(specification.toPredicate(root, cq, cb));
        }

        final List<Order> o = definition.defaultOrder(cb, root);
        if (o != null)
            cq = cq.orderBy(o);

        final List<T> resultList = entityManager.createQuery(cq).setMaxResults(pageable.getPageSize()).setFirstResult(pageable.getOffset()).getResultList();
        // and the total
        CriteriaQuery<Long> countCq = cb.createQuery(Long.class);
        Root<T> countRoot = countCq.from(definition.entityClass());
        if (specification != null) {
            countCq = countCq.where(specification.toPredicate(countRoot, countCq, cb));
        }
        countCq = countCq.select(cb.count(countRoot));
        return new PageImpl<T>(resultList, pageable, entityManager.createQuery(countCq).getSingleResult());
    }
}
