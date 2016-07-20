package me.jiangcai.lib.embedweb.jboss;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.vfs.VFS;
import org.jboss.vfs.VFSUtils;
import org.jboss.vfs.VirtualFile;
import org.jboss.vfs.VirtualFileVisitor;
import org.jboss.vfs.VisitorAttributes;
import org.springframework.web.context.WebApplicationContext;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;

/**
 * 避免一个不存在的Class被Spring扫描到
 *
 * @author CJ
 */
public class ResourceCopy {

    private static final Log log = LogFactory.getLog(ResourceCopy.class);

    public static void copy(URI uri, String uuid, String tag, WebApplicationContext webApplicationContext) throws IOException {
        VirtualFile vfsFile = VFS.getChild(uri);
        vfsFile.visit(new VirtualFileVisitor() {
            @Override
            public VisitorAttributes getAttributes() {
                return VisitorAttributes.RECURSE_LEAVES_ONLY;
            }

            @Override
            public void visit(VirtualFile virtualFile) {
                String name = virtualFile.getPathName().substring(vfsFile.getPathName().length());
                log.debug("start copy resource " + virtualFile + " for " + name);

                String targetPath = webApplicationContext.getServletContext().getRealPath("/" + uuid + "/" + tag
                        + name);

                try {
                    File targetFile = new File(targetPath);
                    if (!targetFile.getParentFile().exists() && !targetFile.getParentFile().mkdirs())
                        throw new IOException("create dir for " + targetFile);
//                            VFSUtils.recursiveCopy(virtualFile, targetFile.getParentFile());
                    VFSUtils.copyStreamAndClose(virtualFile.openStream(), new FileOutputStream(targetFile));
                } catch (IOException e) {
                    throw new RuntimeException("failed to copy file " + virtualFile, e);
                }
            }
        });
    }
}
