package org.luffy.lib.libspring.logging;

import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;

/**
 * @author CJ
 */
@WebAppConfiguration
@ContextConfiguration(classes = {LoggingConfig.class})
public class ConfigManagerTest extends LoggingConfigTest {


    @Test
    public void flow() {
        driver.get("http://localhost/loggingConfig");
        LoggingManagerPage page = initPage(LoggingManagerPage.class);
    }


}
