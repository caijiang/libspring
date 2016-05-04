package org.luffy.lib.libspring.embedweb.host;

import org.junit.Test;
import org.luffy.test.SpringWebTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * @author CJ
 */
@WebAppConfiguration
@ContextConfiguration(classes = {WebHost.class})
public class WebHostTest extends SpringWebTest {

    @Test
    public void well() {
        File root = new File("hello");
        File file = new File(root, "foo/bar");
        assertThat(file.getName())
                .isEqualTo("bar");
    }

}