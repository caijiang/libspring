package me.jiangcai.lib.resource.web;

import me.jiangcai.lib.resource.ResourceSpringConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * {@link me.jiangcai.lib.resource.ResourceSpringConfig}的超集，提供了直接以MVC方式的资源上传
 * TODO 安全系统现在很简陋只要已认证即可访问
 *
 * @author CJ
 */
@Configuration
@EnableWebMvc
@Import(ResourceSpringConfig.class)
@ComponentScan("me.jiangcai.lib.resource.web.bean")
public class WebResourceSpringConfig extends WebMvcConfigurerAdapter {
    /**
     * 文件上传
     */
    @Bean
    public CommonsMultipartResolver multipartResolver() {
        return new CommonsMultipartResolver();
    }

}
