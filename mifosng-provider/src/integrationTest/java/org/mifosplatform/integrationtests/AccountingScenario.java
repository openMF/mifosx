package org.mifosplatform.integrationtests;


import com.jayway.restassured.builder.RequestSpecBuilder;
import com.jayway.restassured.builder.ResponseSpecBuilder;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.specification.RequestSpecification;
import com.jayway.restassured.specification.ResponseSpecification;
import org.junit.Before;


import org.junit.Test;
import org.mifosplatform.integrationtests.common.*;

import java.util.HashMap;

import static org.junit.Assert.assertThat;

public class AccountingScenario {
    RequestSpecification requestSpec;
    ResponseSpecification responseSpec;

    String CREATE_GL_ACCOUNT_URL = "/mifosng-provider/api/v1/glaccounts?tenantIdentifier=default";
    String GL_ACCOUNT_ID_RESPONSE = "resourceId";

    String DATE_OF_JOINING = "01 January 2011";

    Float LP_PRINCIPAL=10000.0f;
    String LP_REPAYMENTS = "5";
    String LP_REPAYMENT_PERIOD = "2";
    String LP_INTEREST_RATE="1";
    String EXPECTED_DISBURSAL_DATE="04 March 2011";
    String LOAN_APPLICATION_SUBMISSION_DATE= "3 March 2011";
    String LOAN_TERM_FREQUENCY= "10";

    String REPAYMENT_DATE[]={"","04 May 2011","04 July 2011","04 September 2011","04 November 2011","04 January 2012"};
    Float REPAYMENT_AMOUNT [] = {.0f,2200.0f,3000.0f,900.0f,2000.0f,2500.0f};

    Float amount_to_be_waive = 400.0f;

    LoanTransactionHelper loanTransactionHelper;

    @Before
    public void setup() {
        Utils.initializeRESTAssured();
        requestSpec = new RequestSpecBuilder().setContentType(ContentType.JSON).build();
        requestSpec.header("Authorization", "Basic " + Utils.loginIntoServerAndGetBase64EncodedAuthenticationKey());
        responseSpec = new ResponseSpecBuilder().expectStatusCode(200).build();

        loanTransactionHelper = new LoanTransactionHelper(requestSpec,responseSpec);
    }

