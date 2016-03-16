package org.luffy.lib.libspring;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

/**
 * @author CJ
 */
@EnableWebMvc
public class SimpleWeb {

    public static final String SIMPLE_URI = "/foobar";
    private static final Log log = LogFactory.getLog(SimpleWeb.class);

//    @Bean
//    public SimpleController simpleController() {
//        return new SimpleController();
//    }


    @Controller
    public static class SimpleController {
        public SimpleController() {
        }

        @RequestMapping(value = SIMPLE_URI)
        public void foobar() {
            log.info("kick me");
        }
    }
}
