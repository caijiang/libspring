package org.luffy.lib.libspring.embedweb;

import org.luffy.lib.libspring.embedweb.exception.NoSuchEmbedWebException;

/**
 * 路径服务
 * EWP项目可以从中获取资源的保存路径
 *
 * @author CJ
 */
public interface PathService {

    /**
     * 获取服务端所用资源路径
     *
     * @param name EWP名称
     * @param path private资源相对路径,必须/开头
     * @return /开头的资源路径,相对于web context 根目录
     * @throws IllegalArgumentException                                           path错误,name或者path为null
     * @throws org.luffy.lib.libspring.embedweb.exception.NoSuchEmbedWebException 没有找到EWP
     */
    String forPrivate(String name, String path) throws NoSuchEmbedWebException;

    /**
     * 获取公共资源路径
     *
     * @param name EWP名称
     * @param path public资源相对路径,必须/开头
     * @return /开头的资源路径,相对于web context 根目录
     * @throws IllegalArgumentException                                           path错误,name或者path为null
     * @throws org.luffy.lib.libspring.embedweb.exception.NoSuchEmbedWebException 没有找到EWP
     */
    String forPublic(String name, String path) throws NoSuchEmbedWebException;

}
