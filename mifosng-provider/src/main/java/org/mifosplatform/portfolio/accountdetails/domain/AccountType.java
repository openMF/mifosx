/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.accountdetails.domain;

/**
 * Enum representation of account types .
 */
public enum AccountType {

    INVALID(0, "accountType.invalid"), //
    INDIVIDUAL(1, "accountType.individual"), //
    GROUP(2, "accountType.group"), //
    JLG(3, "accountType.jlg");// JLG account given in group context

    private final Integer value;
    private final String code;

    private AccountType(final Integer value, final String code) {
        this.value = value;
        this.code = code;
    }

    public static AccountType fromInt(final Integer accountTypeValue) {

        AccountType enumeration = AccountType.INVALID;
        switch (accountTypeValue) {
            case 1:
                enumeration = AccountType.INDIVIDUAL;
            break;
            case 2:
                enumeration = AccountType.GROUP;
            break;
            case 3:
                enumeration = AccountType.JLG;
            break;
        }
        return enumeration;
    }

    public static AccountType fromName(final String name) {
        AccountType accountType = AccountType.INVALID;
        for (final AccountType type : AccountType.values()) {
            if (type.getName().equals(name)) {
                accountType = type;
                break;
            }
        }
        return accountType;
    }

    public Integer getValue() {
        return this.value;
    }

    public String getCode() {
        return this.code;
    }

    public String getName() {
        return name().toLowerCase();
    }

    public boolean isInvalid() {
        return this.value.equals(AccountType.INVALID.getValue());
    }

    public boolean isIndividualAccount() {
        return this.value.equals(AccountType.INDIVIDUAL.getValue());
    }

    public boolean isGroupAccount() {
        return this.value.equals(AccountType.GROUP.getValue());
    }

    public boolean isJLGAccount() {
        return this.value.equals(AccountType.JLG.getValue());
    }
}