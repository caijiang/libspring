package me.jiangcai.lib.test.matcher;

import org.hamcrest.Description;
import org.hamcrest.Matcher;

import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * 简化的Matcher
 *
 * @author CJ
 * @since 1.8
 */
public class SimpleMatcher<T> implements Matcher<T> {
    private final Predicate<T> asExpected;
    private final Consumer<Description> exceptMessage;

    public SimpleMatcher(Predicate<T> asExpected) {
        this(asExpected, null);
    }

    public SimpleMatcher(Predicate<T> asExpected, Consumer<Description> exceptMessage) {
        this.asExpected = asExpected;
        this.exceptMessage = exceptMessage;
    }

    @Override
    public boolean matches(Object item) {
        @SuppressWarnings("unchecked") T target = (T) item;
        return asExpected.test(target);
    }

    @Override
    public void describeMismatch(Object item, Description mismatchDescription) {
        mismatchDescription.appendValue(item);
    }

    @Override
    public void _dont_implement_Matcher___instead_extend_BaseMatcher_() {

    }

    @Override
    public void describeTo(Description description) {
        if (exceptMessage != null)
            exceptMessage.accept(description);
    }
}
