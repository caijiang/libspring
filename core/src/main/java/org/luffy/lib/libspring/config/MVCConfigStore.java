package org.luffy.lib.libspring.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.thymeleaf.spring3.SpringTemplateEngine;
import org.thymeleaf.spring3.view.ThymeleafViewResolver;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

/**
 * <p>内置了一个ThymeleafViewResolver 它会自动读取/WEB-INF/templates/的html文件作为view</p>
 * Created by CJ on 5/12/15.
 *
 * @author CJ
 */
@Configuration
public class MVCConfigStore extends WebMvcConfigurationSupport {

    @Autowired
    private Environment env;


    /**
     * for upload
     */
    @Bean
    public CommonsMultipartResolver multipartResolver(){
        return new CommonsMultipartResolver();
    }

    @Bean
    public ThymeleafViewResolver thymeleafViewResolver() {
        ThymeleafViewResolver bean = new ThymeleafViewResolver();
        bean.setTemplateEngine(this.templateEngine());
        bean.setOrder(1);
        bean.setCharacterEncoding("UTF-8");
        return bean;
    }

    public SpringTemplateEngine templateEngine() {
        SpringTemplateEngine bean = new SpringTemplateEngine();
        bean.setTemplateResolver(this.templateResolver());
        return bean;
    }


    public ServletContextTemplateResolver templateResolver() {
        ServletContextTemplateResolver bean = new ServletContextTemplateResolver();
        bean.setPrefix("/WEB-INF/templates/");
        bean.setSuffix(".html");
        bean.setCharacterEncoding("UTF-8");
        if(env.acceptsProfiles("dev")){
            System.out.println("Develement Mode");
            bean.setCacheable(false);
        }
//        bean.setTemplateMode("HTML5");
        return bean;
    }

}
