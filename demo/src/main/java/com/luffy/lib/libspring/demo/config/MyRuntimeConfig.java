package com.luffy.lib.libspring.demo.config;

import org.luffy.lib.libspring.config.RuntimeConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.JpaDialect;
import org.springframework.orm.jpa.vendor.EclipseLinkJpaDialect;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.sql.DataSource;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by CJ on 5/12/15.
 *
 * @author CJ
 */
@Configuration
public class MyRuntimeConfig extends RuntimeConfig{

    @Resource
    private DataSource dataSource;

    @Override
    public boolean containerEnv() {
        return true;
    }

    @Override
    public boolean JTASupport() {
        return false;
    }

    @Override
    public DataSource dataSource() {
        return null;
    }

    @Override
    public String persistenceUnitName() {
        return "abdel";
    }

    @Override
    public Class<? extends JpaDialect> dialectClass() {
        return EclipseLinkJpaDialect.class;
    }

    @Override
    public InputStream propertiesStream() {
        return getClass().getResourceAsStream("jpa.properties");
    }

}
