package me.jiangcai.lib.test.driver;

import org.assertj.core.api.AbstractBooleanAssert;
import org.assertj.core.api.AbstractCharSequenceAssert;
import org.assertj.core.api.Assertions;
import org.openqa.selenium.WebElement;

/**
 * @author CJ
 */
public abstract class AbstractWebElementAssert<S extends AbstractWebElementAssert<S, A>, A extends WebElement>
        extends AbstractSearchContextAssert<S, A> {

    protected AbstractWebElementAssert(A actual, Class<?> selfType) {
        super(actual, selfType);
    }

    public static AbstractWebElementAssert<?, ? extends WebElement> assertThat(WebElement actual) {
        return new WebElementAssert(actual);
    }

    public AbstractCharSequenceAssert<?, ? extends CharSequence> tagName() {
        return Assertions.assertThat(actual.getTagName());
    }

    public AbstractCharSequenceAssert<?, ? extends CharSequence> text() {
        return Assertions.assertThat(actual.getText());
    }

    public AbstractCharSequenceAssert<?, ? extends CharSequence> attribute(String name) {
        return Assertions.assertThat(actual.getAttribute(name));
    }

    public AbstractCharSequenceAssert<?, ? extends CharSequence> cssValue(String name) {
        return Assertions.assertThat(actual.getCssValue(name));
    }

    public AbstractBooleanAssert<?> selected() {
        return Assertions.assertThat(actual.isSelected());
    }

    public AbstractBooleanAssert<?> enabled() {
        return Assertions.assertThat(actual.isEnabled());
    }

    public AbstractBooleanAssert<?> displayed() {
        return Assertions.assertThat(actual.isDisplayed());
    }

}
