package org.luffy.lib.libspring.config;

import org.luffy.lib.libspring.viewresolver.InnerViewResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * Created by luffy on 2015/5/19.
 * 配置了一些基础controller
 *
 * @author luffy luffy.ja at gmail.com
 */
@EnableWebMvc
@Configuration
@DependsOn("innerViewResolver")
@ComponentScan({"org.luffy.lib.libspring.viewresolver","org.luffy.lib.libspring.controller"})
public class MVCConfig extends WebMvcConfigurerAdapter{

    @Autowired
    private InnerViewResolver innerViewResolver;

    @Override
    public void configureViewResolvers(ViewResolverRegistry registry) {
        registry.viewResolver(innerViewResolver);
    }
}