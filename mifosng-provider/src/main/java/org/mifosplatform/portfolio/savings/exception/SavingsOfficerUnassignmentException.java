package org.mifosplatform.portfolio.savings.exception;

import org.mifosplatform.infrastructure.core.exception.AbstractPlatformDomainRuleException;

public class SavingsOfficerUnassignmentException extends AbstractPlatformDomainRuleException {

    public SavingsOfficerUnassignmentException(final Long accountId) {
        super("error.msg.savings.account.not.assigned.to.savings.officer", "Savings Account Identifier" + accountId + " is not assigned to any savings officer.");
    }
}
