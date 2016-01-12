/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.integrationtests;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mifosplatform.integrationtests.common.ClientHelper;
import org.mifosplatform.integrationtests.common.Utils;
import org.mifosplatform.integrationtests.common.accounting.Account;
import org.mifosplatform.integrationtests.common.loans.LoanApplicationTestBuilder;
import org.mifosplatform.integrationtests.common.loans.LoanProductTestBuilder;
import org.mifosplatform.integrationtests.common.loans.LoanStatusChecker;
import org.mifosplatform.integrationtests.common.loans.LoanTransactionHelper;

import com.jayway.restassured.builder.RequestSpecBuilder;
import com.jayway.restassured.builder.ResponseSpecBuilder;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.specification.RequestSpecification;
import com.jayway.restassured.specification.ResponseSpecification;

@SuppressWarnings("rawtypes")
public class LoanRepaymentRescheduleAtDisbursementTest {

	private ResponseSpecification responseSpec;
    private RequestSpecification requestSpec;
    private LoanTransactionHelper loanTransactionHelper;
    private LoanApplicationApprovalTest loanApplicationApprovalTest;
    private ResponseSpecification generalResponseSpec;
    
    @Before
    public void setup() {
        Utils.initializeRESTAssured();
        this.requestSpec = new RequestSpecBuilder().setContentType(ContentType.JSON).build();
        this.requestSpec.header("Authorization", "Basic " + Utils.loginIntoServerAndGetBase64EncodedAuthenticationKey());
        this.responseSpec = new ResponseSpecBuilder().expectStatusCode(200).build();
        this.loanTransactionHelper = new LoanTransactionHelper(this.requestSpec, this.responseSpec);
        this.loanApplicationApprovalTest = new LoanApplicationApprovalTest();
        this.generalResponseSpec = new ResponseSpecBuilder().build();
    }
    
	@SuppressWarnings("unchecked")
	@Test
    public void testLoanRepaymentRescheduleAtDisbursement(){
    	
        final String approvalAmount = "10000";
        final String approveDate = "01 March 2015";
        final String expectedDisbursementDate = "01 March 2015";
        final String disbursementDate = "01 March 2015";
        final String nextRepaymentDate = "15 March 2015";
        final String adjustRepaymentDate = "16 March 2015";
         
        // CREATE CLIENT
    	final Integer clientID = ClientHelper.createClient(this.requestSpec, this.responseSpec, "01 January 2014");
        System.out.println("---------------------------------CLIENT CREATED WITH ID---------------------------------------------------"
                + clientID);

        // CREATE LOAN MULTIDISBURSAL PRODUCT WITH INTEREST RECALCULATION 
        final Integer loanProductID = createLoanProductWithInterestRecalculation(LoanProductTestBuilder.RBI_INDIA_STRATEGY,
                LoanProductTestBuilder.RECALCULATION_COMPOUNDING_METHOD_NONE,
                LoanProductTestBuilder.RECALCULATION_STRATEGY_REDUCE_NUMBER_OF_INSTALLMENTS,
                LoanProductTestBuilder.RECALCULATION_FREQUENCY_TYPE_SAME_AS_REPAYMENT_PERIOD, "0", null,
                LoanProductTestBuilder.INTEREST_APPLICABLE_STRATEGY_ON_PRE_CLOSE_DATE, null);
        
        // CREATE TRANCHES
        List<HashMap> createTranches = new ArrayList<>();
        createTranches.add(this.loanApplicationApprovalTest.createTrancheDetail("01 March 2015", "5000"));
        createTranches.add(this.loanApplicationApprovalTest.createTrancheDetail("01 May 2015", "5000"));
    	
        // APPROVE TRANCHES
        List<HashMap> approveTranches = new ArrayList<>();
        approveTranches.add(this.loanApplicationApprovalTest.createTrancheDetail("01 March 2015", "5000"));
        approveTranches.add(this.loanApplicationApprovalTest.createTrancheDetail("01 May 2015", "5000"));
        
        // APPLY FOR TRANCHE LOAN WITH INTEREST RECALCULATION 
        final Integer loanID = applyForLoanApplicationForInterestRecalculation(clientID, loanProductID, disbursementDate,
                null, LoanApplicationTestBuilder.RBI_INDIA_STRATEGY, new ArrayList<HashMap>(0), createTranches);
        
        HashMap loanStatusHashMap = LoanStatusChecker.getStatusOfLoan(this.requestSpec, this.responseSpec, loanID);
        
        // VALIDATE THE LOAN STATUS
        LoanStatusChecker.verifyLoanIsPending(loanStatusHashMap);

        System.out.println("-----------------------------------APPROVE LOAN-----------------------------------------------------------");
        loanStatusHashMap = this.loanTransactionHelper.approveLoanWithApproveAmount(approveDate, expectedDisbursementDate, approvalAmount,
                loanID, approveTranches);
        
        // VALIDATE THE LOAN IS APPROVED
        LoanStatusChecker.verifyLoanIsApproved(loanStatusHashMap);
        LoanStatusChecker.verifyLoanIsWaitingForDisbursal(loanStatusHashMap);
        
        // DISBURSE A FIRST TRANCHE
        this.loanTransactionHelper.disburseLoanWithRepaymentReschedule(disbursementDate, loanID, nextRepaymentDate, adjustRepaymentDate);
        loanStatusHashMap = LoanStatusChecker.getStatusOfLoan(this.requestSpec, this.responseSpec, loanID);
        
        ArrayList<HashMap> loanRepaymnetSchedule = this.loanTransactionHelper.getLoanRepaymentSchedule(requestSpec, generalResponseSpec, loanID);
        HashMap firstInstallement  = loanRepaymnetSchedule.get(1);
        Map<String, Object> expectedvalues = new HashMap<>(3);
        Calendar date = Calendar.getInstance(Utils.getTimeZoneOfTenant());
        date.set(2015, Calendar.MARCH, 16);
        expectedvalues.put("dueDate", getDateAsArray(date, 0));
        expectedvalues.put("principalDue", "834.71");
        expectedvalues.put("interestDue", "49.32");
        expectedvalues.put("feeChargesDue", "0");
        expectedvalues.put("penaltyChargesDue", "0");
        expectedvalues.put("totalDueForPeriod", "884.03");
        
        // VALIDATE REPAYMENT SCHEDULE
        verifyLoanRepaymentSchedule(firstInstallement, expectedvalues);
        
    }
	
