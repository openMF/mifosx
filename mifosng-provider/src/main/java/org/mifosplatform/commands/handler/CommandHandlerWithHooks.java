package org.mifosplatform.commands.handler;

import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.exception.GeneralPlatformDomainRuleException;
import org.mifosplatform.infrastructure.hooks.CommandHookRegistry;
import org.mifosplatform.infrastructure.hooks.CommandHookResult;
import org.mifosplatform.infrastructure.hooks.CommandHookType;
import org.mifosplatform.infrastructure.hooks.ResolvedHook;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public abstract class CommandHandlerWithHooks implements NewCommandSourceHandler{


    private final CommandHookType commandHookType;

    public CommandHandlerWithHooks(final CommandHookType commandHookType) {
        this.commandHookType = commandHookType;
    }

    private CommandHookRegistry commandHookRegistry;

    @Autowired
    public void setCommandHookRegistry(CommandHookRegistry commandHookRegistry) {
        this.commandHookRegistry = commandHookRegistry;
    }

    public abstract CommandProcessingResult actualProcessCommand(final JsonCommand command);

    public CommandProcessingResult processCommand(JsonCommand command) {
        List<ResolvedHook> hooks = commandHookRegistry.getCommandHooks(commandHookType);
        for(ResolvedHook resolvedHook: hooks ) {
            CommandHookResult result = resolvedHook.getHook().preHook(command);
            if( result.isError()) {
                final String defaultUserMessage = "Error in pre-hook " + commandHookType.toString();
                throw new GeneralPlatformDomainRuleException(
                        "error.prehook",
                        result.getErrorMessage()!=null ? result.getErrorMessage(): defaultUserMessage,
                command.getClientId());
            }
        }

        CommandProcessingResult result = actualProcessCommand(command);

        for(ResolvedHook resolvedHook: hooks ) {
            resolvedHook.getHook().postHook(command,result);
        }
        return result;
    }
}