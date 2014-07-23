package org.mifosplatform.infrastructure.hooks;

import org.mifosplatform.commands.handler.CommandHook;

public class ResolvedHook {
    Class<? extends CommandHook> klass;
    CommandHook hook;

    public ResolvedHook(Class<? extends CommandHook> klass, final CommandHook hook)  {
        this.klass = klass;
        this.hook = hook;
    }

    public Class<? extends CommandHook> getKlass() {
        return klass;
    }

    public CommandHook getHook() {
        return hook;
    }

}
