package com.luffy.lib.libspring.demo.config;

import org.luffy.lib.libspring.config.SecurityConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

/**
 * Created by CJ on 5/12/15.
 *
 * @author CJ
 */
@Configuration
public class MySecurityConfig extends SecurityConfig {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        super.configure(http);

        http.csrf().disable();
    }
}
