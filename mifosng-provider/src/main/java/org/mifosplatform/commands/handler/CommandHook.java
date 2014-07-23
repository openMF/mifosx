package org.mifosplatform.commands.handler;

import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.hooks.CommandHookResult;

public interface CommandHook {
    public CommandHookResult preHook(JsonCommand command);
    public void postHook(JsonCommand command, CommandProcessingResult result);
}