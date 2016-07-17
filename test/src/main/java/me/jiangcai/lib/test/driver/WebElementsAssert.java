package me.jiangcai.lib.test.driver;

import org.openqa.selenium.WebElement;

import java.util.List;

/**
 * @author CJ
 */
class WebElementsAssert extends AbstractWebElementsAssert<WebElementsAssert,List<WebElement>> {

    WebElementsAssert(List<WebElement> actual) {
        super(actual, WebElementsAssert.class);
    }
}
