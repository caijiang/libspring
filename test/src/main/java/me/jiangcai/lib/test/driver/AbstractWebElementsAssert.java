package me.jiangcai.lib.test.driver;

import org.assertj.core.api.AbstractListAssert;
import org.openqa.selenium.WebElement;

import java.util.List;

/**
 * @author CJ
 */
public abstract class AbstractWebElementsAssert<S extends AbstractWebElementsAssert<S, A>, A extends List<? extends WebElement>>
        extends AbstractListAssert<S, A, WebElement> {

    protected AbstractWebElementsAssert(A actual, Class<?> selfType) {
        super(actual, selfType);
    }

    public static AbstractWebElementsAssert<?, ? extends List<? extends WebElement>> assertThat(List<WebElement> actual) {
        return new WebElementsAssert(actual);
    }


}
