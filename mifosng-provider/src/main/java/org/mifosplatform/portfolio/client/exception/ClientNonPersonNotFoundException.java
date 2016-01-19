package org.mifosplatform.portfolio.client.exception;

import org.mifosplatform.infrastructure.core.exception.AbstractPlatformResourceNotFoundException;

/**
 * A {@link RuntimeException} thrown when clientNonPerson resources are not found.
 */
public class ClientNonPersonNotFoundException extends AbstractPlatformResourceNotFoundException {
	
	public ClientNonPersonNotFoundException(final Long id) {
        super("error.msg.clientnonperson.id.invalid", "ClientNonPerson with identifier " + id + " does not exist", id);
    }
	
}
