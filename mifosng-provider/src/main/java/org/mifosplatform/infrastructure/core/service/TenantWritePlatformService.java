package org.mifosplatform.infrastructure.core.service;

import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;

public interface TenantWritePlatformService {

	CommandProcessingResult createTenant(JsonCommand command);
}
