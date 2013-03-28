/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.collateral.exception;

import org.mifosplatform.infrastructure.core.exception.AbstractPlatformDomainRuleException;

public class CollateralCannotBeCreatedException extends AbstractPlatformDomainRuleException {

    /*** enum of reasons of why Loan Charge cannot be waived **/
    public static enum LOAN_COLLATERAL_CANNOT_BE_CREATED_REASON {
        LOAN_NOT_IN_SUBMITTED_AND_PENDING_APPROVAL_STAGE;

        public String errorMessage() {
            if (name().toString().equalsIgnoreCase("LOAN_NOT_IN_SUBMITTED_AND_PENDING_APPROVAL_STAGE")) { return "This collateral cannot be created as the loan it is associated with is not in submitted and pending approval stage"; }
            return name().toString();
        }

        public String errorCode() {
            if (name().toString().equalsIgnoreCase("LOAN_NOT_IN_SUBMITTED_AND_PENDING_APPROVAL_STAGE")) { return "error.msg.loan.collateral.associated.loan.not.in.submitted.and.pending.approval.stage"; }
            return name().toString();
        }
    }

    public CollateralCannotBeCreatedException(final LOAN_COLLATERAL_CANNOT_BE_CREATED_REASON reason, final Long loanId) {
        super(reason.errorCode(), reason.errorMessage(), loanId);
    }
}
