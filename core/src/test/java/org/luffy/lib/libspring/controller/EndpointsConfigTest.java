package org.luffy.lib.libspring.controller;

import org.junit.Test;
import org.luffy.test.SpringWebTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author CJ
 */
@WebAppConfiguration
@ContextConfiguration(classes = {LibGlobalController.EndpointsConfig.class})
public class EndpointsConfigTest extends SpringWebTest {

    @Test
    public void testLibGlobalController() throws Exception {
        mockMvc.perform(
                get("/endpoints")
        )
                .andDo(print())
                .andExpect(status().isOk());
    }
}