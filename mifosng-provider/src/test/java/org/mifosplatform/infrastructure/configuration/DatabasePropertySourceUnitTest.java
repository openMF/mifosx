package org.mifosplatform.infrastructure.configuration;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifosplatform.infrastructure.configuration.domain.DatabasePropertySource;
import org.mifosplatform.infrastructure.configuration.domain.ExternalService;
import org.mifosplatform.infrastructure.configuration.domain.ExternalServiceRepository;
import org.mifosplatform.infrastructure.configuration.spring.PropertyConfigurerConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.PropertySource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import edu.emory.mathcs.backport.java.util.Collections;

/**
 * DatabasePropertySource unit test. More intended as an illustration how to
 * properly write unit tests, which mock their dependencies, then really having
 * any value, in this case.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class DatabasePropertySourceUnitTest {

    @Configuration
    public static class DatabasePropertySourceConfiguration extends PropertyConfigurerConfiguration {

        @Override
        @SuppressWarnings("unchecked")
        protected Iterable<PropertySource<?>> getPropertySources() {
            return Collections.singleton(new DatabasePropertySource(mockExternalServiceRepository()));
        }

        private ExternalServiceRepository mockExternalServiceRepository() {
            ExternalServiceRepository mockExternalServiceRepository = mock(ExternalServiceRepository.class);
            List<ExternalService> externalServices = new ArrayList<ExternalService>(2);
            externalServices.add(new ExternalService("sample.string", "hello, world"));
            externalServices.add(new ExternalService("sample.int", "123"));
            when(mockExternalServiceRepository.findAll()).thenReturn(externalServices);
            return mockExternalServiceRepository;
        }

        @Bean
        ExampleTestConfigurableService exampleTestConfigurableService() {
            return new ExampleTestConfigurableService(dependency());
        }

        @Bean
        ExampleTestDependendantService dependency() {
            return new ExampleTestDependendantService();
        }
    }

    @Autowired
    ExampleTestConfigurableService exampleTestservice;

    @Test
    public void testValuePropertiesFromMockPropertySource() {
        Assert.assertEquals("hello, world", exampleTestservice.getSomeStringConfigurationProperty());
        Assert.assertEquals(123, exampleTestservice.getSomeIntegerConfigurationProperty());
    }
}
