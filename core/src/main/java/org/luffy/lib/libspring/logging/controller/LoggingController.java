package org.luffy.lib.libspring.logging.controller;

import org.luffy.lib.libspring.logging.LoggingConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author CJ
 */
@Controller
//@PreAuthorize("hasAnyRole('ROOT','" + LoggingConfig.ROLE_MANAGER + "')")
public class LoggingController {

    @Autowired
    private LoggingConfig loggingConfig;

    @RequestMapping(method = RequestMethod.GET, value = "/loggingConfig")
    public String index(Model model) {
        model.addAttribute("currentConfigs", loggingConfig.getManageableConfigs());
        return "inner.logging";
    }
}
