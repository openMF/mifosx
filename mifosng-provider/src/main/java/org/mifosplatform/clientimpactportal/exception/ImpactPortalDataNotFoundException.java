package org.mifosplatform.clientimpactportal.exception;

import org.mifosplatform.infrastructure.core.exception.AbstractPlatformResourceNotFoundException;


public class ImpactPortalDataNotFoundException extends AbstractPlatformResourceNotFoundException {

    public ImpactPortalDataNotFoundException(final String name) {
        super("error.msg", "Data with name " + name + " does not exist", name);
    }

}
