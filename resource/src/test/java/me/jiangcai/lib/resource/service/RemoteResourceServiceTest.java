package me.jiangcai.lib.resource.service;

import me.jiangcai.lib.resource.ResourceSpringConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.ContextConfiguration;

/**
 * @author CJ
 */
@ContextConfiguration(classes = {RemoteResourceServiceTest.RemoteResourceServiceTestConfig.class})
public class RemoteResourceServiceTest extends ResourceServiceTest {

//    @Configuration
    @Import(ResourceSpringConfig.class)
    @PropertySource(name = "resourceLD",value = "classpath:/remoteResource.properties")
    public static class RemoteResourceServiceTestConfig {

    }

}