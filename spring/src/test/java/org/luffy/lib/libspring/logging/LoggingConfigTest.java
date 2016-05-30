package org.luffy.lib.libspring.logging;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.luffy.libs.libseext.IOUtils;
import org.luffy.test.SpringWebTest;

import java.io.FileInputStream;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author CJ
 */
//@WebAppConfiguration
//@ContextConfiguration(classes = {LoggingConfig.class})
public abstract class LoggingConfigTest extends SpringWebTest {

    private static final Log log = LogFactory.getLog(LoggingConfigTest.class);

    public void enableDebug() throws IOException {
        log.error("error");

        assertThat(lastLogMessage())
                .isEqualTo("error")
                .as("看不到错误信息");

        log.debug("debug");
        assertThat(lastLogMessage())
                .isEqualTo("debug")
                .as("应该记录debug信息");
    }

    public void disableDebug() throws IOException {
        log.error("error");

        assertThat(lastLogMessage())
                .isEqualTo("error")
                .as("看不到错误信息");

        log.debug("debug");
        assertThat(lastLogMessage())
                .isNotEqualTo("debug")
                .as("不应该记录debug信息");
    }


    protected String lastLogMessage() throws IOException {
        FileInputStream fileInputStream = new FileInputStream("target/log.log");
        StringBuilder stringBuilder = new StringBuilder();
        IOUtils.processString(fileInputStream, "UTF-8", str -> {
            stringBuilder.setLength(0);
            stringBuilder.append(str);
            return false;
        });
        return stringBuilder.toString();
    }

}