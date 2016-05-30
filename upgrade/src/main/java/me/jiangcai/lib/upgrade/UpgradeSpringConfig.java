package me.jiangcai.lib.upgrade;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * 配置该类即可获得{@link me.jiangcai.lib.upgrade.service.UpgradeService 系统升级服务},
 * 但在此之前必须提供{@link VersionInfoService 版本信息服务}
 *
 * @author CJ
 */
@Configuration
@ComponentScan("me.jiangcai.lib.upgrade.service")
public class UpgradeSpringConfig {


}
