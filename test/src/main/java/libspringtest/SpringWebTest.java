package libspringtest;

import org.springframework.test.context.ContextConfiguration;

/**
 * Created by luffy on 2015/5/15.
 * <p>Spring web测试基类</p> *
 * <p>典型测试如下</p>
 * <pre class="code">
 * <code>@</code>ActiveProfiles("test")
 * <code>@</code>ContextConfiguration(loader = Abc.class)
 * 实际上发现这样Load好像不太靠谱 老老实实一个一个Load吧……
 * <code>@</code>RunWith(SpringJUnit4ClassRunner.class)
 * </pre>
 * <p>1.3 fixed bug</p>
 *
 * @author luffy luffy.ja at gmail.com
 * @see ContextConfiguration
 * @deprecated replaced with {@link org.luffy.test.SpringWebTest}
 */
public class SpringWebTest extends org.luffy.test.SpringWebTest {

}
