package org.luffy.lib.libspring.embedweb.thymeleaf;

import org.luffy.lib.libspring.embedweb.PathService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.thymeleaf.dialect.AbstractProcessorDialect;
import org.thymeleaf.processor.IProcessor;
import org.thymeleaf.standard.processor.StandardXmlNsTagProcessor;
import org.thymeleaf.templatemode.TemplateMode;

import java.util.HashSet;
import java.util.Set;

/**
 * @author CJ
 */
@Component
public class EWPProcessorDialect extends AbstractProcessorDialect {

    @Autowired
    private PathService pathService;

    public EWPProcessorDialect() {
        super("EmbedWeb", "ewp", 900);
    }

    @Override
    public Set<IProcessor> getProcessors(String dialectPrefix) {
        HashSet<IProcessor> processors = new HashSet<>();

//        processors.add(new StandardSrcTagProcessor(dialectPrefix));
        processors.add(new SrcTagProcessor(dialectPrefix, pathService));
        processors.add(new StandardXmlNsTagProcessor(TemplateMode.HTML, dialectPrefix));
        return processors;
    }
}
