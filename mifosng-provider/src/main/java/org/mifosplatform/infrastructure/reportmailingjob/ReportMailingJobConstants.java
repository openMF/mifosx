/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.infrastructure.reportmailingjob;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class ReportMailingJobConstants {

    // define the API resource entity name
    public static final String REPORT_MAILING_JOB_ENTITY_NAME = "REPORTMAILINGJOB";
    
    // general API resource request parameter constants
    public static final String LOCALE_PARAM_NAME = "locale";
    public static final String DATE_FORMAT_PARAM_NAME = "dateFormat";
    
    // parameter constants for create/update entity API request
    public static final String NAME_PARAM_NAME = "name";
    public static final String DESCRIPTION_PARAM_NAME = "description";
    public static final String START_DATE_TIME_PARAM_NAME = "startDateTime";
    public static final String RECURRENCE_PARAM_NAME = "recurrence";
    public static final String EMAIL_RECIPIENTS_PARAM_NAME = "emailRecipients";
    public static final String EMAIL_SUBJECT_PARAM_NAME = "emailSubject";
    public static final String EMAIL_MESSAGE_PARAM_NAME = "emailMessage";
    public static final String EMAIL_ATTACHMENT_FILE_FORMAT_ID_PARAM_NAME = "emailAttachmentFileFormatId";
    public static final String STRETCHY_REPORT_ID_PARAM_NAME = "stretchyReportId";
    public static final String STRETCHY_REPORT_PARAM_MAP_PARAM_NAME = "stretchyReportParamMap";
    public static final String IS_ACTIVE_PARAM_NAME = "isActive";
    
    // other parameter constants
    public static final String ID_PARAM_NAME = "id";
    public static final String EMAIL_ATTACHMENT_FILE_FORMAT_PARAM_NAME = "emailAttachmentFileFormat";
    public static final String PREVIOUS_RUN_DATE_TIME_PARAM_NAME = "previousRunDateTime";
    public static final String NEXT_RUN_DATE_TIME_PARAM_NAME = "nextRunDateTime";
    public static final String PREVIOUS_RUN_STATUS = "previousRunStatus";
    public static final String PREVIOUS_RUN_ERROR_LOG = "previousRunErrorLog";
    public static final String PREVIOUS_RUN_ERROR_MESSAGE = "previousRunErrorMessage";
    public static final String NUMBER_OF_RUNS = "numberOfRuns";
    public static final String STRETCHY_REPORT_PARAM_NAME = "stretchyReport";
    
    // list of permitted parameters for the create report mailing job request
    public static final Set<String> CREATE_REQUEST_PARAMETERS = new HashSet<>(Arrays.asList(LOCALE_PARAM_NAME, DATE_FORMAT_PARAM_NAME, 
            NAME_PARAM_NAME, DESCRIPTION_PARAM_NAME, RECURRENCE_PARAM_NAME, EMAIL_RECIPIENTS_PARAM_NAME, EMAIL_SUBJECT_PARAM_NAME, 
            EMAIL_MESSAGE_PARAM_NAME, EMAIL_ATTACHMENT_FILE_FORMAT_ID_PARAM_NAME, STRETCHY_REPORT_ID_PARAM_NAME, 
            STRETCHY_REPORT_PARAM_MAP_PARAM_NAME, IS_ACTIVE_PARAM_NAME, START_DATE_TIME_PARAM_NAME));
    
    // list of permitted parameters for the update report mailing job request
    public static final Set<String> UPDATE_REQUEST_PARAMETERS = new HashSet<>(Arrays.asList(LOCALE_PARAM_NAME, DATE_FORMAT_PARAM_NAME, 
            NAME_PARAM_NAME, DESCRIPTION_PARAM_NAME, RECURRENCE_PARAM_NAME, EMAIL_RECIPIENTS_PARAM_NAME, EMAIL_SUBJECT_PARAM_NAME, 
            EMAIL_MESSAGE_PARAM_NAME, EMAIL_ATTACHMENT_FILE_FORMAT_ID_PARAM_NAME, STRETCHY_REPORT_ID_PARAM_NAME, 
            STRETCHY_REPORT_PARAM_MAP_PARAM_NAME, IS_ACTIVE_PARAM_NAME, START_DATE_TIME_PARAM_NAME));
    
    // list of parameters that represent the properties of a report mailing job
    public static final Set<String> REPORT_MAILING_JOB_DATA_PARAMETERS = new HashSet<>(Arrays.asList(ID_PARAM_NAME, NAME_PARAM_NAME, 
            DESCRIPTION_PARAM_NAME, RECURRENCE_PARAM_NAME, EMAIL_RECIPIENTS_PARAM_NAME, EMAIL_SUBJECT_PARAM_NAME, EMAIL_MESSAGE_PARAM_NAME, 
            EMAIL_ATTACHMENT_FILE_FORMAT_PARAM_NAME, STRETCHY_REPORT_PARAM_NAME, STRETCHY_REPORT_PARAM_MAP_PARAM_NAME, IS_ACTIVE_PARAM_NAME, 
            START_DATE_TIME_PARAM_NAME, PREVIOUS_RUN_DATE_TIME_PARAM_NAME, NEXT_RUN_DATE_TIME_PARAM_NAME, PREVIOUS_RUN_STATUS, 
            PREVIOUS_RUN_ERROR_LOG, PREVIOUS_RUN_ERROR_MESSAGE, NUMBER_OF_RUNS));
    
    // report mailing job configuration names
    public static final String GMAIL_SMTP_SERVER = "GMAIL_SMTP_SERVER";
    public static final String GMAIL_SMTP_PORT = "GMAIL_SMTP_PORT";
    public static final String GMAIL_SMTP_USERNAME = "GMAIL_SMTP_USERNAME";
    public static final String GMAIL_SMTP_PASSWORD = "GMAIL_SMTP_PASSWORD";
}
