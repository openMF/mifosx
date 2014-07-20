package org.mifosplatform.infrastructure.configuration.spring;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;

/**
 * Spring @Configuration which is set-up exactly like a real production
 * run-time, including having a full/normal database available. Contrary to the
 * tests which (at the time of writing) are in src/integrationTest/java (which
 * actually really are more "end-to-end" kind of tests; they call the REST API
 * via remote (localhost) HTTP, out-of-process), these tests are integration
 * tests in the Spring sense - to run in-process. Contrary to unit tests, which
 * test only one/a few class and mock others, such integration tests have access
 * to a "fully initialized" (all beans) environment.
 */
@Configuration
@Import(TestDataSourceConfiguration.class)
// we do NOT want the testContext.xml here, but the real one!
@ImportResource("classpath*:META-INF/spring/appContext.xml")
public class TestSpringConfiguration {
}