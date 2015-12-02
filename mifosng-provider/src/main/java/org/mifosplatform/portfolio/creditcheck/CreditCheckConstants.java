/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.creditcheck;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class CreditCheckConstants {

    public static final String CREDIT_CHECK_ENTITY_NAME = "CREDITCHECK";
    
    // general API resource request parameter constants
    public static final String LOCALE_PARAM_NAME = "locale";
    public static final String DATE_FORMAT_PARAM_NAME = "dateFormat";
    
    // parameters constants for create/update entity API request
    public static final String NAME_PARAM_NAME = "name";
    public static final String RELATED_ENTITY_PARAM_NAME = "relatedEntity";
    public static final String EXPECTED_RESULT_PARAM_NAME = "expectedResult";
    public static final String SEVERITY_LEVEL_PARAM_NAME = "severityLevel";
    public static final String STRETCHY_REPORT_ID_PARAM_NAME = "stretchyReportId";
    public static final String STRETCHY_REPORT_PARAM_MAP_PARAM_NAME = "stretchyReportParamMap";
    public static final String MESSAGE_PARAM_NAME = "message";
    public static final String IS_ACTIVE_PARAM_NAME = "isActive";
    
    // other parameters
    public static final String ID_PARAM_NAME = "id";
    public static final String CREDIT_CHECKS_PARAM_NAME = "creditChecks";
    public static final String CREDIT_CHECK_OPTIONS_PARAM_NAME = "creditCheckOptions";
    
    // list of permitted parameters for the create credit check request
    public static final Set<String> CREATE_REQUEST_PARAMETERS = new HashSet<>(Arrays.asList(LOCALE_PARAM_NAME, 
            DATE_FORMAT_PARAM_NAME, NAME_PARAM_NAME, RELATED_ENTITY_PARAM_NAME, EXPECTED_RESULT_PARAM_NAME, 
            SEVERITY_LEVEL_PARAM_NAME, STRETCHY_REPORT_ID_PARAM_NAME, STRETCHY_REPORT_PARAM_MAP_PARAM_NAME, 
            MESSAGE_PARAM_NAME, IS_ACTIVE_PARAM_NAME));
    
    // list of permitted parameters for the update credit check request
    public static final Set<String> UPDATE_REQUEST_PARAMETERS = new HashSet<>(Arrays.asList(LOCALE_PARAM_NAME, 
            DATE_FORMAT_PARAM_NAME, NAME_PARAM_NAME, EXPECTED_RESULT_PARAM_NAME, SEVERITY_LEVEL_PARAM_NAME, 
            STRETCHY_REPORT_ID_PARAM_NAME, STRETCHY_REPORT_PARAM_MAP_PARAM_NAME, MESSAGE_PARAM_NAME, IS_ACTIVE_PARAM_NAME));
    
    // list of parameters that represent the properties of a credit check object
    public static final Set<String> CREDIT_CHECK_DATA_PARAMETERS = new HashSet<>(Arrays.asList(ID_PARAM_NAME, 
            NAME_PARAM_NAME, RELATED_ENTITY_PARAM_NAME, EXPECTED_RESULT_PARAM_NAME, SEVERITY_LEVEL_PARAM_NAME, 
            STRETCHY_REPORT_ID_PARAM_NAME, STRETCHY_REPORT_PARAM_MAP_PARAM_NAME, MESSAGE_PARAM_NAME, IS_ACTIVE_PARAM_NAME));

    // stretchy report SQL parameter patterns
    public static final String CLIENT_ID_PARAM_PATTERN = "${clientId}";
    public static final String GROUP_ID_PARAM_PATTERN = "${loanId}";
    public static final String LOAN_ID_PARAM_PATTERN = "${groupId}";
    public static final String USER_ID_PARAM_PATTERN = "${userId}";
    public static final String STAFF_ID_PARAM_PATTERN = "${staffId}";
    public static final String OFFICE_ID_PARAM_PATTERN = "${officeId}";
    public static final String PRODUCT_ID_PARAM_PATTERN = "${productId}";
}
