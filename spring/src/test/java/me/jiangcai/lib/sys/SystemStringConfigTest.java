package me.jiangcai.lib.sys;

import me.jiangcai.lib.sys.service.SystemStringService;
import me.jiangcai.lib.sys.service.SystemStringServiceTest;
import me.jiangcai.lib.test.SpringWebTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;

import java.net.URLEncoder;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author CJ
 */
@WebAppConfiguration
@ContextConfiguration(classes = {SystemStringConfig.class, SystemStringServiceTest.Config.class, SystemStringConfigTest.Config.class})
public class SystemStringConfigTest extends SpringWebTest {

    @Autowired
    private Environment environment;
    @Autowired
    private SystemStringService systemStringService;

    @Test
    public void go() throws Exception {
        systemStringService.getCustomSystemString("test.key", null, true, String.class, "hello");
        String uri = environment.getProperty("me.jiangcai.lib.sys.uri");

        mockMvc.perform(get(uri)
                .locale(Locale.CHINA)
        )
                .andDo(print())
                .andExpect(status().isOk());

        final String content = randomEmailAddress();
        mockMvc.perform(put(uri)
                .param("id", URLEncoder.encode("test.key", "UTF-8"))
                .contentType(MediaType.TEXT_PLAIN)
                .content(content)
        )
                .andExpect(status().is2xxSuccessful());

        assertThat(systemStringService.getCustomSystemString("test.key", null, true, String.class, "hello"))
                .isEqualTo(content);

        mockMvc.perform(delete(uri).param("id", URLEncoder.encode("test.key", "UTF-8")))
                .andExpect(status().is2xxSuccessful());

        assertThat(systemStringService.getCustomSystemString("test.key", null, true, String.class, "hello"))
                .isEqualTo("hello");
    }

    @Configuration
    @PropertySource("classpath:/sys_ui.properties")
    static class Config {
        @Bean
        public MessageSource messageSource() {
            ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
            messageSource.setBasename("classpath:/testMessage");
            return messageSource;
        }
    }

}