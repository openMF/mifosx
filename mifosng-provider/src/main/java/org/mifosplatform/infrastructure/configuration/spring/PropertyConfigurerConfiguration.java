package org.mifosplatform.infrastructure.configuration.spring;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;

@Configuration
public abstract class PropertyConfigurerConfiguration {

    abstract protected Iterable<PropertySource<?>> getPropertySources();

    @Bean
    PropertySourcesPlaceholderConfigurer propertyConfigurer() {
        // http://blog.jamesdbloom.com/UsingPropertySourceAndEnvironment.html
        PropertySourcesPlaceholderConfigurer propertyConfigurer = new PropertySourcesPlaceholderConfigurer();
        MutablePropertySources propertySources = new MutablePropertySources();
        for (PropertySource<?> propertySource : getPropertySources()) {
            propertySources.addLast(propertySource);
        }
        propertyConfigurer.setPropertySources(propertySources);
        return propertyConfigurer;
    }

}
