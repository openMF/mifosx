package org.mifosplatform.portfolio.savings.exception;

import org.mifosplatform.infrastructure.core.exception.AbstractPlatformDomainRuleException;

public class SavingsOfficerAssignmentDateException extends AbstractPlatformDomainRuleException {

    public SavingsOfficerAssignmentDateException(final String postFix, final String defaultUserMessage, final Object... defaultUserMessageArgs) {
        super("error.msg.savings.assignment.date." + postFix, defaultUserMessage, defaultUserMessageArgs);
    }

}