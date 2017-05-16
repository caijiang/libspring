package me.jiangcai.lib.spring.converter;

import org.junit.Test;

import java.text.ParseException;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author CJ
 */
public class EnumFormatterTest extends EnumFormatter<Locale.Category> {

    @Test
    public void go() throws ParseException {
        assertThat(this.parse("DISPLAY", null))
                .isEqualTo(Locale.Category.DISPLAY);
        assertThat(this.parse("FORMAT", null))
                .isEqualTo(Locale.Category.FORMAT);
        assertThat(this.parse("0", null))
                .isEqualTo(Locale.Category.DISPLAY);
        assertThat(this.parse("1", null))
                .isEqualTo(Locale.Category.FORMAT);

        assertThat(this.parse(null, null))
                .isEqualTo(null);
    }


}