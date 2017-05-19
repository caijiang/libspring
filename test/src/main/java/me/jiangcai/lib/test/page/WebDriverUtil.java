package me.jiangcai.lib.test.page;

import com.gargoylesoftware.htmlunit.html.HtmlElement;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitWebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.lang.reflect.Field;

/**
 * driver方面的工具包
 *
 * @since 3.0
 */
public class WebDriverUtil {

    /**
     * 让浏览器等一会儿，知道条件达成
     *
     * @param driver    driver
     * @param predicate 条件
     * @param seconds   最长等待描述
     */
    public static void waitFor(WebDriver driver, java.util.function.Predicate<WebDriver> predicate, int seconds) {
        new WebDriverWait(driver, seconds).until(input -> input != null && predicate.test(input));
//        new WebDriverWait(driver, seconds)
//                .until((Predicate<WebDriver>) driver1 -> driver1 != null && predicate.test(driver1));
    }

    /**
     * @param element html unit driver的WebElement
     * @return 获取原生DOM元素
     */
    public static HtmlElement getHtmlElement(WebElement element) {
        try {
            Field field = HtmlUnitWebElement.class.getDeclaredField("element");
            field.setAccessible(true);
            return (HtmlElement) field.get(element);
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
    }
}
