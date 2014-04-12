package org.mifosplatform.infrastructure.smsgateway.exception;

import org.mifosplatform.infrastructure.core.exception.AbstractPlatformResourceNotFoundException;

/**
 * A {@link RuntimeException} thrown when a code is not found.
 */
public class SmsGatewayNotFoundException extends AbstractPlatformResourceNotFoundException{
	public SmsGatewayNotFoundException(final Long resourceId) {
        super("error.msg.smsgateway.identifier.not.found", "SMSGATEWAY with identifier `" + resourceId + "` does not exist", resourceId);
    }
}
