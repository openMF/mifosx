/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.client.data;

import org.mifosplatform.portfolio.loanaccount.data.LoanStatusEnumData;
import org.mifosplatform.portfolio.savings.data.SavingsAccountStatusEnumData;

/**
 * Immutable data object for client loan accounts.
 */
public class ClientAccountSummaryData {

    @SuppressWarnings("unused")
    private final Long id;
    @SuppressWarnings("unused")
    private final String accountNo;
    @SuppressWarnings("unused")
    private final String externalId;
    @SuppressWarnings("unused")
    private final Long productId;
    @SuppressWarnings("unused")
    private final String productName;

    private final Integer accountStatusId;
    private final LoanStatusEnumData status;
    private final SavingsAccountStatusEnumData savingAccountStatus;

    public ClientAccountSummaryData(final Long id, final String externalId, final Long productId, final String loanProductName,
            final Integer loanStatusId) {
        this.id = id;
        this.accountNo = null;
        this.externalId = externalId;
        this.productId = productId;
        this.productName = loanProductName;
        this.accountStatusId = loanStatusId;
        this.status = null;
        this.savingAccountStatus = null;
    }

    public ClientAccountSummaryData(final Long id, final String accountNo, final String externalId, final Long productId,
            final String loanProductName, final LoanStatusEnumData loanStatus) {
        this.id = id;
        this.accountNo = accountNo;
        this.externalId = externalId;
        this.productId = productId;
        this.productName = loanProductName;
        this.accountStatusId = null;
        this.status = loanStatus;
        this.savingAccountStatus = null;
    }
    
    public ClientAccountSummaryData(final Long id, final String accountNo, final String externalId, final Long productId,
            final String loanProductName, final SavingsAccountStatusEnumData savingAccountStatus) {
        this.id = id;
        this.accountNo = accountNo;
        this.externalId = externalId;
        this.productId = productId;
        this.productName = loanProductName;
        this.accountStatusId = null;
        this.status = null;
        this.savingAccountStatus = savingAccountStatus;
    }

    public Integer accountStatusId() {
        Integer accountStatus = this.accountStatusId;
        if (accountStatus == null && this.status != null) {
            accountStatus = this.status.id().intValue();
        }else  if (accountStatus == null && this.savingAccountStatus != null) {
            accountStatus = this.savingAccountStatus.id().intValue();
        }
        return accountStatus;
    }
}