/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.infrastructure.reportmailingjob.exception;

import org.mifosplatform.infrastructure.core.exception.AbstractPlatformResourceNotFoundException;

/**
 * A {@link RuntimeException} thrown when report mailing job configuration resources are not found.
 **/
@SuppressWarnings("serial")
public class ReportMailingJobConfigurationNotFoundException extends AbstractPlatformResourceNotFoundException {

    public ReportMailingJobConfigurationNotFoundException(final String name) {
        super("error.msg.report.mailing.job.configuration.name.invalid", "Report mailing job configuration with name " + name + 
                " does not exist", name);
    }
}
