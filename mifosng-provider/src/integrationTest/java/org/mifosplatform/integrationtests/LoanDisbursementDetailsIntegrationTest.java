package org.mifosplatform.integrationtests;

import static org.junit.Assert.assertEquals;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mifosplatform.integrationtests.common.ClientHelper;
import org.mifosplatform.integrationtests.common.Utils;
import org.mifosplatform.integrationtests.common.loans.LoanApplicationTestBuilder;
import org.mifosplatform.integrationtests.common.loans.LoanProductTestBuilder;
import org.mifosplatform.integrationtests.common.loans.LoanStatusChecker;
import org.mifosplatform.integrationtests.common.loans.LoanTransactionHelper;

import com.jayway.restassured.builder.RequestSpecBuilder;
import com.jayway.restassured.builder.ResponseSpecBuilder;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.specification.RequestSpecification;
import com.jayway.restassured.specification.ResponseSpecification;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class LoanDisbursementDetailsIntegrationTest {

	private ResponseSpecification responseSpec;
	private RequestSpecification requestSpec;
	private LoanTransactionHelper loanTransactionHelper;
	private Integer loanID;
	private Integer disbursementId;
	final String approveDate = "01 March 2014";
	final String expectedDisbursementDate = "01 March 2014";
	final String proposedAmount = "5000";
	final String approvalAmount = "5000";

	@Before
	public void setup() {
		Utils.initializeRESTAssured();
		this.requestSpec = new RequestSpecBuilder().setContentType(
				ContentType.JSON).build();
		this.requestSpec
				.header("Authorization",
						"Basic "
								+ Utils.loginIntoServerAndGetBase64EncodedAuthenticationKey());
		this.responseSpec = new ResponseSpecBuilder().expectStatusCode(200)
				.build();
		this.loanTransactionHelper = new LoanTransactionHelper(
				this.requestSpec, this.responseSpec);
	}
	
	@Test
	public void createAndValidateMultiDisburseLoansBasedOnEmi(){
		List<HashMap> createTranches = new ArrayList<>();
		String id= null;
		String installmentAmount = "500";
		String withoutInstallmentAmount = "";
		createTranches.add(this.loanTransactionHelper.createTrancheDetail(
				id, "01 March 2014", "1000"));

		final Integer clientID = ClientHelper.createClient(this.requestSpec,
				this.responseSpec, "01 January 2014");
		System.out
				.println("---------------------------------CLIENT CREATED WITH ID---------------------------------------------------"
						+ clientID);

		final Integer loanProductID = this.loanTransactionHelper
				.getLoanProductId(new LoanProductTestBuilder()
						.withInterestTypeAsDecliningBalance()
						.withMoratorium("", "")
						.withAmortizationTypeAsEqualInstallments()
						.withTranches(true).build(null));
		System.out
				.println("----------------------------------LOAN PRODUCT CREATED WITH ID-------------------------------------------"
						+ loanProductID);

		final Integer loanIDWithEmi = applyForLoanApplicationWithEmiAmount(clientID,
				loanProductID, proposedAmount, createTranches, installmentAmount);
		
		System.out.println("loan with emi as 500"+this.loanTransactionHelper.getLoanDetails(this.requestSpec,this.responseSpec, loanIDWithEmi));
		
		System.out
		.println("-----------------------------------LOAN CREATED WITH EMI LOANID-------------------------------------------------"
				+ loanIDWithEmi);
		
		HashMap repaymentSchedule = (HashMap)this.loanTransactionHelper.getLoanDetail(this.requestSpec,this.responseSpec, loanIDWithEmi, "repaymentSchedule");
		this.validateRepaymentScheduleWithEMI(repaymentSchedule);
		
		HashMap loanStatusHashMap = LoanStatusChecker.getStatusOfLoan(
				this.requestSpec, this.responseSpec, loanIDWithEmi);
		LoanStatusChecker.verifyLoanIsPending(loanStatusHashMap);

		System.out
				.println("-----------------------------------APPROVE LOAN-----------------------------------------------------------");
		loanStatusHashMap = this.loanTransactionHelper
				.approveLoanWithApproveAmount(approveDate,
						"01 March 2014", approvalAmount, loanIDWithEmi,
						createTranches);
		LoanStatusChecker.verifyLoanIsApproved(loanStatusHashMap);
		LoanStatusChecker.verifyLoanIsWaitingForDisbursal(loanStatusHashMap);
		System.out
				.println("-----------------------------------MULTI DISBURSAL LOAN WITH EMI APPROVED SUCCESSFULLY---------------------------------------");
		

		final Integer loanIDWithoutEmi = applyForLoanApplicationWithEmiAmount(clientID,
				loanProductID, proposedAmount, createTranches, withoutInstallmentAmount);
		
		HashMap repaymentScheduleWithoutEmi = (HashMap)this.loanTransactionHelper.getLoanDetail(this.requestSpec,this.responseSpec, loanIDWithoutEmi, "repaymentSchedule");
		System.out
		.println("-----------------------------------LOAN CREATED WITHOUT EMI LOANID-------------------------------------------------"
				+ loanIDWithoutEmi);

		this.validateRepaymentScheduleWithoutEMI(repaymentScheduleWithoutEmi);

		HashMap loanStatusMap = LoanStatusChecker.getStatusOfLoan(
				this.requestSpec, this.responseSpec, loanIDWithoutEmi);
		LoanStatusChecker.verifyLoanIsPending(loanStatusMap);

		System.out
				.println("-----------------------------------APPROVE LOAN-----------------------------------------------------------");
		loanStatusHashMap = this.loanTransactionHelper
				.approveLoanWithApproveAmount(approveDate,
						"01 March 2014", approvalAmount, loanIDWithoutEmi,
						createTranches);
		LoanStatusChecker.verifyLoanIsApproved(loanStatusHashMap);
		LoanStatusChecker.verifyLoanIsWaitingForDisbursal(loanStatusHashMap);
		System.out
				.println("-----------------------------------MULTI DISBURSAL LOAN WITHOUT EMI APPROVED SUCCESSFULLY---------------------------------------");
		
	}
	
	private void validateRepaymentScheduleWithEMI(HashMap repaymentSchedule){
		assertEquals(repaymentSchedule.get("totalInterestCharged"), 160.0f);
		assertEquals(repaymentSchedule.get("totalRepaymentExpected"), 1160.0f);
		assertEquals(repaymentSchedule.get("loanTermInDays"), 61);	
	}
	
	private void validateRepaymentScheduleWithoutEMI(HashMap repaymentSchedule){
		assertEquals(repaymentSchedule.get("totalInterestCharged"), 627.42f);
		assertEquals(repaymentSchedule.get("totalRepaymentExpected"), 1627.42f);
		assertEquals(repaymentSchedule.get("loanTermInDays"), 306);	
	}

	private Integer applyForLoanApplicationWithEmiAmount(final Integer clientID,
			final Integer loanProductID, final String proposedAmount,
			List<HashMap> tranches, final String installmentAmount) {

		System.out
				.println("--------------------------------APPLYING FOR LOAN APPLICATION--------------------------------");
		final String loanApplicationJSON = new LoanApplicationTestBuilder()
				//
				.withPrincipal(proposedAmount)
				//
				.withLoanTermFrequency("10")
				//
				.withLoanTermFrequencyAsMonths()
				//
				.withNumberOfRepayments("10").withRepaymentEveryAfter("1")
				.withRepaymentFrequencyTypeAsMonths() //
				.withInterestRatePerPeriod("10") //
				.withExpectedDisbursementDate("01 March 2014") //
				.withTranches(tranches) //
				.withFixedEmiAmount(installmentAmount) //
				.withInterestTypeAsDecliningBalance() //
				.withSubmittedOnDate("01 March 2014") //
				.withExpectedDisbursementDate("01 March 2014") //
				.withAmortizationTypeAsEqualInstallments() //
				.build(clientID.toString(), loanProductID.toString(), null);

		return this.loanTransactionHelper.getLoanId(loanApplicationJSON);

	}
	
	@Test
	public void createApproveAndValidateMultiDisburseLoan() {

		List<HashMap> createTranches = new ArrayList<>();
		String id= null;
		createTranches.add(this.loanTransactionHelper.createTrancheDetail(
				id, "01 March 2014", "1000"));

		final Integer clientID = ClientHelper.createClient(this.requestSpec,
				this.responseSpec, "01 January 2014");
		System.out
				.println("---------------------------------CLIENT CREATED WITH ID---------------------------------------------------"
						+ clientID);

		final Integer loanProductID = this.loanTransactionHelper
				.getLoanProductId(new LoanProductTestBuilder()
						.withInterestTypeAsDecliningBalance()
						.withTranches(true).build(null));
		System.out
				.println("----------------------------------LOAN PRODUCT CREATED WITH ID-------------------------------------------"
						+ loanProductID);

		this.loanID = applyForLoanApplicationWithTranches(clientID,
				loanProductID, proposedAmount, createTranches);
		System.out
				.println("-----------------------------------LOAN CREATED WITH LOANID-------------------------------------------------"
						+ loanID);

		HashMap loanStatusHashMap = LoanStatusChecker.getStatusOfLoan(
				this.requestSpec, this.responseSpec, loanID);
		LoanStatusChecker.verifyLoanIsPending(loanStatusHashMap);

		System.out
				.println("-----------------------------------APPROVE LOAN-----------------------------------------------------------");
		loanStatusHashMap = this.loanTransactionHelper
				.approveLoanWithApproveAmount(approveDate,
						expectedDisbursementDate, approvalAmount, loanID,
						createTranches);
		LoanStatusChecker.verifyLoanIsApproved(loanStatusHashMap);
		LoanStatusChecker.verifyLoanIsWaitingForDisbursal(loanStatusHashMap);
		System.out
				.println("-----------------------------------MULTI DISBURSAL LOAN APPROVED SUCCESSFULLY---------------------------------------");
		ArrayList<HashMap> disbursementDetails = (ArrayList<HashMap>) this.loanTransactionHelper
				.getLoanDetail(this.requestSpec, this.responseSpec,
						this.loanID, "disbursementDetails");
		this.disbursementId = (Integer) disbursementDetails.get(0).get("id");
		this.EditLoanDisbursementDetails();
	}

	private void EditLoanDisbursementDetails() {
		this.editDateAndPrincipalOfExistingTranche();
		this.addNewDisbursementDetails();
		this.deleteDisbursmentDetails();
	}

	
	private void addNewDisbursementDetails(){
		List<HashMap> addTranches = new ArrayList<>();
        ArrayList<HashMap> disbursementDetails =  (ArrayList<HashMap>) this.loanTransactionHelper.getLoanDetail(this.requestSpec, this.responseSpec, this.loanID, "disbursementDetails");
        ArrayList expectedDisbursementDate = (ArrayList) disbursementDetails
				.get(0).get("expectedDisbursementDate");
		String date = formatExpectedDisbursementDate(expectedDisbursementDate
				.toString());
    	
    	String id = null;
    	addTranches.add(this.loanTransactionHelper.createTrancheDetail(disbursementDetails
				.get(0).get("id").toString(), date, disbursementDetails
				.get(0).get("principal").toString()));
    	addTranches.add(this.loanTransactionHelper.createTrancheDetail(id, "3 March 2014", "2000"));
    	addTranches.add(this.loanTransactionHelper.createTrancheDetail(id, "4 March 2014", "500"));

    	/* Add disbursement detail */
   	 this.loanTransactionHelper.addAndDeleteDisbursementDetail(this.loanID, this.approvalAmount, this.expectedDisbursementDate, addTranches, "");
    }
	
	private void deleteDisbursmentDetails(){
		List<HashMap> deleteTranches = new ArrayList<>();
        ArrayList<HashMap> disbursementDetails =  (ArrayList<HashMap>) this.loanTransactionHelper.getLoanDetail(this.requestSpec, this.responseSpec, this.loanID, "disbursementDetails");
        /* Delete the last tranche */
        for(int i=0; i< disbursementDetails.size()-1 ;i++) {
        	ArrayList expectedDisbursementDate = (ArrayList) disbursementDetails
    				.get(i).get("expectedDisbursementDate");
    		String disbursementDate = formatExpectedDisbursementDate(expectedDisbursementDate
    				.toString());
    		deleteTranches.add(this.loanTransactionHelper.createTrancheDetail(disbursementDetails
    				.get(i).get("id").toString(), disbursementDate, disbursementDetails
    				.get(i).get("principal").toString()));
        }

    	/* Add disbursement detail */
   	 this.loanTransactionHelper.addAndDeleteDisbursementDetail(this.loanID, this.approvalAmount, this.expectedDisbursementDate, deleteTranches, "");
    }
	

	private void editDateAndPrincipalOfExistingTranche() {
		String updatedExpectedDisbursementDate = "01 March 2014";
		String updatedPrincipal = "900";
		/* Update */
		this.loanTransactionHelper.editDisbursementDetail(this.loanID,
				this.disbursementId, this.approvalAmount,
				this.expectedDisbursementDate, updatedExpectedDisbursementDate,
				updatedPrincipal, "");
		/* Validate Edit */
		ArrayList<HashMap> disbursementDetails = (ArrayList<HashMap>) this.loanTransactionHelper
				.getLoanDetail(this.requestSpec, this.responseSpec,
						this.loanID, "disbursementDetails");
		assertEquals(Float.valueOf(updatedPrincipal), disbursementDetails
				.get(0).get("principal"));
		ArrayList expectedDisbursementDate = (ArrayList) disbursementDetails
				.get(0).get("expectedDisbursementDate");
		String date = formatExpectedDisbursementDate(expectedDisbursementDate
				.toString());
		assertEquals(updatedExpectedDisbursementDate, date);

	}

	private String formatExpectedDisbursementDate(
			String expectedDisbursementDate) {

		SimpleDateFormat source = new SimpleDateFormat("[yyyy, MM, dd]");
		SimpleDateFormat target = new SimpleDateFormat("dd MMMM yyyy");
		String date = null;
		try {
			date = target.format(source.parse(expectedDisbursementDate));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}

	private Integer applyForLoanApplicationWithTranches(final Integer clientID,
			final Integer loanProductID, String principal,
			List<HashMap> tranches) {
		System.out
				.println("--------------------------------APPLYING FOR LOAN APPLICATION--------------------------------");
		final String loanApplicationJSON = new LoanApplicationTestBuilder()
				//
				.withPrincipal(principal)
				//
				.withLoanTermFrequency("5")
				//
				.withLoanTermFrequencyAsMonths()
				//
				.withNumberOfRepayments("5").withRepaymentEveryAfter("1")
				.withRepaymentFrequencyTypeAsMonths() //
				.withInterestRatePerPeriod("2") //
				.withExpectedDisbursementDate("01 March 2014") //
				.withTranches(tranches) //
				.withInterestTypeAsDecliningBalance() //
				.withSubmittedOnDate("01 March 2014") //
				.build(clientID.toString(), loanProductID.toString(), null);

		return this.loanTransactionHelper.getLoanId(loanApplicationJSON);
	}

}
