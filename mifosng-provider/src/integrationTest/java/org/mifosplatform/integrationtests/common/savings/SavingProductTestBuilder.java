package org.mifosplatform.integrationtests.common.savings;

import java.util.HashMap;
import java.util.Map;

import org.mifosplatform.integrationtests.common.ClientHelper;
import org.mifosplatform.integrationtests.common.accounting.Account;
import org.mifosplatform.integrationtests.common.loans.LoanProductTestBuilder;

import com.google.gson.Gson;

public class SavingProductTestBuilder {

    private static final String LOCALE = "en_GB";
    private static final String DIGITS_AFTER_DECIMAL = "2";
    private static final String IN_MULTIPLES_OF = "0";
    private static final String INR = "INR";
    private static final String DAYS = "0";
    private static final String WEEKS = "1";
    private static final String MONTHS = "2";
    private static final String YEARS = "3";
    private static final String DAILY = "1";
    private static final String MONTHLY = "4";
    private static final String QUARTERLY = "5";
    private static final String ANNUALLY = "7";
    private static final String INTEREST_CALCULATION_USING_DAILY_BALANCE = "1";
    private static final String INTEREST_CALCULATION_USING_AVERAGE_DAILY_BALANCE = "2";
    private static final String NONE = "1";
    private static final String CASH_BASED = "2";
    private static final String ACCURAL_BASED = "3";

    private String nameOfSavingProduct = ClientHelper.randomNameGenerator("SAVING_PRODUCT_", 6);
    private String shortName = ClientHelper.randomNameGenerator("", 4);
    private String description = ClientHelper.randomNameGenerator("", 20);
    private String interestCompoundingPeriodType = "1";
    private String interestPostingPeriodType = "4";
    private String interestCalculationType = INTEREST_CALCULATION_USING_DAILY_BALANCE;
    private String nominalAnnualInterestRate = "5.0";
    private String accountingRule = NONE;
    private final String currencyCode = INR;
    private final String interestCalculationDaysInYearType = "365";
    private Account[] accountList = null;

    public String build() {
        final HashMap<String, String> map = new HashMap<String, String>();

        map.put("name", this.nameOfSavingProduct);
        map.put("shortName", this.shortName);
        map.put("description", this.description);
        map.put("currencyCode", this.currencyCode);
        map.put("interestCalculationDaysInYearType", this.interestCalculationDaysInYearType);
        map.put("locale", LOCALE);
        map.put("digitsAfterDecimal", DIGITS_AFTER_DECIMAL);
        map.put("inMultiplesOf", IN_MULTIPLES_OF);
        map.put("interestCalculationType", INTEREST_CALCULATION_USING_DAILY_BALANCE);
        map.put("nominalAnnualInterestRate", this.nominalAnnualInterestRate);
        map.put("interestCompoundingPeriodType", this.interestCompoundingPeriodType);
        map.put("interestPostingPeriodType", this.interestPostingPeriodType);
        //map.put("transactionProcessingStrategyId", this.transactionProcessingStrategy);
        map.put("accountingRule", this.accountingRule);

        if (this.accountingRule.equals(ACCURAL_BASED)) {
            map.putAll(getAccountMappingForAccrualBased());
        } else if (this.accountingRule.equals(CASH_BASED)) {
            map.putAll(getAccountMappingForCashBased());
        }
        return new Gson().toJson(map);
    }

    public SavingProductTestBuilder withSavingName(final String savingName) {
        this.nameOfSavingProduct = savingName;
        return this;
    }
 
    public SavingProductTestBuilder withInterestCompoundingPeriodTypeAsDaily() {
        this.interestCompoundingPeriodType = DAILY;
        return this;
    }

    public SavingProductTestBuilder withInterestCompoundingPeriodTypeAsMonthly() {
        this.interestCompoundingPeriodType = MONTHLY;
        return this;
    }
    
