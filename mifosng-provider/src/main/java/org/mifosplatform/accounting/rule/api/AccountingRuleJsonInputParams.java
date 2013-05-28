/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.accounting.rule.api;

import java.util.HashSet;
import java.util.Set;

/***
 * Enum of all parameters passed in while creating/updating a loan product
 ***/
public enum AccountingRuleJsonInputParams {
    ID("id"), OFFICE_ID("officeId"), ACCOUNT_TO_DEBIT("debitAccountHead"), ACCOUNT_TO_CREDIT("creditAccountHead"), NAME("name"), DESCRIPTION(
            "description"), SYSTEM_DEFINED("systemDefined"), DEBIT_ACCOUNT_TAGS("debitTags"), CREDIT_ACCOUNT_TAGS("creditTags");

    private final String value;

    private AccountingRuleJsonInputParams(final String value) {
        this.value = value;
    }

    private static final Set<String> values = new HashSet<String>();
    static {
        for (final AccountingRuleJsonInputParams type : AccountingRuleJsonInputParams.values()) {
            values.add(type.value);
        }
    }

    public static Set<String> getAllValues() {
        return values;
    }

    @Override
    public String toString() {
        return name().toString().replaceAll("_", " ");
    }

    public String getValue() {
        return this.value;
    }
}