package me.jiangcai.lib.resource.vfs;

import me.jiangcai.lib.resource.vfs.FileObjectFunction;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystem;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.FileSystemOptions;
import org.apache.commons.vfs2.impl.StandardFileSystemManager;
import org.apache.commons.vfs2.provider.ftp.FtpFileSystemConfigBuilder;
import org.apache.commons.vfs2.provider.sftp.SftpFileSystemConfigBuilder;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * @author CJ
 */
@Service
public class VFSHelper {

    private final FileSystemOptions options = new FileSystemOptions();
    private boolean passive;

    public VFSHelper() {
        super();
        SftpFileSystemConfigBuilder.getInstance().setUserDirIsRoot(options, false);
        SftpFileSystemConfigBuilder.getInstance().setTimeout(options, 30000);

        FtpFileSystemConfigBuilder.getInstance().setUserDirIsRoot(options, false);
        FtpFileSystemConfigBuilder.getInstance().setDataTimeout(options, 30000);
        FtpFileSystemConfigBuilder.getInstance().setSoTimeout(options, 30000);
        FtpFileSystemConfigBuilder.getInstance().setPassiveMode(options, passive);
    }

    public <R> R handle(String name, FileObjectFunction<FileObject, R> function) throws IOException {
        StandardFileSystemManager manager = new StandardFileSystemManager();
        manager.init();
        FileSystem fileSystem = null;
        try {
            FileObject file = resolveFile(name, manager);
            fileSystem = file.getFileSystem();
            try {
                if (function != null)
                    return function.apply(file);
                return null;
            } catch (FileSystemException ex) {
                togglePassive();
                return function.apply(file);
            } finally {
                file.close();
            }
        } finally {
            if (fileSystem != null)
                manager.closeFileSystem(fileSystem);
            manager.close();
        }
    }

    public FileObject resolveFile(String name, FileSystemManager manager) throws FileSystemException {
        return manager.resolveFile(name, options);
    }

    public void togglePassive() {
        passive = !passive;
        FtpFileSystemConfigBuilder.getInstance().setPassiveMode(options, passive);
    }
}
