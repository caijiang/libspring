package me.jiangcai.lib.sys.service;

import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.Date;

/**
 * 系统级别
 *
 * @author CJ
 * @since 3.0
 */
public interface SystemStringService {

    /**
     * 获取配置值
     *
     * @param key          key
     * @param exceptedType 期待的类型
     * @param defaultValue 默认数据
     * @param <T>          范型，支持所有的基本数据类型以及各种时间类型
     * @return 默认数据或者当前值
     */
    @Transactional(readOnly = true)
    <T> T getSystemString(String key, Class<T> exceptedType, T defaultValue);

    // 基本
    @Transactional
    default void updateSystemString(String key, boolean value) {
        updateSystemString(key, String.valueOf(value));
    }

    @Transactional
    default void updateSystemString(String key, byte value) {
        updateSystemString(key, String.valueOf(value));
    }

    @Transactional
    default void updateSystemString(String key, short value) {
        updateSystemString(key, String.valueOf(value));
    }

    @Transactional
    default void updateSystemString(String key, char value) {
        updateSystemString(key, String.valueOf(value));
    }

    @Transactional
    default void updateSystemString(String key, int value) {
        updateSystemString(key, String.valueOf(value));
    }

    @Transactional
    default void updateSystemString(String key, double value) {
        updateSystemString(key, String.valueOf(value));
    }

    @Transactional
    default void updateSystemString(String key, long value) {
        updateSystemString(key, String.valueOf(value));
    }

    @Transactional
    default void updateSystemString(String key, float value) {
        updateSystemString(key, String.valueOf(value));
    }

    @Transactional
    void updateSystemString(String key, BigDecimal decimal);

    @Transactional
    void updateSystemString(String key, String value);

    @Transactional
    void updateSystemString(String key, LocalDateTime value);

    @Transactional
    void updateSystemString(String key, LocalDate value);

    @Transactional
    void updateSystemString(String key, LocalTime value);

    @Transactional
    void updateSystemString(String key, Date value);

    @Transactional
    void updateSystemString(String key, Calendar value);

}
