package me.jiangcai.crud;

import me.jiangcai.lib.test.SpringWebTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;

/**
 * @author CJ
 */
@ContextConfiguration(classes = {CrudConfig.class, BaseTestConfig.class})
@WebAppConfiguration
public abstract class BaseTest extends SpringWebTest {
}
