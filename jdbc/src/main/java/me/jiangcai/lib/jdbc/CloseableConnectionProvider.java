package me.jiangcai.lib.jdbc;

import java.sql.Connection;

/**
 * 可被关闭的提供者
 *
 * @author CJ
 */
public interface CloseableConnectionProvider extends ConnectionProvider {

    void close(Connection connection);

}
