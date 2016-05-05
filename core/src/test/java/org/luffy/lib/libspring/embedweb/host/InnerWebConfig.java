package org.luffy.lib.libspring.embedweb.host;

import org.luffy.lib.libspring.embedweb.EmbedWeb;

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
        return "/inner/public";
    }
}
