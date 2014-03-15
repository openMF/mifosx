package org.mifosplatform.organisation.staff.exception;

import org.mifosplatform.infrastructure.core.exception.AbstractPlatformResourceNotFoundException;


public class NoStaffLinkedException extends AbstractPlatformResourceNotFoundException {

    public NoStaffLinkedException(final Long id) {
        super("error.msg.staff.not.linked", "User with identifier " + id + " has no staff linked to it", id);
    }
}
