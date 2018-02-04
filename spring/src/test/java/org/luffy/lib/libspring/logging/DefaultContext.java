package org.luffy.lib.libspring.logging;

import me.jiangcai.lib.spring.logging.LoggingConfig;
import org.junit.Test;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.ContextConfiguration;

import java.io.IOException;

/**
 * 默认的配置是无法看到低级别输出的
 *
 * @author CJ
 */
//@Ignore
@ContextConfiguration(classes = {LoggingConfig.class, DefaultContext.DefaultContextConfig.class})
public class DefaultContext extends LoggingConfigTest {

    @Test
    public void testNormal() throws IOException {
        disableDebug();
    }

    @Configuration
    @PropertySource("classpath:/disableDebug.properties")
    public static class DefaultContextConfig {

    }
}
