/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.infrastructure.jobs.handler;

import org.mifosplatform.commands.handler.CommandHandlerWithHooks;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.hooks.CommandHookType;
import org.mifosplatform.infrastructure.jobs.service.SchedularWritePlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UpdateJobDetailCommandhandler extends CommandHandlerWithHooks {

    private final SchedularWritePlatformService schedularWritePlatformService;

    @Autowired
    public UpdateJobDetailCommandhandler(final SchedularWritePlatformService schedularWritePlatformService) {
        super(CommandHookType.UpdateJobDetail);
        this.schedularWritePlatformService = schedularWritePlatformService;
    }

    @Override
    public CommandProcessingResult actualProcessCommand(final JsonCommand command) {
        return this.schedularWritePlatformService.updateJobDetail(command.entityId(), command);
    }

}
