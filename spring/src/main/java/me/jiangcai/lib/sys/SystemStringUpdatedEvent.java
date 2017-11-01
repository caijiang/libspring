package me.jiangcai.lib.sys;

import lombok.Data;

/**
 * 一个系统字符串被删除或者被更新
 *
 * @author CJ
 */
@Data
public class SystemStringUpdatedEvent {
    private final String key;
    private final boolean delete;
    private final Object newValue;
}