    public SavingProductTestBuilder withInterestPostingPeriodTypeAsMonthly() {
        this.interestPostingPeriodType = MONTHLY;
        return this;
    }

    public SavingProductTestBuilder withInterestPostingPeriodTypeAsQuarterly() {
        this.interestPostingPeriodType = QUARTERLY;
        return this;
    }
    
    public SavingProductTestBuilder withInterestPostingPeriodTypeAsAnnually() {
        this.interestPostingPeriodType = ANNUALLY;
        return this;
    }
    
    public SavingProductTestBuilder withInterestCalculationPeriodTypeAsDailyBalance() {
        this.interestCalculationType = INTEREST_CALCULATION_USING_DAILY_BALANCE;
        return this;
    }
    
    public SavingProductTestBuilder withInterestCalculationPeriodTypeAsAverageDailyBalance() {
        this.interestCalculationType = INTEREST_CALCULATION_USING_AVERAGE_DAILY_BALANCE;
        return this;
    }

    public SavingProductTestBuilder withAccountingRuleAsNone() {
        this.accountingRule = NONE;
        return this;
    }

    public SavingProductTestBuilder withAccountingRuleAsCashBased(final Account[] account_list) {
        this.accountingRule = CASH_BASED;
        this.accountList = account_list;
        return this;
    }
    
    public SavingProductTestBuilder withAccountingRuleAsAccrualBased(final Account[] account_list) {
        this.accountingRule = ACCURAL_BASED;
        this.accountList = account_list;
        return this;
    }

    private Map<String, String> getAccountMappingForCashBased() {
        final Map<String, String> map = new HashMap<String, String>();
        for (int i = 0; i < this.accountList.length; i++) {
            if (this.accountList[i].getAccountType().equals(Account.AccountType.ASSET)) {
                final String ID = this.accountList[i].getAccountID().toString();
                map.put("savingReferenceAccountId", ID);
            }
            if (this.accountList[i].getAccountType().equals(Account.AccountType.LIABILITY)) {
                final String ID = this.accountList[i].getAccountID().toString();
                map.put("savingControlAccountId", ID);
                map.put("savingTransfersInSuspenseAccountId", ID);
            }
            if (this.accountList[i].getAccountType().equals(Account.AccountType.EXPENSE)) {
                final String ID = this.accountList[i].getAccountID().toString();
                map.put("interestOnSavingsAccountId", ID);
            }
            if (this.accountList[i].getAccountType().equals(Account.AccountType.INCOME)) {
                final String ID = this.accountList[i].getAccountID().toString();
                map.put("incomeFromFeeAccountId", ID);
                map.put("incomeFromPenaltyAccountId", ID);
            }
        }
        return map;
    }

    private Map<String, String> getAccountMappingForAccrualBased() {
        final Map<String, String> map = new HashMap<String, String>();
        for (int i = 0; i < this.accountList.length; i++) {
            if (this.accountList[i].getAccountType().equals(Account.AccountType.ASSET)) {
                final String ID = this.accountList[i].getAccountID().toString();
                map.put("savingReferenceAccountId", ID);

            }
            if (this.accountList[i].getAccountType().equals(Account.AccountType.INCOME)) {
                final String ID = this.accountList[i].getAccountID().toString();
                map.put("incomeFromFeeAccountId", ID);
                map.put("incomeFromPenaltyAccountId", ID);
            }
            if (this.accountList[i].getAccountType().equals(Account.AccountType.EXPENSE)) {
                final String ID = this.accountList[i].getAccountID().toString();
                map.put("interestOnSavingsAccountId", ID);
            }
            if (this.accountList[i].getAccountType().equals(Account.AccountType.LIABILITY)) {
                final String ID = this.accountList[i].getAccountID().toString();
                map.put("savingControlAccountId", ID);
                map.put("savingTransfersInSuspenseAccountId", ID);
            }
        }

        return map;
    }

}