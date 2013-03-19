/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.collectionsheet.data;

import java.util.Collection;

/**
 * Immutable data object for clients with loans due for disbursement or collection.
 */
public class ClientLoansData {

    private final Long clientId;
    private final String clientName;
    private Collection<LoanDueData> loans;

    public ClientLoansData(final Long clientId, final String clientName, final Collection<LoanDueData> loans) {
        this.clientId = clientId;
        this.clientName = clientName;
        this.loans = loans;
    }

    public ClientLoansData(final Long clientId, final String clientName) {
        this.clientId = clientId;
        this.clientName = clientName;
        this.loans = null;
    }

    public Long getClientId() {
        return this.clientId;
    }

    public String getClientName() {
        return this.clientName;
    }

    public Collection<LoanDueData> getLoans() {
        return this.loans;
    }

    public void setLoans(final Collection<LoanDueData> loans) {
        this.loans = loans;
    }

}