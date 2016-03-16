package org.luffy.lib.libspring.controller;

import org.luffy.lib.libspring.config.InnerViewConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

/**
 * Created by luffy on 2015/5/20.
 *
 * @author luffy luffy.ja at gmail.com
 */
public class LibGlobalController {

    @Autowired
    private RequestMappingHandlerMapping handlerMapping;

    @RequestMapping(value = "/endpoints", method = RequestMethod.GET)
    public String endpoints(Model model) {
        model.addAttribute("handlerMethods", handlerMapping.getHandlerMethods());
        return "inner.endpoints";
    }

    /**
     * 载入该配置,可以通过访问/endpoints获取当前所有controller信息
     */
    @Configuration
    @Import(InnerViewConfig.class)
    @Profile("test")
    public static class EndpointsConfig {
        @Bean(name = "_libGlobalController")
        public LibGlobalController libGlobalController() {
            return new LibGlobalController();
        }
    }


}
