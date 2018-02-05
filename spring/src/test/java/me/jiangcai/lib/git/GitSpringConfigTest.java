package me.jiangcai.lib.git;

import me.jiangcai.lib.test.SpringWebTest;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

/**
 * @author CJ
 */
@ContextConfiguration(classes = GitSpringConfig.class)
@WebAppConfiguration
public class GitSpringConfigTest extends SpringWebTest {

    @Test
    public void go() throws Exception {
        mockMvc.perform(
                get("/_version")
                        .accept(MediaType.APPLICATION_JSON)
        )
                .andDo(print());
    }

    @EnableWebMvc
    public static class Config {

    }

}