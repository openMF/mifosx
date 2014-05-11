package org.mifosplatform.infrastructure.configuration;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * DatabasePropertySource integration test.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:META-INF/spring/appContext.xml")
public class DatabasePropertySourceIntegrationTest {

    @Autowired
    ExampleTestConfigurableService exampleTestservice;

    @Test
    public void testValuePropertiesFromMockPropertySource() {
        Assert.assertEquals("hello, world", exampleTestservice.getSomeStringConfigurationProperty());
        Assert.assertEquals(123, exampleTestservice.getSomeIntegerConfigurationProperty());
    }
}
