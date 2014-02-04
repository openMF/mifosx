/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.accountdetails.data;

import java.math.BigDecimal;

import org.mifosplatform.infrastructure.core.data.EnumOptionData;
import org.mifosplatform.organisation.monetary.data.CurrencyData;
import org.mifosplatform.portfolio.savings.data.SavingsAccountStatusEnumData;

/**
 * Immutable data object for savings accounts.
 */
@SuppressWarnings("unused")
public class SavingsAccountSummaryData {

    private final Long id;
    private final String accountNo;
    private final String externalId;
    private final Long productId;
    private final String productName;
    private final SavingsAccountStatusEnumData status;
    private final CurrencyData currency;
    private final BigDecimal accountBalance;
    private final EnumOptionData accountType;

    public SavingsAccountSummaryData(final Long id, final String accountNo, final String externalId, final Long productId,
            final String productName, final SavingsAccountStatusEnumData status, final CurrencyData currency,
            final BigDecimal accountBalance, final EnumOptionData accountType) {
        this.id = id;
        this.accountNo = accountNo;
        this.externalId = externalId;
        this.productId = productId;
        this.productName = productName;
        this.status = status;
        this.currency = currency;
        this.accountBalance = accountBalance;
        this.accountType = accountType;
    }
}