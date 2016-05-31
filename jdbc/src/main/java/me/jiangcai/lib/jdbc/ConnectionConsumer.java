package me.jiangcai.lib.jdbc;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;

/**
 * @author CJ
 */
@FunctionalInterface
public interface ConnectionConsumer {

    void accept(ConnectionProvider connection) throws SQLException;

    /**
     * Returns a composed {@code Consumer} that performs, in sequence, this
     * operation followed by the {@code after} operation. If performing either
     * operation throws an exception, it is relayed to the caller of the
     * composed operation.  If performing this operation throws an exception,
     * the {@code after} operation will not be performed.
     *
     * @param after the operation to perform after this operation
     * @return a composed {@code Consumer} that performs in sequence this
     * operation followed by the {@code after} operation
     * @throws NullPointerException if {@code after} is null
     */
    default ConnectionConsumer andThen(CloseableConnectionProvider after) {
        Objects.requireNonNull(after);
        return t -> {
            Connection connection = t.getConnection();
            try {
                accept(t);
            } finally {
                //noinspection ThrowFromFinallyBlock
                after.close(connection);
            }
        };
    }
}
