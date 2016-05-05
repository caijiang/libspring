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
     * @param aPackage 包
     */
    void setupCurrentEmbedWeb(Package aPackage);

    /**
     * @return 当前EWP
     */
    EmbedWebInfo getCurrentEmbedWebInfo();
}
