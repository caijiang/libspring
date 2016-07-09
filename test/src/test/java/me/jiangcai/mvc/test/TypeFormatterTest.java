package me.jiangcai.mvc.test;

import me.jiangcai.mvc.test.bean.ForLove;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.FormatterRegistry;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.lang.reflect.Type;

/**
 * @author CJ
 */
@ContextConfiguration(classes = TypeFormatterTest.FormatterConfig.class)
public class TypeFormatterTest extends TypeTest {
    @EnableWebMvc
    static class FormatterConfig extends WebMvcConfigurerAdapter{

        @Autowired
        protected ForLove forLove;

        @Override
        public void addFormatters(FormatterRegistry registry) {
            super.addFormatters(registry);
            registry.addFormatter(forLove);
        }
    }
}
