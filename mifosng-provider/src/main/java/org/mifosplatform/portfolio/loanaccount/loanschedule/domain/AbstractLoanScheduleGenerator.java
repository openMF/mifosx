/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.loanaccount.loanschedule.domain;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.mifosplatform.organisation.holiday.domain.Holiday;
import org.mifosplatform.organisation.monetary.domain.ApplicationCurrency;
import org.mifosplatform.organisation.monetary.domain.MonetaryCurrency;
import org.mifosplatform.organisation.monetary.domain.Money;
import org.mifosplatform.organisation.workingdays.domain.WorkingDays;
import org.mifosplatform.portfolio.calendar.domain.CalendarInstance;
import org.mifosplatform.portfolio.calendar.service.CalendarUtils;
import org.mifosplatform.portfolio.loanaccount.data.DisbursementData;
import org.mifosplatform.portfolio.loanaccount.domain.Loan;
import org.mifosplatform.portfolio.loanaccount.domain.LoanCharge;
import org.mifosplatform.portfolio.loanaccount.domain.LoanRepaymentScheduleInstallment;
import org.mifosplatform.portfolio.loanaccount.domain.LoanTransaction;
import org.mifosplatform.portfolio.loanaccount.domain.transactionprocessor.LoanRepaymentScheduleTransactionProcessor;
import org.mifosplatform.portfolio.loanaccount.domain.LoanSummary;
import org.mifosplatform.portfolio.loanaccount.loanschedule.exception.MultiDisbursementDisbursementDateException;
import org.mifosplatform.portfolio.loanaccount.loanschedule.exception.MultiDisbursementEmiAmountException;
import org.mifosplatform.portfolio.loanaccount.loanschedule.exception.MultiDisbursementOutstandingAmoutException;
import org.mifosplatform.portfolio.loanaccount.rescheduleloan.domain.LoanRepaymentScheduleHistory;
import org.mifosplatform.portfolio.loanaccount.rescheduleloan.domain.LoanRescheduleModel;
import org.mifosplatform.portfolio.loanaccount.rescheduleloan.domain.LoanRescheduleModelRepaymentPeriod;
import org.mifosplatform.portfolio.loanaccount.rescheduleloan.domain.LoanRescheduleRequest;
import org.mifosplatform.portfolio.loanproduct.domain.LoanProductMinimumRepaymentScheduleRelatedDetail;
import org.mifosplatform.useradministration.domain.AppUser;

/**
 *
 */
public abstract class AbstractLoanScheduleGenerator implements LoanScheduleGenerator {

    private final ScheduledDateGenerator scheduledDateGenerator = new DefaultScheduledDateGenerator();
    private final PaymentPeriodsInOneYearCalculator paymentPeriodsInOneYearCalculator = new DefaultPaymentPeriodsInOneYearCalculator();

    @Override
    public LoanScheduleModel generate(final MathContext mc, final ApplicationCurrency applicationCurrency,
            final LoanApplicationTerms loanApplicationTerms, final Set<LoanCharge> loanCharges, final boolean isHolidayEnabled,
            final List<Holiday> holidays, final WorkingDays workingDays) {
        final List<RecalculationDetail> diffAmt = null;
        return generate(mc, applicationCurrency, loanApplicationTerms, loanCharges, isHolidayEnabled, holidays, workingDays, diffAmt);
    }

