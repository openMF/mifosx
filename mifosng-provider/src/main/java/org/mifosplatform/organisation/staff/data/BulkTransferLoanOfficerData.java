/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.organisation.staff.data;

import java.util.Collection;

import org.joda.time.LocalDate;
import org.mifosplatform.organisation.office.data.OfficeData;

/**
 * Immutable data object returned for loan-officer bulk transfer screens.
 */
public class BulkTransferLoanOfficerData {

    @SuppressWarnings("unused")
    private final Long officeId;
    @SuppressWarnings("unused")
    private final Long fromLoanOfficerId;
    @SuppressWarnings("unused")
    private final Long fromDsaOfficerId;
    @SuppressWarnings("unused")
    private final LocalDate assignmentDate;

    // template
    @SuppressWarnings("unused")
    private final Collection<OfficeData> officeOptions;
    @SuppressWarnings("unused")
    private final Collection<StaffData> loanOfficerOptions;
    @SuppressWarnings("unused")
    private final Collection<StaffData> dSaOptions;
    @SuppressWarnings("unused")
    private final StaffAccountSummaryCollectionData accountSummaryCollection;

    public static BulkTransferLoanOfficerData templateForBulk(final Long officeId, final Long fromLoanOfficerId,final Long fromDsaOfficerId,final LocalDate assignmentDate, final Collection<OfficeData> officeOptions, final Collection<StaffData> loanOfficerOptions,final Collection<StaffData> dSaOptions,final StaffAccountSummaryCollectionData accountSummaryCollection) {
        return new BulkTransferLoanOfficerData(officeId, fromLoanOfficerId,fromDsaOfficerId, assignmentDate, officeOptions, loanOfficerOptions,dSaOptions,
                accountSummaryCollection);
    }

    public static BulkTransferLoanOfficerData template(final Long fromLoanOfficerId,final Long fromDsaOfficerId, final Collection<StaffData> loanOfficerOptions,final Collection<StaffData> dSaOptions,
            final LocalDate assignmentDate) {
        return new BulkTransferLoanOfficerData(null, fromLoanOfficerId,fromDsaOfficerId, assignmentDate, null, loanOfficerOptions,dSaOptions, null);
    }

    private BulkTransferLoanOfficerData(final Long officeId, final Long fromLoanOfficerId,final Long fromDsaOfficerId, final LocalDate assignmentDate,
            final Collection<OfficeData> officeOptions, final Collection<StaffData> loanOfficerOptions,final Collection<StaffData> dSaOptions,
            final StaffAccountSummaryCollectionData accountSummaryCollection) {
        this.officeId = officeId;
        this.fromLoanOfficerId = fromLoanOfficerId;
        this.fromDsaOfficerId = fromDsaOfficerId;
        this.assignmentDate = assignmentDate;
        this.officeOptions = officeOptions;
        this.loanOfficerOptions = loanOfficerOptions;
        this.dSaOptions = dSaOptions;
        this.accountSummaryCollection = accountSummaryCollection;
    }
}