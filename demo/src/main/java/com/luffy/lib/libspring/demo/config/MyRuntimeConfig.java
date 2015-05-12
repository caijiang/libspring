package com.luffy.lib.libspring.demo.config;

import org.luffy.lib.libspring.config.RuntimeConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.JpaDialect;
import org.springframework.orm.jpa.vendor.EclipseLinkJpaDialect;

/**
 * Created by CJ on 5/12/15.
 *
 * @author CJ
 */
@Configuration
public class MyRuntimeConfig extends RuntimeConfig{
    @Override
    public boolean containerEnv() {
        return false;
    }

    @Override
    public String persistenceUnitName() {
        return "abdel";
    }

    @Override
    public Class<? extends JpaDialect> dialectClass() {
        return EclipseLinkJpaDialect.class;
    }
}
