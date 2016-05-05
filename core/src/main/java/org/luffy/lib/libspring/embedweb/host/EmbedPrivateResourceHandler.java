package org.luffy.lib.libspring.embedweb.host;

import org.luffy.lib.libspring.embedweb.host.model.EmbedWebInfo;
import org.luffy.lib.libspring.embedweb.host.service.EmbedWebInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.mvc.method.annotation.ViewNameMethodReturnValueHandler;

/**
 * Created by luffy on 2016/5/5.
 *
 * @author luffy luffy.ja at gmail.com
 */
@Component
public class EmbedPrivateResourceHandler extends ViewNameMethodReturnValueHandler implements HandlerMethodReturnValueHandler {

    @Autowired
    private EmbedWebInfoService embedWebInfoService;

    @Override
    public boolean supportsReturnType(MethodParameter returnType) {
        Class<?> paramType = returnType.getParameterType();
        if (!(void.class == paramType || CharSequence.class.isAssignableFrom(paramType)))
            return false;
        embedWebInfoService.setupCurrentEmbedWeb(returnType.getMethod().getDeclaringClass().getPackage());
        return embedWebInfoService.getCurrentEmbedWebInfo() != null;
    }

    @Override
    public void handleReturnValue(Object returnValue, MethodParameter returnType, ModelAndViewContainer mavContainer
            , NativeWebRequest webRequest) throws Exception {

        if (returnValue instanceof CharSequence) {
            String viewName = returnValue.toString();
            if (isRedirectViewName(viewName)) {
                mavContainer.setViewName(viewName);
                mavContainer.setRedirectModelScenario(true);
            } else {
                // 最简陋的实现，内含的约定是 视图总是从根目录开始获取的
                EmbedWebInfo info = embedWebInfoService.getCurrentEmbedWebInfo();
                String url = info.getPrivateResourceUri().substring(1);
                if (!viewName.startsWith("/"))
                    url = url + "/";
                mavContainer.setViewName(url + viewName);
                // mavContainer.setViewName(viewName);
//                ViewResolver viewResolver = null;
//
//                View view = viewResolver.resolveViewName(viewName,webRequest.getLocale());
//                mavContainer.setView(view);
            }
        }
    }
}
