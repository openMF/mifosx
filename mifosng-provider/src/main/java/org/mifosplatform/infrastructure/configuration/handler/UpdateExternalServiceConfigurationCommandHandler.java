package org.mifosplatform.infrastructure.configuration.handler;

import org.mifosplatform.commands.annotation.CommandType;
import org.mifosplatform.commands.handler.NewCommandSourceHandler;
import org.mifosplatform.infrastructure.configuration.service.ExternalServiceWritePlatformService;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@CommandType(entity = "EXTERNALSERVICES", action = "UPDATE")
public class UpdateExternalServiceConfigurationCommandHandler implements NewCommandSourceHandler {
	
	private final ExternalServiceWritePlatformService writePlatformService;
	
	@Autowired
	public UpdateExternalServiceConfigurationCommandHandler(final ExternalServiceWritePlatformService writePlatformService){
		this.writePlatformService = writePlatformService;
	}
	
	@Transactional
    @Override
    public CommandProcessingResult processCommand(final JsonCommand command) {
		return this.writePlatformService.updateExternalServicesProperties(command.getTransactionId(), command);
	}

}