    private LoanScheduleModel generate(final MathContext mc, final ApplicationCurrency applicationCurrency,
            final LoanApplicationTerms loanApplicationTerms, final Set<LoanCharge> loanCharges, final boolean isHolidayEnabled,
            final List<Holiday> holidays, final WorkingDays workingDays, List<RecalculationDetail> diffAmt) {

        // 1. generate list of proposed schedule due dates
        final LocalDate loanEndDate = this.scheduledDateGenerator.getLastRepaymentDate(loanApplicationTerms, isHolidayEnabled, holidays,
                workingDays);
        loanApplicationTerms.updateLoanEndDate(loanEndDate);

        // 2. determine the total charges due at time of disbursement
        final BigDecimal chargesDueAtTimeOfDisbursement = deriveTotalChargesDueAtTimeOfDisbursement(loanCharges);

        // 3. setup variables for tracking important facts required for loan
        // schedule generation.
        Money principalDisbursed = loanApplicationTerms.getPrincipal();
        final Money expectedPrincipalDisburse = loanApplicationTerms.getPrincipal();
        final MonetaryCurrency currency = principalDisbursed.getCurrency();
        final int numberOfRepayments = loanApplicationTerms.getNumberOfRepayments();

        // variables for cumulative totals
        int loanTermInDays = Integer.valueOf(0);
        BigDecimal totalPrincipalExpected = BigDecimal.ZERO;
        final BigDecimal totalPrincipalPaid = BigDecimal.ZERO;
        BigDecimal totalInterestCharged = BigDecimal.ZERO;
        BigDecimal totalFeeChargesCharged = chargesDueAtTimeOfDisbursement;
        BigDecimal totalPenaltyChargesCharged = BigDecimal.ZERO;
        BigDecimal totalRepaymentExpected = chargesDueAtTimeOfDisbursement;
        final BigDecimal totalOutstanding = BigDecimal.ZERO;

        final Collection<LoanScheduleModelPeriod> periods = createNewLoanScheduleListWithDisbursementDetails(numberOfRepayments,
                loanApplicationTerms, chargesDueAtTimeOfDisbursement);

        // 4. Determine the total interest owed over the full loan for FLAT
        // interest method .
        Money totalInterestChargedForFullLoanTerm = loanApplicationTerms.calculateTotalInterestCharged(
                this.paymentPeriodsInOneYearCalculator, mc);

        LocalDate periodStartDate = loanApplicationTerms.getExpectedDisbursementDate();
        LocalDate actualRepaymentDate = periodStartDate;
        boolean isFirstRepayment = true;
        LocalDate firstRepaymentdate = this.scheduledDateGenerator.generateNextRepaymentDate(periodStartDate, loanApplicationTerms,
                isFirstRepayment);
        final LocalDate idealDisbursementDate = this.scheduledDateGenerator.idealDisbursementDateBasedOnFirstRepaymentDate(
                loanApplicationTerms.getLoanTermPeriodFrequencyType(), loanApplicationTerms.getRepaymentEvery(), firstRepaymentdate);

        LocalDate periodStartDateApplicableForInterest = periodStartDate;

        int periodNumber = 1;
        int instalmentNumber = 1;
        Money totalCumulativePrincipal = principalDisbursed.zero();
        Money totalCumulativeInterest = principalDisbursed.zero();
        Money totalOutstandingInterestPaymentDueToGrace = principalDisbursed.zero();
        Money outstandingBalance = principalDisbursed;
        if (loanApplicationTerms.isMultiDisburseLoan()) {
            BigDecimal disburseAmt = getDisbursementAmount(loanApplicationTerms, periodStartDate, periods, chargesDueAtTimeOfDisbursement);
            principalDisbursed = principalDisbursed.zero().plus(disburseAmt);
            loanApplicationTerms.setPrincipal(loanApplicationTerms.getPrincipal().zero().plus(disburseAmt));
            outstandingBalance = outstandingBalance.zero().plus(disburseAmt);
        }
        Money reducePrincipal = totalCumulativePrincipal.zero();
        Money fixedEmiAmount = totalCumulativePrincipal.zero();
        int daysCalcForInstallmentNumber = 0;
        LocalDate scheduleStartDateAsPerFrequency = periodStartDate;
        while (!outstandingBalance.isZero()) {
            isFirstRepayment = false;
            // to insert a new schedule between actual schedule to collect
            // interest(till the transaction date) first on a payment
            boolean recalculatedInterestComponent = false;
            RecalculationDetail interestonlyPeriodDetail = null;
            if (diffAmt != null) {
                for (RecalculationDetail recalculationDetail : diffAmt) {
                    if (recalculationDetail.isInterestompound() && recalculationDetail.getStartDate().isEqual(periodStartDate)) {
                        interestonlyPeriodDetail = recalculationDetail;
                        break;
                    }
                }
            }
            LocalDate scheduledDueDate = null;
            LocalDate scheduledDueDateAsPerFrequency = null;

            int daysInPeriodApplicableInFullInstallment = 0;
            // will change the schedule dates as per the interest only Period
            if (interestonlyPeriodDetail == null) {
                actualRepaymentDate = this.scheduledDateGenerator.generateNextRepaymentDate(actualRepaymentDate, loanApplicationTerms,
                        isFirstRepayment);
                scheduledDueDate = this.scheduledDateGenerator.adjustRepaymentDate(actualRepaymentDate, loanApplicationTerms,
                        isHolidayEnabled, holidays, workingDays);
                scheduledDueDateAsPerFrequency = scheduledDueDate;
            } else {
                recalculatedInterestComponent = true;
                scheduledDueDate = interestonlyPeriodDetail.getToDate();
                daysCalcForInstallmentNumber = periodNumber;
                LocalDate actualRepayment = this.scheduledDateGenerator.generateNextRepaymentDate(actualRepaymentDate,
                        loanApplicationTerms, isFirstRepayment);
                scheduledDueDateAsPerFrequency = this.scheduledDateGenerator.adjustRepaymentDate(actualRepayment, loanApplicationTerms,
                        isHolidayEnabled, holidays, workingDays);
            }

            final int daysInPeriod = Days.daysBetween(periodStartDate, scheduledDueDate).getDays();
            if (loanApplicationTerms.isMultiDisburseLoan()) {
                loanApplicationTerms.setFixedEmiAmountForPeriod(scheduledDueDate);
                BigDecimal disburseAmt = disbursementForPeriod(loanApplicationTerms, periodStartDate, scheduledDueDate, periods,
                        BigDecimal.ZERO);
                principalDisbursed = principalDisbursed.plus(disburseAmt);
                loanApplicationTerms.setPrincipal(loanApplicationTerms.getPrincipal().plus(disburseAmt));
                outstandingBalance = outstandingBalance.plus(disburseAmt);
                if (loanApplicationTerms.getMaxOutstandingBalance() != null
                        && outstandingBalance.isGreaterThan(loanApplicationTerms.getMaxOutstandingBalance())) {
                    String errorMsg = "Outstanding balance must not exceed the amount: " + loanApplicationTerms.getMaxOutstandingBalance();
                    throw new MultiDisbursementOutstandingAmoutException(errorMsg, loanApplicationTerms.getMaxOutstandingBalance()
                            .getAmount(), disburseAmt);
                }
            }
            int daysInPeriodApplicableForInterest = daysInPeriod;

            if (periodStartDate.isBefore(idealDisbursementDate)) {
                if (loanApplicationTerms.getInterestChargedFromLocalDate() != null) {
                    periodStartDateApplicableForInterest = loanApplicationTerms.getInterestChargedFromLocalDate();
                } else {
                    periodStartDateApplicableForInterest = idealDisbursementDate;
                }
                daysInPeriodApplicableForInterest = Days.daysBetween(periodStartDateApplicableForInterest, scheduledDueDate).getDays();
            }

            // this block identifies full payment period as per the
            // schedule(excluding interest payments) for interest calculation
            daysInPeriodApplicableInFullInstallment = daysInPeriodApplicableForInterest;
            if (daysCalcForInstallmentNumber == periodNumber) {
                periodStartDateApplicableForInterest = scheduleStartDateAsPerFrequency;
                if (scheduleStartDateAsPerFrequency.isBefore(idealDisbursementDate)) {
                    if (loanApplicationTerms.getInterestChargedFromLocalDate() != null) {
                        periodStartDateApplicableForInterest = loanApplicationTerms.getInterestChargedFromLocalDate();
                    } else {
                        periodStartDateApplicableForInterest = idealDisbursementDate;
                    }
                }

                daysInPeriodApplicableInFullInstallment = Days.daysBetween(periodStartDateApplicableForInterest,
                        scheduledDueDateAsPerFrequency).getDays();
            }
            double interestCalculationGraceOnRepaymentPeriodFraction = this.paymentPeriodsInOneYearCalculator
                    .calculatePortionOfRepaymentPeriodInterestChargingGrace(periodStartDateApplicableForInterest,
                            scheduledDueDateAsPerFrequency, loanApplicationTerms.getInterestChargedFromLocalDate(),
                            loanApplicationTerms.getLoanTermPeriodFrequencyType(), loanApplicationTerms.getRepaymentEvery());

            Money balanceForcalculation = outstandingBalance;
            // reduce principal is the early payment, will processed as per the
            // reschedule strategy
            if (reducePrincipal.isGreaterThanZero()) {
                switch (loanApplicationTerms.getRescheduleStrategyMethod()) {
                    case REDUCE_EMI_AMOUNT:
                        loanApplicationTerms.setFixedEmiAmount(null);
                        outstandingBalance = outstandingBalance.minus(reducePrincipal);
                        balanceForcalculation = outstandingBalance;
                        totalCumulativePrincipal = totalCumulativePrincipal.plus(reducePrincipal);
                        reducePrincipal = reducePrincipal.zero();
                    break;
                    case REDUCE_NUMBER_OF_INSTALLMENTS:
                        loanApplicationTerms.setFixedEmiAmount(fixedEmiAmount.getAmount());
                        outstandingBalance = outstandingBalance.minus(reducePrincipal);
                        balanceForcalculation = outstandingBalance;
                        totalCumulativePrincipal = totalCumulativePrincipal.plus(reducePrincipal);
                        reducePrincipal = reducePrincipal.zero();
                    break;
                    case RESCHEDULE_NEXT_REPAYMENTS:
                        balanceForcalculation = balanceForcalculation.minus(reducePrincipal);
                    break;
                    default:
                    break;
                }
            }

            if (!balanceForcalculation.isGreaterThanZero()) {
                break;
            }

            // 5 determine principal,interest of repayment period
            PrincipalInterest principalInterestForThisPeriod = calculatePrincipalInterestComponentsForPeriod(
                    this.paymentPeriodsInOneYearCalculator, interestCalculationGraceOnRepaymentPeriodFraction, totalCumulativePrincipal,
                    totalCumulativeInterest, totalInterestChargedForFullLoanTerm, totalOutstandingInterestPaymentDueToGrace,
                    daysInPeriodApplicableInFullInstallment, balanceForcalculation, loanApplicationTerms, periodNumber, mc);

            if (loanApplicationTerms.getFixedEmiAmount() != null
                    && loanApplicationTerms.getFixedEmiAmount().compareTo(principalInterestForThisPeriod.interest().getAmount()) != 1) {
                String errorMsg = "EMI amount must be greter than : " + principalInterestForThisPeriod.interest().getAmount();
                throw new MultiDisbursementEmiAmountException(errorMsg, principalInterestForThisPeriod.interest().getAmount(),
                        loanApplicationTerms.getFixedEmiAmount());
            }
            // update cumulative fields for principal & interest
            Money interestForThisinstallment = principalInterestForThisPeriod.interest();
            if (daysCalcForInstallmentNumber == periodNumber) {
                interestForThisinstallment = interestForThisinstallment.zero().plus(
                        calculateInterestForDays(daysInPeriodApplicableInFullInstallment, interestForThisinstallment,
                                daysInPeriodApplicableForInterest));
            }
            Money principalForThisPeriod = principalDisbursed.zero();
            Money outstandingForThePeriod = outstandingBalance;
            // Exclude principal portion for interest only installments
            if (!recalculatedInterestComponent) {
                principalForThisPeriod = principalInterestForThisPeriod.principal();
                if (daysCalcForInstallmentNumber == periodNumber && loanApplicationTerms.getAmortizationMethod().isEqualInstallment()) {
                    principalForThisPeriod = principalForThisPeriod.plus(principalInterestForThisPeriod.interest()).minus(
                            interestForThisinstallment);
                }
                totalCumulativePrincipal = totalCumulativePrincipal.plus(principalForThisPeriod);

                // 6. update outstandingLoanBlance using correct 'principalDue'
                outstandingBalance = outstandingBalance.minus(principalForThisPeriod);
            }

            totalCumulativeInterest = totalCumulativeInterest.plus(interestForThisinstallment);
            totalOutstandingInterestPaymentDueToGrace = principalInterestForThisPeriod.interestPaymentDueToGrace();

            Money actualOutstandingbalance = outstandingBalance;

            Money feeChargesForInstallment = principalDisbursed.zero();
            Money penaltyChargesForInstallment = principalDisbursed.zero();

            if (principalForThisPeriod.isGreaterThan(reducePrincipal)) {
                principalForThisPeriod = principalForThisPeriod.minus(reducePrincipal);
                reducePrincipal = reducePrincipal.zero();
            } else {
                reducePrincipal = reducePrincipal.minus(principalForThisPeriod);
                principalForThisPeriod = principalForThisPeriod.zero();
            }

            if (periodNumber == 1) {
                fixedEmiAmount = principalForThisPeriod.plus(interestForThisinstallment);
            }

            Money extraPrincipal = reducePrincipal.zero();
            // this block is to identify interest based on late/early payment
            if (diffAmt != null && !diffAmt.isEmpty()) {
                BigDecimal interestDueToLatePayment = BigDecimal.ZERO;
                BigDecimal interestReducedDueToEarlyPayment = BigDecimal.ZERO;

                boolean isperiodPrincipalReduced = false;
                for (RecalculationDetail detail : diffAmt) {
                    if (!detail.isInterestompound()) {
                        // will increase the principal portion and reduces
                        // interest
                        // as per the number of days. and also identifies reduce
                        // principal for reschedule strategy
                        if (!detail.isLatePayment() && detail.getStartDate().isAfter(periodStartDate)
                                && !detail.getStartDate().isAfter(scheduledDueDate)) {
                            reducePrincipal = reducePrincipal.plus(detail.getAmount());
                            extraPrincipal = extraPrincipal.plus(detail.getAmount());
                            int diffDays = Days.daysBetween(detail.getStartDate(), scheduledDueDate).getDays();
                            if (diffDays > 0 && !isperiodPrincipalReduced) {
                                reducePrincipal = reducePrincipal.minus(principalForThisPeriod);
                                isperiodPrincipalReduced = true;
                            }

                            LocalDate startDate = detail.getStartDate();
                            if (loanApplicationTerms.getInterestChargedFromLocalDate() != null
                                    && startDate.isBefore(loanApplicationTerms.getInterestChargedFromLocalDate())) {
                                startDate = loanApplicationTerms.getInterestChargedFromLocalDate();
                            }
                            LocalDate endDate = scheduledDueDate;
                            if (startDate.isBefore(endDate)) {
                                Money amountForInterestCalculation = detail.getAmount();
                                Money balanceDiff = outstandingForThePeriod.minus(extraPrincipal);
                                if (balanceDiff.isLessThanZero()) {
                                    amountForInterestCalculation = amountForInterestCalculation.plus(balanceDiff);
                                }
                                interestReducedDueToEarlyPayment = interestReducedDueToEarlyPayment.add(calculateInterestForSpecificDays(
                                        mc, loanApplicationTerms, periodNumber, totalOutstandingInterestPaymentDueToGrace,
                                        daysInPeriodApplicableInFullInstallment, interestCalculationGraceOnRepaymentPeriodFraction,
                                        amountForInterestCalculation, startDate, endDate));
                            }

                        }
                        // calculates the interest for late payment and increase
                        // the interest for the installment
                        else if (detail.isLatePayment() && detail.isOverlapping(periodStartDate, scheduledDueDate)
                                && periodStartDate.isBefore(LocalDate.now())) {
                            LocalDate fromDate = periodStartDate;
                            LocalDate toDate = scheduledDueDate;
                            if (!detail.getStartDate().isBefore(periodStartDate)) {
                                fromDate = detail.getStartDate();
                            }
                            if (!detail.getToDate().isAfter(scheduledDueDate)) {
                                toDate = detail.getToDate();
                            }
                            if (toDate.isAfter(LocalDate.now())) {
                                toDate = LocalDate.now();
                            }
                            if (fromDate.isAfter(LocalDate.now())) {
                                fromDate = LocalDate.now();
                            }
                            if (loanApplicationTerms.getInterestChargedFromLocalDate() != null
                                    && fromDate.isBefore(loanApplicationTerms.getInterestChargedFromLocalDate())) {
                                fromDate = loanApplicationTerms.getInterestChargedFromLocalDate();
                            }
                            if (fromDate.isBefore(toDate)) {
                                interestDueToLatePayment = interestDueToLatePayment.add(calculateInterestForSpecificDays(mc,
                                        loanApplicationTerms, periodNumber, totalOutstandingInterestPaymentDueToGrace,
                                        daysInPeriodApplicableInFullInstallment, interestCalculationGraceOnRepaymentPeriodFraction,
                                        detail.getAmount(), fromDate, toDate));
                            }
                        }
                    }
                }

                if (reducePrincipal.isLessThanZero()) {
                    reducePrincipal = reducePrincipal.zero();
                } else {
                    Money actualOutstanding = outstandingBalance.minus(reducePrincipal);
                    principalForThisPeriod = reducePrincipal.plus(principalForThisPeriod);
                    if (actualOutstanding.isLessThanZero()) {
                        principalForThisPeriod = principalForThisPeriod.plus(actualOutstanding);
                    }
                }

                if (totalOutstandingInterestPaymentDueToGrace.isGreaterThanZero()) {
                    totalOutstandingInterestPaymentDueToGrace = totalOutstandingInterestPaymentDueToGrace.plus(interestDueToLatePayment)
                            .minus(interestReducedDueToEarlyPayment);
                } else {
                    totalCumulativeInterest = totalCumulativeInterest.plus(interestDueToLatePayment)
                            .minus(interestReducedDueToEarlyPayment);
                    interestForThisinstallment = interestForThisinstallment.plus(interestDueToLatePayment).minus(
                            interestReducedDueToEarlyPayment);
                }

            }
            actualOutstandingbalance = actualOutstandingbalance.minus(reducePrincipal);

            // 8. sum up real totalInstallmentDue from components
            final Money totalInstallmentDue = principalForThisPeriod//
                    .plus(interestForThisinstallment) //
                    .plus(feeChargesForInstallment) //
                    .plus(penaltyChargesForInstallment);

            // 9. create repayment period from parts
            final LoanScheduleModelPeriod installment = LoanScheduleModelRepaymentPeriod.repayment(instalmentNumber, periodStartDate,
                    scheduledDueDate, principalForThisPeriod, actualOutstandingbalance, interestForThisinstallment,
                    feeChargesForInstallment, penaltyChargesForInstallment, totalInstallmentDue, recalculatedInterestComponent);
            periods.add(installment);

            // handle cumulative fields
            loanTermInDays += daysInPeriod;
            totalPrincipalExpected = totalPrincipalExpected.add(principalInterestForThisPeriod.principal().getAmount());
            totalInterestCharged = totalInterestCharged.add(interestForThisinstallment.getAmount());
            totalFeeChargesCharged = totalFeeChargesCharged.add(feeChargesForInstallment.getAmount());
            totalPenaltyChargesCharged = totalPenaltyChargesCharged.add(penaltyChargesForInstallment.getAmount());
            totalRepaymentExpected = totalRepaymentExpected.add(totalInstallmentDue.getAmount());
            periodStartDate = scheduledDueDate;
            periodStartDateApplicableForInterest = periodStartDate;

            if (!recalculatedInterestComponent) {
                periodNumber++;
                scheduleStartDateAsPerFrequency = periodStartDate;
            }
            instalmentNumber++;
        }

        if (principalDisbursed.isNotEqualTo(expectedPrincipalDisburse)) {
            final String errorMsg = "One of the Disbursement date is not falling on Loan Schedule";
            throw new MultiDisbursementDisbursementDateException(errorMsg);
        }

        // 7. determine fees and penalties
        for (LoanScheduleModelPeriod loanScheduleModelPeriod : periods) {
            if (loanScheduleModelPeriod.isRepaymentPeriod()) {
                PrincipalInterest principalInterest = new PrincipalInterest(Money.of(currency, loanScheduleModelPeriod.principalDue()),
                        Money.of(currency, loanScheduleModelPeriod.interestDue()), null);
                Money feeChargesForInstallment = cumulativeFeeChargesDueWithin(loanScheduleModelPeriod.periodFromDate(),
                        loanScheduleModelPeriod.periodDueDate(), loanCharges, currency, principalInterest, principalDisbursed,
                        totalCumulativeInterest, numberOfRepayments, !loanScheduleModelPeriod.isRecalculatedInterestComponent());
                Money penaltyChargesForInstallment = cumulativePenaltyChargesDueWithin(loanScheduleModelPeriod.periodFromDate(),
                        loanScheduleModelPeriod.periodDueDate(), loanCharges, currency, principalInterest, principalDisbursed,
                        totalCumulativeInterest, numberOfRepayments, !loanScheduleModelPeriod.isRecalculatedInterestComponent());
                totalFeeChargesCharged = totalFeeChargesCharged.add(feeChargesForInstallment.getAmount());
                totalPenaltyChargesCharged = totalPenaltyChargesCharged.add(penaltyChargesForInstallment.getAmount());
                loanScheduleModelPeriod.addLoanCharges(feeChargesForInstallment.getAmount(), penaltyChargesForInstallment.getAmount());
            }
        }

        // this block is to add extra re-payment schedules with interest portion
        // if the last payment is missed
        if (diffAmt != null && !diffAmt.isEmpty() && !periodStartDate.isAfter(LocalDate.now())) {
            boolean recalculatedInterestComponent = true;
            Map<LocalDate, RecalculationDetail> processDetails = new TreeMap<>();
            for (RecalculationDetail detail : diffAmt) {
                if (!periodStartDate.isAfter(detail.getStartDate()) && detail.isLatePayment() && !detail.isInterestompound()) {
                    if (processDetails.containsKey(detail.getToDate())) {
                        RecalculationDetail recalculationDetail = processDetails.get(detail.getToDate());
                        RecalculationDetail updatedDetail = new RecalculationDetail(recalculationDetail.isLatePayment(),
                                recalculationDetail.getStartDate(), recalculationDetail.getToDate(), recalculationDetail.getAmount().plus(
                                        detail.getAmount()), detail.isInterestompound());
                        processDetails.put(updatedDetail.getToDate(), updatedDetail);
                    } else {
                        processDetails.put(detail.getToDate(), detail);
                    }
                }
            }

            for (RecalculationDetail detail : processDetails.values()) {
                LocalDate fromDate = detail.getStartDate();
                LocalDate toDate = detail.getToDate();
                if (!toDate.isAfter(LocalDate.now())) {
                    BigDecimal interestForLatePayment = loanApplicationTerms.interestRateFor(this.paymentPeriodsInOneYearCalculator, mc,
                            detail.getAmount(), fromDate, toDate);
                    Money interestDueToLatePayment = Money.of(detail.getAmount().getCurrency(), interestForLatePayment);
                    if (interestDueToLatePayment.isGreaterThanZero()) {
                        totalInterestCharged = totalInterestCharged.add(interestDueToLatePayment.getAmount());
                        totalRepaymentExpected = totalRepaymentExpected.add(interestDueToLatePayment.getAmount());

                        final LoanScheduleModelPeriod installment = LoanScheduleModelRepaymentPeriod.repayment(instalmentNumber, fromDate,
                                toDate, interestDueToLatePayment.zero(), interestDueToLatePayment.zero(), interestDueToLatePayment,
                                interestDueToLatePayment.zero(), interestDueToLatePayment.zero(), interestDueToLatePayment,
                                recalculatedInterestComponent);
                        periods.add(installment);
                        instalmentNumber++;
                    }
                }
            }
        }

        return LoanScheduleModel.from(periods, applicationCurrency, loanTermInDays, principalDisbursed, totalPrincipalExpected,
                totalPrincipalPaid, totalInterestCharged, totalFeeChargesCharged, totalPenaltyChargesCharged, totalRepaymentExpected,
                totalOutstanding);
    }
    
