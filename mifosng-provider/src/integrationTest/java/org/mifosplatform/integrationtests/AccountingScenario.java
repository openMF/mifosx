package org.mifosplatform.integrationtests;


import com.jayway.restassured.builder.RequestSpecBuilder;
import com.jayway.restassured.builder.ResponseSpecBuilder;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.specification.RequestSpecification;
import com.jayway.restassured.specification.ResponseSpecification;
import org.junit.Before;


import org.junit.Test;
import org.mifosplatform.integrationtests.common.*;

import java.util.ArrayList;
import java.util.HashMap;

public class AccountingScenario {
    RequestSpecification requestSpec;
    ResponseSpecification responseSpec;

    String CREATE_GL_ACCOUNT_URL = "/mifosng-provider/api/v1/glaccounts?tenantIdentifier=default";
    String GL_ACCOUNT_ID_RESPONSE = "resourceId";

    String DATE_OF_JOINING = "01 January 2011";

    String LP_PRINCIPAL="10,000.00";
    String LP_REPAYMENTS = "10";
    String LP_REPAYMENT_PERIOD = "1";
    String LP_INTEREST_RATE="1";
    String EXPECTED_DISBURSAL_DATE="4 March 2011";
    String LOAN_APPLICATION_SUBMISSION_DATE= "3 March 2011";
    String LOAN_TERM_FREQUENCY= "10";


    @Before
    public void setup() {
        Utils.initializeRESTAssured();
        requestSpec = new RequestSpecBuilder().setContentType(ContentType.JSON).build();
        requestSpec.header("Authorization", "Basic " + Utils.loginIntoServerAndGetBase64EncodedAuthenticationKey());
        responseSpec = new ResponseSpecBuilder().expectStatusCode(200).build();
    }

    @Test
    public void checkAccountingFlow() {

        Account assetAccount  = createAssetAccount(requestSpec, responseSpec);
        Account incomeAccount = createIncomeAccount(requestSpec,responseSpec);
        Account expenseAccount= createExpenseAccount(requestSpec,responseSpec);

        Integer loanProductID = createLoanProduct(assetAccount,incomeAccount,expenseAccount);
        System.out.println("********* LOAN PROD ID :"+loanProductID);

        Integer clientID = ClientHelper.createClient(requestSpec, responseSpec, DATE_OF_JOINING);
        Integer loanID = applyForLoanApplication(clientID, loanProductID);

        HashMap loanStatusHashMap = LoanStatusChecker.getStatusOfLoan(requestSpec, responseSpec, loanID);
        LoanStatusChecker.verifyLoanIsPending(loanStatusHashMap);

        loanStatusHashMap = LoanTransactionHelper.approveLoan(requestSpec, responseSpec,EXPECTED_DISBURSAL_DATE, loanID);
        LoanStatusChecker.verifyLoanIsApproved(loanStatusHashMap);
        LoanStatusChecker.verifyLoanIsWaitingForDisbursal(loanStatusHashMap);

        loanStatusHashMap = LoanTransactionHelper.disburseLoan(requestSpec, responseSpec, EXPECTED_DISBURSAL_DATE, loanID);
        System.out.println("DISBURSE " + loanStatusHashMap);
        LoanStatusChecker.verifyLoanIsActive(loanStatusHashMap);



    }

    public Account createAssetAccount(RequestSpecification requestSpec, ResponseSpecification responseSpec){
        String assetAccountJSON= new GLAccountBuilder()
                .withAccountTypeAsAsset()
                .build();
        Integer accountID = Utils.performServerPost(requestSpec, responseSpec, CREATE_GL_ACCOUNT_URL, assetAccountJSON, GL_ACCOUNT_ID_RESPONSE);
        return new Account(accountID, Account.AccountType.ASSET);
    }
    public Account createIncomeAccount(RequestSpecification requestSpec, ResponseSpecification responseSpec){
        String assetAccountJSON= new GLAccountBuilder()
                .withAccountTypeAsIncome()
                .build();
        Integer accountID = Utils.performServerPost(requestSpec, responseSpec, CREATE_GL_ACCOUNT_URL, assetAccountJSON, GL_ACCOUNT_ID_RESPONSE);
        return new Account(accountID, Account.AccountType.INCOME);
    }
    public Account createExpenseAccount(RequestSpecification requestSpec, ResponseSpecification responseSpec){
        String assetAccountJSON= new GLAccountBuilder()
                .withAccountTypeAsExpense()
                .build();
        Integer accountID = Utils.performServerPost(requestSpec, responseSpec, CREATE_GL_ACCOUNT_URL, assetAccountJSON, GL_ACCOUNT_ID_RESPONSE);
        return new Account(accountID, Account.AccountType.EXPENSE);
    }

    private Integer createLoanProduct(Account ... accounts) {
        System.out.println("------------------------------CREATING NEW LOAN PRODUCT ---------------------------------------");
        String loanProductJSON = new LoanProductTestBuilder().withPrincipal(LP_PRINCIPAL).withRepaymentTypeAsMonth()
                .withRepaymentAfterEvery(LP_REPAYMENT_PERIOD).withNumberOfRepayments(LP_REPAYMENTS).withRepaymentTypeAsMonth()
                .withinterestRatePerPeriod(LP_INTEREST_RATE).withInterestRateFrequencyTypeAsMonths()
                .withAmortizationTypeAsEqualPrinciplePayment().withInterestTypeAsFlat()
                .withAccountingRuleAsAccrualBased(accounts)
                .build();
        return LoanTransactionHelper.getLoanProductId(requestSpec, responseSpec, loanProductJSON);
    }

    private Integer applyForLoanApplication(final Integer clientID, final Integer loanProductID) {
        System.out.println("--------------------------------APPLYING FOR LOAN APPLICATION--------------------------------");
        String loanApplicationJSON = new LoanApplicationTestBuilder().withPrincipal(LP_PRINCIPAL).withLoanTermFrequency(LOAN_TERM_FREQUENCY)
                .withLoanTermFrequencyAsMonths().withNumberOfRepayments(LP_REPAYMENTS).withRepaymentEveryAfter(LP_REPAYMENT_PERIOD)
                .withRepaymentFrequencyTypeAsMonths().withInterestRateFrequencyTypeAsMonths()
                .withInterestRatePerPeriod(LP_INTEREST_RATE).withInterestTypeAsFlatBalance()
                .withAmortizationTypeAsEqualPrincipalPayments().withInterestCalculationPeriodTypeSameAsRepaymentPeriod()
                .withExpectedDisbursementDate(EXPECTED_DISBURSAL_DATE).withSubmittedOnDate(LOAN_APPLICATION_SUBMISSION_DATE)
                .build(clientID.toString(), loanProductID.toString());
        return LoanTransactionHelper.getLoanId(requestSpec, responseSpec, loanApplicationJSON);
    }
}

