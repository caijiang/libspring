package me.jiangcai.lib.test.driver;

import org.openqa.selenium.WebDriver;

/**
 * @author CJ
 */
class WebDriverAssert extends AbstractWebDriverAssert<WebDriverAssert,WebDriver> {

    WebDriverAssert(WebDriver actual) {
        super(actual, WebDriverAssert.class);
    }
}