    @Override
	public LoanRescheduleModel reschedule(final MathContext mathContext, final LoanRescheduleRequest loanRescheduleRequest, 
			final ApplicationCurrency applicationCurrency, final boolean isHolidayEnabled, 
			final List<Holiday> holidays, final WorkingDays workingDays) {
		
		final Loan loan = loanRescheduleRequest.getLoan();
		final LoanSummary loanSummary = loan.getSummary();
		final LoanProductMinimumRepaymentScheduleRelatedDetail loanProductRelatedDetail = loan.getLoanRepaymentScheduleDetail();
		final MonetaryCurrency currency = loanProductRelatedDetail.getCurrency();
		
		// create an archive of the current loan schedule installments
		Collection<LoanRepaymentScheduleHistory> loanRepaymentScheduleHistoryList = createLoanScheduleArchive(loanRescheduleRequest);
		
		// get the initial list of repayment installments
		List<LoanRepaymentScheduleInstallment> repaymentScheduleInstallments = loan.getRepaymentScheduleInstallments();
		
		// sort list by installment number in ASC order
		Collections.sort(repaymentScheduleInstallments, LoanRepaymentScheduleInstallment.installmentNumberComparator);
		
		final Collection<LoanRescheduleModelRepaymentPeriod> periods = new ArrayList<LoanRescheduleModelRepaymentPeriod>();
		
		Money outstandingLoanBalance = loan.getPrincpal();
		
		for(LoanRepaymentScheduleInstallment repaymentScheduleInstallment : repaymentScheduleInstallments) {
			
			Integer oldPeriodNumber = repaymentScheduleInstallment.getInstallmentNumber();
			LocalDate fromDate = repaymentScheduleInstallment.getFromDate();
			LocalDate dueDate = repaymentScheduleInstallment.getDueDate();
			Money principalDue = repaymentScheduleInstallment.getPrincipal(currency);
			Money interestDue = repaymentScheduleInstallment.getInterestCharged(currency);
			Money feeChargesDue = repaymentScheduleInstallment.getFeeChargesCharged(currency);
			Money penaltyChargesDue = repaymentScheduleInstallment.getPenaltyChargesCharged(currency);
			Money totalDue = principalDue.plus(interestDue).plus(feeChargesDue).plus(penaltyChargesDue);
			
			outstandingLoanBalance = outstandingLoanBalance.minus(principalDue);
			
			LoanRescheduleModelRepaymentPeriod period = LoanRescheduleModelRepaymentPeriod.instance(oldPeriodNumber, oldPeriodNumber, 
					fromDate, dueDate, principalDue, outstandingLoanBalance, interestDue, feeChargesDue, penaltyChargesDue, 
					totalDue, false);
			
			periods.add(period);
		}
		
        Money outstandingBalance = loan.getPrincpal();
		Money totalCumulativePrincipal = Money.zero(currency);
		Money totalCumulativeInterest = Money.zero(currency);
		Money actualTotalCumulativeInterest = Money.zero(currency);
		Money totalOutstandingInterestPaymentDueToGrace = Money.zero(currency);
		Money totalPrincipalBeforeReschedulePeriod = Money.zero(currency);
        
		LocalDate installmentDueDate = null;
		LocalDate adjustedInstallmentDueDate = null;
		LocalDate installmentFromDate = null;
		Integer rescheduleFromInstallmentNo = defaultToZeroIfNull(loanRescheduleRequest.getRescheduleFromInstallment());
		Integer installmentNumber = rescheduleFromInstallmentNo;
		Integer graceOnPrincipal = defaultToZeroIfNull(loanRescheduleRequest.getGraceOnPrincipal());
		Integer graceOnInterest = defaultToZeroIfNull(loanRescheduleRequest.getGraceOnInterest());
		Integer extraTerms = defaultToZeroIfNull(loanRescheduleRequest.getExtraTerms());
		final boolean recalculateInterest = loanRescheduleRequest.getRecalculateInterest();
		Integer numberOfRepayments = repaymentScheduleInstallments.size();
		Integer rescheduleNumberOfRepayments = numberOfRepayments;
		final Money principal = loan.getPrincpal();
		final Money totalPrincipalOutstanding = Money.of(currency, loanSummary.getTotalPrincipalOutstanding());
		LocalDate adjustedDueDate = loanRescheduleRequest.getAdjustedDueDate();
		BigDecimal newInterestRate = loanRescheduleRequest.getInterestRate();
		int loanTermInDays = Integer.valueOf(0);
		
		if(rescheduleFromInstallmentNo > 0) {
			// this will hold the loan repayment installment that is before the reschedule start installment 
			// (rescheduleFrominstallment)
			LoanRepaymentScheduleInstallment previousInstallment = null;
			
			// get the install number of the previous installment
			int previousInstallmentNo = rescheduleFromInstallmentNo - 1;
			
			// only fetch the installment if the number is greater than 0
			if(previousInstallmentNo > 0) {
				previousInstallment = loan.fetchRepaymentScheduleInstallment(previousInstallmentNo);
			}
			
			LoanRepaymentScheduleInstallment firstInstallment = loan.fetchRepaymentScheduleInstallment(1);
			
			// the "installment from date" is equal to the due date of the previous installment, if it exists
			if(previousInstallment != null) {
				installmentFromDate = previousInstallment.getDueDate();
			}
			
			else {
				installmentFromDate = firstInstallment.getFromDate();
			}
			
			installmentDueDate = installmentFromDate;
			LocalDate periodStartDateApplicableForInterest = installmentFromDate;
			Integer periodNumber = 1;
			outstandingLoanBalance = loan.getPrincpal();
			
			for(LoanRescheduleModelRepaymentPeriod period : periods) {
				
				if(period.periodDueDate().isBefore(loanRescheduleRequest.getRescheduleFromDate())) {
					
					totalPrincipalBeforeReschedulePeriod = totalPrincipalBeforeReschedulePeriod.plus(period.principalDue());
					actualTotalCumulativeInterest = actualTotalCumulativeInterest.plus(period.interestDue());
					rescheduleNumberOfRepayments--;
					outstandingLoanBalance = outstandingLoanBalance.minus(period.principalDue());
					outstandingBalance = outstandingBalance.minus(period.principalDue());
				}
			}
			
			while(graceOnPrincipal > 0 || graceOnInterest > 0) {
				
				LoanRescheduleModelRepaymentPeriod period = LoanRescheduleModelRepaymentPeriod.instance(0, 0, new LocalDate(), 
						new LocalDate(), Money.zero(currency), Money.zero(currency), Money.zero(currency), Money.zero(currency), 
						Money.zero(currency), Money.zero(currency), true);
				
				periods.add(period);
				
				if(graceOnPrincipal > 0) {
					graceOnPrincipal--;
				}
				
				if(graceOnInterest > 0) {
					graceOnInterest--;
				}
				
				rescheduleNumberOfRepayments++;
				numberOfRepayments++;
			}
			
			while(extraTerms > 0) {
				
				LoanRescheduleModelRepaymentPeriod period = LoanRescheduleModelRepaymentPeriod.instance(0, 0, new LocalDate(), 
						new LocalDate(), Money.zero(currency), Money.zero(currency), Money.zero(currency), Money.zero(currency), 
						Money.zero(currency), Money.zero(currency), true);
				
				periods.add(period);
				
				extraTerms--;
				rescheduleNumberOfRepayments++;
				numberOfRepayments++;
			}
			
			// get the loan application terms from the Loan object
			final LoanApplicationTerms loanApplicationTerms = loan.getLoanApplicationTerms(applicationCurrency);
			
			// update the number of repayments
			loanApplicationTerms.updateNumberOfRepayments(numberOfRepayments);
			
			LocalDate loanEndDate = this.scheduledDateGenerator.getLastRepaymentDate(loanApplicationTerms, isHolidayEnabled, 
					holidays, workingDays);
	        loanApplicationTerms.updateLoanEndDate(loanEndDate);
			
			if(newInterestRate != null) {
				loanApplicationTerms.updateAnnualNominalInterestRate(newInterestRate);
				loanApplicationTerms.updateInterestRatePerPeriod(newInterestRate);
			}
			
			graceOnPrincipal = defaultToZeroIfNull(loanRescheduleRequest.getGraceOnPrincipal());
			graceOnInterest = defaultToZeroIfNull(loanRescheduleRequest.getGraceOnInterest());
			
			loanApplicationTerms.updateInterestPaymentGrace(graceOnInterest);
			loanApplicationTerms.updatePrincipalGrace(graceOnPrincipal); 
			
			loanApplicationTerms.setPrincipal(totalPrincipalOutstanding);
            loanApplicationTerms.updateNumberOfRepayments(rescheduleNumberOfRepayments);
            loanApplicationTerms.updateLoanTermFrequency(rescheduleNumberOfRepayments);
            loanApplicationTerms.updateInterestChargedFromDate(periodStartDateApplicableForInterest);
            
            Money totalInterestChargedForFullLoanTerm = loanApplicationTerms.calculateTotalInterestCharged(
	                this.paymentPeriodsInOneYearCalculator, mathContext);
            
            if(!recalculateInterest && newInterestRate == null) {
            	totalInterestChargedForFullLoanTerm = Money.of(currency, loanSummary.getTotalInterestCharged());
	            totalInterestChargedForFullLoanTerm = totalInterestChargedForFullLoanTerm.minus(actualTotalCumulativeInterest);
	            
	            loanApplicationTerms.updateTotalInterestDue(totalInterestChargedForFullLoanTerm);
            }
            
            for(LoanRescheduleModelRepaymentPeriod period : periods) {
				
				if(period.periodDueDate().isEqual(loanRescheduleRequest.getRescheduleFromDate()) || 
						period.periodDueDate().isAfter(loanRescheduleRequest.getRescheduleFromDate()) ||
						period.isNew()) {
					
					installmentDueDate = this.scheduledDateGenerator.generateNextRepaymentDate(installmentDueDate, loanApplicationTerms, 
							false);
					
					if(adjustedDueDate != null && periodNumber == 1) {
						installmentDueDate = adjustedDueDate;
					}
					
					adjustedInstallmentDueDate = this.scheduledDateGenerator.adjustRepaymentDate(installmentDueDate, loanApplicationTerms, 
							isHolidayEnabled, holidays, workingDays);
					
					final int daysInInstallment = Days.daysBetween(installmentFromDate, adjustedInstallmentDueDate).getDays();
					
					period.updatePeriodNumber(installmentNumber);
		            period.updatePeriodFromDate(installmentFromDate);
		            period.updatePeriodDueDate(adjustedInstallmentDueDate);
					
					double interestCalculationGraceOnRepaymentPeriodFraction = this.paymentPeriodsInOneYearCalculator
		                    .calculatePortionOfRepaymentPeriodInterestChargingGrace(periodStartDateApplicableForInterest, adjustedInstallmentDueDate,
		                    		periodStartDateApplicableForInterest, loanApplicationTerms.getLoanTermPeriodFrequencyType(),
		                            loanApplicationTerms.getRepaymentEvery());
					
					// ========================= Calculate the interest due ========================================
					
					// change the principal to => Principal Disbursed - Total Principal Paid
					// interest calculation is always based on the total principal outstanding
					loanApplicationTerms.setPrincipal(totalPrincipalOutstanding);
		            
					// determine the interest & principal for the period
		            PrincipalInterest principalInterestForThisPeriod = calculatePrincipalInterestComponentsForPeriod(
		                    this.paymentPeriodsInOneYearCalculator, interestCalculationGraceOnRepaymentPeriodFraction, totalCumulativePrincipal,
		                    totalCumulativeInterest, totalInterestChargedForFullLoanTerm, totalOutstandingInterestPaymentDueToGrace,
		                    daysInInstallment, outstandingBalance, loanApplicationTerms, periodNumber, mathContext);
		            
		            // update the interest due for the period
		            period.updateInterestDue(principalInterestForThisPeriod.interest());
		            
		            // =============================================================================================
		            
		            // ========================== Calculate the principal due ======================================
		            
		            // change the principal to => Principal Disbursed - Total cumulative Principal Amount before the reschedule installment
		            loanApplicationTerms.setPrincipal(principal.minus(totalPrincipalBeforeReschedulePeriod));
		            
		            principalInterestForThisPeriod = calculatePrincipalInterestComponentsForPeriod(
		                    this.paymentPeriodsInOneYearCalculator, interestCalculationGraceOnRepaymentPeriodFraction, totalCumulativePrincipal,
		                    totalCumulativeInterest, totalInterestChargedForFullLoanTerm, totalOutstandingInterestPaymentDueToGrace,
		                    daysInInstallment, outstandingBalance, loanApplicationTerms, periodNumber, mathContext);
		            
		            period.updatePrincipalDue(principalInterestForThisPeriod.principal());
		            
		            // ==============================================================================================
		            
		            outstandingLoanBalance = outstandingLoanBalance.minus(period.principalDue());
		            period.updateOutstandingLoanBalance(outstandingLoanBalance);
		            
		            Money principalDue = Money.of(currency, period.principalDue());
		            Money interestDue = Money.of(currency, period.interestDue());
		            Money feeChargesDue = Money.of(currency, period.feeChargesDue());
		            Money penaltyChargesDue = Money.of(currency, period.penaltyChargesDue());
		            
		            Money totalDue = principalDue
		            		.plus(interestDue)
		            		.plus(feeChargesDue)
		            		.plus(penaltyChargesDue);
		            
		            period.updateTotalDue(totalDue);
		            
		            // update cumulative fields for principal & interest
		            totalCumulativePrincipal = totalCumulativePrincipal.plus(period.principalDue());
		            totalCumulativeInterest = totalCumulativeInterest.plus(period.interestDue());
		            actualTotalCumulativeInterest = actualTotalCumulativeInterest.plus(period.interestDue());
		            totalOutstandingInterestPaymentDueToGrace = principalInterestForThisPeriod.interestPaymentDueToGrace();
					
					installmentFromDate = adjustedInstallmentDueDate;
					installmentNumber++;
					periodNumber++;
					loanTermInDays += daysInInstallment;
					
					outstandingBalance = outstandingBalance.minus(period.principalDue());
				}
			}
		}
		
		final Money totalRepaymentExpected = principal // get the loan Principal amount
                .plus(actualTotalCumulativeInterest) // add the actual total cumulative interest
                .plus(loanSummary.getTotalFeeChargesCharged()) // add the total fees charged
                .plus(loanSummary.getTotalPenaltyChargesCharged()); // finally add the total penalty charged
		
		return LoanRescheduleModel.instance(periods, loanRepaymentScheduleHistoryList, applicationCurrency, loanTermInDays, 
				loan.getPrincpal(), loan.getPrincpal().getAmount(), loanSummary.getTotalPrincipalRepaid(), actualTotalCumulativeInterest.getAmount(), 
				loanSummary.getTotalFeeChargesCharged(), loanSummary.getTotalPenaltyChargesCharged(), totalRepaymentExpected.getAmount(), 
				loanSummary.getTotalOutstanding());
	}
    
