/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.paymenttowhom.exception;

import org.mifosplatform.infrastructure.core.exception.AbstractPlatformResourceNotFoundException;

public class PaymentToWhomNotFoundException extends AbstractPlatformResourceNotFoundException {
	
	public PaymentToWhomNotFoundException(final Long id){
		super ("error.msg.payment.to.whom.invalid", "Payment To Whom " + id + "does not exist", id);
	}

}
