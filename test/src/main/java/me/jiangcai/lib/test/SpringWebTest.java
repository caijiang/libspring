package me.jiangcai.lib.test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gargoylesoftware.htmlunit.WebClient;
import me.jiangcai.lib.seext.NumberUtils;
import me.jiangcai.lib.test.page.AbstractPage;
import me.jiangcai.lib.test.why.ParamFilter;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpMethod;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.security.web.context.HttpRequestResponseHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultHandler;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.htmlunit.MockMvcWebClientBuilder;
import org.springframework.test.web.servlet.htmlunit.webdriver.MockMvcHtmlUnitDriverBuilder;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder;
import org.springframework.test.web.servlet.setup.MockMvcConfigurer;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URI;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
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

    private static final Log log = LogFactory.getLog(SpringWebTest.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();
    protected final Random random = new Random();
    private final SecurityContextRepository httpSessionSecurityContextRepository
            = new HttpSessionSecurityContextRepository();
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

    private static <T> Iterable<T> IterableIterator(Iterator<T> iterator) {
        return () -> iterator;
    }

    /**
     * 如果没有激活Spring安全框架 则该方法无效
     *
     * @return 所用MVC请求都将使用该身份；如果为null则不会执行
     * @since 3.0
     */
    protected Authentication autoAuthentication() {
        return null;
    }

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
     * 采用/^1(3|4|5|7|8)\d{9} 结构
     *
     * @return 获取一个随机的手机号码
     */
    protected String randomMobile() {
        String[] p2 = new String[]{
                "3", "4", "5", "7", "8"
        };
        return "1" + p2[random.nextInt(p2.length)] + RandomStringUtils.randomNumeric(9);
//        String p1 = String.valueOf(100000 + random.nextInt(100000));
//        //还有5位 而且必须保证5位
//        String p2 = String.format("%05d", random.nextInt(100000));
//        return p1 + p2;
    }

    /**
     * @return 尽可能唯一的随机字符串
     * @since 2.2
     */
    protected String randomString() {
        return NumberUtils.hash62(UUID.randomUUID());
    }

    /**
     * @param maxLength 最大长度
     * @return 尽可能唯一的随机字符串
     * @since 2.2
     */
    protected String randomString(int maxLength) {
        StringBuilder stringBuilder = new StringBuilder();
        while (true) {
            if (stringBuilder.length() > maxLength) {
                stringBuilder.setLength(maxLength);
                break;
            }
            stringBuilder.append(randomString());
        }
        return stringBuilder.toString();
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
        MockMvcHtmlUnitDriverBuilder mockMvcHtmlUnitDriverBuilder = MockMvcHtmlUnitDriverBuilder
                .mockMvcSetup(mockMvc);
        mockMvcHtmlUnitDriverBuilder = buildWebDriver(mockMvcHtmlUnitDriverBuilder);
        driver = mockMvcHtmlUnitDriverBuilder
                // DIY by interface.
                .build();
    }

    /**
     * 可覆盖以自定义 {@link #webClient}的初始化过程
     * <p>如果 {@link #mockMvc}构造失败该方法就不会被调用</p>
     */
    protected void createWebClient() {
        MockMvcWebClientBuilder mockMvcWebClientBuilder = MockMvcWebClientBuilder
                .mockMvcSetup(mockMvc);
        mockMvcWebClientBuilder = buildWebClient(mockMvcWebClientBuilder);
        webClient = mockMvcWebClientBuilder
                // DIY by interface.
                .build();
    }

    /**
     * 构建{@link #driver}的辅助方法
     *
     * @param mockMvcHtmlUnitDriverBuilder builder
     * @since 3.0
     */
    protected MockMvcHtmlUnitDriverBuilder buildWebDriver(MockMvcHtmlUnitDriverBuilder mockMvcHtmlUnitDriverBuilder) {
        return mockMvcHtmlUnitDriverBuilder;
    }

    /**
     * 构建{@link #webClient}的辅助方法
     *
     * @param mockMvcWebClientBuilder builder
     * @since 3.0
     */
    protected MockMvcWebClientBuilder buildWebClient(MockMvcWebClientBuilder mockMvcWebClientBuilder) {
        return mockMvcWebClientBuilder;
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
        DefaultMockMvcBuilder builder = webAppContextSetup(context).addFilters(new ParamFilter());

        if (springSecurityFilter != null) {
            builder = builder.addFilters(new Filter() {
                @Override
                public void init(FilterConfig filterConfig) throws ServletException {

                }

                @Override
                public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
                    Authentication authentication = autoAuthentication();

                    if (authentication != null) {
                        HttpRequestResponseHolder holder = new HttpRequestResponseHolder((HttpServletRequest) request, (HttpServletResponse) response);
                        SecurityContext context = httpSessionSecurityContextRepository.loadContext(holder);

                        context.setAuthentication(authentication);

                        SecurityContextHolder.getContext().setAuthentication(authentication);

                        httpSessionSecurityContextRepository.saveContext(context, holder.getRequest(), holder.getResponse());
                    }
                    chain.doFilter(request, response);
                }

                @Override
                public void destroy() {

                }
            });
        }

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

    /**
     * 如果遇见302一直执行get
     *
     * @param perform 操作
     * @param session session
     * @return 操作
     * @throws Exception
     * @since 3.0
     */
    protected ResultActions redirectTo(ResultActions perform, MockHttpSession session) throws Exception {
        final MockHttpServletResponse response = perform.andReturn().getResponse();
        if (response.getStatus() == 302) {
            String uri = response.getRedirectedUrl();
            if (session == null)
                return redirectTo(mockMvc.perform(get(uri)), null);
            return redirectTo(mockMvc.perform(get(uri).session(session)), session);
        }
        return perform;
    }

    /**
     * 断言输入json是一个数组,并且结构上跟inputStream类似
     *
     * @param json
     * @param inputStream
     * @throws IOException
     * @since 3.0
     */
    protected void assertSimilarJsonArray(JsonNode json, InputStream inputStream) throws IOException {
        assertThat(json.isArray())
                .isTrue();
        JsonNode mockArray = objectMapper.readTree(inputStream);
        JsonNode mockOne = mockArray.get(0);

        assertSimilarJsonObject(json.get(0), mockOne);
    }

    /**
     * 断言实际json是类似期望json的
     *
     * @param actual
     * @param excepted
     * @since 3.0
     */
    protected void assertSimilarJsonObject(JsonNode actual, JsonNode excepted) {
        assertThat(actual.isObject())
                .isTrue();
        assertThat(actual.fieldNames())
                .containsAll(IterableIterator(excepted.fieldNames()));
    }

    /**
     * @param resource 参考资源
     * @return 应该是一个JSON Array资源
     * @since 3.0
     */
    @SuppressWarnings("unused")
    protected ResultMatcher similarJsonArrayAs(String resource) {
        return result -> {
            Resource resource1 = context.getResource(resource);
            JsonNode actual = objectMapper.readTree(result.getResponse().getContentAsByteArray());
            assertThat(actual.isArray())
                    .isTrue();

            assertSimilarJsonArray(actual, resource1.getInputStream());
        };
    }


    /**
     * @param resource 参考资源
     * @return 跟resource数据相对应的JSON Object
     * @since 3.0
     */
    @SuppressWarnings("unused")
    protected ResultMatcher similarJsonObjectAs(String resource) {
        return result -> {
            Resource resource1 = context.getResource(resource);
            JsonNode actual = objectMapper.readTree(result.getResponse().getContentAsByteArray());
            assertThat(actual.isObject())
                    .isTrue();

            assertSimilarJsonObject(actual, objectMapper.readTree(resource1.getInputStream()));
        };
    }

    /**
     * @param resource Spring资源path
     * @return 结果跟资源的json格式相近
     * @since 3.0
     */
    @SuppressWarnings("unused")
    protected ResultMatcher similarBootstrapDataTable(String resource) {
        return result -> {
            Resource resource1 = context.getResource(resource);
            try (InputStream inputStream = resource1.getInputStream()) {
                JsonNode actual = objectMapper.readTree(result.getResponse().getContentAsByteArray());
                assertThat(actual.get("total").isNumber())
                        .isTrue();
                JsonNode rows = actual.get("rows");
                assertThat(rows.isArray())
                        .isTrue();
                if (rows.size() == 0) {
                    log.warn("响应的rows为空,无法校验");
                    return;
                }
                JsonNode exceptedAll = objectMapper.readTree(inputStream);
                JsonNode excepted = exceptedAll.get("rows").get(0);

                assertSimilarJsonObject(rows.get(0), excepted);
            }
        };
    }

    /**
     * @param resource Spring资源path
     * @return 结果跟资源的json格式相近
     * @since 3.0
     */
    @SuppressWarnings("unused")
    protected ResultMatcher similarJQueryDataTable(String resource) {
        return result -> {
            Resource resource1 = context.getResource(resource);
            try (InputStream inputStream = resource1.getInputStream()) {
                JsonNode actual = objectMapper.readTree(result.getResponse().getContentAsByteArray());
                assertThat(actual.get("recordsTotal").isNumber())
                        .isTrue();
                assertThat(actual.get("recordsFiltered").isNumber())
                        .isTrue();
                assertThat(actual.get("draw").isNumber())
                        .isTrue();

                JsonNode rows = actual.get("data");
                assertThat(rows.isArray())
                        .isTrue();
                if (rows.size() == 0) {
                    log.warn("响应的rows为空,无法校验");
                    return;
                }
                JsonNode exceptedAll = objectMapper.readTree(inputStream);
                JsonNode excepted = exceptedAll.get("data").get(0);

                assertSimilarJsonObject(rows.get(0), excepted);
            }
        };
    }

    /**
     * @param stream 输入流
     * @param <T>    范型
     * @return 随机排序之后的流
     * @since 3.0
     */
    protected <T> Stream<T> randomSort(Stream<T> stream) {
        return stream.sorted(new RandomComparator());
    }

    /**
     * Create a {@link MockHttpServletRequestBuilder} for a GET request.
     *
     * @param urlTemplate  a URL template; the resulting URL will be encoded
     * @param urlVariables zero or more URL variables
     */
    protected MockHttpServletRequestBuilder get(String urlTemplate, Object... urlVariables) {
        return MockMvcRequestBuilders.get(urlTemplate, urlVariables);
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

    /**
     * Create a {@link MockHttpServletRequestBuilder} for a GET request.
     *
     * @param uri the URL
     * @since 4.0.3
     */
    protected MockHttpServletRequestBuilder get(URI uri) {
        return MockMvcRequestBuilders.get(uri);
    }

    /**
     * Create a {@link MockHttpServletRequestBuilder} for a POST request.
     *
     * @param urlTemplate  a URL template; the resulting URL will be encoded
     * @param urlVariables zero or more URL variables
     */
    protected MockHttpServletRequestBuilder post(String urlTemplate, Object... urlVariables) {
        return MockMvcRequestBuilders.post(urlTemplate, urlVariables);
    }

    /**
     * Create a {@link MockHttpServletRequestBuilder} for a POST request.
     *
     * @param uri the URL
     * @since 4.0.3
     */
    protected MockHttpServletRequestBuilder post(URI uri) {
        return MockMvcRequestBuilders.post(uri);
    }

    /**
     * Create a {@link MockHttpServletRequestBuilder} for a PUT request.
     *
     * @param urlTemplate  a URL template; the resulting URL will be encoded
     * @param urlVariables zero or more URL variables
     */
    protected MockHttpServletRequestBuilder put(String urlTemplate, Object... urlVariables) {
        return MockMvcRequestBuilders.put(urlTemplate, urlVariables);
    }

    /**
     * Create a {@link MockHttpServletRequestBuilder} for a PUT request.
     *
     * @param uri the URL
     * @since 4.0.3
     */
    protected MockHttpServletRequestBuilder put(URI uri) {
        return MockMvcRequestBuilders.put(uri);
    }

    /**
     * Create a {@link MockHttpServletRequestBuilder} for a PATCH request.
     *
     * @param urlTemplate  a URL template; the resulting URL will be encoded
     * @param urlVariables zero or more URL variables
     */
    protected MockHttpServletRequestBuilder patch(String urlTemplate, Object... urlVariables) {
        return MockMvcRequestBuilders.patch(urlTemplate, urlVariables);
    }

    /**
     * Create a {@link MockHttpServletRequestBuilder} for a PATCH request.
     *
     * @param uri the URL
     * @since 4.0.3
     */
    protected MockHttpServletRequestBuilder patch(URI uri) {
        return MockMvcRequestBuilders.patch(uri);
    }

    /**
     * Create a {@link MockHttpServletRequestBuilder} for a DELETE request.
     *
     * @param urlTemplate  a URL template; the resulting URL will be encoded
     * @param urlVariables zero or more URL variables
     */
    protected MockHttpServletRequestBuilder delete(String urlTemplate, Object... urlVariables) {
        return MockMvcRequestBuilders.delete(urlTemplate, urlVariables);
    }

    /**
     * Create a {@link MockHttpServletRequestBuilder} for a DELETE request.
     *
     * @param uri the URL
     * @since 4.0.3
     */
    protected MockHttpServletRequestBuilder delete(URI uri) {
        return MockMvcRequestBuilders.delete(uri);
    }

    /**
     * Create a {@link MockHttpServletRequestBuilder} for an OPTIONS request.
     *
     * @param urlTemplate  a URL template; the resulting URL will be encoded
     * @param urlVariables zero or more URL variables
     */
    protected MockHttpServletRequestBuilder options(String urlTemplate, Object... urlVariables) {
        return MockMvcRequestBuilders.options(urlTemplate, urlVariables);
    }

    /**
     * Create a {@link MockHttpServletRequestBuilder} for an OPTIONS request.
     *
     * @param uri the URL
     * @since 4.0.3
     */
    protected MockHttpServletRequestBuilder options(URI uri) {
        return MockMvcRequestBuilders.options(uri);
    }

    /**
     * Create a {@link MockHttpServletRequestBuilder} for a HEAD request.
     *
     * @param urlTemplate  a URL template; the resulting URL will be encoded
     * @param urlVariables zero or more URL variables
     * @since 4.1
     */
    protected MockHttpServletRequestBuilder head(String urlTemplate, Object... urlVariables) {
        return MockMvcRequestBuilders.head(urlTemplate, urlVariables);
    }

    /**
     * Create a {@link MockHttpServletRequestBuilder} for a HEAD request.
     *
     * @param uri the URL
     * @since 4.1
     */
    protected MockHttpServletRequestBuilder head(URI uri) {
        return MockMvcRequestBuilders.head(uri);
    }

    /**
     * Create a {@link MockHttpServletRequestBuilder} for a request with the given HTTP method.
     *
     * @param httpMethod   the HTTP method
     * @param urlTemplate  a URL template; the resulting URL will be encoded
     * @param urlVariables zero or more URL variables
     */
    protected MockHttpServletRequestBuilder request(HttpMethod httpMethod, String urlTemplate, Object... urlVariables) {
        return MockMvcRequestBuilders.request(httpMethod, urlTemplate, urlVariables);
    }

    /**
     * Create a {@link MockHttpServletRequestBuilder} for a request with the given HTTP method.
     *
     * @param httpMethod the HTTP method (GET, POST, etc)
     * @param uri        the URL
     * @since 4.0.3
     */
    protected MockHttpServletRequestBuilder request(HttpMethod httpMethod, URI uri) {
        return MockMvcRequestBuilders.request(httpMethod, uri);
    }

    /**
     * Create a {@link MockMultipartHttpServletRequestBuilder} for a multipart request.
     *
     * @param urlTemplate  a URL template; the resulting URL will be encoded
     * @param urlVariables zero or more URL variables
     */
    protected MockMultipartHttpServletRequestBuilder fileUpload(String urlTemplate, Object... urlVariables) {
        return MockMvcRequestBuilders.fileUpload(urlTemplate, urlVariables);
    }

    /**
     * Create a {@link MockMultipartHttpServletRequestBuilder} for a multipart request.
     *
     * @param uri the URL
     * @since 4.0.3
     */
    protected MockMultipartHttpServletRequestBuilder fileUpload(URI uri) {
        return MockMvcRequestBuilders.fileUpload(uri);
    }

    /**
     * Print {@link MvcResult} details to the "standard" output stream.
     *
     * @see System#out
     */
    protected ResultHandler print() {
        return MockMvcResultHandlers.print();
    }

    /**
     * 随机排序器
     *
     * @since 3.0
     */
    public static class RandomComparator implements Comparator<Object> {
        static Random random = new Random();

        @Override
        public int compare(Object o1, Object o2) {
            return random.nextInt();
        }
    }

}