    private Collection<LoanRepaymentScheduleHistory> createLoanScheduleArchive(final LoanRescheduleRequest loanRescheduleRequest) {
    	final Loan loan = loanRescheduleRequest.getLoan();
    	final List<LoanRepaymentScheduleInstallment> repaymentScheduleInstallments = loan.getRepaymentScheduleInstallments();
    	final LoanProductMinimumRepaymentScheduleRelatedDetail loanProductRelatedDetail = loan.getLoanRepaymentScheduleDetail();
		final MonetaryCurrency currency = loanProductRelatedDetail.getCurrency();
		List<LoanRepaymentScheduleHistory> loanRepaymentScheduleHistoryList = new ArrayList<LoanRepaymentScheduleHistory>();
    	
    	for(LoanRepaymentScheduleInstallment repaymentScheduleInstallment : repaymentScheduleInstallments) {
    		final Integer installmentNumber = repaymentScheduleInstallment.getInstallmentNumber();
    		Date fromDate = null;
    		Date dueDate = null;
    		
    		if(repaymentScheduleInstallment.getFromDate() != null) {
    			fromDate = repaymentScheduleInstallment.getFromDate().toDate();
    		}
    		
    		if(repaymentScheduleInstallment.getDueDate() != null) {
    			dueDate = repaymentScheduleInstallment.getDueDate().toDate();
    		}
    		
    		final BigDecimal principal = repaymentScheduleInstallment.getPrincipal(currency).getAmount();
    		final BigDecimal principalCompleted = repaymentScheduleInstallment.getPrincipalCompleted(currency).getAmount();
    		final BigDecimal principalWrittenOff = repaymentScheduleInstallment.getPrincipalWrittenOff(currency).getAmount();
    		final BigDecimal interestCharged = repaymentScheduleInstallment.getInterestCharged(currency).getAmount();
    		final BigDecimal interestPaid = repaymentScheduleInstallment.getInterestPaid(currency).getAmount();
    		final BigDecimal interestWaived = repaymentScheduleInstallment.getInterestWaived(currency).getAmount();
    		final BigDecimal interestWrittenOff = repaymentScheduleInstallment.getInterestWrittenOff(currency).getAmount();
    		final BigDecimal feeChargesCharged = repaymentScheduleInstallment.getFeeChargesCharged(currency).getAmount();
    		final BigDecimal feeChargesPaid = repaymentScheduleInstallment.getFeeChargesPaid(currency).getAmount();
    		final BigDecimal feeChargesWrittenOff = repaymentScheduleInstallment.getFeeChargesWrittenOff(currency).getAmount();
    		final BigDecimal feeChargesWaived = repaymentScheduleInstallment.getFeeChargesWaived(currency).getAmount();
    		final BigDecimal penaltyCharges = repaymentScheduleInstallment.getPenaltyChargesCharged(currency).getAmount();
    		final BigDecimal penaltyChargesPaid = repaymentScheduleInstallment.getPenaltyChargesPaid(currency).getAmount();
    		final BigDecimal penaltyChargesWrittenOff = repaymentScheduleInstallment.getPenaltyChargesWrittenOff(currency).getAmount();
    		final BigDecimal penaltyChargesWaived = repaymentScheduleInstallment.getPenaltyChargesWaived(currency).getAmount();
    		final BigDecimal totalPaidInAdvance = repaymentScheduleInstallment.getTotalPaidInAdvance();
    		final BigDecimal totalPaidLate = repaymentScheduleInstallment.getTotalPaidLate();
    		final boolean obligationsMet = repaymentScheduleInstallment.isObligationsMet();
    		Date obligationsMetOnDate = null;
    		
    		if(repaymentScheduleInstallment.getObligationsMetOnDate() != null) {
    			obligationsMetOnDate = repaymentScheduleInstallment.getObligationsMetOnDate().toDate();
    		}
    		
    		Date createdOnDate = null;
    		
    		if(repaymentScheduleInstallment.getCreatedDate() != null) {
    			createdOnDate = repaymentScheduleInstallment.getCreatedDate().toDate();
    		}
    		
    		final AppUser createdByUser = repaymentScheduleInstallment.getCreatedBy();
    		final AppUser lastModifiedByUser = repaymentScheduleInstallment.getLastModifiedBy();
    		
    		Date lastModifiedOnDate = null;
    		
    		if(repaymentScheduleInstallment.getLastModifiedDate() != null) {
    			lastModifiedOnDate = repaymentScheduleInstallment.getLastModifiedDate().toDate();
    		}
    		
    		LoanRepaymentScheduleHistory loanRepaymentScheduleHistory = LoanRepaymentScheduleHistory.instance(loan, 
    				loanRescheduleRequest, installmentNumber, fromDate, dueDate, principal, principalCompleted, 
    				principalWrittenOff, interestCharged, interestPaid, interestWaived, interestWrittenOff, 
    				feeChargesCharged, feeChargesPaid, feeChargesWrittenOff, feeChargesWaived, penaltyCharges, 
    				penaltyChargesPaid, penaltyChargesWrittenOff, penaltyChargesWaived, totalPaidInAdvance, totalPaidLate, 
    				obligationsMet, obligationsMetOnDate, createdOnDate, createdByUser, lastModifiedByUser, lastModifiedOnDate);
    		
    		loanRepaymentScheduleHistoryList.add(loanRepaymentScheduleHistory);
    	}
    	
    	return loanRepaymentScheduleHistoryList;
    }

