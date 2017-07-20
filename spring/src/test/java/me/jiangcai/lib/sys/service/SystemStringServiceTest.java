package me.jiangcai.lib.sys.service;

import me.jiangcai.lib.sys.SystemStringConfig;
import me.jiangcai.lib.test.config.H2DataSourceConfig;
import org.apache.commons.lang.RandomStringUtils;
import org.assertj.core.data.Offset;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AdviceMode;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.ImportResource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author CJ
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {SystemStringConfig.class, SystemStringServiceTest.Config.class})
public class SystemStringServiceTest {

    private Random random = new Random();
    @Autowired
    private SystemStringService systemStringService;

    @Test
    public void updateSystemString() throws Exception {
        final String key = UUID.randomUUID().toString();
        final BigDecimal decimal = new BigDecimal(random.nextDouble());
        systemStringService.updateSystemString(key, decimal);
        assertThat(systemStringService.getSystemString(key, BigDecimal.class, null))
                .isCloseTo(decimal, Offset.offset(new BigDecimal("0.000001")));
    }

    @Test
    public void updateSystemString1() throws Exception {
        final String key = UUID.randomUUID().toString();
        final boolean value = random.nextBoolean();
        systemStringService.updateSystemString(key, value);
        assertThat(systemStringService.getSystemString(key, Boolean.class, null))
                .isEqualTo(value);
    }

    @Test
    public void updateSystemString2() throws Exception {
        final String key = UUID.randomUUID().toString();
        final byte[] bytes = new byte[1];
        random.nextBytes(bytes);
        final byte value = bytes[0];
        systemStringService.updateSystemString(key, value);
        assertThat(systemStringService.getSystemString(key, Byte.class, null))
                .isEqualTo(value);
    }

    @Test
    public void updateSystemString3() throws Exception {
        final String key = UUID.randomUUID().toString();
        final short value = (short) random.nextInt(32767);
        systemStringService.updateSystemString(key, value);
        assertThat(systemStringService.getSystemString(key, Short.class, null))
                .isEqualTo(value);
    }

    @Test
    public void updateSystemString4() throws Exception {
        final String key = UUID.randomUUID().toString();
        final char value = RandomStringUtils.random(1).charAt(0);
        systemStringService.updateSystemString(key, value);
        assertThat(systemStringService.getSystemString(key, Character.class, null))
                .isEqualTo(value);
    }

    @Test
    public void updateSystemString5() throws Exception {
        final String key = UUID.randomUUID().toString();
        final int value = random.nextInt();
        systemStringService.updateSystemString(key, value);
        assertThat(systemStringService.getSystemString(key, Integer.class, null))
                .isEqualTo(value);
    }

    @Test
    public void updateSystemString6() throws Exception {
        final String key = UUID.randomUUID().toString();
        final long value = random.nextLong();
        systemStringService.updateSystemString(key, value);
        assertThat(systemStringService.getSystemString(key, Long.class, null))
                .isEqualTo(value);
    }

    @Test
    public void updateSystemString7() throws Exception {
        final String key = UUID.randomUUID().toString();
        final float value = random.nextFloat();
        systemStringService.updateSystemString(key, value);
        assertThat(systemStringService.getSystemString(key, Float.class, null))
                .isEqualTo(value);
    }

    @Test
    public void updateSystemString8() throws Exception {
        final String key = UUID.randomUUID().toString();
        final double value = random.nextDouble();
        systemStringService.updateSystemString(key, value);
        assertThat(systemStringService.getSystemString(key, Double.class, null))
                .isEqualTo(value);
    }

    @Test
    public void updateSystemString9() throws Exception {
        final String key = UUID.randomUUID().toString();
        final String value = RandomStringUtils.random(10);
        systemStringService.updateSystemString(key, value);
        assertThat(systemStringService.getSystemString(key, String.class, null))
                .isEqualTo(value);
        assertThat(systemStringService.getCustomSystemString(key, "", true, String.class, null))
                .isEqualTo(value);
    }

    @Test
    public void updateSystemString10() throws Exception {
        final String key = UUID.randomUUID().toString();
        final LocalDateTime value = LocalDateTime.now();
        systemStringService.updateSystemString(key, value);
        assertThat(systemStringService.getSystemString(key, LocalDateTime.class, null))
                .isEqualTo(value);
    }

    @Test
    public void updateSystemString11() throws Exception {
        final String key = UUID.randomUUID().toString();
        final LocalDate value = LocalDate.now();
        systemStringService.updateSystemString(key, value);
        assertThat(systemStringService.getSystemString(key, LocalDate.class, null))
                .isEqualTo(value);
    }

    @Test
    public void updateSystemString12() throws Exception {
        final String key = UUID.randomUUID().toString();
        final LocalTime value = LocalTime.now();
        systemStringService.updateSystemString(key, value);
        assertThat(systemStringService.getSystemString(key, LocalTime.class, null))
                .isEqualTo(value);
    }

    @Test
    public void updateSystemString13() throws Exception {
        final String key = UUID.randomUUID().toString();
        final Date value = new Date();
        systemStringService.updateSystemString(key, value);
        assertThat(systemStringService.getSystemString(key, Date.class, null))
                .isEqualTo(value);
    }

    @Test
    public void updateSystemString14() throws Exception {
        final String key = UUID.randomUUID().toString();
        final Calendar value = Calendar.getInstance();
        systemStringService.updateSystemString(key, value);
        assertThat(systemStringService.getSystemString(key, Calendar.class, null))
                .isEqualTo(value);
    }

    @Configuration
    @EnableTransactionManagement(mode = AdviceMode.PROXY)
    @EnableAspectJAutoProxy
    @ImportResource("classpath:/datasource_sys.xml")
    public static class Config extends H2DataSourceConfig {
        @Bean
        public DataSource dataSource() {
            return memDataSource("sys");
        }
    }

}