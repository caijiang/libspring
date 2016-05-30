package me.jiangcai.lib.embedweb.ewp;

import me.jiangcai.lib.embedweb.EmbedWeb;
import me.jiangcai.lib.embedweb.thymeleaf.EWPProcessorDialect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.spring4.SpringTemplateEngine;
import org.thymeleaf.spring4.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.spring4.view.ThymeleafViewResolver;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ITemplateResolver;

/**
 * <pre>@Import(MockMVC.TemplateEngineLoader.class)</pre>
 *
 * @author CJ
 */
@Import(MockMVC.TemplateEngineLoader.class)
@ComponentScan({"org.luffy.lib.libspring.embedweb.ewp.service", "org.luffy.lib.libspring.embedweb.thymeleaf"})
@Configuration
@EnableWebMvc
public abstract class MockMVC extends WebMvcConfigurerAdapter {

    public static final String PUBLIC_STORE = "/_ewp_";
    private EmbedWeb embedWeb;
    @Autowired
    private TemplateEngine templateEngine;
    @Autowired
    private EWPProcessorDialect ewpProcessorDialect;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        super.addResourceHandlers(registry);

        registry.addResourceHandler(PUBLIC_STORE + "/**").addResourceLocations("classpath:" + embedWeb.publicResourcePath()
                + "/");
    }

    /**
     * @return 当前EWP项目所关联的实例
     */
    public EmbedWeb embedWeb() {
        return embedWeb;
    }

    public void setEmbedWeb(EmbedWeb embedWeb) {
        this.embedWeb = embedWeb;

        for (ITemplateResolver templateResolver : templateEngine.getTemplateResolvers()) {
            SpringResourceTemplateResolver springResourceTemplateResolver = (SpringResourceTemplateResolver) templateResolver;
            springResourceTemplateResolver.setPrefix("classpath:" + embedWeb.privateResourcePath() + "/");
        }
    }

    @Bean
    public ViewResolver viewResolver() {
        templateEngine.addDialect(ewpProcessorDialect);
        ThymeleafViewResolver resolver = new ThymeleafViewResolver();
        resolver.setTemplateEngine(templateEngine);
        resolver.setCharacterEncoding("UTF-8");
        return resolver;
    }

    public static class TemplateEngineLoader {

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
            resolver.setSuffix(".html");
            resolver.setTemplateMode(TemplateMode.HTML);
            return resolver;
        }

    }


}
