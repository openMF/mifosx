package org.mifosplatform.infrastructure.configuration.spring;

import org.mifosplatform.infrastructure.core.service.TenantDatabaseUpgradeService;
import org.mifosplatform.infrastructure.jobs.service.JobRegisterService;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * TestSpringConfiguration extension which does not require a running DataBase.
 * It also does not load any job configuration (as they are in the DB), thus nor
 * starting any background jobs. For some integration tests, this may be
 * perfectly sufficient (and faster to run such tests).
 */
@Configuration
public class TestSpringConfigurationWithoutDatabaseAndNoJobs extends TestSpringConfiguration {

    /**
     * Override TenantDatabaseUpgradeService binding, because the real
     * one has a @PostConstruct upgradeAllTenants() which accesses
     * the database on start-up.
     */
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

    // TODO Another @Bean that would be sensible to mock in this @Configuration (with a separate test) would be:
    // @Bean public DataSource tenantDataSourceJndi() {
    // so that it throws a clear IllegalStateException("DataSource not available with TestSpringConfigurationWithoutDatabaseAndNoJobs, use TestSpringConfiguration if you need a DB (or override the @Bean requiring a DB at start-up if you don't)")
}