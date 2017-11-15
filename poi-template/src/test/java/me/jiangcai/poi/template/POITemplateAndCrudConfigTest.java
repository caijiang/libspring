package me.jiangcai.poi.template;

import me.jiangcai.lib.test.SpringWebTest;
import me.jiangcai.lib.test.config.H2DataSourceConfig;
import org.junit.Test;
import org.springframework.context.annotation.AdviceMode;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.util.StreamUtils;
import org.springframework.validation.MessageCodesResolver;
import org.springframework.validation.Validator;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.config.annotation.AsyncSupportConfigurer;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.sql.DataSource;
import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author CJ
 */
@ContextConfiguration(classes = POITemplateAndCrudConfigTest.Config.class)
@WebAppConfiguration
@ActiveProfiles({"test"})
public class POITemplateAndCrudConfigTest extends SpringWebTest {

    @Test
    public void go() throws Exception {
        byte[] responseBuffer = mockMvc.perform(get("/message"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsByteArray();

        //  如果桌面支持 则将它写入到一个临时文件
        if (Desktop.isDesktopSupported()) {
            File file = new File("target/POITemplateAndCrudConfigTest.go.xls");
            StreamUtils.copy(responseBuffer, new FileOutputStream(file));
            Desktop.getDesktop().open(file);
        }
    }

    @Configuration
    @ImportResource(locations = "classpath:/datasource_local.xml")
    @EnableTransactionManagement(mode = AdviceMode.PROXY)
    @EnableJpaRepositories("me.jiangcai.poi.template.test.repository")
    @EnableAspectJAutoProxy
    @Import({POITemplateAndCrudConfig.class})
    @ComponentScan("me.jiangcai.poi.template.test.controller")
    @EnableWebMvc
    public static class Config extends H2DataSourceConfig implements WebMvcConfigurer {
        @Bean
        public DataSource dataSource() {
            return memDataSource("foobar");
        }

        @Override
        public void configurePathMatch(PathMatchConfigurer configurer) {

        }

        @Override
        public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {

        }

        @Override
        public void configureAsyncSupport(AsyncSupportConfigurer configurer) {

        }

        @Override
        public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {

        }

        @Override
        public void addFormatters(FormatterRegistry registry) {

        }

        @Override
        public void addInterceptors(InterceptorRegistry registry) {

        }

        @Override
        public void addResourceHandlers(ResourceHandlerRegistry registry) {

        }

        @Override
        public void addCorsMappings(CorsRegistry registry) {

        }

        @Override
        public void addViewControllers(ViewControllerRegistry registry) {

        }

        @Override
        public void configureViewResolvers(ViewResolverRegistry registry) {

        }

        @Override
        public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {

        }

        @Override
        public void addReturnValueHandlers(List<HandlerMethodReturnValueHandler> returnValueHandlers) {

        }

        @Override
        public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {

        }

        @Override
        public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {

        }

        @Override
        public void configureHandlerExceptionResolvers(List<HandlerExceptionResolver> exceptionResolvers) {

        }

        @Override
        public void extendHandlerExceptionResolvers(List<HandlerExceptionResolver> exceptionResolvers) {

        }

        @Override
        public Validator getValidator() {
            return null;
        }

        @Override
        public MessageCodesResolver getMessageCodesResolver() {
            return null;
        }
        // 安装JPA环境
    }
}