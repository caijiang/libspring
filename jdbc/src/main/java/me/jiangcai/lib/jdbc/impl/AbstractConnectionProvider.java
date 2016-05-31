package me.jiangcai.lib.jdbc.impl;

import me.jiangcai.lib.jdbc.CloseableConnectionProvider;
import me.jiangcai.lib.jdbc.ConnectionProvider;
import org.eclipse.persistence.platform.database.DatabasePlatform;
import org.eclipse.persistence.platform.database.H2Platform;
import org.eclipse.persistence.platform.database.MySQLPlatform;
import org.eclipse.persistence.platform.database.SQLServerPlatform;
import org.springframework.jdbc.datasource.SmartDataSource;
import org.springframework.jdbc.support.JdbcUtils;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

/**
 * @author CJ
 */
abstract class AbstractConnectionProvider implements ConnectionProvider {


    @Override
    public DatabasePlatform profile() throws SQLException {
        Connection connection = getConnection();
        try {
            String databaseName = JdbcUtils.commonDatabaseName(connection.getMetaData().getDatabaseProductName());
            if (databaseName.equalsIgnoreCase("MySQL")) {
                return new MySQLPlatform();
            } else if (databaseName.equalsIgnoreCase("Microsoft SQL Server")) {
                return new SQLServerPlatform();
            } else if (databaseName.equalsIgnoreCase("H2")) {
                return new H2Platform();
            } else {
                throw new InternalError("unsupported Database " + databaseName);
            }
        } finally {
            if (this instanceof CloseableConnectionProvider)
                ((CloseableConnectionProvider) this).close(connection);
        }

    }

    @Override
    public DataSource getDataSource() {
        return new SmartDataSource() {

            @Override
            public boolean shouldClose(Connection con) {
                return false;
            }

            @Override
            public Connection getConnection() throws SQLException {
                // SmartDataSource
                return AbstractConnectionProvider.this.getConnection();
            }

            @Override
            public Connection getConnection(String username, String password) throws SQLException {
                return getConnection();
            }

            @Override
            public <T> T unwrap(Class<T> iface) throws SQLException {
                return null;
            }

            @Override
            public boolean isWrapperFor(Class<?> iface) throws SQLException {
                return false;
            }

            @Override
            public PrintWriter getLogWriter() throws SQLException {
                return null;
            }

            @Override
            public void setLogWriter(PrintWriter out) throws SQLException {

            }

            @Override
            public void setLoginTimeout(int seconds) throws SQLException {

            }

            @Override
            public int getLoginTimeout() throws SQLException {
                return 0;
            }

            @Override
            public Logger getParentLogger() throws SQLFeatureNotSupportedException {
                return null;
            }
        };
    }
}
