package me.jiangcai.lib.resource.web;

import com.jayway.jsonpath.JsonPath;
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

import java.net.URLEncoder;
import java.util.UUID;

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
    public void upload() throws Exception {
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

    @Test
    public void jQueryFilesUpload() throws Exception {
        String path = JsonPath.read(mockMvc.perform(
                fileUpload("/_resourceUpload/jQueryFilesUpload")
                        .file(randomFile(UUID.randomUUID().toString()))
        )
                .andExpect(status().isOk())
                .andReturn()
                .getResponse().getContentAsString(), "$[0].path");

        System.out.println(path);
        // 现在去读取吧
        mockMvc.perform(
                get("/_resourceUpload/paths/" + URLEncoder.encode(path, "UTF-8"))
        )
                .andExpect(status().isFound());
        mockMvc.perform(
                delete("/_resourceUpload/paths/" + URLEncoder.encode(path, "UTF-8"))
        )
                .andExpect(status().is2xxSuccessful());
        mockMvc.perform(
                get("/_resourceUpload/paths/" + URLEncoder.encode(path, "UTF-8"))
        )
                .andExpect(status().isNotFound());

    }

    private MockMultipartFile randomFile(String name) {
        return new MockMultipartFile(name, "test.png", "image/png", new byte[]{1, 2});
    }

    @Configuration
    @Import(WebResourceSpringConfig.class)
    public static class Config {
    }

}