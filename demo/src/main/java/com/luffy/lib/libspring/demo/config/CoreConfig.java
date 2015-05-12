package com.luffy.lib.libspring.demo.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Created by CJ on 5/12/15.
 *
 * @author CJ
 */
@Configuration
@ComponentScan("com.luffy.lib.libspring.demo.service")
@EnableJpaRepositories("com.luffy.lib.libspring.demo.repository")
public class CoreConfig {
}