	private void verifyLoanRepaymentSchedule(final HashMap firstInstallement, final Map<String, Object> expectedvalues) {
       
		assertEquals(expectedvalues.get("dueDate"), firstInstallement.get("dueDate"));
		assertEquals(String.valueOf(expectedvalues.get("principalDue")), String.valueOf(firstInstallement.get("principalDue")));
		assertEquals(String.valueOf(expectedvalues.get("interestDue")), String.valueOf(firstInstallement.get("interestDue")));
		assertEquals(String.valueOf(expectedvalues.get("feeChargesDue")), String.valueOf(firstInstallement.get("feeChargesDue")));
		assertEquals(String.valueOf(expectedvalues.get("penaltyChargesDue")), String.valueOf(firstInstallement.get("penaltyChargesDue")));
		assertEquals(String.valueOf(expectedvalues.get("totalDueForPeriod")), String.valueOf(firstInstallement.get("totalDueForPeriod")));

    }
    
    private Integer createLoanProductWithInterestRecalculation(final String repaymentStrategy,
            final String interestRecalculationCompoundingMethod, final String rescheduleStrategyMethod,
            final String recalculationRestFrequencyType, final String recalculationRestFrequencyInterval,
            final String recalculationRestFrequencyDate, final String preCloseInterestCalculationStrategy, final Account[] accounts) {
        final String recalculationCompoundingFrequencyType = null;
        final String recalculationCompoundingFrequencyInterval = null;
        final String recalculationCompoundingFrequencyDate = null;
        return createLoanProductWithInterestRecalculation(repaymentStrategy, interestRecalculationCompoundingMethod,
                rescheduleStrategyMethod, recalculationRestFrequencyType, recalculationRestFrequencyInterval,
                recalculationRestFrequencyDate, recalculationCompoundingFrequencyType, recalculationCompoundingFrequencyInterval,
                recalculationCompoundingFrequencyDate, preCloseInterestCalculationStrategy, accounts, null, false);
    }
    
