/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.accounting.closure.handler;

import org.mifosplatform.accounting.closure.service.GLClosureWritePlatformService;
import org.mifosplatform.commands.handler.NewCommandSourceHandler;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DeleteGLClosureCommandHandler implements NewCommandSourceHandler {

    private final GLClosureWritePlatformService closureWritePlatformService;

    @Autowired
    public DeleteGLClosureCommandHandler(final GLClosureWritePlatformService guarantorWritePlatformService) {
        this.closureWritePlatformService = guarantorWritePlatformService;
    }

    @Transactional
    @Override
    public CommandProcessingResult processCommand(final JsonCommand command) {
        return this.closureWritePlatformService.deleteGLClosure(command.entityId());
    }
}