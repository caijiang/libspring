package me.jiangcai.lib.resource.thymeleaf.process;

import me.jiangcai.lib.resource.Resource;
import me.jiangcai.lib.resource.ResourceHolder;
import me.jiangcai.lib.resource.ResourcePathHolder;
import me.jiangcai.lib.resource.service.ResourceService;
import me.jiangcai.lib.resource.thymeleaf.ResourceDialect;
import me.jiangcai.lib.resource.thymeleaf.ResourceProcess;
import org.springframework.beans.factory.annotation.Autowired;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.engine.AttributeName;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.thymeleaf.standard.processor.AbstractStandardExpressionAttributeTagProcessor;
import org.thymeleaf.templatemode.TemplateMode;

import java.io.IOException;
import java.net.URL;

/**
 * @author CJ
 */
public abstract class AbstractResourceProcess extends AbstractStandardExpressionAttributeTagProcessor
        implements ResourceProcess {

    private final String attrName;
    @Autowired
    protected ResourceService resourceService;

    protected AbstractResourceProcess(String attrName) {
        super(TemplateMode.HTML, ResourceDialect.PREFIX, attrName, 10000, false);
        this.attrName = attrName;
    }

    @Override
    protected void doProcess(ITemplateContext context, IProcessableElementTag tag, AttributeName attributeName
            , String attributeValue, Object expressionResult, IElementTagStructureHandler structureHandler) {
        if (expressionResult == null)
            return;
        URL url;
        try {
            if (expressionResult instanceof ResourceHolder) {
                url = ((ResourceHolder) expressionResult).getHoldedResource().httpUrl();
            } else if (expressionResult instanceof ResourcePathHolder) {
                url = resourceService.getResource(((ResourcePathHolder) expressionResult).getHoldedResourcePath())
                        .httpUrl();
            } else if (expressionResult instanceof Resource) {
                url = ((Resource) expressionResult).httpUrl();
            } else if (expressionResult instanceof CharSequence) {
                url = resourceService.getResource(expressionResult.toString()).httpUrl();
            } else
                throw new IllegalArgumentException("can not get resource from " + expressionResult);

            structureHandler.replaceAttribute(attributeName, attrName, url.toString());

        } catch (IOException ex) {
            throw new IllegalStateException("failed on resource", ex);
        }

    }
}