    @Test
    public void checkAccountingFlow() {
        Account assetAccount  = createAssetAccount();
        Account incomeAccount = createIncomeAccount();
        Account expenseAccount= createExpenseAccount();

        Integer loanProductID = createLoanProduct(assetAccount,incomeAccount,expenseAccount);

        Integer clientID = ClientHelper.createClient(requestSpec, responseSpec, DATE_OF_JOINING);
        Integer loanID = applyForLoanApplication(clientID, loanProductID);

        HashMap loanStatusHashMap = LoanStatusChecker.getStatusOfLoan(requestSpec, responseSpec, loanID);
        LoanStatusChecker.verifyLoanIsPending(loanStatusHashMap);

        loanStatusHashMap = loanTransactionHelper.approveLoan(EXPECTED_DISBURSAL_DATE, loanID);
        LoanStatusChecker.verifyLoanIsApproved(loanStatusHashMap);
        LoanStatusChecker.verifyLoanIsWaitingForDisbursal(loanStatusHashMap);

        loanStatusHashMap = loanTransactionHelper.disburseLoan(EXPECTED_DISBURSAL_DATE, loanID);
        LoanStatusChecker.verifyLoanIsActive(loanStatusHashMap);

        JournalEntryHelper journalEntryHelper = new JournalEntryHelper(requestSpec, responseSpec);
        
        //CHECK ACCOUNT ENTRIES
        System.out.println("Entries ......");
        AccountEntry[] assetAccountInitialEntry = { new AccountEntry(1000.0f,AccountEntry.TransactionType.DEBIT),
                                   new AccountEntry(LP_PRINCIPAL,AccountEntry.TransactionType.CREDIT),
                                   new AccountEntry(LP_PRINCIPAL,AccountEntry.TransactionType.DEBIT),
        };
        journalEntryHelper.checkJournalEntryForAssetAccount(assetAccount, EXPECTED_DISBURSAL_DATE, assetAccountInitialEntry);
        System.out.println("CHECKING INCOME: ******************************************");
        AccountEntry incomeAccountEntry = new AccountEntry(1000.0f, AccountEntry.TransactionType.CREDIT);
        journalEntryHelper.checkJournalEntryForIncomeAccount(incomeAccount,EXPECTED_DISBURSAL_DATE, incomeAccountEntry);

        //MAKE 1
        System.out.println("Repayment 1 ......");
        loanTransactionHelper.makeRepayment(REPAYMENT_DATE[1], REPAYMENT_AMOUNT[1], loanID);
        loanTransactionHelper.verifyRepaymentScheduleEntryFor(1,8000.0f,loanID);
        AccountEntry[] assetAccountFirstEntry=  { new AccountEntry(2200.0f,AccountEntry.TransactionType.DEBIT),
                               new AccountEntry(200.0f,AccountEntry.TransactionType.CREDIT),
                                new AccountEntry(2000.0f,AccountEntry.TransactionType.CREDIT),
        };
        journalEntryHelper.checkJournalEntryForAssetAccount(assetAccount, REPAYMENT_DATE[1],assetAccountFirstEntry);
        System.out.println("Repayment 1 Done......");

        //REPAYMENT 2
        System.out.println("Repayment 2 ......");
        loanTransactionHelper.makeRepayment(REPAYMENT_DATE[2], REPAYMENT_AMOUNT[2], loanID);
        loanTransactionHelper.verifyRepaymentScheduleEntryFor(2,6000.0f,loanID);

        AccountEntry[] assetAccountSecondEntry = { new AccountEntry(REPAYMENT_AMOUNT[2],AccountEntry.TransactionType.DEBIT),
                                                   new AccountEntry(400.0f,AccountEntry.TransactionType.CREDIT),
                                                   new AccountEntry(2600.0f,AccountEntry.TransactionType.CREDIT),
        };
        journalEntryHelper.checkJournalEntryForAssetAccount(assetAccount, REPAYMENT_DATE[2], assetAccountSecondEntry);
        System.out.println("Repayment 2 Done ......");

        //WAIVE INTEREST
        System.out.println("Waive Interest  ......");
        loanTransactionHelper.waiveInterest(REPAYMENT_DATE[4],amount_to_be_waive.toString(),loanID);

        AccountEntry waivedEntry = new  AccountEntry(amount_to_be_waive,AccountEntry.TransactionType.CREDIT);
        journalEntryHelper.checkJournalEntryForAssetAccount(assetAccount, REPAYMENT_DATE[4],waivedEntry);

        AccountEntry expenseAccountEntry = new AccountEntry(amount_to_be_waive, AccountEntry.TransactionType.DEBIT);
        journalEntryHelper.checkJournalEntryForExpenseAccount(expenseAccount, REPAYMENT_DATE[4], expenseAccountEntry);
        System.out.println("Waive Interest Done......");

        //REPAYMENT 3
        loanTransactionHelper.makeRepayment(REPAYMENT_DATE[3], REPAYMENT_AMOUNT[3], loanID);
        AccountEntry[] assetAccountThirdEntry = {
                new AccountEntry(REPAYMENT_AMOUNT[3],AccountEntry.TransactionType.DEBIT),
                new AccountEntry(REPAYMENT_AMOUNT[3],AccountEntry.TransactionType.CREDIT)
        };
        loanTransactionHelper.verifyRepaymentScheduleEntryFor(3, 4000.0f, loanID);
        journalEntryHelper.checkJournalEntryForAssetAccount(assetAccount, REPAYMENT_DATE[3], assetAccountThirdEntry);

        //REPAYMENT 4
        loanTransactionHelper.makeRepayment(REPAYMENT_DATE[4], REPAYMENT_AMOUNT[4], loanID);
        loanTransactionHelper.verifyRepaymentScheduleEntryFor(4,2000.0f,loanID);
        AccountEntry[] assetAccountFourthEntry = { new AccountEntry(2000.0f,AccountEntry.TransactionType.DEBIT),
                                                   new AccountEntry(2000.0f,AccountEntry.TransactionType.CREDIT)
        };
        journalEntryHelper.checkJournalEntryForAssetAccount(assetAccount,REPAYMENT_DATE[4],assetAccountFourthEntry);
        System.out.println("Repayment 4 Done  ......");

        //Repayment 5

        AccountEntry[] assetAccountFifthEntry = { new AccountEntry(REPAYMENT_AMOUNT[5],AccountEntry.TransactionType.DEBIT),
                                                  new AccountEntry(REPAYMENT_AMOUNT[5],AccountEntry.TransactionType.CREDIT)
        };
        loanTransactionHelper.makeRepayment(REPAYMENT_DATE[5], REPAYMENT_AMOUNT[5], loanID);
        loanTransactionHelper.verifyRepaymentScheduleEntryFor(5,0.0f,loanID);
        journalEntryHelper.checkJournalEntryForAssetAccount(assetAccount, REPAYMENT_DATE[5], assetAccountFifthEntry);
        System.out.println("Repayment 5 Done  ......");
    }