    private BigDecimal calculateInterestForSpecificDays(final MathContext mc, final LoanApplicationTerms loanApplicationTerms,
            int periodNumber, Money totalOutstandingInterestPaymentDueToGrace, int daysInPeriodApplicableForInterest,
            double interestCalculationGraceOnRepaymentPeriodFraction, Money amount, LocalDate startDate, LocalDate endDate) {
        PrincipalInterest principalInterest = loanApplicationTerms.calculateTotalInterestForPeriod(this.paymentPeriodsInOneYearCalculator,
                interestCalculationGraceOnRepaymentPeriodFraction, periodNumber, mc, totalOutstandingInterestPaymentDueToGrace.zero(),
                daysInPeriodApplicableForInterest, amount);

        int days = Days.daysBetween(startDate, endDate).getDays();
        double interest = calculateInterestForDays(daysInPeriodApplicableForInterest, principalInterest.interest(), days);

        return BigDecimal.valueOf(interest);
    }

    private double calculateInterestForDays(int daysInPeriodApplicableForInterest, Money interest, int days) {
        return ((interest.getAmount().doubleValue()) / daysInPeriodApplicableForInterest) * days;
    }

    public abstract PrincipalInterest calculatePrincipalInterestComponentsForPeriod(PaymentPeriodsInOneYearCalculator calculator,
            double interestCalculationGraceOnRepaymentPeriodFraction, Money totalCumulativePrincipal, Money totalCumulativeInterest,
            Money totalInterestDueForLoan, Money cumulatingInterestPaymentDueToGrace, int daysInPeriodApplicableForInterest,
            Money outstandingBalance, LoanApplicationTerms loanApplicationTerms, int periodNumber, MathContext mc);

    protected final boolean isLastRepaymentPeriod(final int numberOfRepayments, final int periodNumber) {
        return periodNumber == numberOfRepayments;
    }

    private BigDecimal deriveTotalChargesDueAtTimeOfDisbursement(final Set<LoanCharge> loanCharges) {
        BigDecimal chargesDueAtTimeOfDisbursement = BigDecimal.ZERO;
        for (final LoanCharge loanCharge : loanCharges) {
            if (loanCharge.isDueAtDisbursement()) {
                chargesDueAtTimeOfDisbursement = chargesDueAtTimeOfDisbursement.add(loanCharge.amount());
            }
        }
        return chargesDueAtTimeOfDisbursement;
    }

    private BigDecimal disbursementForPeriod(final LoanApplicationTerms loanApplicationTerms, LocalDate startDate, LocalDate endDate,
            final Collection<LoanScheduleModelPeriod> periods, final BigDecimal chargesDueAtTimeOfDisbursement) {
        BigDecimal principal = BigDecimal.ZERO;
        MonetaryCurrency currency = loanApplicationTerms.getPrincipal().getCurrency();
        for (DisbursementData disbursementData : loanApplicationTerms.getDisbursementDatas()) {
            if (disbursementData.isDueForDisbursement(startDate, endDate)) {
                final LoanScheduleModelDisbursementPeriod disbursementPeriod = LoanScheduleModelDisbursementPeriod.disbursement(
                        disbursementData.disbursementDate(), Money.of(currency, disbursementData.amount()), chargesDueAtTimeOfDisbursement);
                periods.add(disbursementPeriod);
                principal = principal.add(disbursementData.amount());
            }
        }
        return principal;
    }

    private BigDecimal getDisbursementAmount(final LoanApplicationTerms loanApplicationTerms, LocalDate disbursementDate,
            final Collection<LoanScheduleModelPeriod> periods, final BigDecimal chargesDueAtTimeOfDisbursement) {
        BigDecimal principal = BigDecimal.ZERO;
        MonetaryCurrency currency = loanApplicationTerms.getPrincipal().getCurrency();
        for (DisbursementData disbursementData : loanApplicationTerms.getDisbursementDatas()) {
            if (disbursementData.disbursementDate().equals(disbursementDate)) {
                final LoanScheduleModelDisbursementPeriod disbursementPeriod = LoanScheduleModelDisbursementPeriod.disbursement(
                        disbursementData.disbursementDate(), Money.of(currency, disbursementData.amount()), chargesDueAtTimeOfDisbursement);
                periods.add(disbursementPeriod);
                principal = principal.add(disbursementData.amount());
            }
        }
        return principal;
    }

    private Collection<LoanScheduleModelPeriod> createNewLoanScheduleListWithDisbursementDetails(final int numberOfRepayments,
            final LoanApplicationTerms loanApplicationTerms, final BigDecimal chargesDueAtTimeOfDisbursement) {

        Collection<LoanScheduleModelPeriod> periods = null;
        if (loanApplicationTerms.isMultiDisburseLoan()) {
            periods = new ArrayList<>(numberOfRepayments + loanApplicationTerms.getDisbursementDatas().size());
        } else {
            periods = new ArrayList<>(numberOfRepayments + 1);
            final LoanScheduleModelDisbursementPeriod disbursementPeriod = LoanScheduleModelDisbursementPeriod.disbursement(
                    loanApplicationTerms, chargesDueAtTimeOfDisbursement);
            periods.add(disbursementPeriod);
        }

        return periods;
    }

