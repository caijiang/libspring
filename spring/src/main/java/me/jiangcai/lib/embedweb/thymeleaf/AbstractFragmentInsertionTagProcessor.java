package me.jiangcai.lib.embedweb.thymeleaf;

import me.jiangcai.lib.embedweb.PathService;
import me.jiangcai.lib.embedweb.exception.NoSuchEmbedWebException;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.engine.AttributeName;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.thymeleaf.standard.processor.AbstractStandardFragmentInsertionTagProcessor;
import org.thymeleaf.templatemode.TemplateMode;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author CJ
 */
class AbstractFragmentInsertionTagProcessor extends AbstractStandardFragmentInsertionTagProcessor {

    private final PathService pathService;
    private static final Pattern FragmentPattern = Pattern.compile("(.+)(?=::)");

    AbstractFragmentInsertionTagProcessor(PathService pathService, TemplateMode templateMode
            , String dialectPrefix, String attributeName, boolean replaceHost) {
        super(templateMode, dialectPrefix, attributeName, 10000, replaceHost);
        this.pathService = pathService;
    }

    @Override
    protected void doProcess(ITemplateContext context, IProcessableElementTag tag, AttributeName attributeName
            , String attributeValue, IElementTagStructureHandler structureHandler) {
        // xx:.. replace xx to ..
        if (attributeValue == null) {
            super.doProcess(context, tag, attributeName, null, structureHandler);
            return;
        }

        try {
            Matcher matcher = FragmentPattern.matcher(attributeValue);
            if (!matcher.find()) {
                super.doProcess(context, tag, attributeName, pathService.forPrivate(attributeValue), structureHandler);
                return;
            }

            super.doProcess(context, tag, attributeName, matcher.replaceFirst(pathService.forPrivate(matcher.group(1))), structureHandler);
        } catch (NoSuchEmbedWebException ex) {
            throw new IllegalStateException("please use this tag in EWP pages.");
        }


    }
}
