package org.luffy.lib.libspring.logging;

import org.junit.Test;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.ContextConfiguration;

import java.io.IOException;

/**
 * 如果配置调整过,那么应该可以看到低级别输出
 * @author CJ
 */
@ContextConfiguration(classes = {LoggingConfig.class, ChangedContext.ChangedContextConfig.class})
public class ChangedContext extends LoggingConfigTest {

    @Configuration
    @PropertySource("classpath:/enableDebug.properties")
    public static class ChangedContextConfig {

    }

    @Test
    public void testNormal() throws IOException {
        enableDebug();
    }
}
