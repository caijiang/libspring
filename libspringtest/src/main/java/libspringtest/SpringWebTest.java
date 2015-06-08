package libspringtest;

import org.junit.Before;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

/**
 * Created by luffy on 2015/5/15.
 * <p>Spring web测试基类</p> *
 * <p>典型测试如下</p>
 * <pre class="code">
 *     <code>@</code>ActiveProfiles("test")
 *     <code>@</code>ContextConfiguration(loader = Abc.class)
 *     实际上发现这样Load好像不太靠谱 老老实实一个一个Load吧……
 *     <code>@</code>RunWith(SpringJUnit4ClassRunner.class)
 * </pre>
 * <p>1.3 fixed bug</p>
 * @see SpringContextLoader
 * @see ContextConfiguration
 * @author luffy luffy.ja at gmail.com
 */
//@WebAppConfiguration
//@ActiveProfiles("test")
//@RunWith(SpringJUnit4ClassRunner.class)
public class SpringWebTest {

    /**
     * 自动注入应用程序上下文
     * **/
    @Inject
    protected WebApplicationContext context;
    /**
     * 自动注入servlet上下文
     * **/
    @Inject
    protected ServletContext servletContext;
    /**
     * 选配 只有在SecurityConfig起作用的情况下
     * **/
    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired(required = false)
    private FilterChainProxy springSecurityFilter;

    /**
     * mock请求
     * **/
    @Autowired
    protected MockHttpServletRequest request;

    /**
     * mockMvc等待初始化
     * **/
    protected MockMvc mockMvc;

    @Before
    public void creatMockMVC() {
        MockitoAnnotations.initMocks(this);
        if(springSecurityFilter!=null)
            mockMvc = webAppContextSetup(context)
                .addFilters(springSecurityFilter)
                .build();
        else
            mockMvc = webAppContextSetup(context)
                    .build();
    }

    /**
     * 保存登陆过以后的信息
     * **/
    protected void saveAuthedSession(HttpSession session){
        SecurityContext securityContext = (SecurityContext)   session.getAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY);

        if (securityContext==null)
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
