package org.mifosplatform.portfolio.loanaccount.exception;

import org.mifosplatform.infrastructure.core.exception.AbstractPlatformDomainRuleException;

public class UndoLastTrancheDisbursementException extends AbstractPlatformDomainRuleException{

	public UndoLastTrancheDisbursementException(final Object... defaultUserMessageArgs) {
		super("error.msg.cannot.undo.last.disbursal.after.repayments"," Cannot undo last disbursement after repayments.",
				defaultUserMessageArgs);
		
	}

}
