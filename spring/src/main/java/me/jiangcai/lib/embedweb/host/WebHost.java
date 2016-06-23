package me.jiangcai.lib.embedweb.host;

import me.jiangcai.lib.embedweb.EmbedWeb;
import me.jiangcai.lib.embedweb.host.service.EmbedWebInfoService;
import me.jiangcai.lib.embedweb.thymeleaf.EWPProcessorDialect;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.access.BootstrapException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.spring4.view.ThymeleafViewResolver;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

/**
 * 继承该配置或者引入该配置表示这是一个web宿主
 *
 * @author CJ
 */
@SuppressWarnings({"WeakerAccess", "SpringFacetCodeInspection"})
@Configuration
@ComponentScan({"me.jiangcai.lib.embedweb.host.service", "me.jiangcai.lib.embedweb.thymeleaf"})
@EnableWebMvc
@EnableAspectJAutoProxy
public class WebHost extends WebMvcConfigurerAdapter implements BeanPostProcessor {

    private static final Log log = LogFactory.getLog(WebHost.class);
    @Autowired
    private EmbedWebInfoService embedWebInfoService;
    @Autowired
    private WebApplicationContext webApplicationContext;
    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    @Autowired(required = false)
    private Set<TemplateEngine> templateEngines;
    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    @Autowired(required = false)
    private Set<ThymeleafViewResolver> thymeleafViewResolvers;

    /**
     * 所有公开资源都会放在这个目录下面
     */
    public static final String HeaderPublic = "_EWP1_";
    /**
     * 所有私有资源都会放在这个目录下
     */
    public static final String HeaderPrivate = "_EWP2_";

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        super.addResourceHandlers(registry);
        // 坑爹原来这里还没有初始化好
        registry.addResourceHandler("/" + HeaderPublic + "/**")
                .addResourceLocations("/" + HeaderPublic + "/");
//        registry.addResourceHandler("/" + uuid + "/public/**")
//                .addResourceLocations("/" + uuid + "/public/");
    }

    /**
     * @return ViewNameAdjust instance
     */
    @Bean
    public ViewNameAdjust viewNameAdjust() {
        return new ViewNameAdjust();
    }

    @Autowired
    public void link(EWPProcessorDialect ewpProcessorDialect) {
        if (templateEngines == null && thymeleafViewResolvers == null) {
            log.warn("no TemplateEngine found, you should use\n\n@Autowired\nEWPProcessorDialect ewpProcessorDialect;\n" +
                    "into your TemplateEngine");
        } else {
            log.debug("setup EWPProcessorDialect");
            Consumer<TemplateEngine> consumer = templateEngine -> templateEngine.addDialect(ewpProcessorDialect);

            if (templateEngines != null)
                templateEngines.forEach(consumer);

            if (thymeleafViewResolvers != null)
                thymeleafViewResolvers.stream().map(thymeleafViewResolver
                        -> (TemplateEngine) thymeleafViewResolver.getTemplateEngine()).forEach(consumer);
        }
    }

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

            log.debug("checking EWP " + web.name());
            try {
                String uuid = uuidFrom(web);
                if (uuid == null) {
                    // 需要部署资源
                    log.info(web.name() + " is installing.");
                    uuid = UUID.randomUUID().toString().replaceAll("-", "");

                    if (web.privateResourcePath() != null)
                        copyResource(HeaderPrivate + "/" + uuid, "private", web.privateResourcePath(), web.getClass());
                    if (web.publicResourcePath() != null)
                        copyResource(HeaderPublic + "/" + uuid, "public", web.publicResourcePath(), web.getClass());

                    updateUuid(uuid, web);
                }
            } catch (IOException | URISyntaxException ex) {
                throw new BootstrapException("deploy resource failed on " + web.name(), ex);
            }
        }

        return bean;
    }

    /**
     * 将webClass所在代码领域的path开头的所有资源，复制到servlet context /uuid/tag下
     *
     * @param uuid
     * @param tag
     * @param path
     * @param webClass
     * @throws URISyntaxException
     * @throws IOException
     */
    private void copyResource(String uuid, String tag, String path, Class<? extends EmbedWeb> webClass)
            throws URISyntaxException, IOException {
        // 情况1  webClass 的确是来自于一个jar
        // Fiel...../abc.jar
        // 情况2  webClass 来自classpath的几个.class 没有独立jar包
        // file:/E:/IdeaWorkSpace/libspring/spring/target/test-classes/
//        webClass.getProtectionDomain().getCodeSource().getLocation();
        URL url = webClass.getResource(path);
        if (url == null) {
            throw new IllegalStateException("no resources find for " + path + " from " + webClass + ", use null" +
                    " resourcePath when EWP has no resource.");
        }
        String rootPath = webApplicationContext.getServletContext().getRealPath("/" + uuid + "/" + tag);
        if (!new File(rootPath).mkdirs()) {
            throw new IOException("failed to mkdirs for " + rootPath);
        }

        URI uri = url.toURI();
        Path myPath;
        FileSystem fileSystem = null;
        try {
            // Jboss VFS support
            // TODO 这里判断Jboss环境很草率,应该给予加强
            if (System.getProperty("jboss.home.dir") != null && uri.getScheme().equals("vfs")
                    && uri.toString().contains(".jar")) {
                String ssp = uri.getSchemeSpecificPart();
                ssp = "file:" + ssp.replaceFirst("\\.jar", ".jar!");
                // ssp
                uri = new URI("jar", ssp, uri.getFragment());
            }
            if (uri.getScheme().equals("jar")) {
                fileSystem = FileSystems.newFileSystem(uri, Collections.emptyMap());
                myPath = fileSystem.getPath(path);
            } else {
                myPath = Paths.get(uri);
            }
            Stream<Path> walk = Files.walk(myPath);

            walk
                    .forEach(filePath -> {
                        String name = filePath.toString().substring(myPath.toString().length());

                        log.debug("start copy resource " + filePath);

                        String targetPath = webApplicationContext.getServletContext().getRealPath("/" + uuid + "/" + tag
                                + name);

                        try {
                            File targetFile = new File(targetPath);
                            Files.copy(filePath, Paths.get(targetFile.toURI()), REPLACE_EXISTING);
                        } catch (IOException e) {
                            throw new RuntimeException("failed to copy file " + filePath, e);
                        }

                    });

        } finally {
            if (fileSystem != null)
                //noinspection ThrowFromFinallyBlock
                fileSystem.close();
        }
    }

    private void updateUuid(String uuid, EmbedWeb web) throws IOException {
        File file = new File(webApplicationContext.getServletContext().getRealPath("/.ewp.properties"));
        Properties properties = new Properties();
        if (file.exists())
            try (FileInputStream inputStream = new FileInputStream(file)) {
                properties.load(inputStream);
            }

        properties.setProperty(web.name() + "-" + web.version(), uuid);
        try (FileOutputStream outputStream = new FileOutputStream(file)) {
            properties.store(outputStream, null);
            outputStream.flush();
        }
        embedWebInfoService.webUUIDs().put(web, uuid);
    }

    private String uuidFrom(EmbedWeb web) throws IOException {
        File file = new File(webApplicationContext.getServletContext().getRealPath("/.ewp.properties"));
        if (!file.exists())
            return null;
        Properties properties = new Properties();
        try (FileInputStream inputStream = new FileInputStream(file)) {
            properties.load(inputStream);
        }

        String uuid = properties.getProperty(web.name() + "-" + web.version());
        if (uuid == null)
            return null;
        embedWebInfoService.webUUIDs().put(web, uuid);
        return uuid;
    }
}
