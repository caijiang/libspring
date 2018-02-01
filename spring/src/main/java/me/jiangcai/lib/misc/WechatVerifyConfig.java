package me.jiangcai.lib.misc;

import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * 简单！实用！总能通过微信的校验！
 * 请在安全设置中为它让道 /MP_verify_*.txt
 *
 * @author CJ
 * @since 4.0
 */
@Configuration
@EnableWebMvc
public class WechatVerifyConfig extends WebMvcConfigurerAdapter {

    @Controller
    public static class VerifyController {
        @GetMapping("/MP_verify_{data}.txt")
        @ResponseBody
        public String fine(@PathVariable("data") String input) {
            return input;
        }
    }
//
//    @Bean
//    public VerifyController verifyController() {
//        return new VerifyController();
//    }

}
