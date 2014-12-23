package org.mifosplatform.useradministration.exception;

import org.mifosplatform.infrastructure.core.exception.AbstractPlatformDomainRuleException;


public class RoleAssociatedException extends AbstractPlatformDomainRuleException {

    public RoleAssociatedException(final String errorkey, final String errormsg, final Long id) {
        super(errorkey, errormsg, id);
    }
}