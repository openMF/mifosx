/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.collectionsheet;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class CollectionSheetConstants {

    public static final String COLLECTIONSHEET_RESOURCE_NAME = "collectionsheet";

    public static final String localeParamName = "locale";
    public static final String dateFormatParamName = "dateFormat";

    public static final String transactionDateParamName = "transactionDate";
    public static final String actualDisbursementDateParamName = "actualDisbursementDate";
    public static final String bulkRepaymentTransactionsParamName = "bulkRepaymentTransactions";
    public static final String bulkDisbursementTransactionsParamName = "bulkDisbursementTransactions";
    public static final String noteParamName = "note";
    public static final String calendarIdParamName = "calendarId";

    // attendance parameters
    public static final String clientsAttendanceParamName = "clientsAttendance";
    public static final String clientIdParamName = "clientId";
    public static final String attendanceTypeParamName = "attendanceType";

    public static final String loanIdParamName = "loanId";
    public static final String transactionAmountParamName = "transactionAmount";

    public static final Set<String> COLLECTIONSHEET_REQUEST_DATA_PARAMETERS = new HashSet<String>(Arrays.asList(localeParamName,
            dateFormatParamName, transactionDateParamName, actualDisbursementDateParamName, bulkRepaymentTransactionsParamName,
            bulkDisbursementTransactionsParamName, noteParamName, calendarIdParamName, clientsAttendanceParamName));
}