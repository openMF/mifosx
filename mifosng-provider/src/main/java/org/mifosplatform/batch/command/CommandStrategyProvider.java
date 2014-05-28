package org.mifosplatform.batch.command;

import java.util.concurrent.ConcurrentHashMap;

import org.mifosplatform.batch.command.internal.UnknownCommandStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class CommandStrategyProvider {
	
	private final ApplicationContext applicationContext;
	private final ConcurrentHashMap<CommandContext, String> commandStrategies = new ConcurrentHashMap<>();
	
	@Autowired
	public CommandStrategyProvider(final ApplicationContext applicationContext) {
		
		this.applicationContext = applicationContext;
	}
	
	public CommandStrategy getCommandStrategy(final CommandContext commandContext){
		
		if(this.commandStrategies.containsKey(commandContext)) {
			return (CommandStrategy) this.applicationContext.getBean(this.commandStrategies.get(commandContext));
		}
		
		return new UnknownCommandStrategy();		
	}
	
	
}