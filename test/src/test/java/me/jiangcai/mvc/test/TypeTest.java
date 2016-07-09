package me.jiangcai.mvc.test;

import com.google.gson.JsonObject;
import com.google.gson.stream.JsonWriter;
import me.jiangcai.lib.test.SpringWebTest;
import org.junit.Test;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author CJ
 */
@ContextConfiguration(classes = TypeTest.BasicConfig.class)
@WebAppConfiguration
public abstract class TypeTest extends SpringWebTest {

    @ComponentScan("me.jiangcai.mvc.test.bean")
    static class BasicConfig {

    }

    public static final String URI = "/loveTypeTest";


    /**
     * 测试类型转换
     * 1 写入,分别用普通表单,json传入值, 分别是包装在一个Bean内,一个数组,单独Love
     * 2 展示 这个该怎么形容。。哦对 可以使用RedirectAttributes进行测试
     */
    @Test
    public void testType() throws Exception {
        mockMvc.perform(
                put(URI + "/form")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("love", "haha")
        )
                .andDo(print())
                .andExpect(status().isNoContent());

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("love", "haha");

        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        JsonWriter writer = new JsonWriter(new OutputStreamWriter(buffer));
        writer.beginObject()
                .name("love").value("haha")
                .endObject().flush();

        mockMvc.perform(
                put(URI + "/json")
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(buffer.toByteArray())
        )
                .andDo(print())
                .andExpect(status().isNoContent());
    }

}
