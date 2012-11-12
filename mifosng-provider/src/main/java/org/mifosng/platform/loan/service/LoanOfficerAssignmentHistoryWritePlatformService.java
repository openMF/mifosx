package org.mifosng.platform.loan.service;


import org.joda.time.LocalDate;
import org.mifosng.platform.loan.domain.Loan;
import org.mifosng.platform.staff.domain.Staff;
import org.springframework.security.access.prepost.PreAuthorize;

public interface LoanOfficerAssignmentHistoryWritePlatformService {

    @PreAuthorize(value = "hasRole('ORGANISATION_ADMINISTRATION_SUPER_USER_ROLE')")
    public void trackLoanOfficerAssignmentHistory(final Loan loan, final Staff fromLoanOfficer,
                                                  final Staff toLoanOfficer, final LocalDate startDate);

    @PreAuthorize(value = "hasRole('ORGANISATION_ADMINISTRATION_SUPER_USER_ROLE')")
    public void untrackNewLoanOfficerAssignmentHistory(final Loan loan);

}
