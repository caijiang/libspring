package me.jiangcai.lib.resource.service;

import me.jiangcai.lib.resource.Resource;
import me.jiangcai.lib.resource.ResourceSpringConfig;
import me.jiangcai.lib.test.SpringWebTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;

import java.io.ByteArrayInputStream;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author CJ
 */
@ContextConfiguration(classes = {ResourceServiceTest.ResourceServiceTestConfig.class})
@WebAppConfiguration
public class ResourceServiceTest extends SpringWebTest {

    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    private ResourceService resourceService;

    @Test
    public void uploadResource() throws Exception {
        byte[] randomData = new byte[random.nextInt(300) + 400];
        random.nextBytes(randomData);

        String path = UUID.randomUUID().toString();

        ByteArrayInputStream buf = new ByteArrayInputStream(randomData);
        Resource resource = resourceService.uploadResource(path, buf);

        buf.reset();
        assertThat(resource.exists())
                .isTrue();
        assertThat(resource.getInputStream())
                .hasSameContentAs(buf);

        resource = resourceService.getResource(path);

        System.out.println(resource.httpUrl());

        buf.reset();
        assertThat(resource.exists())
                .isTrue();
        assertThat(resource.getInputStream())
                .hasSameContentAs(buf);

        resourceService.deleteResource(path);
        assertThat(resource.exists())
                .isFalse();

        String path1 = UUID.randomUUID().toString();
        resourceService.uploadResource(path1, buf);
        String path2 = UUID.randomUUID().toString();
        resourceService.moveResource(path2, path1);
        assertThat(resourceService.getResource(path1).exists())
                .isFalse();
        assertThat(resourceService.getResource(path2).exists())
                .isTrue();
        assertThat(resourceService.getResource(path2).getInputStream())
                .hasSameContentAs(buf);
        resourceService.deleteResource(path2);


    }

    //    @Configuration
    @Import(ResourceSpringConfig.class)
    @PropertySource(name = "resourceLD", value = "classpath:/localResource.properties")
    public static class ResourceServiceTestConfig {

    }

}