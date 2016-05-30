package me.jiangcai.lib.resource;

import java.io.IOException;
import java.net.URL;

/**
 * 资源
 * 此处继承了Spring的Resource,添加更多资源行为
 *
 * @author CJ
 */
public interface Resource extends org.springframework.core.io.Resource {

    /**
     * @return 以https或者http为schema的访问url
     * @throws IOException 产生时发生IO错误
     */
    URL httpUrl() throws IOException;

}
