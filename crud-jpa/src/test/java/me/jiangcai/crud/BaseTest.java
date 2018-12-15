package me.jiangcai.crud;

import me.jiangcai.crud.event.EntityVariationEvent;
import me.jiangcai.lib.test.SpringWebTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;

/**
 * @author CJ
 */
@ContextConfiguration(classes = {CrudConfig.class, BaseTest.MyConfig.class, BaseTestConfig.class})
@WebAppConfiguration
public abstract class BaseTest extends SpringWebTest {

    @Configuration
    public static class MyConfig {
        @EventListener(EntityVariationEvent.class)
        public void fire(EntityVariationEvent event) {
            System.out.println("" + event.getVariationType() + event.getTarget());
        }
    }
}
