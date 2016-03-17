/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2015. All rights reserved.
 */

package org.luffy.lib.libspring.logging;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.AppenderRef;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.luffy.lib.libspring.config.InnerViewConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.EnumerablePropertySource;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

/**
 * 通用可载入的日志配置辅助。
 * 它可以自动实现自定义日志级别，目前还不支持增加日志写入目的。
 * <p>原则上，只要存在任何log4j.开头的属性(包括上下文属性和系统属性)都将该值应用到相关日志级别。</p>
 * <p>比如，设置了一个<code>log4j.org.luffy.lib</code>的属性，值为debug；则将生成新日志级别debug到org.luffy.lib</p>
 * <p>
 * 同样也可以应用于无配置文件，它会采用<code>log4j.root.level</code>作为默认日志级别，该功能在1.9以后生效。
 * </p>
 * <strong>目前仅支持log4j2</strong>
 *
 * @author CJ
 */
@Import(InnerViewConfig.class)
@org.springframework.context.annotation.Configuration
public class LoggingConfig implements ApplicationListener<ContextRefreshedEvent> {

    /**
     * 可配置日志的权限
     */
    public static final String ROLE_MANAGER = "LOGGING_CONFIG";
    public static final String ROOT_LEVEL = "root.level";
    private static final Log log = LogFactory.getLog(LoggingConfig.class);
    @Autowired(required = false)
    private ServletContext servletContext;
    @Autowired
    private ConfigurableEnvironment environment;
    @Autowired
    private LoggingController loggingController;

    @Bean
    public LoggingController loggingController() {
        return new LoggingController();
    }

    @PostConstruct
    public void init() {
        try {
            configLog4j(currentLoggingProperties());
        } catch (Throwable ignored) {

        }
    }

    /**
     * <p>键为包名,值为键值</p>
     * <p>优先级别为servlet参数,spring环境,系统环境</p>
     * <p>必然包含root.level</p>
     *
     * @return 获取当前日志配置
     */
    public Properties currentLoggingProperties() {

        Properties properties = new Properties();

//        System.getProperties().stringPropertyNames()
//                .stream()
//                .filter(name -> name.startsWith("log4j."))
//                .forEach(name -> {
//                    String loggerName = name.substring(6);
//                    properties.setProperty(loggerName, System.getProperty(name, "warn"));
//                });

        environment.getSystemProperties().keySet().stream().filter(name -> name.startsWith("log4j."))
                .forEach(name -> {
                    String loggerName = name.substring(6);
                    properties.setProperty(loggerName
                            , environment.getSystemProperties().getOrDefault(name, "warn").toString());
                });
        properties.setProperty(ROOT_LEVEL, environment.getProperty("log4j.root.level", "info"));

        environment.getPropertySources().forEach(propertySource -> {
//            if (propertySource.containsProperty("log4j.root.level")) {
//                properties.setProperty(ROOT_LEVEL, propertySource.getProperty("log4j.root.level").toString());
//            }
            if (propertySource instanceof EnumerablePropertySource) {
                String[] names = ((EnumerablePropertySource) propertySource).getPropertyNames();
                Arrays.stream(names).filter(name -> name.startsWith("log4j."))
                        .forEach(name -> {
                            String loggerName = name.substring(6);
                            properties.setProperty(loggerName
                                    , propertySource.getProperty(name).toString());
                        });
            }
        });

        if (servletContext != null) {
            Enumeration<String> names = servletContext.getInitParameterNames();
            while (names.hasMoreElements()) {
                String name = names.nextElement();
                if (name.startsWith("log4j.")) {
                    String loggerName = name.substring(6);
                    properties.setProperty(loggerName, servletContext.getInitParameter(name));
                }
            }
//            String levelName = servletContext.getInitParameter("log4j.root.level");
//            if (levelName != null) {
//                properties.setProperty(ROOT_LEVEL, levelName);
//            }
        }

        if (loggingController.getManageableConfigs() != null) {
            loggingController.getManageableConfigs().forEach(properties::put);
        }

        return properties;
    }

    public void configLog4j(Properties properties) {
        final LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
        final Configuration config = ctx.getConfiguration();

        Map<String, Appender> appenderMap = config.getAppenders();

        //如果没有配置 则默认输出级别更正为info 如果设置了 log4j.root.level 则依赖此项
        if (appenderMap.size() == 1) {
            ctx.getConfiguration().getRootLogger().setLevel(Level.toLevel(properties.getProperty(ROOT_LEVEL)
                    , Level.INFO));
        }

        if (appenderMap.isEmpty())
            return;
        AppenderRef[] refs = appenderMap.keySet().stream()
                .map(name -> AppenderRef.createAppenderRef(name, null, null)).toArray(AppenderRef[]::new);

        properties.stringPropertyNames().stream()
                .filter(name -> !Objects.equals(name, ROOT_LEVEL))
                .forEach(loggerName -> {
                    // remove first
                    config.removeLogger(loggerName);

                    LoggerConfig loggerConfig = LoggerConfig.createLogger("false"
                            , Level.toLevel(properties.getProperty(loggerName)), loggerName, "true", refs, null
                            , config, null);
                    config.addLogger(loggerName, loggerConfig);
                    appenderMap.values().forEach(appender -> loggerConfig.addAppender(appender, null, null));
                });

        ctx.updateLoggers();
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        log.debug("Refresh Logging Config on start.");
        try {
            configLog4j(currentLoggingProperties());
        } catch (Throwable ignored) {

        }
    }

    @EventListener(classes = RefreshLoggingEvent.class)
    public void refreshLogging() {
        log.debug("Refresh Logging Config on event.");
        try {
            configLog4j(currentLoggingProperties());
        } catch (Throwable ignored) {

        }
    }
}
