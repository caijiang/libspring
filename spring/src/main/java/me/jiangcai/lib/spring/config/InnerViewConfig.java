package me.jiangcai.lib.spring.config;

import me.jiangcai.lib.spring.viewresolver.InnerViewResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * 负责载入InnerViewResolver
 *
 * @author CJ
 */
@Configuration
@ComponentScan({"org.luffy.lib.libspring.viewresolver"})
public class InnerViewConfig extends WebMvcConfigurerAdapter {

    @Autowired
    private InnerViewResolver innerViewResolver;

    @Override
    public void configureViewResolvers(ViewResolverRegistry registry) {
        registry.viewResolver(innerViewResolver);
    }

}
