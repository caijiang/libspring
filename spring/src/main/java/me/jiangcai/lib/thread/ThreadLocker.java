package me.jiangcai.lib.thread;

/**
 * @author CJ
 * @since 3.0
 */
public interface ThreadLocker {
    /**
     * 建议按照业务形成唯一字符串，并且用它的{@link String#intern()}作为锁
     *
     * @return 作为锁对象
     */
    Object lockObject();
}
