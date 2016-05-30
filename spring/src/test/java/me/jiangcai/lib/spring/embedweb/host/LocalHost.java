package me.jiangcai.lib.spring.embedweb.host;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.web.servlet.ViewResolver;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.spring4.SpringTemplateEngine;
import org.thymeleaf.spring4.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.spring4.view.ThymeleafViewResolver;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ITemplateResolver;

/**
 * 定义一些宿主需要定义的东西
 *
 * @author CJ
 */
@Import(LocalHost.TemplateEngineLoader.class)
public class LocalHost {

    @Autowired
    private TemplateEngine templateEngine;

    @Bean
    public ViewResolver viewResolver() {
        ThymeleafViewResolver resolver = new ThymeleafViewResolver();
        resolver.setTemplateEngine(templateEngine);
        resolver.setCharacterEncoding("UTF-8");
        return resolver;
    }

    static class TemplateEngineLoader {

        @Autowired
        private ApplicationContext applicationContext;

        @Bean
        public TemplateEngine templateEngine() {
            SpringTemplateEngine engine = new SpringTemplateEngine();
            engine.setEnableSpringELCompiler(true);
            engine.setTemplateResolver(templateResolver());
            return engine;
        }

        private ITemplateResolver templateResolver() {
            SpringResourceTemplateResolver resolver = new SpringResourceTemplateResolver();
            resolver.setApplicationContext(applicationContext);
            resolver.setPrefix("/");
            resolver.setSuffix(".html");
            resolver.setTemplateMode(TemplateMode.HTML);
            return resolver;
        }

    }


}
