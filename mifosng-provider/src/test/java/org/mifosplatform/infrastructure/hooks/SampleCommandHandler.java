package org.mifosplatform.infrastructure.hooks;

import org.mifosplatform.commands.handler.CommandHandlerWithHooks;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.springframework.stereotype.Component;

@Component
public class SampleCommandHandler extends CommandHandlerWithHooks {
    public static CommandHookType commandHookType = CommandHookType.ActivateCenter;

    public SampleCommandHandler() {
        super(commandHookType);
    }

    @Override
    public CommandProcessingResult actualProcessCommand(JsonCommand command) {
        return CommandProcessingResult.commandOnlyResult(1L);
    }
}
