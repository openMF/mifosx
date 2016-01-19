package org.mifosplatform.portfolio.client.exception;

import org.mifosplatform.infrastructure.core.exception.AbstractPlatformResourceNotFoundException;

/**
 * A {@link RuntimeException} thrown when clientNonPerson resources are not found.
 */
public class ClientNonPersonNotFoundByClientIdException extends AbstractPlatformResourceNotFoundException {
	
	public ClientNonPersonNotFoundByClientIdException(final Long id) {
        super("error.msg.clientnonperson.id.invalid", "ClientNonPerson with client identifier " + id + " does not exist", id);
    }
}
