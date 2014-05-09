package org.mifosplatform.integrationtests;

import static org.junit.Assert.assertEquals;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Months;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mifosplatform.integrationtests.common.ClientHelper;
import org.mifosplatform.integrationtests.common.CommonConstants;
import org.mifosplatform.integrationtests.common.Utils;
import org.mifosplatform.integrationtests.common.accounting.Account;
import org.mifosplatform.integrationtests.common.accounting.AccountHelper;
import org.mifosplatform.integrationtests.common.fixeddeposit.FixedDepositAccountHelper;
import org.mifosplatform.integrationtests.common.fixeddeposit.FixedDepositAccountStatusChecker;
import org.mifosplatform.integrationtests.common.fixeddeposit.FixedDepositProductHelper;
import org.mifosplatform.integrationtests.common.loans.LoanApplicationTestBuilder;
import org.mifosplatform.integrationtests.common.savings.SavingsAccountHelper;
import org.mifosplatform.integrationtests.common.savings.SavingsProductHelper;
import org.mifosplatform.integrationtests.common.savings.SavingsStatusChecker;

import com.jayway.restassured.builder.RequestSpecBuilder;
import com.jayway.restassured.builder.ResponseSpecBuilder;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.specification.RequestSpecification;
import com.jayway.restassured.specification.ResponseSpecification;

@SuppressWarnings({ "unused", "unchecked", "rawtypes", "static-access" })
public class FixedDepositTest {

    private ResponseSpecification responseSpec;
    private RequestSpecification requestSpec;
    private FixedDepositProductHelper fixedDepositProductHelper;
    private FixedDepositAccountHelper fixedDepositAccountHelper;
    private AccountHelper accountHelper;
    private SavingsAccountHelper savingsAccountHelper;

    private static final String WHOLE_TERM = "1";
    private static final String TILL_PREMATURE_WITHDRAWAL = "2";
    private static final String DAILY = "1";
    private static final String MONTHLY = "4";
    private static final String QUARTERLY = "5";
    private static final String ANNUALLY = "7";
    private static final String INTEREST_CALCULATION_USING_DAILY_BALANCE = "1";
    private static final String INTEREST_CALCULATION_USING_AVERAGE_DAILY_BALANCE = "2";
    private static final String DAYS_360 = "360";
    private static final String DAYS_365 = "365";

    public static final String MINIMUM_OPENING_BALANCE = "1000.0";
    public static final String ACCOUNT_TYPE_INDIVIDUAL = "INDIVIDUAL";
    public static final String CLOSURE_TYPE_WITHDRAW_DEPOSIT = "100";
    public static final String CLOSURE_TYPE_TRANSFER_TO_SAVINGS = "200";
    public static final String CLOSURE_TYPE_REINVEST = "300";

    @Before
    public void setup() {
        Utils.initializeRESTAssured();
        this.requestSpec = new RequestSpecBuilder().setContentType(ContentType.JSON).build();
        this.requestSpec.header("Authorization", "Basic " + Utils.loginIntoServerAndGetBase64EncodedAuthenticationKey());
        this.requestSpec.header("X-Mifos-Platform-TenantId", "default");
        this.responseSpec = new ResponseSpecBuilder().expectStatusCode(200).build();
    }

