package me.jiangcai.lib.test.driver;

import org.openqa.selenium.WebElement;

/**
 * @author CJ
 */
class WebElementAssert extends AbstractWebElementAssert<WebElementAssert, WebElement> {

    WebElementAssert(WebElement actual) {
        super(actual, WebElementAssert.class);
    }
}
