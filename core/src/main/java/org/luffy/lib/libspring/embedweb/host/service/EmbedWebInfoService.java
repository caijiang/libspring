package org.luffy.lib.libspring.embedweb.host.service;

import org.luffy.lib.libspring.embedweb.EmbedWeb;
import org.luffy.lib.libspring.embedweb.exception.NoSuchEmbedWebException;
import org.luffy.lib.libspring.embedweb.host.model.EmbedWebInfo;

import java.util.Map;
import java.util.stream.Stream;

/**
 * Created by luffy on 2016/5/6.
 *
 * @author luffy luffy.ja at gmail.com
 */
public interface EmbedWebInfoService {

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

    Map<EmbedWeb, String> webUUIDs();

    /**
     * 设定当前EWP
     *
     * @param type ewp内某一个Class,如果该Class并非在任意一个ewp内,则当前ewp为null
     */
    void setupCurrentEmbedWeb(Class type);

    /**
     * @return 当前EWP
     */
    EmbedWebInfo getCurrentEmbedWebInfo();

    /**
     * @return 只读流
     */
    Stream<EmbedWebInfo> embedWebInfoStream();
}
