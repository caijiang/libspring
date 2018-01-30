package me.jiangcai.lib.resource.service.impl;

import me.jiangcai.lib.resource.Resource;
import me.jiangcai.lib.resource.vfs.FileObjectFunction;
import me.jiangcai.lib.resource.vfs.VFSHelper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.vfs2.FileObject;
import org.springframework.core.io.AbstractResource;
import org.springframework.core.io.WritableResource;
import org.springframework.util.StreamUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * @author CJ
 */
public class VFSResource extends AbstractResource implements Resource, WritableResource {


    private static final Log log = LogFactory.getLog(VFSResource.class);

    //    private final FileObject fileObject;
    private final URI uri;
    private final String fileName;
    private transient final VFSHelper helper;
    private final String path;

    public VFSResource(String path, VFSHelper vfsHelper, String file, URI uri) {
        this.path = path;
        this.uri = uri;
        this.helper = vfsHelper;
        this.fileName = file;
    }

    public <T> T accessFileObject(FileObjectFunction<FileObject, T> function) throws IOException {
        return helper.handle(fileName, function);
    }

    @Override
    public boolean exists() {
        try {
            return accessFileObject(FileObject::exists);
        } catch (IOException e) {
            log.error("exists", e);
            return false;
        }
    }

    @Override
    public long contentLength() throws IOException {
        return accessFileObject(fileObject -> fileObject.getContent().getSize());
    }

    @Override
    public long lastModified() throws IOException {
        return accessFileObject(fileObject -> fileObject.getContent().getLastModifiedTime());
    }

    @Override
    public String getFilename() {
        return super.getFilename();
    }

    @Override
    public URI getURI() throws IOException {
        try {
            return getURL().toURI();
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("bad VFS file:" + fileName, e);
        }
    }

    @Override
    public URL getURL() throws IOException {
        return new URL(fileName);
    }

    @Override
    public boolean isWritable() {
        try {
            return accessFileObject(FileObject::isWriteable);
        } catch (IOException e) {
            log.error("isWritable", e);
            return false;
        }
    }

    @Override
    public OutputStream getOutputStream() throws IOException {
        return new ByteArrayOutputStream() {
            @Override
            public void close() throws IOException {
                accessFileObject(fileObject -> {
                    OutputStream outputStream = fileObject.getContent().getOutputStream();
                    StreamUtils.copy(buf, outputStream);
                    outputStream.flush();
                    outputStream.close();
                    return null;
                });
            }
        };
//        return accessFileObject(fileObject -> fileObject.getContent().getOutputStream());
    }

    @Override
    public String getDescription() {
        return fileName;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return accessFileObject(fileObject -> {
            InputStream inputStream = fileObject.getContent().getInputStream();
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            StreamUtils.copy(inputStream, byteArrayOutputStream);
            inputStream.close();
            return new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
        });

    }

    @Override
    public URL httpUrl() throws IOException {
        return uri.toURL();
    }

    @Override
    public String getResourcePath() {
        return path;
    }

}
