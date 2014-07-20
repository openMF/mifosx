package org.mifosplatform.infrastructure.configuration.spring;

import org.mifosplatform.infrastructure.core.service.TenantDatabaseUpgradeService;
import org.mifosplatform.infrastructure.jobs.service.JobRegisterService;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TestSpringConfigurationWithoutDatabaseAndNoJobs extends TestSpringConfiguration {

    @Bean
    public TenantDatabaseUpgradeService tenantDatabaseUpgradeService() {
        return new TenantDatabaseUpgradeService(null) {

            @Override
            public void upgradeAllTenants() {
                // NOOP
            }
        };
    }

    /**
     * Override JobRegisterService binding, because the real
     * JobRegisterServiceImpl has a @PostConstruct loadAllJobs() which accesses
     * the database on start-up.
     */
    @Bean
    public JobRegisterService jobRegisterServiceImpl() {
        JobRegisterService mockJobRegisterService = Mockito.mock(JobRegisterService.class);
        return mockJobRegisterService;
    }

    // TODO .. and test separately!
    // @Bean
    // public DataSource tenantDataSourceJndi() throws SQLException {
    // DataSource mockDS = Mockito.mock(DataSource.class);
    // Mockito.when(mockDS.getConnection()).thenThrow(new
    // RuntimeException("yo"));
    // return mockDS;
    // }
}