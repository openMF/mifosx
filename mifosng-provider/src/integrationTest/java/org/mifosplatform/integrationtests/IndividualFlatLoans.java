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
public class IndividualFlatLoans {

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
    public void checkIndividualFlatLoanCreateAndDisburseFlow() {
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
                .withNumberOfRepayments("24") //
                .withRepaymentAfterEvery("1") //
                .withinterestRatePerPeriod("20") //
                .withInterestRateFrequencyTypeAsYear() //
                .withAmortizationTypeAsEqualInstallments() //
                .withInterestCalculationPeriodTypeAsRepaymentPeriod()
                .withRepaymentTypeAsWeek()
                .withInterestTypeAsFlat()
                .withGraceOnPrincipalPayment("2")
                .withGraceOnInterestPayment("2")
                .withGraceOnInterestCharged("0")
                .withDigitsAfterDecimal("0")
                .build();
        return this.loanTransactionHelper.getLoanProductId(loanProductJSON);
    }

    private Integer applyForLoanApplication(final Integer clientID, final Integer loanProductID) {
        System.out.println("--------------------------------APPLYING FOR LOAN APPLICATION--------------------------------");
        final String loanApplicationJSON = new LoanApplicationTestBuilder() //
                .withPrincipal("10,000.00") //
                .withLoanTermFrequency("24") //
                .withLoanTermFrequencyAsWeeks() //
                .withNumberOfRepayments("24") //
                .withRepaymentEveryAfter("1") //
                .withRepaymentFrequencyTypeAsWeeks() //
                .withInterestRatePerPeriod("20") //
                .withAmortizationTypeAsEqualInstallments() //
                .withInterestTypeAsFlatBalance() //
                .withInterestCalculationPeriodTypeSameAsRepaymentPeriod() //
                .withExpectedDisbursementDate("5 March 2014") //
                .withSubmittedOnDate("1 March 2014")
                .withRepaymentsStartingFromDate("12 March 2014")
                .withGraceOnPrincipalPayment("2")
                .withGraceOnInterestPayment("2")
                .withGraceOnInterestCharged("0") 
                .withLoanType("individual").build(clientID.toString(), loanProductID.toString());
        System.out.println(loanApplicationJSON);
        return this.loanTransactionHelper.getLoanId(loanApplicationJSON);
    }

    @SuppressWarnings("unchecked")
    private void verifyLoanRepaymentSchedule(final ArrayList<HashMap> loanSchedule) {
        System.out.println("--------------------VERIFYING THE PRINCIPAL DUES,INTEREST DUE AND DUE DATE--------------------------");

        assertEquals("Checking for Due Date for 1st Week", new ArrayList<Integer>(Arrays.asList(2014, 3, 12)),
                loanSchedule.get(1).get("dueDate"));
        assertEquals("Checking for Total Due for 1st Week", new Integer("0"), loanSchedule.get(1).get("totalOriginalDueForPeriod"));

        assertEquals("Checking for Due Date for 2nd Week", new ArrayList<Integer>(Arrays.asList(2014, 3, 19)),
                loanSchedule.get(2).get("dueDate"));
        assertEquals("Checking for Total Due for 2nd Week", new Integer("0"), loanSchedule.get(2).get("totalOriginalDueForPeriod"));

        // Loop through installment 3 up to 23:
        int installment = 3;
        while(installment <= 23)
        {
            assertEquals("Checking for Principal Due for "+ installment +" Week", new Float("455.00"), loanSchedule.get(installment).get("principalDue"));
            assertEquals("Checking for Interest Due for "+ installment +" Week", new Float("42.00"), loanSchedule.get(installment).get("interestOriginalDue"));
            
            installment++;
        }

        // Now lets do the final installment:
        assertEquals("Checking for Due Date for 24 Week", new ArrayList<Integer>(Arrays.asList(2014, 8, 20)),
                loanSchedule.get(24).get("dueDate"));
        assertEquals("Checking for Principal Due for 24 Week", new Float("445.00"), loanSchedule.get(24).get("principalDue"));
        assertEquals("Checking for Interest Due for 24 Week", new Float("41.00"), loanSchedule.get(24).get("interestOriginalDue"));
    }
}