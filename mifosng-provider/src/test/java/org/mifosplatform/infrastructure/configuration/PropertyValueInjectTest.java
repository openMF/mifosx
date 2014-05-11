package org.mifosplatform.infrastructure.configuration;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;
import org.springframework.mock.env.MockPropertySource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class PropertyValueInjectTest {

    @Configuration
    public static class PropertyValueInjectTestConfiguration {

        @Bean
        ExampleTestConfigurableService exampleTestConfigurableService() {
            // TODO
            // http://stackoverflow.com/questions/13583524/spring-annotations-configuration-to-invoke-spring-bean-auto-building
            return new ExampleTestConfigurableService(dependency());
        }

        @Bean
        ExampleTestDependendantService dependency() {
            return new ExampleTestDependendantService();
        }

        @Bean
        PropertySource<?> propertySource() {
            MockPropertySource properties = new MockPropertySource();
            properties.setProperty("sample.string", "hello, world");
            properties.setProperty("sample.int", 123);
            return properties;
        }

        @Bean
        PropertySourcesPlaceholderConfigurer propertyConfigurer() {
            // http://blog.jamesdbloom.com/UsingPropertySourceAndEnvironment.html
            PropertySourcesPlaceholderConfigurer propertyConfigurer = new PropertySourcesPlaceholderConfigurer();
            MutablePropertySources propertySources = new MutablePropertySources();
            propertySources.addFirst(propertySource());
            propertyConfigurer.setPropertySources(propertySources);
            return propertyConfigurer;
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
