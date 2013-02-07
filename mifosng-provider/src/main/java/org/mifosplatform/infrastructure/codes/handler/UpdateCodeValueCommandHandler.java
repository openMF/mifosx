package org.mifosplatform.infrastructure.codes.handler;

import org.mifosplatform.commands.handler.NewCommandSourceHandler;
import org.mifosplatform.infrastructure.codes.service.CodeValueWritePlatformService;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UpdateCodeValueCommandHandler implements NewCommandSourceHandler {

    private final CodeValueWritePlatformService writePlatformService;

    @Autowired
    public UpdateCodeValueCommandHandler(final CodeValueWritePlatformService writePlatformService) {
        this.writePlatformService = writePlatformService;
    }

    @Transactional
    @Override
    public CommandProcessingResult processCommand(final JsonCommand command) {

        return this.writePlatformService.updateCodeValue(command.entityId(), command);
    }
}