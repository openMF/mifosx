package org.mifosng.platform.loan.domain;


import org.mifosng.platform.staff.domain.Staff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface LoanOfficerAssignmentHistoryRepository extends JpaRepository<LoanOfficerAssignmentHistory, Long>,
        JpaSpecificationExecutor<LoanOfficerAssignmentHistory> {

    LoanOfficerAssignmentHistory findByLoanAndLoanOfficerAndEndDateIsNull(Loan loan, Staff loanOfficer);

}
