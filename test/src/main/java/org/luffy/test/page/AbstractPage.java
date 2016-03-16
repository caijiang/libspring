package org.luffy.test.page;

import org.luffy.test.SpringWebTest;
import org.openqa.selenium.WebDriver;

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


}
