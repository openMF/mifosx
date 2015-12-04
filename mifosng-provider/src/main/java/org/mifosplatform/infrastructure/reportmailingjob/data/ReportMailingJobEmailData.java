/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.infrastructure.reportmailingjob.data;

import java.io.File;

/** 
 * Immutable data object representing report mailing job email data. 
 **/
public class ReportMailingJobEmailData {
    private final String to;
    private final String text;
    private final String subject;
    private final File attachment;
    
    public ReportMailingJobEmailData(final String to, final String text, final String subject, final File attachment) {
        this.to = to;
        this.text = text;
        this.subject = subject;
        this.attachment = attachment;
    }

    /**
     * @return the to
     */
    public String getTo() {
        return to;
    }

    /**
     * @return the text
     */
    public String getText() {
        return text;
    }

    /**
     * @return the subject
     */
    public String getSubject() {
        return subject;
    }

    /**
     * @return the attachment
     */
    public File getAttachment() {
        return attachment;
    }
    
    @Override
    public String toString() {
        return "ReportMailingJobEmailData [to=" + to + ", text=" + text + ", subject=" + subject + ", attachment="
                + attachment + "]";
    }
}