    @Test
    public void testFixedDepositAccountWithPrematureClosureTypeWithdrawal() {
        this.fixedDepositProductHelper = new FixedDepositProductHelper(this.requestSpec, this.responseSpec);
        this.accountHelper = new AccountHelper(this.requestSpec, this.responseSpec);
        this.savingsAccountHelper = new SavingsAccountHelper(this.requestSpec, this.responseSpec);
        this.fixedDepositAccountHelper = new FixedDepositAccountHelper(this.requestSpec, this.responseSpec);

        DateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy");
        DateFormat monthDayFormat = new SimpleDateFormat("dd MMM");

        Calendar todaysDate = Calendar.getInstance();
        todaysDate.add(Calendar.MONTH, -3);
        final String VALID_FROM = dateFormat.format(todaysDate.getTime());
        todaysDate.add(Calendar.YEAR, 10);
        final String VALID_TO = dateFormat.format(todaysDate.getTime());

        todaysDate = Calendar.getInstance();
        todaysDate.add(Calendar.MONTH, -1);
        final String SUBMITTED_ON_DATE = dateFormat.format(todaysDate.getTime());
        final String APPROVED_ON_DATE = dateFormat.format(todaysDate.getTime());
        final String ACTIVATION_DATE = dateFormat.format(todaysDate.getTime());
        final String MONTH_DAY = monthDayFormat.format(todaysDate.getTime());
        todaysDate.add(Calendar.MONTH, 1);
        final String CLOSED_ON_DATE = dateFormat.format(todaysDate.getTime());

        final Account assetAccount = this.accountHelper.createAssetAccount();
        final Account incomeAccount = this.accountHelper.createIncomeAccount();
        final Account expenseAccount = this.accountHelper.createExpenseAccount();
        final Account liabilityAccount = this.accountHelper.createLiabilityAccount();

        Integer clientId = ClientHelper.createClient(this.requestSpec, this.responseSpec);
        Assert.assertNotNull(clientId);

        Integer fixedDepositProductId = createFixedDepositProduct(VALID_FROM, VALID_TO);
        Assert.assertNotNull(fixedDepositProductId);

        Integer fixedDepositAccountId = applyForFixedDepositApplication(clientId.toString(), fixedDepositProductId.toString(), VALID_FROM,
                VALID_TO, SUBMITTED_ON_DATE, WHOLE_TERM, null);
        Assert.assertNotNull(fixedDepositAccountId);

        HashMap fixedDepositAccountStatusHashMap = FixedDepositAccountStatusChecker.getStatusOfFixedDepositAccount(this.requestSpec,
                this.responseSpec, fixedDepositAccountId.toString());
        FixedDepositAccountStatusChecker.verifyFixedDepositIsPending(fixedDepositAccountStatusHashMap);

        fixedDepositAccountStatusHashMap = this.fixedDepositAccountHelper.approveFixedDeposit(fixedDepositAccountId, APPROVED_ON_DATE);
        FixedDepositAccountStatusChecker.verifyFixedDepositIsApproved(fixedDepositAccountStatusHashMap);

        fixedDepositAccountStatusHashMap = this.fixedDepositAccountHelper.activateFixedDeposit(fixedDepositAccountId, ACTIVATION_DATE);
        FixedDepositAccountStatusChecker.verifyFixedDepositIsActive(fixedDepositAccountStatusHashMap);

        fixedDepositAccountId = this.fixedDepositAccountHelper.calculateInterestForFixedDeposit(fixedDepositAccountId);
        Assert.assertNotNull(fixedDepositAccountId);

        Integer transactionIdForPostInterest = this.fixedDepositAccountHelper.postInterestForFixedDeposit(fixedDepositAccountId);
        Assert.assertNotNull(transactionIdForPostInterest);

        HashMap fixedDepositPrematureData = this.fixedDepositAccountHelper.calculatePrematureAmountForFixedDeposit(fixedDepositAccountId,
                CLOSED_ON_DATE);

        Integer prematureClosureTransactionId = (Integer) this.fixedDepositAccountHelper.prematureCloseForFixedDeposit(
                fixedDepositAccountId, CLOSED_ON_DATE, CLOSURE_TYPE_WITHDRAW_DEPOSIT, null, CommonConstants.RESPONSE_RESOURCE_ID);
        Assert.assertNotNull(prematureClosureTransactionId);

        fixedDepositAccountStatusHashMap = FixedDepositAccountStatusChecker.getStatusOfFixedDepositAccount(this.requestSpec,
                this.responseSpec, fixedDepositAccountId.toString());
        FixedDepositAccountStatusChecker.verifyFixedDepositAccountIsPrematureClosed(fixedDepositAccountStatusHashMap);

    }

    @Test
    public void testFixedDepositAccountUpdation() {
        this.fixedDepositProductHelper = new FixedDepositProductHelper(this.requestSpec, this.responseSpec);
        this.fixedDepositAccountHelper = new FixedDepositAccountHelper(this.requestSpec, this.responseSpec);

        DateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy");
        DateFormat monthDayFormat = new SimpleDateFormat("dd MMM");

        Calendar todaysDate = Calendar.getInstance();
        todaysDate.add(Calendar.MONTH, -3);
        final String VALID_FROM = dateFormat.format(todaysDate.getTime());
        todaysDate.add(Calendar.YEAR, 10);
        final String VALID_TO = dateFormat.format(todaysDate.getTime());

        todaysDate = Calendar.getInstance();
        todaysDate.add(Calendar.MONTH, -1);
        final String MONTH_DAY = monthDayFormat.format(todaysDate.getTime());
        String SUBMITTED_ON_DATE = dateFormat.format(todaysDate.getTime());

        Integer clientId = ClientHelper.createClient(this.requestSpec, this.responseSpec);
        Assert.assertNotNull(clientId);

        Integer fixedDepositProductId = createFixedDepositProduct(VALID_FROM, VALID_TO);
        Assert.assertNotNull(fixedDepositProductId);

        ArrayList<HashMap> allFixedDepositProductsData = this.fixedDepositProductHelper.retrieveAllFixedDepositProducts(this.requestSpec,
                this.responseSpec);
        HashMap fixedDepositProductData = this.fixedDepositProductHelper.retrieveFixedDepositProductById(this.requestSpec,
                this.responseSpec, fixedDepositProductId.toString());

        Integer fixedDepositAccountId = applyForFixedDepositApplication(clientId.toString(), fixedDepositProductId.toString(), VALID_FROM,
                VALID_TO, SUBMITTED_ON_DATE, WHOLE_TERM, null);
        Assert.assertNotNull(fixedDepositAccountId);

        todaysDate.add(Calendar.DATE, -1);
        SUBMITTED_ON_DATE = dateFormat.format(todaysDate.getTime());
        HashMap modificationsHashMap = this.fixedDepositAccountHelper.updateFixedDepositAccount(clientId.toString(),
                fixedDepositProductId.toString(), fixedDepositAccountId.toString(), VALID_FROM, VALID_TO, WHOLE_TERM, SUBMITTED_ON_DATE);
        Assert.assertTrue(modificationsHashMap.containsKey("submittedOnDate"));

    }

