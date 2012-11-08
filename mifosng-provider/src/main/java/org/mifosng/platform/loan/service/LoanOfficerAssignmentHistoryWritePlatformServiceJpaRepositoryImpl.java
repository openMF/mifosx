package org.mifosng.platform.loan.service;

import org.joda.time.LocalDate;
import org.mifosng.platform.exceptions.LoanOfficerAssignmentException;
import org.mifosng.platform.loan.domain.Loan;
import org.mifosng.platform.loan.domain.LoanOfficerAssignmentHistory;
import org.mifosng.platform.loan.domain.LoanOfficerAssignmentHistoryRepository;
import org.mifosng.platform.staff.domain.Staff;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LoanOfficerAssignmentHistoryWritePlatformServiceJpaRepositoryImpl implements LoanOfficerAssignmentHistoryWritePlatformService {

    private final LoanOfficerAssignmentHistoryRepository loanOfficerAssignmentHistoryRepository;

    @Autowired
    public LoanOfficerAssignmentHistoryWritePlatformServiceJpaRepositoryImpl(
            final LoanOfficerAssignmentHistoryRepository loanOfficerAssignmentHistoryRepository) {
        this.loanOfficerAssignmentHistoryRepository = loanOfficerAssignmentHistoryRepository;
    }

    @Transactional
    @Override
    public void trackLoanOfficerAssignmentHistory(final Loan loan, final Staff fromLoanOfficer,
                                                  final Staff toLoanOfficer, final LocalDate startDate) {
        boolean createNewLoanOfficerAssignmentHistoryRecord = true;

        LoanOfficerAssignmentHistory prevLoanOfficerAssignmentHistory = this.loanOfficerAssignmentHistoryRepository.
                findByLoanAndLoanOfficerAndEndDateIsNull(loan, fromLoanOfficer);

        if (prevLoanOfficerAssignmentHistory != null){

            if (prevLoanOfficerAssignmentHistory.wasAlreadyAssignedToday()){
                prevLoanOfficerAssignmentHistory.updateStartDate(startDate);
                prevLoanOfficerAssignmentHistory.updateLoanOfficer(toLoanOfficer);
                createNewLoanOfficerAssignmentHistoryRecord = false;
            } else {
                validateAssignmentStartDate(prevLoanOfficerAssignmentHistory, startDate);
                prevLoanOfficerAssignmentHistory.updateEndDate(startDate);
            }

            this.loanOfficerAssignmentHistoryRepository.save(prevLoanOfficerAssignmentHistory);
        }

        if (createNewLoanOfficerAssignmentHistoryRecord){
            LoanOfficerAssignmentHistory newLoanOfficerAssignmentHistory = LoanOfficerAssignmentHistory.
                    createNew(loan, toLoanOfficer, startDate);
            this.loanOfficerAssignmentHistoryRepository.save(newLoanOfficerAssignmentHistory);
        }
    }

    @Transactional
    @Override
    public void untrackNewLoanOfficerAssignmentHistory(final Loan loan) {
        if (loan.hasLoanOfficer()){
            LoanOfficerAssignmentHistory loanOfficerAssignmentHistory = this.loanOfficerAssignmentHistoryRepository.
                    findByLoanAndLoanOfficerAndEndDateIsNull(loan, loan.getLoanofficer());
            if (loanOfficerAssignmentHistory != null){
                this.loanOfficerAssignmentHistoryRepository.delete(loanOfficerAssignmentHistory);
            }
        }
    }

    private void validateAssignmentStartDate(LoanOfficerAssignmentHistory loanOfficerAssignmentHistory, LocalDate startDate){
        if (startDate.isBefore(loanOfficerAssignmentHistory.getStartDate())){
            throw new LoanOfficerAssignmentException(loanOfficerAssignmentHistory.getLoan().getId(), startDate);
        }
    }
}
