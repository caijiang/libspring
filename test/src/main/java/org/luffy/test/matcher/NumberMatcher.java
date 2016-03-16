package org.luffy.test.matcher;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

/**
 * 数字比较
 *
 * @author CJ
 */
public class NumberMatcher extends BaseMatcher<Number> {

    private static final Log log = LogFactory.getLog(NumberMatcher.class);
    private final boolean asDouble;
    private final Number value;
    /**
     * 0 equals
     * 1 great than
     * 2 grant than or equals
     * 3 less than
     * 4 less than or equals
     */
    private final int type;

    private NumberMatcher(int type, Number value) {
        this(false, type, value);
    }

    private NumberMatcher(boolean asDouble, int type, Number value) {
        this.asDouble = asDouble;
        this.type = type;
        this.value = value;
    }

    public static NumberMatcher numberEquals(Number number) {
        return new NumberMatcher(0, number);
    }

    public static NumberMatcher numberGreatThanOrEquals(Number number) {
        return new NumberMatcher(2, number);
    }

    public static NumberMatcher numberGreatThan(Number number) {
        return new NumberMatcher(1, number);
    }

    public static NumberMatcher numberLessThanOrEquals(Number number) {
        return new NumberMatcher(4, number);
    }

    public static NumberMatcher numberLessThan(Number number) {
        return new NumberMatcher(3, number);
    }

    public static NumberMatcher numberAsDoubleEquals(Number number) {
        return new NumberMatcher(true, 0, number);
    }

    public static NumberMatcher numberAsDoubleGreatThanOrEquals(Number number) {
        return new NumberMatcher(true, 2, number);
    }

    public static NumberMatcher numberAsDoubleGreatThan(Number number) {
        return new NumberMatcher(true, 1, number);
    }

    public static NumberMatcher numberAsDoubleLessThanOrEquals(Number number) {
        return new NumberMatcher(true, 4, number);
    }

    public static NumberMatcher numberAsDoubleLessThan(Number number) {
        return new NumberMatcher(true, 3, number);
    }

    @Override
    public boolean matches(Object o) {
        if (!(o instanceof Number))
            return false;
        Number number = (Number) o;
        if (asDouble) {
            switch (type) {
                case 0:
                    return number.doubleValue() == value.doubleValue();
                case 1:
                    return number.doubleValue() > value.doubleValue();
                case 2:
                    return number.doubleValue() >= value.doubleValue();
                case 3:
                    return number.doubleValue() < value.doubleValue();
                case 4:
                    return number.doubleValue() <= value.doubleValue();
                default:
                    log.warn("未知的类型");
                    return false;
            }
        }
        switch (type) {
            case 0:
                return number.floatValue() == value.floatValue();
            case 1:
                return number.floatValue() > value.floatValue();
            case 2:
                return number.floatValue() >= value.floatValue();
            case 3:
                return number.floatValue() < value.floatValue();
            case 4:
                return number.floatValue() <= value.floatValue();
            default:
                log.warn("未知的类型");
                return false;
        }
    }

    @Override
    public void describeTo(Description description) {
    }
}
