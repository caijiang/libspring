package me.jiangcai.lib.jdbc.impl;

import me.jiangcai.lib.jdbc.ConnectionProvider;

import java.sql.Connection;

/**
 * @author CJ
 */
class SimpleConnectionProvider extends AbstractConnectionProvider implements ConnectionProvider {

    private final Connection connection;

    SimpleConnectionProvider(Connection connection) {
        this.connection = connection;
    }

    @Override
    public Connection getConnection() {
        return connection;
    }
}
