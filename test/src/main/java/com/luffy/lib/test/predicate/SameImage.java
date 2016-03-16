package com.luffy.lib.test.predicate;

import java.io.InputStream;

/**
 * 验证2个数据流是否为同一图片
 *
 * @author CJ
 * @deprecated replaced with {@link org.luffy.test.predicate.SameImage}
 */
public class SameImage extends org.luffy.test.predicate.SameImage {

    public SameImage(InputStream data) {
        super(data);
    }
}