package org.luffy.lib.libspring.loader;

import org.luffy.lib.libspring.config.JpaConfig;
import org.luffy.lib.libspring.config.RuntimeConfig;
import org.luffy.lib.libspring.config.SecurityConfig;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * <p></p>
 * Created by CJ on 5/12/15.
 *
 * @author CJ
 */
public abstract class SimpleMVCLoader extends AbstractAnnotationConfigDispatcherServletInitializer {

    /**
     * @return 自定义SecurityConfig类 可以为空 如果不提供安全支持
     * */
    protected abstract Class<? extends SecurityConfig>securityConfig();

    /**
     * @return 自定义RuntimeConfig类 不可为空！
     * */
    protected abstract Class<? extends RuntimeConfig>runtimeConfig();

    /**
     * @see #getRootConfigClasses()
     * */
    protected abstract Class<?>[] getCoreRootConfigClasses();

    protected Class<?>[] getRootConfigClasses() {
        List<Class> list = new ArrayList(Arrays.asList(getCoreRootConfigClasses()));
        list.add(JpaConfig.class);
        list.add(runtimeConfig());
        Class sc = securityConfig();
        if (sc!=null)
            list.add(sc);
        return list.toArray(new Class[list.size()]);
    }

}