    @Test
    public void testFixedDepositAccountWithPrematureClosureTypeTransferToSavings() {
        this.fixedDepositProductHelper = new FixedDepositProductHelper(this.requestSpec, this.responseSpec);
        this.accountHelper = new AccountHelper(this.requestSpec, this.responseSpec);
        this.savingsAccountHelper = new SavingsAccountHelper(this.requestSpec, this.responseSpec);
        this.fixedDepositAccountHelper = new FixedDepositAccountHelper(this.requestSpec, this.responseSpec);

        DateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy");
        DateFormat monthDayFormat = new SimpleDateFormat("dd MMM");

        Calendar todaysDate = Calendar.getInstance();
        todaysDate.add(Calendar.MONTH, -3);
        final String VALID_FROM = dateFormat.format(todaysDate.getTime());
        todaysDate.add(Calendar.YEAR, 10);
        final String VALID_TO = dateFormat.format(todaysDate.getTime());

        todaysDate = Calendar.getInstance();
        todaysDate.add(Calendar.MONTH, -1);
        final String SUBMITTED_ON_DATE = dateFormat.format(todaysDate.getTime());
        final String APPROVED_ON_DATE = dateFormat.format(todaysDate.getTime());
        final String ACTIVATION_DATE = dateFormat.format(todaysDate.getTime());
        final String MONTH_DAY = monthDayFormat.format(todaysDate.getTime());
        todaysDate.add(Calendar.MONTH, 1);
        final String CLOSED_ON_DATE = dateFormat.format(todaysDate.getTime());

        Integer clientId = ClientHelper.createClient(this.requestSpec, this.responseSpec);
        Assert.assertNotNull(clientId);

        final Integer savingsProductID = createSavingsProduct(this.requestSpec, this.responseSpec, MINIMUM_OPENING_BALANCE);
        Assert.assertNotNull(savingsProductID);

        final Integer savingsId = this.savingsAccountHelper.applyForSavingsApplication(clientId, savingsProductID, ACCOUNT_TYPE_INDIVIDUAL);
        Assert.assertNotNull(savingsProductID);

        HashMap savingsStatusHashMap = SavingsStatusChecker.getStatusOfSavings(this.requestSpec, this.responseSpec, savingsId);
        SavingsStatusChecker.verifySavingsIsPending(savingsStatusHashMap);

        savingsStatusHashMap = this.savingsAccountHelper.approveSavings(savingsId);
        SavingsStatusChecker.verifySavingsIsApproved(savingsStatusHashMap);

        savingsStatusHashMap = this.savingsAccountHelper.activateSavings(savingsId);
        SavingsStatusChecker.verifySavingsIsActive(savingsStatusHashMap);

        Integer fixedDepositProductId = createFixedDepositProduct(VALID_FROM, VALID_TO);
        Assert.assertNotNull(fixedDepositProductId);

        Integer fixedDepositAccountId = applyForFixedDepositApplication(clientId.toString(), fixedDepositProductId.toString(), VALID_FROM,
                VALID_TO, SUBMITTED_ON_DATE, WHOLE_TERM, null);
        Assert.assertNotNull(fixedDepositAccountId);

        HashMap fixedDepositAccountStatusHashMap = FixedDepositAccountStatusChecker.getStatusOfFixedDepositAccount(this.requestSpec,
                this.responseSpec, fixedDepositAccountId.toString());
        FixedDepositAccountStatusChecker.verifyFixedDepositIsPending(fixedDepositAccountStatusHashMap);

        fixedDepositAccountStatusHashMap = this.fixedDepositAccountHelper.approveFixedDeposit(fixedDepositAccountId, APPROVED_ON_DATE);
        FixedDepositAccountStatusChecker.verifyFixedDepositIsApproved(fixedDepositAccountStatusHashMap);

        fixedDepositAccountStatusHashMap = this.fixedDepositAccountHelper.activateFixedDeposit(fixedDepositAccountId, ACTIVATION_DATE);
        FixedDepositAccountStatusChecker.verifyFixedDepositIsActive(fixedDepositAccountStatusHashMap);

        fixedDepositAccountId = this.fixedDepositAccountHelper.calculateInterestForFixedDeposit(fixedDepositAccountId);
        Assert.assertNotNull(fixedDepositAccountId);

        Integer transactionIdForPostInterest = this.fixedDepositAccountHelper.postInterestForFixedDeposit(fixedDepositAccountId);
        Assert.assertNotNull(transactionIdForPostInterest);

        HashMap savingsSummaryBefore = this.savingsAccountHelper.getSavingsSummary(savingsId);

        Float balanceBefore = (Float) savingsSummaryBefore.get("accountBalance");

        HashMap fixedDepositPrematureData = this.fixedDepositAccountHelper.calculatePrematureAmountForFixedDeposit(fixedDepositAccountId,
                CLOSED_ON_DATE);

        Integer prematureClosureTransactionId = (Integer) this.fixedDepositAccountHelper.prematureCloseForFixedDeposit(
                fixedDepositAccountId, CLOSED_ON_DATE, CLOSURE_TYPE_TRANSFER_TO_SAVINGS, savingsId, CommonConstants.RESPONSE_RESOURCE_ID);
        Assert.assertNotNull(prematureClosureTransactionId);

        fixedDepositAccountStatusHashMap = FixedDepositAccountStatusChecker.getStatusOfFixedDepositAccount(this.requestSpec,
                this.responseSpec, fixedDepositAccountId.toString());
        FixedDepositAccountStatusChecker.verifyFixedDepositAccountIsPrematureClosed(fixedDepositAccountStatusHashMap);

        HashMap fixedDepositData = this.fixedDepositAccountHelper.getFixedDepositAccountById(this.requestSpec, this.responseSpec,
                fixedDepositAccountId);
        Float prematurityAmount = (Float) fixedDepositData.get("maturityAmount");

        HashMap savingsSummaryAfter = this.savingsAccountHelper.getSavingsSummary(savingsId);
        Float balanceAfter = (Float) savingsSummaryAfter.get("accountBalance");
        Float expectedSavingsBalance = balanceBefore + prematurityAmount;

        Assert.assertEquals("Verifying Savings Account Balance after Premature Closure", balanceAfter, expectedSavingsBalance);

    }

