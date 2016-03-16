package org.luffy.lib.libspring.logging;

import org.junit.Test;

import java.io.IOException;

/**
 * 默认的配置是无法看到低级别输出的
 * @author CJ
 */
public class DefaultContext extends LoggingConfigTest {

    @Test
    public void testNormal() throws IOException {
        disableDebug();
    }
}
