package org.mifosplatform.organisation.dsa.exception;

import org.mifosplatform.infrastructure.core.exception.AbstractPlatformResourceNotFoundException;

public class DsaNotFoundException extends AbstractPlatformResourceNotFoundException {
	
	public DsaNotFoundException(final Long id){
		 super("error.msg.dsa.id.invalid", "DSA with identifier " + id + " does not exist", id);
	}
	

}
