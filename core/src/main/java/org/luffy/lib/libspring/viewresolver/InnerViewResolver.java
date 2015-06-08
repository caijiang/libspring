package org.luffy.lib.libspring.viewresolver;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.View;
import org.thymeleaf.spring4.SpringTemplateEngine;
import org.thymeleaf.spring4.view.ThymeleafViewResolver;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import java.util.Locale;

/**
 * Created by luffy on 2015/5/20.
 * 内部视图处理器
 *
 * @author luffy luffy.ja at gmail.com
 */
@Component
public class InnerViewResolver extends ThymeleafViewResolver {

    @Autowired
    public void setEnv(Environment env){
        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setPrefix("org/luffy/lib/libspring/");
        templateResolver.setSuffix(".html");
        templateResolver.setCharacterEncoding("UTF-8");
        if (env.acceptsProfiles("dev")) {
            templateResolver.setCacheable(false);
        }

        SpringTemplateEngine engine = new SpringTemplateEngine();
        engine.setTemplateResolver(templateResolver);

        setTemplateEngine(engine);
    }

    public InnerViewResolver() {
        super();
    }

    @Override
    public View resolveViewName(String viewName, Locale locale) throws Exception {
        if (viewName != null && viewName.startsWith("inner.")) {
            viewName = viewName.substring("inner.".length());
            return super.resolveViewName(viewName, locale);
        }
        return null;
    }
}
