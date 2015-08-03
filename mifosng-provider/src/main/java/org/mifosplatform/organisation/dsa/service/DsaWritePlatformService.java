package org.mifosplatform.organisation.dsa.service;

import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;

public interface DsaWritePlatformService {

	CommandProcessingResult createDsa(final JsonCommand command);
	
	CommandProcessingResult updateDsa(final Long dsaId,final JsonCommand command);
}
