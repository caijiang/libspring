package me.jiangcai.lib.jdbc;

import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author CJ
 */
public class DataSourceConfig {

    @Bean
    public DataSource dataSource() throws IOException {

        Path h2 = Paths.get("target","h2test.h2.db");
        Path trace = Paths.get("target","h2test.trace.db");

        Files.deleteIfExists(h2);
        Files.deleteIfExists(trace);

        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.h2.Driver");
        dataSource.setUrl("jdbc:h2:target/h2test");
        return dataSource;
    }

}
