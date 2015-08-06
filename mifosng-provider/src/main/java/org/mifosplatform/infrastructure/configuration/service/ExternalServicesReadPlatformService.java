package org.mifosplatform.infrastructure.configuration.service;

import org.mifosplatform.infrastructure.configuration.data.ExternalServicesData;

public interface ExternalServicesReadPlatformService {
	
	ExternalServicesData getExternalServiceDetailsByServiceName(String serviceName);

}
