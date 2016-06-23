package me.jiangcai.lib.embedweb.thymeleaf;

import me.jiangcai.lib.embedweb.PathService;
import org.thymeleaf.templatemode.TemplateMode;

import javax.servlet.ServletContext;

/**
 * Created by hzbc on 2016/6/16.
 */
public class HrefTagProcessor extends AbstractLinkProcessor {

    public HrefTagProcessor(ServletContext servletContext, final String dialectPrefix, PathService pathService) {
        super(pathService, servletContext, TemplateMode.HTML, dialectPrefix, TAG_NAME);
    }

    private final static String TAG_NAME = "href";
}
