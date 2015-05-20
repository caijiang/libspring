package com.luffy.lib.libspring.demo;

import com.luffy.lib.libspring.demo.config.CoreConfig;
import com.luffy.lib.libspring.demo.config.MyRuntimeConfig;
import com.luffy.lib.libspring.demo.config.MyLibSecurityConfig;
import com.luffy.lib.libspring.demo.entity.User;
import com.luffy.lib.libspring.demo.repository.UserRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.luffy.lib.libspring.config.LibJpaConfig;
import org.luffy.lib.libspring.config.LibMVCConfig;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.*;

/**
 * Created by luffy on 2015/5/20.
 *
 * @author luffy luffy.ja at gmail.com
 */
@ActiveProfiles("test")
@ContextConfiguration(classes = {CoreConfig.class, TestConfig.class, MyLibSecurityConfig.class, LibJpaConfig.class, LibMVCConfig.class})
@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
public class DemoTest {

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
    @Autowired
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
        mockMvc = webAppContextSetup(context)
                .addFilters(springSecurityFilter)
                .build();
    }

    /**
     * 保存登陆过以后的信息
     * **/
    protected void saveAuth(HttpSession session){
        request.setSession(session);
        SecurityContext securityContext = (SecurityContext)   session.getAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY);
        // context 不为空 表示成功登陆
        SecurityContextHolder.setContext(securityContext);
    }

    protected MockHttpSession loginAs(String userName,String password) throws Exception{
        MockHttpSession session = (MockHttpSession) this.mockMvc.perform(get("/api/user"))
//                 .andDo(print())
//                 .andExpect(status().isFound())
//                 .andExpect(redirectedUrl("http://localhost/loginPage"))
                .andReturn().getRequest().getSession(true);
//         Redirected URL = http://localhost/login

        //bad password
        session  = (MockHttpSession) this.mockMvc.perform(post("/login").session(session)
                .param("username", userName).param("password", password))
                .andDo(print())
//                 .andExpect(status().isFound())
//                 .andExpect(redirectedUrl("/loginPage?error"))
//                 .andExpect(status().isUnauthorized())
                .andReturn().getRequest().getSession();
//                 ;

//         CsrfToken token = new HttpSessionCsrfTokenRepository().loadToken(request);

        saveAuth(session);

//         this.mockMvc.perform(get("/api/user").session(session))
////                 .andDo(print())
//                 .andExpect(status().isOk());
        return session;
    }

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @After
    public void removeDemo(){
        User demo =userRepository.findByLoginName("demo");
        if (demo!=null)
            userRepository.delete(demo);
    }

    @Test
    @Rollback
    public void justtest() throws Exception {
        removeDemo();

        User u = new User();
        u.setLoginName("demo");
        u.setPassword(passwordEncoder.encode("demo"));

        userRepository.save(u);

        MockHttpSession session = loginAs("demo","demo");

        mockMvc.perform(get("/endpoints")
        .session(session))
                .andDo(print())
        .andExpect(status().isOk())
        .andExpect(view().name("inner.endpoints"))
        ;
    }
}