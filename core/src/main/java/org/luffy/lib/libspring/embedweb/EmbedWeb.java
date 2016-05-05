package org.luffy.lib.libspring.embedweb;

import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 实现该接口,表示可以作为一个EWP
 *
 * @author CJ
 */
public interface EmbedWeb {

    /**
     * 每一个EWP必须拥有一个唯一的名字
     *
     * @return EWP 名称
     */
    String name();

    /**
     * 默认 /web/private
     *
     * @return 服务端可用资源的访问路径
     */
    default String privateResourcePath() {
        return "/web/private";
    }

    /**
     * 默认 /web/public
     *
     * @return 客户端可用资源的访问路径
     */
    default String publicResourcePath() {
        return "/web/public";
    }

    /**
     * 只有版本迭代以后web application才会重新组装
     *
     * @return 这个EWP的版本
     */
    default String version() {
        Class clazz = getClass();
        String version = clazz.getPackage().getImplementationVersion();
        if (version == null && (clazz.getProtectionDomain() != null
                && clazz.getProtectionDomain().getCodeSource() != null)) {
            URL codeLocation = clazz.getProtectionDomain().getCodeSource()
                    .getLocation();
            // service-1.3-SNAPSHOT.jar
            Pattern pattern = Pattern.compile(".*-(.+)\\.jar.*");
            Matcher matcher = pattern.matcher(codeLocation.toString());
            if (matcher.matches()) {
                version = matcher.group(1);
            }
        }

        return version == null ? "unknown" : version;
    }

}
