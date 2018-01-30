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
    private final String path;

    public LocalResource(String path, String filePath, String httpUrl) {
        super(filePath);
        this.path = path;
        this.httpUrl = httpUrl;
    }

    @Override
    public URL httpUrl() throws IOException {
        return new URL(httpUrl);
    }

    @Override
    public String getResourcePath() {
        return path;
    }

}
