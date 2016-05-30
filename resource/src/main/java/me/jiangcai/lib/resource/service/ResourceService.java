package me.jiangcai.lib.resource.service;


import me.jiangcai.lib.resource.Resource;

import java.io.IOException;
import java.io.InputStream;

/**
 * 静态资源服务
 * <p>在不同的环境中将有不同的实现管理该服务</p>
 *
 * @author CJ
 */
@SuppressWarnings("WeakerAccess")
public interface ResourceService {

    /**
     * 上传资源
     *
     * @param path 资源路径（相对）,<strong>不可以以/开头</strong>
     * @param data 数据
     * @return 新资源的资源定位符
     * @throws IOException 保存时出错
     */
    Resource uploadResource(String path, InputStream data) throws IOException;

    /**
     * 获取指定资源的资源定位符
     *
     * @param path 资源路径（相对）,<strong>不可以以/开头</strong>
     * @return 资源实体
     */
    Resource getResource(String path);

    /**
     * 删除资源
     *
     * @param path 资源路径（相对）,<strong>不可以以/开头</strong>
     * @throws IOException 删除时错误
     */
    void deleteResource(String path) throws IOException;
}
