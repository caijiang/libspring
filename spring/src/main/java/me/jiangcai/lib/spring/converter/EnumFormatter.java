package me.jiangcai.lib.spring.converter;

import org.springframework.format.Formatter;
import org.springframework.util.NumberUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.ParameterizedType;
import java.text.ParseException;
import java.util.Locale;
import java.util.stream.Stream;

/**
 * 可以按name或者索引转换的通用枚举转换器
 * 覆盖{@link #print(Enum, Locale)}方法可以重新定义其展现方式
 *
 * @author CJ
 * @since 3.0
 */
public abstract class EnumFormatter<T extends Enum> implements Formatter<T> {

    @Override
    public T parse(String text, Locale locale) throws ParseException {
        if (StringUtils.isEmpty(text))
            return null;
        // 获取其类型
        ParameterizedType parameterizedType = (ParameterizedType) getClass().getGenericSuperclass();
        @SuppressWarnings("unchecked")
        Class<T> targetType = (Class<T>) parameterizedType.getActualTypeArguments()[0];

        // 先按名字
        return Stream.of(targetType.getEnumConstants())
                .filter(m -> m.name().equals(text))
                .findAny()
                .orElseGet(() -> {
                    // 那就认为是一个数字
                    try {
                        return targetType.getEnumConstants()[NumberUtils.parseNumber(text, Integer.class)];
                    } catch (Exception ex) {
                        throw new IllegalArgumentException("failed convert " + text + " to " + targetType, ex);
                    }
                });
    }

    @Override
    public String print(T object, Locale locale) {
        if (object == null)
            return null;
        return object.toString();
    }
}