    @Test
    public void testFixedDepositAccountWithPrematureClosureTypeReinvest() {
        this.fixedDepositProductHelper = new FixedDepositProductHelper(this.requestSpec, this.responseSpec);
        this.accountHelper = new AccountHelper(this.requestSpec, this.responseSpec);
        this.savingsAccountHelper = new SavingsAccountHelper(this.requestSpec, this.responseSpec);
        this.fixedDepositAccountHelper = new FixedDepositAccountHelper(this.requestSpec, this.responseSpec);

        FixedDepositAccountHelper fixedDepositAccountHelperValidationError = new FixedDepositAccountHelper(this.requestSpec,
                new ResponseSpecBuilder().build());

        DateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy");
        DateFormat monthDayFormat = new SimpleDateFormat("dd MMM");

        Calendar todaysDate = Calendar.getInstance();
        todaysDate.add(Calendar.MONTH, -3);
        final String VALID_FROM = dateFormat.format(todaysDate.getTime());
        todaysDate.add(Calendar.YEAR, 10);
        final String VALID_TO = dateFormat.format(todaysDate.getTime());

        todaysDate = Calendar.getInstance();
        todaysDate.add(Calendar.MONTH, -1);
        final String SUBMITTED_ON_DATE = dateFormat.format(todaysDate.getTime());
        final String APPROVED_ON_DATE = dateFormat.format(todaysDate.getTime());
        final String ACTIVATION_DATE = dateFormat.format(todaysDate.getTime());
        final String MONTH_DAY = monthDayFormat.format(todaysDate.getTime());
        todaysDate.add(Calendar.MONTH, 1);
        final String CLOSED_ON_DATE = dateFormat.format(todaysDate.getTime());

        Integer clientId = ClientHelper.createClient(this.requestSpec, this.responseSpec);
        Assert.assertNotNull(clientId);

        Integer fixedDepositProductId = createFixedDepositProduct(VALID_FROM, VALID_TO);
        Assert.assertNotNull(fixedDepositProductId);

        Integer fixedDepositAccountId = applyForFixedDepositApplication(clientId.toString(), fixedDepositProductId.toString(), VALID_FROM,
                VALID_TO, SUBMITTED_ON_DATE, WHOLE_TERM, null);
        Assert.assertNotNull(fixedDepositAccountId);

        HashMap fixedDepositAccountStatusHashMap = FixedDepositAccountStatusChecker.getStatusOfFixedDepositAccount(this.requestSpec,
                this.responseSpec, fixedDepositAccountId.toString());
        FixedDepositAccountStatusChecker.verifyFixedDepositIsPending(fixedDepositAccountStatusHashMap);

        fixedDepositAccountStatusHashMap = this.fixedDepositAccountHelper.approveFixedDeposit(fixedDepositAccountId, APPROVED_ON_DATE);
        FixedDepositAccountStatusChecker.verifyFixedDepositIsApproved(fixedDepositAccountStatusHashMap);

        fixedDepositAccountStatusHashMap = this.fixedDepositAccountHelper.activateFixedDeposit(fixedDepositAccountId, ACTIVATION_DATE);
        FixedDepositAccountStatusChecker.verifyFixedDepositIsActive(fixedDepositAccountStatusHashMap);

        fixedDepositAccountId = this.fixedDepositAccountHelper.calculateInterestForFixedDeposit(fixedDepositAccountId);
        Assert.assertNotNull(fixedDepositAccountId);

        Integer transactionIdForPostInterest = this.fixedDepositAccountHelper.postInterestForFixedDeposit(fixedDepositAccountId);
        Assert.assertNotNull(transactionIdForPostInterest);

        HashMap fixedDepositPrematureData = this.fixedDepositAccountHelper.calculatePrematureAmountForFixedDeposit(fixedDepositAccountId,
                CLOSED_ON_DATE);

        ArrayList<HashMap> errorResponse = (ArrayList<HashMap>) fixedDepositAccountHelperValidationError.prematureCloseForFixedDeposit(
                fixedDepositAccountId, CLOSED_ON_DATE, CLOSURE_TYPE_REINVEST, null, CommonConstants.RESPONSE_ERROR);

        assertEquals("validation.msg.fixeddepositaccount.onAccountClosureId.reinvest.not.allowed",
                errorResponse.get(0).get(CommonConstants.RESPONSE_ERROR_MESSAGE_CODE));

    }

