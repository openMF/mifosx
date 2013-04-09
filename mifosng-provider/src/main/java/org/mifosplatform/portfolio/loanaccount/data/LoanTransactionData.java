/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.loanaccount.data;

import java.math.BigDecimal;

import org.joda.time.LocalDate;
import org.mifosplatform.organisation.monetary.data.CurrencyData;

/**
 * Immutable data object representing a loan transaction.
 */
public class LoanTransactionData {

    @SuppressWarnings("unused")
    private final Long id;

    private final LoanTransactionEnumData type;

    private final LocalDate date;

    @SuppressWarnings("unused")
    private final CurrencyData currency;
    @SuppressWarnings("unused")
    private final PaymentDetailData paymentDetailData;

    @SuppressWarnings("unused")
    private final BigDecimal amount;
    @SuppressWarnings("unused")
    private final BigDecimal principalPortion;
    @SuppressWarnings("unused")
    private final BigDecimal interestPortion;
    @SuppressWarnings("unused")
    private final BigDecimal feeChargesPortion;
    @SuppressWarnings("unused")
    private final BigDecimal penaltyChargesPortion;

    public LoanTransactionData(final Long id, final LoanTransactionEnumData transactionType, final PaymentDetailData paymentDetailData,
            final CurrencyData currency, final LocalDate date, final BigDecimal amount, final BigDecimal principalPortion,
            final BigDecimal interestPortion, final BigDecimal feeChargesPortion, final BigDecimal penaltyChargesPortion) {
        this.id = id;
        this.type = transactionType;
        this.paymentDetailData = paymentDetailData;
        this.currency = currency;
        this.date = date;
        this.amount = amount;
        this.principalPortion = principalPortion;
        this.interestPortion = interestPortion;
        this.feeChargesPortion = feeChargesPortion;
        this.penaltyChargesPortion = penaltyChargesPortion;
    }

    public LocalDate dateOf() {
        return this.date;
    }

    public boolean isNotDisbursement() {
        return Integer.valueOf(1).equals(type.id());
    }
}