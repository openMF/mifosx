package org.mifosplatform.infrastructure.configuration.exception;

import org.mifosplatform.infrastructure.core.exception.AbstractPlatformResourceNotFoundException;

public class ExternalServiceConfigurationNotFoundException extends AbstractPlatformResourceNotFoundException {

	 public ExternalServiceConfigurationNotFoundException(final String serviceName) {
	        super("error.msg.configuration.property.invalid", "Service Name`" + serviceName + "` does not exist", serviceName);
	    }
	 
	 public ExternalServiceConfigurationNotFoundException(final Long id, final String name) {
	        super("error.msg.configuration.property.invalid", "Service Id`" + id + "` does not exist with the following named parameter`" + name + "`" , name);
	    }
}
