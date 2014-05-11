package org.mifosplatform.infrastructure.configuration;

import org.springframework.beans.factory.annotation.Value;

@SuppressWarnings("unused")
// do NOT @Service annotate this one, as we don't want Spring to create it at
// initial wiring time, but, but our utility
public class ExampleTestConfigurableService {

    private final ExampleTestDependendantService dependency;

    @Value("${sample.string}")
    private String someStringConfigurationProperty;

    @Value("${sample.int}")
    private int someIntegerConfigurationProperty;

    public ExampleTestConfigurableService(ExampleTestDependendantService dependency) {
        super();
        this.dependency = dependency;
    }

    public String getSomeStringConfigurationProperty() {
        return this.someStringConfigurationProperty;
    }

    public int getSomeIntegerConfigurationProperty() {
        return this.someIntegerConfigurationProperty;
    }

}
