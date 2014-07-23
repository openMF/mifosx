/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.interestratechart.handler;

import org.mifosplatform.commands.handler.CommandHandlerWithHooks;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.hooks.CommandHookType;
import org.mifosplatform.portfolio.interestratechart.service.InterestRateChartSlabWritePlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DeleteInterestRateChartSlabCommandHandler extends CommandHandlerWithHooks {

    private final InterestRateChartSlabWritePlatformService writePlatformService;

    @Autowired
    public DeleteInterestRateChartSlabCommandHandler(final InterestRateChartSlabWritePlatformService writePlatformService) {
        super(CommandHookType.DeleteInterestRateChartSlab);
        this.writePlatformService = writePlatformService;
    }

    @Override
    public CommandProcessingResult actualProcessCommand(final JsonCommand command) {
        //command.subentityId(); //returns chart id
        return this.writePlatformService.deleteChartSlab(command.entityId(), command.subentityId());
    }
}