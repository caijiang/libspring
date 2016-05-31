package me.jiangcai.lib.jdbc;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.metadata.TableMetaDataContext;
import org.springframework.jdbc.core.metadata.TableMetaDataProvider;
import org.springframework.jdbc.core.metadata.TableMetaDataProviderFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.sql.Statement;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author CJ
 */
@ContextConfiguration(classes = {JdbcSpringConfig.class, DataSourceConfig.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class JdbcServiceTest {

    @Autowired
    protected JdbcService jdbcService;

    @Test
    public void runJdbcWork() throws Exception {
        // 删除一个表,再创建一个表如何?
        jdbcService.runJdbcWork(connection -> {
            try (Statement statement = connection.getConnection().createStatement()) {
                statement.execute("CREATE TABLE simple (id INT )");
                // IF NOT EXISTS
            }
        });

        jdbcService.runJdbcWork(connection -> {
            TableMetaDataContext context = new TableMetaDataContext();
            context.setTableName("simple");
            TableMetaDataProvider provider = TableMetaDataProviderFactory.createMetaDataProvider(connection.getDataSource(), context);
            assertThat(provider.getTableParameterMetaData())
                    .hasSize(1);
            assertThat(provider.getTableParameterMetaData().get(0).getParameterName())
                    .isEqualToIgnoringCase("id");
        });

        jdbcService.runJdbcWork(connection -> {
            try (Statement statement = connection.getConnection().createStatement()) {
                statement.execute("DROP TABLE simple");
            }
        });
    }


}