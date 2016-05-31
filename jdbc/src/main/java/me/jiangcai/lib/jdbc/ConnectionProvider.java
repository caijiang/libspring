package me.jiangcai.lib.jdbc;

import org.eclipse.persistence.platform.database.DatabasePlatform;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Jdbc 连接的提供者
 * @author CJ
 */
public interface ConnectionProvider {

    Connection getConnection();

    DataSource getDataSource();

    DatabasePlatform profile() throws SQLException;

}
