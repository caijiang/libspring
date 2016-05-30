package org.luffy.test.predicate;

import java.io.InputStream;

/**
 * 验证2个数据流是否为同一图片
 *
 * @author CJ
 * @since 2.0
 * @deprecated 使用 {@link me.jiangcai.lib.test.predicate.SameImage}代替
 */
public class SameImage extends me.jiangcai.lib.test.predicate.SameImage {

    public SameImage(InputStream data) {
        super(data);
    }
}
