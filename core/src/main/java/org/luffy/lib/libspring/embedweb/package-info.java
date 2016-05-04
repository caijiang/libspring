/**
 * <strong>Embed Web</strong>
 * <p>
 * 可以让一个packaging为jar的项目拥有web能力.
 * 我设想的一种场景是数个实际项目(Embed Web Project),然后一个唯一的Web项目
 * </p>
 * <p>
 * 资源应该区分开静态资源和一般资源（可能是模板什么什么的）
 * 要静态资源,可测试性,独立性以及整合难度
 * </p>
 * <p>
 * 可提供的thymeleaf标签
 * </p>
 * <ul>
 * <li>语言级别的自定义 获取EWP资源</li>
 * <li>传统级别 应当可以从指定web项目中获取.</li>
 * </ul>
 * <strong>约定</strong>
 * <ul>
 * <li>与提供者同包的 /web/private 服务端可用资源</li>
 * <li>与提供者同包的 /web/public 全局可用资源</li>
 * <li>默认使用Thymeleaf作为视图解析</li>
 * </ul>
 * <p>
 * <strong>EWP</strong>
 * <ul>
 * <li>静态资源入口标识</li>
 * <li>可提供的ViewResolver</li>
 * </ul>
 *
 * @author CJ
 * @since 2.1
 */
package org.luffy.lib.libspring.embedweb;