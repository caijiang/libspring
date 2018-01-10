package me.jiangcai.crud.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.jiangcai.crud.BaseTest;
import me.jiangcai.lib.test.matcher.SimpleMatcher;
import org.junit.Test;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.StreamSupport;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author CJ
 */
public class AbstractCrudControllerTest extends BaseTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void go() throws Exception {
        // 这个应该还不存在
        mockMvc.perform(get("/items/1"))
                .andExpect(status().isNotFound());

        // 继续
        // 现在新增
        Map<String, Object> newData = new HashMap<>();
        newData.put("name", "中文呢？");
        newData.put("other", "非标数据");

        // 新增并且获得地址
        String newOneUri = mockMvc.perform(
                post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(newData))
        )
                .andExpect(status().isCreated())
                .andExpect(header().stringValues("Location", new SimpleMatcher<>(
                        data ->
                                StreamSupport.stream(data.spliterator(), false).anyMatch(s -> s != null && s.startsWith("/items/"))
                        , description ->
                        description.appendText("应该包含/items"))))
                .andReturn().getResponse().getHeader("Location");

        // 新增的URI可以打开正确的资源
        mockMvc.perform(get(newOneUri))
                .andExpect(status().isOk())
                .andDo(print());

        // 现在看看吧
        mockMvc.perform(
                get("/items")
        )
                .andDo(print())
                .andExpect(status().isOk());
        mockMvc.perform(
                get("/items/ant-d")
        )
                .andDo(print())
                .andExpect(status().isOk());
        mockMvc.perform(
                get("/items/jQuery")
        )
                .andDo(print())
                .andExpect(status().isOk());
        mockMvc.perform(
                get("/items/select2")
        )
                .andDo(print())
                .andExpect(status().isOk());
    }

}