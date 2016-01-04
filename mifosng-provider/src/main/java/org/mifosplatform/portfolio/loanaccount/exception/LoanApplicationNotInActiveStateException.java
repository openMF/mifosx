/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.loanaccount.exception;

import org.mifosplatform.infrastructure.core.exception.AbstractPlatformDomainRuleException;


public class LoanApplicationNotInActiveStateException extends AbstractPlatformDomainRuleException{
	
	 public LoanApplicationNotInActiveStateException(final Long id) {
	        super("error.msg.loan.cannot.undo.last.disbursal.as.its.not.in.active.state", "Loan with identifier " + id
	                + " cannot undo last disbursal as its not in active state.", id);
	    }
}
