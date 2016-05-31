package me.jiangcai.lib.jdbc.jpa;

import me.jiangcai.lib.jdbc.DataSourceConfig;
import me.jiangcai.lib.jdbc.JdbcServiceTest;
import me.jiangcai.lib.jdbc.jpa.entity.TestData;
import org.junit.Test;
import org.springframework.jdbc.core.metadata.TableMetaDataContext;
import org.springframework.jdbc.core.metadata.TableMetaDataProvider;
import org.springframework.jdbc.core.metadata.TableMetaDataProviderFactory;
import org.springframework.jdbc.core.metadata.TableParameterMetaData;
import org.springframework.test.context.ContextConfiguration;

import java.sql.Statement;
import java.sql.Types;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author CJ
 */
@ContextConfiguration(classes = {JpaConfig.class, DataSourceConfig.class})
public class JpaJdbcServiceTest extends JdbcServiceTest {

    @Test
    public void tableAlertAddColumn() throws Exception {
        // 首先先删除那个表的玩意儿……
        jdbcService.runJdbcWork(connection -> {
            TableMetaDataContext context = new TableMetaDataContext();
            context.setTableName("TestData");
            TableMetaDataProvider provider = TableMetaDataProviderFactory.createMetaDataProvider
                    (connection.getDataSource(), context);
            if (provider.getTableParameterMetaData().size() > 1) {
                //移除掉多的
                try (Statement statement = connection.getConnection().createStatement()) {
                    statement.execute("ALTER TABLE TestData DROP name1");
                }
            }

            provider = TableMetaDataProviderFactory.createMetaDataProvider(connection.getDataSource(), context);
            assertThat(provider.getTableParameterMetaData())
                    .hasSize(1);
        });


        jdbcService.tableAlterAddColumn(TestData.class, "name1", null);

        jdbcService.runJdbcWork(connection -> {
            TableMetaDataContext context = new TableMetaDataContext();
            context.setTableName("TestData");
            TableMetaDataProvider provider = TableMetaDataProviderFactory.createMetaDataProvider(connection.getDataSource(), context);
            assertThat(provider.getTableParameterMetaData())
                    .hasSize(2);
        });

    }

    private TableParameterMetaData columnByName(String name, TableMetaDataProvider provider) {
        for (TableParameterMetaData metaData : provider.getTableParameterMetaData()) {
            if (name.equalsIgnoreCase(metaData.getParameterName()))
                return metaData;
        }
        return null;
    }

    @Test
    public void tableAlertModifyColumn() throws Exception {
        jdbcService.runJdbcWork(connection -> {
            TableMetaDataContext context = new TableMetaDataContext();
            context.setTableName("TestData");
            TableMetaDataProvider provider = TableMetaDataProviderFactory.createMetaDataProvider
                    (connection.getDataSource(), context);

            TableParameterMetaData name1 = columnByName("name1", provider);

            assertThat(name1)
                    .isNotNull();
            assertThat(name1.getSqlType())
                    .isEqualTo(Types.VARCHAR);


            try (Statement statement = connection.getConnection().createStatement()) {
                statement.execute("ALTER TABLE TestData ALTER name1 INT ");
            }

            provider = TableMetaDataProviderFactory.createMetaDataProvider(connection.getDataSource(), context);

            name1 = columnByName("name1", provider);

            assertThat(name1)
                    .isNotNull();
            assertThat(name1.getSqlType())
                    .isEqualTo(Types.INTEGER);

        });

        jdbcService.tableAlterModifyColumn(TestData.class, "name1", null);


        jdbcService.runJdbcWork(connection -> {
            TableMetaDataContext context = new TableMetaDataContext();
            context.setTableName("TestData");
            TableMetaDataProvider provider = TableMetaDataProviderFactory.createMetaDataProvider
                    (connection.getDataSource(), context);

            TableParameterMetaData name1 = columnByName("name1", provider);

            assertThat(name1)
                    .isNotNull();
            assertThat(name1.getSqlType())
                    .isEqualTo(Types.VARCHAR);

        });
    }
}
