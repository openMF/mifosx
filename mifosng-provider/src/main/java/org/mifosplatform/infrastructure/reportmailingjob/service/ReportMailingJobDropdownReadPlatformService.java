/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.infrastructure.reportmailingjob.service;

import java.util.List;

import org.mifosplatform.infrastructure.core.data.EnumOptionData;

public interface ReportMailingJobDropdownReadPlatformService {
    /** 
     * retrieve a list of email attachment file format enumeration options  
     **/
    List<EnumOptionData> retrieveEmailAttachmentFileFormatOptions();
    
    /** 
     * retrieve a list of stretchy report param date enumeration options 
     **/
    List<EnumOptionData> retrieveStretchyReportDateOptions();
}
