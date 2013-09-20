/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.loanaccount.exception;

import org.mifosplatform.infrastructure.core.exception.AbstractPlatformDomainRuleException;

public class LoanOfficerUnassignmentException extends AbstractPlatformDomainRuleException {

    public LoanOfficerUnassignmentException(final Long loanId) {
        super("error.msg.loan.not.assigned.to.loan.officer", "Loan with identifier " + loanId + " is not assigned to any loan officer.",
                loanId);
    }
}
