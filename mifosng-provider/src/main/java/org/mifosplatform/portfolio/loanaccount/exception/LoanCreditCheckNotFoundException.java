/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.loanaccount.exception;

import org.mifosplatform.infrastructure.core.exception.AbstractPlatformResourceNotFoundException;

/**
 * A {@link RuntimeException} thrown when loan credit check resources are not found.
 **/
@SuppressWarnings("serial")
public class LoanCreditCheckNotFoundException extends AbstractPlatformResourceNotFoundException {

    public LoanCreditCheckNotFoundException(final Long loanCreditCheckId) {
        super("error.msg.loan.credit.check.id.invalid", 
                "Loan credit check with identifier " + loanCreditCheckId + " does not exist", loanCreditCheckId);
    }
}
