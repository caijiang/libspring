/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.luffy.lib.libspring.config;

import javax.inject.Inject;
import javax.persistence.EntityManagerFactory;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaDialect;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.support.ResourceTransactionManager;

/**
 * 使用JPA时应该使用该配置
 * @deprecated since 2.0 not supported
 * @author luffy
 */
@Configuration
@EnableJpaRepositories
@DependsOn("entityManagerFactory")
@Deprecated
public class LibJpaConfig {
   
    @Inject
    private org.luffy.lib.libspring.config.RuntimeConfig runtime;
    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Inject
    private EntityManagerFactory entityManagerFactory;
    @Inject
    private JpaDialect jpaDialect;
    
    @Bean
    public ResourceTransactionManager transactionManager(){
        JpaTransactionManager bean = new JpaTransactionManager();
        bean.setEntityManagerFactory(entityManagerFactory);
        return bean;
    }
    
}
