/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.client.handler;

import org.mifosplatform.commands.annotation.CommandType;
import org.mifosplatform.commands.handler.NewCommandSourceHandler;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.portfolio.client.service.ClientAddressWritePlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service 
@CommandType(entity = "CLIENTADDRESS", action = "CREATE")
public class CreateClientAddressCommandHandler implements NewCommandSourceHandler {
	
	private final ClientAddressWritePlatformService clientAddressWritePlatformService;
	
	@Autowired
	public CreateClientAddressCommandHandler (final ClientAddressWritePlatformService clientAddressWritePlatformService){
		this.clientAddressWritePlatformService = clientAddressWritePlatformService;
		
	}
	
	@Transactional
	@Override
	public CommandProcessingResult processCommand(final JsonCommand command){
		
		return this.clientAddressWritePlatformService.addClientAddress(command.getClientId(), command);
	}

}
