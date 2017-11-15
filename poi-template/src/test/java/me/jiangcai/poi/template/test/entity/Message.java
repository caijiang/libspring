package me.jiangcai.poi.template.test.entity;

import lombok.Getter;
import lombok.Setter;
import me.jiangcai.poi.template.ExeclEntityRow;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @author CJ
 */
@Entity
@Setter
@Getter
public class Message implements ExeclEntityRow {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String value1;
    private String value2;
    private String value3;
    private String value4;
    private String value5;
    private String value6;
    private String value7;

    @Override
    public Set<String> keySet() {
        return new HashSet<>(Arrays.asList("id", "value1", "value1", "value2", "value3", "value4", "value5", "value6", "value7"));
    }

    @Override
    public Object get(String key) {
        if ("id".equalsIgnoreCase(key))
            return id;
        if ("value1".equalsIgnoreCase(key))
            return value1;
        if ("value2".equalsIgnoreCase(key))
            return value2;
        if ("value3".equalsIgnoreCase(key))
            return value3;
        if ("value4".equalsIgnoreCase(key))
            return value4;
        if ("value5".equalsIgnoreCase(key))
            return value5;
        if ("value6".equalsIgnoreCase(key))
            return value6;
        if ("value7".equalsIgnoreCase(key))
            return value7;
        return null;
    }
}
