package org.mifosplatform.batch.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class CommandStrategyFactory {
	
	private final ApplicationContext applicationContext;
	
	@Autowired
	public CommandStrategyFactory(final ApplicationContext applicationContext) {
		
		this.applicationContext = applicationContext;
	}
	
	/*
	 * TO BE IMPLEMENTED: Implementation of getCommandStrategy(BatchRequest): CommandStrategy
	 */
}