/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.collectionsheet.data;

import java.math.BigDecimal;

/**
 * Immutable data object for extracting flat data for joint liability group's collection sheet.
 */
public class JLGCollectionSheetFlatData {

    private final String groupName;
    private final Long groupId;
    private final Long staffId;
    private final String staffName;
    private final Long levelId;
    private final String levelName;
    private final String clientName;
    private final Long clientId;
    private final Long loanId;
    private final String accountId;
    private final Integer accountStatusId;
    private final String productShortName;
    private final Long productId;
    private final String currencyCode;
    private final Integer currencyDigits;
    private BigDecimal disbursementAmount = BigDecimal.ZERO;
    private BigDecimal principalDue = BigDecimal.ZERO;
    private BigDecimal principalPaid = BigDecimal.ZERO;
    private BigDecimal interestDue = BigDecimal.ZERO;
    private BigDecimal interestPaid = BigDecimal.ZERO;
    private BigDecimal chargesDue = BigDecimal.ZERO;

    public JLGCollectionSheetFlatData(final String groupName, final Long groupId, final Long staffId, final String staffName,
            final Long levelId, final String levelName, final String clientName, final Long clientId, final Long loanId,
            final String accountId, final Integer accountStatusId, final String productShortName, final Long productId,
            final String currencyCode, final Integer currencyDigits, final BigDecimal disbursementAmount, final BigDecimal principalDue,
            final BigDecimal principalPaid, final BigDecimal interestDue, final BigDecimal interestPaid, final BigDecimal chargesDue) {
        this.groupName = groupName;
        this.groupId = groupId;
        this.staffId = staffId;
        this.staffName = staffName;
        this.levelId = levelId;
        this.levelName = levelName;
        this.clientName = clientName;
        this.clientId = clientId;
        this.loanId = loanId;
        this.accountId = accountId;
        this.accountStatusId = accountStatusId;
        this.productShortName = productShortName;
        this.productId = productId;
        this.currencyCode = currencyCode;
        this.currencyDigits = currencyDigits;
        this.disbursementAmount = disbursementAmount;
        this.principalDue = principalDue;
        this.principalPaid = principalPaid;
        this.interestDue = interestDue;
        this.interestPaid = interestPaid;
        this.chargesDue = chargesDue;
    }

    public String getGroupName() {
        return this.groupName;
    }

    public Long getGroupId() {
        return this.groupId;
    }

    public Long getStaffId() {
        return this.staffId;
    }

    public String getStaffName() {
        return this.staffName;
    }

    public Long getLevelId() {
        return this.levelId;
    }

    public String getLevelName() {
        return this.levelName;
    }

    public String getClientName() {
        return this.clientName;
    }

    public Long getClientId() {
        return this.clientId;
    }

    public Long getLoanId() {
        return this.loanId;
    }

    public String getAccountId() {
        return this.accountId;
    }

    public Integer getAccountStatusId() {
        return this.accountStatusId;
    }

    public String getProductShortName() {
        return this.productShortName;
    }

    public Long getProductId() {
        return this.productId;
    }

    public String getCurrencyCode() {
        return this.currencyCode;
    }

    public Integer getCurrencyDigits() {
        return this.currencyDigits;
    }

    public BigDecimal getDisbursementAmount() {
        return this.disbursementAmount;
    }

    public BigDecimal getPrincipalDue() {
        return this.principalDue;
    }

    public BigDecimal getPrincipalPaid() {
        return this.principalPaid;
    }

    public BigDecimal getInterestDue() {
        return this.interestDue;
    }

    public BigDecimal getInterestPaid() {
        return this.interestPaid;
    }

    public BigDecimal getChargesDue() {
        return this.chargesDue;
    }

    public LoanDueData getLoanDueData() {
        return new LoanDueData(this.loanId, this.accountId, this.accountStatusId, this.productShortName, this.productId, this.currencyCode,
                this.currencyDigits, this.disbursementAmount, this.principalDue, this.principalPaid, this.interestDue, this.interestPaid,
                this.chargesDue);
    }

    public ClientLoansData getClientLoansData() {
        return new ClientLoansData(this.clientId, this.clientName);
    }

    public JLGClientsData getJLGClientsData() {

        return new JLGClientsData(this.groupId, this.groupName, this.staffId, this.staffName, this.levelId, this.levelName);
    }

}