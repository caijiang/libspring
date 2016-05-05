package org.luffy.lib.libspring.embedweb.host;

import org.junit.Test;
import org.luffy.test.SpringWebTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;


/**
 * @author CJ
 */
@WebAppConfiguration
@ContextConfiguration(classes = {WebHost.class, InnerWebConfig.class})
public class WebHostTest2 extends SpringWebTest {

    @Test
    public void well() {
    }

}