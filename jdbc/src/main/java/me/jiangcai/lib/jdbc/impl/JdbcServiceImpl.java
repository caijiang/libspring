package me.jiangcai.lib.jdbc.impl;

import com.google.common.primitives.Primitives;
import me.jiangcai.lib.jdbc.CloseableConnectionProvider;
import me.jiangcai.lib.jdbc.ConnectionConsumer;
import me.jiangcai.lib.jdbc.ConnectionProvider;
import me.jiangcai.lib.jdbc.JdbcService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.persistence.internal.databaseaccess.FieldTypeDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.Column;
import javax.persistence.EntityManager;
import javax.persistence.Table;
import javax.sql.DataSource;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Locale;
import java.util.Set;

/**
 * @author CJ
 */
@Service
public class JdbcServiceImpl implements JdbcService {

    private static final Log log = LogFactory.getLog(JdbcServiceImpl.class);

    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired(required = false)
    private Set<EntityManager> entityManagerSet;

//    @Autowired
//    private JpaDialect jpaDialect;

    @Autowired(required = false)
    private DataSource dataSource;

    @Override
    public void tableAlterAddColumn(Class entityClass, String field, String defaultValue)
            throws SQLException, NoSuchFieldException {
        tableAlertColumn(entityClass, field, defaultValue, true);
    }

    @Override
    public void tableAlterModifyColumn(Class entityClass, String field, String defaultValue)
            throws SQLException, NoSuchFieldException {
        tableAlertColumn(entityClass, field, defaultValue, false);
    }

    private void tableAlertColumn(Class entityClass, String fieldName, String defaultValue, boolean creation)
            throws SQLException, NoSuchFieldException {
        ConnectionProvider connectionProvider = getConnectionProvider(entityClass);
        StringBuilder sql = new StringBuilder("ALTER TABLE ");
        Table table = (Table) entityClass.getAnnotation(Table.class);
        if (table != null && table.name().length() > 0)
            sql.append(table.name());
        else
            sql.append(entityClass.getSimpleName().toUpperCase(Locale.ENGLISH));

        if (creation)
            sql.append(" ADD");
        else {
            String alter = "ALTER";
            if (connectionProvider.profile().isMySQL()) {
                alter = "MODIFY";
            }
            sql.append(' ');
            sql.append(alter).append(" COLUMN");
        }

        Field field;
        try {
            field = entityClass.getDeclaredField(fieldName);
        } catch (NoSuchFieldException ex) {
            field = entityClass.getField(fieldName);
        }

        // 字段名未必是准确的 我们最好先找下这个表 找出这个最准确的字段名

        Column column = field.getAnnotation(Column.class);
        if (column != null && column.name().length() > 0)
            sql.append(' ').append(column.name());
        else
            sql.append(' ').append(fieldName.toUpperCase(Locale.ENGLISH));

        //类型
        if (column != null && column.columnDefinition().length() > 0) {
            sql.append(' ').append(column.columnDefinition());
        } else {
            Class type = field.getType();
            boolean primitive = false;
            if (type.isPrimitive()) {
                primitive = true;
                type = Primitives.wrap(type);
                // 如果是基本类型 那么先
            }
            FieldTypeDefinition fieldTypeDefinition = connectionProvider.profile().getFieldTypeDefinition(type);
            sql.append(' ').append(fieldTypeDefinition.getName());
            if (fieldTypeDefinition.isSizeAllowed() && fieldTypeDefinition.isSizeRequired()) {
                if (column != null && fieldTypeDefinition.getName().equalsIgnoreCase("DECIMAL")) {
                    sql.append('(');
                    sql.append(column.precision());
                    sql.append(',');
                    sql.append(column.scale());
                    sql.append(')');
                } else {
                    sql.append('(');
                    if (column != null)
                        sql.append(column.length());
                    else
                        sql.append(fieldTypeDefinition.getDefaultSize());
                    sql.append(')');
                }
            }

            boolean nullable;
            if (column != null) {
                nullable = column.nullable();
                if (nullable) {
                    nullable = fieldTypeDefinition.shouldAllowNull();
                }
            } else {
                nullable = !primitive && fieldTypeDefinition.shouldAllowNull();
            }


            if (nullable)
                sql.append(" NULL");
            else {
                sql.append(" NOT NULL");
                if (defaultValue != null)
                    sql.append(" DEFAULT ").append(defaultValue);
                else
                    sql.append(" DEFAULT 0");
            }
        }
        log.debug("Prepare to " + sql);

        Connection connection = connectionProvider.getConnection();
        try {
            try (Statement statement = connection.createStatement()) {
                statement.executeUpdate(sql.toString());
            }
        } finally {
            if (connectionProvider instanceof CloseableConnectionProvider)
                ((CloseableConnectionProvider) connectionProvider).close(connection);
        }

    }

    private ConnectionProvider getConnectionProvider(Class entityClass) {
        EntityManager entityManager;
        if (entityManagerSet != null) {
            if (entityClass != null) {
                EntityManager matchedEntityManager = null;
                for (EntityManager em : entityManagerSet) {
                    try {
                        em.getEntityManagerFactory().getMetamodel().managedType(entityClass);
                        matchedEntityManager = em;
                        break;
                    } catch (IllegalArgumentException ignored) {
                    }
                }
                entityManager = matchedEntityManager;
            } else
                //noinspection OptionalGetWithoutIsPresent
                entityManager = entityManagerSet.stream().findAny().get();
        } else
            entityManager = null;

        //TODO JpaDialect 是一个很好的获取连接的方式,但目前定位上存在困难
        if (entityManager != null) {
            Connection connection = entityManager.unwrap(Connection.class);
            if (connection == null) {
                throw new IllegalStateException("@Transactional did not work check DataSupportConfig for details.");
            }
            return new SimpleConnectionProvider(connection);
        }

        if (dataSource != null)
            return new DataSourceConnectionProvider(dataSource);

        throw new IllegalStateException("there is no connection provide.");
    }

    @Override
    public void runJdbcWork(ConnectionConsumer connectionConsumer) throws SQLException {
        log.debug("Prepare to run jdbc");
        ConnectionProvider provider = getConnectionProvider(null);
        if (provider instanceof CloseableConnectionProvider) {
            connectionConsumer = connectionConsumer.andThen((CloseableConnectionProvider) provider);
        }

        connectionConsumer.accept(provider);
        log.debug("End jdbc");
    }

    @Override
    public void runStandaloneJdbcWork(ConnectionConsumer connectionConsumer) throws SQLException {
        runJdbcWork(connectionConsumer);
    }

}
