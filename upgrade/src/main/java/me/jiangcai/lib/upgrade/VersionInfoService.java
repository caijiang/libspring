package me.jiangcai.lib.upgrade;

import org.springframework.transaction.annotation.Transactional;

/**
 * 版本信息服务
 *
 * @author CJ
 */
public interface VersionInfoService {

    /**
     * @param <T>  参考{@link me.jiangcai.lib.upgrade.service.UpgradeService}中的范型
     * @param type 要求返回的类型
     * @return 当前数据库版本, 如果没有就返回null
     */
    <T extends Enum> T currentVersion(Class<T> type);

    /**
     * 保存输入的版本为数据库版本
     *
     * @param currentVersion 输入版本
     * @param <T>            参考{@link me.jiangcai.lib.upgrade.service.UpgradeService}中的范型
     */
    @Transactional
    <T extends Enum> void updateVersion(T currentVersion);
}
