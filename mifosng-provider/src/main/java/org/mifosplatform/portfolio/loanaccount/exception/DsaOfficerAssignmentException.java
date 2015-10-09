/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.loanaccount.exception;

import org.mifosplatform.infrastructure.core.exception.AbstractPlatformDomainRuleException;

/**
 * Created by saranshsharma on 02/09/15.
 */
public class DsaOfficerAssignmentException extends AbstractPlatformDomainRuleException {

    public DsaOfficerAssignmentException(final Long loanId, final Long fromdsaOfficerId){
        super("error.msg.loan.not.assigned.to.loan.dsa", "Loan with identifier"  + loanId
                    + "is not assigned to dsa officer with identifier" + fromdsaOfficerId + "." , loanId);
    }
}
