package org.mifosplatform.portfolio.transfer.handler;


import org.mifosplatform.commands.handler.NewCommandSourceHandler;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.portfolio.transfer.service.TransferWritePlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TransferStaffToGroupCommandHandler  implements NewCommandSourceHandler {

    private final TransferWritePlatformService writePlatformService;

    @Autowired
    public TransferStaffToGroupCommandHandler(TransferWritePlatformService writePlatformService) {
        this.writePlatformService = writePlatformService;
    }

    @Override
    public CommandProcessingResult processCommand(JsonCommand command) {
       return this.writePlatformService.transferLoanOfficerToGroup(command.entityId(), command);
    }
}
