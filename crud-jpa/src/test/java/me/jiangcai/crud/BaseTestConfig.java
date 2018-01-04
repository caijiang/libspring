package me.jiangcai.crud;

import me.jiangcai.lib.test.config.H2DataSourceConfig;
import org.springframework.context.annotation.AdviceMode;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.ImportResource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import javax.sql.DataSource;

/**
 * @author CJ
 */
@Configuration
@EnableWebMvc
@EnableAspectJAutoProxy
@EnableJpaRepositories
@EnableTransactionManagement(mode = AdviceMode.PROXY)
@ComponentScan("me.jiangcai.crud.env.controller")
@ImportResource("classpath:/datasource.xml")
public class BaseTestConfig extends H2DataSourceConfig {
    @Bean
    public DataSource dataSource() {
        return memDataSource("curd");
    }
}
