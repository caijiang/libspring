package me.jiangcai.lib.test.config;

import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 内置数据库的配置
 *
 * @author CJ
 */
public class H2DataSourceConfig {

    public DataSource dataSource(String name) throws IOException {
        Path h2 = Paths.get("target", name + ".h2.db");
        Path trace = Paths.get("target", name + ".trace.db");

        Files.deleteIfExists(h2);
        Files.deleteIfExists(trace);

        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.h2.Driver");
        dataSource.setUrl("jdbc:h2:target/" + name);
        return dataSource;
    }

}