    private Integer createLoanProductWithInterestRecalculation(final String repaymentStrategy,
            final String interestRecalculationCompoundingMethod, final String rescheduleStrategyMethod,
            final String recalculationRestFrequencyType, final String recalculationRestFrequencyInterval,
            final String recalculationRestFrequencyDate, final String recalculationCompoundingFrequencyType,
            final String recalculationCompoundingFrequencyInterval, final String recalculationCompoundingFrequencyDate,
            final String preCloseInterestCalculationStrategy, final Account[] accounts, final String chargeId,
            boolean isArrearsBasedOnOriginalSchedule) {
        System.out.println("------------------------------CREATING NEW LOAN PRODUCT ---------------------------------------");
        LoanProductTestBuilder builder = new LoanProductTestBuilder()
                .withPrincipal("10000.00")
                .withNumberOfRepayments("12")
                .withRepaymentAfterEvery("2")
                .withRepaymentTypeAsWeek()
                .withinterestRatePerPeriod("2")
                .withInterestRateFrequencyTypeAsMonths()
                .withTranches(true)
                .withRepaymentStrategy(repaymentStrategy)
                .withInterestTypeAsDecliningBalance()
                .withInterestRecalculationDetails(interestRecalculationCompoundingMethod, rescheduleStrategyMethod,
                        preCloseInterestCalculationStrategy)
                .withInterestRecalculationRestFrequencyDetails(recalculationRestFrequencyType, recalculationRestFrequencyInterval,
                        recalculationRestFrequencyDate)
                .withInterestRecalculationCompoundingFrequencyDetails(recalculationCompoundingFrequencyType,
                        recalculationCompoundingFrequencyInterval, recalculationCompoundingFrequencyDate);
        if (accounts != null) {
            builder = builder.withAccountingRulePeriodicAccrual(accounts);
        }

        if (isArrearsBasedOnOriginalSchedule) builder = builder.withArrearsConfiguration();

        final String loanProductJSON = builder.build(chargeId);
        return this.loanTransactionHelper.getLoanProductId(loanProductJSON);
    }
    
    private Integer applyForLoanApplicationForInterestRecalculation(final Integer clientID, final Integer loanProductID,
            final String disbursementDate, final String restStartDate, final String repaymentStrategy, final List<HashMap> charges, List<HashMap> tranches) {
        final String graceOnInterestPayment = null;
        final String compoundingStartDate = null;
        final String graceOnPrincipalPayment = null;
        return applyForLoanApplicationForInterestRecalculation(clientID, loanProductID, disbursementDate, restStartDate,
                compoundingStartDate, repaymentStrategy, charges, graceOnInterestPayment, graceOnPrincipalPayment,tranches);
    }
    
    private Integer applyForLoanApplicationForInterestRecalculation(final Integer clientID, final Integer loanProductID,
            final String disbursementDate, final String restStartDate, final String compoundingStartDate, final String repaymentStrategy,
            final List<HashMap> charges, final String graceOnInterestPayment, final String graceOnPrincipalPayment, List<HashMap> tranches) {
        System.out.println("--------------------------------APPLYING FOR LOAN APPLICATION--------------------------------");
        final String loanApplicationJSON = new LoanApplicationTestBuilder() //
                .withPrincipal("10000.00") //
                .withLoanTermFrequency("24") //
                .withLoanTermFrequencyAsWeeks() //
                .withNumberOfRepayments("12") //
                .withRepaymentEveryAfter("2") //
                .withRepaymentFrequencyTypeAsWeeks() //
                .withInterestRatePerPeriod("2") //
                .withAmortizationTypeAsEqualInstallments() //
                .withTranches(tranches)
                .withFixedEmiAmount("") //
                .withInterestTypeAsDecliningBalance() //
                .withInterestCalculationPeriodTypeAsDays() //
                .withInterestCalculationPeriodTypeAsDays() //
                .withExpectedDisbursementDate(disbursementDate) //
                .withSubmittedOnDate(disbursementDate) //
                .withRestFrequencyDate(restStartDate)//
                .withwithRepaymentStrategy(repaymentStrategy) //
                .withCharges(charges)//
                .build(clientID.toString(), loanProductID.toString(), null);
        return this.loanTransactionHelper.getLoanId(loanApplicationJSON);
    }
    
    private List getDateAsArray(Calendar date, int addPeriod) {
        return getDateAsArray(date, addPeriod, Calendar.DAY_OF_MONTH);
    }

    private List getDateAsArray(Calendar date, int addvalue, int type) {
    	date.add(type, addvalue);
        return new ArrayList<>(Arrays.asList(date.get(Calendar.YEAR), date.get(Calendar.MONTH) + 1,
        		date.get(Calendar.DAY_OF_MONTH)));
    }
}
