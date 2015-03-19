/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.savings.data;

import java.math.BigDecimal;

import org.joda.time.LocalDate;
import org.mifosplatform.infrastructure.core.data.EnumOptionData;

public class DepositAccountOnHoldTransactionData {

	@SuppressWarnings("unused")
	private final Long id;
	@SuppressWarnings("unused")
	private final BigDecimal amount;
	@SuppressWarnings("unused")
	private EnumOptionData transactionType;
	@SuppressWarnings("unused")
	private final LocalDate transactionDate;
	@SuppressWarnings("unused")
	private final boolean reversed;
	@SuppressWarnings("unused")
	private final String savingsAccNo;
	@SuppressWarnings("unused")
	private final String savingsClientName;
	@SuppressWarnings("unused")
	private final String loanAccNo;
	@SuppressWarnings("unused")
	private final String loanClientName;

	public DepositAccountOnHoldTransactionData(Long id, BigDecimal amount,
			EnumOptionData transactionType, LocalDate transactionDate,
			boolean reversed, String savingsAccNo, String savingsClientName,
			String loanAccNo, String loanClientName) {
		this.id = id;
		this.amount = amount;
		this.transactionType = transactionType;
		this.transactionDate = transactionDate;
		this.reversed = reversed;
		this.savingsAccNo = savingsAccNo;
		this.savingsClientName = savingsClientName;
		this.loanAccNo = loanAccNo;
		this.loanClientName = loanClientName;
	}

	public static DepositAccountOnHoldTransactionData instance(final Long id,
			final BigDecimal amount, final EnumOptionData transactionType,
			final LocalDate transactionDate, final boolean reversed,
			final String savingsAccNo, final String savingsClientName,
			final String loanAccNo, final String loanClientName) {
		return new DepositAccountOnHoldTransactionData(id, amount,
				transactionType, transactionDate, reversed, savingsAccNo,
				savingsClientName, loanAccNo, loanClientName);
	}
}
