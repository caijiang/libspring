package org.luffy.lib.libspring.logging;

import org.luffy.test.page.AbstractPage;
import org.openqa.selenium.WebDriver;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author CJ
 */
public class LoggingManagerPage extends AbstractPage {

    public LoggingManagerPage(WebDriver webDriver) {
        super(webDriver);
    }

    @Override
    public void validatePage() {
        assertThat(webDriver.getTitle())
                .isEqualTo("Logging Config Manager");
    }
}
