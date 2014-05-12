package org.mifosplatform.infrastructure.configuration;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * DatabasePropertySource integration test.
 */
public class DatabasePropertySourceIntegrationTest {

    // TODO extends AbstractIntegrationTest

    // TODO re-activate after https://github.com/openMF/mifosx/pull/907 is in

    @Autowired
    ExampleTestConfigurableService exampleTestservice;

    // TODO setUp which insert two rows, make sure tx aborts so no result left..

    @Test
    @Ignore
    public void testValuePropertiesFromMockPropertySource() {
        Assert.assertEquals("hello, world", exampleTestservice.getSomeStringConfigurationProperty());
        Assert.assertEquals(123, exampleTestservice.getSomeIntegerConfigurationProperty());
    }
}
