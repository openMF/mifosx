/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.infrastructure.reportmailingjob.service;

import org.mifosplatform.infrastructure.reportmailingjob.data.ReportMailingJobEmailData;

public interface ReportMailingJobEmailService {
    void sendEmailWithAttachment(ReportMailingJobEmailData reportMailingJobEmailData);
}
