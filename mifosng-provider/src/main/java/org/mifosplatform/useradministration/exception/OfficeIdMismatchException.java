package org.mifosplatform.useradministration.exception;

import org.mifosplatform.infrastructure.core.exception.AbstractPlatformResourceNotFoundException;


public class OfficeIdMismatchException extends AbstractPlatformResourceNotFoundException {

    public OfficeIdMismatchException(final Long staffOfficeId, final Long userOfficeId) {
        super("error.msg.user.client.officeId.mismatch", "officeId of User must match officeId of Staff", "User.OfficeId = "+userOfficeId, "Staff.OfficeId = "+staffOfficeId);
    }

    
    
}
