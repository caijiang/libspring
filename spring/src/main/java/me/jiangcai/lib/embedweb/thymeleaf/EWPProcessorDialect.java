package me.jiangcai.lib.embedweb.thymeleaf;

import me.jiangcai.lib.embedweb.PathService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.thymeleaf.dialect.AbstractProcessorDialect;
import org.thymeleaf.processor.IProcessor;
import org.thymeleaf.standard.processor.StandardXmlNsTagProcessor;
import org.thymeleaf.templatemode.TemplateMode;

import javax.servlet.ServletContext;
import java.util.HashSet;
import java.util.Set;

/**
 * @author CJ
 */
@Component
public class EWPProcessorDialect extends AbstractProcessorDialect {

    @Autowired
    private PathService pathService;

    @Autowired
    ServletContext servletContext;

    public EWPProcessorDialect() {
        super("EmbedWeb", "ewp", 900);
    }

    @Override
    public Set<IProcessor> getProcessors(String dialectPrefix) {
        HashSet<IProcessor> processors = new HashSet<>();

//        processors.add(new StandardSrcTagProcessor(dialectPrefix));
        processors.add(new SrcTagProcessor(servletContext, dialectPrefix, pathService));
        processors.add(new HrefTagProcessor(servletContext, dialectPrefix, pathService));
        processors.add(new ReplaceProcessor(pathService, dialectPrefix));
        processors.add(new IncludeProcessor(pathService, dialectPrefix));
        processors.add(new StandardXmlNsTagProcessor(TemplateMode.HTML, dialectPrefix));
        return processors;
    }
}
