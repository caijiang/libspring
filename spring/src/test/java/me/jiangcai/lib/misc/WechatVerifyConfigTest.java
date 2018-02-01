package me.jiangcai.lib.misc;

import me.jiangcai.lib.test.SpringWebTest;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author CJ
 */
@WebAppConfiguration
@ContextConfiguration(classes = WechatVerifyConfig.class)
public class WechatVerifyConfigTest extends SpringWebTest {

    @Test
    public void go() throws Exception {
        mockMvc.perform(
                get("/MP_verify_12345.txt")
        ).andExpect(status().isOk())
                .andExpect(content().string("12345"));
    }

}