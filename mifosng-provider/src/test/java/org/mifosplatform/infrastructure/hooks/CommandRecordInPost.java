package org.mifosplatform.infrastructure.hooks;

import org.mifosplatform.commands.handler.CommandHook;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;

public class CommandRecordInPost implements CommandHook {
    CommandProcessingResult saved;

    public CommandProcessingResult getSaved() {
        return saved;
    }

    @Override
    public CommandHookResult preHook(JsonCommand command) {
        return CommandHookResult.createSuccess();
    }

    @Override
    public void postHook(JsonCommand command, CommandProcessingResult result) {
        saved = result;
    }
}
