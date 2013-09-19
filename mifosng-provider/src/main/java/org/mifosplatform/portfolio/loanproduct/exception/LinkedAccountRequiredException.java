package org.mifosplatform.portfolio.loanproduct.exception;

import org.mifosplatform.infrastructure.core.exception.AbstractPlatformDomainRuleException;


public class LinkedAccountRequiredException extends AbstractPlatformDomainRuleException {

    public LinkedAccountRequiredException(final String entity, String defaultUserMessage, Object... defaultUserMessageArgs) {
        super("error.msg." + entity + ".requires.linked.account", defaultUserMessage, defaultUserMessageArgs);
    }

}
