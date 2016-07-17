package me.jiangcai.lib.test;

import com.gargoylesoftware.htmlunit.WebClient;
import me.jiangcai.lib.test.page.AbstractPage;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.htmlunit.MockMvcWebClientBuilder;
import org.springframework.test.web.servlet.htmlunit.webdriver.MockMvcHtmlUnitDriverBuilder;
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder;
import org.springframework.test.web.servlet.setup.MockMvcConfigurer;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.util.Arrays;
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
 * @see ContextConfiguration
 * @since 2.0
 */
//@WebAppConfiguration
@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
public class SpringWebTest {

    protected final Random random = new Random();
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
    @Autowired(required = false)
    protected MockHttpServletRequest request;
    /**
     * mockMvc等待初始化
     **/
    protected MockMvc mockMvc;
    protected WebClient webClient;
    protected WebDriver driver;

    /**
     * 初始化逻辑页面
     * <p>会首先{@link AbstractPage#validatePage() 验证}该页面</p>
     *
     * @param clazz 该页面相对应的逻辑页面的类
     * @param <T>   该页面相对应的逻辑页面
     * @return 页面实例
     */
    public <T extends AbstractPage> T initPage(Class<T> clazz) {
        T page = PageFactory.initElements(driver, clazz);
//        page.setResourceService(resourceService);
        page.setTestInstance(this);
        page.validatePage();
        return page;
    }

    /**
     * @return 获取随机http url
     */
    protected String randomHttpURL() {
        StringBuilder stringBuilder = new StringBuilder("http://");
        stringBuilder.append(RandomStringUtils.randomAlphabetic(2 + random.nextInt(4)));
        stringBuilder.append(".");
        stringBuilder.append(RandomStringUtils.randomAlphabetic(2 + random.nextInt(4)));
        if (random.nextBoolean()) {
            stringBuilder.append(".");
            stringBuilder.append(RandomStringUtils.randomAlphabetic(2 + random.nextInt(4)));
        }
        return stringBuilder.toString();
    }

    /**
     * @return 获取随机email地址
     */
    protected String randomEmailAddress() {
        return RandomStringUtils.randomAlphabetic(random.nextInt(5) + 3)
                + "@"
                + RandomStringUtils.randomAlphabetic(random.nextInt(5) + 3)
                + "."
                + RandomStringUtils.randomAlphabetic(random.nextInt(2) + 2);
    }

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
     * 随机一个数字
     *
     * @param min       最小边界
     * @param max       最大边界
     * @param precision 精度
     * @return max&gt;随机小数&gt;min
     */
    protected double randomDouble(double min, double max, int precision) {
        double value = max - min;
        if (value <= 0)
            throw new IllegalArgumentException("max should great than min");
        value = random.nextDouble() * value;
        return new BigDecimal(value + min).setScale(precision, BigDecimal.ROUND_HALF_DOWN).doubleValue();
    }

    /**
     * 随机抓取一个数组
     *
     * @param origin    原始数组
     * @param minLength 最小宽度
     * @param <T>       该数组的数据类型
     * @return 随机数组
     */
    protected <T> T[] randomArray(T[] origin, int minLength) {
        //先生成结果数据索引表
        int length = random.nextInt(origin.length - minLength) + minLength;
        T[] newArray = Arrays.copyOf(origin, length);
        // 抓去唯一随机的结果
        int wheel = -1;
//        System.out.println("do random array");
        for (int i = 0; i < newArray.length; i++) {
            //最少要留下 length-i-1 个结果
            int seed = random.nextInt(origin.length - wheel - (newArray.length - i) - 1);
            wheel = wheel + seed + 1;
            newArray[i] = origin[wheel];
//            System.out.println(wheel);
            //  1-(-1)-1 = 0
            // 2 2
            // i:0 2-(-1)-2 -1 = 0 w:-1+0+1 = 0
            // i:1 2-0-1-1 = 0     w:0+0+1 = 1
            // 2 1
            // i:0 2-(-1)-1-1 = 1  w:-1+(0|1)+1 = 0|1
            // 3 2
            // i:0 3-(-1)-2-1= 1  w:-1+(0|1)+1 = 0|1
            // if 0
            // i:1 3-0-1-1=1  w:0+(0|1)+1 = 1|2
            // if 1
            // i:1 3-1-1-1=0 w:1+0+1 = 2
        }
        return newArray;
    }

    /**
     * 准备测试环境所需的各个字段
     */
    @Before
    public void prepareFields() {

        createMockMVC();

        if (mockMvc == null)
            return;

        // 现在创建其他
        createWebClient();

        createWebDriver();
    }

    /**
     * 可覆盖以自定义 {@link #driver}的初始化过程
     * <p>如果 {@link #mockMvc}构造失败该方法就不会被调用</p>
     */
    protected void createWebDriver() {
        driver = MockMvcHtmlUnitDriverBuilder
                .mockMvcSetup(mockMvc)
                // DIY by interface.
                .build();

    }

    /**
     * 可覆盖以自定义 {@link #webClient}的初始化过程
     * <p>如果 {@link #mockMvc}构造失败该方法就不会被调用</p>
     */
    protected void createWebClient() {
        webClient = MockMvcWebClientBuilder
                .mockMvcSetup(mockMvc)
                // DIY by interface.
                .build();
    }

    /**
     * 构建{@link #mockMvc}的辅助方法
     *
     * @param builder builder
     * @return new Builder
     * @since 2.2
     */
    protected DefaultMockMvcBuilder buildMockMVC(DefaultMockMvcBuilder builder) {
        return builder;
    }

    /**
     * 创建{@link #mockMvc}
     * <span>2.2以后需要添加更多的filter或者其他什么的可以覆盖{@link #buildMockMVC(DefaultMockMvcBuilder)}不用再重新实现一次这个
     * 方法了。</span>
     */
    public void createMockMVC() {
        MockitoAnnotations.initMocks(this);
        // ignore it, so it works in no-web fine.
        if (context == null)
            return;
        DefaultMockMvcBuilder builder = webAppContextSetup(context);
        builder = buildMockMVC(builder);
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