    @Test
    public void testFixedDepositAccountUndoApproval() {
        this.fixedDepositProductHelper = new FixedDepositProductHelper(this.requestSpec, this.responseSpec);
        this.fixedDepositAccountHelper = new FixedDepositAccountHelper(this.requestSpec, this.responseSpec);

        DateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy");
        DateFormat monthDayFormat = new SimpleDateFormat("dd MMM");

        Calendar todaysDate = Calendar.getInstance();
        todaysDate.add(Calendar.MONTH, -3);
        final String VALID_FROM = dateFormat.format(todaysDate.getTime());
        todaysDate.add(Calendar.YEAR, 10);
        final String VALID_TO = dateFormat.format(todaysDate.getTime());

        todaysDate = Calendar.getInstance();
        todaysDate.add(Calendar.MONTH, -1);
        final String SUBMITTED_ON_DATE = dateFormat.format(todaysDate.getTime());
        final String APPROVED_ON_DATE = dateFormat.format(todaysDate.getTime());
        final String MONTH_DAY = monthDayFormat.format(todaysDate.getTime());

        Integer clientId = ClientHelper.createClient(this.requestSpec, this.responseSpec);
        Assert.assertNotNull(clientId);

        Integer fixedDepositProductId = createFixedDepositProduct(VALID_FROM, VALID_TO);
        Assert.assertNotNull(fixedDepositProductId);

        Integer fixedDepositAccountId = applyForFixedDepositApplication(clientId.toString(), fixedDepositProductId.toString(), VALID_FROM,
                VALID_TO, SUBMITTED_ON_DATE, WHOLE_TERM, null);
        Assert.assertNotNull(fixedDepositAccountId);

        HashMap fixedDepositAccountStatusHashMap = FixedDepositAccountStatusChecker.getStatusOfFixedDepositAccount(this.requestSpec,
                this.responseSpec, fixedDepositAccountId.toString());
        FixedDepositAccountStatusChecker.verifyFixedDepositIsPending(fixedDepositAccountStatusHashMap);

        fixedDepositAccountStatusHashMap = this.fixedDepositAccountHelper.approveFixedDeposit(fixedDepositAccountId, APPROVED_ON_DATE);
        FixedDepositAccountStatusChecker.verifyFixedDepositIsApproved(fixedDepositAccountStatusHashMap);

        fixedDepositAccountStatusHashMap = this.fixedDepositAccountHelper.undoApproval(fixedDepositAccountId);
        FixedDepositAccountStatusChecker.verifyFixedDepositIsPending(fixedDepositAccountStatusHashMap);
    }

