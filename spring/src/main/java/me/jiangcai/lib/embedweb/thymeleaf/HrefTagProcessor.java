package me.jiangcai.lib.embedweb.thymeleaf;

import me.jiangcai.lib.embedweb.PathService;
import me.jiangcai.lib.embedweb.exception.NoSuchEmbedWebException;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.engine.AttributeDefinition;
import org.thymeleaf.engine.AttributeDefinitions;
import org.thymeleaf.engine.AttributeName;
import org.thymeleaf.engine.IAttributeDefinitionsAware;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.IElementTagProcessor;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.thymeleaf.standard.processor.AbstractStandardExpressionAttributeTagProcessor;
import org.thymeleaf.standard.util.StandardProcessorUtils;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.util.Validate;
import org.unbescape.html.HtmlEscape;

/**
 * Created by hzbc on 2016/6/16.
 */
public class HrefTagProcessor extends AbstractStandardExpressionAttributeTagProcessor implements IElementTagProcessor
        , IAttributeDefinitionsAware {

    private final static String TAG_NAME = "href";
    private final PathService pathService;
    private AttributeDefinition targetAttributeDefinition;

    public HrefTagProcessor(final String dialectPrefix, PathService pathService) {
        super(TemplateMode.HTML, dialectPrefix, TAG_NAME, 900, false);
        this.pathService = pathService;
    }


    public void setAttributeDefinitions(final AttributeDefinitions attributeDefinitions) {
        Validate.notNull(attributeDefinitions, "Attribute Definitions cannot be null");
        // We precompute the AttributeDefinition of the target attribute in order to being able to use much
        // faster methods for setting/replacing attributes on the ElementAttributes implementation
        this.targetAttributeDefinition = attributeDefinitions.forName(getTemplateMode(), TAG_NAME);
    }


    @Override
    protected final void doProcess(
            final ITemplateContext context,
            final IProcessableElementTag tag,
            final AttributeName attributeName, final String attributeValue,
            final Object expressionResult,
            final IElementTagStructureHandler structureHandler) {

        String newAttributeValue = HtmlEscape.escapeHtml4Xml(expressionResult == null
                ? null : expressionResult.toString());

        try {
            newAttributeValue = pathService.forPublic(newAttributeValue);
        } catch (NoSuchEmbedWebException ignored) {

        }

        StandardProcessorUtils.replaceAttribute(
                structureHandler, attributeName, this.targetAttributeDefinition, TAG_NAME
                , (newAttributeValue == null ? "" : newAttributeValue));

    }
}
