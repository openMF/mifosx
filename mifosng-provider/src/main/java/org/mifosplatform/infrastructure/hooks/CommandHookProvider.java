package org.mifosplatform.infrastructure.hooks;

import org.mifosplatform.commands.handler.CommandHook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Iterator;

@Component
public class CommandHookProvider {
    private final ApplicationContext applicationContext;

    @Autowired
    public CommandHookProvider(final ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public CommandHook getCommandHook(final Class<? extends CommandHook> klass) {

        Iterator<? extends CommandHook> iterator = this.applicationContext.getBeansOfType(klass).values().iterator();
        if (iterator.hasNext()) {
            return iterator.next();
        } else {
            return null;
        }
    }


}
