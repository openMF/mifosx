package org.mifosplatform.integrationtests.common.savings;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.mifosplatform.integrationtests.common.CommonConstants;
import org.mifosplatform.integrationtests.common.Utils;
import org.mifosplatform.integrationtests.common.accounting.Account;
import org.mifosplatform.integrationtests.common.accounting.Account.AccountType;

import com.google.gson.Gson;
import com.jayway.restassured.specification.RequestSpecification;
import com.jayway.restassured.specification.ResponseSpecification;

@SuppressWarnings("unused")
public class SavingsProductHelper {

    private static final String SAVINGS_PRODUCT_URL = "/mifosng-provider/api/v1/savingsproducts";
    private static final String CREATE_SAVINGS_PRODUCT_URL = SAVINGS_PRODUCT_URL + "?" + Utils.TENANT_IDENTIFIER;

    private static final String LOCALE = "en_GB";
    private static final String DIGITS_AFTER_DECIMAL = "4";
    private static final String IN_MULTIPLES_OF = "0";
    private static final String USD = "USD";
    private static final String DAYS = "0";
    private static final String WEEKS = "1";
    private static final String MONTHS = "2";
    private static final String YEARS = "3";
    private static final String DAILY = "1";
    private static final String MONTHLY = "4";
    private static final String QUARTERLY = "5";
    private static final String ANNUAL = "7";
    private static final String INTEREST_CALCULATION_USING_DAILY_BALANCE = "1";
    private static final String INTEREST_CALCULATION_USING_AVERAGE_DAILY_BALANCE = "2";
    private static final String DAYS_360 = "360";
    private static final String DAYS_365 = "365";
    private static final String NONE = "1";
    private static final String CASH_BASED = "2";

    private String nameOfSavingsProduct = Utils.randomNameGenerator("SAVINGS_PRODUCT_", 6);
    private String shortName = Utils.randomNameGenerator("", 4);
    private String description = Utils.randomNameGenerator("", 20);
    private String interestCompoundingPeriodType = "2";
    private String interestPostingPeriodType = "4";
    private String interestCalculationType = INTEREST_CALCULATION_USING_DAILY_BALANCE;
    private String nominalAnnualInterestRate = "10.0";
    private String accountingRule = NONE;
    private String savingsReferenceAccountId = null;
    private String transfersInSuspenseAccountId = null;
    private String savingsControlAccountId = null;
    private String interestOnSavingsAccountId = null;
    private String incomeFromFeeAccountId = null;
    private String incomeFromPenaltyAccountId = null;
    private String overdraftPortfolioControlId = null;
    private String incomeFromInterestId = null;
    private String writeOffAccountId = null;
    private String minRequiredOpeningBalance = null;
    private String lockinPeriodFrequency = "0";
    private String withdrawalFeeForTransfers = "true";
    private String lockingPeriodFrequencyType = DAYS;
    private final String currencyCode = USD;
    private final String interestCalculationDaysInYearType = DAYS_365;
    private Account[] accountList = null;

    public String build() {
        final HashMap<String, String> map = new HashMap<String, String>();

        map.put("name", this.nameOfSavingsProduct);
        map.put("shortName", this.shortName);
        map.put("description", this.description);
        map.put("currencyCode", this.currencyCode);
        map.put("interestCalculationDaysInYearType", this.interestCalculationDaysInYearType);
        map.put("locale", LOCALE);
        map.put("digitsAfterDecimal", DIGITS_AFTER_DECIMAL);
        map.put("inMultiplesOf", IN_MULTIPLES_OF);
        map.put("interestCalculationType", this.interestCalculationType);
        map.put("nominalAnnualInterestRate", this.nominalAnnualInterestRate);
        map.put("interestCompoundingPeriodType", this.interestCompoundingPeriodType);
        map.put("interestPostingPeriodType", this.interestPostingPeriodType);
        map.put("accountingRule", this.accountingRule);
        map.put("savingsReferenceAccountId", this.savingsReferenceAccountId);
        map.put("transfersInSuspenseAccountId", this.transfersInSuspenseAccountId);
        map.put("savingsControlAccountId", this.savingsControlAccountId);
        map.put("interestOnSavingsAccountId", this.interestOnSavingsAccountId);
        map.put("incomeFromFeeAccountId", this.incomeFromFeeAccountId);
        map.put("incomeFromPenaltyAccountId", this.incomeFromPenaltyAccountId);
        map.put("overdraftPortfolioControlId", this.overdraftPortfolioControlId);
        map.put("incomeFromInterestId", this.incomeFromInterestId);
        map.put("writeOffAccountId", this.writeOffAccountId);
        map.put("minRequiredOpeningBalance", this.minRequiredOpeningBalance);
        map.put("lockinPeriodFrequency", this.lockinPeriodFrequency);
        map.put("lockinPeriodFrequencyType", this.lockingPeriodFrequencyType);
        map.put("withdrawalFeeForTransfers", this.withdrawalFeeForTransfers);

        if (this.accountingRule.equals(CASH_BASED)) {
            map.putAll(getAccountMappingForCashBased());
        }
        String savingsProductCreateJson = new Gson().toJson(map);
        System.out.println(savingsProductCreateJson);
        return savingsProductCreateJson;
    }

