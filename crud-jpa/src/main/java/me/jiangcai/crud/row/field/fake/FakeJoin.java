package me.jiangcai.crud.row.field.fake;

import javax.persistence.criteria.Expression;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.metamodel.Attribute;

/**
 * @author CJ
 */
public class FakeJoin<Z, X> extends FakeFrom<Z, X> implements Join<Z, X> {
    public FakeJoin(AbstractFake fake) {
        super(fake);
    }

    public FakeJoin() {
        super();
    }

    @Override
    public Join<Z, X> on(Expression<Boolean> restriction) {
        return this;
    }

    @Override
    public Join<Z, X> on(Predicate... restrictions) {
        return this;
    }

    @Override
    public Predicate getOn() {
        throw new NoSuchMethodError("");
    }

    @Override
    public Attribute<? super Z, ?> getAttribute() {
        throw new NoSuchMethodError("");
    }

    @Override
    public From<?, Z> getParent() {
        throw new NoSuchMethodError("");
    }

    @Override
    public JoinType getJoinType() {
        throw new NoSuchMethodError("");
    }
}
