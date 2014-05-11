package org.mifosplatform.infrastructure.configuration;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifosplatform.infrastructure.configuration.spring.PropertyConfigurerConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.PropertySource;
import org.springframework.mock.env.MockPropertySource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import edu.emory.mathcs.backport.java.util.Collections;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(/*
                       * initializers = TestApplicationContextInitializer.class
                       * req. newer Spring
                       */)
public class PropertyValueInjectTest {

    // public static class TestApplicationContextInitializer implements
    // ApplicationContextInitializer<ConfigurableApplicationContext> {
    // @Override
    // public void initialize(ConfigurableApplicationContext applicationContext)
    // {
    // }
    // }

    @Configuration
    public static class PropertyValueInjectTestConfiguration extends PropertyConfigurerConfiguration {

        // @Autowired
        // // TODO move
        // private AutowireCapableBeanFactory beanFactory;

        // protected <T> T get(Class<T> type) {
        // return type.cast(beanFactory.createBean(type,
        // AutowireCapableBeanFactory.AUTOWIRE_CONSTRUCTOR, true));
        // }

        @Bean
        ExampleTestConfigurableService exampleTestConfigurableService() {
            // TODO @see
            // http://stackoverflow.com/questions/13583524/spring-annotations-configuration-to-invoke-spring-bean-auto-building
            // return get(ExampleTestConfigurableService.class);
            return new ExampleTestConfigurableService(dependency());
        }

        @Bean
        ExampleTestDependendantService dependency() {
            return new ExampleTestDependendantService();
        }

        @Override
        @SuppressWarnings("unchecked")
        protected Iterable<PropertySource<?>> getPropertySources() {
            MockPropertySource properties = new MockPropertySource();
            properties.setProperty("sample.string", "hello, world");
            properties.setProperty("sample.int", 123);
            return Collections.singleton(properties);
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
