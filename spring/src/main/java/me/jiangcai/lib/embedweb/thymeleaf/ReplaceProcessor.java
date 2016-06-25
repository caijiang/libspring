package me.jiangcai.lib.embedweb.thymeleaf;

import me.jiangcai.lib.embedweb.PathService;
import org.thymeleaf.standard.processor.StandardReplaceTagProcessor;
import org.thymeleaf.templatemode.TemplateMode;

/**
 * @author CJ
 */
public class ReplaceProcessor extends AbstractFragmentInsertionTagProcessor{

    protected ReplaceProcessor(PathService pathService, String dialectPrefix) {
        super(pathService, TemplateMode.HTML, dialectPrefix, "replace", true);
    }
}
