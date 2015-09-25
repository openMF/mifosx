/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.paymenttowhom.handler;

import javax.transaction.Transactional;

import org.mifosplatform.commands.annotation.CommandType;
import org.mifosplatform.commands.handler.NewCommandSourceHandler;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.portfolio.paymenttowhom.service.PaymentToWhomWriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
@CommandType(entity = "PAYMENTTOWHOM", action = "DELETE")
public class DeletePaymentToWhomCommandHandler implements NewCommandSourceHandler{
	
	private final PaymentToWhomWriteService paymentToWhomWriteService;
	
	@Autowired
	public DeletePaymentToWhomCommandHandler(final PaymentToWhomWriteService paymentToWhomWriteService){
		this.paymentToWhomWriteService = paymentToWhomWriteService;
		
	}
	@Override
	@Transactional
	public CommandProcessingResult processCommand(JsonCommand command){
		return this.paymentToWhomWriteService.deletePaymentToWhom(command.entityId());
	}

}
