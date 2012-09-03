package org.mifosng.platform.exceptions;

/**
 * A {@link RuntimeException} thrown when staff resources are not found.
 */
public class StaffNotFoundException extends
		AbstractPlatformResourceNotFoundException {

	public StaffNotFoundException(final Long id) {
		super("error.msg.fund.id.invalid", "Staff with identifier " + id
				+ " does not exist", id);
	}
}