package me.jiangcai.lib.embedweb.thymeleaf;

import me.jiangcai.lib.embedweb.PathService;
import me.jiangcai.lib.embedweb.exception.NoSuchEmbedWebException;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.dialect.IDialect;
import org.thymeleaf.engine.AttributeDefinition;
import org.thymeleaf.engine.AttributeDefinitions;
import org.thymeleaf.engine.AttributeName;
import org.thymeleaf.engine.IAttributeDefinitionsAware;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.IElementTagProcessor;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.thymeleaf.standard.expression.LinkExpression;
import org.thymeleaf.standard.processor.AbstractStandardExpressionAttributeTagProcessor;
import org.thymeleaf.standard.util.StandardProcessorUtils;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.util.Validate;
import org.unbescape.html.HtmlEscape;

import javax.servlet.ServletContext;

/**
 * @author CJ
 */
public class SrcTagProcessor extends AbstractStandardExpressionAttributeTagProcessor implements IElementTagProcessor
        , IAttributeDefinitionsAware {


    private final static String TAG_NAME = "src";
    private final PathService pathService;
    private AttributeDefinition targetAttributeDefinition;
    private ServletContext servletContext;

    public SrcTagProcessor(ServletContext servletContext,final String dialectPrefix, PathService pathService) {
        super(TemplateMode.HTML, dialectPrefix, TAG_NAME, 900, false);
        this.pathService = pathService;
        this.servletContext=servletContext;
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

        // 输入值就是资源相对于public的路径(String)
        String newAttributeValue = HtmlEscape.escapeHtml4Xml(expressionResult == null
                ? null : expressionResult.toString());

        try {
            newAttributeValue =servletContext.getContextPath()+pathService.forPublic(newAttributeValue);
        } catch (NoSuchEmbedWebException ignored) {

        }

        // 额外的工作 类似@{}

        StandardProcessorUtils.replaceAttribute(
                structureHandler, attributeName, this.targetAttributeDefinition, TAG_NAME
                , (newAttributeValue == null ? "" : newAttributeValue));

    }

}