package me.jiangcai.lib.test.page;

import com.google.common.base.Predicate;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * driver方面的工具包
 *
 * @since 3.0
 */
public class WebDriverUtil {
    public static void waitFor(WebDriver driver, java.util.function.Predicate<WebDriver> predicate, int seconds) {
        new WebDriverWait(driver, seconds)
                .until((Predicate<WebDriver>) driver1 -> driver1 != null && predicate.test(driver1));
    }
}
