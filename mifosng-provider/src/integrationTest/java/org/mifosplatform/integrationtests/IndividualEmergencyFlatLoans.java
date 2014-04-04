package org.mifosplatform.integrationtests;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;
import org.mifosplatform.integrationtests.common.ClientHelper;
import org.mifosplatform.integrationtests.common.GroupHelper;
import org.mifosplatform.integrationtests.common.Utils;
import org.mifosplatform.integrationtests.common.loans.LoanApplicationTestBuilder;
import org.mifosplatform.integrationtests.common.loans.LoanProductTestBuilder;
import org.mifosplatform.integrationtests.common.loans.LoanTransactionHelper;

import com.jayway.restassured.builder.RequestSpecBuilder;
import com.jayway.restassured.builder.ResponseSpecBuilder;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.specification.RequestSpecification;
import com.jayway.restassured.specification.ResponseSpecification;
import org.junit.Assert;
import org.mifosplatform.integrationtests.common.loans.LoanStatusChecker;

/**
 * Group Loan Integration Test for checking Loan Application Repayment Schedule.
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class IndividualEmergencyFlatLoans {

    private ResponseSpecification responseSpec;
    private RequestSpecification requestSpec;
    private LoanTransactionHelper loanTransactionHelper;

    @Before
    public void setup() {
        Utils.initializeRESTAssured();
        this.requestSpec = new RequestSpecBuilder().setContentType(ContentType.JSON).build();
        this.requestSpec.header("Authorization", "Basic " + Utils.loginIntoServerAndGetBase64EncodedAuthenticationKey());
        this.responseSpec = new ResponseSpecBuilder().expectStatusCode(200).build();
    }

    @Test
    public void checkIndividualEmergencyFlatLoanCreateAndDisburseFlow() {
        this.loanTransactionHelper = new LoanTransactionHelper(this.requestSpec, this.responseSpec);
        
        final Integer clientID = ClientHelper.createClient(this.requestSpec, this.responseSpec);
        Assert.assertNotNull(clientID);

        final Integer loanProductID = createLoanProduct();
        Assert.assertNotNull(loanProductID);
        
        final Integer loanID = applyForLoanApplication(clientID, loanProductID);
        Assert.assertNotNull(loanID);
        
        // Before we even disburse the loan check the schedule to see if it makes sense:
        ArrayList<HashMap> loanSchedule = this.loanTransactionHelper.getLoanRepaymentSchedule(this.requestSpec, this.responseSpec,
                loanID);
        verifyLoanRepaymentSchedule(loanSchedule);

        // Now approve and disburse the loan:
        HashMap loanStatusHashMap = LoanStatusChecker.getStatusOfLoan(this.requestSpec, this.responseSpec, loanID);
        LoanStatusChecker.verifyLoanIsPending(loanStatusHashMap);

        System.out.println("-----------------------------------APPROVE LOAN-----------------------------------------");
        loanStatusHashMap = this.loanTransactionHelper.approveLoan("1 March 2014", loanID);
        LoanStatusChecker.verifyLoanIsApproved(loanStatusHashMap);
        LoanStatusChecker.verifyLoanIsWaitingForDisbursal(loanStatusHashMap);
        
        loanStatusHashMap = this.loanTransactionHelper.disburseLoan("6 March 2014", loanID);
        System.out.println("DISBURSE " + loanStatusHashMap);
        LoanStatusChecker.verifyLoanIsActive(loanStatusHashMap);
        
        // Check the schedule again:
        loanSchedule = this.loanTransactionHelper.getLoanRepaymentSchedule(this.requestSpec, this.responseSpec,
                loanID);
        verifyLoanRepaymentSchedule(loanSchedule);
    
        
    }

    private Integer createLoanProduct() {
        System.out.println("------------------------------CREATING NEW LOAN PRODUCT ---------------------------------------");
        final String loanProductJSON = new LoanProductTestBuilder() //
                .withPrincipal("10,000.00") // 
                .withNumberOfRepayments("1") //
                .withRepaymentAfterEvery("1") //
                .withinterestRatePerPeriod("25") //
                .withInterestRateFrequencyTypeAsMonths() //
                .withAmortizationTypeAsEqualInstallments() //
                .withInterestCalculationPeriodTypeAsRepaymentPeriod()
                .withRepaymentTypeAsMonth()
                .withInterestTypeAsFlat()
                .withGraceOnPrincipalPayment("0")
                .withGraceOnInterestPayment("0")
                .withGraceOnInterestCharged("0")
                .withDigitsAfterDecimal("0")
                .build();
        return this.loanTransactionHelper.getLoanProductId(loanProductJSON);
    }

    private Integer applyForLoanApplication(final Integer clientID, final Integer loanProductID) {
        System.out.println("--------------------------------APPLYING FOR LOAN APPLICATION--------------------------------");
        final String loanApplicationJSON = new LoanApplicationTestBuilder() //
                .withPrincipal("10,000.00") //
                .withLoanTermFrequency("1") //
                .withLoanTermFrequencyAsMonths() //
                .withNumberOfRepayments("1") //
                .withRepaymentEveryAfter("1") //
                .withRepaymentFrequencyTypeAsMonths() //
                .withInterestRatePerPeriod("25") //
                .withAmortizationTypeAsEqualInstallments() //
                .withInterestTypeAsFlatBalance() //
                .withInterestCalculationPeriodTypeSameAsRepaymentPeriod() //
                .withExpectedDisbursementDate("1 March 2014") //
                .withSubmittedOnDate("1 March 2014")
                .withRepaymentsStartingFromDate("1 April 2014")
                .withGraceOnPrincipalPayment("0")
                .withGraceOnInterestPayment("0")
                .withGraceOnInterestCharged("0") 
                .withLoanType("individual").build(clientID.toString(), loanProductID.toString());
        System.out.println(loanApplicationJSON);
        return this.loanTransactionHelper.getLoanId(loanApplicationJSON);
    }

    @SuppressWarnings("unchecked")
    private void verifyLoanRepaymentSchedule(final ArrayList<HashMap> loanSchedule) {
        System.out.println("--------------------VERIFYING THE PRINCIPAL DUES,INTEREST DUE AND DUE DATE--------------------------");

        // Now lets do the final installment:
        assertEquals("Checking for Due Date for payment", new ArrayList<Integer>(Arrays.asList(2014, 4, 1)),
                loanSchedule.get(1).get("dueDate"));
        assertEquals("Checking for Principal Due for payment", new Float("10000.00"), loanSchedule.get(1).get("principalDue"));
        assertEquals("Checking for Interest Due for payment", new Float("2500.00"), loanSchedule.get(1).get("interestOriginalDue"));
    }
}