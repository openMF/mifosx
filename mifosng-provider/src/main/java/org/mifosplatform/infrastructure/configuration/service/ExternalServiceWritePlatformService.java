package org.mifosplatform.infrastructure.configuration.service;

import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;

public interface ExternalServiceWritePlatformService {
	
	CommandProcessingResult updateExternalServicesProperties(Long externalServiceId, JsonCommand command);

}
