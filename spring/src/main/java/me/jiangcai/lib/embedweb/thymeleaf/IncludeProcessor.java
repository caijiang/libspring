package me.jiangcai.lib.embedweb.thymeleaf;

import me.jiangcai.lib.embedweb.PathService;
import org.thymeleaf.templatemode.TemplateMode;

/**
 * @author CJ
 */
public class IncludeProcessor extends AbstractFragmentInsertionTagProcessor{

    protected IncludeProcessor(PathService pathService, String dialectPrefix) {
        super(pathService, TemplateMode.HTML, dialectPrefix, "include", false);
    }
}
