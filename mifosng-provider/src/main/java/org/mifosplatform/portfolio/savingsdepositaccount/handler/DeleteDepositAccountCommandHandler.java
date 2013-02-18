package org.mifosplatform.portfolio.savingsdepositaccount.handler;

import org.mifosplatform.commands.handler.NewCommandSourceHandler;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.portfolio.savingsdepositaccount.service.DepositAccountWritePlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DeleteDepositAccountCommandHandler implements NewCommandSourceHandler {
	
	private final DepositAccountWritePlatformService depositAccountWritePlatformService;
	
	@Autowired
	public DeleteDepositAccountCommandHandler(final DepositAccountWritePlatformService depositAccountWritePlatformService) {
		this.depositAccountWritePlatformService = depositAccountWritePlatformService;
	}

	@Override
	public CommandProcessingResult processCommand(JsonCommand command) {
		return this.depositAccountWritePlatformService.deleteDepositAccount(command.entityId());
	}
	

}
