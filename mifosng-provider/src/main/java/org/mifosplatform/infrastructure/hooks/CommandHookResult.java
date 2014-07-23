package org.mifosplatform.infrastructure.hooks;

public class CommandHookResult {
    private final Boolean isError;
    private final String errorMessage;

    public CommandHookResult(Boolean isError, String errorMessage) {
        this.isError = isError;
        this.errorMessage = errorMessage;
    }

    private static CommandHookResult success = new CommandHookResult(false,null);

    public static CommandHookResult createSuccess() { return success;};

    public String getErrorMessage() {
        return errorMessage;
    }

    public Boolean isError() {
        return this.isError;
    }
}
