package org.luffy.lib.libspring.embedweb.host;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.luffy.lib.libspring.embedweb.EmbedWeb;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.access.BootstrapException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StreamUtils;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.security.CodeSource;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * 继承该配置或者引入该配置表示这是一个web宿主
 *
 * @author CJ
 */
@SuppressWarnings("WeakerAccess")
@Configuration
@EnableWebMvc
public class WebHost implements BeanPostProcessor {

    private static final Log log = LogFactory.getLog(WebHost.class);
    private final Map<EmbedWeb, String> uuids = new HashMap<>();
    @Autowired
    private WebApplicationContext webApplicationContext;

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        assert webApplicationContext != null;
        // name-version=value

        if (bean instanceof EmbedWeb) {
            EmbedWeb web = (EmbedWeb) bean;
            log.info(web.name());
            try {
                String uuid = uuidFrom(web);
                if (uuid == null) {
                    // 需要部署资源

                    uuid = UUID.randomUUID().toString();
                    CodeSource src = bean.getClass().getProtectionDomain().getCodeSource();
                    if (src != null) {
                        URL jar = src.getLocation();
                        ZipInputStream zip = new ZipInputStream(jar.openStream());
                        while (true) {
                            ZipEntry e = zip.getNextEntry();
                            if (e == null)
                                break;
                            String name = e.getName();
                            if (name.startsWith(web.privateResourcePath())) {
                                copyResource(uuid, "private", e, name.substring(web.privateResourcePath().length()));
                            }
                            if (name.startsWith(web.publicResourcePath())) {
                                copyResource(uuid, "public", e, name.substring(web.publicResourcePath().length()));
                            }
                        }
                        updateUuid(uuid, web);
                    } else {
                        log.warn("Local Deploy " + web.name());
                    }


                }
            } catch (IOException ex) {
                throw new BootstrapException("deploy resource failed on " + web.name(), ex);
            }
        }

        return bean;
    }


    private void copyResource(String uuid, String tag, ZipEntry entry, String name) throws IOException {
        if (entry.isDirectory())
            return;
        String path = webApplicationContext.getServletContext().getRealPath(uuid + "/" + tag);
        File root = new File(path);
        File file = new File(root, name);
        if (!file.getParentFile().exists() && !file.getParentFile().mkdirs())
            throw new IOException("failed to mkdirs for " + file);
        try (FileOutputStream outputStream = new FileOutputStream(file)) {
            StreamUtils.copy(entry.getExtra(), outputStream);
            outputStream.flush();
        }

    }

    private void updateUuid(String uuid, EmbedWeb web) throws IOException {
        File file = new File(webApplicationContext.getServletContext().getRealPath(".ewp.properties"));
        Properties properties = new Properties();
        if (file.exists())
            try (FileInputStream inputStream = new FileInputStream(file)) {
                properties.load(inputStream);
            }

        properties.setProperty(web.name() + "-" + web.version(), uuid);
        try (FileOutputStream outputStream = new FileOutputStream(file)) {
            properties.store(outputStream, new Date().toString());
            outputStream.flush();
        }
        uuids.put(web, uuid);
    }

    private String uuidFrom(EmbedWeb web) throws IOException {
        File file = new File(webApplicationContext.getServletContext().getRealPath(".ewp.properties"));
        if (!file.exists())
            return null;
        Properties properties = new Properties();
        try (FileInputStream inputStream = new FileInputStream(file)) {
            properties.load(inputStream);
        }


        String uuid = properties.getProperty(web.name() + "-" + web.version());
        uuids.put(web, uuid);
        return uuid;
    }
}
