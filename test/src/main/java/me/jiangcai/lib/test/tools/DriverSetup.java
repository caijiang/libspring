/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2017. All rights reserved.
 *
 */

package me.jiangcai.lib.test.tools;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StreamUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFileAttributeView;
import java.nio.file.attribute.PosixFilePermission;
import java.util.Arrays;
import java.util.HashSet;

/**
 * @author CJ
 */
public class DriverSetup {

    static {
        try {
            setup();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void setup() throws IOException {
        forDriver("webdriver.chrome.driver", "chrome");
        forDriver("webdriver.gecko.driver", "gecko");
    }

    public static WebDriver newDriver() {
        return new ChromeDriver();
    }

    private static void forDriver(String driverPropertyName, String browserName) throws IOException {
        if (System.getProperty(driverPropertyName) != null)
            return;
        // Optional, if not specified, WebDriver will search your path for chromedriver.
        // 平台判定
        String targetDriver;
        final String osName = System.getProperty("os.name");
        if (osName.contains("Mac")) {
            File file = File.createTempFile(browserName, "driver");
            try (InputStream inputStream = new ClassPathResource("/driver/mac/" + browserName + "driver").getInputStream()) {
                try (FileOutputStream outputStream = new FileOutputStream(file)) {
                    StreamUtils.copy(inputStream, outputStream);
                }
            }
            file.deleteOnExit();
            final Path filePath = Paths.get(file.toURI());
            Files.getFileAttributeView(filePath, PosixFileAttributeView.class)
                    .setPermissions(new HashSet<>(Arrays.asList(
                            PosixFilePermission.OWNER_EXECUTE,
                            PosixFilePermission.OWNER_READ,
                            PosixFilePermission.OWNER_WRITE
                    )));
            assert file.canExecute();
            targetDriver = file.getAbsolutePath();
        } else if (osName.contains("Win")) {
            File file = File.createTempFile(browserName, "driver.exe");
//            assert file.setExecutable(true);
            try (InputStream inputStream = new ClassPathResource("/driver/win32/" + browserName + "driver").getInputStream()) {
                try (FileOutputStream outputStream = new FileOutputStream(file)) {
                    StreamUtils.copy(inputStream, outputStream);
                }
            }
            file.deleteOnExit();
            targetDriver = file.getAbsolutePath();
        } else {
            System.out.printf("not support for:" + osName + ", can only run in no-driver profile.");
            return;
        }
        System.setProperty(driverPropertyName, targetDriver);
    }

}
