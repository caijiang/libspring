package me.jiangcai.lib.upgrade.service.impl;

import me.jiangcai.lib.upgrade.VersionInfoService;
import me.jiangcai.lib.upgrade.VersionUpgrade;
import me.jiangcai.lib.upgrade.service.UpgradeService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.ParameterizedType;

/**
 * @author CJ
 */
@Service
public class UpgradeServiceImpl implements UpgradeService {

    private static final Log log = LogFactory.getLog(UpgradeServiceImpl.class);

    @Autowired
    private VersionInfoService versionInfoService;

    @Override
    public <T extends Enum> void systemUpgrade(VersionUpgrade<T> upgrade) {

        ParameterizedType type = (ParameterizedType) upgrade.getClass().getGenericInterfaces()[0];
        @SuppressWarnings("unchecked")
        Class<T> versionType = (Class<T>) type.getActualTypeArguments()[0];
        T currentVersion = versionType.getEnumConstants()[versionType.getEnumConstants().length - 1];

        log.debug("Subsystem should upgrade to " + currentVersion);

        T databaseVersion = versionInfoService.currentVersion(versionType);

        try {
            if (databaseVersion == null) {
                versionInfoService.updateVersion(currentVersion);
            } else {
                //比较下等级
                if (databaseVersion != currentVersion) {
                    upgrade(versionType, databaseVersion, currentVersion, upgrade);
                }
            }
        } catch (Exception ex) {
            throw new InternalError("Failed Upgrade Database", ex);
        }
    }

    private <T extends Enum> void upgrade(Class<T> clazz, T origin, T target, VersionUpgrade<T> upgrader)
            throws Exception {
        log.debug("Subsystem prepare to upgrade to " + target);
        boolean started = false;
        for (T step : clazz.getEnumConstants()) {
            if (origin == null || origin.ordinal() < step.ordinal()) {
                started = true;
            }

            if (started) {
                log.debug("Subsystem upgrade step: to " + target);
                upgrader.upgradeToVersion(step);
                log.debug("Subsystem upgrade step done");
            }

            if (step == target)
                break;
        }

        versionInfoService.updateVersion(target);
    }
}
