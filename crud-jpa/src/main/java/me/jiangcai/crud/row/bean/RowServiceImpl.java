package me.jiangcai.crud.row.bean;

import me.jiangcai.crud.row.FieldDefinition;
import me.jiangcai.crud.row.RowDefinition;
import me.jiangcai.crud.row.RowService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author CJ
 */
@Service
public class RowServiceImpl implements RowService {

    private static final Log log = LogFactory.getLog(RowServiceImpl.class);

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

    @Override
    @SuppressWarnings("unchecked")
    public Page<?> queryFields(RowDefinition rowDefinition, boolean distinct,
                               BiFunction<CriteriaBuilder, Root, List<Order>> customOrderFunction, Pageable pageable) {
        final List<FieldDefinition> fieldDefinitions = rowDefinition.fields();

        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery originDataQuery = criteriaBuilder.createQuery();
        CriteriaQuery<Long> countQuery = criteriaBuilder.createQuery(Long.class);
//        Subquery subquery = null;
//        subquery.from(rowDefinition.entityClass());

        Root root = originDataQuery.from(rowDefinition.entityClass());
        Root countRoot = countQuery.from(rowDefinition.entityClass());


        CriteriaQuery dataQuery = originDataQuery.multiselect(fieldDefinitions.stream()
                .map(new Function<FieldDefinition, Selection>() {
                    @Override
                    public Selection apply(FieldDefinition fieldDefinition) {
//                        field
//                                -> field.select(criteriaBuilder, originDataQuery, root)
                        return fieldDefinition.select(criteriaBuilder, originDataQuery, root);
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList()));

        // where
        dataQuery = where(criteriaBuilder, dataQuery, root, rowDefinition);
        dataQuery = rowDefinition.dataGroup(criteriaBuilder, dataQuery, root);
        countQuery = where(criteriaBuilder, countQuery, countRoot, rowDefinition);
        countQuery = rowDefinition.countQuery(criteriaBuilder, countQuery, countRoot);

        if (distinct)
            countQuery = countQuery.select(criteriaBuilder.countDistinct(rowDefinition.count(countQuery, criteriaBuilder, countRoot)));
        else
            countQuery = countQuery.select(criteriaBuilder.count(rowDefinition.count(countQuery, criteriaBuilder, countRoot)));

        // Distinct
        if (distinct)
            dataQuery = dataQuery.distinct(true);

        // sort
        List<Order> order = customOrderFunction == null ? null : customOrderFunction.apply(criteriaBuilder, root);
        if (CollectionUtils.isEmpty(order))
            order = rowDefinition.defaultOrder(criteriaBuilder, root);

        if (!CollectionUtils.isEmpty(order))
            dataQuery = dataQuery.orderBy(order);

        // 打包成Object[]
        try {
            long total;
            try {
                total = entityManager.createQuery(countQuery).getSingleResult();
            } catch (NonUniqueResultException ex) {
                total = entityManager.createQuery(countQuery).getResultList().size();
            }
            List<?> list;
            if (total == 0)
                list = Collections.emptyList();
            else {
                list = entityManager.createQuery(dataQuery)
                        .setFirstResult(pageable.getOffset())
                        .setMaxResults(pageable.getPageSize())
                        .getResultList();
            }

            // 输出到结果
            log.debug("RW Result: total:" + total + ", list:" + list + ", fields:" + fieldDefinitions.size());
            return new PageImpl(list, pageable, total);
        } catch (NoResultException ex) {
            log.debug("RW Result: no result found.");
            return new PageImpl(Collections.EMPTY_LIST, pageable, 0);
        }
    }


    private <T> CriteriaQuery<T> where(CriteriaBuilder criteriaBuilder, CriteriaQuery<T> query, Root<?> root
            , RowDefinition<?> rowDefinition) {
        final Specification<?> specification = rowDefinition.specification();
        if (specification == null)
            return query;
        //noinspection unchecked
        return query.where(specification.toPredicate((Root) root, query, criteriaBuilder));
    }
}
