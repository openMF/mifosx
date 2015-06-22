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
		String installmentAmount = "800";
		String withoutInstallmentAmount = "";
		String proposedAmount = "10000";
		createTranches.add(this.loanTransactionHelper.createTrancheDetail(
				id, "01 June 2015", "5000"));
		createTranches.add(this.loanTransactionHelper.createTrancheDetail(
				id, "01 Sep 2015", "5000"));

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
		System.out.println("repaymen sche with emi"+repaymentSchedule);
		this.validateRepaymentScheduleWithEMI(repaymentSchedule);
		
		HashMap loanStatusHashMap = LoanStatusChecker.getStatusOfLoan(
				this.requestSpec, this.responseSpec, loanIDWithEmi);
		LoanStatusChecker.verifyLoanIsPending(loanStatusHashMap);

		System.out
				.println("-----------------------------------APPROVE LOAN-----------------------------------------------------------");
		loanStatusHashMap = this.loanTransactionHelper
				.approveLoanWithApproveAmount("01 June 2015",
						"01 June 2015", "10000", loanIDWithEmi,
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

		/* To be uncommented once issue MIFOSX-2006 is closed.*/
		//this.validateRepaymentScheduleWithoutEMI(repaymentScheduleWithoutEmi);

		HashMap loanStatusMap = LoanStatusChecker.getStatusOfLoan(
				this.requestSpec, this.responseSpec, loanIDWithoutEmi);
		LoanStatusChecker.verifyLoanIsPending(loanStatusMap);

		System.out
				.println("-----------------------------------APPROVE LOAN-----------------------------------------------------------");
		loanStatusHashMap = this.loanTransactionHelper
				.approveLoanWithApproveAmount("01 June 2015",
						"01 June 2015", "10000", loanIDWithoutEmi,
						createTranches);
		LoanStatusChecker.verifyLoanIsApproved(loanStatusHashMap);
		LoanStatusChecker.verifyLoanIsWaitingForDisbursal(loanStatusHashMap);
		System.out
				.println("-----------------------------------MULTI DISBURSAL LOAN WITHOUT EMI APPROVED SUCCESSFULLY---------------------------------------");
		
	}
	
	private void validateRepaymentScheduleWithEMI(HashMap repaymentSchedule) {
		assertEquals(repaymentSchedule.get("totalInterestCharged"), 566.11f);
		assertEquals(repaymentSchedule.get("totalRepaymentExpected"), 10566.11f);
		assertEquals(repaymentSchedule.get("totalOutstanding"), 10566.11f);
		assertEquals(repaymentSchedule.get("totalPrincipalExpected"), 10000f);
		assertEquals(repaymentSchedule.get("totalPrincipalDisbursed"), 10000f);
		assertEquals(repaymentSchedule.get("loanTermInDays"), 396);
		ArrayList periods = (ArrayList) repaymentSchedule.get("periods");
		assertEquals(periods.size(), 15);
		for (int i = 0; i < periods.size(); i++) {
			HashMap data = (HashMap) periods.get(i);
			switch (i) {
			case 0:
				assertEquals(data.get("principalLoanBalanceOutstanding"),
						5000.0f);
				assertEquals(data.get("dueDate").toString(), "[2015, 6, 1]");
				assertEquals(data.get("principalDisbursed"), 5000f);
				assertEquals(data.get("totalOutstandingForPeriod"), 0.0f);
				assertEquals(data.get("totalOriginalDueForPeriod"), 0.0f);
				break;

			case 1:
				assertEquals(data.get("dueDate").toString(), "[2015, 7, 1]" );
				assertEquals(data.get("totalOriginalDueForPeriod"), 800.0f);
				assertEquals(data.get("totalOutstandingForPeriod"), 800.0f);
				assertEquals(data.get("interestOutstanding"), 50.0f);
				assertEquals(data.get("principalOutstanding"), 750.0f);
				assertEquals(data.get("principalLoanBalanceOutstanding"), 4250.0f);
				assertEquals(data.get("principalDue"), 750.0f);
				assertEquals(data.get("principalOriginalDue"), 750.0f);
				assertEquals(data.get("fromDate").toString(), "[2015, 6, 1]"); 
				break;

			case 2:
				assertEquals(data.get("dueDate").toString(), "[2015, 8, 1]");
				assertEquals(data.get("totalOriginalDueForPeriod"), 800.0f);
				assertEquals(data.get("totalOutstandingForPeriod"), 800.0f);
				assertEquals(data.get("interestOutstanding"), 42.5f);
				assertEquals(data.get("principalOutstanding"), 757.5f);
				assertEquals(data.get("principalLoanBalanceOutstanding"), 3492.5f);
				assertEquals(data.get("principalDue"), 757.5f);
				assertEquals(data.get("principalOriginalDue"), 757.5f);
				assertEquals(data.get("fromDate").toString(), "[2015, 7, 1]"); 
				break;

			case 3:
				assertEquals(data.get("principalLoanBalanceOutstanding"),
						5000.0f);
				assertEquals(data.get("dueDate").toString(), "[2015, 9, 1]");
				assertEquals(data.get("principalDisbursed"), 5000f);
				assertEquals(data.get("totalOutstandingForPeriod"), 0);
				assertEquals(data.get("totalOriginalDueForPeriod"), 0);
				break;

			case 4:
				assertEquals(data.get("dueDate").toString(), "[2015, 9, 1]");
				assertEquals(data.get("totalOriginalDueForPeriod"), 800.0f);
				assertEquals(data.get("totalOutstandingForPeriod"), 800.0f);
				assertEquals(data.get("interestOutstanding"), 34.92f);
				assertEquals(data.get("principalOutstanding"), 765.08f);
				assertEquals(data.get("principalLoanBalanceOutstanding"), 7727.42f);
				assertEquals(data.get("principalDue"), 765.08f);
				assertEquals(data.get("principalOriginalDue"), 765.08f);
				assertEquals(data.get("fromDate").toString(), "[2015, 8, 1]"); 
				break;

			case 5:
				assertEquals(data.get("dueDate").toString(), "[2015, 10, 1]");
				assertEquals(data.get("totalOriginalDueForPeriod"), 800.0f);
				assertEquals(data.get("totalOutstandingForPeriod"), 800.0f);
				assertEquals(data.get("interestOutstanding"), 77.27f);
				assertEquals(data.get("principalOutstanding"), 722.73f);
				assertEquals(data.get("principalLoanBalanceOutstanding"), 7004.69f);
				assertEquals(data.get("principalDue"), 722.73f);
				assertEquals(data.get("principalOriginalDue"), 722.73f);
				assertEquals(data.get("fromDate").toString(), "[2015, 9, 1]"); 
				break;

			case 6:
				assertEquals(data.get("dueDate").toString(), "[2015, 11, 1]");
				assertEquals(data.get("totalOriginalDueForPeriod"), 800.0f);
				assertEquals(data.get("totalOutstandingForPeriod"), 800.0f);
				assertEquals(data.get("interestOutstanding"), 70.05f);
				assertEquals(data.get("principalOutstanding"), 729.95f);
				assertEquals(data.get("principalLoanBalanceOutstanding"), 6274.74f);
				assertEquals(data.get("principalDue"), 729.95f);
				assertEquals(data.get("principalOriginalDue"), 729.95f);
				assertEquals(data.get("fromDate").toString(), "[2015, 10, 1]"); 
				break;

			case 7:
				assertEquals(data.get("dueDate").toString(), "[2015, 12, 1]");
				assertEquals(data.get("totalOriginalDueForPeriod"), 800.0f);
				assertEquals(data.get("totalOutstandingForPeriod"), 800.0f);
				assertEquals(data.get("interestOutstanding"), 62.75f);
				assertEquals(data.get("principalOutstanding"), 737.25f);
				assertEquals(data.get("principalLoanBalanceOutstanding"), 5537.49f);
				assertEquals(data.get("principalDue"), 737.25f);
				assertEquals(data.get("principalOriginalDue"),737.25f );
				assertEquals(data.get("fromDate").toString(), "[2015, 11, 1]"); 
				break;

			case 8:
				assertEquals(data.get("dueDate").toString(), "[2016, 1, 1]");
				assertEquals(data.get("totalOriginalDueForPeriod"),800.0f );
				assertEquals(data.get("totalOutstandingForPeriod"), 800.0f);
				assertEquals(data.get("interestOutstanding"), 55.37f);
				assertEquals(data.get("principalOutstanding"), 744.63f);
				assertEquals(data.get("principalLoanBalanceOutstanding"), 4792.86f);
				assertEquals(data.get("principalDue"), 744.63f);
				assertEquals(data.get("principalOriginalDue"), 744.63f);
				assertEquals(data.get("fromDate").toString(), "[2015, 12, 1]"); 
				break;

			case 9:
				assertEquals(data.get("dueDate").toString(), "[2016, 2, 1]" );
				assertEquals(data.get("totalOriginalDueForPeriod"), 800.0f);
				assertEquals(data.get("totalOutstandingForPeriod"), 800.0f);
				assertEquals(data.get("interestOutstanding"), 47.93f);
				assertEquals(data.get("principalOutstanding"), 752.07f);
				assertEquals(data.get("principalLoanBalanceOutstanding"), 4040.79f);
				assertEquals(data.get("principalDue"), 752.07f);
				assertEquals(data.get("principalOriginalDue"), 752.07f);
				assertEquals(data.get("fromDate").toString(), "[2016, 1, 1]"); 
				break;

			case 10:
				assertEquals(data.get("dueDate").toString(), "[2016, 3, 1]");
				assertEquals(data.get("totalOriginalDueForPeriod"), 800.0f);
				assertEquals(data.get("totalOutstandingForPeriod"), 800.0f);
				assertEquals(data.get("interestOutstanding"), 40.41f);
				assertEquals(data.get("principalOutstanding"), 759.59f);
				assertEquals(data.get("principalLoanBalanceOutstanding"), 3281.2f);
				assertEquals(data.get("principalDue"), 759.59f);
				assertEquals(data.get("principalOriginalDue"), 759.59f);
				assertEquals(data.get("fromDate").toString(), "[2016, 2, 1]"); 
				break;

			case 11:
				assertEquals(data.get("dueDate").toString(), "[2016, 4, 1]");
				assertEquals(data.get("totalOriginalDueForPeriod"), 800.0f );
				assertEquals(data.get("totalOutstandingForPeriod"), 800.0f);
				assertEquals(data.get("interestOutstanding"), 32.81f);
				assertEquals(data.get("principalOutstanding"), 767.19f);
				assertEquals(data.get("principalLoanBalanceOutstanding"), 2514.01f);
				assertEquals(data.get("principalDue"), 767.19f);
				assertEquals(data.get("principalOriginalDue"), 767.19f);
				assertEquals(data.get("fromDate").toString(), "[2016, 3, 1]"); 
				break;

			case 12:
				assertEquals(data.get("dueDate").toString(), "[2016, 5, 1]");
				assertEquals(data.get("totalOriginalDueForPeriod"), 800.0f);
				assertEquals(data.get("totalOutstandingForPeriod"), 800.0f);
				assertEquals(data.get("interestOutstanding"), 25.14f);
				assertEquals(data.get("principalOutstanding"), 774.86f);
				assertEquals(data.get("principalLoanBalanceOutstanding"), 1739.15f);
				assertEquals(data.get("principalDue"), 774.86f);
				assertEquals(data.get("principalOriginalDue"), 774.86f);
				assertEquals(data.get("fromDate").toString(), "[2016, 4, 1]"); 
				break;

			case 13:
				assertEquals(data.get("dueDate").toString(), "[2016, 6, 1]");
				assertEquals(data.get("totalOriginalDueForPeriod"), 800.0f);
				assertEquals(data.get("totalOutstandingForPeriod"), 800.0f);
				assertEquals(data.get("interestOutstanding"), 17.39f);
				assertEquals(data.get("principalOutstanding"), 782.61f);
				assertEquals(data.get("principalLoanBalanceOutstanding"), 956.54f);
				assertEquals(data.get("principalDue"), 782.61f);
				assertEquals(data.get("principalOriginalDue"), 782.61f);
				assertEquals(data.get("fromDate").toString(), "[2016, 5, 1]"); 
				break;

			case 14:
				assertEquals(data.get("dueDate").toString(), "[2016, 7, 1]");
				assertEquals(data.get("totalOriginalDueForPeriod"), 966.11f);
				assertEquals(data.get("totalOutstandingForPeriod"), 966.11f);
				assertEquals(data.get("interestOutstanding"), 9.57f);
				assertEquals(data.get("principalOutstanding"), 956.54f);
				assertEquals(data.get("principalLoanBalanceOutstanding"), 0.0f );
				assertEquals(data.get("principalDue"), 956.54f);
				assertEquals(data.get("principalOriginalDue"), 956.54f);
				assertEquals(data.get("fromDate").toString(), "[2016, 6, 1]"); 
				break;

			}
		}
	}
	
	private void validateRepaymentScheduleWithoutEMI(HashMap repaymentSchedule){
		assertEquals(repaymentSchedule.get("totalInterestCharged"), 799.34f);
		assertEquals(repaymentSchedule.get("totalRepaymentExpected"), 10799.34f);
		assertEquals(repaymentSchedule.get("loanTermInDays"), 366);
		assertEquals(repaymentSchedule.get("totalOutstanding"), 10799.34f);
		assertEquals(repaymentSchedule.get("totalPrincipalExpected"), 10000f);
		assertEquals(repaymentSchedule.get("totalPrincipalDisbursed"), 10000f);
		ArrayList periods = (ArrayList) repaymentSchedule.get("periods");
		assertEquals(periods.size(), 14);
		for (int i = 0; i < periods.size(); i++) {
			HashMap data = (HashMap) periods.get(i);
			switch (i) {
			case 0:
				assertEquals(data.get("principalLoanBalanceOutstanding"),
						5000.0f);
				assertEquals(data.get("dueDate").toString(), "[2015, 6, 1]");
				assertEquals(data.get("principalDisbursed"), 5000f);
				assertEquals(data.get("totalOutstandingForPeriod"), 0.0f);
				assertEquals(data.get("totalOriginalDueForPeriod"), 0.0f);
				break;

			case 1:
				assertEquals(data.get("dueDate").toString(), "[2015, 7, 1]" );
				assertEquals(data.get("totalOriginalDueForPeriod"), 444.24f);
				assertEquals(data.get("totalOutstandingForPeriod"), 444.24f);
				assertEquals(data.get("interestOutstanding"), 50.0f);
				assertEquals(data.get("principalOutstanding"), 394.24f);
				assertEquals(data.get("principalLoanBalanceOutstanding"), 4605.76f);
				assertEquals(data.get("principalDue"), 394.24f);
				assertEquals(data.get("principalOriginalDue"), 394.24f);
				assertEquals(data.get("fromDate").toString(), "[2015, 6, 1]"); 
				break;

			case 2:
				assertEquals(data.get("dueDate").toString(), "[2015, 8, 1]");
				assertEquals(data.get("totalOriginalDueForPeriod"), 444.24f);
				assertEquals(data.get("totalOutstandingForPeriod"), 444.24f);
				assertEquals(data.get("interestOutstanding"), 46.06f);
				assertEquals(data.get("principalOutstanding"), 398.18f);
				assertEquals(data.get("principalLoanBalanceOutstanding"), 4207.58f);
				assertEquals(data.get("principalDue"), 398.18f);
				assertEquals(data.get("principalOriginalDue"), 398.18f);
				assertEquals(data.get("fromDate").toString(), "[2015, 7, 1]"); 
				break;

			case 3:
				assertEquals(data.get("principalLoanBalanceOutstanding"),
						5000.0f);
				assertEquals(data.get("dueDate").toString(), "[2015, 9, 1]");
				assertEquals(data.get("principalDisbursed"), 5000f);
				assertEquals(data.get("totalOutstandingForPeriod"), 0);
				assertEquals(data.get("totalOriginalDueForPeriod"), 0);
				break;

			case 4:
				assertEquals(data.get("dueDate").toString(), "[2015, 9, 1]");
				assertEquals(data.get("totalOriginalDueForPeriod"), 444.24f);
				assertEquals(data.get("totalOutstandingForPeriod"), 444.24f);
				assertEquals(data.get("interestOutstanding"), 42.08f);
				assertEquals(data.get("principalOutstanding"), 402.16f);
				assertEquals(data.get("principalLoanBalanceOutstanding"), 8805.42f);
				assertEquals(data.get("principalDue"), 402.16f);
				assertEquals(data.get("principalOriginalDue"), 402.16f);
				assertEquals(data.get("fromDate").toString(), "[2015, 8, 1]"); 
				break;

			case 5:
				assertEquals(data.get("dueDate").toString(), "[2015, 10, 1]");
				assertEquals(data.get("totalOriginalDueForPeriod"), 444.24f);
				assertEquals(data.get("totalOutstandingForPeriod"), 444.24f);
				assertEquals(data.get("interestOutstanding"), 88.05f);
				assertEquals(data.get("principalOutstanding"), 356.19f);
				assertEquals(data.get("principalLoanBalanceOutstanding"), 8449.23f);
				assertEquals(data.get("principalDue"), 356.19f);
				assertEquals(data.get("principalOriginalDue"), 356.19f);
				assertEquals(data.get("fromDate").toString(), "[2015, 9, 1]"); 
				break;

			case 6:
				assertEquals(data.get("dueDate").toString(), "[2015, 11, 1]");
				assertEquals(data.get("totalOriginalDueForPeriod"), 444.24f);
				assertEquals(data.get("totalOutstandingForPeriod"), 444.24f);
				assertEquals(data.get("interestOutstanding"), 84.49f);
				assertEquals(data.get("principalOutstanding"), 359.75f);
				assertEquals(data.get("principalLoanBalanceOutstanding"), 8089.48f);
				assertEquals(data.get("principalDue"), 359.75f);
				assertEquals(data.get("principalOriginalDue"), 359.75f);
				assertEquals(data.get("fromDate").toString(), "[2015, 10, 1]"); 
				break;

			case 7:
				assertEquals(data.get("dueDate").toString(), "[2015, 12, 1]");
				assertEquals(data.get("totalOriginalDueForPeriod"), 444.24f);
				assertEquals(data.get("totalOutstandingForPeriod"), 444.24f);
				assertEquals(data.get("interestOutstanding"), 80.89f);
				assertEquals(data.get("principalOutstanding"), 363.35f);
				assertEquals(data.get("principalLoanBalanceOutstanding"), 7726.13f);
				assertEquals(data.get("principalDue"), 363.35f);
				assertEquals(data.get("principalOriginalDue"),363.35f );
				assertEquals(data.get("fromDate").toString(), "[2015, 11, 1]"); 
				break;

			case 8:
				assertEquals(data.get("dueDate").toString(), "[2016, 1, 1]");
				assertEquals(data.get("totalOriginalDueForPeriod"),444.24f );
				assertEquals(data.get("totalOutstandingForPeriod"), 444.24f);
				assertEquals(data.get("interestOutstanding"), 77.26f);
				assertEquals(data.get("principalOutstanding"), 366.98f);
				assertEquals(data.get("principalLoanBalanceOutstanding"), 7359.15f);
				assertEquals(data.get("principalDue"), 366.98f);
				assertEquals(data.get("principalOriginalDue"), 366.98f);
				assertEquals(data.get("fromDate").toString(), "[2015, 12, 1]"); 
				break;

			case 9:
				assertEquals(data.get("dueDate").toString(), "[2016, 2, 1]" );
				assertEquals(data.get("totalOriginalDueForPeriod"), 444.24f);
				assertEquals(data.get("totalOutstandingForPeriod"), 444.24f);
				assertEquals(data.get("interestOutstanding"), 73.59f);
				assertEquals(data.get("principalOutstanding"), 370.65f);
				assertEquals(data.get("principalLoanBalanceOutstanding"), 6988.5f);
				assertEquals(data.get("principalDue"), 370.65f);
				assertEquals(data.get("principalOriginalDue"), 370.65f);
				assertEquals(data.get("fromDate").toString(), "[2016, 1, 1]"); 
				break;

			case 10:
				assertEquals(data.get("dueDate").toString(), "[2016, 3, 1]");
				assertEquals(data.get("totalOriginalDueForPeriod"), 444.24f);
				assertEquals(data.get("totalOutstandingForPeriod"), 444.24f);
				assertEquals(data.get("interestOutstanding"), 69.88f);
				assertEquals(data.get("principalOutstanding"), 374.36f);
				assertEquals(data.get("principalLoanBalanceOutstanding"), 6614.14f);
				assertEquals(data.get("principalDue"), 374.36f);
				assertEquals(data.get("principalOriginalDue"), 374.36f);
				assertEquals(data.get("fromDate").toString(), "[2016, 2, 1]"); 
				break;

			case 11:
				assertEquals(data.get("dueDate").toString(), "[2016, 4, 1]");
				assertEquals(data.get("totalOriginalDueForPeriod"), 444.24f );
				assertEquals(data.get("totalOutstandingForPeriod"), 444.24f);
				assertEquals(data.get("interestOutstanding"), 66.14f);
				assertEquals(data.get("principalOutstanding"), 378.1f);
				assertEquals(data.get("principalLoanBalanceOutstanding"), 6236.04f);
				assertEquals(data.get("principalDue"), 378.1f);
				assertEquals(data.get("principalOriginalDue"), 378.1f);
				assertEquals(data.get("fromDate").toString(), "[2016, 3, 1]"); 
				break;
				
			case 12:
				assertEquals(data.get("dueDate").toString(), "[2016, 5, 1]");
				assertEquals(data.get("totalOriginalDueForPeriod"), 444.24f );
				assertEquals(data.get("totalOutstandingForPeriod"), 444.24f);
				assertEquals(data.get("interestOutstanding"), 62.36f);
				assertEquals(data.get("principalOutstanding"), 381.88f);
				assertEquals(data.get("principalLoanBalanceOutstanding"), 5854.16f);
				assertEquals(data.get("principalDue"), 381.88f);
				assertEquals(data.get("principalOriginalDue"), 381.88f);
				assertEquals(data.get("fromDate").toString(), "[2016, 4, 1]"); 
				break;
				
			case 13:
				assertEquals(data.get("dueDate").toString(), "[2016, 6, 1]");
				assertEquals(data.get("totalOriginalDueForPeriod"), 5912.7f );
				assertEquals(data.get("totalOutstandingForPeriod"), 5912.7f);
				assertEquals(data.get("interestOutstanding"), 58.54f);
				assertEquals(data.get("principalOutstanding"), 5854.16f);
				assertEquals(data.get("principalLoanBalanceOutstanding"), 0.0f);
				assertEquals(data.get("principalDue"), 5854.16f);
				assertEquals(data.get("principalOriginalDue"), 5854.16f);
				assertEquals(data.get("fromDate").toString(), "[2016, 5, 1]"); 
				break;
			}
		}
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
				.withLoanTermFrequency("12")
				//
				.withLoanTermFrequencyAsMonths()
				//
				.withNumberOfRepayments("12").withRepaymentEveryAfter("1")
				.withRepaymentFrequencyTypeAsMonths() //
				.withInterestRatePerPeriod("1") //
				.withExpectedDisbursementDate("01 June 2015") //
				.withTranches(tranches) //
				.withFixedEmiAmount(installmentAmount) //
				.withInterestTypeAsDecliningBalance() //
				.withSubmittedOnDate("01 June 2015") //
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
		this.editLoanDisbursementDetails();
	}

	private void editLoanDisbursementDetails() {
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
