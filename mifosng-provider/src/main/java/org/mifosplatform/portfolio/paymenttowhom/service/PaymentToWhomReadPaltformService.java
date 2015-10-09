/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.paymenttowhom.service;

import java.util.Collection;

import org.mifosplatform.portfolio.paymenttowhom.data.PaymentToWhomData;

public interface PaymentToWhomReadPaltformService {

	Collection<PaymentToWhomData>retrieveAllPaymentToWhom();
	PaymentToWhomData retrieveOne(Long paymentToWhomId);
	
}
