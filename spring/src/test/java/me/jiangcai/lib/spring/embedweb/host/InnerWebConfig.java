package me.jiangcai.lib.spring.embedweb.host;


import me.jiangcai.lib.embedweb.EmbedWeb;

/**
 * @author CJ
 */
public class InnerWebConfig implements EmbedWeb {

    @Override
    public String name() {
        return "inner";
    }

    @Override
    public String privateResourcePath() {
        return "/inner/private";
    }

    @Override
    public String publicResourcePath() {
        return null;
    }

}