    public Account createAssetAccount(){
        String assetAccountJSON= new GLAccountBuilder()
                .withAccountTypeAsAsset()
                .build();
        Integer accountID = Utils.performServerPost(requestSpec, responseSpec, CREATE_GL_ACCOUNT_URL, assetAccountJSON, GL_ACCOUNT_ID_RESPONSE);
        return new Account(accountID, Account.AccountType.ASSET);
    }
    public Account createIncomeAccount(){
        String assetAccountJSON= new GLAccountBuilder()
                .withAccountTypeAsIncome()
                .build();
        Integer accountID = Utils.performServerPost(requestSpec, responseSpec, CREATE_GL_ACCOUNT_URL, assetAccountJSON, GL_ACCOUNT_ID_RESPONSE);
        return new Account(accountID, Account.AccountType.INCOME);
    }
    public Account createExpenseAccount(){
        String assetAccountJSON= new GLAccountBuilder()
                .withAccountTypeAsExpense()
                .build();
        Integer accountID = Utils.performServerPost(requestSpec, responseSpec, CREATE_GL_ACCOUNT_URL, assetAccountJSON, GL_ACCOUNT_ID_RESPONSE);
        return new Account(accountID, Account.AccountType.EXPENSE);
    }

    private Integer createLoanProduct(Account ... accounts) {
        System.out.println("------------------------------CREATING NEW LOAN PRODUCT ---------------------------------------");
        String loanProductJSON = new LoanProductTestBuilder().withPrincipal(LP_PRINCIPAL.toString()).withRepaymentTypeAsMonth()
                .withRepaymentAfterEvery(LP_REPAYMENT_PERIOD).withNumberOfRepayments(LP_REPAYMENTS).withRepaymentTypeAsMonth()
                .withinterestRatePerPeriod(LP_INTEREST_RATE).withInterestRateFrequencyTypeAsMonths()
                .withAmortizationTypeAsEqualPrinciplePayment().withInterestTypeAsFlat()
                .withAccountingRuleAsAccrualBased(accounts)
                .build();
        return loanTransactionHelper.getLoanProductId(requestSpec, responseSpec, loanProductJSON);
    }

    private Integer applyForLoanApplication(final Integer clientID, final Integer loanProductID) {
        System.out.println("--------------------------------APPLYING FOR LOAN APPLICATION--------------------------------");
        String loanApplicationJSON = new LoanApplicationTestBuilder().withPrincipal(LP_PRINCIPAL.toString()).withLoanTermFrequency(LOAN_TERM_FREQUENCY)
                .withLoanTermFrequencyAsMonths().withNumberOfRepayments(LP_REPAYMENTS).withRepaymentEveryAfter(LP_REPAYMENT_PERIOD)
                .withRepaymentFrequencyTypeAsMonths().withInterestRateFrequencyTypeAsMonths()
                .withInterestRatePerPeriod(LP_INTEREST_RATE).withInterestTypeAsFlatBalance()
                .withAmortizationTypeAsEqualPrincipalPayments().withInterestCalculationPeriodTypeSameAsRepaymentPeriod()
                .withExpectedDisbursementDate(EXPECTED_DISBURSAL_DATE).withSubmittedOnDate(LOAN_APPLICATION_SUBMISSION_DATE)
                .build(clientID.toString(), loanProductID.toString());
        return loanTransactionHelper.getLoanId(requestSpec, responseSpec, loanApplicationJSON);
    }








}

