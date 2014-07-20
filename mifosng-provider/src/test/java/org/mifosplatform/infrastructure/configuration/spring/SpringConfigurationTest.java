package org.mifosplatform.infrastructure.configuration.spring;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * This integration test ensure that the "production" Spring XML configuration
 * files (appContext.xml & Co.) are valid.  It does not need any database for that.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestSpringConfigurationWithoutDatabaseAndNoJobs.class)
public class SpringConfigurationTest {

    @Test
    public void testSpringXMLConfiguration() {
        // Ahl iz wehl, or what? ;) This method is intentionally empty. Its only
        // goal is to "test" the Spring XML configuration files (appContext.xml
        // & Co., which it automatically does; it would fail if e.g. they cannot
        // be read, or any bean in them cannot be instantiated for whatever
        // reason.
    }
}
