package org.luffy.lib.libspring.embedweb.host.service.impl;

import org.luffy.lib.libspring.embedweb.EmbedWeb;
import org.luffy.lib.libspring.embedweb.PathService;
import org.luffy.lib.libspring.embedweb.exception.NoSuchEmbedWebException;
import org.luffy.lib.libspring.embedweb.host.model.EmbedWebInfo;
import org.luffy.lib.libspring.embedweb.host.service.EmbedWebInfoService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by luffy on 2016/5/6.
 *
 * @author luffy luffy.ja at gmail.com
 */
@Service
public class EmbedWebInfoServiceImpl implements EmbedWebInfoService, PathService {


    private final ThreadLocal<EmbedWebInfo> currentInfo = new ThreadLocal<>();
    private final List<EmbedWebInfo> webInfoList = new ArrayList<>();
    private final Map<EmbedWeb, String> UUIDs = new HashMap<EmbedWeb, String>() {
        @Override
        public String put(EmbedWeb key, String value) {
            String result = super.put(key, value);
            EmbedWebInfo info = new EmbedWebInfo();
            info.setUuid(value);
            info.setaPackage(key.getClass().getPackage());
            info.setName(key.name());
            info.setVersion(key.version());
            info.setPrivateResourceUri("/" + value + "/private");
            info.setPubicResourceUri("/" + value + "/public");
            webInfoList.add(info);
            return result;
        }
    };


    private EmbedWebInfo fromName(String name, String path) throws NoSuchEmbedWebException {
        if (name == null || path == null)
            throw new IllegalArgumentException("bad name or path");
        if (!path.startsWith("/"))
            throw new IllegalArgumentException("bad path:" + path);

        return webInfoList.stream().filter(embedWebInfo -> embedWebInfo.getName().equals(name))
                .findFirst()
                .orElseThrow(() -> new NoSuchEmbedWebException(name, null));
    }

    @Override
    public String forPrivate(String name, String path) throws NoSuchEmbedWebException {
        EmbedWebInfo info = fromName(name, path);
        return info.getPrivateResourceUri() + path;
    }

    @Override
    public String forPublic(String name, String path) throws NoSuchEmbedWebException {
        EmbedWebInfo info = fromName(name, path);
        return info.getPubicResourceUri() + path;
    }


    @Override
    public void setupCurrentEmbedWeb(Package aPackage) {
        currentInfo.set(fromPackage(aPackage));
    }

    @Override
    public EmbedWebInfo getCurrentEmbedWebInfo() {
        return currentInfo.get();
    }

    private EmbedWebInfo fromPackage(Package aPackage) {
        return webInfoList.stream().filter(embedWebInfo -> embedWebInfo.getaPackage() == aPackage)
                .findFirst()
                .orElse(null);
    }

    @Override
    public Map<EmbedWeb, String> webUUIDs() {
        return UUIDs;
    }
}
