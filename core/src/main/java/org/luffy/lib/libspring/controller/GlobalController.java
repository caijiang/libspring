package org.luffy.lib.libspring.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

/**
 * Created by luffy on 2015/5/20.
 *
 * @author luffy luffy.ja at gmail.com
 */
@Controller
public class GlobalController {

    @Autowired
    private RequestMappingHandlerMapping handlerMapping;

    @RequestMapping(value="/endpoints", method= RequestMethod.GET)
    public String endpoints(Model model) {
        model.addAttribute("handlerMethods", handlerMapping.getHandlerMethods());
        return "inner.endpoints";
    }


}
