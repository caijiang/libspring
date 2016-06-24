package me.jiangcai.lib.embedweb.host;

import me.jiangcai.lib.embedweb.host.model.EmbedWebInfo;
import me.jiangcai.lib.embedweb.host.service.EmbedWebInfoService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

/**
 * @author CJ
 */
@Aspect
public class ViewNameAdjust {

    private static final Log log = LogFactory.getLog(ViewNameAdjust.class);

    @Autowired
    private EmbedWebInfoService embedWebInfoService;

    @Autowired
    WebApplicationContext webApplicationContext;

    @Pointcut("execution(java.lang.String *(..)) && @annotation(org.springframework.web.bind.annotation.RequestMapping)")
    public void stringController() {

    }

    @Pointcut("execution(org.springframework.web.servlet.ModelAndView *(..))" +
            "&& @annotation(org.springframework.web.bind.annotation.RequestMapping)\"")
    public void modelAndViewController() {
    }

    @Around(value = "stringController()")
    public Object viewName(ProceedingJoinPoint point) throws Throwable {
        embedWebInfoService.setupCurrentEmbedWeb(point.getTarget().getClass());

        EmbedWebInfo info = embedWebInfoService.getCurrentEmbedWebInfo();
        if (log.isDebugEnabled())
            log.debug("[EWP] working in " + info);

        if (info == null)
            return point.proceed();

        String viewName = (String) point.proceed();

        return checkViewName(info, viewName);
    }

    private String checkViewName(EmbedWebInfo info, String viewName) {
        if (viewName==null)
            return null;

        if (viewName.startsWith("redirect:"))
            return viewName;

        String url = info.getPrivateResourceUri().substring(1);
        if (!viewName.startsWith("/"))
            url = url + "/";

        return url + viewName;
    }

    @Around(value = "modelAndViewController()")
    public Object modelAndView(ProceedingJoinPoint point) throws Throwable {
        embedWebInfoService.setupCurrentEmbedWeb(point.getTarget().getClass());
        EmbedWebInfo info = embedWebInfoService.getCurrentEmbedWebInfo();
        if (log.isDebugEnabled())
            log.debug("[EWP] working in " + info);
        if (info == null)
            return point.proceed();
        ModelAndView modelAndView = (ModelAndView) point.proceed();

        if (modelAndView.getView() != null && (modelAndView.getView() instanceof RedirectView)) {
            return modelAndView;
        }

        String viewName = modelAndView.getViewName();

        modelAndView.setViewName(checkViewName(info, viewName));
        return modelAndView;
    }

}
