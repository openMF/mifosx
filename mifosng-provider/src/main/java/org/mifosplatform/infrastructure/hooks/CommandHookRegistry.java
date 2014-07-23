package org.mifosplatform.infrastructure.hooks;


import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimaps;
import org.mifosplatform.commands.handler.CommandHook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommandHookRegistry {

    private final CommandHookProvider commandHookProvider;

    @Autowired
    public CommandHookRegistry(final CommandHookProvider commandHookProvider) {
        this.commandHookProvider = commandHookProvider;
    }

    private final static ListMultimap<CommandHookType,ResolvedHook> hooks = Multimaps.synchronizedListMultimap(ArrayListMultimap.<CommandHookType, ResolvedHook>create());

    public void register(final CommandHookType hookType, final Class<? extends CommandHook> hookClass ) throws IllegalAccessException, InstantiationException {
        hooks.put(hookType,new ResolvedHook(hookClass,commandHookProvider.getCommandHook(hookClass)));
    }

    public void deregister(final CommandHookType hookType, final Class<? extends CommandHook> hookClass) {
        for( ResolvedHook i : hooks.get(hookType) ) {
            if( i.getKlass().equals(hookClass) ) {
                hooks.remove(hookType,hookClass);
                return;
            }
        }
    }

    public List<ResolvedHook> getCommandHooks(final CommandHookType hookType) {
        return hooks.get(hookType);
    }
}
