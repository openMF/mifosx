package org.mifosplatform.portfolio.savings.exception;

import org.mifosplatform.infrastructure.core.exception.AbstractPlatformDomainRuleException;

public class SavingsOfficerAssignmentException extends AbstractPlatformDomainRuleException {

    public SavingsOfficerAssignmentException(final Long accountId, final Long fromSavingsOfficerId) {
        super("error.msg.savings.account.not.assigned.to.savings.officer", "Savings Account Identifier " + accountId
                + " is not assigned to Savings Officer with identifier " + fromSavingsOfficerId + ".", accountId);
    }
}
