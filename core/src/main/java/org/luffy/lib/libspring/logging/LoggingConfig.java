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
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;
import java.util.Enumeration;
import java.util.Map;

/**
 * 通用可载入的日志配置辅助。
 * 它可以自动实现自定义日志级别，目前还不支持增加日志写入目的。
 * <p>原则上，只要存在任何log4j.开头的属性(包括上下文属性和系统属性)都将该值应用到相关日志级别。</p>
 * <p>比如，设置了一个<code>log4j.org.luffy.lib</code>的属性，值为debug；则将生成新日志级别debug到org.luffy.lib</p>
 * <strong>目前仅支持log4j2</strong>
 *
 * @author CJ
 */
@org.springframework.context.annotation.Configuration
//@ComponentScan("com.huotu.test.bean")
public class LoggingConfig {

    private static final Log log = LogFactory.getLog(LoggingConfig.class);

    @Autowired(required = false)
    private ServletContext servletContext;


    @PostConstruct
    public void init() {
        try {
            initLog4j2();
        } catch (Throwable ignored) {

        }

        log.info("test info");
        log.debug("test debug");

        log.warn("test warn");
        log.trace("test trace");
    }

    private void initLog4j2() {
        final LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
        final Configuration config = ctx.getConfiguration();
//        Layout layout = PatternLayout.createLayout(PatternLayout.SIMPLE_CONVERSION_PATTERN, config, null,
//                null,null, null);
//        Appender appender = FileAppender.createAppender("target/test.log", "false", "false", "File", "true",
//                "false", "false", "4000", layout, null, "false", null, config);
//        appender.start();
//        config.addAppender(appender);
//        AppenderRef ref = AppenderRef.createAppenderRef("File", null, null);
//        AppenderRef[] refs = new AppenderRef[] {ref};

        //获取所有的appender
        Map<String, Appender> appenderMap = config.getAppenders();
        if (appenderMap.isEmpty())
            return;
        AppenderRef[] refs = appenderMap.keySet().stream().map(name -> AppenderRef.createAppenderRef(name, null, null)).toArray(AppenderRef[]::new);

        System.getProperties().stringPropertyNames()
                .stream()
                .filter(name -> name.startsWith("log4j."))
                .forEach(name -> {
                    String loggerName = name.substring(6);
                    LoggerConfig loggerConfig = LoggerConfig.createLogger("false", Level.toLevel(System.getProperty(name, "warn")), loggerName, "true", refs, null, config, null);
                    config.addLogger(loggerName, loggerConfig);
                    appenderMap.values().forEach(appender -> loggerConfig.addAppender(appender, null, null));
                });

        if (servletContext != null) {
            Enumeration<String> names = servletContext.getInitParameterNames();
            while (names.hasMoreElements()) {
                String name = names.nextElement();
                if (name.startsWith("log4j.")) {
                    String loggerName = name.substring(6);
                    LoggerConfig loggerConfig = LoggerConfig.createLogger("false", Level.toLevel(servletContext.getInitParameter(name)), loggerName, "true", refs, null, config, null);
                    config.addLogger(loggerName, loggerConfig);
                    appenderMap.values().forEach(appender -> loggerConfig.addAppender(appender, null, null));
                }
            }
        }

//        LoggerConfig loggerConfig = LoggerConfig.createLogger("false", "info", "org.apache.logging.log4j",
//                "true", refs, null, config, null);
//        loggerConfig.addAppender(appender, null, null);
//        config.addLogger("org.apache.logging.log4j", loggerConfig);

        ctx.updateLoggers();
    }
}
