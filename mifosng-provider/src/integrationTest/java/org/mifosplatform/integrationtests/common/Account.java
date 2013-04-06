package org.mifosplatform.integrationtests.common;

public class Account {


    public enum AccountType {
        ASSET, INCOME, EXPENSE, LIABILITY,EQUITY;
    }

    private final AccountType accountType;
    private final Integer accountID;


    public Account(Integer accountID, AccountType accountType) {
        this.accountID = accountID;
        this.accountType = accountType;
    }

    public AccountType getAccountType(){
        return this.accountType;
    }

    public Integer getAccountID(){
        return this.accountID;
    }
}
