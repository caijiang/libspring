package org.luffy.lib.libspring.controller;

import org.junit.Test;
import org.luffy.lib.libspring.SimpleWeb;
import org.luffy.test.SpringWebTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author CJ
 */
@WebAppConfiguration
@ContextConfiguration(classes = {
        LibGlobalController.EndpointsConfig.class
        ,SimpleWeb.class
})
public class EndpointsConfigTest extends SpringWebTest {

    @Test
    public void testLibGlobalController() throws Exception {
        String response = mockMvc.perform(
                get("/endpoints")
        )
                .andExpect(status().isOk())
        .andReturn().getResponse().getContentAsString();

        assertThat(response)
        .contains(SimpleWeb.SIMPLE_URI);
    }
}