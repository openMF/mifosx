/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.paymenttowhom.domain;

import org.mifosplatform.portfolio.paymenttowhom.exception.PaymentToWhomNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PaymentToWhomRepositoryWrapper {
	
	private final PaymentToWhomRepository repository;
	
	@Autowired
	public PaymentToWhomRepositoryWrapper(final PaymentToWhomRepository repository){
		
		this.repository = repository;
		
	}
	
	public PaymentToWhom findOneWithNotFoundDetection(final Long id){
		final PaymentToWhom paymentToWhom = this.repository.findOne(id);
		if (paymentToWhom == null) {throw new PaymentToWhomNotFoundException(id); }
		return paymentToWhom;
		}
	}
	

