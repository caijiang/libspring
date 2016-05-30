package org.luffy.lib.libspring.controller;

import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Controller;

/**
 * Created by luffy on 2015/5/20.
 *
 * @deprecated 使用 {@link me.jiangcai.lib.spring.controller.LibGlobalController}代替
 * @author luffy luffy.ja at gmail.com
 */
@Controller
public class LibGlobalController extends me.jiangcai.lib.spring.controller.LibGlobalController {

    /**
     * 载入该配置,可以通过访问/endpoints获取当前所有controller信息
     */
    @Configuration
    public static class EndpointsConfig extends me.jiangcai.lib.spring.controller.LibGlobalController.EndpointsConfig {
    }


}
