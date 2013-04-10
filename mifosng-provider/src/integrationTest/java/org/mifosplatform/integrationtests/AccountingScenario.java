package org.mifosplatform.integrationtests;


import com.jayway.restassured.builder.RequestSpecBuilder;
import com.jayway.restassured.builder.ResponseSpecBuilder;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.specification.RequestSpecification;
import com.jayway.restassured.specification.ResponseSpecification;
import org.junit.Before;


import org.junit.Test;
import org.mifosplatform.integrationtests.common.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

public class AccountingScenario {
    RequestSpecification requestSpec;
    ResponseSpecification responseSpec;

    String CREATE_GL_ACCOUNT_URL = "/mifosng-provider/api/v1/glaccounts?tenantIdentifier=default";
    String GL_ACCOUNT_ID_RESPONSE = "resourceId";

    String DATE_OF_JOINING = "01 January 2011";

    String LP_PRINCIPAL="10,000.00";
    String LP_REPAYMENTS = "5";
    String LP_REPAYMENT_PERIOD = "2";
    String LP_INTEREST_RATE="1";
    String EXPECTED_DISBURSAL_DATE="04 March 2011";
    String LOAN_APPLICATION_SUBMISSION_DATE= "3 March 2011";
    String LOAN_TERM_FREQUENCY= "10";

    String REPAYMENT_DATE[]={"","04 May 2011","04 July 2011","04 September 2011","04 November 2011","04 January 2012"};
    Float REPAYMENT_AMOUNT [] = {.0f,2200.0f,3000.0f,900.0f,2000.0f,2500.0f};
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
        LoanStatusChecker.verifyLoanIsActive(loanStatusHashMap);

        //CHECK ACCOUNT ENTRIES
        System.out.println("Entries ......");
        checkJournalEntryForAsset(assetAccount.getAccountID(),convertDateToURLFormat(EXPECTED_DISBURSAL_DATE),buildAccountEntry());
        System.out.println("CHECKING INCOME: ******************************************");
        checkJournalEntryForIncome(incomeAccount.getAccountID(), buildAccountEntry_two());

        //MAKE 1
        System.out.println("Repayment 1 ......");
        LoanTransactionHelper.makeRepayment(requestSpec, responseSpec, REPAYMENT_DATE[1], REPAYMENT_AMOUNT[1], loanID);
        LoanTransactionHelper.verifyRepaymentScheduleEntryFor(requestSpec,responseSpec,1,8000.0f,loanID);
        checkJournalEntryForAsset(assetAccount.getAccountID(),convertDateToURLFormat(REPAYMENT_DATE[1]),buildAccountEntry_three());
        System.out.println("Repayment 1 Done......");

        //REPAYMENT 2
        System.out.println("Repayment 2 ......");
        LoanTransactionHelper.makeRepayment(requestSpec, responseSpec, REPAYMENT_DATE[2], REPAYMENT_AMOUNT[2], loanID);
        LoanTransactionHelper.verifyRepaymentScheduleEntryFor(requestSpec,responseSpec,2,6000.0f,loanID);
        checkJournalEntryForAsset(assetAccount.getAccountID(),convertDateToURLFormat(REPAYMENT_DATE[2]),buildAccountEntry_four());
        System.out.println("Repayment 2 Done ......");

        //REPAYMENt 3
        System.out.println("Repayment 3 ......");
        LoanTransactionHelper.waiveInterest(requestSpec,responseSpec,REPAYMENT_DATE[4],"400.0",loanID);
        LoanTransactionHelper.makeRepayment(requestSpec, responseSpec, REPAYMENT_DATE[3], REPAYMENT_AMOUNT[3], loanID);
        LoanTransactionHelper.verifyRepaymentScheduleEntryFor(requestSpec,responseSpec,3,4000.0f,loanID);

