/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.loanaccount.exception;

import org.mifosplatform.infrastructure.core.exception.AbstractPlatformResourceNotFoundException;

/**
 * A {@link RuntimeException} thrown when a loan does not pass a credit check with error severity
 **/
@SuppressWarnings("serial")
public class LoanCreditCheckFailedException extends AbstractPlatformResourceNotFoundException {
    public LoanCreditCheckFailedException(final Long loanId, final Long creditCheckId, final String userMessage) {
        super("error.msg.loan.credit.check.failed", userMessage);
    }
}
