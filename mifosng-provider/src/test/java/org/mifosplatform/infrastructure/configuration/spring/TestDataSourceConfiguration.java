package org.mifosplatform.infrastructure.configuration.spring;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

@Configuration
public class TestDataSourceConfiguration {

    // TODO use a real data source!

    /*
     * @Bean public void simpleNamingContextBuilder() throws
     * IllegalStateException, NamingException { SimpleNamingContextBuilder
     * builder = new SimpleNamingContextBuilder(); DataSource ds =
     * getNewTenantDataSource(); // name here must match <jee:jndi-lookup> in
     * infrastructure.xml
     * builder.bind("java:comp/env/jdbc/mifosplatform-tenants", ds);
     * builder.activate(); }
     */

    /**
     * JdbcTenantDetailsService needs this. It's usually defined by the
     * <jee:jndi-lookup> in infrastructure.xml, but for tests we do like this.
     */
    @Bean
    public DataSource tenantDataSourceJndi() {
        return getNewTenantDataSource();
    }

    // NOT @Bean
    private DriverManagerDataSource getNewTenantDataSource() {
        DriverManagerDataSource dmds = new DriverManagerDataSource();
        dmds.setDriverClassName(com.mysql.jdbc.Driver.class.getName());
        dmds.setUrl("jdbc:mysql://localhost:3306/mifosplatform-tenants");
        dmds.setUsername("root");
        dmds.setPassword("mysql");
        return dmds;
    }
}
