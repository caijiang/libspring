/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.jiangcai.lib.spring.loader;

import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.web.context.AbstractContextLoaderInitializer;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;
import javax.servlet.ServletRegistration;
import java.util.EnumSet;

/**
 * 多Dispatcher的载入器
 *
 * @author luffy
 */
public abstract class MutliDispatcherLoader extends AbstractContextLoaderInitializer {

    /**
     * @param servletContext
     * @param servletName
     * @param configClasses
     * @param startup        启动序号
     * @param mappings       url比如/*
     * @param async          一般都是true
     * @param filters        不同的servlet必须为不同的filer
     */
    protected void registerDispatcherServlet(ServletContext servletContext, String servletName, Class[] configClasses, int startup, String[] mappings, boolean async, Filter[] filters) {
        Assert.hasLength(servletName, "getServletName() may not return empty or null");

        WebApplicationContext servletAppContext = createServletApplicationContext(configClasses);
        Assert.notNull(servletAppContext,
                "createServletApplicationContext() did not return an application "
                        + "context for servlet [" + servletName + "]");

        DispatcherServlet dispatcherServlet = new DispatcherServlet(servletAppContext);
        ServletRegistration.Dynamic registration = servletContext.addServlet(servletName, dispatcherServlet);
        Assert.notNull(registration,
                "Failed to register servlet with name '" + servletName + "'."
                        + "Check if there is another servlet registered under the same name.");

        registration.setLoadOnStartup(startup);
        registration.addMapping(mappings);
        registration.setAsyncSupported(async);

        if (!ObjectUtils.isEmpty(filters)) {
            for (Filter filter : filters) {
                registerServletFilter(servletContext, filter, async, servletName);
            }
        }

        customizeRegistration(registration);
    }

    private EnumSet<DispatcherType> getDispatcherTypes(boolean async) {
        return async
                ? EnumSet.of(DispatcherType.REQUEST, DispatcherType.FORWARD, DispatcherType.INCLUDE, DispatcherType.ASYNC)
                : EnumSet.of(DispatcherType.REQUEST, DispatcherType.FORWARD, DispatcherType.INCLUDE);
    }

    protected FilterRegistration.Dynamic registerServletFilter(ServletContext servletContext, Filter filter, boolean async, String servletName) {
//        String filterName = servletName+Conventions.getVariableName(filter);
        String filterName = servletName + "Filter";
        FilterRegistration.Dynamic registration = servletContext.addFilter(filterName, filter);
        registration.setAsyncSupported(async);
        registration.addMappingForServletNames(getDispatcherTypes(async), false, servletName);
        return registration;
    }

    protected WebApplicationContext createServletApplicationContext(Class[] configClasses) {
        AnnotationConfigWebApplicationContext servletAppContext = new AnnotationConfigWebApplicationContext();
        if (!ObjectUtils.isEmpty(configClasses)) {
            servletAppContext.register(configClasses);
        }
        return servletAppContext;
    }

    protected void customizeRegistration(ServletRegistration.Dynamic registration) {
    }

}
