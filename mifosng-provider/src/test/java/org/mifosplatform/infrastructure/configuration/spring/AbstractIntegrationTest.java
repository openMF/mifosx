package org.mifosplatform.infrastructure.configuration.spring;

import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Base class for integration tests.
 * 
 * Contrary to the tests which (at the time of writing) are in
 * src/integrationTest/java (which actually really are more "end-to-end" kind of
 * tests; they call the REST API via remote (localhost) HTTP, out-of-process),
 * these tests are integration tests in the Spring sense - to run in-process.
 * Contrary to unit tests, which test only one/a few class and mock others, such
 * integration tests have access to a "fully initialized" (all beans)
 * environment.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestSpringConfiguration.class)
public abstract class AbstractIntegrationTest {

}
