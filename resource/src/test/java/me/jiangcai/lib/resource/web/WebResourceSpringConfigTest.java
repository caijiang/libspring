package me.jiangcai.lib.resource.web;

import me.jiangcai.lib.resource.service.ResourceService;
import me.jiangcai.lib.test.SpringWebTest;
import me.jiangcai.lib.test.matcher.SimpleMatcher;
import org.hamcrest.core.StringStartsWith;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * @author CJ
 */
@ContextConfiguration(classes = {WebResourceSpringConfigTest.Config.class})
@WebAppConfiguration
public class WebResourceSpringConfigTest extends SpringWebTest {
    @Autowired
    private ResourceService resourceService;

    @Test
    public void go() throws Exception {
        mockMvc.perform(
                fileUpload("/_resourceUpload/upload")
                        .file(randomFile("file"))
        )
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", new StringStartsWith("http://")))
                .andExpect(content().string(new SimpleMatcher<String>(str -> resourceService.getResource(str).exists()) {
                }))
        ;
    }

    private MockMultipartFile randomFile(String name) {
        return new MockMultipartFile(name, "test.png", "image/png", new byte[]{1, 2});
    }

    @Configuration
    @Import(WebResourceSpringConfig.class)
    public static class Config {
    }

}