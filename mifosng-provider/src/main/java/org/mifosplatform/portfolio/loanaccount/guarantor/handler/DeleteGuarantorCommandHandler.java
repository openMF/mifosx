package org.mifosplatform.portfolio.loanaccount.guarantor.handler;

import org.mifosplatform.commands.handler.NewCommandSourceHandler;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.portfolio.loanaccount.guarantor.service.GuarantorWritePlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DeleteGuarantorCommandHandler implements NewCommandSourceHandler {

    private final GuarantorWritePlatformService guarantorWritePlatformService;

    @Autowired
    public DeleteGuarantorCommandHandler(final GuarantorWritePlatformService guarantorWritePlatformService) {
        this.guarantorWritePlatformService = guarantorWritePlatformService;
    }

    @Transactional
    @Override
    public CommandProcessingResult processCommand(final JsonCommand command) {
        return this.guarantorWritePlatformService.removeGuarantor(command.entityId());
    }
}