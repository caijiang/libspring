package org.luffy.lib.libspring.embedweb.host.service.impl;

import org.luffy.lib.libspring.embedweb.EmbedWeb;
import org.luffy.lib.libspring.embedweb.exception.NoSuchEmbedWebException;
import org.luffy.lib.libspring.embedweb.host.service.HostedPathService;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @author CJ
 */
@Service
@Primary
public class HostedPathServiceImpl implements HostedPathService {

    private final Map<EmbedWeb, String> UUIDs = new HashMap<>();

    @Override
    public String forPrivate(String name, String path) throws NoSuchEmbedWebException {
        return doPath(name, path, "/private");

    }

    private String doPath(String name, String path, String tag) throws NoSuchEmbedWebException {
        if (name == null || path == null)
            throw new IllegalArgumentException("bad name or path");
        if (!path.startsWith("/"))
            throw new IllegalArgumentException("bad path:" + path);

        EmbedWeb key = UUIDs.keySet().stream().filter(embedWeb -> embedWeb.name().equals(name))
                .findFirst().orElseThrow(() -> new NoSuchEmbedWebException(name, null));

        return "/" + UUIDs.get(key) + tag + path;
    }

    @Override
    public String forPublic(String name, String path) throws NoSuchEmbedWebException {
        return doPath(name, path, "/public");
    }

    @Override
    public Map<EmbedWeb, String> webUUIDs() {
        return UUIDs;
    }
}
