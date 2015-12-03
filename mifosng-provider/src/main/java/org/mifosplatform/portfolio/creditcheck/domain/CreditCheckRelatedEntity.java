/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.creditcheck.domain;

public enum CreditCheckRelatedEntity {

    INVALID(0, "creditCheckRelatedEntity.invalid"),
    LOAN(1, "creditCheckRelatedEntity.loan"),
    SAVINGS(2, "creditCheckRelatedEntity.savings");

    private final Integer value;
    private final String code;

    private CreditCheckRelatedEntity(final Integer value, final String code) {
        this.value = value;
        this.code = code;
    }

    public Integer getValue() {
        return this.value;
    }

    public String getCode() {
        return this.code;
    }

    public static CreditCheckRelatedEntity fromInt(final Integer creditCheckRelatedEntity) {
        CreditCheckRelatedEntity appliesToType = INVALID;

        if (creditCheckRelatedEntity != null) {
            switch (creditCheckRelatedEntity) {
                case 1:
                    appliesToType = LOAN;
                break;
                
                case 2:
                    appliesToType = SAVINGS;
                break;
                
                default:
                    appliesToType = INVALID;
                break;
            }
        }

        return appliesToType;
    }

    public boolean isLoanCreditCheck() {
        return this.value.equals(CreditCheckRelatedEntity.LOAN.getValue());
    }

    public boolean isSavingsCreditCheck() {
        return this.value.equals(CreditCheckRelatedEntity.SAVINGS.getValue());
    }

    public static Object[] validValues() {
        return new Object[] { CreditCheckRelatedEntity.LOAN.getValue(), CreditCheckRelatedEntity.SAVINGS.getValue() };
    }
}