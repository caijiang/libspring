package me.jiangcai.crud.row.field.fake;

import org.springframework.beans.BeanUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.function.Function;

/**
 * 可以获取值
 *
 * @author CJ
 */
public abstract class AbstractFake {
    private Function<Object, Object> function = Function.identity();

    public AbstractFake(AbstractFake fake) {
        this.function = fake.function;
    }

    public AbstractFake() {
    }

    protected void andThen(Function<Object, Object> next) {
        function = function.andThen(next);
    }

    protected void andThenForName(String name) {
        andThen(entity -> {
            try {
                return BeanUtils.getPropertyDescriptor(entity.getClass(), name).getReadMethod().invoke(entity);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new IllegalStateException(e);
            }
        });

    }

    public Object toValue(Object entity) {
        return function.apply(entity);
    }
}
