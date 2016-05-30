package me.jiangcai.lib.test.page;

import me.jiangcai.lib.test.SpringWebTest;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.function.Consumer;

/**
 * 页面的基类
 *
 * @author CJ
 */
public abstract class AbstractPage {

    protected final WebDriver webDriver;
    /**
     * 操作该页面的测试实例
     */
    private SpringWebTest TestInstance;

    public AbstractPage(WebDriver webDriver) {
        this.webDriver = webDriver;
    }

    public SpringWebTest getTestInstance() {
        return TestInstance;
    }

    public void setTestInstance(SpringWebTest testInstance) {
        TestInstance = testInstance;
    }

    /**
     * 验证该页面
     */
    public abstract void validatePage();

    /**
     * 刷新当前页面,跟浏览器刷新是一致的,并在完成之后调用验证
     */
    public void refresh() {
        webDriver.navigate().refresh();
        PageFactory.initElements(webDriver, this);
        validatePage();
    }

    /**
     * 重新载入当前逻辑页面信息,并在完成之后调用验证
     */
    public void reloadPageInfo() {
        PageFactory.initElements(webDriver, this);
        validatePage();
    }

    /**
     * 等待,只到当前页面符合指定条件
     * <p>
     * 完成后会调用 {@link #reloadPageInfo() 刷新}
     * </p>
     *
     * @param waitConsumer 条件给予者
     */
    public void waitOn(Consumer<FluentWait<WebDriver>> waitConsumer) throws InterruptedException {
        Thread.sleep(500L);
        WebDriverWait wait = new WebDriverWait(webDriver, 10);
        wait.ignoring(NoSuchElementException.class);
        waitConsumer.accept(wait);
        reloadPageInfo();
    }


}
