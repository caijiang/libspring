package libspringtest;

import org.luffy.lib.libspring.config.LibJpaConfig;
import org.luffy.lib.libspring.config.LibMVCConfig;
import org.luffy.lib.libspring.config.LibSecurityConfig;
import org.luffy.lib.libspring.config.RuntimeConfig;
import org.springframework.beans.factory.support.BeanDefinitionReader;
import org.springframework.context.annotation.AnnotatedBeanDefinitionReader;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.test.context.ContextConfigurationAttributes;
import org.springframework.test.context.MergedContextConfiguration;
import org.springframework.test.context.support.AbstractGenericContextLoader;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by luffy on 2015/5/15.
 * <p>需要作为ContextLoader参数放置在 {@link org.springframework.test.context.ContextConfiguration}中 比如：</p>
 *
 * <pre class="code">
 *     <code>@</code>ContextConfiguration(loader = Abc.class)
 *     <code>@</code>RunWith(SpringJUnit4ClassRunner.class)
 * </pre>
 * @author luffy luffy.ja at gmail.com
 */
public abstract class SpringContextLoader extends AbstractGenericContextLoader {

    /**
     * 载入默认MVC
     * **/
    protected abstract boolean loadDefaultMVC();

    protected abstract Class<? extends LibSecurityConfig> securityConfig();

    protected abstract Class<? extends RuntimeConfig> runtimeConfig();

    protected abstract Class<?>[] getCoreRootConfigClasses();

    @Override
    protected void loadBeanDefinitions(GenericApplicationContext context, MergedContextConfiguration mergedConfig) {
        Class<?>[] annotatedClasses = mergedConfig.getClasses();
        if (logger.isDebugEnabled()) {
            logger.debug("Registering annotated classes: " + ObjectUtils.nullSafeToString(annotatedClasses));
        }
        new AnnotatedBeanDefinitionReader(context).register(annotatedClasses);
    }

    @Override
    protected BeanDefinitionReader createBeanDefinitionReader(GenericApplicationContext context) {
        throw new UnsupportedOperationException(
                "AnnotationConfigContextLoader does not support the createBeanDefinitionReader(GenericApplicationContext) method");
    }

    @Override
    protected String getResourceSuffix() {
        throw new UnsupportedOperationException(
                "AnnotationConfigContextLoader does not support the getResourceSuffix() method");
    }

    @Override
    public void processContextConfiguration(ContextConfigurationAttributes configAttributes) {
        if (!configAttributes.hasClasses() && isGenerateDefaultLocations()) {
            configAttributes.setClasses(detectDefaultConfigurationClasses(configAttributes.getDeclaringClass()));
        }
    }

    //    @Override
    protected Class<?>[] detectDefaultConfigurationClasses(Class<?> declaringClass) {
//        ArrayList<Class<?>> list = new ArrayList(Arrays.asList(super.detectDefaultConfigurationClasses(declaringClass)));
        ArrayList<Class<?>> list  = new ArrayList(Arrays.asList(getCoreRootConfigClasses()));

        list.add(LibJpaConfig.class);
        list.add(runtimeConfig());
        Class securityClass = securityConfig();
        if (securityClass!=null)
            list.add(securityClass);
        if (loadDefaultMVC()){
            list.add(LibMVCConfig.class);
        }


        return list.toArray(new Class[list.size()]);
    }
}