    private Money cumulativeFeeChargesDueWithin(final LocalDate periodStart, final LocalDate periodEnd, final Set<LoanCharge> loanCharges,
            final MonetaryCurrency monetaryCurrency, final PrincipalInterest principalInterestForThisPeriod,
            final Money principalDisbursed, final Money totalInterestChargedForFullLoanTerm, int numberOfRepayments,
            boolean isInstallmentChargeApplicable) {

        Money cumulative = Money.zero(monetaryCurrency);

        for (final LoanCharge loanCharge : loanCharges) {
            if (loanCharge.isFeeCharge()) {
                if (loanCharge.isInstalmentFee() && isInstallmentChargeApplicable) {
                    if (loanCharge.getChargeCalculation().isPercentageBased()) {
                        BigDecimal amount = BigDecimal.ZERO;
                        if (loanCharge.getChargeCalculation().isPercentageOfAmountAndInterest()) {
                            amount = amount.add(principalInterestForThisPeriod.principal().getAmount()).add(
                                    principalInterestForThisPeriod.interest().getAmount());
                        } else if (loanCharge.getChargeCalculation().isPercentageOfInterest()) {
                            amount = amount.add(principalInterestForThisPeriod.interest().getAmount());
                        } else {
                            amount = amount.add(principalInterestForThisPeriod.principal().getAmount());
                        }
                        BigDecimal loanChargeAmt = amount.multiply(loanCharge.getPercentage()).divide(BigDecimal.valueOf(100));
                        cumulative = cumulative.plus(loanChargeAmt);
                    } else {
                        cumulative = cumulative.plus(loanCharge.amount().divide(BigDecimal.valueOf(numberOfRepayments)));
                    }
                } else if (loanCharge.isOverdueInstallmentCharge()
                        && loanCharge.isDueForCollectionFromAndUpToAndIncluding(periodStart, periodEnd)
                        && loanCharge.getChargeCalculation().isPercentageBased()) {
                    cumulative = cumulative.plus(loanCharge.chargeAmount());
                } else if (loanCharge.isDueForCollectionFromAndUpToAndIncluding(periodStart, periodEnd)
                        && loanCharge.getChargeCalculation().isPercentageBased()) {
                    BigDecimal amount = BigDecimal.ZERO;
                    if (loanCharge.getChargeCalculation().isPercentageOfAmountAndInterest()) {
                        amount = amount.add(principalDisbursed.getAmount()).add(totalInterestChargedForFullLoanTerm.getAmount());
                    } else if (loanCharge.getChargeCalculation().isPercentageOfInterest()) {
                        amount = amount.add(totalInterestChargedForFullLoanTerm.getAmount());
                    } else {
                        amount = amount.add(principalDisbursed.getAmount());
                    }
                    BigDecimal loanChargeAmt = amount.multiply(loanCharge.getPercentage()).divide(BigDecimal.valueOf(100));
                    cumulative = cumulative.plus(loanChargeAmt);
                } else if (loanCharge.isDueForCollectionFromAndUpToAndIncluding(periodStart, periodEnd)) {
                    cumulative = cumulative.plus(loanCharge.amount());
                }
            }
        }

        return cumulative;
    }

    private Money cumulativePenaltyChargesDueWithin(final LocalDate periodStart, final LocalDate periodEnd,
            final Set<LoanCharge> loanCharges, final MonetaryCurrency monetaryCurrency,
            final PrincipalInterest principalInterestForThisPeriod, final Money principalDisbursed,
            final Money totalInterestChargedForFullLoanTerm, int numberOfRepayments, boolean isInstallmentChargeApplicable) {

        Money cumulative = Money.zero(monetaryCurrency);

        for (final LoanCharge loanCharge : loanCharges) {
            if (loanCharge.isPenaltyCharge()) {
                if (loanCharge.isInstalmentFee() && isInstallmentChargeApplicable) {
                    if (loanCharge.getChargeCalculation().isPercentageBased()) {
                        BigDecimal amount = BigDecimal.ZERO;
                        if (loanCharge.getChargeCalculation().isPercentageOfAmountAndInterest()) {
                            amount = amount.add(principalInterestForThisPeriod.principal().getAmount()).add(
                                    principalInterestForThisPeriod.interest().getAmount());
                        } else if (loanCharge.getChargeCalculation().isPercentageOfInterest()) {
                            amount = amount.add(principalInterestForThisPeriod.interest().getAmount());
                        } else {
                            amount = amount.add(principalInterestForThisPeriod.principal().getAmount());
                        }
                        BigDecimal loanChargeAmt = amount.multiply(loanCharge.getPercentage()).divide(BigDecimal.valueOf(100));
                        cumulative = cumulative.plus(loanChargeAmt);
                    } else {
                        cumulative = cumulative.plus(loanCharge.amount().divide(BigDecimal.valueOf(numberOfRepayments)));
                    }
                } else if (loanCharge.isOverdueInstallmentCharge()
                        && loanCharge.isDueForCollectionFromAndUpToAndIncluding(periodStart, periodEnd)
                        && loanCharge.getChargeCalculation().isPercentageBased()) {
                    cumulative = cumulative.plus(loanCharge.chargeAmount());
                } else if (loanCharge.isDueForCollectionFromAndUpToAndIncluding(periodStart, periodEnd)
                        && loanCharge.getChargeCalculation().isPercentageBased()) {
                    BigDecimal amount = BigDecimal.ZERO;
                    if (loanCharge.getChargeCalculation().isPercentageOfAmountAndInterest()) {
                        amount = amount.add(principalDisbursed.getAmount()).add(totalInterestChargedForFullLoanTerm.getAmount());
                    } else if (loanCharge.getChargeCalculation().isPercentageOfInterest()) {
                        amount = amount.add(totalInterestChargedForFullLoanTerm.getAmount());
                    } else {
                        amount = amount.add(principalDisbursed.getAmount());
                    }
                    BigDecimal loanChargeAmt = amount.multiply(loanCharge.getPercentage()).divide(BigDecimal.valueOf(100));
                    cumulative = cumulative.plus(loanChargeAmt);
                } else if (loanCharge.isDueForCollectionFromAndUpToAndIncluding(periodStart, periodEnd)) {
                    cumulative = cumulative.plus(loanCharge.amount());
                }
            }
        }

        return cumulative;
    }

    /**
     * Method calls schedule regeneration by passing transactions one after
     * another(this is done mainly to handle the scenario where interest or fee
     * of over due installment should be collected before collecting principal )
     */
    @Override
    public LoanScheduleModel rescheduleNextInstallments(final MathContext mc, final ApplicationCurrency applicationCurrency,
            final LoanApplicationTerms loanApplicationTerms, final Set<LoanCharge> loanCharges, final boolean isHolidayEnabled,
            final List<Holiday> holidays, final WorkingDays workingDays, final List<LoanTransaction> transactions,
            final LoanRepaymentScheduleTransactionProcessor loanRepaymentScheduleTransactionProcessor,
            final List<LoanRepaymentScheduleInstallment> previousSchedule, LocalDate recalculateFrom) {

        LoanScheduleModel loanScheduleModel = null;
        List<LoanRepaymentScheduleInstallment> installments = previousSchedule;
        List<LoanRepaymentScheduleInstallment> removeInstallments = new ArrayList<>();
        MonetaryCurrency currency = loanApplicationTerms.getPrincipal().getCurrency();
        LoanTransaction preCloseTransaction = getPreclosureTransaction(transactions, installments, currency, loanApplicationTerms);
        for (LoanRepaymentScheduleInstallment installment : installments) {
            if (installment.isRecalculatedInterestComponent() && recalculateFrom != null
                    && !recalculateFrom.isAfter(installment.getDueDate())) {
                removeInstallments.add(installment);
            }
        }
        installments.removeAll(removeInstallments);
        if (previousSchedule == null) {
            loanScheduleModel = generate(mc, applicationCurrency, loanApplicationTerms, loanCharges, isHolidayEnabled, holidays,
                    workingDays);
            installments = retrieveRepaymentSchedule(loanScheduleModel);
        }

        if (recalculateFrom == null) {
            recalculateFrom = loanApplicationTerms.getExpectedDisbursementDate();
        }

        final List<RecalculationDetail> recalculationDetails = new ArrayList<>();
        final Map<LocalDate, RecalculationDetail> retainRecalculationDetails = new HashMap<>();
        LocalDate processTransactionsForInterestCompound = loanApplicationTerms.getExpectedDisbursementDate();
        while (recalculateFrom.isAfter(processTransactionsForInterestCompound) && !transactions.isEmpty()
                && loanRepaymentScheduleTransactionProcessor.isInterestFirstRepaymentScheduleTransactionProcessor()) {

            int installmentNumber = findLastProcessedInstallmentNumber(installments, processTransactionsForInterestCompound);
            List<LoanTransaction> processTransactions = processTransactions(transactions, processTransactionsForInterestCompound);
            if (processTransactions.size() > 0) {
                loanScheduleModel = createInterestOnlyRecalculationDetails(mc, applicationCurrency, loanApplicationTerms, loanCharges,
                        isHolidayEnabled, holidays, workingDays, loanRepaymentScheduleTransactionProcessor, previousSchedule,
                        loanScheduleModel, installments, currency, recalculationDetails, retainRecalculationDetails, false,
                        installmentNumber, processTransactions, preCloseTransaction);
            }
            recalculationDetails.retainAll(retainRecalculationDetails.values());
            processTransactionsForInterestCompound = getNextRecalculateFromDate(transactions, processTransactionsForInterestCompound);
        }

        while (!recalculateFrom.isAfter(LocalDate.now())) {

            int installmentNumber = findLastProcessedInstallmentNumber(installments, recalculateFrom);
            List<LoanTransaction> processTransactions = processTransactions(transactions, recalculateFrom);
            if (loanRepaymentScheduleTransactionProcessor.isInterestFirstRepaymentScheduleTransactionProcessor()
                    && processTransactions.size() > 0) {
                loanScheduleModel = createInterestOnlyRecalculationDetails(mc, applicationCurrency, loanApplicationTerms, loanCharges,
                        isHolidayEnabled, holidays, workingDays, loanRepaymentScheduleTransactionProcessor, previousSchedule,
                        loanScheduleModel, installments, currency, recalculationDetails, retainRecalculationDetails, true,
                        installmentNumber, processTransactions, preCloseTransaction);
                if (loanScheduleModel != null) {
                    installments = retrieveRepaymentSchedule(loanScheduleModel);
                }
            }
            recalculationDetails.retainAll(retainRecalculationDetails.values());
            loanScheduleModel = recalculateInstallment(mc, applicationCurrency, loanApplicationTerms, loanCharges, isHolidayEnabled,
                    holidays, workingDays, loanRepaymentScheduleTransactionProcessor, previousSchedule, loanScheduleModel, installments,
                    processTransactions, installmentNumber, recalculationDetails, preCloseTransaction);
            if (loanScheduleModel != null) {
                installments = retrieveRepaymentSchedule(loanScheduleModel);
            }
            recalculateFrom = getNextRecalculateFromDate(transactions, recalculateFrom);
        }
        return loanScheduleModel;

    }

