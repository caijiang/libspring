package me.jiangcai.poi.template;

import java.util.Set;

/**
 * execl的一整大行
 *
 * @author CJ
 */
public interface ExeclEntityRow {
    Set<String> keySet();

    Object get(String key);
}
