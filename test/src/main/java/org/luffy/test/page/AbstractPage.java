package org.luffy.test.page;

import org.openqa.selenium.WebDriver;

/**
 * 页面的基类
 *
 * @author CJ
 * @deprecated 使用 {@link me.jiangcai.lib.test.page.AbstractPage}代替
 */
public abstract class AbstractPage extends me.jiangcai.lib.test.page.AbstractPage {

    public AbstractPage(WebDriver webDriver) {
        super(webDriver);
    }
}
