package me.jiangcai.lib.spring.data;

import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

/**
 * 合并2个规格
 *
 * @author CJ
 * @since 3.0
 */
public class AndSpecification<T> implements Specification<T> {

    private final Specification<T> one;
    private final Specification<T> another;

    public AndSpecification(Specification<T> one, Specification<T> another) {
        this.one = one;
        this.another = another;
    }

    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        if (one == null)
            return another.toPredicate(root, query, cb);
        if (another == null)
            return one.toPredicate(root, query, cb);

        return cb.and(one.toPredicate(root, query, cb), another.toPredicate(root, query, cb));
    }
}
