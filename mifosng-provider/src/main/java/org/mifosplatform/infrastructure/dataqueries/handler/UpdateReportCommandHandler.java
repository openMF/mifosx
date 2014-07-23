/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.infrastructure.dataqueries.handler;

import org.mifosplatform.commands.handler.CommandHandlerWithHooks;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.dataqueries.service.ReportWritePlatformService;
import org.mifosplatform.infrastructure.hooks.CommandHookType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UpdateReportCommandHandler extends CommandHandlerWithHooks {

    private final ReportWritePlatformService writePlatformService;

    @Autowired
    public UpdateReportCommandHandler(final ReportWritePlatformService writePlatformService) {
        super(CommandHookType.UpdateReport);
        this.writePlatformService = writePlatformService;
    }

    @Transactional
    @Override
    public CommandProcessingResult actualProcessCommand(final JsonCommand command) {

        return this.writePlatformService.updateReport(command.entityId(), command);
    }
}