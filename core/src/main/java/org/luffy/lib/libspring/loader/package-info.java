/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * <p>启动器</p>
 * <p>{@link libspring.loader.MutliDispatcherLoader}是一个多Dispatcher启动器，并不在常见场合中使用。</p>
 * <p>如需Spring Security请创建类继承与{@link org.springframework.security.web.context.AbstractSecurityWebApplicationInitializer} 如下</p>
 * <pre class="code">
 *     public class SecurityWebApplicationInitializer extends AbstractSecurityWebApplicationInitializer {
 *          public SecurityWebApplicationInitializer() {
 *                  super(SecurityConfig.class);
 *         }
 *     }
 * </pre>
 * <p>MVC的实现请创建类继承与{@link libspring.loader.SimpleMVCLoader}</p>
 * <pre class="code">
 *     public class MyLoader extends SimpleMVCLoader{
 *     }
 * </pre>
 *
 **/
package org.luffy.lib.libspring.loader;
