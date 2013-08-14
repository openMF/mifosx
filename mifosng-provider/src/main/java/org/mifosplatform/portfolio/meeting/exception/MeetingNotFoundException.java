/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.meeting.exception;

import org.mifosplatform.infrastructure.core.exception.AbstractPlatformResourceNotFoundException;

/**
 * A {@link RuntimeException} thrown when Meeting resources are not found.
 */
public class MeetingNotFoundException extends AbstractPlatformResourceNotFoundException {

    public MeetingNotFoundException(final Long id) {
        super("error.msg.meeting.id.invalid", "Meeting with identifier " + id + " does not exist", id);
    }
}