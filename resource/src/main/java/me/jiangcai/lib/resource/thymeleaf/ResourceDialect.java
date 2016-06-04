package me.jiangcai.lib.resource.thymeleaf;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.thymeleaf.dialect.AbstractDialect;
import org.thymeleaf.dialect.IProcessorDialect;
import org.thymeleaf.processor.IProcessor;
import org.thymeleaf.standard.processor.StandardXmlNsTagProcessor;
import org.thymeleaf.templatemode.TemplateMode;

import java.util.HashSet;
import java.util.Set;

/**
 * res:src
 * res:href
 * 等标签可以处理{@link String 资源Path},{@link me.jiangcai.lib.resource.Resource 资源}
 * ,{@link me.jiangcai.lib.resource.ResourceHolder}以及{@link me.jiangcai.lib.resource.ResourcePathHolder}
 *
 * TODO 缺少测试
 * @author CJ
 */
@Component
public class ResourceDialect extends AbstractDialect implements IProcessorDialect {

    public static final String PREFIX = "res";
    private final Set<IProcessor> processorSet = new HashSet<>();

    protected ResourceDialect() {
        super("Resource");
    }

    @Autowired
    public void setProcessorSet(Set<ResourceProcess> resourceProcesses) {
        processorSet.addAll(resourceProcesses);
        processorSet.add(new StandardXmlNsTagProcessor(TemplateMode.HTML, PREFIX));
    }

    @Override
    public String getPrefix() {
        return PREFIX;
    }

    @Override
    public int getDialectProcessorPrecedence() {
        return 10000;
    }

    @Override
    public Set<IProcessor> getProcessors(String dialectPrefix) {
        return processorSet;
    }
}
