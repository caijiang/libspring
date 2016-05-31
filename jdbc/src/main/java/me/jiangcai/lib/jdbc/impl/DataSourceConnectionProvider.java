package me.jiangcai.lib.jdbc.impl;

import me.jiangcai.lib.jdbc.CloseableConnectionProvider;
import org.springframework.jdbc.datasource.DataSourceUtils;

import javax.sql.DataSource;
import java.sql.Connection;

/**
 * @author CJ
 */
class DataSourceConnectionProvider extends AbstractConnectionProvider implements CloseableConnectionProvider {

    private final DataSource dataSource;

    DataSourceConnectionProvider(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Connection getConnection() {
        return DataSourceUtils.getConnection(dataSource);
    }

    @Override
    public void close(Connection connection) {
        DataSourceUtils.releaseConnection(connection, dataSource);
    }
}
