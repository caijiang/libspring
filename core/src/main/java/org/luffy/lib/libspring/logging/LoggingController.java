package org.luffy.lib.libspring.logging;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.view.RedirectView;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author CJ
 */
@Controller
@PreAuthorize("hasAnyRole('ROOT','" + LoggingConfig.ROLE_MANAGER + "')")
public class LoggingController {


    private final Map<String, String> manageableConfigs = Collections.synchronizedMap(new HashMap<>());
    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;
//
//    public void setManageableConfigs(Map<String, String> manageableConfigs) {
//        this.manageableConfigs = manageableConfigs;
//    }

    public Map<String, String> getManageableConfigs() {
        return manageableConfigs;
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/loggingConfig/{name}/")
    public RedirectView delete(@PathVariable("name") String name) {
        manageableConfigs.remove(name);
        applicationEventPublisher.publishEvent(new RefreshLoggingEvent());
//        System.out.println("after delete,there is " + manageableConfigs.size() + " configs");
        return new RedirectView("/loggingConfig");
    }

    @RequestMapping(method = RequestMethod.POST, value = "/loggingConfig")
    public RedirectView add(String name, String level) {
        manageableConfigs.put(name, level);
        applicationEventPublisher.publishEvent(new RefreshLoggingEvent());
        return new RedirectView("/loggingConfig");
    }

    @RequestMapping(method = RequestMethod.GET, value = "/loggingConfig")
    public String index(Model model) {
//        System.out.println("there is " + manageableConfigs.size() + " configs");
//        DefaultAnnotationHandlerMapping annotationHandlerMapping;
//        org.springframework.web.servlet.PageNotFound pageNotFound;
        model.addAttribute("currentConfigs", manageableConfigs);
        return "inner.logging";
    }
}
