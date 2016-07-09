package me.jiangcai.lib.embedweb.thymeleaf;

import me.jiangcai.lib.embedweb.PathService;
import org.thymeleaf.templatemode.TemplateMode;

import javax.servlet.ServletContext;

/**
 * @author CJ
 */
public class SrcTagProcessor extends AbstractLinkProcessor {

    private final static String TAG_NAME = "src";

    public SrcTagProcessor(final String dialectPrefix, PathService pathService) {
        super(pathService, TemplateMode.HTML, dialectPrefix, TAG_NAME);
    }


}