    /**
     * Method identifies interest only repayment periods
     */
    private LoanScheduleModel createInterestOnlyRecalculationDetails(final MathContext mc, final ApplicationCurrency applicationCurrency,
            final LoanApplicationTerms loanApplicationTerms, final Set<LoanCharge> loanCharges, final boolean isHolidayEnabled,
            final List<Holiday> holidays, final WorkingDays workingDays,
            final LoanRepaymentScheduleTransactionProcessor loanRepaymentScheduleTransactionProcessor,
            final List<LoanRepaymentScheduleInstallment> previousSchedule, LoanScheduleModel loanScheduleModel,
            List<LoanRepaymentScheduleInstallment> installments, MonetaryCurrency currency,
            final List<RecalculationDetail> recalculationDetails, final Map<LocalDate, RecalculationDetail> retainRecalculationDetails,
            boolean recalculate, int installmentNumber, List<LoanTransaction> processTransactions, LoanTransaction preCloseTransaction) {
        LoanTransaction loanTransaction = processTransactions.remove(processTransactions.size() - 1);
        if (loanTransaction == preCloseTransaction) {
            processTransactions.add(loanTransaction);
            return loanScheduleModel;
        }
        LocalDate processTransactionsForInterestCompound = getNextRestScheduleDate(loanTransaction.getTransactionDate().minusDays(1),
                loanApplicationTerms, isHolidayEnabled, holidays, workingDays);
        List<LoanRepaymentScheduleInstallment> scheduleInstallments = getInstallmentsForInterestCompound(installments,
                processTransactionsForInterestCompound, retainRecalculationDetails.keySet());

        if (!scheduleInstallments.isEmpty()) {
            for (LoanRepaymentScheduleInstallment installment : installments) {
                installment.resetDerivedComponents();
                installment.updateDerivedFields(currency, loanApplicationTerms.getExpectedDisbursementDate());
            }
            loanRepaymentScheduleTransactionProcessor.applyTransaction(processTransactions, currency, installments);

            Money principalUnprocessed = Money.zero(currency);
            Money interestUnprocessed = Money.zero(currency);
            Money feeUnprocessed = Money.zero(currency);
            boolean isBeforeFirstInstallment = false;
            if (scheduleInstallments.size() == 1) {
                LoanRepaymentScheduleInstallment installment = scheduleInstallments.get(0);
                if (installment.getInstallmentNumber() == 1
                        && (installment.getDueDate().isAfter(loanTransaction.getTransactionDate()) || (installment
                                .isRecalculatedInterestComponent() && installment.getDueDate()
                                .isEqual(loanTransaction.getTransactionDate())))) {
                    isBeforeFirstInstallment = true;
                }
            }
            if (!isBeforeFirstInstallment) {
                for (LoanRepaymentScheduleInstallment installment : scheduleInstallments) {
                    principalUnprocessed = principalUnprocessed.plus(installment.getPrincipalOutstanding(currency));
                    interestUnprocessed = interestUnprocessed.plus(installment.getInterestOutstanding(currency));
                    feeUnprocessed = feeUnprocessed.plus(installment.getFeeChargesOutstanding(currency));
                    feeUnprocessed = feeUnprocessed.plus(installment.getPenaltyChargesOutstanding(currency));
                }
            }
            if (interestUnprocessed.isLessThan(loanTransaction.getAmount(currency))) {
                LoanRepaymentScheduleInstallment lastProcessedInstallment = scheduleInstallments.get(scheduleInstallments.size() - 1);
                LocalDate startDate = lastProcessedInstallment.getDueDate();
                if (isBeforeFirstInstallment) {
                    startDate = loanApplicationTerms.getExpectedDisbursementDate();
                }
                RecalculationDetail recalculationDetail = new RecalculationDetail(false, startDate, processTransactionsForInterestCompound,
                        null, true);
                retainRecalculationDetails.put(processTransactionsForInterestCompound, recalculationDetail);
                recalculationDetails.add(recalculationDetail);
                if (recalculate) {
                    recalculationDetails.retainAll(retainRecalculationDetails.values());
                    loanScheduleModel = recalculateInstallment(mc, applicationCurrency, loanApplicationTerms, loanCharges,
                            isHolidayEnabled, holidays, workingDays, loanRepaymentScheduleTransactionProcessor, previousSchedule,
                            loanScheduleModel, installments, processTransactions, installmentNumber, recalculationDetails,
                            preCloseTransaction);

                }
            }
        }
        processTransactions.add(loanTransaction);
        return loanScheduleModel;
    }

    private LocalDate getNextRecalculateFromDate(List<LoanTransaction> processTransactions, LocalDate preCalculationDate) {
        LocalDate recalculateFrom = null;
        for (LoanTransaction loanTransaction : processTransactions) {
            if (preCalculationDate.isBefore(loanTransaction.getTransactionDate())
                    && (recalculateFrom == null || recalculateFrom.isAfter(loanTransaction.getTransactionDate()))) {
                recalculateFrom = loanTransaction.getTransactionDate();
            }
        }
        if (recalculateFrom == null) {
            recalculateFrom = LocalDate.now().plusDays(1);
        }
        return recalculateFrom;
    }

    /**
     * Method calls regenerate schedule for a particular set of transactions
     * till all the installments are processed to identify early or late
     * payments and will be used in regeneration of schedule
     * 
     * @param preCloseTransaction
     *            TODO
     */
    private LoanScheduleModel recalculateInstallment(final MathContext mc, final ApplicationCurrency applicationCurrency,
            final LoanApplicationTerms loanApplicationTerms, final Set<LoanCharge> loanCharges, final boolean isHolidayEnabled,
            final List<Holiday> holidays, final WorkingDays workingDays,
            final LoanRepaymentScheduleTransactionProcessor loanRepaymentScheduleTransactionProcessor,
            final List<LoanRepaymentScheduleInstallment> previousSchedule, LoanScheduleModel loanScheduleModel,
            List<LoanRepaymentScheduleInstallment> installments, List<LoanTransaction> processTransactions, final int skipRecalculation,
            final List<RecalculationDetail> recalculationDetails, LoanTransaction preCloseTransaction) {
        int processInstallmentsFrom = 0;
        Integer lastInstallmentNumber = previousSchedule.size();
        while (processInstallmentsFrom < lastInstallmentNumber) {
            RecalculatedSchedule recalculatedSchedule = recalculateInterest(mc, applicationCurrency, loanApplicationTerms, loanCharges,
                    isHolidayEnabled, holidays, workingDays, processTransactions, loanRepaymentScheduleTransactionProcessor,
                    processInstallmentsFrom, installments, recalculationDetails, skipRecalculation, preCloseTransaction);
            processInstallmentsFrom = recalculatedSchedule.getInstallmentNumber();
            if (recalculatedSchedule.getLoanScheduleModel() != null) {
                loanScheduleModel = recalculatedSchedule.getLoanScheduleModel();
                installments = retrieveRepaymentSchedule(loanScheduleModel);
                lastInstallmentNumber = installments.size() - 1;
            }
        }
        return loanScheduleModel;
    }

    /**
     * Method identifies late or early payments for schedule recalculation.
     * 
     * @param preCloseTransaction
     *            TODO
     */
    private RecalculatedSchedule recalculateInterest(final MathContext mc, final ApplicationCurrency applicationCurrency,
            final LoanApplicationTerms loanApplicationTerms, final Set<LoanCharge> loanCharges, final boolean isHolidayEnabled,
            final List<Holiday> holidays, final WorkingDays workingDays, final List<LoanTransaction> transactions,
            final LoanRepaymentScheduleTransactionProcessor loanRepaymentScheduleTransactionProcessor, final int installmentNumber,
            final List<LoanRepaymentScheduleInstallment> installments, final List<RecalculationDetail> recalculationDetails,
            final int skipRecalculation, LoanTransaction preCloseTransaction) {
        boolean processRecalculate = false;
        int processedInstallmentNumber = installmentNumber;

        final List<RecalculationDetail> diffAmt = new ArrayList<>();
        Money unpaidPricipal = loanApplicationTerms.getPrincipal();

        MonetaryCurrency currency = loanApplicationTerms.getPrincipal().getCurrency();
        final List<LoanRepaymentScheduleInstallment> processinstallmets = new ArrayList<>();
        for (LoanRepaymentScheduleInstallment installment : installments) {
            installment.resetDerivedComponents();
            installment.updateDerivedFields(currency, loanApplicationTerms.getExpectedDisbursementDate());
        }
        for (LoanRepaymentScheduleInstallment installment : installments) {
            processinstallmets.add(installment);
            unpaidPricipal = unpaidPricipal.minus(installment.getPrincipal(currency));
            if (installment.getInstallmentNumber() <= installmentNumber) {
                continue;
            }
            processedInstallmentNumber = installment.getInstallmentNumber();
            if (installment.getInstallmentNumber() == installments.size()) {
                processRecalculate = true;
            }

            List<LoanTransaction> transactionsForInstallment = new ArrayList<>();
            Map<LocalDate, LocalDate> recalculationDates = new HashMap<>();
            LocalDate transactionsDate = getNextRestScheduleDate(installment.getDueDate().minusDays(1), loanApplicationTerms,
                    isHolidayEnabled, holidays, workingDays);
            for (LoanTransaction loanTransaction : transactions) {
                LocalDate loantransactionDate = loanTransaction.getTransactionDate();
                if (!loantransactionDate.isAfter(transactionsDate)) {
                    transactionsForInstallment.add(loanTransaction);
                    recalculationDates.put(
                            loantransactionDate,
                            getNextRestScheduleDate(loantransactionDate.minusDays(1), loanApplicationTerms, isHolidayEnabled, holidays,
                                    workingDays));
                }
            }

            if (installment.isRecalculatedInterestComponent() && installment.getPrincipal(currency).isGreaterThanZero()) {
                diffAmt.add(new RecalculationDetail(false, installment.getDueDate(), null, installment.getPrincipal(currency), false));
            }
            List<RecalculationDetail> earlypaymentDetail = loanRepaymentScheduleTransactionProcessor.handleRepaymentSchedule(
                    transactionsForInstallment, currency, processinstallmets, installment, recalculationDates, preCloseTransaction);

            // this block is to create early payment entries for schedule
            // generation
            for (RecalculationDetail recalculationDetail : earlypaymentDetail) {
                if (!recalculationDetail.getStartDate().isAfter(installment.getDueDate())
                        && recalculationDetail.getStartDate().isAfter(installment.getFromDate())) {
                    diffAmt.add(recalculationDetail);
                }
            }

            if (installment.getDueDate().isBefore(LocalDate.now())) {
                LocalDate startDate = installment.getDueDate();
                Money totalOutstanding = installment.getTotalOutstanding(currency);
                boolean reduceStartDate = false;

                // this block is to identify late payment based on the rest
                // calculation date
                while (totalOutstanding.isGreaterThanZero() && startDate.isBefore(LocalDate.now())) {
                    LocalDate recalculateFrom = getNextRestScheduleDate(startDate.minusDays(1), loanApplicationTerms, isHolidayEnabled,
                            holidays, workingDays);
                    LocalDate recalcualteTill = getNextRestScheduleDate(recalculateFrom, loanApplicationTerms, isHolidayEnabled, holidays,
                            workingDays);
                    if (reduceStartDate) {
                        startDate = startDate.minusDays(1);
                    }
                    applyRest(transactions, startDate, recalculateFrom, installment, loanRepaymentScheduleTransactionProcessor, currency,
                            loanApplicationTerms, isHolidayEnabled, holidays, workingDays, installments, preCloseTransaction);
                    totalOutstanding = installment.getTotalOutstanding(currency);
                    if (totalOutstanding.isGreaterThanZero()) {
                        Money latepaymentoutstanding = installment.getPrincipalOutstanding(currency);
                        switch (loanApplicationTerms.getInterestRecalculationCompoundingMethod()) {
                            case INTEREST:
                                latepaymentoutstanding = latepaymentoutstanding.plus(installment.getInterestOutstanding(currency));
                            break;
                            case INTEREST_AND_FEE:
                                latepaymentoutstanding = latepaymentoutstanding.plus(installment.getInterestOutstanding(currency))
                                        .plus(installment.getFeeChargesOutstanding(currency))
                                        .plus(installment.getPenaltyChargesOutstanding(currency));
                            break;
                            case FEE:
                                latepaymentoutstanding = latepaymentoutstanding.plus(installment.getFeeChargesOutstanding(currency)).plus(
                                        installment.getPenaltyChargesOutstanding(currency));
                            break;

                            default:
                            break;
                        }
                        RecalculationDetail recalculationDetail = new RecalculationDetail(true, recalculateFrom, recalcualteTill,
                                latepaymentoutstanding, false);
                        diffAmt.add(recalculationDetail);
                    }
                    startDate = recalculateFrom.plusDays(1);
                    reduceStartDate = true;
                }
            }

            if (!diffAmt.isEmpty() && skipRecalculation <= installment.getInstallmentNumber()) {
                processRecalculate = true;
            }
            break;

        }
        LoanScheduleModel model = null;
        recalculationDetails.addAll(diffAmt);
        if (processRecalculate) {
            model = generate(mc, applicationCurrency, loanApplicationTerms, loanCharges, isHolidayEnabled, holidays, workingDays,
                    recalculationDetails);
        }
        return new RecalculatedSchedule(model, processedInstallmentNumber);
    }

