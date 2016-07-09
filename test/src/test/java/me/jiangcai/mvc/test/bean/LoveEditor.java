package me.jiangcai.mvc.test.bean;

import me.jiangcai.mvc.test.model.Love;

import java.beans.PropertyEditorSupport;

/**
 * @author CJ
 */
public class LoveEditor extends PropertyEditorSupport {

    @Override
    public void setSource(Object source) {
        super.setSource(source);
    }

    @Override
    public void setValue(Object value) {
        super.setValue(value);
    }

    @Override
    public Object getValue() {
        return super.getValue();
    }

    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        if (text==null)
            return;
        Love love = new Love();
        love.setWho(text);
        setValue(love);
    }

    @Override
    public String getAsText() {
        return super.getAsText();
    }
}
