/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.loanaccount.handler;

import org.mifosplatform.commands.handler.CommandHandlerWithHooks;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.hooks.CommandHookType;
import org.mifosplatform.portfolio.loanaccount.service.LoanWritePlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LoanRecoveryPaymentCommandHandler extends CommandHandlerWithHooks {

    private final LoanWritePlatformService writePlatformService;

    @Autowired
    public LoanRecoveryPaymentCommandHandler(LoanWritePlatformService writePlatformService) {
        super(CommandHookType.LoanRecoveryPayment);
        this.writePlatformService = writePlatformService;
    }

    @Override
    public CommandProcessingResult actualProcessCommand(JsonCommand command) {
        final boolean isRecoveryRepayment = true;
        return writePlatformService.makeLoanRepayment(command.getLoanId(), command, isRecoveryRepayment);
    }
}
