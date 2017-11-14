package me.jiangcai.poi.template.thymeleaf;

import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.engine.AttributeName;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.thymeleaf.standard.processor.AbstractStandardExpressionAttributeTagProcessor;
import org.thymeleaf.templatemode.TemplateMode;

/**
 * 可以导出到namespace属性的处理器
 *
 * @author CJ
 */
public class AbstractNamespaceAttributeTagProcessor extends AbstractStandardExpressionAttributeTagProcessor {


    private final String namespace;
    private final String attrName;

    AbstractNamespaceAttributeTagProcessor(TemplateMode templateMode, String dialectPrefix, String namespace, String attrName) {
        super(templateMode, dialectPrefix, attrName, 10000, false);
        this.namespace = namespace;
        this.attrName = attrName;
    }

    @Override
    protected void doProcess(ITemplateContext context, IProcessableElementTag tag, AttributeName attributeName
            , String attributeValue, Object expressionResult, IElementTagStructureHandler structureHandler) {
        structureHandler.replaceAttribute(attributeName, namespace + ":" + attrName, expressionResult == null ? "" : expressionResult.toString());
    }

}
