package me.jiangcai.lib.resource.service.impl;

import me.jiangcai.lib.resource.Resource;
import me.jiangcai.lib.resource.service.ResourceService;
import me.jiangcai.lib.resource.vfs.VFSHelper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.ServletContext;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

/**
 * 生产环境静态资源处理器
 * <p>如需自定义资源管理位置,请务必设置系统属性</p>
 * <code>me.jiangcai.lib.resource.http.uri</code>
 * <p>比如<code>http://res.fanmore.cn/</code></p>
 * <code>me.jiangcai.lib.resource.home</code>
 * <p>比如<code>file://D:/fanmoreresources</code></p>
 * 或者让把资源放在部署目录的_resources下,定义<code>me.jiangcai.server.port</code>明示应用服务器的IP,
 * 服务会使用{@link ServletContext#getVirtualServerName()}获取主机名
 *
 * @author CJ
 */
@Service
public class VFSResourceService implements ResourceService {

    private static final Log log = LogFactory.getLog(VFSResourceService.class);
    private boolean localFileMode;

    public static final String ServletContextResourcePath = "/_resources";

    protected final URI uriPrefix;
    protected URI fileHome;
    protected File fileFile;
    @Autowired
    private VFSHelper vfsHelper;

    @Autowired(required = false)
    public VFSResourceService(Environment environment, WebApplicationContext webApplicationContext) {
        this(environment.getProperty("me.jiangcai.lib.resource.http.uri", (String) null)
                , environment.getProperty("me.jiangcai.lib.resource.home", (String) null)
                , environment.getProperty("me.jiangcai.server.port", Integer.class, 8080)
                , webApplicationContext, "me.jiangcai.lib.resource");
    }

    /**
     * @param uri                   要求使用的资源http访问uri,为null表示不用
     * @param home                  要求使用的资源位置,为null表示不用
     * @param port                  该应用的部署port,用于将资源管理在部署目录
     * @param webApplicationContext webApplicationContext
     * @param prefix                系统属性的前缀,保留应用中存在多个资源管理系统的可能
     */
    public VFSResourceService(String uri, String home, int port, WebApplicationContext webApplicationContext
            , String prefix) {
        // 应该先构建字符串,再根据值判断是否未本地系统(file)

        boolean autoHome = false;
        if (StringUtils.isEmpty(uri) || StringUtils.isEmpty(home)) {
            autoHome = true;
            if (webApplicationContext == null)
                throw new IllegalStateException("ResourceService required web Environment.");
            try {
                uri = "http://" + webApplicationContext.getServletContext().getVirtualServerName();
            } catch (AbstractMethodError ignored) {
                uri = "http://localhost";
                log.warn("ResourceService can not use getVirtualServerName in Servlet Version < 3.1. ");
            }
            if (port != 80)
                uri = uri + ":" + port;
            uri = uri + ServletContextResourcePath + "/";
            home = webApplicationContext.getServletContext().getRealPath(ServletContextResourcePath);
            log.warn("ResourceService running in ServletContextPath, please setup " + prefix + ".http.uri" + ","
                    + prefix + ".home to define VFS ResourceService");

        }

        if (!home.endsWith("/"))
            home = home + "/";

        // 研究home
        try {
            URI homeUri = new URI(home);
            if ("file".equals(homeUri.getScheme()) || homeUri.getScheme() == null) {
                localFileMode = true;
                fileHome = null;
                fileFile = new File(homeUri.getPath());
            } else {
                localFileMode = false;
                fileFile = null;
                fileHome = homeUri;
            }
        } catch (URISyntaxException e) {
            if (autoHome) {
                localFileMode = true;
                fileHome = null;
                fileFile = new File(home);
            } else
                throw new IllegalArgumentException(e);
        }

        if (!uri.endsWith("/"))
            uri = uri + "/";


        log.info("ResourceService running on " + home + ", via:" + uri);

        try {
            uriPrefix = new URI(uri);
//
//            try {
//                if (home.startsWith("/") || home.startsWith("file:/"))
//                    localFileMode = true;
//            } catch (InvalidPathException ignored) {
//            }

        } catch (URISyntaxException e) {
            throw new IllegalArgumentException(e);
        }

    }

    @Override
    public Resource uploadResource(String path, InputStream data) throws IOException {
        if (path == null || path.startsWith("/"))
            throw new IllegalArgumentException("bad resource path:" + path);
        // 检查是否是本地文件系统,如果是的话就使用本地文件系统技术
        if (localFileMode) {
            File file = new File(fileFile.toString() + File.separator + path);
            Path path1 = Paths.get(file.toURI());
            Files.createDirectories(path1.getParent());
            Files.copy(data, path1, REPLACE_EXISTING);
            return getResource(path);
        }

        String filePath = fileHome.toString() + path;

        vfsHelper.handle(filePath, file -> {
            OutputStream out = file.getContent().getOutputStream();
            try {
                StreamUtils.copy(data, out);
            } catch (IOException e) {
                throw new FileSystemException(e);
            } finally {
                try {
                    data.close();
                    out.close();
                } catch (IOException e) {
                    log.info("Exception on close stream." + e);
                }
            }
            return null;
        });
        return getResource(path);
    }

    @Override
    public Resource getResource(String path) {
        if (path == null || path.startsWith("/"))
            throw new IllegalArgumentException("bad resource path:" + path);

        String url = uriPrefix.toString() + path;

        if (localFileMode) {
            return new LocalResource(fileFile.toString() + File.separator + path, url);
        }
        String filePath = fileHome.toString() + path;

        try {
//            String uri = stringBuilder.toString();
//            FileObject fileObject = vfsHelper.handle(fileBuilder.toString(), null);
            return new VFSResource(vfsHelper, filePath, new URI(url));
//            return new VFSResource(fileObject, new URI(stringBuilder.toString()));
        } catch (URISyntaxException e) {
            log.error("解释资源时", e);
            return null;
        }
    }

    @Override
    public void deleteResource(String path) throws IOException {
        if (path == null || path.startsWith("/"))
            throw new IllegalArgumentException("bad resource path:" + path);

        String filePath = fileHome.toString() + path;

        if (localFileMode) {
            Path ioPath = Paths.get(filePath);
            Files.deleteIfExists(ioPath);
            return;
        }


        vfsHelper.handle(filePath, FileObject::delete);
    }

}
