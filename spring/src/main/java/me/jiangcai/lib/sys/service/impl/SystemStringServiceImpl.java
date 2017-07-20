package me.jiangcai.lib.sys.service.impl;

import me.jiangcai.lib.sys.entity.SystemString;
import me.jiangcai.lib.sys.repository.SystemStringRepository;
import me.jiangcai.lib.sys.service.SystemStringService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.NumberUtils;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * @author CJ
 * @since 3.0
 */
@Service("systemStringService")
public class SystemStringServiceImpl implements SystemStringService {

    private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS"
            , Locale.CHINA);
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss:SSS"
            , Locale.CHINA);
    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd"
            , Locale.CHINA);
    private static final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss:SSS"
            , Locale.CHINA);

    private static final Log log = LogFactory.getLog(SystemStringServiceImpl.class);

    private final SystemStringRepository systemStringRepository;

    @Autowired
    public SystemStringServiceImpl(SystemStringRepository systemStringRepository) {
        this.systemStringRepository = systemStringRepository;
    }

    @Override
    public <T> T getSystemString(String key, Class<T> exceptedType, T defaultValue) {
        SystemString ss = systemStringRepository.findOne(key);
        if (ss == null)
            return defaultValue;
        if (ss.getValue() == null)
            return defaultValue;

        return toValue(exceptedType, ss.getValue());
    }

    @Override
    public <T> T getCustomSystemString(String key, String comment, boolean runtime, Class<T> exceptedType, T defaultValue) {
        SystemString ss = systemStringRepository.findOne(key);
        if (ss == null) {
            ss = new SystemString();
            ss.setId(key);
            ss = systemStringRepository.save(ss);
        }

        ss.setComment(comment);
        ss.setCustom(true);
        ss.setRuntime(runtime);
        ss.setJavaTypeName(exceptedType.getName());

        if (ss.getValue() == null)
            return defaultValue;

        return toValue(exceptedType, ss.getValue());
    }

    @SuppressWarnings("unchecked")
    private <T> T toValue(Class<T> type, String value) {
        if (type.equals(String.class))
            return (T) value;
        else if (type.equals(BigDecimal.class))
            return (T) new BigDecimal(value);
        else if (type.equals(LocalDateTime.class))
            return (T) LocalDateTime.from(dateTimeFormatter.parse(value));
        else if (type.equals(LocalDate.class))
            return (T) LocalDate.from(dateFormatter.parse(value));
        else if (type.equals(LocalTime.class))
            return (T) LocalTime.from(timeFormatter.parse(value));
        else if (type.equals(Date.class))
            try {
                return (T) simpleDateFormat.parse(value);
            } catch (ParseException e) {
                log.info("pares " + value + " into Date", e);
                throw new IllegalStateException("pares " + value + " into Date", e);
            }
        else if (type.equals(Calendar.class)) {
            try {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(simpleDateFormat.parse(value));
                return (T) calendar;
            } catch (ParseException e) {
                log.info("pares " + value + " into Calendar", e);
                throw new IllegalStateException("pares " + value + " into Calendar", e);
            }
        } else if (type.equals(Boolean.class))
            return (T) Boolean.valueOf(value);
        else if (type.equals(Character.class))
            return (T) Character.valueOf(value.charAt(0));
        else if (Number.class.isAssignableFrom(type)) {
            Class<? extends Number> numberType = (Class<? extends Number>) type;
            return (T) NumberUtils.parseNumber(value, numberType);
        } else
            throw new IllegalArgumentException("not support type for:" + type);
    }

    @Override
    public void updateSystemString(String key, BigDecimal decimal) {
        if (decimal == null) {
            updateSystemString(key, (String) null);
        } else {
            updateSystemString(key, decimal.toString());
        }
    }

    @Override
    public void updateSystemString(String key, String value) {
        SystemString ss = systemStringRepository.findOne(key);
        if (ss == null) {
            ss = new SystemString();
            ss.setId(key);
        }
        ss.setValue(value);
        systemStringRepository.save(ss);
    }

    @Override
    public void updateSystemString(String key, LocalDateTime value) {
        if (value == null) {
            updateSystemString(key, (String) null);
        } else {
            updateSystemString(key, dateTimeFormatter.format(value));
        }
    }

    @Override
    public void updateSystemString(String key, LocalDate value) {
        if (value == null) {
            updateSystemString(key, (String) null);
        } else {
            updateSystemString(key, dateFormatter.format(value));
        }
    }

    @Override
    public void updateSystemString(String key, LocalTime value) {
        if (value == null) {
            updateSystemString(key, (String) null);
        } else {
            updateSystemString(key, timeFormatter.format(value));
        }
    }

    @Override
    public void updateSystemString(String key, Date value) {
        if (value == null) {
            updateSystemString(key, (String) null);
        } else {
            updateSystemString(key, simpleDateFormat.format(value));
        }
    }

    @Override
    public void updateSystemString(String key, Calendar value) {
        updateSystemString(key, value.getTime());
    }

    @Override
    public void delete(String key) {
        if (systemStringRepository.findOne(key) != null)
            systemStringRepository.delete(key);
    }

    @Override
    public List<SystemString> listCustom() {
        return systemStringRepository.findAll((root, query, cb) -> cb.and(
                cb.isTrue(root.get("custom"))
                , cb.isNotNull(root.get("javaTypeName"))
        ));
    }
}
