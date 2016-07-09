package me.jiangcai.mvc.test.bean;

import me.jiangcai.mvc.test.model.Love;
import org.springframework.core.convert.converter.Converter;
import org.springframework.format.Formatter;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.Locale;

/**
 * @author CJ
 */
@Component
public class ForLove implements Formatter<Love>,Converter<String,Love> {
    @Override
    public Love parse(String text, Locale locale) throws ParseException {
        if (text==null)
            return null;
        Love love = new Love();
        love.setWho(text);
        return love;
    }

    @Override
    public String print(Love object, Locale locale) {
        if (object==null)
            return null;
        return object.toString();
    }

    @Override
    public Love convert(String source) {
        try {
            return parse(source,null);
        } catch (ParseException e) {
            return null;
        }
    }
}