    public SavingsProductHelper withSavingsName(final String savingsName) {
        this.nameOfSavingsProduct = savingsName;
        return this;
    }

    public SavingsProductHelper withInterestCompoundingPeriodTypeAsDaily() {
        this.interestCompoundingPeriodType = DAILY;
        return this;
    }

    public SavingsProductHelper withMinimumOpenningBalance(String minBalance) {
        this.minRequiredOpeningBalance = minBalance;
        return this;
    }
    
    public SavingsProductHelper withInterestCompoundingPeriodTypeAsMonthly() {
        this.interestCompoundingPeriodType = MONTHLY;
        return this;
    }

    public SavingsProductHelper withInterestPostingPeriodTypeAsMonthly() {
        this.interestPostingPeriodType = MONTHLY;
        return this;
    }

    public SavingsProductHelper withInterestPostingPeriodTypeAsQuarterly() {
        this.interestPostingPeriodType = QUARTERLY;
        return this;
    }

    public SavingsProductHelper withInterestPostingPeriodTypeAsAnnual() {
        this.interestPostingPeriodType = ANNUAL;
        return this;
    }

    public SavingsProductHelper withInterestCalculationPeriodTypeAsDailyBalance() {
        this.interestCalculationType = INTEREST_CALCULATION_USING_DAILY_BALANCE;
        return this;
    }

    public SavingsProductHelper withInterestCalculationPeriodTypeAsAverageDailyBalance() {
        this.interestCalculationType = INTEREST_CALCULATION_USING_AVERAGE_DAILY_BALANCE;
        return this;
    }

    public SavingsProductHelper withAccountingRuleAsNone() {
        this.accountingRule = NONE;
        return this;
    }

    public SavingsProductHelper withAccountingRuleAsCashBased(final Account[] account_list) {
        this.accountingRule = CASH_BASED;
        this.accountList = account_list;
        return this;
    }

    private Map<String, String> getAccountMappingForCashBased() {
        final Map<String, String> map = new HashMap<String, String>();
        if (accountList != null) {
            for (int i = 0; i < this.accountList.length; i++) {
                if (this.accountList[i].getAccountType().equals(Account.AccountType.ASSET)) {
                    final String ID = this.accountList[i].getAccountID().toString();
                    map.put("savingsReferenceAccountId", ID);
                    map.put("overdraftPortfolioControlId", ID);
                }
                if (this.accountList[i].getAccountType().equals(Account.AccountType.LIABILITY)) {
                    final String ID = this.accountList[i].getAccountID().toString();
                    map.put("savingsControlAccountId", ID);
                    map.put("transfersInSuspenseAccountId", ID);
                }
                if (this.accountList[i].getAccountType().equals(Account.AccountType.EXPENSE)) {
                    final String ID = this.accountList[i].getAccountID().toString();
                    map.put("interestOnSavingsAccountId", ID);
                    map.put("writeOffAccountId", ID);
                }
                if (this.accountList[i].getAccountType().equals(Account.AccountType.INCOME)) {
                    final String ID = this.accountList[i].getAccountID().toString();
                    map.put("incomeFromFeeAccountId", ID);
                    map.put("incomeFromPenaltyAccountId", ID);
                    map.put("incomeFromInterestId", ID);
                }
            }
        }
        return map;
    }

    public static Integer createSavingsProduct(final String savingsProductJSON, final RequestSpecification requestSpec,
            final ResponseSpecification responseSpec) {
        return Utils.performServerPost(requestSpec, responseSpec, CREATE_SAVINGS_PRODUCT_URL, savingsProductJSON, "resourceId");
    }

    public static void verifySavingsProductCreatedOnServer(final RequestSpecification requestSpec,
            final ResponseSpecification responseSpec, final Integer generatedProductID) {
        System.out.println("------------------------------CHECK CLIENT DETAILS------------------------------------\n");
        final String GET_SAVINGS_PRODUCT_URL = SAVINGS_PRODUCT_URL + "/" + generatedProductID + "?" + Utils.TENANT_IDENTIFIER;
        final Integer responseSavingsProductID = Utils.performServerGet(requestSpec, responseSpec, GET_SAVINGS_PRODUCT_URL, "id");
        assertEquals("ERROR IN CREATING THE Savings Product", generatedProductID, responseSavingsProductID);
    }

}