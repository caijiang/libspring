package me.jiangcai.lib.git;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

//<plugin>
//        <groupId>pl.project13.maven</groupId>
//        <artifactId>git-commit-id-plugin</artifactId>
//        <version>2.2.4</version>
//        <executions>
//        <execution>
//        <id>get-the-git-infos</id>
//        <goals>
//        <goal>revision</goal>
//        </goals>
//        </execution>
//        </executions>
//        <configuration>
//        <generateGitPropertiesFile>true</generateGitPropertiesFile>
//        <generateGitPropertiesFilename>${project.build.outputDirectory}/git.properties</generateGitPropertiesFilename>
//        </configuration>
//        </plugin>

/**
 * 添加插件如下即可，文档不好写，直接下载源码看下即可
 *
 * @author CJ
 */
@Configuration
@PropertySource("classpath:/git.properties")
@ComponentScan("me.jiangcai.lib.git.bean")
public class GitSpringConfig {
}