    @Test
    public void testFixedDepositAccountRejectedAndClosed() {
        this.fixedDepositProductHelper = new FixedDepositProductHelper(this.requestSpec, this.responseSpec);
        this.accountHelper = new AccountHelper(this.requestSpec, this.responseSpec);
        this.fixedDepositAccountHelper = new FixedDepositAccountHelper(this.requestSpec, this.responseSpec);

        DateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy");
        DateFormat monthDayFormat = new SimpleDateFormat("dd MMM");

        Calendar todaysDate = Calendar.getInstance();
        todaysDate.add(Calendar.MONTH, -3);
        final String VALID_FROM = dateFormat.format(todaysDate.getTime());
        todaysDate.add(Calendar.YEAR, 10);
        final String VALID_TO = dateFormat.format(todaysDate.getTime());

        todaysDate = Calendar.getInstance();
        todaysDate.add(Calendar.MONTH, -1);
        final String SUBMITTED_ON_DATE = dateFormat.format(todaysDate.getTime());
        final String REJECTED_ON_DATE = dateFormat.format(todaysDate.getTime());
        final String MONTH_DAY = monthDayFormat.format(todaysDate.getTime());

        Integer clientId = ClientHelper.createClient(this.requestSpec, this.responseSpec);
        Assert.assertNotNull(clientId);

        Integer fixedDepositProductId = createFixedDepositProduct(VALID_FROM, VALID_TO);
        Assert.assertNotNull(fixedDepositProductId);

        Integer fixedDepositAccountId = applyForFixedDepositApplication(clientId.toString(), fixedDepositProductId.toString(), VALID_FROM,
                VALID_TO, SUBMITTED_ON_DATE, WHOLE_TERM, null);
        Assert.assertNotNull(fixedDepositAccountId);

        HashMap fixedDepositAccountStatusHashMap = FixedDepositAccountStatusChecker.getStatusOfFixedDepositAccount(this.requestSpec,
                this.responseSpec, fixedDepositAccountId.toString());
        FixedDepositAccountStatusChecker.verifyFixedDepositIsPending(fixedDepositAccountStatusHashMap);

        fixedDepositAccountStatusHashMap = this.fixedDepositAccountHelper.rejectApplication(fixedDepositAccountId, REJECTED_ON_DATE);
        FixedDepositAccountStatusChecker.verifyFixedDepositIsRejected(fixedDepositAccountStatusHashMap);
        FixedDepositAccountStatusChecker.verifyFixedDepositAccountIsClosed(fixedDepositAccountStatusHashMap);
    }

    @Test
    public void testFixedDepositAccountWithdrawnByClientAndClosed() {
        this.fixedDepositProductHelper = new FixedDepositProductHelper(this.requestSpec, this.responseSpec);
        this.accountHelper = new AccountHelper(this.requestSpec, this.responseSpec);
        this.fixedDepositAccountHelper = new FixedDepositAccountHelper(this.requestSpec, this.responseSpec);

        DateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy");
        DateFormat monthDayFormat = new SimpleDateFormat("dd MMM");

        Calendar todaysDate = Calendar.getInstance();
        todaysDate.add(Calendar.MONTH, -3);
        final String VALID_FROM = dateFormat.format(todaysDate.getTime());
        todaysDate.add(Calendar.YEAR, 10);
        final String VALID_TO = dateFormat.format(todaysDate.getTime());

        todaysDate = Calendar.getInstance();
        todaysDate.add(Calendar.MONTH, -1);
        final String SUBMITTED_ON_DATE = dateFormat.format(todaysDate.getTime());
        final String WITHDRAWN_ON_DATE = dateFormat.format(todaysDate.getTime());
        final String MONTH_DAY = monthDayFormat.format(todaysDate.getTime());

        Integer clientId = ClientHelper.createClient(this.requestSpec, this.responseSpec);
        Assert.assertNotNull(clientId);

        Integer fixedDepositProductId = createFixedDepositProduct(VALID_FROM, VALID_TO);
        Assert.assertNotNull(fixedDepositProductId);

        Integer fixedDepositAccountId = applyForFixedDepositApplication(clientId.toString(), fixedDepositProductId.toString(), VALID_FROM,
                VALID_TO, SUBMITTED_ON_DATE, WHOLE_TERM, null);
        Assert.assertNotNull(fixedDepositAccountId);

        HashMap fixedDepositAccountStatusHashMap = FixedDepositAccountStatusChecker.getStatusOfFixedDepositAccount(this.requestSpec,
                this.responseSpec, fixedDepositAccountId.toString());
        FixedDepositAccountStatusChecker.verifyFixedDepositIsPending(fixedDepositAccountStatusHashMap);

        fixedDepositAccountStatusHashMap = this.fixedDepositAccountHelper.withdrawApplication(fixedDepositAccountId, WITHDRAWN_ON_DATE);
        FixedDepositAccountStatusChecker.verifyFixedDepositIsWithdrawn(fixedDepositAccountStatusHashMap);
        FixedDepositAccountStatusChecker.verifyFixedDepositAccountIsClosed(fixedDepositAccountStatusHashMap);
    }

