package org.mifosplatform.portfolio.loanproduct.exception;

import org.mifosplatform.infrastructure.core.exception.AbstractPlatformDomainRuleException;


public class LoanProductDateException extends AbstractPlatformDomainRuleException{

    public LoanProductDateException(String postFix, String defaultUserMessage,
            Object... defaultUserMessageArgs) {
        super("error.msg.loan.product." + postFix, defaultUserMessage, defaultUserMessageArgs);
    }

}
