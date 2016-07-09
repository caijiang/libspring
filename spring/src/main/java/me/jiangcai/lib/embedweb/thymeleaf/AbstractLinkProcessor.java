package me.jiangcai.lib.embedweb.thymeleaf;

import me.jiangcai.lib.embedweb.PathService;
import me.jiangcai.lib.embedweb.exception.NoSuchEmbedWebException;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.engine.AttributeDefinition;
import org.thymeleaf.engine.AttributeDefinitions;
import org.thymeleaf.engine.AttributeName;
import org.thymeleaf.engine.EngineEventUtils;
import org.thymeleaf.engine.IAttributeDefinitionsAware;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.AbstractAttributeTagProcessor;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.thymeleaf.standard.expression.FragmentExpression;
import org.thymeleaf.standard.expression.IStandardExpression;
import org.thymeleaf.standard.expression.StandardExpressionExecutionContext;
import org.thymeleaf.standard.util.StandardProcessorUtils;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.util.Validate;
import org.unbescape.html.HtmlEscape;

import javax.servlet.ServletContext;

/**
 * 改变连接的处理器
 *
 * @author CJ
 */
public class AbstractLinkProcessor extends AbstractAttributeTagProcessor implements IAttributeDefinitionsAware {

    private final PathService pathService;
    private final String attributeName;
    private AttributeDefinition targetAttributeDefinition;

    protected AbstractLinkProcessor(PathService pathService, TemplateMode templateMode, String dialectPrefix, String attributeName) {
        super(templateMode, dialectPrefix, null, false, attributeName, true, 10000, true);
        this.pathService = pathService;
        this.attributeName = attributeName;
    }

    public void setAttributeDefinitions(final AttributeDefinitions attributeDefinitions) {
        Validate.notNull(attributeDefinitions, "Attribute Definitions cannot be null");
        // We precompute the AttributeDefinition of the target attribute in order to being able to use much
        // faster methods for setting/replacing attributes on the ElementAttributes implementation
        this.targetAttributeDefinition = attributeDefinitions.forName(getTemplateMode(), attributeName);
    }

    @Override
    protected void doProcess(ITemplateContext context, IProcessableElementTag tag, AttributeName attributeName
            , String attributeValue, IElementTagStructureHandler structureHandler) {
        String targetLink = attributeValue;
        try {
            final Object expressionResult;
            if (attributeValue != null) {

                final IStandardExpression expression = EngineEventUtils.computeAttributeExpression(context, tag, attributeName, attributeValue);

                if (expression != null && expression instanceof FragmentExpression) {
                    // This is merely a FragmentExpression (not complex, not combined with anything), so we can apply a shortcut
                    // so that we don't require a "null" result for this expression if the template does not exist. That will
                    // save a call to resource.exists() which might be costly.

                    final FragmentExpression.ExecutedFragmentExpression executedFragmentExpression =
                            FragmentExpression.createExecutedFragmentExpression(context, (FragmentExpression) expression, StandardExpressionExecutionContext.NORMAL);

                    expressionResult =
                            FragmentExpression.resolveExecutedFragmentExpression(context, executedFragmentExpression, true);

                } else {
                    assert expression != null;
                    expressionResult = expression.execute(context);
                }
                targetLink = expressionResult.toString();
            }

        } catch (Exception ignored) {
        }

        targetLink = HtmlEscape.escapeHtml4Xml(targetLink);
        try {
            targetLink = pathService.publicContentPath(targetLink);
            StandardProcessorUtils.replaceAttribute(
                    structureHandler, attributeName, this.targetAttributeDefinition, this.attributeName
                    , targetLink);
        } catch (NoSuchEmbedWebException e) {
            throw new IllegalStateException("please use this tag in EWP pages.");
        }


    }
}
