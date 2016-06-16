package me.jiangcai.lib.embedweb.host.service.impl;

import me.jiangcai.lib.embedweb.EmbedWeb;
import me.jiangcai.lib.embedweb.PathService;
import me.jiangcai.lib.embedweb.exception.NoSuchEmbedWebException;
import me.jiangcai.lib.embedweb.host.model.EmbedWebInfo;
import me.jiangcai.lib.embedweb.host.service.EmbedWebInfoService;
import org.springframework.stereotype.Service;

import java.security.CodeSource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

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
            info.setSource(key.getClass().getProtectionDomain().getCodeSource());
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
    public void setupCurrentEmbedWeb(Class type) {
        currentInfo.set(fromType(type));
    }

    @Override
    public EmbedWebInfo getCurrentEmbedWebInfo() {
        return currentInfo.get();
    }

    private EmbedWebInfo fromType(Class type) {
        CodeSource source = type.getProtectionDomain().getCodeSource();
        return webInfoList.stream().filter(embedWebInfo -> embedWebInfo.getSource() == source)
                .findFirst()
                .orElse(null);
    }

    @Override
    public Map<EmbedWeb, String> webUUIDs() {
        return UUIDs;
    }

    @Override
    public Stream<EmbedWebInfo> embedWebInfoStream() {
        return webInfoList.stream();
    }

    @Override
    public String forPrivate(String path) throws NoSuchEmbedWebException {
        EmbedWebInfo info = getCurrentEmbedWebInfo();
        if (info == null)
            throw new NoSuchEmbedWebException("unknown", null);
        return info.getPrivateResourceUri() + path;
    }

    @Override
    public String forPublic(String path) throws NoSuchEmbedWebException {
        EmbedWebInfo info = getCurrentEmbedWebInfo();
        if (info == null)
            throw new NoSuchEmbedWebException("unknown", null);
        return info.getPubicResourceUri() + path;
    }
}
