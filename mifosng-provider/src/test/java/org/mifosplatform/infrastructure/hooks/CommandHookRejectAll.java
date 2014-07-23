package org.mifosplatform.infrastructure.hooks;

import org.mifosplatform.commands.handler.CommandHook;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.springframework.stereotype.Component;

@Component
public class CommandHookRejectAll implements CommandHook {
    @Override
    public CommandHookResult preHook(JsonCommand command) {
        return new CommandHookResult(true,"Testing rejection");
    }

    @Override
    public void postHook(JsonCommand command, CommandProcessingResult result) {

    }
}
