package org.luffy.test.page;

import org.luffy.test.SpringWebTest;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;

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


}
