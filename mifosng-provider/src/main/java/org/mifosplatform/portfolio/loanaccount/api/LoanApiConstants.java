/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.loanaccount.api;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public interface LoanApiConstants {

    public static final String emiAmountParameterName = "fixedEmiAmount";
    public static final String maxOutstandingBalanceParameterName = "maxOutstandingLoanBalance";
    public static final String disbursementDataParameterName = "disbursementData";
    public static final String disbursementDateParameterName = "expectedDisbursementDate";
    public static final String disbursementPrincipalParameterName = "principal";
    public static final String updatedDisbursementDateParameterName = "updatedExpectedDisbursementDate";
    public static final String updatedDisbursementPrincipalParameterName = "updatedPrincipal";
    public static final String disbursementIdParameterName = "id";
    public static final String loanChargeIdParameterName = "loanChargeId";
    public static final String principalDisbursedParameterName = "transactionAmount";
    public static final String chargesParameterName = "charges" ;
    
    public static final String approvedLoanAmountParameterName = "approvedLoanAmount";
    public static final String approvedOnDateParameterName = "approvedOnDate";
    public static final String noteParameterName = "note";
    public static final String localeParameterName = "locale";
    public static final String dateFormatParameterName = "dateFormat";
    public static final String rejectedOnDateParameterName = "rejectedOnDate";
    public static final String withdrawnOnDateParameterName = "withdrawnOnDate";

    // Interest recalculation related
    public static final String isInterestRecalculationEnabledParameterName = "isInterestRecalculationEnabled";
    public static final String daysInYearTypeParameterName = "daysInYearType";
    public static final String daysInMonthTypeParameterName = "daysInMonthType";
    public static final String interestRecalculationCompoundingMethodParameterName = "interestRecalculationCompoundingMethod";
    public static final String rescheduleStrategyMethodParameterName = "rescheduleStrategyMethod";
    
    // Floating interest rate related
    public static final String interestRateDifferentialParameterName = "interestRateDifferential";
    public static final String isFloatingInterestRateParameterName = "isFloatingInterestRate";
    
    // Error codes
    public static final String LOAN_CHARGE_CAN_NOT_BE_ADDED_WITH_INTEREST_CALCULATION_TYPE= "loancharge.with.calculation.type.interest.not.allowed";
    public static final String LOAN_CHARGE_CAN_NOT_BE_ADDED_WITH_PRINCIPAL_CALCULATION_TYPE= "loancharge.with.calculation.type.principal.not.allowed";
    public static final String DISBURSEMENT_DATE_START_WITH_ERROR = "first.disbursement.date.must.start.with.expected.disbursement.date";
    public static final String PRINCIPAL_AMOUNT_SHOULD_BE_SAME = "sum.of.multi.disburse.amounts.must.equal.with.total.principal";
    public static final String DISBURSEMENT_DATE_UNIQUE_ERROR = "disbursement.date.must.be.unique.for.tranches";
    public static final String ALREADY_DISBURSED = "can.not.change.disbursement.date";
    public static final String APPROVED_AMOUNT_IS_LESS_THAN_SUM_OF_TRANCHES = "sum.of.multi.disburse.amounts.must.be.equal.to.or.lesser.than.approved.principal";
    public static final String DISBURSEMENT_DATES_NOT_IN_ORDER = "disbursements.should.be.ordered.based.on.their.disbursement.dates";
    public static final String DISBURSEMENT_DATE_BEFORE_ERROR = "disbursement.date.of.tranche.cannot.be.before.expected.disbursement.date";
	
    public static final String isFloatingInterestRate = "isFloatingInterestRate";
	public static final String interestRateDifferential = "interestRateDifferential";
   
   // loan credit check related constants
    public static final String LOAN_CREDIT_CHECK_ENTITY_NAME = "LOANCREDITCHECK";
    public static final String ID_PARAM_NAME = "id";
    public static final String CREDIT_CHECK_ID_PARAM_NAME = "creditCheckId";
    public static final String lOAN_ID_PARAM_NAME = "loanId";
    public static final String EXPECTED_RESULT_PARAM_NAME = "expectedResult";
    public static final String ACTUAL_RESULT_PARAM_NAME = "actualResult";
    public static final String SEVERITY_LEVEL_PARAM_NAME = "severityLevel";
    public static final String MESSAGE_PARAM_NAME = "message";
    public static final String IS_ACTIVE_PARAM_NAME = "isActive";
    public static final String CREDIT_CHECK_DATA_OPTIONS_PARAM_NAME = "creditCheckDataOptions";
    public static final String TIMELINE_PARAM_NAME = "timeline";
    public static final Set<String> LOAN_CREDIT_CHECK_RESPONSE_DATA = new HashSet<>(Arrays.asList(ID_PARAM_NAME, CREDIT_CHECK_ID_PARAM_NAME, 
            lOAN_ID_PARAM_NAME, EXPECTED_RESULT_PARAM_NAME, ACTUAL_RESULT_PARAM_NAME, SEVERITY_LEVEL_PARAM_NAME, MESSAGE_PARAM_NAME, 
            IS_ACTIVE_PARAM_NAME, CREDIT_CHECK_DATA_OPTIONS_PARAM_NAME, TIMELINE_PARAM_NAME));
}
