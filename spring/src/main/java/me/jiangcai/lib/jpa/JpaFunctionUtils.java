package me.jiangcai.lib.jpa;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import java.lang.reflect.Array;

/**
 * @author CJ
 * @since 4.0
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class JpaFunctionUtils {

    /**
     * @return 将args使用 {@link CriteriaBuilder#concat(Expression, Expression)}链接起来
     */
    @SafeVarargs
    public static Expression<String> contact(CriteriaBuilder criteriaBuilder, Expression<String>... args) {
        if (args.length == 1)
            return criteriaBuilder.concat(args[0], criteriaBuilder.literal(""));
        if (args.length == 2)
            return criteriaBuilder.concat(args[0], args[1]);
        // 合并
        @SuppressWarnings("unchecked")
        Expression<String>[] newArgs = (Expression<String>[]) Array.newInstance(Expression.class
                , args.length / 2 + (args.length % 2 == 1 ? 1 : 0));
        for (int i = 0; i < newArgs.length; i++) {
            // 有2个么？
            // 最后一个而且有多的
            if (i == newArgs.length - 1 && args.length % 2 == 1)
                newArgs[i] = args[i * 2];
            else
                newArgs[i] = criteriaBuilder.concat(args[i * 2], args[i * 2 + 1]);
        }
        return contact(criteriaBuilder, newArgs);
    }
}
