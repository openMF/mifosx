package org.mifosplatform.portfolio.savings.exception;


import org.mifosplatform.infrastructure.core.exception.AbstractPlatformDomainRuleException;

public class SavingsOfficerUnassignmentDateException extends AbstractPlatformDomainRuleException {

    public SavingsOfficerUnassignmentDateException(final String postFix, final String defaultUserMessage,
            final Object... defaultUserMessageArgs) {
        super("error.msg.savings.savingsofficer.unassign.date." + postFix, defaultUserMessage, defaultUserMessageArgs);
    }
}
