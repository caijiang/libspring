package me.jiangcai.lib.test.page;

import me.jiangcai.lib.test.driver.AbstractWebDriverAssert;
import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.AbstractBooleanAssert;
import org.assertj.core.api.AbstractCharSequenceAssert;
import org.assertj.core.api.Assertions;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * 针对{@link AbstractPage}的断言
 *
 * @author CJ
 * @since 2.2
 */
public abstract class AbstractPageAssert<S extends AbstractPageAssert<S, A>, A extends AbstractPage>
        extends AbstractAssert<S, A> {

    protected AbstractPageAssert(A actual, Class<?> selfType) {
        super(actual, selfType);
    }

    public static AbstractPageAssert<?, AbstractPage> assertThat(AbstractPage actual) {
        return new PageAssert(actual);
    }

    public AbstractWebDriverAssert<?, ? extends WebDriver> driver() {
        actual.beforeDriver();
        return AbstractWebDriverAssert.assertThat(actual.webDriver);
    }

    public AbstractBooleanAssert<?> checkInput(WebElement form, String name) {
        actual.beforeDriver();
        WebElement input = form.findElement(By.name(name));
        return Assertions.assertThat(actual.inputChecked(input));
    }

    public AbstractCharSequenceAssert<?, ? extends CharSequence> input(WebElement form, String name) {
        actual.beforeDriver();
        WebElement input = form.findElement(By.name(name));
        if (input.getAttribute("type").equalsIgnoreCase("select"))
            // chosen-single
            if (!input.isDisplayed()) {

                WebElement container = form.findElements(By.className("chosen-container"))
                        .stream()
                        .filter(webElement -> webElement.getAttribute("title") != null && webElement.getAttribute("title")
                                .equals(input.getAttribute("title")))
                        .findAny().orElseThrow(() -> new IllegalStateException("使用了chosen-select,但没看到chosen-container"));

                return Assertions.assertThat(container.findElement(By.className("chosen-single")).getText());
            } else {
                return Assertions.assertThat(input.getText());
            }


        return Assertions.assertThat(input.getAttribute("value"));
    }


}