        //For Waived amount
        System.out.println("Waive Interest ......");
        checkJournalEntryForAsset(assetAccount.getAccountID(), convertDateToURLFormat(REPAYMENT_DATE[4]), buildAccountEntry_five());
        System.out.println("Waive checking entry for asset account done  ......");
        checkJournalEntryForSpecificType(expenseAccount.getAccountID(),GLAccountBuilder.EXPENSE_ACCOUNT,convertDateToURLFormat(REPAYMENT_DATE[4]),buildAccountEntry_seven());
        System.out.println("Waive Interest Done......");
        //FOr Paid Amount
        checkJournalEntryForAsset(assetAccount.getAccountID(), convertDateToURLFormat(REPAYMENT_DATE[3]), buildAccountEntry_six());
        System.out.println("Repayment 3 Done ......");

        //Repayment 4
        LoanTransactionHelper.makeRepayment(requestSpec, responseSpec, REPAYMENT_DATE[4], REPAYMENT_AMOUNT[4], loanID);
        LoanTransactionHelper.verifyRepaymentScheduleEntryFor(requestSpec,responseSpec,4,2000.0f,loanID);
        checkJournalEntryForAsset(assetAccount.getAccountID(),convertDateToURLFormat(REPAYMENT_DATE[4]),buildAccountEntry_eight());
        System.out.println("Repayment 4 Done  ......");

        //Repayment 5
        LoanTransactionHelper.makeRepayment(requestSpec, responseSpec, REPAYMENT_DATE[5], REPAYMENT_AMOUNT[5], loanID);
        LoanTransactionHelper.verifyRepaymentScheduleEntryFor(requestSpec,responseSpec,5,0.0f,loanID);
        checkJournalEntryForAsset(assetAccount.getAccountID(),convertDateToURLFormat(REPAYMENT_DATE[5]),buildAccountEntry_nine());
        System.out.println("Repayment 5 Done  ......");


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

    private void checkJournalEntryForAsset(Integer assetAccountID, String date, AccountEntry... accountEntries){
                checkJournalEntryForAsset(assetAccountID, date,date, accountEntries);
    }
    private void checkJournalEntryForAsset(Integer assetAccountID, String fromDate, String toDate, AccountEntry... accountEntries){
         String getAssetEntryURL= "/mifosng-provider/api/v1/journalentries?glAccountId="+assetAccountID+"&type="+
                 GLAccountBuilder.ASSET_ACCOUNT +"&fromDate="+fromDate+"&toDate="+toDate+"&tenantIdentifier=default";
         ArrayList <HashMap> response = Utils.performServerGet(requestSpec,responseSpec,getAssetEntryURL,"");
         for(int i=0;i<accountEntries.length;i++)
         {
           assertThat(getEntryValueFromJournalEntry(response,i),equalTo(accountEntries[i].getTransactionType()));
           assertThat(getTransactionAmountFromJournalEntry(response,i),equalTo(accountEntries[i].getTransactionAmount()));
         }
    }

    private void checkJournalEntryForSpecificType(Integer accountID, String accountType, String date, AccountEntry... accountEntries){
        String getAssetEntryURL= "/mifosng-provider/api/v1/journalentries?glAccountId="+accountID+"&type="+
                                  accountType+"&fromDate="+date+"&toDate="+date+"&tenantIdentifier=default";
        ArrayList <HashMap> response = Utils.performServerGet(requestSpec,responseSpec,getAssetEntryURL,"");
        for(int i=0;i<accountEntries.length;i++)
        {
            assertThat(getEntryValueFromJournalEntry(response,i),equalTo(accountEntries[i].getTransactionType()));
            assertThat(getTransactionAmountFromJournalEntry(response,i),equalTo(accountEntries[i].getTransactionAmount()));
        }

    }

