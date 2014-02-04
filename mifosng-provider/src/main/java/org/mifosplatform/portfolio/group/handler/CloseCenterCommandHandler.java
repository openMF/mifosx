package org.mifosplatform.portfolio.group.handler;

import org.mifosplatform.commands.handler.NewCommandSourceHandler;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.portfolio.group.service.GroupingTypesWritePlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CloseCenterCommandHandler implements NewCommandSourceHandler {

    private final GroupingTypesWritePlatformService groupingTypesWritePlatformService;

    @Autowired
    public CloseCenterCommandHandler(final GroupingTypesWritePlatformService groupingTypesWritePlatformService) {
        this.groupingTypesWritePlatformService = groupingTypesWritePlatformService;
    }

    @Override
    @Transactional
    public CommandProcessingResult processCommand(final JsonCommand command) {
        return this.groupingTypesWritePlatformService.closeCenter(command.entityId(), command);
    }

}
