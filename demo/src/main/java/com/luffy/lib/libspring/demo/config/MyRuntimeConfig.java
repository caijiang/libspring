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
        return true;
    }

//    //    @Resource(lookup = "jdbc/sdemo")
//    private DataSource dataSource;
//
//    @Autowired
//    private ServletContext servletContext;
//
//    @Override
//    public boolean JTASupport() {
//        if (servletContext.getServerInfo().contains("GlassFish")){
//            return true;
//        }
//        return false;
//    }
//
//    @Override
//    public DataSource dataSource() {
//        return dataSource;
//    }
//
//    @Override
//    public InputStream propertiesStream() {
//        return getClass().getResourceAsStream("jpa.properties");
//    }

    @Override
    public String persistenceUnitName() {
        return "abdel";
    }

    @Override
    public Class<? extends JpaDialect> dialectClass() {
        return EclipseLinkJpaDialect.class;
    }



}
