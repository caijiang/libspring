package me.jiangcai.lib.embedweb;

import me.jiangcai.lib.embedweb.exception.NoSuchEmbedWebException;

/**
 * 路径服务
 * EWP项目可以从中获取资源的保存路径
 *
 * @author CJ
 */
public interface PathService {


    /**
     * 获取当前EWP服务端所用资源路径
     *
     * @param path private资源相对路径,必须/开头
     * @return /开头的资源路径,在EWP环境中获取的是资源的classpath,而在宿主环境中获取的是资源在当前web环境中的访问uri,但具备相同的语义
     * @throws IllegalArgumentException                                           path错误
     * @throws NoSuchEmbedWebException 没有找到EWP
     */
    String forPrivate(String path) throws NoSuchEmbedWebException;

    /**
     * 获取当前EWP公共资源路径
     *
     * @param path public资源相对路径,必须/开头
     * @return /开头的资源路径,在EWP环境中获取的是资源的classpath,而在宿主环境中获取的是资源在当前web环境中的访问uri,但具备相同的语义
     * @throws IllegalArgumentException                                           path错误
     * @throws NoSuchEmbedWebException 没有找到EWP
     */
    String forPublic(String path) throws NoSuchEmbedWebException;


}
