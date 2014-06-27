/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.luffy.lib.libspring.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.EntityManagerFactoryInfo;
import org.springframework.orm.jpa.JpaDialect;

/**
 * 运行时环境
 * @author luffy
 */
@Configuration
public interface RuntimeConfig {
    
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
    public EntityManagerFactoryInfo entityManagerFactory();
    
    /**
     * 最好使用@Inject JpaDialect jpaDialect 
     * jpa方言
     * @return 方言
     * @see org.springframework.orm.jpa.vendor.EclipseLinkJpaDialect
     **/
    @Bean
    public JpaDialect jpaDialect();
    
}
