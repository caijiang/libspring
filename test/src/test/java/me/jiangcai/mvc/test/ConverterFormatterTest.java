package me.jiangcai.mvc.test;

import me.jiangcai.mvc.test.bean.ForLove;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.format.FormatterRegistry;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * @author CJ
 */
@ContextConfiguration(classes = ConverterFormatterTest.FormatterConfig.class)
public class ConverterFormatterTest extends TypeTest {
    @EnableWebMvc
    static class FormatterConfig extends WebMvcConfigurerAdapter{

        @Autowired
        protected ForLove forLove;

        @Override
        public void addFormatters(FormatterRegistry registry) {
            super.addFormatters(registry);
            registry.addConverter(forLove);
        }
    }
}
