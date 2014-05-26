package org.mifosplatform.batch.service;

import org.springframework.context.ApplicationContext;

public class CommandStrategyFactory {
	
	private final ApplicationContext applicationContext;
	
	public CommandStrategyFactory(final ApplicationContext applicationContext) {
		
		this.applicationContext = applicationContext;
	}
	
	/*
	 * TO BE IMPLEMENTED: Implementation of getCommandStrategy(BatchRequest): CommandStrategy
	 */
}