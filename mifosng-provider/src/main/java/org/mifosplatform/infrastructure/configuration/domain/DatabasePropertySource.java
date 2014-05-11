package org.mifosplatform.infrastructure.configuration.domain;

import java.util.List;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.stereotype.Component;

/**
 * Implementation of a Spring @see {@link PropertySource}, backed by rows of @see
 * {@link ExternalService} read from the c_external_service database table.
 */
@Component
public class DatabasePropertySource extends PropertiesPropertySource {

    // private ExternalServiceRepository externalServiceRepository;

    @Autowired
    public DatabasePropertySource(ExternalServiceRepository externalServiceRepository) {
        super(DatabasePropertySource.class.getName(), loadProperties(externalServiceRepository));
        // this.externalServiceRepository = externalServiceRepository;
    }

    private static Properties loadProperties(ExternalServiceRepository externalServiceRepository) {
        Properties properties = new Properties();
        List<ExternalService> propertyPairs = externalServiceRepository.findAll();
        for (ExternalService propertyPair : propertyPairs) {
            properties.put(propertyPair.getName(), propertyPair.getValue());
        }
        return properties;
    }
}
