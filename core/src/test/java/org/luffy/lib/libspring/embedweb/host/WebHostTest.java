package org.luffy.lib.libspring.embedweb.host;

import me.jiangcai.ewp.test.HelloWebConfig;
import org.junit.Test;
import org.luffy.lib.libspring.embedweb.PathService;
import org.luffy.lib.libspring.embedweb.exception.NoSuchEmbedWebException;
import org.luffy.lib.libspring.logging.LoggingConfig;
import org.luffy.test.SpringWebTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.context.WebApplicationContext;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;


/**
 * @author CJ
 */
@WebAppConfiguration
@ContextConfiguration(classes = {WebHost.class, HelloWebConfig.class, InnerWebConfig.class, LoggingConfig.class})
public class WebHostTest extends SpringWebTest {

    @Autowired
    private PathService pathService;
    @Autowired
    private WebApplicationContext webApplicationContext;

    /**
     * 测试部署能力
     *
     * @throws NoSuchEmbedWebException
     */
    @Test
    public void deploy() throws Exception {
        File root = new File("hello");
        File file = new File(root, "foo/bar");
        assertThat(file.getName())
                .isEqualTo("bar");

        // 校验资源
        String path = pathService.forPrivate("inner", "/foo/bar.js");
        File bar1 = new File(webApplicationContext.getServletContext().getRealPath(path));
        assertThat(bar1)
                .exists()
                .isFile();

        path = pathService.forPublic("hello", "/bar/foo.js");
        File bar2 = new File(webApplicationContext.getServletContext().getRealPath(path));
        assertThat(bar2)
                .exists()
                .isFile();

        mockMvc.perform(
                get("/hello")
        )
                .andDo(print());
    }

    @Test
    public void mvc() throws Exception {

        mockMvc.perform(
                get("/loggingConfig")
        )
//                .andDo(print())
        ;
//        PageNotFound pageNotFound;
        mockMvc.perform(
                get("/index")
        )
//                .andDo(print())
        ;
    }

}