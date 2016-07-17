package me.jiangcai.lib.test.driver;

import org.assertj.core.api.AbstractAssert;
import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebElement;

import java.util.List;

/**
 * @author CJ
 */
public abstract class AbstractSearchContextAssert<S extends AbstractSearchContextAssert<S, A>, A extends SearchContext>
        extends AbstractAssert<S, A> {

    protected AbstractSearchContextAssert(A actual, Class<?> selfType) {
        super(actual, selfType);
    }

    public AbstractWebElementsAssert<?, ? extends List<? extends WebElement>> elements(By by) {
        return AbstractWebElementsAssert.assertThat(actual.findElements(by));
    }

    public AbstractWebElementAssert<?, ? extends WebElement> element(By by) {
        return AbstractWebElementAssert.assertThat(actual.findElement(by));
    }

}
