package me.jiangcai.lib.resource;

import me.jiangcai.lib.resource.vfs.VFSHelper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * 可以载入资源管理服务的Spring配置
 * 使用VFS的应用可以参考{@link me.jiangcai.lib.resource.service.impl.VFSResourceService}
 *
 * @author CJ
 */
@Configuration
@ComponentScan("me.jiangcai.lib.resource.service")
public class ResourceSpringConfig {

    @Bean
    public VFSHelper vfsHelper() {
        return new VFSHelper();
    }

}
