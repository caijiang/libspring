package me.jiangcai.lib.test.driver;

import org.assertj.core.api.AbstractCharSequenceAssert;
import org.assertj.core.api.Assertions;
import org.openqa.selenium.WebDriver;

/**
 * @author CJ
 */
public abstract class AbstractWebDriverAssert<S extends AbstractWebDriverAssert<S, A>, A extends WebDriver>
        extends AbstractSearchContextAssert<S, A> {

    protected AbstractWebDriverAssert(A actual, Class<?> selfType) {
        super(actual, selfType);
    }

    public static AbstractWebDriverAssert<?, ? extends WebDriver> assertThat(WebDriver actual) {
        return new WebDriverAssert(actual);
    }

    public AbstractCharSequenceAssert<?, ? extends CharSequence> currentUrl() {
        return Assertions.assertThat(actual.getCurrentUrl());
    }

    public AbstractCharSequenceAssert<?, ? extends CharSequence> pageSource() {
        return Assertions.assertThat(actual.getPageSource());
    }

    public AbstractCharSequenceAssert<?, ? extends CharSequence> title() {
        return Assertions.assertThat(actual.getTitle());
    }

}
