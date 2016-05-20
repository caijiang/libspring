package org.luffy.lib.libspring.embedweb.ewp.service;

import org.luffy.lib.libspring.embedweb.EmbedWeb;
import org.luffy.lib.libspring.embedweb.PathService;
import org.luffy.lib.libspring.embedweb.ewp.MockMVC;
import org.luffy.lib.libspring.embedweb.exception.NoSuchEmbedWebException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.WebApplicationContext;

/**
 * 再每一个ewp jar包中只有一个{@link org.luffy.lib.libspring.embedweb.EmbedWeb}实例
 *
 * @author CJ
 */
@Service
public class EWPPathService implements PathService {

    @Autowired
    private EmbedWeb embedWeb;
    @Autowired
    private WebApplicationContext webApplicationContext;

    @Override
    public String forPrivate(String path) throws NoSuchEmbedWebException {
        return null;
    }

    @Override
    public String forPublic(String path) throws NoSuchEmbedWebException {
        // 把 publicResourcePath  都保存在 _ewp_/ 下面
//        Resource resource = webApplicationContext.getResource("classpath:" + embedWeb.publicResourcePath() + path);
//        if (resource.exists()) {
//            File targetFile = new File(webApplicationContext.getServletContext().getRealPath(MockMVC.PUBLIC_STORE + path));
//
//            try {
//                if (!targetFile.getParentFile().exists() && !targetFile.getParentFile().mkdirs())
//                    throw new IOException("failed to create " + targetFile.getParentFile());
//                Files.copy(resource.getInputStream(), Paths.get(targetFile.toURI()),REPLACE_EXISTING);
//            } catch (IOException e) {
//                throw new NoSuchEmbedWebException(e, null, embedWeb);
//            }
//        }
        return MockMVC.PUBLIC_STORE + path;
    }
}
