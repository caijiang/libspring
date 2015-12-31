package libspringtest;

import com.gargoylesoftware.htmlunit.WebClient;
import org.junit.After;
import org.junit.Before;
import org.mockito.MockitoAnnotations;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.htmlunit.MockMvcWebClientBuilder;
import org.springframework.test.web.servlet.htmlunit.webdriver.MockMvcHtmlUnitDriverBuilder;
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder;
import org.springframework.test.web.servlet.setup.MockMvcConfigurer;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import java.util.Random;

import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

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
 * @see SpringContextLoader
 * @see ContextConfiguration
 */
//@WebAppConfiguration
//@ActiveProfiles("test")
//@RunWith(SpringJUnit4ClassRunner.class)
public class SpringWebTest {

    /**
     * 自动注入应用程序上下文
     **/
    @Autowired(required = false)
    protected WebApplicationContext context;
    /**
     * 自动注入servlet上下文
     **/
    @Autowired(required = false)
    protected ServletContext servletContext;
    /**
     * 选配 只有在SecurityConfig起作用的情况下
     **/
    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired(required = false)
    protected FilterChainProxy springSecurityFilter;

    @Autowired(required = false)
    protected MockMvcConfigurer mockMvcConfigurer;
    /**
     * mock请求
     **/
    @Autowired
    protected MockHttpServletRequest request;

    /**
     * mockMvc等待初始化
     **/
    protected MockMvc mockMvc;
    protected WebClient webClient;
    protected WebDriver driver;

    protected final Random random = new Random();

    /**
     * <p>位数不足无法保证其唯一性,需要客户端代码自行校验唯一性.</p>
     * <p>具体的区间是10000000000-19999999999</p>
     *
     * @return 获取一个随机的手机号码
     */
    protected String randomMobile() {
        String p1 = String.valueOf(100000 + random.nextInt(100000));
        //还有5位 而且必须保证5位
        String p2 = String.format("%05d", random.nextInt(100000));
        return p1 + p2;
    }

    /**
     * 准备测试环境所需的各个字段
     */
    @Before
    public void prepareFields() {

        createMockMVC();

        // 现在创建其他
        webClient = MockMvcWebClientBuilder
                .mockMvcSetup(mockMvc)
                // DIY by interface.
                .build();

        driver = MockMvcHtmlUnitDriverBuilder
                .mockMvcSetup(mockMvc)
                // DIY by interface.
                .build();

    }

    /**
     * 创建{@link #mockMvc}
     */
    public void createMockMVC() {
        MockitoAnnotations.initMocks(this);
        DefaultMockMvcBuilder builder = webAppContextSetup(context);
        if (springSecurityFilter != null) {
            builder = builder.addFilters(springSecurityFilter);
        }

        if (mockMvcConfigurer != null) {
            builder = builder.apply(mockMvcConfigurer);
        }
        mockMvc = builder.build();
    }

    @After
    public void afterTest() {
        if (driver != null) {
            driver.close();
        }
    }

    /**
     * 保存登陆过以后的信息
     **/
    protected void saveAuthedSession(HttpSession session) {
        SecurityContext securityContext = (SecurityContext) session.getAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY);

        if (securityContext == null)
            throw new IllegalStateException("尚未登录");

        request.setSession(session);

        // context 不为空 表示成功登陆
        SecurityContextHolder.setContext(securityContext);
    }


// just for store
//    protected MockHttpSession loginAs(String userName,String password) throws Exception{
//        MockHttpSession session = (MockHttpSession) this.mockMvc.perform(get("/api/user"))
////                 .andDo(print())
////                 .andExpect(status().isFound())
////                 .andExpect(redirectedUrl("http://localhost/loginPage"))
//                .andReturn().getRequest().getSession(true);
////         Redirected URL = http://localhost/login
//
//        //bad password
//        session  = (MockHttpSession) this.mockMvc.perform(post("/login").session(session)
//                .param("username", userName).param("password", password))
//                .andDo(print())
////                 .andExpect(status().isFound())
////                 .andExpect(redirectedUrl("/loginPage?error"))
////                 .andExpect(status().isUnauthorized())
//                .andReturn().getRequest().getSession();
////                 ;
//
////         CsrfToken token = new HttpSessionCsrfTokenRepository().loadToken(request);
//
//        saveAuth(session);
//
////         this.mockMvc.perform(get("/api/user").session(session))
//////                 .andDo(print())
////                 .andExpect(status().isOk());
//        return session;
//    }

}
