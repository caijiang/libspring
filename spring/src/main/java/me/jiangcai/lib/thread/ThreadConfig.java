package me.jiangcai.lib.thread;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author CJ
 * @since 3.0
 */
@Configuration
public class ThreadConfig {
    @Bean
    public ThreadJoin threadJoin() {
        return new ThreadJoin();
    }
}
