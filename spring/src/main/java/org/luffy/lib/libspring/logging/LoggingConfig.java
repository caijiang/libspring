/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2015. All rights reserved.
 */

package org.luffy.lib.libspring.logging;

import org.springframework.context.annotation.Configuration;

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
 * @deprecated 使用 {@link me.jiangcai.lib.spring.logging.LoggingConfig}代替
 */
@Configuration
public class LoggingConfig extends me.jiangcai.lib.spring.logging.LoggingConfig {

}
