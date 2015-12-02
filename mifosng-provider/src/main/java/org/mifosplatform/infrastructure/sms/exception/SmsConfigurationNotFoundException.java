/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.infrastructure.sms.exception;

import org.mifosplatform.infrastructure.core.exception.AbstractPlatformResourceNotFoundException;

/**
 * A {@link RuntimeException} thrown when a code is not found.
 */
public class SmsConfigurationNotFoundException extends AbstractPlatformResourceNotFoundException {

	public SmsConfigurationNotFoundException(final String name) {
		super("error.msg.sms.configuration.name.not.found", "SMS configuration with name " + name + " does not exist", name);
	}
}