    private void checkJournalEntryForIncome(Integer incomeAccountID, AccountEntry ... accountEntries) {
        String getIncomeEntryURL= "/mifosng-provider/api/v1/journalentries?glAccountId="+incomeAccountID+"&type="+ GLAccountBuilder.INCOME_ACCOUNT +"&tenantIdentifier=default";
        ArrayList <HashMap> response = Utils.performServerGet(requestSpec,responseSpec,getIncomeEntryURL,"");
        assertThat(getEntryValueFromJournalEntry(response,0),equalTo(accountEntries[0].getTransactionType()));
        assertThat(getTransactionAmountFromJournalEntry(response,0),equalTo(accountEntries[0].getTransactionAmount()));
    }


    private AccountEntry[] buildAccountEntry(){
       AccountEntry[] entries = { new AccountEntry(1000.0f,AccountEntry.TransactionType.DEBIT),
                                  new AccountEntry(10000.0f,AccountEntry.TransactionType.CREDIT),
                                  new AccountEntry(10000.0f,AccountEntry.TransactionType.DEBIT),
                                };
       return entries;
    }

    private AccountEntry buildAccountEntry_two(){
        return new AccountEntry(1000.0f,AccountEntry.TransactionType.CREDIT);
    }

    private AccountEntry[] buildAccountEntry_three(){
        AccountEntry[] entries = { new AccountEntry(2200.0f,AccountEntry.TransactionType.DEBIT),
                                   new AccountEntry(200.0f,AccountEntry.TransactionType.CREDIT),
                                   new AccountEntry(2000.0f,AccountEntry.TransactionType.CREDIT),
        };
        return entries;
    }

    private AccountEntry[] buildAccountEntry_four(){
        AccountEntry[] entries = { new AccountEntry(3000.0f,AccountEntry.TransactionType.DEBIT),
                                   new AccountEntry(400.0f,AccountEntry.TransactionType.CREDIT),
                                   new AccountEntry(2600.0f,AccountEntry.TransactionType.CREDIT),
        };
        return entries;
    }
    private AccountEntry buildAccountEntry_five(){
        return new AccountEntry(400.0f,AccountEntry.TransactionType.CREDIT);
    }

    private AccountEntry[] buildAccountEntry_six(){
        AccountEntry[] entries = { new AccountEntry(900.0f,AccountEntry.TransactionType.DEBIT),
                                   new AccountEntry(900.0f,AccountEntry.TransactionType.CREDIT)
        };
        return entries;
    }

    private AccountEntry buildAccountEntry_seven(){
        return  new AccountEntry(400.0f,AccountEntry.TransactionType.DEBIT) ;
    }
    private AccountEntry[] buildAccountEntry_eight(){
        AccountEntry[] entries = { new AccountEntry(2000.0f,AccountEntry.TransactionType.DEBIT),
                                   new AccountEntry(2000.0f,AccountEntry.TransactionType.CREDIT)
        };
        return entries;
    }
     private AccountEntry[] buildAccountEntry_nine(){
        AccountEntry[] entries = { new AccountEntry(2500.0f,AccountEntry.TransactionType.DEBIT),
                                   new AccountEntry(2500.0f,AccountEntry.TransactionType.CREDIT)
        };
        return entries;
    }

    private String getEntryValueFromJournalEntry(ArrayList <HashMap> entryResponse,int entryNumber){
        HashMap map = (HashMap) entryResponse.get(entryNumber).get("entryType");
        return (String) map.get("value");
    }

    private Float getTransactionAmountFromJournalEntry(ArrayList<HashMap> entryResponse, int entryNumber){
        return (Float)entryResponse.get(entryNumber).get("amount");
    }

    private String convertDateToURLFormat(String dateToBeConvert){
        SimpleDateFormat oldFormat = new SimpleDateFormat("dd MMMMMM yyyy");
        SimpleDateFormat newFormat = new SimpleDateFormat("yyyy-MM-dd");
        String reformattedStr="";
        try {
            reformattedStr = newFormat.format(oldFormat.parse(dateToBeConvert));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return reformattedStr;
    }
}

