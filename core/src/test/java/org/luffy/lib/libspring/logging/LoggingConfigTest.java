package org.luffy.lib.libspring.logging;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.luffy.libs.libseext.IOUtils;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.FileInputStream;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author CJ
 */
//@WebAppConfiguration
@ActiveProfiles("test")
@ContextConfiguration(classes = {LoggingConfig.class})
@RunWith(SpringJUnit4ClassRunner.class)
public abstract class LoggingConfigTest {

    private static final Log log = LogFactory.getLog(LoggingConfigTest.class);

    public void enableDebug() throws IOException {
        log.error("error");

        assertThat(lastLogMessage())
                .isEqualTo("error");

        log.debug("debug");
        assertThat(lastLogMessage())
                .isEqualTo("debug");
    }

    public void disableDebug() throws IOException {
        log.error("error");

        assertThat(lastLogMessage())
                .isEqualTo("error");

        log.debug("debug");
        assertThat(lastLogMessage())
                .isNotEqualTo("debug");
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