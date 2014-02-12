package org.mifosplatform.portfolio.loanaccount.handler;

import org.mifosplatform.commands.handler.NewCommandSourceHandler;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.portfolio.loanaccount.service.LoanApplicationWritePlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LoanAccountFundMappingModificationCommandHandler implements NewCommandSourceHandler {

    private final LoanApplicationWritePlatformService writePlatformService;

    @Autowired
    public LoanAccountFundMappingModificationCommandHandler(final LoanApplicationWritePlatformService writePlatformService) {
        this.writePlatformService = writePlatformService;
    }

    @Transactional
    @Override
    public CommandProcessingResult processCommand(final JsonCommand command) {
        // TODO Auto-generated method stub
        return this.writePlatformService.modifyLoanAccountForFundMapping(command);
    }

}