    @Test
    public void testFixedDepositAccountIsDeleted() {
        this.fixedDepositProductHelper = new FixedDepositProductHelper(this.requestSpec, this.responseSpec);
        this.fixedDepositAccountHelper = new FixedDepositAccountHelper(this.requestSpec, this.responseSpec);

        DateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy");
        DateFormat monthDayFormat = new SimpleDateFormat("dd MMM");

        Calendar todaysDate = Calendar.getInstance();
        todaysDate.add(Calendar.MONTH, -3);
        final String VALID_FROM = dateFormat.format(todaysDate.getTime());
        todaysDate.add(Calendar.YEAR, 10);
        final String VALID_TO = dateFormat.format(todaysDate.getTime());

        todaysDate = Calendar.getInstance();
        todaysDate.add(Calendar.MONTH, -1);
        final String SUBMITTED_ON_DATE = dateFormat.format(todaysDate.getTime());
        final String MONTH_DAY = monthDayFormat.format(todaysDate.getTime());

        Integer clientId = ClientHelper.createClient(this.requestSpec, this.responseSpec);
        Assert.assertNotNull(clientId);

        Integer fixedDepositProductId = createFixedDepositProduct(VALID_FROM, VALID_TO);
        Assert.assertNotNull(fixedDepositProductId);

        Integer fixedDepositAccountId = applyForFixedDepositApplication(clientId.toString(), fixedDepositProductId.toString(), VALID_FROM,
                VALID_TO, SUBMITTED_ON_DATE, WHOLE_TERM, null);
        Assert.assertNotNull(fixedDepositAccountId);

        HashMap fixedDepositAccountStatusHashMap = FixedDepositAccountStatusChecker.getStatusOfFixedDepositAccount(this.requestSpec,
                this.responseSpec, fixedDepositAccountId.toString());
        FixedDepositAccountStatusChecker.verifyFixedDepositIsPending(fixedDepositAccountStatusHashMap);

        fixedDepositAccountId = (Integer) this.fixedDepositAccountHelper.deleteFixedDepositApplication(fixedDepositAccountId, "resourceId");
        Assert.assertNotNull(fixedDepositAccountId);
    }

    @Test
    public void testFixedDepositAccountWithLinkedAccount() {
        this.fixedDepositProductHelper = new FixedDepositProductHelper(this.requestSpec, this.responseSpec);
        this.accountHelper = new AccountHelper(this.requestSpec, this.responseSpec);
        this.savingsAccountHelper = new SavingsAccountHelper(this.requestSpec, this.responseSpec);
        this.fixedDepositAccountHelper = new FixedDepositAccountHelper(this.requestSpec, this.responseSpec);

        DateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy");
        DateFormat monthDayFormat = new SimpleDateFormat("dd MMM");

        Calendar todaysDate = Calendar.getInstance();
        todaysDate.add(Calendar.MONTH, -3);
        final String VALID_FROM = dateFormat.format(todaysDate.getTime());
        todaysDate.add(Calendar.YEAR, 10);
        final String VALID_TO = dateFormat.format(todaysDate.getTime());

        todaysDate = Calendar.getInstance();
        todaysDate.add(Calendar.MONTH, -1);
        final String SUBMITTED_ON_DATE = dateFormat.format(todaysDate.getTime());
        final String APPROVED_ON_DATE = dateFormat.format(todaysDate.getTime());
        final String ACTIVATION_DATE = dateFormat.format(todaysDate.getTime());
        final String MONTH_DAY = monthDayFormat.format(todaysDate.getTime());
        todaysDate.add(Calendar.MONTH, 1);
        final String CLOSED_ON_DATE = dateFormat.format(todaysDate.getTime());

        Integer clientId = ClientHelper.createClient(this.requestSpec, this.responseSpec);
        Assert.assertNotNull(clientId);
        Float balance = new Float(MINIMUM_OPENING_BALANCE) + new Float(FixedDepositAccountHelper.depositAmount);
        final Integer savingsId = createSavings(clientId, String.valueOf(balance));

        HashMap summary = savingsAccountHelper.getSavingsSummary(savingsId);
        assertEquals("Verifying opening Balance", balance, summary.get("accountBalance"));

        Integer fixedDepositProductId = createFixedDepositProduct(VALID_FROM, VALID_TO);
        Assert.assertNotNull(fixedDepositProductId);

        Integer fixedDepositAccountId = applyForFixedDepositApplication(clientId.toString(), fixedDepositProductId.toString(), VALID_FROM,
                VALID_TO, SUBMITTED_ON_DATE, WHOLE_TERM, savingsId.toString());
        Assert.assertNotNull(fixedDepositAccountId);

        HashMap fixedDepositAccountStatusHashMap = FixedDepositAccountStatusChecker.getStatusOfFixedDepositAccount(this.requestSpec,
                this.responseSpec, fixedDepositAccountId.toString());
        FixedDepositAccountStatusChecker.verifyFixedDepositIsPending(fixedDepositAccountStatusHashMap);

        fixedDepositAccountStatusHashMap = this.fixedDepositAccountHelper.approveFixedDeposit(fixedDepositAccountId, APPROVED_ON_DATE);
        FixedDepositAccountStatusChecker.verifyFixedDepositIsApproved(fixedDepositAccountStatusHashMap);

        fixedDepositAccountStatusHashMap = this.fixedDepositAccountHelper.activateFixedDeposit(fixedDepositAccountId, ACTIVATION_DATE);
        FixedDepositAccountStatusChecker.verifyFixedDepositIsActive(fixedDepositAccountStatusHashMap);
        summary = savingsAccountHelper.getSavingsSummary(savingsId);
        balance = new Float(MINIMUM_OPENING_BALANCE);
        assertEquals("Verifying opening Balance", balance, summary.get("accountBalance"));

    }

