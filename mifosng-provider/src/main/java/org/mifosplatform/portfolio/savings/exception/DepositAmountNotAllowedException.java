/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.savings.exception;

import java.math.BigDecimal;

import org.mifosplatform.infrastructure.core.exception.AbstractPlatformDomainRuleException;

/**
 * Thrown when deposit amount is not between the minimum and maximum allowed
 * 
 */
public class DepositAmountNotAllowedException extends AbstractPlatformDomainRuleException {

    public DepositAmountNotAllowedException(final String paramName, final BigDecimal depositmount, final BigDecimal mininumAmountAllowed,
    		final BigDecimal maximumAmountAllowed) {
        super( "error.msg.savingsaccount.transaction.insufficient.account.balance.withdraw", paramName,
        		mininumAmountAllowed, maximumAmountAllowed);
    }
}