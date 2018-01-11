package me.jiangcai.lib.test.config;

import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.io.IOException;

/**
 * 内置数据库的配置
 *
 * @author CJ
 */
public class H2DataSourceConfig {

    /**
     * @param name 名字
     * @return 以文件保存的数据源
     * @since 3.0
     */
    public DataSource fileDataSource(String name) {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.h2.Driver");
        dataSource.setUrl("jdbc:h2:target/" + name);
        return dataSource;
    }

    /**
     * @param name 名字
     * @return 内存形式的数据源
     * @since 3.0
     */
    public DataSource memDataSource(String name) {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.h2.Driver");
        dataSource.setUrl("jdbc:h2:mem:" + name + ";DB_CLOSE_DELAY=-1");
        return dataSource;
    }

    /**
     *
     * @param name 名字
     * @param mode 兼容模式 该值可以为：DB2、Derby、HSQLDB、MSSQLServer、MySQL、Oracle、PostgreSQL
     * @return
     */
    public DataSource memDataSource(String name,String mode){
        if(StringUtils.isEmpty(mode)){
            return memDataSource(name);
        }
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.h2.Driver");
        dataSource.setUrl("jdbc:h2:mem:" + name + ";DB_CLOSE_DELAY=-1;MODE=" + mode);
        return dataSource;
    }


    /**
     * @param name 名字
     * @return 数据源
     * @throws IOException never
     * @deprecated 请使用 {@link #fileDataSource(String)} 代替
     */
    public DataSource dataSource(String name) throws IOException {
        return fileDataSource(name);
    }

}