    private List<LoanRepaymentScheduleInstallment> retrieveRepaymentSchedule(LoanScheduleModel model) {
        final List<LoanRepaymentScheduleInstallment> installments = new ArrayList<>();
        for (final LoanScheduleModelPeriod scheduledLoanInstallment : model.getPeriods()) {
            if (scheduledLoanInstallment.isRepaymentPeriod()) {
                final LoanRepaymentScheduleInstallment installment = new LoanRepaymentScheduleInstallment(null,
                        scheduledLoanInstallment.periodNumber(), scheduledLoanInstallment.periodFromDate(),
                        scheduledLoanInstallment.periodDueDate(), scheduledLoanInstallment.principalDue(),
                        scheduledLoanInstallment.interestDue(), scheduledLoanInstallment.feeChargesDue(),
                        scheduledLoanInstallment.penaltyChargesDue(), scheduledLoanInstallment.isRecalculatedInterestComponent());
                installments.add(installment);
            }
        }
        return installments;
    }

    /**
     * Method to identify which transaction did the payment for current
     * installment
     * 
     * @param preCloseTransaction
     *            TODO
     */
    private void applyRest(List<LoanTransaction> loanTransactions, LocalDate from, LocalDate to,
            LoanRepaymentScheduleInstallment installment,
            final LoanRepaymentScheduleTransactionProcessor loanRepaymentScheduleTransactionProcessor, MonetaryCurrency currency,
            final LoanApplicationTerms loanApplicationTerms, final boolean isHolidayEnabled, final List<Holiday> holidays,
            final WorkingDays workingDays, final List<LoanRepaymentScheduleInstallment> installments, LoanTransaction preCloseTransaction) {
        List<LoanTransaction> transactions = new ArrayList<>();
        Map<LocalDate, LocalDate> recalculationDates = new HashMap<>();
        for (LoanTransaction transaction : loanTransactions) {
            LocalDate loantransactionDate = transaction.getTransactionDate();
            if (loantransactionDate.isAfter(from) && !transaction.getTransactionDate().isAfter(to)) {
                transactions.add(transaction);
                recalculationDates.put(
                        loantransactionDate,
                        getNextRestScheduleDate(loantransactionDate.minusDays(1), loanApplicationTerms, isHolidayEnabled, holidays,
                                workingDays));
            }
        }

        loanRepaymentScheduleTransactionProcessor.handleRepaymentSchedule(transactions, currency, installments, installment,
                recalculationDates, preCloseTransaction);
    }

    @SuppressWarnings("unused")
    private LocalDate getNextRestScheduleDate(LocalDate startDate, LoanApplicationTerms loanApplicationTerms,
            final boolean isHolidayEnabled, final List<Holiday> holidays, final WorkingDays workingDays) {
        CalendarInstance calendarInstance = loanApplicationTerms.getRestCalendarInstance();
        LocalDate nextScheduleDate = CalendarUtils.getNextScheduleDate(calendarInstance.getCalendar(), startDate);
        /*
         * nextScheduleDate =
         * this.scheduledDateGenerator.adjustRepaymentDate(nextScheduleDate,
         * loanApplicationTerms, isHolidayEnabled, holidays, workingDays);
         */
        return nextScheduleDate;
    }

    /**
     * Method returns the amount payable to close the loan account as of today.
     */
    @Override
    public LoanRepaymentScheduleInstallment calculatePrepaymentAmount(final List<LoanRepaymentScheduleInstallment> installments,
            MonetaryCurrency currency, final LoanApplicationTerms applicationTerms, final LocalDate onDate) {
        Money outstandingPrincipal = Money.zero(currency);
        Money feeCharges = Money.zero(currency);
        Money penaltyCharges = Money.zero(currency);
        Money totalPrincipal = Money.zero(currency);
        Money totalInterest = Money.zero(currency);
        LocalDate calculateInterestFrom = onDate;
        LocalDate dueDate = onDate;
        Money inerestForCurrentInstallment = null;
        for (final LoanRepaymentScheduleInstallment currentInstallment : installments) {
            if (currentInstallment.isNotFullyPaidOff()) {
                if (!currentInstallment.getDueDate().isAfter(onDate)) {
                    totalPrincipal = totalPrincipal.plus(currentInstallment.getPrincipalOutstanding(currency));
                    totalInterest = totalInterest.plus(currentInstallment.getInterestOutstanding(currency));
                    feeCharges = feeCharges.plus(currentInstallment.getFeeChargesOutstanding(currency));
                    penaltyCharges = penaltyCharges.plus(currentInstallment.getPenaltyChargesOutstanding(currency));
                } else {
                    outstandingPrincipal = outstandingPrincipal.plus(currentInstallment.getPrincipal(currency));
                    totalPrincipal = totalPrincipal.minus(currentInstallment.getPrincipalCompleted(currency));
                    totalInterest = totalInterest.minus(currentInstallment.getInterestPaid(currency)).minus(
                            currentInstallment.getInterestWaived(currency));
                    if (currentInstallment.getFromDate().isBefore(calculateInterestFrom)) {
                        calculateInterestFrom = currentInstallment.getFromDate();
                        dueDate = currentInstallment.getDueDate();
                        inerestForCurrentInstallment = currentInstallment.getInterestCharged(currency);
                    }

                }
            }
        }
        if (applicationTerms.getInterestChargedFromLocalDate() != null
                && calculateInterestFrom.isBefore(applicationTerms.getInterestChargedFromLocalDate())) {
            calculateInterestFrom = applicationTerms.getInterestChargedFromLocalDate();
        }
        Money interest = Money.zero(currency);
        ;
        if (calculateInterestFrom.isBefore(onDate)) {
            int daysInPeriodApplicableForInterest = Days.daysBetween(calculateInterestFrom, dueDate).getDays();
            int days = Days.daysBetween(calculateInterestFrom, onDate).getDays();
            interest = interest.plus(calculateInterestForDays(daysInPeriodApplicableForInterest, inerestForCurrentInstallment, days));
        }

        totalInterest = totalInterest.plus(interest);
        totalPrincipal = totalPrincipal.plus(outstandingPrincipal);

        return new LoanRepaymentScheduleInstallment(null, 0, onDate, onDate, totalPrincipal.getAmount(), totalInterest.getAmount(),
                feeCharges.getAmount(), penaltyCharges.getAmount(), false);
    }

    private Integer findLastProcessedInstallmentNumber(final List<LoanRepaymentScheduleInstallment> installments, LocalDate date) {
        int installmentNumber = 0;
        for (LoanRepaymentScheduleInstallment installment : installments) {
            if (!installment.getDueDate().isAfter(date) && installmentNumber < installment.getInstallmentNumber()) {
                installmentNumber = installment.getInstallmentNumber();
            }
        }

        return installmentNumber;
    }

    private List<LoanRepaymentScheduleInstallment> getInstallmentsForInterestCompound(
            final List<LoanRepaymentScheduleInstallment> installments, final LocalDate date, Collection<LocalDate> processedDates) {
        List<LoanRepaymentScheduleInstallment> repaymentScheduleInstallments = new ArrayList<>();
        for (LoanRepaymentScheduleInstallment installment : installments) {
            if (installment.getDueDate().isEqual(date)
                    && (!installment.isRecalculatedInterestComponent() || (processedDates.contains(date) && installment
                            .isRecalculatedInterestComponent()))) {
                repaymentScheduleInstallments.clear();
                break;
            } else if (installment.getDueDate().isBefore(date)) {
                repaymentScheduleInstallments.add(installment);
            } else {
                if (installment.getInstallmentNumber() == 1) {
                    repaymentScheduleInstallments.add(installment);
                }
                break;
            }
        }
        return repaymentScheduleInstallments;
    }

    private List<LoanTransaction> processTransactions(final List<LoanTransaction> transactions, final LocalDate tillDate) {
        List<LoanTransaction> toProcess = new ArrayList<>();
        for (LoanTransaction loanTransaction : transactions) {
            if (!loanTransaction.getTransactionDate().isAfter(tillDate)) {
                toProcess.add(loanTransaction);
            }
        }
        return toProcess;
    }

    private LoanTransaction getPreclosureTransaction(List<LoanTransaction> loanTransactions,
            List<LoanRepaymentScheduleInstallment> installments, MonetaryCurrency currency, final LoanApplicationTerms applicationTerms) {
        LoanTransaction precloseTransaction = null;
        for (LoanTransaction loanTransaction : loanTransactions) {
            if (precloseTransaction == null || precloseTransaction.getTransactionDate().isBefore(loanTransaction.getTransactionDate())) {
                precloseTransaction = loanTransaction;
            }
        }
        if (precloseTransaction != null) {
            LoanRepaymentScheduleInstallment loanRepaymentScheduleInstallment = calculatePrepaymentAmount(installments, currency,
                    applicationTerms, precloseTransaction.getTransactionDate());
            if (precloseTransaction.getAmount(currency).isLessThan(loanRepaymentScheduleInstallment.getTotalOutstanding(currency))) {
                precloseTransaction = null;
            }
        }

        return precloseTransaction;

    }
    
    /** 
     * set the value to zero if the provided value is null 
     * 
     * @return integer value equal/greater than 0
     **/
    private Integer defaultToZeroIfNull(Integer value) {
    	
    	return (value != null) ? value : 0;
    }
}