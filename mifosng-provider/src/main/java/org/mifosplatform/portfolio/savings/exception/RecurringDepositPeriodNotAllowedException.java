/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.savings.exception;

import org.mifosplatform.infrastructure.core.exception.AbstractPlatformDomainRuleException;

/**
 * Thrown when deposit amount is not between the minimum and maximum allowed
 * 
 */
public class RecurringDepositPeriodNotAllowedException extends AbstractPlatformDomainRuleException {

    public RecurringDepositPeriodNotAllowedException(final String paramName, Integer depositPeriodFrequency, String depositPeriodFrequencyType,
    		Integer recurringPeriodFrequency, String recurringPeriodFrequencyType) {
        super( "error.msg.deposit.term.is.less.than.recurring.period", paramName,
        		depositPeriodFrequency, depositPeriodFrequencyType, recurringPeriodFrequency, recurringPeriodFrequencyType);
    }
}