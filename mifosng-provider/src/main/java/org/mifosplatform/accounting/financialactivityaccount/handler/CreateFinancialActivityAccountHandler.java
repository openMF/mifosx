/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.accounting.financialactivityaccount.handler;

import org.mifosplatform.accounting.financialactivityaccount.service.FinancialActivityAccountWritePlatformService;
import org.mifosplatform.commands.handler.CommandHandlerWithHooks;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.hooks.CommandHookType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreateFinancialActivityAccountHandler extends CommandHandlerWithHooks {

    private final FinancialActivityAccountWritePlatformService writePlatformService;

    @Autowired
    public CreateFinancialActivityAccountHandler(final FinancialActivityAccountWritePlatformService writePlatformService) {
        super(CommandHookType.CreateFinancialActivityAccount);
        this.writePlatformService = writePlatformService;
    }

    @Transactional
    @Override
    public CommandProcessingResult actualProcessCommand(final JsonCommand command) {

        return this.writePlatformService.createFinancialActivityAccountMapping(command);
    }
}