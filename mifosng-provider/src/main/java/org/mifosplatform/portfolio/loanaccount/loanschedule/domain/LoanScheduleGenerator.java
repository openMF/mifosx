/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.loanaccount.loanschedule.domain;

import java.math.MathContext;
import java.util.List;
import java.util.Set;

import org.joda.time.LocalDate;
import org.mifosplatform.organisation.holiday.domain.Holiday;
import org.mifosplatform.organisation.monetary.domain.ApplicationCurrency;
import org.mifosplatform.organisation.monetary.domain.MonetaryCurrency;
import org.mifosplatform.organisation.workingdays.domain.WorkingDays;
import org.mifosplatform.portfolio.loanaccount.domain.LoanCharge;
import org.mifosplatform.portfolio.loanaccount.domain.LoanRepaymentScheduleInstallment;
import org.mifosplatform.portfolio.loanaccount.domain.LoanTransaction;
import org.mifosplatform.portfolio.loanaccount.domain.transactionprocessor.LoanRepaymentScheduleTransactionProcessor;
import org.mifosplatform.portfolio.loanaccount.rescheduleloan.domain.LoanRescheduleModel;
import org.mifosplatform.portfolio.loanaccount.rescheduleloan.domain.LoanRescheduleRequest;

public interface LoanScheduleGenerator {

    LoanScheduleModel generate(MathContext mc, ApplicationCurrency applicationCurrency, LoanApplicationTerms loanApplicationTerms,
            Set<LoanCharge> loanCharges, boolean isHolidayEnabled, List<Holiday> holidays, final WorkingDays workingDays);

    LoanScheduleModel rescheduleNextInstallments(MathContext mc, ApplicationCurrency applicationCurrency,
            LoanApplicationTerms loanApplicationTerms, Set<LoanCharge> loanCharges, boolean isHolidayEnabled, List<Holiday> holidays,
            WorkingDays workingDays, List<LoanTransaction> transactions,
            LoanRepaymentScheduleTransactionProcessor loanRepaymentScheduleTransactionProcessor,
            List<LoanRepaymentScheduleInstallment> previousSchedule, LocalDate recalculateFrom);

    LoanRepaymentScheduleInstallment calculatePrepaymentAmount(List<LoanRepaymentScheduleInstallment> installments, MonetaryCurrency currency,
            LoanApplicationTerms applicationTerms, LocalDate onDate);
    
    LoanRescheduleModel reschedule(final MathContext mathContext, final LoanRescheduleRequest loanRescheduleRequest, 
			final ApplicationCurrency applicationCurrency, final boolean isHolidayEnabled, 
			final List<Holiday> holidays, final WorkingDays workingDays);
}