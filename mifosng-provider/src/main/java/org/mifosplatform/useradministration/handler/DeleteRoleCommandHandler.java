/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.useradministration.handler;

import org.mifosplatform.commands.annotation.CommandType;
import org.mifosplatform.commands.handler.NewCommandSourceHandler;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.useradministration.service.RoleWritePlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@CommandType(entity = "ROLE", action = "DELETE")

public class DeleteRoleCommandHandler implements NewCommandSourceHandler {

    private final RoleWritePlatformService writePlatformService;

    @Autowired
    public DeleteRoleCommandHandler(final RoleWritePlatformService writePlatformService) {
        this.writePlatformService = writePlatformService;
    }

    @Override
    @Transactional
    public CommandProcessingResult processCommand(final JsonCommand command) {
        return this.writePlatformService.deleteRole(command.entityId());
    }

}
