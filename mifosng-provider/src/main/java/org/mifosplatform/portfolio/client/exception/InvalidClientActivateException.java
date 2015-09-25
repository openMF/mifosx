/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.client.exception;

import org.mifosplatform.infrastructure.core.exception.AbstractPlatformDomainRuleException;

public class InvalidClientActivateException extends AbstractPlatformDomainRuleException {
	
	/**
	 * {@link AbstractPlatformDomainRuleException} thrown an action to transition a
	 * client for violating a domain rule.
	 * 
	 */
	public InvalidClientActivateException(final String action, final String postfix, final String defaultUserMessage,
			final Object... defaultUserMessageArgs){
		super("error.msg.client" + action + "." + postfix,defaultUserMessage,defaultUserMessageArgs);
	}
	
}
