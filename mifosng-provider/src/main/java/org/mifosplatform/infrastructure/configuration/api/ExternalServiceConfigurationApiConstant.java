package org.mifosplatform.infrastructure.configuration.api;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class ExternalServiceConfigurationApiConstant {

	public static final String NAME = "name";
	public static final String VALUE = "value";
	public static final String EXTERNAL_SERVICE_RESOURCE_NAME = "externalServiceConfiguration";
	
	public static final Set<String> EXTERNAL_SERVICE_CONFIGURATION_DATA_PARAMETERS = new HashSet<>(Arrays.asList(NAME, VALUE));
}
