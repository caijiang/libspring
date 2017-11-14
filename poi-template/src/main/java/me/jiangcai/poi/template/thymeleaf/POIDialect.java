package me.jiangcai.poi.template.thymeleaf;

import org.thymeleaf.dialect.IProcessorDialect;
import org.thymeleaf.processor.IProcessor;
import org.thymeleaf.standard.processor.StandardXmlNsTagProcessor;
import org.thymeleaf.templatemode.TemplateMode;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author CJ
 */
public class POIDialect implements IProcessorDialect {
    @Override
    public String getPrefix() {
        return "poi";
    }

    @Override
    public int getDialectProcessorPrecedence() {
        return 10000;
    }

    @Override
    public Set<IProcessor> getProcessors(String dialectPrefix) {
        Set<IProcessor> processors = new LinkedHashSet<>();
        processors.add(new StandardXmlNsTagProcessor(TemplateMode.XML, dialectPrefix));
        processors.add(new AbstractNamespaceAttributeTagProcessor(TemplateMode.XML, dialectPrefix, "ss", "MergeDown"));

        processors.add(new StandardXmlNsTagProcessor(TemplateMode.HTML, dialectPrefix));
        processors.add(new AbstractNamespaceAttributeTagProcessor(TemplateMode.HTML, dialectPrefix, "ss", "MergeDown"));
        return processors;
    }

    @Override
    public String getName() {
        return "POI";
    }
}
