/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.luffy.lib.libspring.config;

import javax.inject.Inject;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.AbstractEntityManagerFactoryBean;
import org.springframework.orm.jpa.EntityManagerFactoryInfo;
import org.springframework.orm.jpa.JpaDialect;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.LocalEntityManagerFactoryBean;

/**
 * 运行时环境
 * @author luffy
 */
@Configuration
public abstract class RuntimeConfig {
    
    @Inject
    private JpaDialect jpaDialect;
    
    /**
     * 是否生产环境
     * @return 
     **/
    public abstract boolean containerEnv();
    
    public abstract String persistenceUnitName();
    
    public abstract Class<? extends JpaDialect> dialectClass();
    
    public boolean disableWeaving(){
        return true;
    }
    
    /**
     * 获取当前entityManagerFactoryBeanInfo
     * 本机环境一般为LocalEntityManagerFactoryBean
     * 生产环境一般为LocalContainerEntityManagerFactoryBean
     * 
     * @see org.springframework.orm.jpa.LocalEntityManagerFactoryBean
     * @see org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean
     * @return 
     **/
    @Bean
    public EntityManagerFactoryInfo entityManagerFactory(){
        AbstractEntityManagerFactoryBean bean;
        if (containerEnv())
            bean = new LocalContainerEntityManagerFactoryBean();
        else
            bean = new LocalEntityManagerFactoryBean();
        bean.setJpaDialect(jpaDialect);
        bean.setPersistenceUnitName(persistenceUnitName());

        /*forbid the weaving
        for enable it
        step 1
        <property name="loadTimeWeaver">
        <bean class="org.springframework.instrument.classloading.InstrumentationLoadTimeWeaver"/>\
        </property>

        to application beans

        step 2 Then add this option to your JVM :
        -javaagent:/path-to-your-javaagent/org.springframework.instrument-3.1.1.RELEASE.jar

        In Spring 3.x the javaagent is localized in the org.springframework.instrument jar.
        You need the org.springframework.instrument library together with aspectjrj.jar & aspectjweaver.jar librairies.

                */
        if(disableWeaving())
            bean.getJpaPropertyMap().put("eclipselink.weaving", "false");
        return bean;
    }
    
    /**
     * 最好使用@Inject JpaDialect jpaDialect 
     * jpa方言
     * @return 方言
     * @see org.springframework.orm.jpa.vendor.EclipseLinkJpaDialect
     **/
    @Bean
    public JpaDialect jpaDialect(){
        try{
            return this.dialectClass().newInstance();
        }catch(InstantiationException | IllegalAccessException ex){
            assert false:"初始化JPA方言失败";
            return null;
        }
    }
    
}
