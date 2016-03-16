package org.luffy.lib.libspring.logging;

import org.luffy.test.page.AbstractPage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author CJ
 */
public class LoggingManagerPage extends AbstractPage {

    private WebElement tbody;
    private WebElement name;
    private WebElement level;
    @FindBy(css = "input[type=submit]")
    private WebElement submitButton;

    public LoggingManagerPage(WebDriver webDriver) {
        super(webDriver);
    }

    @Override
    public void validatePage() {
        assertThat(webDriver.getTitle())
                .isEqualTo("Logging Config Manager");
    }

    /**
     * 删除指定的配置
     *
     * @param name 配置名称
     * @return 现有配置数量
     */
    public int removeConfigSuccess(String name) {
        List<WebElement> trs = tbody.findElements(By.tagName("tr"));
        WebElement targetButton = null;
        for (WebElement webElement : trs) {
            WebElement nameElement = webElement.findElement(By.tagName("td"));
            if (nameElement.getText().equals(name)) {
                targetButton = webElement.findElement(By.tagName("button"));
            }
        }
        if (targetButton == null) {
            return trs.size();
        }
        targetButton.click();
        reloadPageInfo();

        List<WebElement> newTrs = tbody.findElements(By.tagName("tr"));
        assertThat(newTrs)
                .hasSize(trs.size() - 1);

        return newTrs.size();
    }

    /**
     * 新增配置
     *
     * @param name  配置名称
     * @param level 级别
     * @return 现有配置数量
     */
    public int addConfigSuccess(String name, String level) {
        this.name.clear();
        this.name.sendKeys(name);

        this.level.click();
        this.level.findElement(By.name(level)).click();

        submitButton.click();

        reloadPageInfo();
        return tbody.findElements(By.tagName("tr")).size();
    }
}
