package org.luffy.lib.libspring.loader;

import org.luffy.lib.libspring.config.LibJpaConfig;
import org.luffy.lib.libspring.config.LibMVCConfig;
import org.luffy.lib.libspring.config.RuntimeConfig;
import org.luffy.lib.libspring.config.LibSecurityConfig;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.filter.HiddenHttpMethodFilter;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

import javax.servlet.Filter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * <p></p>
 * Created by CJ on 5/12/15.
 *
 * 1.3添加过滤器
 *
 * @deprecated since 2.0 not supported
 * @author CJ
 */
@Deprecated
public abstract class SimpleMVCLoader extends AbstractAnnotationConfigDispatcherServletInitializer {

    protected abstract boolean loadDefaultMVC();

    /**
     * @return 自定义SecurityConfig类 可以为空 如果不提供安全支持
     * */
    protected abstract Class<? extends LibSecurityConfig>securityConfig();

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
        list.add(LibJpaConfig.class);
        list.add(runtimeConfig());
        Class sc = securityConfig();
        if (sc!=null)
            list.add(sc);
        if (loadDefaultMVC())
            list.add(LibMVCConfig.class);
        return list.toArray(new Class[list.size()]);
    }

    @Override
    protected Filter[] getServletFilters() {
        return new Filter[] { new HiddenHttpMethodFilter(), new CharacterEncodingFilter() };
    }

}
