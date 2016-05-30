/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.luffy.lib.libspring.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.AbstractEntityManagerFactoryBean;
import org.springframework.orm.jpa.EntityManagerFactoryInfo;
import org.springframework.orm.jpa.JpaDialect;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.LocalEntityManagerFactoryBean;

import javax.inject.Inject;

/**
 * <p>运行时环境</p>
 * <p>所有方法都可被覆盖</p>
 * @deprecated since 2.0 not supported
 * @author luffy
 */
@SuppressWarnings("CdiManagedBeanInconsistencyInspection")
@Configuration
@Deprecated
public abstract class RuntimeConfig {
    
    @Inject
    private JpaDialect jpaDialect;
    
    /**
     * 是否生产环境
     * @return 
     **/
    public abstract boolean containerEnv();

//    /**
//     * 是否生产环境
//     * @return
//     **/
//    public abstract boolean JTASupport();
//
//    /**
//     * @return 数据源
//     * **/
//    public abstract DataSource dataSource();

    /**
     * @return 持久化单元名称
     **/
    public abstract String persistenceUnitName();

    /**
     * @return JPA实现者方言
     * */
    public abstract Class<? extends JpaDialect> dialectClass();

    /**
     * @return 是否禁止Weaving 默认true
     * */
    public boolean disableWeaving(){
        return true;
    }

//    public abstract InputStream propertiesStream();

    /**
     * 获取当前entityManagerFactoryBeanInfo
     * 本机环境一般为LocalEntityManagerFactoryBean
     * 生产环境一般为LocalContainerEntityManagerFactoryBean
     * 
     * @see LocalEntityManagerFactoryBean
     * @see LocalContainerEntityManagerFactoryBean
     * @return 
     **/
    @Bean
    public EntityManagerFactoryInfo entityManagerFactory(){
        AbstractEntityManagerFactoryBean bean;
        if (containerEnv()){
            bean = new LocalContainerEntityManagerFactoryBean();
//            LocalContainerEntityManagerFactoryBean cbean = (LocalContainerEntityManagerFactoryBean) bean;
//            if (JTASupport()){
//                cbean.setJtaDataSource(dataSource());
//            }else{
//                cbean.setDataSource(dataSource());
//            }
        }
        else{
            bean = new LocalEntityManagerFactoryBean();
        }

        bean.setJpaDialect(jpaDialect);

//        Properties ps = new Properties();
//        InputStream in = propertiesStream();
//        if (in!=null){
//            try {
//                ps.load(in);
//            } catch (IOException e) {
//                throw new IllegalStateException(e);
//            } finally {
//                try {
//                    in.close();
//                } catch (IOException e) {
//                }
//            }
//        }
//        bean.setJpaProperties(ps);

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
