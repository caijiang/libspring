package org.luffy.test.predicate;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.function.Predicate;

/**
 * 验证2个数据流是否为同一图片
 *
 * @author CJ
 * @since 2.0
 */
public class SameImage implements Predicate<InputStream> {

    private final InputStream data;

    public SameImage(InputStream data) {
        this.data = data;
    }

    @Override
    public boolean test(InputStream inputStream) {

        try {
            BufferedImage image1 = ImageIO.read(data);
            BufferedImage image2 = ImageIO.read(inputStream);
            ByteArrayOutputStream pngBuffer1 = new ByteArrayOutputStream();
            ByteArrayOutputStream pngBuffer2 = new ByteArrayOutputStream();
            ImageIO.write(image1, "PNG", pngBuffer1);
            ImageIO.write(image2, "PNG", pngBuffer2);

            return Arrays.equals(pngBuffer1.toByteArray(), pngBuffer2.toByteArray());
        } catch (IOException e) {
            return false;
        }

    }
}
