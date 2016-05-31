package me.jiangcai.lib.jdbc;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * 载入Jdbc服务的Spring配置
 * @author CJ
 */
@Configuration
@ComponentScan("me.jiangcai.lib.jdbc.impl")
public class JdbcSpringConfig {
}
