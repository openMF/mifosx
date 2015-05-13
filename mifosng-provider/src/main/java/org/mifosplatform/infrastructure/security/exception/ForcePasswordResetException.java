/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.mifosplatform.infrastructure.security.exception;

import org.mifosplatform.infrastructure.core.exception.AbstractPlatformDomainRuleException;

public class ForcePasswordResetException extends AbstractPlatformDomainRuleException {
	
	public ForcePasswordResetException(final String defaultUserMessage, final String entity, final Object... defaultUserMessageArgs){
		super("error.msg." + entity, defaultUserMessage, defaultUserMessageArgs);
	}

}
