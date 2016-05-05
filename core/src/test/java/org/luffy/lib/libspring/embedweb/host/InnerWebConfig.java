package org.luffy.lib.libspring.embedweb.host;

import org.luffy.lib.libspring.embedweb.EmbedWeb;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ViewResolver;

/**
 * @author CJ
 */
public class InnerWebConfig implements EmbedWeb {

    @Bean
    public SimpleController simpleController() {
        return new SimpleController();
    }

    @Override
    public String name() {
        return "inner";
    }

    @Override
    public String privateResourcePath() {
        return "/inner/private";
    }

    @Override
    public String publicResourcePath() {
        return "/inner/public";
    }

    /**
     * 很显然 作为EWP开发 这里返回结果的视图 自然是取自 自身资源的
     */
    @Controller
    public static class SimpleController {

        @RequestMapping
        public String hello() {
            // 其实应该是寻找.. /inner/private
            // 认真的说
            ViewResolver viewResolver;
            // 但它输入参数太少了 应该继续寻找底层
            return "hello";
        }

    }
}
