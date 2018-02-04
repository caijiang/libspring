package org.luffy.lib.libspring.logging;

import me.jiangcai.lib.spring.logging.LoggingConfig;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author CJ
 */
@WebAppConfiguration
@ContextConfiguration(classes = {LoggingConfig.class})
public class ConfigManagerTest extends LoggingConfigTest {

    private static final Log log = LogFactory.getLog(ConfigManagerTest.class);

    private static final String NAME = ConfigManagerTest.class.getPackage().getName();

    @Test
    public void flow() throws IOException, InterruptedException {
        driver.get("http://localhost/loggingConfig");
        LoggingManagerPage page = initPage(LoggingManagerPage.class);

        page.addConfigSuccess(NAME, "INFO");

        disableDebug();

        // 一开始几个? anyway 先删除
        int size = page.removeConfigSuccess(NAME);

        int newSize = page.addConfigSuccess(NAME, "ALL");

        assertThat(newSize)
                .isEqualTo(size + 1);

        enableDebug();

        size = page.addConfigSuccess(NAME, "INFO");
        assertThat(size).isEqualTo(newSize);

        disableDebug();

    }


}
