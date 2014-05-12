package org.mifosplatform.infrastructure.configuration.spring;

import java.sql.Connection;
import java.sql.Statement;

import javax.sql.DataSource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestDataSourceConfiguration.class)
public class TestDataSourceConfigurationTest {

    @Autowired
    @Qualifier("tenantDataSourceJndi")
    DataSource dataSource;

    @Test
    public void testTestDatasource() throws Exception {
        Connection connection = dataSource.getConnection();
        Statement statement = connection.createStatement();
        statement.execute("select 1");
        statement.close();
        connection.close();
    }
}
