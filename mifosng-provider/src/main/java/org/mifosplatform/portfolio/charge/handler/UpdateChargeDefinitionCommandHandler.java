/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.charge.handler;

import org.mifosplatform.commands.annotation.CommandType;
import org.mifosplatform.commands.handler.NewCommandSourceHandler;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.portfolio.charge.service.ChargeWritePlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@CommandType(entity = "CHARGE", action = "UPDATE")

public class UpdateChargeDefinitionCommandHandler implements NewCommandSourceHandler {

    private final ChargeWritePlatformService clientWritePlatformService;

    @Autowired
    public UpdateChargeDefinitionCommandHandler(final ChargeWritePlatformService clientWritePlatformService) {
        this.clientWritePlatformService = clientWritePlatformService;
    }

    @Transactional
    @Override
    public CommandProcessingResult processCommand(final JsonCommand command) {

        return this.clientWritePlatformService.updateCharge(command.entityId(), command);
    }
}