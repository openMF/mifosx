package org.mifosplatform.batch.command;

import java.util.concurrent.ConcurrentHashMap;

import org.mifosplatform.batch.command.internal.UnknownCommandStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 * Provides an appropriate CommandStrategy using the 'method' and 'resourceUrl'.
 * CommandStrategy bean is created using Spring Application Context. 
 * 
 * @author Rishabh Shukla
 * 
 * @see org.mifosplatform.batch.command.internal.UnknownCommandStrategy 
 */
@Component
public class CommandStrategyProvider {
	
	private final ApplicationContext applicationContext;
	private final ConcurrentHashMap<CommandContext, String> commandStrategies = new ConcurrentHashMap<>();
	
	/**
	 * Constructs a CommandStrategyProvider with argument of ApplicationContext type.
	 * It also initialize commandStrategies using init() function by filling it
	 * with available CommandStrategies in {@link org.mifosplatform.batch.command.internal}.
	 * 
	 * @param applicationContext
	 */
	@Autowired
	public CommandStrategyProvider(final ApplicationContext applicationContext) {
		
		//calls init() function of this class.
		init();
		
		this.applicationContext = applicationContext;
	}
	
	/**
	 * Returns an appropriate commandStrategy after determining it using
	 * the CommandContext of the request. If no such Strategy is found then
	 * a default strategy is returned back.
	 * 
	 * @param commandContext
	 * @return CommandStrategy
	 * @see org.mifosplatform.batch.command.internal.UnknownCommandStrategy 
	 */
	public CommandStrategy getCommandStrategy(final CommandContext commandContext) {
		
		if(this.commandStrategies.containsKey(commandContext)) {
			return (CommandStrategy) this.applicationContext.getBean(this.commandStrategies.get(commandContext));
		}
		
		return new UnknownCommandStrategy();		
	}
	
	/**
	 * Contains various available command strategies in {@link org.mifosplatform.batch.command.internal}.
	 * Any new command Strategy will have to be added within this function in order to initiate it
	 * within the constructor. 
	 */
	private void init() {
		this.commandStrategies.put(CommandContext.resource("clients").method("POST").build(), "CreateClientCommandStrategy");
	}
	
}