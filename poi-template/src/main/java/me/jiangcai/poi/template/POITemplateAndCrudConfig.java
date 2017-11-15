package me.jiangcai.poi.template;

import me.jiangcai.crud.CrudConfig;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author CJ
 */
@Configuration
@Import({CrudConfig.class, POITemplateConfig.class})
@ComponentScan("me.jiangcai.poi.template.crud")
public class POITemplateAndCrudConfig {
}
