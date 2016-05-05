package org.luffy.lib.libspring.embedweb.exception;

import org.luffy.lib.libspring.embedweb.EmbedWeb;

/**
 * 没有找到EmbedWeb异常
 *
 * @author CJ
 */
public class NoSuchEmbedWebException extends Exception {

    private String name;
    private EmbedWeb web;
    private String message;

    public NoSuchEmbedWebException(String name, EmbedWeb web) {
        this.name = name;
        this.web = web;
        initMessage();
    }

    public NoSuchEmbedWebException(Throwable cause, String name, EmbedWeb web) {
        super(cause);
        this.name = name;
        this.web = web;
        initMessage();
    }

    private void initMessage() {
        StringBuilder stringBuilder = new StringBuilder("no EmbedWeb found for ");
        if (name != null)
            stringBuilder.append("name:").append(name).append(" ");
        else if (web != null)
            stringBuilder.append("name:").append(web.name()).append(" ");
        this.message = stringBuilder.toString();
    }

    @Override
    public String getMessage() {
        return message;
    }
}
