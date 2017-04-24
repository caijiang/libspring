package me.jiangcai.lib.sys;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * @author CJ
 */
@Configuration
@ComponentScan("me.jiangcai.lib.sys.service")
@EnableJpaRepositories("me.jiangcai.lib.sys.repository")
public class SystemStringConfig {
}