    private Integer createFixedDepositProduct(final String validFrom, final String validTo, Account... accounts) {
        System.out.println("------------------------------CREATING NEW FIXED DEPOSIT PRODUCT ---------------------------------------");
        FixedDepositProductHelper fixedDepositProductHelper = new FixedDepositProductHelper(this.requestSpec, this.responseSpec);
        final String fixedDepositProductJSON = fixedDepositProductHelper //
                // .withAccountingRuleAsCashBased(accounts)
                .build(validFrom, validTo);
        return FixedDepositProductHelper.createFixedDepositProduct(fixedDepositProductJSON, requestSpec, responseSpec);
    }

    private Integer applyForFixedDepositApplication(final String clientID, final String productID, final String validFrom,
            final String validTo, final String submittedOnDate, final String penalInterestType, String savingsId) {
        System.out.println("--------------------------------APPLYING FOR FIXED DEPOSIT ACCOUNT --------------------------------");
        final String fixedDepositApplicationJSON = new FixedDepositAccountHelper(this.requestSpec, this.responseSpec)
                //
                .withSubmittedOnDate(submittedOnDate).withSavings(savingsId)
                .build(clientID, productID, validFrom, validTo, penalInterestType);
        return this.fixedDepositAccountHelper
                .applyFixedDepositApplication(fixedDepositApplicationJSON, this.requestSpec, this.responseSpec);
    }

    private Integer createSavingsProduct(final RequestSpecification requestSpec, final ResponseSpecification responseSpec,
            final String minOpenningBalance) {
        System.out.println("------------------------------CREATING NEW SAVINGS PRODUCT ---------------------------------------");
        SavingsProductHelper savingsProductHelper = new SavingsProductHelper();
        final String savingsProductJSON = savingsProductHelper //
                .withInterestCompoundingPeriodTypeAsDaily() //
                .withInterestPostingPeriodTypeAsMonthly() //
                .withInterestCalculationPeriodTypeAsDailyBalance() //
                .withMinimumOpenningBalance(minOpenningBalance).build();
        return SavingsProductHelper.createSavingsProduct(savingsProductJSON, requestSpec, responseSpec);
    }

    private Integer createSavings(Integer clientID, String openningBalance) {
        final Integer savingsProductID = createSavingsProduct(this.requestSpec, this.responseSpec, openningBalance);
        Assert.assertNotNull(savingsProductID);

        SavingsAccountHelper savingsAccountHelper = new SavingsAccountHelper(this.requestSpec, this.responseSpec);

        final Integer savingsId = savingsAccountHelper.applyForSavingsApplication(clientID, savingsProductID, ACCOUNT_TYPE_INDIVIDUAL);
        Assert.assertNotNull(savingsProductID);

        HashMap savingsStatusHashMap = SavingsStatusChecker.getStatusOfSavings(this.requestSpec, this.responseSpec, savingsId);
        SavingsStatusChecker.verifySavingsIsPending(savingsStatusHashMap);

        savingsStatusHashMap = savingsAccountHelper.approveSavings(savingsId);
        SavingsStatusChecker.verifySavingsIsApproved(savingsStatusHashMap);

        savingsStatusHashMap = savingsAccountHelper.activateSavings(savingsId);
        SavingsStatusChecker.verifySavingsIsActive(savingsStatusHashMap);
        return savingsId;
    }
}