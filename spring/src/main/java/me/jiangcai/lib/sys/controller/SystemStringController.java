package me.jiangcai.lib.sys.controller;

import me.jiangcai.lib.sys.SystemStringConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author CJ
 */
@Controller
@PreAuthorize("hasAnyRole('ROOT','" + SystemStringConfig.MANAGER_ROLE + "')")
public class SystemStringController {

    @Autowired
    private Environment environment;

    @GetMapping("${me.jiangcai.lib.sys.uri}")
    public String index(Model model) {
        model.addAttribute("uri", environment.getRequiredProperty("me.jiangcai.lib.sys.uri"));
        return "inner.sys";
    }

}
