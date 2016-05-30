package me.jiangcai.lib.upgrade.service;

import me.jiangcai.lib.upgrade.UpgradeSpringConfig;
import me.jiangcai.lib.upgrade.VersionInfoService;
import me.jiangcai.lib.upgrade.VersionUpgrade;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static me.jiangcai.lib.upgrade.service.FemaleVersion.girl;

/**
 * @author CJ
 */
@ContextConfiguration(classes = UpgradeServiceTest.Config.class)
@RunWith(SpringJUnit4ClassRunner.class)
public class UpgradeServiceTest {

    private static final Log log = LogFactory.getLog(UpgradeServiceTest.class);

    @Import(UpgradeSpringConfig.class)
    public static class Config {

        @Bean
        public VersionInfoService versionInfoService() {
            return new VersionInfoService() {

                private Enum current = girl;

                @Override
                public <T extends Enum> T currentVersion(Class<T> type) {
                    //noinspection unchecked
                    return (T) current;
                }

                @Override
                public <T extends Enum> void updateVersion(T currentVersion) {
                    current = currentVersion;
                    log.info("sub-upgrade to "+currentVersion);
                }
            };
        }

    }

    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    private UpgradeService upgradeService;

    @Test
    public void systemUpgrade() throws Exception {
        upgradeService.systemUpgrade(new VersionUpgrade<FemaleVersion>() {
            @Override
            public void upgradeToVersion(FemaleVersion version) throws Exception {
                log.info("try to upgrade to " + version);
            }
        });
    }

}