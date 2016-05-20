package org.luffy.lib.libspring.embedweb.host.service;

import org.luffy.lib.libspring.embedweb.EmbedWeb;
import org.luffy.lib.libspring.embedweb.host.model.EmbedWebInfo;

import java.util.Map;

/**
 * Created by luffy on 2016/5/6.
 *
 * @author luffy luffy.ja at gmail.com
 */
public interface EmbedWebInfoService {

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
}
