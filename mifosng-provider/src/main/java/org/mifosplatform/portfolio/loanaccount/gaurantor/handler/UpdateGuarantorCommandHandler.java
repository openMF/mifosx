package org.mifosplatform.portfolio.loanaccount.gaurantor.handler;

import org.mifosplatform.commands.handler.NewCommandSourceHandler;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.portfolio.loanaccount.gaurantor.service.GuarantorWritePlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UpdateGuarantorCommandHandler implements NewCommandSourceHandler {

    private final GuarantorWritePlatformService writePlatformService;

    @Autowired
    public UpdateGuarantorCommandHandler(final GuarantorWritePlatformService writePlatformService) {
        this.writePlatformService = writePlatformService;
    }

    @Transactional
    @Override
    public CommandProcessingResult processCommand(final JsonCommand command) {
        return this.writePlatformService.updateGuarantor(command.entityId(), command);
    }
}