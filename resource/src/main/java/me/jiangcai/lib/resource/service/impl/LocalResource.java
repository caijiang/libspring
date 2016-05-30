package me.jiangcai.lib.resource.service.impl;

import me.jiangcai.lib.resource.Resource;
import org.springframework.core.io.FileSystemResource;

import java.io.IOException;
import java.net.URL;

/**
 * @author CJ
 */
public class LocalResource extends FileSystemResource implements Resource {

    private final String httpUrl;

    public LocalResource(String path, String httpUrl) {
        super(path);
        this.httpUrl = httpUrl;
    }

    @Override
    public URL httpUrl() throws IOException {
        return new URL(httpUrl);
    }

}
