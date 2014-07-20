package org.mifosplatform.infrastructure.configuration.spring;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifosplatform.infrastructure.core.service.TenantDatabaseUpgradeService;
import org.mifosplatform.infrastructure.jobs.service.JobRegisterService;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * This integration test ensure that the "production" Spring XML configuration
 * files (appContext.xml & Co.) are valid.  Because it uses the ...
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration // NOT TestSpringConfiguration, but the TestSpringConfigurationWithoutDatabase inner class (picked up automatically)
public class SpringConfigurationTest {

    @Configuration
    public static class TestSpringConfigurationWithoutDatabaseAndNoJobs extends TestSpringConfiguration {
        
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
         * JobRegisterServiceImpl has a @PostConstruct loadAllJobs() which
         * accesses the database on start-up.
         */
        @Bean
        public JobRegisterService jobRegisterServiceImpl() {
            JobRegisterService mockJobRegisterService = Mockito.mock(JobRegisterService.class);
            return mockJobRegisterService;
        }
        
// TODO .. and test separately! 
//        @Bean
//        public DataSource tenantDataSourceJndi() throws SQLException {
//            DataSource mockDS = Mockito.mock(DataSource.class);
//            Mockito.when(mockDS.getConnection()).thenThrow(new RuntimeException("yo"));
//            return mockDS;
//        }
    }
    
    @Test
    public void testSpringXMLConfiguration() {
        // Ahl iz wehl, or what? ;) This method is intentionally empty. Its only
        // goal is to "test" the appContext.xml (which it automatically does).
    }
}
