package me.jiangcai.lib.spring.viewresolver;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.View;
import org.thymeleaf.spring4.SpringTemplateEngine;
import org.thymeleaf.spring4.messageresolver.SpringMessageResolver;
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

    @Autowired(required = false)
    private MessageSource sysMessageSource;

    public InnerViewResolver() {
        super();
    }

    @Autowired
    public void setEnv(Environment env) {
        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setName("LuffyClassLoaderTemplateResolver");
        templateResolver.setOrder(0);
        // 具有极高的优先级,所以它在获取解决方案时  应该判定是否存在该资源
        templateResolver.setPrefix("me/jiangcai/lib/spring/");
        templateResolver.setSuffix(".html");
        setContentType(MediaType.TEXT_HTML_VALUE + ";charset=UTF-8");
        setCharacterEncoding("UTF-8");
        templateResolver.setCharacterEncoding("UTF-8");
        if (env.acceptsProfiles("dev")) {
            templateResolver.setCacheable(false);
        }

        SpringTemplateEngine engine = new SpringTemplateEngine();
        engine.setTemplateResolver(templateResolver);
        if (sysMessageSource != null) {
            engine.setMessageSource(sysMessageSource);
            final SpringMessageResolver messageResolver = new SpringMessageResolver();
            messageResolver.setMessageSource(sysMessageSource);
            engine.setMessageResolver(messageResolver);
        }

        setTemplateEngine(engine);
    }

    @Override
    public int getOrder() {
        // 具有极高的优先级,所以它在获取解决方案时  应该判定是否存在该资源
        return 0;
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
