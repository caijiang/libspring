package me.jiangcai.lib.sys;

import me.jiangcai.lib.spring.config.InnerViewConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

/**
 * 支持通过MVC方式管理属性，路径为系统参数<b>me.jiangcai.lib.sys.uri</b>必须以/开头并且以/结尾
 *
 * @author CJ
 * @since 3.0
 */
@Configuration
@Import(InnerViewConfig.class)
@EnableWebMvc
@ComponentScan({"me.jiangcai.lib.sys.service", "me.jiangcai.lib.sys.controller"})
@EnableJpaRepositories("me.jiangcai.lib.sys.repository")
public class SystemStringConfig {
    /**
     * 管理属性所需权限
     */
    public static final String MANAGER_ROLE = "_M_CJ_SYSTEM_STRING";

    @Autowired(required = false)
    private MessageSource messageSource;

    @Bean
    public MessageSource sysMessageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setParentMessageSource(this.messageSource);
        messageSource.setBasename("me/jiangcai/lib/spring/sysMessage");
        return messageSource;
    }

}
