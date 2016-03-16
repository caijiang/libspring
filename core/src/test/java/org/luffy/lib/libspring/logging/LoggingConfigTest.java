package org.luffy.lib.libspring.logging;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author CJ
 */
//@WebAppConfiguration
@ActiveProfiles("test")
@ContextConfiguration(classes = {LoggingConfig.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class LoggingConfigTest {

    private static final Log log = LogFactory.getLog(LoggingConfigTest.class);

    @Test
    public void testInit() throws Exception {
        log.info("hah");
    }
}