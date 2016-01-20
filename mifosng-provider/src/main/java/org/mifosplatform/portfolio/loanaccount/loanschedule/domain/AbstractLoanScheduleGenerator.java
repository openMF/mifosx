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
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.mifosplatform.infrastructure.core.service.DateUtils;
import org.mifosplatform.organisation.monetary.domain.ApplicationCurrency;
import org.mifosplatform.organisation.monetary.domain.MonetaryCurrency;
import org.mifosplatform.organisation.monetary.domain.Money;
import org.mifosplatform.organisation.workingdays.domain.RepaymentRescheduleType;
import org.mifosplatform.portfolio.calendar.domain.Calendar;
import org.mifosplatform.portfolio.calendar.domain.CalendarInstance;
import org.mifosplatform.portfolio.calendar.service.CalendarUtils;
import org.mifosplatform.portfolio.common.domain.PeriodFrequencyType;
import org.mifosplatform.portfolio.floatingrates.data.FloatingRateDTO;
import org.mifosplatform.portfolio.loanaccount.data.DisbursementData;
import org.mifosplatform.portfolio.loanaccount.data.HolidayDetailDTO;
import org.mifosplatform.portfolio.loanaccount.data.LoanTermVariationsData;
import org.mifosplatform.portfolio.loanaccount.domain.Loan;
import org.mifosplatform.portfolio.loanaccount.domain.LoanCharge;
import org.mifosplatform.portfolio.loanaccount.domain.LoanRepaymentScheduleInstallment;
import org.mifosplatform.portfolio.loanaccount.domain.LoanSummary;
import org.mifosplatform.portfolio.loanaccount.domain.LoanTransaction;
import org.mifosplatform.portfolio.loanaccount.domain.transactionprocessor.LoanRepaymentScheduleTransactionProcessor;
import org.mifosplatform.portfolio.loanaccount.loanschedule.data.LoanScheduleDTO;
import org.mifosplatform.portfolio.loanaccount.loanschedule.data.LoanScheduleParams;
import org.mifosplatform.portfolio.loanaccount.loanschedule.exception.MultiDisbursementEmiAmountException;
import org.mifosplatform.portfolio.loanaccount.loanschedule.exception.MultiDisbursementOutstandingAmoutException;
import org.mifosplatform.portfolio.loanaccount.loanschedule.exception.ScheduleDateException;
import org.mifosplatform.portfolio.loanaccount.rescheduleloan.domain.LoanRescheduleModel;
import org.mifosplatform.portfolio.loanaccount.rescheduleloan.domain.LoanRescheduleModelRepaymentPeriod;
import org.mifosplatform.portfolio.loanaccount.rescheduleloan.domain.LoanRescheduleRequest;
import org.mifosplatform.portfolio.loanproduct.domain.LoanProductMinimumRepaymentScheduleRelatedDetail;

public abstract class AbstractLoanScheduleGenerator implements LoanScheduleGenerator {

    private final ScheduledDateGenerator scheduledDateGenerator = new DefaultScheduledDateGenerator();
    private final PaymentPeriodsInOneYearCalculator paymentPeriodsInOneYearCalculator = new DefaultPaymentPeriodsInOneYearCalculator();

    @Override
    public LoanScheduleModel generate(final MathContext mc, final LoanApplicationTerms loanApplicationTerms,
            final Set<LoanCharge> loanCharges, final HolidayDetailDTO holidayDetailDTO) {
        final LoanScheduleParams loanScheduleRecalculationDTO = null;
        return generate(mc, loanApplicationTerms, loanCharges, holidayDetailDTO, loanScheduleRecalculationDTO);
    }

    private LoanScheduleModel generate(final MathContext mc, final LoanApplicationTerms loanApplicationTerms,
            final Set<LoanCharge> loanCharges, final HolidayDetailDTO holidayDetailDTO, final LoanScheduleParams loanScheduleParams) {

        final ApplicationCurrency applicationCurrency = loanApplicationTerms.getApplicationCurrency();
        // generate list of proposed schedule due dates
        LocalDate loanEndDate = this.scheduledDateGenerator.getLastRepaymentDate(loanApplicationTerms, holidayDetailDTO);
        LoanTermVariationsData lastDueDateVariation = loanApplicationTerms.getLoanTermVariations().fetchLoanTermDueDateVariationsData(
                loanEndDate);
        if (lastDueDateVariation != null) {
            loanEndDate = lastDueDateVariation.getDateValue();
        }
        loanApplicationTerms.updateLoanEndDate(loanEndDate);

        // determine the total charges due at time of disbursement
        final BigDecimal chargesDueAtTimeOfDisbursement = deriveTotalChargesDueAtTimeOfDisbursement(loanCharges);

        // setup variables for tracking important facts required for loan
        // schedule generation.

        final MonetaryCurrency currency = loanApplicationTerms.getCurrency();
        final int numberOfRepayments = loanApplicationTerms.fetchNumberOfRepaymentsAfterExceptions();

        LoanScheduleParams scheduleParams = null;
        if (loanScheduleParams == null) {
            scheduleParams = LoanScheduleParams.createLoanScheduleParams(currency, Money.of(currency, chargesDueAtTimeOfDisbursement),
                    loanApplicationTerms.getExpectedDisbursementDate(), getPrincipalToBeScheduled(loanApplicationTerms));
        } else if (!loanScheduleParams.isPartialUpdate()) {
            scheduleParams = LoanScheduleParams
                    .createLoanScheduleParams(currency, Money.of(currency, chargesDueAtTimeOfDisbursement),
                            loanApplicationTerms.getExpectedDisbursementDate(), getPrincipalToBeScheduled(loanApplicationTerms),
                            loanScheduleParams);
        } else {
            scheduleParams = loanScheduleParams;
        }

        final Collection<RecalculationDetail> transactions = scheduleParams.getRecalculationDetails();
        final LoanRepaymentScheduleTransactionProcessor loanRepaymentScheduleTransactionProcessor = scheduleParams
                .getLoanRepaymentScheduleTransactionProcessor();

        final Collection<LoanScheduleModelPeriod> periods = createNewLoanScheduleListWithDisbursementDetails(numberOfRepayments,
                loanApplicationTerms, chargesDueAtTimeOfDisbursement);

        // Determine the total interest owed over the full loan for FLAT
        // interest method .
        final Money totalInterestChargedForFullLoanTerm = loanApplicationTerms.calculateTotalInterestCharged(
                this.paymentPeriodsInOneYearCalculator, mc);

        boolean isFirstRepayment = true;
        LocalDate firstRepaymentdate = this.scheduledDateGenerator.generateNextRepaymentDate(
                loanApplicationTerms.getExpectedDisbursementDate(), loanApplicationTerms, isFirstRepayment, holidayDetailDTO);
        final LocalDate idealDisbursementDate = this.scheduledDateGenerator.idealDisbursementDateBasedOnFirstRepaymentDate(
                loanApplicationTerms.getLoanTermPeriodFrequencyType(), loanApplicationTerms.getRepaymentEvery(), firstRepaymentdate);

        if (!scheduleParams.isPartialUpdate()) {
            // Set Fixed Principal Amount
            updateAmortization(mc, loanApplicationTerms, scheduleParams.getPeriodNumber(), scheduleParams.getOutstandingBalance());

            if (loanApplicationTerms.isMultiDisburseLoan()) {
                // fetches the first tranche amount and also updates other
                // tranche
                // details to map
                BigDecimal disburseAmt = getDisbursementAmount(loanApplicationTerms, scheduleParams.getPeriodStartDate(), periods,
                        chargesDueAtTimeOfDisbursement, scheduleParams.getDisburseDetailMap(), scheduleParams.applyInterestRecalculation());
                scheduleParams.setPrincipalToBeScheduled(Money.of(currency, disburseAmt));
                loanApplicationTerms.setPrincipal(loanApplicationTerms.getPrincipal().zero().plus(disburseAmt));
                scheduleParams.setOutstandingBalance(Money.of(currency, disburseAmt));
                scheduleParams.setOutstandingBalanceAsPerRest(Money.of(currency, disburseAmt));
            }
        }

        // charges which depends on total loan interest will be added to this
        // set and handled separately after all installments generated
        final Set<LoanCharge> nonCompoundingCharges = seperateTotalCompoundingPercentageCharges(loanCharges);

        LocalDate currentDate = DateUtils.getLocalDateOfTenant();
        LocalDate lastRestDate = currentDate;
        if (loanApplicationTerms.getRestCalendarInstance() != null) {
            lastRestDate = getNextRestScheduleDate(currentDate.minusDays(1), loanApplicationTerms, holidayDetailDTO);
        }

        boolean isNextRepaymentAvailable = true;
        Boolean extendTermForDailyRepayments = false;

        if (holidayDetailDTO.getWorkingDays().getExtendTermForDailyRepayments() == true
                && loanApplicationTerms.getRepaymentPeriodFrequencyType() == PeriodFrequencyType.DAYS
                && loanApplicationTerms.getRepaymentEvery() == 1) {
            holidayDetailDTO.getWorkingDays().setRepaymentReschedulingType(RepaymentRescheduleType.MOVE_TO_NEXT_WORKING_DAY.getValue());
            extendTermForDailyRepayments = true;
        }

        final Collection<LoanTermVariationsData> interestRates = loanApplicationTerms.getLoanTermVariations().getInterestRateChanges();

        // this block is to start the schedule generation from specified date
        if (scheduleParams.isPartialUpdate()) {
            if (loanApplicationTerms.isMultiDisburseLoan()) {
                loanApplicationTerms.setPrincipal(scheduleParams.getPrincipalToBeScheduled());
            }

            applyLoanVariationsForPartialScheduleGenerate(loanApplicationTerms, scheduleParams, interestRates);

            isFirstRepayment = false;
        }
        while (!scheduleParams.getOutstandingBalance().isZero() || !scheduleParams.getDisburseDetailMap().isEmpty()) {
            LocalDate previousRepaymentDate = scheduleParams.getActualRepaymentDate();
            scheduleParams.setActualRepaymentDate(this.scheduledDateGenerator.generateNextRepaymentDate(
                    scheduleParams.getActualRepaymentDate(), loanApplicationTerms, isFirstRepayment, holidayDetailDTO));
            isFirstRepayment = false;
            LocalDate scheduledDueDate = this.scheduledDateGenerator.adjustRepaymentDate(scheduleParams.getActualRepaymentDate(),
                    loanApplicationTerms, holidayDetailDTO);

            // calculated interest start date for the period
            LocalDate periodStartDateApplicableForInterest = calculateInterestStartDateForPeriod(loanApplicationTerms,
                    scheduleParams.getPeriodStartDate(), idealDisbursementDate, firstRepaymentdate);

            // Loan Schedule Exceptions that need to be applied for Loan Account
            LoanTermVariationParams termVariationParams = applyLoanTermVariations(loanApplicationTerms, scheduleParams,
                    previousRepaymentDate, scheduledDueDate);

            scheduledDueDate = termVariationParams.getScheduledDueDate();
            // Updates total days in term
            scheduleParams.addLoanTermInDays(Days.daysBetween(scheduleParams.getPeriodStartDate(), scheduledDueDate).getDays());
            if (termVariationParams.isSkipPeriod()) {
                continue;
            }

            if (scheduleParams.getPeriodStartDate().isAfter(scheduledDueDate)) { throw new ScheduleDateException(
                    "Due date can't be before period start date", scheduledDueDate); }

            if (!scheduleParams.getLatePaymentMap().isEmpty()) {
                populateCompoundingDatesInPeriod(scheduleParams.getPeriodStartDate(), scheduledDueDate, currentDate, loanApplicationTerms,
                        holidayDetailDTO, scheduleParams.getCompoundingMap(), loanCharges, currency);
                scheduleParams.getCompoundingDateVariations().put(scheduleParams.getPeriodStartDate(),
                        new TreeMap<>(scheduleParams.getCompoundingMap()));
            }

            if (extendTermForDailyRepayments) {
                scheduleParams.setActualRepaymentDate(scheduledDueDate);
            }

            // this block is to generate the schedule till the specified
            // date(used for calculating preclosure)
            if (scheduleParams.getScheduleTillDate() != null && !scheduledDueDate.isBefore(scheduleParams.getScheduleTillDate())) {
                scheduledDueDate = scheduleParams.getScheduleTillDate();
                isNextRepaymentAvailable = false;
            }

            // populates the collection with transactions till the due date of
            // the period for interest recalculation enabled loans
            Collection<RecalculationDetail> applicableTransactions = getApplicableTransactionsForPeriod(
                    scheduleParams.applyInterestRecalculation(), scheduledDueDate, transactions);

            final double interestCalculationGraceOnRepaymentPeriodFraction = this.paymentPeriodsInOneYearCalculator
                    .calculatePortionOfRepaymentPeriodInterestChargingGrace(periodStartDateApplicableForInterest, scheduledDueDate,
                            loanApplicationTerms.getInterestChargedFromLocalDate(), loanApplicationTerms.getLoanTermPeriodFrequencyType(),
                            loanApplicationTerms.getRepaymentEvery());
            ScheduleCurrentPeriodParams currentPeriodParams = new ScheduleCurrentPeriodParams(currency,
                    interestCalculationGraceOnRepaymentPeriodFraction);

            if (loanApplicationTerms.isMultiDisburseLoan()) {
                updateBalanceBasedOnDisbursement(loanApplicationTerms, chargesDueAtTimeOfDisbursement, scheduleParams, periods,
                        scheduledDueDate);
            }

            // process repayments to the schedule as per the repayment
            // transaction processor configuration
            // will add a new schedule with interest till the transaction date
            // for a loan repayment which falls between the
            // two periods for interest first repayment strategies
            handleRecalculationForNonDueDateTransactions(mc, loanApplicationTerms, loanCharges, holidayDetailDTO, scheduleParams, periods,
                    totalInterestChargedForFullLoanTerm, idealDisbursementDate, firstRepaymentdate, lastRestDate, scheduledDueDate,
                    periodStartDateApplicableForInterest, applicableTransactions, currentPeriodParams);

            if (currentPeriodParams.isSkipCurrentLoop()) {
                continue;
            }
            periodStartDateApplicableForInterest = calculateInterestStartDateForPeriod(loanApplicationTerms,
                    scheduleParams.getPeriodStartDate(), idealDisbursementDate, firstRepaymentdate);

            // backup for pre-close transaction
            updateCompoundingDetails(scheduleParams, periodStartDateApplicableForInterest);

            // 5 determine principal,interest of repayment period
            PrincipalInterest principalInterestForThisPeriod = calculatePrincipalInterestComponentsForPeriod(
                    this.paymentPeriodsInOneYearCalculator, currentPeriodParams.getInterestCalculationGraceOnRepaymentPeriodFraction(),
                    scheduleParams.getTotalCumulativePrincipal().minus(scheduleParams.getReducePrincipal()),
                    scheduleParams.getTotalCumulativeInterest(), totalInterestChargedForFullLoanTerm,
                    scheduleParams.getTotalOutstandingInterestPaymentDueToGrace(), scheduleParams.getOutstandingBalanceAsPerRest(),
                    loanApplicationTerms, scheduleParams.getPeriodNumber(), mc, mergeVariationsToMap(scheduleParams),
                    scheduleParams.getCompoundingMap(), periodStartDateApplicableForInterest, scheduledDueDate, interestRates);

            // will check for EMI amount greater than interest calculated
            if (loanApplicationTerms.getFixedEmiAmount() != null
                    && loanApplicationTerms.getFixedEmiAmount().compareTo(principalInterestForThisPeriod.interest().getAmount()) == -1) {
                String errorMsg = "EMI amount must be greater than : " + principalInterestForThisPeriod.interest().getAmount();
                throw new MultiDisbursementEmiAmountException(errorMsg, principalInterestForThisPeriod.interest().getAmount(),
                        loanApplicationTerms.getFixedEmiAmount());
            }

            // update cumulative fields for principal & interest
            currentPeriodParams.setInterestForThisPeriod(principalInterestForThisPeriod.interest());
            Money lastTotalOutstandingInterestPaymentDueToGrace = scheduleParams.getTotalOutstandingInterestPaymentDueToGrace();
            scheduleParams.setTotalOutstandingInterestPaymentDueToGrace(principalInterestForThisPeriod.interestPaymentDueToGrace());
            currentPeriodParams.setPrincipalForThisPeriod(principalInterestForThisPeriod.principal());

            // applies early payments on principal portion
            updatePrincipalPortionBasedOnPreviousEarlyPayments(currency, scheduleParams, currentPeriodParams);

            // updates amounts with current earlyPaidAmount
            updateAmountsBasedOnCurrentEarlyPayments(mc, loanApplicationTerms, scheduleParams, currentPeriodParams);

            if (scheduleParams.getOutstandingBalance().isLessThanZero() || !isNextRepaymentAvailable) {
                currentPeriodParams.plusPrincipalForThisPeriod(scheduleParams.getOutstandingBalance());
                scheduleParams.setOutstandingBalance(Money.zero(currency));
            }

            if (!isNextRepaymentAvailable) {
                scheduleParams.getDisburseDetailMap().clear();
            }

            // applies charges for the period
            applyChargesForCurrentPeriod(loanCharges, currency, scheduleParams, scheduledDueDate, currentPeriodParams);

            // sum up real totalInstallmentDue from components
            final Money totalInstallmentDue = currentPeriodParams.fetchTotalAmountForPeriod();

            // if previous installment is last then add interest to same
            // installment
            if (currentPeriodParams.getLastInstallment() != null && currentPeriodParams.getPrincipalForThisPeriod().isZero()) {
                currentPeriodParams.getLastInstallment().addInterestAmount(currentPeriodParams.getInterestForThisPeriod());
                continue;
            }

            // create repayment period from parts
            LoanScheduleModelPeriod installment = LoanScheduleModelRepaymentPeriod.repayment(scheduleParams.getInstalmentNumber(),
                    scheduleParams.getPeriodStartDate(), scheduledDueDate, currentPeriodParams.getPrincipalForThisPeriod(),
                    scheduleParams.getOutstandingBalance(), currentPeriodParams.getInterestForThisPeriod(),
                    currentPeriodParams.getFeeChargesForInstallment(), currentPeriodParams.getPenaltyChargesForInstallment(),
                    totalInstallmentDue, false);

            // apply loan transactions on installments to identify early/late
            // payments for interest recalculation
            installment = handleRecalculationForTransactions(mc, loanApplicationTerms, holidayDetailDTO, currency, scheduleParams,
                    loanRepaymentScheduleTransactionProcessor, totalInterestChargedForFullLoanTerm, lastRestDate, scheduledDueDate,
                    periodStartDateApplicableForInterest, applicableTransactions, currentPeriodParams,
                    lastTotalOutstandingInterestPaymentDueToGrace, installment);
            periods.add(installment);

            // Updates principal paid map with efective date for reducing
            // the amount from outstanding balance(interest calculation)
            updateAmountsWithEffectiveDate(loanApplicationTerms, holidayDetailDTO, scheduleParams, scheduledDueDate, currentPeriodParams,
                    installment);

            // handle cumulative fields

            scheduleParams.addTotalCumulativePrincipal(currentPeriodParams.getPrincipalForThisPeriod());
            scheduleParams.addTotalRepaymentExpected(totalInstallmentDue);
            scheduleParams.addTotalCumulativeInterest(currentPeriodParams.getInterestForThisPeriod());
            scheduleParams.setPeriodStartDate(scheduledDueDate);
            scheduleParams.incrementInstalmentNumber();
            scheduleParams.incrementPeriodNumber();
            scheduleParams.getCompoundingDateVariations().clear();
            if (termVariationParams.isRecalculateAmounts()) {
                loanApplicationTerms.setCurrentPeriodFixedEmiAmount(null);
                loanApplicationTerms.setCurrentPeriodFixedPrincipalAmount(null);
                adjustInstallmentOrPrincipalAmount(loanApplicationTerms, scheduleParams.getTotalCumulativePrincipal(),
                        scheduleParams.getPeriodNumber(), mc);
            }
        }

        // this condition is to add the interest from grace period if not
        // already applied.
        if (scheduleParams.getTotalOutstandingInterestPaymentDueToGrace().isGreaterThanZero()) {
            LoanScheduleModelPeriod installment = ((List<LoanScheduleModelPeriod>) periods).get(periods.size() - 1);
            installment.addInterestAmount(scheduleParams.getTotalOutstandingInterestPaymentDueToGrace());
            scheduleParams.addTotalRepaymentExpected(scheduleParams.getTotalOutstandingInterestPaymentDueToGrace());
            scheduleParams.addTotalCumulativeInterest(scheduleParams.getTotalOutstandingInterestPaymentDueToGrace());
            scheduleParams.setTotalOutstandingInterestPaymentDueToGrace(Money.zero(currency));
        }

        // determine fees and penalties for charges which depends on total
        // loan interest
        updatePeriodsWithCharges(currency, scheduleParams, periods, nonCompoundingCharges);

        // this block is to add extra re-payment schedules with interest portion
        // if the loan not paid with in loan term

        if (scheduleParams.getScheduleTillDate() != null) {
            currentDate = scheduleParams.getScheduleTillDate();
        }
        if (scheduleParams.applyInterestRecalculation() && scheduleParams.getLatePaymentMap().size() > 0
                && currentDate.isAfter(scheduleParams.getPeriodStartDate())) {
            Money totalInterest = addInterestOnlyRepaymentScheduleForCurrentdate(mc, loanApplicationTerms, holidayDetailDTO, currency,
                    periods, currentDate, loanRepaymentScheduleTransactionProcessor, transactions, loanCharges, scheduleParams);
            scheduleParams.addTotalCumulativeInterest(totalInterest);
        }

        loanApplicationTerms.resetFixedEmiAmount();
        final BigDecimal totalPrincipalPaid = BigDecimal.ZERO;
        final BigDecimal totalOutstanding = BigDecimal.ZERO;

        return LoanScheduleModel.from(periods, applicationCurrency, scheduleParams.getLoanTermInDays(),
                scheduleParams.getPrincipalToBeScheduled(), scheduleParams.getTotalCumulativePrincipal().getAmount(), totalPrincipalPaid,
                scheduleParams.getTotalCumulativeInterest().getAmount(), scheduleParams.getTotalFeeChargesCharged().getAmount(),
                scheduleParams.getTotalPenaltyChargesCharged().getAmount(), scheduleParams.getTotalRepaymentExpected().getAmount(),
                totalOutstanding);
    }

    private void applyChargesForCurrentPeriod(final Set<LoanCharge> loanCharges, final MonetaryCurrency currency,
            LoanScheduleParams scheduleParams, LocalDate scheduledDueDate, ScheduleCurrentPeriodParams currentPeriodParams) {
        PrincipalInterest principalInterest = new PrincipalInterest(currentPeriodParams.getPrincipalForThisPeriod(),
                currentPeriodParams.getInterestForThisPeriod(), null);
        currentPeriodParams.setFeeChargesForInstallment(cumulativeFeeChargesDueWithin(scheduleParams.getPeriodStartDate(),
                scheduledDueDate, loanCharges, currency, principalInterest, scheduleParams.getPrincipalToBeScheduled(),
                scheduleParams.getTotalCumulativeInterest(), true));
        currentPeriodParams.setPenaltyChargesForInstallment(cumulativePenaltyChargesDueWithin(scheduleParams.getPeriodStartDate(),
                scheduledDueDate, loanCharges, currency, principalInterest, scheduleParams.getPrincipalToBeScheduled(),
                scheduleParams.getTotalCumulativeInterest(), true));
        scheduleParams.addTotalFeeChargesCharged(currentPeriodParams.getFeeChargesForInstallment());
        scheduleParams.addTotalPenaltyChargesCharged(currentPeriodParams.getPenaltyChargesForInstallment());
    }

    private void updatePeriodsWithCharges(final MonetaryCurrency currency, LoanScheduleParams scheduleParams,
            final Collection<LoanScheduleModelPeriod> periods, final Set<LoanCharge> nonCompoundingCharges) {
        for (LoanScheduleModelPeriod loanScheduleModelPeriod : periods) {
            if (loanScheduleModelPeriod.isRepaymentPeriod()) {
                PrincipalInterest principalInterest = new PrincipalInterest(Money.of(currency, loanScheduleModelPeriod.principalDue()),
                        Money.of(currency, loanScheduleModelPeriod.interestDue()), null);
                Money feeChargesForInstallment = cumulativeFeeChargesDueWithin(loanScheduleModelPeriod.periodFromDate(),
                        loanScheduleModelPeriod.periodDueDate(), nonCompoundingCharges, currency, principalInterest,
                        scheduleParams.getPrincipalToBeScheduled(), scheduleParams.getTotalCumulativeInterest(),
                        !loanScheduleModelPeriod.isRecalculatedInterestComponent());
                Money penaltyChargesForInstallment = cumulativePenaltyChargesDueWithin(loanScheduleModelPeriod.periodFromDate(),
                        loanScheduleModelPeriod.periodDueDate(), nonCompoundingCharges, currency, principalInterest,
                        scheduleParams.getPrincipalToBeScheduled(), scheduleParams.getTotalCumulativeInterest(),
                        !loanScheduleModelPeriod.isRecalculatedInterestComponent());
                scheduleParams.addTotalFeeChargesCharged(feeChargesForInstallment);
                scheduleParams.addTotalPenaltyChargesCharged(penaltyChargesForInstallment);
                scheduleParams.addTotalRepaymentExpected(feeChargesForInstallment.plus(penaltyChargesForInstallment));
                loanScheduleModelPeriod.addLoanCharges(feeChargesForInstallment.getAmount(), penaltyChargesForInstallment.getAmount());
            }
        }
    }

    private void updateAmountsWithEffectiveDate(final LoanApplicationTerms loanApplicationTerms, final HolidayDetailDTO holidayDetailDTO,
            LoanScheduleParams scheduleParams, LocalDate scheduledDueDate, ScheduleCurrentPeriodParams currentPeriodParams,
            LoanScheduleModelPeriod installment) {
        LocalDate amountApplicableDate = installment.periodDueDate();
        if (loanApplicationTerms.isInterestRecalculationEnabled()) {
            amountApplicableDate = getNextRestScheduleDate(installment.periodDueDate().minusDays(1), loanApplicationTerms, holidayDetailDTO);
        }
        updateMapWithAmount(scheduleParams.getPrincipalPortionMap(),
                currentPeriodParams.getPrincipalForThisPeriod().minus(currentPeriodParams.getReducedBalance()), amountApplicableDate);

        // update outstanding balance for interest calculation
        updateOutstandingBalanceAsPerRest(scheduleParams, scheduledDueDate);
    }

    private LoanScheduleModelPeriod handleRecalculationForTransactions(final MathContext mc,
            final LoanApplicationTerms loanApplicationTerms, final HolidayDetailDTO holidayDetailDTO, final MonetaryCurrency currency,
            final LoanScheduleParams scheduleParams,
            final LoanRepaymentScheduleTransactionProcessor loanRepaymentScheduleTransactionProcessor,
            final Money totalInterestChargedForFullLoanTerm, final LocalDate lastRestDate, final LocalDate scheduledDueDate,
            final LocalDate periodStartDateApplicableForInterest, final Collection<RecalculationDetail> applicableTransactions,
            final ScheduleCurrentPeriodParams currentPeriodParams, final Money lastTotalOutstandingInterestPaymentDueToGrace,
            final LoanScheduleModelPeriod installment) {
        LoanScheduleModelPeriod modifiedInstallment = installment;
        if (scheduleParams.applyInterestRecalculation() && loanRepaymentScheduleTransactionProcessor != null) {
            Money principalProcessed = Money.zero(currency);
            addLoanRepaymentScheduleInstallment(scheduleParams.getInstallments(), modifiedInstallment);
            for (RecalculationDetail detail : applicableTransactions) {
                if (!detail.isProcessed()) {
                    LocalDate transactionDate = detail.getTransactionDate();
                    List<LoanTransaction> currentTransactions = new ArrayList<>(2);
                    currentTransactions.add(detail.getTransaction());
                    // applies the transaction as per transaction strategy
                    // on scheduled installments to identify the
                    // unprocessed(early payment ) amounts
                    Money unprocessed = loanRepaymentScheduleTransactionProcessor.handleRepaymentSchedule(currentTransactions, currency,
                            scheduleParams.getInstallments());

                    if (unprocessed.isGreaterThanZero()) {
                        scheduleParams.reduceOutstandingBalance(unprocessed);
                        // pre closure check and processing
                        modifiedInstallment = handlePrepaymentOfLoan(mc, loanApplicationTerms, holidayDetailDTO, scheduleParams,
                                totalInterestChargedForFullLoanTerm, scheduledDueDate, periodStartDateApplicableForInterest,
                                currentPeriodParams.getInterestCalculationGraceOnRepaymentPeriodFraction(), currentPeriodParams,
                                lastTotalOutstandingInterestPaymentDueToGrace, transactionDate, modifiedInstallment);

                        Money addToPrinciapal = Money.zero(currency);
                        if (scheduleParams.getOutstandingBalance().isLessThanZero()) {
                            addToPrinciapal = addToPrinciapal.plus(scheduleParams.getOutstandingBalance());
                            scheduleParams.setOutstandingBalance(Money.zero(currency));
                        }
                        updateAmountsBasedOnEarlyPayment(loanApplicationTerms, holidayDetailDTO, scheduleParams, modifiedInstallment,
                                detail, unprocessed, addToPrinciapal);

                        scheduleParams.addReducePrincipal(unprocessed);
                        currentPeriodParams.plusPrincipalForThisPeriod(unprocessed.plus(addToPrinciapal));
                        principalProcessed = principalProcessed.plus(unprocessed.plus(addToPrinciapal));
                        BigDecimal fixedEmiAmount = loanApplicationTerms.getFixedEmiAmount();
                        scheduleParams.setReducePrincipal(applyEarlyPaymentStrategy(
                                loanApplicationTerms,
                                scheduleParams.getReducePrincipal(),
                                scheduleParams.getTotalCumulativePrincipal().plus(
                                        currentPeriodParams.getPrincipalForThisPeriod().minus(principalProcessed)),
                                scheduleParams.getPeriodNumber() + 1, mc));
                        if (loanApplicationTerms.getAmortizationMethod().isEqualInstallment()
                                && fixedEmiAmount.compareTo(loanApplicationTerms.getFixedEmiAmount()) != 0) {
                            currentPeriodParams.setEmiAmountChanged(true);
                        }

                    }
                }
            }
            updateLatePaymentsToMap(loanApplicationTerms, holidayDetailDTO, currency, scheduleParams.getLatePaymentMap(), scheduledDueDate,
                    scheduleParams.getInstallments(), true, lastRestDate, scheduleParams.getCompoundingMap());
            currentPeriodParams.minusPrincipalForThisPeriod(principalProcessed);
        }
        return modifiedInstallment;
    }

    private LoanScheduleModelPeriod handlePrepaymentOfLoan(final MathContext mc, final LoanApplicationTerms loanApplicationTerms,
            final HolidayDetailDTO holidayDetailDTO, final LoanScheduleParams scheduleParams,
            final Money totalInterestChargedForFullLoanTerm, final LocalDate scheduledDueDate,
            LocalDate periodStartDateApplicableForInterest, final double interestCalculationGraceOnRepaymentPeriodFraction,
            final ScheduleCurrentPeriodParams currentPeriodParams, final Money lastTotalOutstandingInterestPaymentDueToGrace,
            final LocalDate transactionDate, final LoanScheduleModelPeriod installment) {
        LoanScheduleModelPeriod modifiedInstallment = installment;
        if (!scheduleParams.getOutstandingBalance().isGreaterThan(currentPeriodParams.getInterestForThisPeriod())
                && !scheduledDueDate.equals(transactionDate)) {
            final Collection<LoanTermVariationsData> interestRates = loanApplicationTerms.getLoanTermVariations().getInterestRateChanges();
            LocalDate calculateTill = transactionDate;
            if (loanApplicationTerms.getPreClosureInterestCalculationStrategy().calculateTillRestFrequencyEnabled()) {
                calculateTill = getNextRestScheduleDate(calculateTill.minusDays(1), loanApplicationTerms, holidayDetailDTO);
            }
            if (scheduleParams.getCompoundingDateVariations().containsKey(periodStartDateApplicableForInterest)) {
                scheduleParams.getCompoundingMap().clear();
                scheduleParams.getCompoundingMap().putAll(
                        scheduleParams.getCompoundingDateVariations().get(periodStartDateApplicableForInterest));
            }
            if (currentPeriodParams.isEmiAmountChanged()) {
                updateFixedInstallmentAmount(mc, loanApplicationTerms, scheduleParams.getPeriodNumber(), loanApplicationTerms
                        .getPrincipal().minus(scheduleParams.getTotalCumulativePrincipal()));
            }
            PrincipalInterest interestTillDate = calculatePrincipalInterestComponentsForPeriod(this.paymentPeriodsInOneYearCalculator,
                    interestCalculationGraceOnRepaymentPeriodFraction, scheduleParams.getTotalCumulativePrincipal(),
                    scheduleParams.getTotalCumulativeInterest(), totalInterestChargedForFullLoanTerm,
                    lastTotalOutstandingInterestPaymentDueToGrace, scheduleParams.getOutstandingBalanceAsPerRest(), loanApplicationTerms,
                    scheduleParams.getPeriodNumber(), mc, mergeVariationsToMap(scheduleParams), scheduleParams.getCompoundingMap(),
                    periodStartDateApplicableForInterest, calculateTill, interestRates);
            Money diff = currentPeriodParams.getInterestForThisPeriod().minus(interestTillDate.interest());
            if (!scheduleParams.getOutstandingBalance().minus(diff).isGreaterThanZero()) {
                scheduleParams.reduceOutstandingBalance(diff);
                currentPeriodParams.minusInterestForThisPeriod(diff);
                currentPeriodParams.plusPrincipalForThisPeriod(diff);
                final Money totalDue = currentPeriodParams.getPrincipalForThisPeriod().plus(currentPeriodParams.getInterestForThisPeriod());

                // create and replaces repayment period
                // from parts
                modifiedInstallment = LoanScheduleModelRepaymentPeriod.repayment(scheduleParams.getInstalmentNumber(),
                        scheduleParams.getPeriodStartDate(), transactionDate, currentPeriodParams.getPrincipalForThisPeriod(),
                        scheduleParams.getOutstandingBalance(), currentPeriodParams.getInterestForThisPeriod(),
                        currentPeriodParams.getFeeChargesForInstallment(), currentPeriodParams.getPenaltyChargesForInstallment(), totalDue,
                        false);
                scheduleParams.setTotalOutstandingInterestPaymentDueToGrace(interestTillDate.interestPaymentDueToGrace());
            }

        }
        return modifiedInstallment;
    }

    private void updateAmountsBasedOnCurrentEarlyPayments(final MathContext mc, final LoanApplicationTerms loanApplicationTerms,
            LoanScheduleParams scheduleParams, ScheduleCurrentPeriodParams currentPeriodParams) {
        currentPeriodParams.setReducedBalance(currentPeriodParams.getEarlyPaidAmount());
        currentPeriodParams.minusEarlyPaidAmount(currentPeriodParams.getPrincipalForThisPeriod());
        if (currentPeriodParams.getEarlyPaidAmount().isGreaterThanZero()) {
            scheduleParams.addReducePrincipal(currentPeriodParams.getEarlyPaidAmount());
            BigDecimal fixedEmiAmount = loanApplicationTerms.getFixedEmiAmount();
            scheduleParams.setReducePrincipal(applyEarlyPaymentStrategy(
                    loanApplicationTerms,
                    scheduleParams.getReducePrincipal(),
                    scheduleParams.getTotalCumulativePrincipal().plus(currentPeriodParams.getPrincipalForThisPeriod())
                            .plus(currentPeriodParams.getEarlyPaidAmount()), scheduleParams.getPeriodNumber() + 1, mc));
            if (loanApplicationTerms.getAmortizationMethod().isEqualInstallment()
                    && fixedEmiAmount.compareTo(loanApplicationTerms.getFixedEmiAmount()) != 0) {
                currentPeriodParams.setEmiAmountChanged(true);
            }
            currentPeriodParams.plusPrincipalForThisPeriod(currentPeriodParams.getEarlyPaidAmount());
        }

        // update outstandingLoanBlance using current period
        // 'principalDue'
        scheduleParams.reduceOutstandingBalance(currentPeriodParams.getPrincipalForThisPeriod().minus(
                currentPeriodParams.getReducedBalance()));
    }

    private void updatePrincipalPortionBasedOnPreviousEarlyPayments(final MonetaryCurrency currency,
            final LoanScheduleParams scheduleParams, final ScheduleCurrentPeriodParams currentPeriodParams) {
        if (currentPeriodParams.getPrincipalForThisPeriod().isGreaterThan(scheduleParams.getReducePrincipal())) {
            currentPeriodParams.minusPrincipalForThisPeriod(scheduleParams.getReducePrincipal());
            scheduleParams.setReducePrincipal(Money.zero(currency));
        } else {
            scheduleParams.reduceReducePrincipal(currentPeriodParams.getPrincipalForThisPeriod());
            currentPeriodParams.setPrincipalForThisPeriod(Money.zero(currency));
        }
    }

    private void updateCompoundingDetails(LoanScheduleParams scheduleParams, LocalDate periodStartDateApplicableForInterest) {
        if (scheduleParams.getCompoundingDateVariations().containsKey(periodStartDateApplicableForInterest)) {
            scheduleParams.getCompoundingMap().clear();
            scheduleParams.getCompoundingMap().putAll(
                    scheduleParams.getCompoundingDateVariations().get(periodStartDateApplicableForInterest));
        } else {
            scheduleParams.getCompoundingDateVariations().put(periodStartDateApplicableForInterest,
                    new TreeMap<>(scheduleParams.getCompoundingMap()));
        }
    }

    private void handleRecalculationForNonDueDateTransactions(final MathContext mc, final LoanApplicationTerms loanApplicationTerms,
            final Set<LoanCharge> loanCharges, final HolidayDetailDTO holidayDetailDTO, LoanScheduleParams scheduleParams,
            final Collection<LoanScheduleModelPeriod> periods, final Money totalInterestChargedForFullLoanTerm,
            final LocalDate idealDisbursementDate, LocalDate firstRepaymentdate, final LocalDate lastRestDate,
            final LocalDate scheduledDueDate, final LocalDate periodStartDateForInterest,
            final Collection<RecalculationDetail> applicableTransactions, final ScheduleCurrentPeriodParams currentPeriodParams) {
        if (scheduleParams.applyInterestRecalculation()) {
            final MonetaryCurrency currency = scheduleParams.getCurrency();
            final Collection<LoanTermVariationsData> interestRates = loanApplicationTerms.getLoanTermVariations().getInterestRateChanges();
            boolean checkForOutstanding = true;
            List<RecalculationDetail> unprocessedTransactions = new ArrayList<>();
            LoanScheduleModelPeriod installment = null;
            LocalDate periodStartDateApplicableForInterest = periodStartDateForInterest;
            for (RecalculationDetail detail : applicableTransactions) {
                if (detail.isProcessed()) {
                    continue;
                }
                boolean updateLatePaymentMap = false;
                final LocalDate transactionDate = detail.getTransactionDate();
                if (transactionDate.isBefore(scheduledDueDate)) {
                    if (scheduleParams.getLoanRepaymentScheduleTransactionProcessor() != null
                            && scheduleParams.getLoanRepaymentScheduleTransactionProcessor()
                                    .isInterestFirstRepaymentScheduleTransactionProcessor()) {
                        List<LoanTransaction> currentTransactions = createCurrentTransactionList(detail);
                        if (!transactionDate.isEqual(scheduleParams.getPeriodStartDate()) ||  scheduleParams.getInstalmentNumber() == 1) {

                            int periodDays = Days.daysBetween(scheduleParams.getPeriodStartDate(), transactionDate).getDays();
                            // calculates period start date for interest
                            // calculation as per the configuration
                            periodStartDateApplicableForInterest = calculateInterestStartDateForPeriod(loanApplicationTerms,
                                    scheduleParams.getPeriodStartDate(), idealDisbursementDate, firstRepaymentdate);

                            int daysInPeriodApplicable = Days.daysBetween(periodStartDateApplicableForInterest, transactionDate).getDays();
                            Money interestForThisinstallment = Money.zero(currency);
                            if (daysInPeriodApplicable > 0) {
                                // 5 determine interest till the transaction
                                // date
                                if (!scheduleParams.getCompoundingDateVariations().containsKey(periodStartDateApplicableForInterest)) {
                                    scheduleParams.getCompoundingDateVariations().put(periodStartDateApplicableForInterest,
                                            new TreeMap<>(scheduleParams.getCompoundingMap()));
                                }
                                PrincipalInterest principalInterestForThisPeriod = calculatePrincipalInterestComponentsForPeriod(
                                        this.paymentPeriodsInOneYearCalculator,
                                        currentPeriodParams.getInterestCalculationGraceOnRepaymentPeriodFraction(), scheduleParams
                                                .getTotalCumulativePrincipal().minus(scheduleParams.getReducePrincipal()),
                                        scheduleParams.getTotalCumulativeInterest(), totalInterestChargedForFullLoanTerm,
                                        scheduleParams.getTotalOutstandingInterestPaymentDueToGrace(),
                                        scheduleParams.getOutstandingBalanceAsPerRest(), loanApplicationTerms,
                                        scheduleParams.getPeriodNumber(), mc, mergeVariationsToMap(scheduleParams),
                                        scheduleParams.getCompoundingMap(), periodStartDateApplicableForInterest, transactionDate,
                                        interestRates);
                                interestForThisinstallment = principalInterestForThisPeriod.interest();

                                scheduleParams.setTotalOutstandingInterestPaymentDueToGrace(principalInterestForThisPeriod
                                        .interestPaymentDueToGrace());
                            }

                            Money principalForThisPeriod = Money.zero(currency);

                            // applies all the applicable charges to the
                            // newly
                            // created installment
                            PrincipalInterest principalInterest = new PrincipalInterest(principalForThisPeriod, interestForThisinstallment,
                                    null);
                            Money feeChargesForInstallment = cumulativeFeeChargesDueWithin(scheduleParams.getPeriodStartDate(),
                                    transactionDate, loanCharges, currency, principalInterest, scheduleParams.getPrincipalToBeScheduled(),
                                    scheduleParams.getTotalCumulativeInterest(), false);
                            Money penaltyChargesForInstallment = cumulativePenaltyChargesDueWithin(scheduleParams.getPeriodStartDate(),
                                    transactionDate, loanCharges, currency, principalInterest, scheduleParams.getPrincipalToBeScheduled(),
                                    scheduleParams.getTotalCumulativeInterest(), false);

                            // sum up real totalInstallmentDue from
                            // components
                            final Money totalInstallmentDue = principalForThisPeriod.plus(interestForThisinstallment)
                                    .plus(feeChargesForInstallment).plus(penaltyChargesForInstallment);
                            // create repayment period from parts
                            installment = LoanScheduleModelRepaymentPeriod.repayment(scheduleParams.getInstalmentNumber(),
                                    scheduleParams.getPeriodStartDate(), transactionDate, principalForThisPeriod,
                                    scheduleParams.getOutstandingBalance(), interestForThisinstallment, feeChargesForInstallment,
                                    penaltyChargesForInstallment, totalInstallmentDue, true);
                            periods.add(installment);

                            // update outstanding balance for interest
                            // calculation as per the rest
                            updateOutstandingBalanceAsPerRest(scheduleParams, transactionDate);

                            // handle cumulative fields
                            scheduleParams.addLoanTermInDays(periodDays);
                            scheduleParams.addTotalRepaymentExpected(totalInstallmentDue);
                            scheduleParams.addTotalCumulativeInterest(interestForThisinstallment);
                            scheduleParams.addTotalFeeChargesCharged(feeChargesForInstallment);
                            scheduleParams.addTotalPenaltyChargesCharged(penaltyChargesForInstallment);

                            scheduleParams.setPeriodStartDate(transactionDate);
                            periodStartDateApplicableForInterest = scheduleParams.getPeriodStartDate();
                            updateLatePaymentMap = true;
                            scheduleParams.incrementInstalmentNumber();
                            // creates and insert Loan repayment schedule
                            // for
                            // the period
                            addLoanRepaymentScheduleInstallment(scheduleParams.getInstallments(), installment);
                        } else if (installment == null) {
                            installment = ((List<LoanScheduleModelPeriod>) periods).get(periods.size() - 1);
                        }
                        // applies the transaction as per transaction
                        // strategy
                        // on scheduled installments to identify the
                        // unprocessed(early payment ) amounts
                        Money unprocessed = scheduleParams.getLoanRepaymentScheduleTransactionProcessor().handleRepaymentSchedule(
                                currentTransactions, currency, scheduleParams.getInstallments());
                        if (unprocessed.isGreaterThanZero()) {

                            if (loanApplicationTerms.getPreClosureInterestCalculationStrategy().calculateTillRestFrequencyEnabled()) {
                                LocalDate applicableDate = getNextRestScheduleDate(transactionDate.minusDays(1), loanApplicationTerms,
                                        holidayDetailDTO);
                                checkForOutstanding = transactionDate.isEqual(applicableDate);

                            }
                            // reduces actual outstanding balance
                            scheduleParams.reduceOutstandingBalance(unprocessed);
                            // if outstanding balance becomes less than zero
                            // then adjusts the princiapal
                            Money addToPrinciapal = Money.zero(currency);
                            if (!scheduleParams.getOutstandingBalance().isGreaterThanZero()) {
                                addToPrinciapal = addToPrinciapal.plus(scheduleParams.getOutstandingBalance());
                                scheduleParams.setOutstandingBalance(Money.zero(currency));
                                currentPeriodParams.setLastInstallment(installment);
                            }
                            // updates principal portion map with the early
                            // payment amounts and applicable date as per
                            // rest
                            updateAmountsBasedOnEarlyPayment(loanApplicationTerms, holidayDetailDTO, scheduleParams, installment, detail,
                                    unprocessed, addToPrinciapal);

                            // method applies early payment strategy
                            scheduleParams.addReducePrincipal(unprocessed);
                            scheduleParams.setReducePrincipal(applyEarlyPaymentStrategy(loanApplicationTerms,
                                    scheduleParams.getReducePrincipal(), scheduleParams.getTotalCumulativePrincipal(),
                                    scheduleParams.getPeriodNumber(), mc));
                        }
                        // identify late payments and add compounding
                        // details to
                        // map for interest calculation
                        handleLatePayments(loanApplicationTerms, holidayDetailDTO, currency, scheduleParams, lastRestDate,
                                periodStartDateApplicableForInterest, detail);
                        if (updateLatePaymentMap) {
                            updateLatePaymentsToMap(loanApplicationTerms, holidayDetailDTO, currency, scheduleParams.getLatePaymentMap(),
                                    scheduledDueDate, scheduleParams.getInstallments(), true, lastRestDate,
                                    scheduleParams.getCompoundingMap());
                        }
                    } else if (scheduleParams.getLoanRepaymentScheduleTransactionProcessor() != null) {
                        LocalDate applicableDate = getNextRestScheduleDate(transactionDate.minusDays(1), loanApplicationTerms,
                                holidayDetailDTO);
                        if (applicableDate.isBefore(scheduledDueDate)) {
                            List<LoanTransaction> currentTransactions = createCurrentTransactionList(detail);
                            Money unprocessed = scheduleParams.getLoanRepaymentScheduleTransactionProcessor().handleRepaymentSchedule(
                                    currentTransactions, currency, scheduleParams.getInstallments());
                            Money arrears = fetchCompoundedArrears(loanApplicationTerms, currency, detail.getTransaction());
                            if (unprocessed.isGreaterThanZero()) {
                                arrears = getTotalAmount(scheduleParams.getLatePaymentMap(), currency);
                                updateMapWithAmount(scheduleParams.getPrincipalPortionMap(), unprocessed, applicableDate);
                                currentPeriodParams.plusEarlyPaidAmount(unprocessed);

                                // this check is to identify pre-closure and
                                // apply interest calculation as per
                                // configuration for non due date payments
                                if (!scheduleParams.getOutstandingBalance().isGreaterThan(unprocessed)
                                        && !loanApplicationTerms.getPreClosureInterestCalculationStrategy()
                                                .calculateTillRestFrequencyEnabled()) {

                                    LocalDate calculateTill = transactionDate;
                                    if (!scheduleParams.getCompoundingDateVariations().containsKey(periodStartDateApplicableForInterest)) {
                                        scheduleParams.getCompoundingDateVariations().put(periodStartDateApplicableForInterest,
                                                new TreeMap<>(scheduleParams.getCompoundingMap()));
                                    }
                                    PrincipalInterest principalInterestForThisPeriod = calculatePrincipalInterestComponentsForPeriod(
                                            this.paymentPeriodsInOneYearCalculator,
                                            currentPeriodParams.getInterestCalculationGraceOnRepaymentPeriodFraction(), scheduleParams
                                                    .getTotalCumulativePrincipal().minus(scheduleParams.getReducePrincipal()),
                                            scheduleParams.getTotalCumulativeInterest(), totalInterestChargedForFullLoanTerm,
                                            scheduleParams.getTotalOutstandingInterestPaymentDueToGrace(),
                                            scheduleParams.getOutstandingBalanceAsPerRest(), loanApplicationTerms,
                                            scheduleParams.getPeriodNumber(), mc, mergeVariationsToMap(scheduleParams),
                                            scheduleParams.getCompoundingMap(), periodStartDateApplicableForInterest, calculateTill,
                                            interestRates);
                                    if (!principalInterestForThisPeriod.interest()
                                            .plus(principalInterestForThisPeriod.interestPaymentDueToGrace())
                                            .plus(scheduleParams.getOutstandingBalance()).isGreaterThan(unprocessed)) {
                                        currentPeriodParams.minusEarlyPaidAmount(unprocessed);
                                        updateMapWithAmount(scheduleParams.getPrincipalPortionMap(), unprocessed.negated(), applicableDate);
                                        LoanTransaction loanTransaction = LoanTransaction.repayment(null, unprocessed, null,
                                                transactionDate, null, DateUtils.getLocalDateTimeOfTenant(), null);
                                        RecalculationDetail recalculationDetail = new RecalculationDetail(transactionDate, loanTransaction);
                                        unprocessedTransactions.add(recalculationDetail);
                                        break;
                                    }
                                }
                                LoanTransaction loanTransaction = LoanTransaction.repayment(null, unprocessed, null, scheduledDueDate,
                                        null, DateUtils.getLocalDateTimeOfTenant(), null);
                                RecalculationDetail recalculationDetail = new RecalculationDetail(scheduledDueDate, loanTransaction);
                                unprocessedTransactions.add(recalculationDetail);
                                checkForOutstanding = false;

                                scheduleParams.reduceOutstandingBalance(unprocessed);
                                // if outstanding balance becomes less than
                                // zero
                                // then adjusts the princiapal
                                Money addToPrinciapal = Money.zero(currency);
                                if (scheduleParams.getOutstandingBalance().isLessThanZero()) {
                                    addToPrinciapal = addToPrinciapal.plus(scheduleParams.getOutstandingBalance());
                                    scheduleParams.setOutstandingBalance(Money.zero(currency));
                                    updateMapWithAmount(scheduleParams.getPrincipalPortionMap(), addToPrinciapal, applicableDate);
                                    currentPeriodParams.plusEarlyPaidAmount(addToPrinciapal);
                                }

                            }
                            if (arrears.isGreaterThanZero() && applicableDate.isBefore(lastRestDate)) {
                                handleLatePayments(loanApplicationTerms, holidayDetailDTO, currency, scheduleParams, lastRestDate,
                                        periodStartDateApplicableForInterest, detail);
                            }
                        }

                    }
                }

            }
            applicableTransactions.addAll(unprocessedTransactions);
            if (checkForOutstanding && scheduleParams.getOutstandingBalance().isZero() && scheduleParams.getDisburseDetailMap().isEmpty()) {
                currentPeriodParams.setSkipCurrentLoop(true);
            }
        }
    }

    /**
     * @param loanApplicationTerms
     * @param holidayDetailDTO
     * @param currency
     * @param scheduleParams
     * @param lastRestDate
     * @param periodStartDateApplicableForInterest
     * @param detail
     */
    private void handleLatePayments(final LoanApplicationTerms loanApplicationTerms, final HolidayDetailDTO holidayDetailDTO,
            final MonetaryCurrency currency, LoanScheduleParams scheduleParams, LocalDate lastRestDate,
            LocalDate periodStartDateApplicableForInterest, RecalculationDetail detail) {
        updateLatePaidAmountsToPrincipalMap(detail.getTransaction(), loanApplicationTerms, currency, holidayDetailDTO, lastRestDate,
                scheduleParams);
        scheduleParams.getCompoundingDateVariations().put(periodStartDateApplicableForInterest,
                new TreeMap<>(scheduleParams.getCompoundingMap()));
    }

    private void updateAmountsBasedOnEarlyPayment(final LoanApplicationTerms loanApplicationTerms, final HolidayDetailDTO holidayDetailDTO,
            LoanScheduleParams scheduleParams, final LoanScheduleModelPeriod installment, RecalculationDetail detail, Money unprocessed,
            Money addToPrinciapal) {
        updatePrincipalPaidPortionToMap(loanApplicationTerms, holidayDetailDTO, scheduleParams.getPrincipalPortionMap(), installment,
                detail, unprocessed.plus(addToPrinciapal), scheduleParams.getInstallments());
        scheduleParams.addTotalRepaymentExpected(unprocessed.plus(addToPrinciapal));
        scheduleParams.addTotalCumulativePrincipal(unprocessed.plus(addToPrinciapal));
    }

    private void updateOutstandingBalanceAsPerRest(final LoanScheduleParams scheduleParams, final LocalDate scheduledDueDate) {
        scheduleParams.setOutstandingBalanceAsPerRest(updateBalanceForInterestCalculation(scheduleParams.getPrincipalPortionMap(),
                scheduledDueDate, scheduleParams.getOutstandingBalanceAsPerRest(), false));
        scheduleParams.setOutstandingBalanceAsPerRest(updateBalanceForInterestCalculation(scheduleParams.getDisburseDetailMap(),
                scheduledDueDate, scheduleParams.getOutstandingBalanceAsPerRest(), true));
    }

    /**
     * Method updates outstanding balance of the loan for interest calculation
     * 
     */
    private void updateBalanceBasedOnDisbursement(final LoanApplicationTerms loanApplicationTerms,
            final BigDecimal chargesDueAtTimeOfDisbursement, LoanScheduleParams scheduleParams,
            final Collection<LoanScheduleModelPeriod> periods, final LocalDate scheduledDueDate) {
        for (Map.Entry<LocalDate, Money> disburseDetail : scheduleParams.getDisburseDetailMap().entrySet()) {
            if (disburseDetail.getKey().isAfter(scheduleParams.getPeriodStartDate()) && !disburseDetail.getKey().isAfter(scheduledDueDate)) {
                // validation check for amount not exceeds specified max
                // amount as per the configuration
                if (loanApplicationTerms.getMaxOutstandingBalance() != null
                        && scheduleParams.getOutstandingBalance().plus(disburseDetail.getValue())
                                .isGreaterThan(loanApplicationTerms.getMaxOutstandingBalance())) {
                    String errorMsg = "Outstanding balance must not exceed the amount: " + loanApplicationTerms.getMaxOutstandingBalance();
                    throw new MultiDisbursementOutstandingAmoutException(errorMsg, loanApplicationTerms.getMaxOutstandingBalance()
                            .getAmount(), disburseDetail.getValue());
                }

                // creates and add disbursement detail to the repayments
                // period
                final LoanScheduleModelDisbursementPeriod disbursementPeriod = LoanScheduleModelDisbursementPeriod.disbursement(
                        disburseDetail.getKey(), disburseDetail.getValue(), chargesDueAtTimeOfDisbursement);
                periods.add(disbursementPeriod);
                // updates actual outstanding balance with new
                // disbursement detail
                scheduleParams.addOutstandingBalance(disburseDetail.getValue());
                scheduleParams.addPrincipalToBeScheduled(disburseDetail.getValue());
                loanApplicationTerms.setPrincipal(loanApplicationTerms.getPrincipal().plus(disburseDetail.getValue()));
            }
        }
    }

    /**
     * @param loanApplicationTerms
     * @param scheduleParams
     * @param priviousScheduledDueDate
     * @param previousRepaymentDate
     * @param scheduledDueDate
     * @param scheduleDateForReversal
     * @return
     */
    private LoanTermVariationParams applyLoanTermVariations(final LoanApplicationTerms loanApplicationTerms,
            final LoanScheduleParams scheduleParams, final LocalDate previousRepaymentDate, final LocalDate scheduledDueDate) {
        boolean skipPeriod = false;
        boolean recalculateAmounts = false;
        LocalDate modifiedScheduledDueDate = scheduledDueDate;

        // due date changes should be applied only for that dueDate
        if (loanApplicationTerms.getLoanTermVariations().hasDueDateVariation(scheduledDueDate)) {
            LoanTermVariationsData loanTermVariationsData = loanApplicationTerms.getLoanTermVariations().nextDueDateVariation();
            if (loanTermVariationsData.getTermApplicableFrom().isEqual(modifiedScheduledDueDate)) {
                modifiedScheduledDueDate = loanTermVariationsData.getDateValue();
                if (!loanTermVariationsData.isSpecificToInstallment()) {
                    scheduleParams.setActualRepaymentDate(modifiedScheduledDueDate);
                }
                loanTermVariationsData.setProcessed(true);
            }
        }

        while (loanApplicationTerms.getLoanTermVariations().hasVariation(modifiedScheduledDueDate)) {
            LoanTermVariationsData loanTermVariationsData = loanApplicationTerms.getLoanTermVariations().nextVariation();
            if (loanTermVariationsData.isProcessed()) {
                continue;
            }
            switch (loanTermVariationsData.getTermVariationType()) {
                case INSERT_INSTALLMENT:
                    scheduleParams.setActualRepaymentDate(previousRepaymentDate);
                    modifiedScheduledDueDate = loanTermVariationsData.getTermApplicableFrom();
                    if (loanTermVariationsData.getDecimalValue() != null) {
                        if (loanApplicationTerms.getInterestMethod().isDecliningBalnce()
                                && loanApplicationTerms.getAmortizationMethod().isEqualInstallment()) {
                            loanApplicationTerms.setCurrentPeriodFixedEmiAmount(loanTermVariationsData.getDecimalValue());
                        } else {
                            loanApplicationTerms.setCurrentPeriodFixedPrincipalAmount(loanTermVariationsData.getDecimalValue());
                        }
                        recalculateAmounts = true;
                    }
                    loanTermVariationsData.setProcessed(true);
                break;
                case DELETE_INSTALLMENT:
                    if (loanTermVariationsData.getTermApplicableFrom().isEqual(modifiedScheduledDueDate)) {
                        skipPeriod = true;
                        loanTermVariationsData.setProcessed(true);
                    }
                break;
                case EMI_AMOUNT:
                    if (loanTermVariationsData.isSpecificToInstallment()) {
                        loanApplicationTerms.setCurrentPeriodFixedEmiAmount(loanTermVariationsData.getDecimalValue());
                        recalculateAmounts = true;
                    } else {
                        loanApplicationTerms.setFixedEmiAmount(loanTermVariationsData.getDecimalValue());
                    }
                    loanTermVariationsData.setProcessed(true);
                break;
                case PRINCIPAL_AMOUNT:
                    if (loanTermVariationsData.isSpecificToInstallment()) {
                        loanApplicationTerms.setCurrentPeriodFixedPrincipalAmount(loanTermVariationsData.getDecimalValue());
                        recalculateAmounts = true;
                    } else {
                        loanApplicationTerms.setFixedPrincipalAmount(loanTermVariationsData.getDecimalValue());
                    }
                    loanTermVariationsData.setProcessed(true);
                break;
                default:
                break;

            }
        }
        LoanTermVariationParams termVariationParams = new LoanTermVariationParams(skipPeriod, recalculateAmounts, modifiedScheduledDueDate);
        return termVariationParams;
    }

    /**
     * @param loanApplicationTerms
     * @param scheduleParams
     * @param interestRates
     */
    private void applyLoanVariationsForPartialScheduleGenerate(final LoanApplicationTerms loanApplicationTerms,
            LoanScheduleParams scheduleParams, final Collection<LoanTermVariationsData> interestRates) {
        // Applies loan variations
        while (loanApplicationTerms.getLoanTermVariations().hasVariation(scheduleParams.getPeriodStartDate())) {
            LoanTermVariationsData variation = loanApplicationTerms.getLoanTermVariations().nextVariation();
            if (!variation.isSpecificToInstallment()) {
                switch (variation.getTermVariationType()) {
                    case EMI_AMOUNT:
                        loanApplicationTerms.setFixedEmiAmount(variation.getDecimalValue());
                    break;
                    case PRINCIPAL_AMOUNT:
                        loanApplicationTerms.setFixedPrincipalAmount(variation.getDecimalValue());
                    break;
                    default:
                    break;
                }
            }

            variation.setProcessed(true);
        }

        // Applies interest rate changes
        for (LoanTermVariationsData variation : interestRates) {
            if (variation.getTermVariationType().isInterestRateVariation() && variation.isApplicable(scheduleParams.getPeriodStartDate())
                    && variation.getDecimalValue() != null) {
                loanApplicationTerms.updateAnnualNominalInterestRate(variation.getDecimalValue());
            }
        }
    }

    /**
     * this method calculates the principal amount for generating the repayment
     * schedule.
     */
    private Money getPrincipalToBeScheduled(final LoanApplicationTerms loanApplicationTerms) {
        Money principalToBeScheduled;
        if (loanApplicationTerms.isMultiDisburseLoan() && loanApplicationTerms.getApprovedPrincipal().isGreaterThanZero()) {
            principalToBeScheduled = loanApplicationTerms.getApprovedPrincipal();
        } else {
            principalToBeScheduled = loanApplicationTerms.getPrincipal();
        }
        return principalToBeScheduled;
    }

    private boolean updateFixedInstallmentAmount(final MathContext mc, final LoanApplicationTerms loanApplicationTerms, int periodNumber,
            Money outstandingBalance) {
        boolean isAmountChanged = false;
        if (loanApplicationTerms.getActualFixedEmiAmount() == null && loanApplicationTerms.getInterestMethod().isDecliningBalnce()
                && loanApplicationTerms.getAmortizationMethod().isEqualInstallment()) {
            if (periodNumber < loanApplicationTerms.getPrincipalGrace() + 1) {
                periodNumber = loanApplicationTerms.getPrincipalGrace() + 1;
            }
            Money emiAmount = loanApplicationTerms.pmtForInstallment(this.paymentPeriodsInOneYearCalculator, outstandingBalance,
                    periodNumber, mc);
            loanApplicationTerms.setFixedEmiAmount(emiAmount.getAmount());
            isAmountChanged = true;
        }
        return isAmountChanged;
    }

    private Money fetchCompoundedArrears(final LoanApplicationTerms loanApplicationTerms, final MonetaryCurrency currency,
            final LoanTransaction transaction) {
        Money arrears = transaction.getPrincipalPortion(currency);
        if (loanApplicationTerms.getInterestRecalculationCompoundingMethod().isInterestCompoundingEnabled()) {
            arrears = arrears.plus(transaction.getInterestPortion(currency));
        }

        if (loanApplicationTerms.getInterestRecalculationCompoundingMethod().isFeeCompoundingEnabled()) {
            arrears = arrears.plus(transaction.getFeeChargesPortion(currency)).plus(transaction.getPenaltyChargesPortion(currency));
        }
        return arrears;
    }

    /**
     * Method calculates interest on not paid outstanding principal and interest
     * (if compounding is enabled) till current date and adds new repayment
     * schedule detail
     * 
     * @param compoundingMap
     *            TODO
     * @param loanCharges
     *            TODO
     * @param principalPortioMap
     *            TODO
     * 
     */
    private Money addInterestOnlyRepaymentScheduleForCurrentdate(final MathContext mc, final LoanApplicationTerms loanApplicationTerms,
            final HolidayDetailDTO holidayDetailDTO, final MonetaryCurrency currency, final Collection<LoanScheduleModelPeriod> periods,
            final LocalDate currentDate, LoanRepaymentScheduleTransactionProcessor loanRepaymentScheduleTransactionProcessor,
            final Collection<RecalculationDetail> transactions, final Set<LoanCharge> loanCharges, final LoanScheduleParams params) {
        boolean isFirstRepayment = false;
        LocalDate startDate = params.getPeriodStartDate();
        Money outstanding = Money.zero(currency);
        Money totalInterest = Money.zero(currency);
        Money totalCumulativeInterest = Money.zero(currency);
        double interestCalculationGraceOnRepaymentPeriodFraction = Double.valueOf(0);
        int periodNumberTemp = 1;
        LocalDate lastRestDate = getNextRestScheduleDate(currentDate.minusDays(1), loanApplicationTerms, holidayDetailDTO);
        Collection<LoanTermVariationsData> applicableVariations = loanApplicationTerms.getLoanTermVariations().getInterestRateChanges();

        do {

            params.setActualRepaymentDate(this.scheduledDateGenerator.generateNextRepaymentDate(params.getActualRepaymentDate(),
                    loanApplicationTerms, isFirstRepayment, holidayDetailDTO));
            if (params.getActualRepaymentDate().isAfter(currentDate)) {
                params.setActualRepaymentDate(currentDate);
            }
            outstanding = updateOutstandingFromLatePayment(params.getPeriodStartDate(), params.getLatePaymentMap(), outstanding);

            Collection<RecalculationDetail> applicableTransactions = getApplicableTransactionsForPeriod(
                    params.applyInterestRecalculation(), params.getActualRepaymentDate(), transactions);

            if (!params.getLatePaymentMap().isEmpty()) {
                populateCompoundingDatesInPeriod(params.getPeriodStartDate(), params.getActualRepaymentDate(), currentDate,
                        loanApplicationTerms, holidayDetailDTO, params.getCompoundingMap(), loanCharges, currency);
            }

            for (RecalculationDetail detail : applicableTransactions) {
                if (detail.isProcessed()) {
                    continue;
                }
                LocalDate transactionDate = detail.getTransactionDate();
                List<LoanTransaction> currentTransactions = createCurrentTransactionList(detail);

                if (!params.getPeriodStartDate().isEqual(transactionDate)) {
                    PrincipalInterest principalInterestForThisPeriod = calculatePrincipalInterestComponentsForPeriod(
                            this.paymentPeriodsInOneYearCalculator, interestCalculationGraceOnRepaymentPeriodFraction,
                            totalInterest.zero(), totalInterest.zero(), totalInterest.zero(), totalInterest.zero(), outstanding,
                            loanApplicationTerms, periodNumberTemp, mc, mergeVariationsToMap(params), params.getCompoundingMap(),
                            params.getPeriodStartDate(), transactionDate, applicableVariations);

                    Money interest = principalInterestForThisPeriod.interest();
                    totalInterest = totalInterest.plus(interest);

                    LoanScheduleModelRepaymentPeriod installment = LoanScheduleModelRepaymentPeriod.repayment(params.getInstalmentNumber(),
                            startDate, transactionDate, totalInterest.zero(), totalInterest.zero(), totalInterest, totalInterest.zero(),
                            totalInterest.zero(), totalInterest, true);
                    params.incrementInstalmentNumber();
                    periods.add(installment);
                    totalCumulativeInterest = totalCumulativeInterest.plus(totalInterest);
                    totalInterest = totalInterest.zero();
                    addLoanRepaymentScheduleInstallment(params.getInstallments(), installment);
                    params.setPeriodStartDate(transactionDate);
                    startDate = transactionDate;
                }
                loanRepaymentScheduleTransactionProcessor.handleRepaymentSchedule(currentTransactions, currency, params.getInstallments());
                updateLatePaymentsToMap(loanApplicationTerms, holidayDetailDTO, currency, params.getLatePaymentMap(), currentDate,
                        params.getInstallments(), false, lastRestDate, params.getCompoundingMap());
                outstanding = outstanding.zero();
                outstanding = updateOutstandingFromLatePayment(params.getPeriodStartDate(), params.getLatePaymentMap(), outstanding);
                outstanding = updateBalanceForInterestCalculation(params.getPrincipalPortionMap(), params.getPeriodStartDate(),
                        outstanding, false);
                if (params.getLatePaymentMap().isEmpty() && !outstanding.isGreaterThanZero()) {
                    break;
                }
            }

            if (outstanding.isGreaterThanZero()) {
                PrincipalInterest principalInterestForThisPeriod = calculatePrincipalInterestComponentsForPeriod(
                        this.paymentPeriodsInOneYearCalculator, interestCalculationGraceOnRepaymentPeriodFraction, totalInterest.zero(),
                        totalInterest.zero(), totalInterest.zero(), totalInterest.zero(), outstanding, loanApplicationTerms,
                        periodNumberTemp, mc, mergeVariationsToMap(params), params.getCompoundingMap(), params.getPeriodStartDate(),
                        params.getActualRepaymentDate(), applicableVariations);
                Money interest = principalInterestForThisPeriod.interest();
                totalInterest = totalInterest.plus(interest);
                if (loanApplicationTerms.getInterestRecalculationCompoundingMethod().isInterestCompoundingEnabled()) {
                    LocalDate compoundingEffectiveDate = getNextCompoundScheduleDate(params.getActualRepaymentDate().minusDays(1),
                            loanApplicationTerms, holidayDetailDTO);
                    params.getLatePaymentMap().put(compoundingEffectiveDate, interest);

                }
            }
            params.setPeriodStartDate(params.getActualRepaymentDate());
        } while (params.getActualRepaymentDate().isBefore(currentDate) && outstanding.isGreaterThanZero());

        if (totalInterest.isGreaterThanZero()) {
            LoanScheduleModelRepaymentPeriod installment = LoanScheduleModelRepaymentPeriod.repayment(params.getInstalmentNumber(),
                    startDate, params.getActualRepaymentDate(), totalInterest.zero(), totalInterest.zero(), totalInterest,
                    totalInterest.zero(), totalInterest.zero(), totalInterest, true);
            params.incrementInstalmentNumber();
            periods.add(installment);
            totalCumulativeInterest = totalCumulativeInterest.plus(totalInterest);
        }
        return totalCumulativeInterest;
    }

    private Collection<RecalculationDetail> getApplicableTransactionsForPeriod(final boolean applyInterestRecalculation,
            LocalDate repaymentDate, final Collection<RecalculationDetail> transactions) {
        Collection<RecalculationDetail> applicableTransactions = new ArrayList<>();
        if (applyInterestRecalculation) {
            for (RecalculationDetail detail : transactions) {
                if (!detail.getTransactionDate().isAfter(repaymentDate)) {
                    applicableTransactions.add(detail);
                }
            }
            transactions.removeAll(applicableTransactions);
        }
        return applicableTransactions;
    }

    private Collection<LoanTermVariationsData> getApplicableTermVariationsForPeriod(final LocalDate fromDate, final LocalDate dueDate,
            final Collection<LoanTermVariationsData> variations) {
        Collection<LoanTermVariationsData> applicableVariations = new ArrayList<>();
        for (LoanTermVariationsData detail : variations) {
            if (detail.isApplicable(fromDate, dueDate)) {
                applicableVariations.add(detail);
            }
        }
        variations.removeAll(applicableVariations);
        return applicableVariations;
    }

    private List<LoanTransaction> createCurrentTransactionList(RecalculationDetail detail) {
        List<LoanTransaction> currentTransactions = new ArrayList<>(2);
        currentTransactions.add(detail.getTransaction());
        detail.setProcessed(true);
        return currentTransactions;
    }

    private Money updateOutstandingFromLatePayment(LocalDate periodStartDate, Map<LocalDate, Money> latePaymentMap, Money outstanding) {
        Map<LocalDate, Money> retainEntries = new HashMap<>();
        for (Map.Entry<LocalDate, Money> mapEntry : latePaymentMap.entrySet()) {
            if (!mapEntry.getKey().isAfter(periodStartDate)) {
                outstanding = outstanding.plus(mapEntry.getValue());
            } else {
                retainEntries.put(mapEntry.getKey(), mapEntry.getValue());
            }
        }
        latePaymentMap.clear();
        latePaymentMap.putAll(retainEntries);
        retainEntries.clear();
        return outstanding;
    }

    /**
     * method applies early payment strategy as per the configurations provided
     */
    private Money applyEarlyPaymentStrategy(final LoanApplicationTerms loanApplicationTerms, Money reducePrincipal,
            final Money totalCumulativePrincipal, int periodNumber, final MathContext mc) {
        if (reducePrincipal.isGreaterThanZero()) {
            switch (loanApplicationTerms.getRescheduleStrategyMethod()) {
                case REDUCE_EMI_AMOUNT:
                    adjustInstallmentOrPrincipalAmount(loanApplicationTerms, totalCumulativePrincipal, periodNumber, mc);
                    reducePrincipal = reducePrincipal.zero();
                break;
                case REDUCE_NUMBER_OF_INSTALLMENTS:
                    // number of installments will reduce but emi amount won't
                    // get effected
                    reducePrincipal = reducePrincipal.zero();
                break;
                case RESCHEDULE_NEXT_REPAYMENTS:
                // will reduce principal from the reduce Principal for each
                // installment(means installments will have less emi amount)
                // until this
                // amount becomes zero
                break;
                default:
                break;
            }
        }
        return reducePrincipal;
    }

    private void adjustInstallmentOrPrincipalAmount(final LoanApplicationTerms loanApplicationTerms, final Money totalCumulativePrincipal,
            int periodNumber, final MathContext mc) {
        // in this case emi amount will be reduced but number of
        // installments won't change
        Money principal = getPrincipalToBeScheduled(loanApplicationTerms);
        if (!principal.minus(totalCumulativePrincipal).isGreaterThanZero()) { return; }
        if (loanApplicationTerms.getAmortizationMethod().isEqualPrincipal()) {
            loanApplicationTerms.updateFixedPrincipalAmount(mc, periodNumber, principal.minus(totalCumulativePrincipal));
        } else if (loanApplicationTerms.getActualFixedEmiAmount() == null) {
            loanApplicationTerms.setFixedEmiAmount(null);
            updateFixedInstallmentAmount(mc, loanApplicationTerms, periodNumber, principal.minus(totalCumulativePrincipal));
        }

    }

    /**
     * Identifies all the past date principal changes and apply them on
     * outstanding balance for future calculations
     */
    private Money updateBalanceForInterestCalculation(final Map<LocalDate, Money> principalPortionMap, final LocalDate scheduledDueDate,
            final Money outstandingBalanceAsPerRest, boolean addMapDetails) {
        List<LocalDate> removeFromprincipalPortionMap = new ArrayList<>();
        Money outstandingBalance = outstandingBalanceAsPerRest;
        for (Map.Entry<LocalDate, Money> principal : principalPortionMap.entrySet()) {
            if (!principal.getKey().isAfter(scheduledDueDate)) {
                if (addMapDetails) {
                    outstandingBalance = outstandingBalance.plus(principal.getValue());
                } else {
                    outstandingBalance = outstandingBalance.minus(principal.getValue());
                }
                removeFromprincipalPortionMap.add(principal.getKey());
            }
        }
        for (LocalDate date : removeFromprincipalPortionMap) {
            principalPortionMap.remove(date);
        }
        return outstandingBalance;
    }

    // this is to make sure even paid late payments(principal and compounded
    // interest/fee) should be reduced as per rest date
    private void updateLatePaidAmountsToPrincipalMap(final LoanTransaction loanTransaction, final LoanApplicationTerms applicationTerms,
            final MonetaryCurrency currency, final HolidayDetailDTO holidayDetailDTO, final LocalDate lastRestDate,
            final LoanScheduleParams params) {
        LocalDate applicableDate = getNextRestScheduleDate(loanTransaction.getTransactionDate().minusDays(1), applicationTerms,
                holidayDetailDTO);

        Money principalPortion = loanTransaction.getPrincipalPortion(currency);
        Money compoundedLatePayments = Money.zero(currency);
        if (applicationTerms.getInterestRecalculationCompoundingMethod().isInterestCompoundingEnabled()) {
            compoundedLatePayments = compoundedLatePayments.plus(loanTransaction.getInterestPortion(currency));
        }
        if (applicationTerms.getInterestRecalculationCompoundingMethod().isFeeCompoundingEnabled()) {
            compoundedLatePayments = compoundedLatePayments.plus(loanTransaction.getFeeChargesPortion(currency)).plus(
                    loanTransaction.getPenaltyChargesPortion(currency));
        }

        updateCompoundingAmount(params.getPrincipalPortionMap(), params.getLatePaymentMap(), currency, lastRestDate, principalPortion,
                applicableDate);
        updateCompoundingAmount(params.getPrincipalPortionMap(), params.getCompoundingMap(), currency, lastRestDate,
                compoundedLatePayments, applicableDate);
    }

    private void updateCompoundingAmount(final Map<LocalDate, Money> principalVariationMap,
            final Map<LocalDate, Money> latePaymentCompoundingMap, final MonetaryCurrency currency, final LocalDate lastRestDate,
            Money compoundedPortion, final LocalDate applicableDate) {
        Money appliedOnPrincipalVariationMap = Money.zero(currency);
        Map<LocalDate, Money> temp = new HashMap<>();
        for (LocalDate date : latePaymentCompoundingMap.keySet()) {
            if (date.isBefore(lastRestDate)) {
                Money money = latePaymentCompoundingMap.get(date);
                appliedOnPrincipalVariationMap = appliedOnPrincipalVariationMap.plus(money);
                if (appliedOnPrincipalVariationMap.isLessThan(compoundedPortion)) {
                    if (date.isBefore(applicableDate)) {
                        updateMapWithAmount(principalVariationMap, money.negated(), date);
                        updateMapWithAmount(principalVariationMap, money, applicableDate);
                    }
                } else if (temp.isEmpty()) {
                    Money diff = money.minus(appliedOnPrincipalVariationMap.minus(compoundedPortion));
                    updateMapWithAmount(principalVariationMap, diff.negated(), date);
                    updateMapWithAmount(principalVariationMap, diff, applicableDate);
                    updateMapWithAmount(temp, money.minus(diff), date);
                    updateMapWithAmount(temp, money.minus(diff).negated(), lastRestDate);
                } else {
                    updateMapWithAmount(temp, money, date);
                    updateMapWithAmount(temp, money.negated(), lastRestDate);
                }
            }
        }
        latePaymentCompoundingMap.clear();
        latePaymentCompoundingMap.putAll(temp);
    }

    /**
     * this Method updates late/ not paid installment components to Map with
     * effective date as per REST(for principal portion ) and compounding
     * (interest or fee or interest and fee portions) frequency
     * 
     */
    private void updateLatePaymentsToMap(final LoanApplicationTerms loanApplicationTerms, final HolidayDetailDTO holidayDetailDTO,
            final MonetaryCurrency currency, final Map<LocalDate, Money> latePaymentMap, final LocalDate scheduledDueDate,
            List<LoanRepaymentScheduleInstallment> installments, boolean applyRestFrequencyForPrincipal, final LocalDate lastRestDate,
            final TreeMap<LocalDate, Money> compoundingMap) {
        latePaymentMap.clear();
        LocalDate currentDate = DateUtils.getLocalDateOfTenant();

        Money totalCompoundingAmount = Money.zero(currency);
        Money compoundedMoney = Money.zero(currency);
        if (!compoundingMap.isEmpty()) {
            compoundedMoney = compoundingMap.get(lastRestDate);
        }
        boolean clearCompoundingMap = true;
        for (LoanRepaymentScheduleInstallment loanRepaymentScheduleInstallment : installments) {
            if (loanRepaymentScheduleInstallment.isNotFullyPaidOff()
                    && !loanRepaymentScheduleInstallment.getDueDate().isAfter(scheduledDueDate)
                    && !loanRepaymentScheduleInstallment.isRecalculatedInterestComponent()) {
                LocalDate principalEffectiveDate = loanRepaymentScheduleInstallment.getDueDate();
                if (applyRestFrequencyForPrincipal) {
                    principalEffectiveDate = getNextRestScheduleDate(loanRepaymentScheduleInstallment.getDueDate().minusDays(1),
                            loanApplicationTerms, holidayDetailDTO);
                }
                if (principalEffectiveDate.isBefore(currentDate)) {
                    updateMapWithAmount(latePaymentMap, loanRepaymentScheduleInstallment.getPrincipalOutstanding(currency),
                            principalEffectiveDate);
                    totalCompoundingAmount = totalCompoundingAmount
                            .plus(loanRepaymentScheduleInstallment.getPrincipalOutstanding(currency));
                }

                final Money changedCompoundedMoney = updateMapWithCompoundingDetails(loanApplicationTerms, holidayDetailDTO, currency,
                        compoundingMap, loanRepaymentScheduleInstallment, lastRestDate, compoundedMoney, scheduledDueDate);
                if (compoundedMoney.isZero() || !compoundedMoney.isEqualTo(changedCompoundedMoney)) {
                    compoundedMoney = changedCompoundedMoney;
                    clearCompoundingMap = false;
                }
            }
        }
        if (totalCompoundingAmount.isGreaterThanZero()) {
            updateMapWithAmount(latePaymentMap, totalCompoundingAmount.negated(), lastRestDate);
        }
        if (clearCompoundingMap) {
            compoundingMap.clear();
        }
    }

    private Money updateMapWithCompoundingDetails(final LoanApplicationTerms loanApplicationTerms, final HolidayDetailDTO holidayDetailDTO,
            final MonetaryCurrency currency, final TreeMap<LocalDate, Money> compoundingMap,
            final LoanRepaymentScheduleInstallment loanRepaymentScheduleInstallment, final LocalDate lastRestDate,
            final Money compoundedMoney, final LocalDate scheduledDueDate) {
        Money ignoreMoney = compoundedMoney;
        if (loanApplicationTerms.getInterestRecalculationCompoundingMethod().isCompoundingEnabled()) {
            LocalDate compoundingEffectiveDate = getNextCompoundScheduleDate(loanRepaymentScheduleInstallment.getDueDate().minusDays(1),
                    loanApplicationTerms, holidayDetailDTO);

            if (compoundingEffectiveDate.isBefore(DateUtils.getLocalDateOfTenant())) {
                Money amount = Money.zero(currency);
                switch (loanApplicationTerms.getInterestRecalculationCompoundingMethod()) {
                    case INTEREST:
                        amount = amount.plus(loanRepaymentScheduleInstallment.getInterestOutstanding(currency));
                    break;
                    case FEE:
                        amount = amount.plus(loanRepaymentScheduleInstallment.getFeeChargesOutstanding(currency));
                        amount = amount.plus(loanRepaymentScheduleInstallment.getPenaltyChargesOutstanding(currency));
                    break;
                    case INTEREST_AND_FEE:
                        amount = amount.plus(loanRepaymentScheduleInstallment.getInterestOutstanding(currency));
                        amount = amount.plus(loanRepaymentScheduleInstallment.getFeeChargesOutstanding(currency));
                        amount = amount.plus(loanRepaymentScheduleInstallment.getPenaltyChargesOutstanding(currency));
                    break;
                    default:
                    break;
                }
                if (compoundingEffectiveDate.isBefore(scheduledDueDate)) {
                    ignoreMoney = ignoreMoney.plus(amount);
                    if (ignoreMoney.isGreaterThanZero()) {
                        updateMapWithAmount(compoundingMap, ignoreMoney, compoundingEffectiveDate);
                        updateMapWithAmount(compoundingMap, ignoreMoney.negated(), lastRestDate);
                        ignoreMoney = ignoreMoney.zero();
                    }
                } else {
                    if (ignoreMoney.isLessThanZero()) {
                        LocalDate firstKey = compoundingMap.firstKey();
                        updateMapWithAmount(compoundingMap, ignoreMoney, firstKey);
                        updateMapWithAmount(compoundingMap, ignoreMoney.negated(), lastRestDate);
                        ignoreMoney = ignoreMoney.zero();
                    }
                    updateMapWithAmount(compoundingMap, amount, compoundingEffectiveDate);
                    updateMapWithAmount(compoundingMap, amount.negated(), lastRestDate);
                }
            }
        }
        return ignoreMoney;
    }

    private void populateCompoundingDatesInPeriod(final LocalDate startDate, final LocalDate endDate, final LocalDate currentDate,
            final LoanApplicationTerms loanApplicationTerms, final HolidayDetailDTO holidayDetailDTO,
            final Map<LocalDate, Money> compoundingMap, final Set<LoanCharge> charges, MonetaryCurrency currency) {
        if (loanApplicationTerms.getInterestRecalculationCompoundingMethod().isCompoundingEnabled()) {
            LocalDate lastCompoundingDate = startDate;
            LocalDate compoundingDate = startDate;
            while (compoundingDate.isBefore(endDate) && compoundingDate.isBefore(currentDate)) {
                compoundingDate = getNextCompoundScheduleDate(compoundingDate, loanApplicationTerms, holidayDetailDTO);
                if (!compoundingDate.isBefore(currentDate)) {
                    break;
                } else if (compoundingDate.isAfter(endDate)) {
                    updateMapWithAmount(compoundingMap, Money.zero(currency), compoundingDate);
                } else {
                    Money feeChargesForInstallment = cumulativeFeeChargesDueWithin(lastCompoundingDate, compoundingDate, charges, currency,
                            null, loanApplicationTerms.getPrincipal(), null, false);
                    Money penaltyChargesForInstallment = cumulativePenaltyChargesDueWithin(lastCompoundingDate, compoundingDate, charges,
                            currency, null, loanApplicationTerms.getPrincipal(), null, false);
                    updateMapWithAmount(compoundingMap, feeChargesForInstallment.plus(penaltyChargesForInstallment), compoundingDate);
                }
                lastCompoundingDate = compoundingDate;
            }
        }
    }

    protected void clearMapDetails(final LocalDate startDate, final Map<LocalDate, Money> compoundingMap) {
        Map<LocalDate, Money> temp = new HashMap<>();
        for (LocalDate date : compoundingMap.keySet()) {
            if (!date.isBefore(startDate)) {
                temp.put(date, compoundingMap.get(date));
            }
        }
        compoundingMap.clear();
        compoundingMap.putAll(temp);
    }

    /**
     * This Method updates principal paid component to map with effective date
     * as per the REST
     * 
     */
    private void updatePrincipalPaidPortionToMap(final LoanApplicationTerms loanApplicationTerms, final HolidayDetailDTO holidayDetailDTO,
            Map<LocalDate, Money> principalPortionMap, final LoanScheduleModelPeriod installment, final RecalculationDetail detail,
            final Money unprocessed, final List<LoanRepaymentScheduleInstallment> installments) {
        LocalDate applicableDate = getNextRestScheduleDate(detail.getTransactionDate().minusDays(1), loanApplicationTerms, holidayDetailDTO);
        updateMapWithAmount(principalPortionMap, unprocessed, applicableDate);
        installment.addPrincipalAmount(unprocessed);
        LoanRepaymentScheduleInstallment lastInstallment = installments.get(installments.size() - 1);
        lastInstallment.updatePrincipal(lastInstallment.getPrincipal(unprocessed.getCurrency()).plus(unprocessed).getAmount());
        lastInstallment.payPrincipalComponent(detail.getTransactionDate(), unprocessed);
    }

    /**
     * merges all the applicable amounts(compounding dates, disbursements, late
     * payment compounding and principal change as per rest) changes to single
     * map for interest calculation
     * 
     */
    private TreeMap<LocalDate, Money> mergeVariationsToMap(final LoanScheduleParams params) {
        TreeMap<LocalDate, Money> map = new TreeMap<>();
        map.putAll(params.getLatePaymentMap());
        for (Map.Entry<LocalDate, Money> mapEntry : params.getDisburseDetailMap().entrySet()) {
            Money value = mapEntry.getValue();
            if (map.containsKey(mapEntry.getKey())) {
                value = value.plus(map.get(mapEntry.getKey()));
            }
            map.put(mapEntry.getKey(), value);
        }

        for (Map.Entry<LocalDate, Money> mapEntry : params.getPrincipalPortionMap().entrySet()) {
            Money value = mapEntry.getValue().negated();
            if (map.containsKey(mapEntry.getKey())) {
                value = value.plus(map.get(mapEntry.getKey()));
            }
            map.put(mapEntry.getKey(), value);
        }

        for (Map.Entry<LocalDate, Money> mapEntry : params.getCompoundingMap().entrySet()) {
            Money value = mapEntry.getValue();
            if (!map.containsKey(mapEntry.getKey())) {
                map.put(mapEntry.getKey(), value.zero());
            }
        }

        return map;
    }

    /**
     * calculates Interest stating date as per the settings
     * 
     * @param firstRepaymentdate
     *            TODO
     */
    private LocalDate calculateInterestStartDateForPeriod(final LoanApplicationTerms loanApplicationTerms, LocalDate periodStartDate,
            final LocalDate idealDisbursementDate, final LocalDate firstRepaymentdate) {
        LocalDate periodStartDateApplicableForInterest = periodStartDate;
        if (periodStartDate.isBefore(idealDisbursementDate) || firstRepaymentdate.isAfter(periodStartDate)) {
            if (loanApplicationTerms.getInterestChargedFromLocalDate() != null) {
                if (periodStartDate.isEqual(loanApplicationTerms.getExpectedDisbursementDate())
                        || loanApplicationTerms.getInterestChargedFromLocalDate().isAfter(periodStartDate)) {
                    periodStartDateApplicableForInterest = loanApplicationTerms.getInterestChargedFromLocalDate();
                }
            } else if (periodStartDate.isEqual(loanApplicationTerms.getExpectedDisbursementDate())) {
                periodStartDateApplicableForInterest = idealDisbursementDate;
            }
        }
        return periodStartDateApplicableForInterest;
    }

    private void updateMapWithAmount(final Map<LocalDate, Money> map, final Money amount, final LocalDate amountApplicableDate) {
        Money principalPaid = amount;
        if (map.containsKey(amountApplicableDate)) {
            principalPaid = map.get(amountApplicableDate).plus(principalPaid);
        }
        map.put(amountApplicableDate, principalPaid);
    }

    private Money getTotalAmount(final Map<LocalDate, Money> map, final MonetaryCurrency currency) {
        Money total = Money.zero(currency);
        for (Map.Entry<LocalDate, Money> mapEntry : map.entrySet()) {
            if (mapEntry.getKey().isBefore(DateUtils.getLocalDateOfTenant())) {
                total = total.plus(mapEntry.getValue());
            }
        }
        return total;
    }

    @Override
    public LoanRescheduleModel reschedule(final MathContext mathContext, final LoanRescheduleRequest loanRescheduleRequest,
            final ApplicationCurrency applicationCurrency, final HolidayDetailDTO holidayDetailDTO,
            final CalendarInstance restCalendarInstance, final CalendarInstance compoundingCalendarInstance, final Calendar loanCalendar,
            final FloatingRateDTO floatingRateDTO) {

        final Loan loan = loanRescheduleRequest.getLoan();
        final LoanSummary loanSummary = loan.getSummary();
        final LoanProductMinimumRepaymentScheduleRelatedDetail loanProductRelatedDetail = loan.getLoanRepaymentScheduleDetail();
        final MonetaryCurrency currency = loanProductRelatedDetail.getCurrency();

        // create an archive of the current loan schedule installments
        Collection<LoanRepaymentScheduleHistory> loanRepaymentScheduleHistoryList = null;

        // get the initial list of repayment installments
        List<LoanRepaymentScheduleInstallment> repaymentScheduleInstallments = loan.getRepaymentScheduleInstallments();

        // sort list by installment number in ASC order
        Collections.sort(repaymentScheduleInstallments, LoanRepaymentScheduleInstallment.installmentNumberComparator);

        final Collection<LoanRescheduleModelRepaymentPeriod> periods = new ArrayList<>();

        Money outstandingLoanBalance = loan.getPrincpal();

        for (LoanRepaymentScheduleInstallment repaymentScheduleInstallment : repaymentScheduleInstallments) {

            Integer oldPeriodNumber = repaymentScheduleInstallment.getInstallmentNumber();
            LocalDate fromDate = repaymentScheduleInstallment.getFromDate();
            LocalDate dueDate = repaymentScheduleInstallment.getDueDate();
            Money principalDue = repaymentScheduleInstallment.getPrincipal(currency);
            Money interestDue = repaymentScheduleInstallment.getInterestCharged(currency);
            Money feeChargesDue = repaymentScheduleInstallment.getFeeChargesCharged(currency);
            Money penaltyChargesDue = repaymentScheduleInstallment.getPenaltyChargesCharged(currency);
            Money totalDue = principalDue.plus(interestDue).plus(feeChargesDue).plus(penaltyChargesDue);

            outstandingLoanBalance = outstandingLoanBalance.minus(principalDue);

            LoanRescheduleModelRepaymentPeriod period = LoanRescheduleModelRepaymentPeriod
                    .instance(oldPeriodNumber, oldPeriodNumber, fromDate, dueDate, principalDue, outstandingLoanBalance, interestDue,
                            feeChargesDue, penaltyChargesDue, totalDue, false);

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

        if (rescheduleFromInstallmentNo > 0) {
            // this will hold the loan repayment installment that is before the
            // reschedule start installment
            // (rescheduleFrominstallment)
            LoanRepaymentScheduleInstallment previousInstallment = null;

            // get the install number of the previous installment
            int previousInstallmentNo = rescheduleFromInstallmentNo - 1;

            // only fetch the installment if the number is greater than 0
            if (previousInstallmentNo > 0) {
                previousInstallment = loan.fetchRepaymentScheduleInstallment(previousInstallmentNo);
            }

            LoanRepaymentScheduleInstallment firstInstallment = loan.fetchRepaymentScheduleInstallment(1);

            // the "installment from date" is equal to the due date of the
            // previous installment, if it exists
            if (previousInstallment != null) {
                installmentFromDate = previousInstallment.getDueDate();
            }

            else {
                installmentFromDate = firstInstallment.getFromDate();
            }

            installmentDueDate = installmentFromDate;
            LocalDate periodStartDateApplicableForInterest = installmentFromDate;
            Integer periodNumber = 1;
            outstandingLoanBalance = loan.getPrincpal();

            for (LoanRescheduleModelRepaymentPeriod period : periods) {

                if (period.periodDueDate().isBefore(loanRescheduleRequest.getRescheduleFromDate())) {

                    totalPrincipalBeforeReschedulePeriod = totalPrincipalBeforeReschedulePeriod.plus(period.principalDue());
                    actualTotalCumulativeInterest = actualTotalCumulativeInterest.plus(period.interestDue());
                    rescheduleNumberOfRepayments--;
                    outstandingLoanBalance = outstandingLoanBalance.minus(period.principalDue());
                    outstandingBalance = outstandingBalance.minus(period.principalDue());
                }
            }

            while (graceOnPrincipal > 0 || graceOnInterest > 0) {

                LoanRescheduleModelRepaymentPeriod period = LoanRescheduleModelRepaymentPeriod.instance(0, 0, new LocalDate(),
                        new LocalDate(), Money.zero(currency), Money.zero(currency), Money.zero(currency), Money.zero(currency),
                        Money.zero(currency), Money.zero(currency), true);

                periods.add(period);

                if (graceOnPrincipal > 0) {
                    graceOnPrincipal--;
                }

                if (graceOnInterest > 0) {
                    graceOnInterest--;
                }

                rescheduleNumberOfRepayments++;
                numberOfRepayments++;
            }

            while (extraTerms > 0) {

                LoanRescheduleModelRepaymentPeriod period = LoanRescheduleModelRepaymentPeriod.instance(0, 0, new LocalDate(),
                        new LocalDate(), Money.zero(currency), Money.zero(currency), Money.zero(currency), Money.zero(currency),
                        Money.zero(currency), Money.zero(currency), true);

                periods.add(period);

                extraTerms--;
                rescheduleNumberOfRepayments++;
                numberOfRepayments++;
            }

            // get the loan application terms from the Loan object
            final LoanApplicationTerms loanApplicationTerms = loan.getLoanApplicationTerms(applicationCurrency, restCalendarInstance,
                    compoundingCalendarInstance, loanCalendar, floatingRateDTO);

            // for applying variations
            Collection<LoanTermVariationsData> loanTermVariations = loanApplicationTerms.getLoanTermVariations().getInterestRateChanges();

            // update the number of repayments
            loanApplicationTerms.updateNumberOfRepayments(numberOfRepayments);

            LocalDate loanEndDate = this.scheduledDateGenerator.getLastRepaymentDate(loanApplicationTerms, holidayDetailDTO);
            LoanTermVariationsData lastDueDateVariation = loanApplicationTerms.getLoanTermVariations().fetchLoanTermDueDateVariationsData(
                    loanEndDate);
            if (lastDueDateVariation != null) {
                loanEndDate = lastDueDateVariation.getDateValue();
            }
            loanApplicationTerms.updateLoanEndDate(loanEndDate);

            if (newInterestRate != null) {
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

            if (!recalculateInterest && newInterestRate == null) {
                totalInterestChargedForFullLoanTerm = Money.of(currency, loanSummary.getTotalInterestCharged());
                totalInterestChargedForFullLoanTerm = totalInterestChargedForFullLoanTerm.minus(actualTotalCumulativeInterest);

                loanApplicationTerms.updateTotalInterestDue(totalInterestChargedForFullLoanTerm);
            }

            for (LoanRescheduleModelRepaymentPeriod period : periods) {

                if (period.periodDueDate().isEqual(loanRescheduleRequest.getRescheduleFromDate())
                        || period.periodDueDate().isAfter(loanRescheduleRequest.getRescheduleFromDate()) || period.isNew()) {

                    installmentDueDate = this.scheduledDateGenerator.generateNextRepaymentDate(installmentDueDate, loanApplicationTerms,
                            false, holidayDetailDTO);

                    if (adjustedDueDate != null && periodNumber == 1) {
                        installmentDueDate = adjustedDueDate;
                    }

                    adjustedInstallmentDueDate = this.scheduledDateGenerator.adjustRepaymentDate(installmentDueDate, loanApplicationTerms,
                            holidayDetailDTO);

                    final int daysInInstallment = Days.daysBetween(installmentFromDate, adjustedInstallmentDueDate).getDays();

                    period.updatePeriodNumber(installmentNumber);
                    period.updatePeriodFromDate(installmentFromDate);
                    period.updatePeriodDueDate(adjustedInstallmentDueDate);

                    double interestCalculationGraceOnRepaymentPeriodFraction = this.paymentPeriodsInOneYearCalculator
                            .calculatePortionOfRepaymentPeriodInterestChargingGrace(periodStartDateApplicableForInterest,
                                    adjustedInstallmentDueDate, periodStartDateApplicableForInterest,
                                    loanApplicationTerms.getLoanTermPeriodFrequencyType(), loanApplicationTerms.getRepaymentEvery());

                    // ========================= Calculate the interest due
                    // ========================================

                    // change the principal to => Principal Disbursed - Total
                    // Principal Paid
                    // interest calculation is always based on the total
                    // principal outstanding
                    loanApplicationTerms.setPrincipal(totalPrincipalOutstanding);

                    // for applying variations
                    Collection<LoanTermVariationsData> applicableVariations = getApplicableTermVariationsForPeriod(installmentFromDate,
                            adjustedInstallmentDueDate, loanTermVariations);

                    // determine the interest & principal for the period
                    PrincipalInterest principalInterestForThisPeriod = calculatePrincipalInterestComponentsForPeriod(
                            this.paymentPeriodsInOneYearCalculator, interestCalculationGraceOnRepaymentPeriodFraction,
                            totalCumulativePrincipal, totalCumulativeInterest, totalInterestChargedForFullLoanTerm,
                            totalOutstandingInterestPaymentDueToGrace, outstandingBalance, loanApplicationTerms, periodNumber, mathContext,
                            null, null, installmentFromDate, adjustedInstallmentDueDate, applicableVariations);

                    // update the interest due for the period
                    period.updateInterestDue(principalInterestForThisPeriod.interest());

                    // =============================================================================================

                    // ========================== Calculate the principal due
                    // ======================================

                    // change the principal to => Principal Disbursed - Total
                    // cumulative Principal Amount before the reschedule
                    // installment
                    loanApplicationTerms.setPrincipal(principal.minus(totalPrincipalBeforeReschedulePeriod));

                    principalInterestForThisPeriod = calculatePrincipalInterestComponentsForPeriod(this.paymentPeriodsInOneYearCalculator,
                            interestCalculationGraceOnRepaymentPeriodFraction, totalCumulativePrincipal, totalCumulativeInterest,
                            totalInterestChargedForFullLoanTerm, totalOutstandingInterestPaymentDueToGrace, outstandingBalance,
                            loanApplicationTerms, periodNumber, mathContext, null, null, installmentFromDate, adjustedInstallmentDueDate,
                            applicableVariations);

                    period.updatePrincipalDue(principalInterestForThisPeriod.principal());

                    // ==============================================================================================

                    outstandingLoanBalance = outstandingLoanBalance.minus(period.principalDue());
                    period.updateOutstandingLoanBalance(outstandingLoanBalance);

                    Money principalDue = Money.of(currency, period.principalDue());
                    Money interestDue = Money.of(currency, period.interestDue());

                    if (principalDue.isZero() && interestDue.isZero()) {
                        period.updateFeeChargesDue(Money.zero(currency));
                        period.updatePenaltyChargesDue(Money.zero(currency));
                    }

                    Money feeChargesDue = Money.of(currency, period.feeChargesDue());
                    Money penaltyChargesDue = Money.of(currency, period.penaltyChargesDue());

                    Money totalDue = principalDue.plus(interestDue).plus(feeChargesDue).plus(penaltyChargesDue);

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

        final Money totalRepaymentExpected = principal // get the loan Principal
                                                       // amount
                .plus(actualTotalCumulativeInterest) // add the actual total
                                                     // cumulative interest
                .plus(loanSummary.getTotalFeeChargesCharged()) // add the total
                                                               // fees charged
                .plus(loanSummary.getTotalPenaltyChargesCharged()); // finally
                                                                    // add the
                                                                    // total
                                                                    // penalty
                                                                    // charged

        return LoanRescheduleModel.instance(periods, loanRepaymentScheduleHistoryList, applicationCurrency, loanTermInDays,
                loan.getPrincpal(), loan.getPrincpal().getAmount(), loanSummary.getTotalPrincipalRepaid(),
                actualTotalCumulativeInterest.getAmount(), loanSummary.getTotalFeeChargesCharged(),
                loanSummary.getTotalPenaltyChargesCharged(), totalRepaymentExpected.getAmount(), loanSummary.getTotalOutstanding());
    }

    public abstract PrincipalInterest calculatePrincipalInterestComponentsForPeriod(PaymentPeriodsInOneYearCalculator calculator,
            double interestCalculationGraceOnRepaymentPeriodFraction, Money totalCumulativePrincipal, Money totalCumulativeInterest,
            Money totalInterestDueForLoan, Money cumulatingInterestPaymentDueToGrace, Money outstandingBalance,
            LoanApplicationTerms loanApplicationTerms, int periodNumber, MathContext mc, TreeMap<LocalDate, Money> principalVariation,
            Map<LocalDate, Money> compoundingMap, LocalDate periodStartDate, LocalDate periodEndDate,
            Collection<LoanTermVariationsData> termVariations);

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

    private BigDecimal getDisbursementAmount(final LoanApplicationTerms loanApplicationTerms, LocalDate disbursementDate,
            final Collection<LoanScheduleModelPeriod> periods, final BigDecimal chargesDueAtTimeOfDisbursement,
            final Map<LocalDate, Money> disurseDetail, final boolean excludePastUndisbursed) {
        BigDecimal principal = BigDecimal.ZERO;
        MonetaryCurrency currency = loanApplicationTerms.getPrincipal().getCurrency();
        for (DisbursementData disbursementData : loanApplicationTerms.getDisbursementDatas()) {
            if (disbursementData.disbursementDate().equals(disbursementDate)) {
                final LoanScheduleModelDisbursementPeriod disbursementPeriod = LoanScheduleModelDisbursementPeriod.disbursement(
                        disbursementData.disbursementDate(), Money.of(currency, disbursementData.amount()), chargesDueAtTimeOfDisbursement);
                periods.add(disbursementPeriod);
                principal = principal.add(disbursementData.amount());
            } else if (!excludePastUndisbursed || disbursementData.isDisbursed()
                    || !disbursementData.disbursementDate().isBefore(DateUtils.getLocalDateOfTenant())) {
                disurseDetail.put(disbursementData.disbursementDate(), Money.of(currency, disbursementData.amount()));
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

    private Set<LoanCharge> seperateTotalCompoundingPercentageCharges(final Set<LoanCharge> loanCharges) {
        Set<LoanCharge> interestCharges = new HashSet<>();
        for (final LoanCharge loanCharge : loanCharges) {
            if (loanCharge.isSpecifiedDueDate()
                    && (loanCharge.getChargeCalculation().isPercentageOfInterest() || loanCharge.getChargeCalculation()
                            .isPercentageOfAmountAndInterest())) {
                interestCharges.add(loanCharge);
            }
        }
        loanCharges.removeAll(interestCharges);
        return interestCharges;
    }

    private Money cumulativeFeeChargesDueWithin(final LocalDate periodStart, final LocalDate periodEnd, final Set<LoanCharge> loanCharges,
            final MonetaryCurrency monetaryCurrency, final PrincipalInterest principalInterestForThisPeriod,
            final Money principalDisbursed, final Money totalInterestChargedForFullLoanTerm, boolean isInstallmentChargeApplicable) {

        Money cumulative = Money.zero(monetaryCurrency);

        for (final LoanCharge loanCharge : loanCharges) {
            if (!loanCharge.isDueAtDisbursement() && loanCharge.isFeeCharge()) {
                if (loanCharge.isInstalmentFee() && isInstallmentChargeApplicable) {
                    cumulative = calculateInstallmentCharge(principalInterestForThisPeriod, cumulative, loanCharge);
                } else if (loanCharge.isOverdueInstallmentCharge()
                        && loanCharge.isDueForCollectionFromAndUpToAndIncluding(periodStart, periodEnd)
                        && loanCharge.getChargeCalculation().isPercentageBased()) {
                    cumulative = cumulative.plus(loanCharge.chargeAmount());
                } else if (loanCharge.isDueForCollectionFromAndUpToAndIncluding(periodStart, periodEnd)
                        && loanCharge.getChargeCalculation().isPercentageBased()) {
                    cumulative = calculateSpecificDueDateChargeWithPercentage(principalDisbursed, totalInterestChargedForFullLoanTerm,
                            cumulative, loanCharge);
                } else if (loanCharge.isDueForCollectionFromAndUpToAndIncluding(periodStart, periodEnd)) {
                    cumulative = cumulative.plus(loanCharge.amount());
                }
            }
        }

        return cumulative;
    }

    private Money calculateSpecificDueDateChargeWithPercentage(final Money principalDisbursed,
            final Money totalInterestChargedForFullLoanTerm, Money cumulative, final LoanCharge loanCharge) {
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
        return cumulative;
    }

    private Money calculateInstallmentCharge(final PrincipalInterest principalInterestForThisPeriod, Money cumulative,
            final LoanCharge loanCharge) {
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
            cumulative = cumulative.plus(loanCharge.amountOrPercentage());
        }
        return cumulative;
    }

    private Money cumulativePenaltyChargesDueWithin(final LocalDate periodStart, final LocalDate periodEnd,
            final Set<LoanCharge> loanCharges, final MonetaryCurrency monetaryCurrency,
            final PrincipalInterest principalInterestForThisPeriod, final Money principalDisbursed,
            final Money totalInterestChargedForFullLoanTerm, boolean isInstallmentChargeApplicable) {

        Money cumulative = Money.zero(monetaryCurrency);

        for (final LoanCharge loanCharge : loanCharges) {
            if (loanCharge.isPenaltyCharge()) {
                if (loanCharge.isInstalmentFee() && isInstallmentChargeApplicable) {
                    cumulative = calculateInstallmentCharge(principalInterestForThisPeriod, cumulative, loanCharge);
                } else if (loanCharge.isOverdueInstallmentCharge()
                        && loanCharge.isDueForCollectionFromAndUpToAndIncluding(periodStart, periodEnd)
                        && loanCharge.getChargeCalculation().isPercentageBased()) {
                    cumulative = cumulative.plus(loanCharge.chargeAmount());
                } else if (loanCharge.isDueForCollectionFromAndUpToAndIncluding(periodStart, periodEnd)
                        && loanCharge.getChargeCalculation().isPercentageBased()) {
                    cumulative = calculateSpecificDueDateChargeWithPercentage(principalDisbursed, totalInterestChargedForFullLoanTerm,
                            cumulative, loanCharge);
                } else if (loanCharge.isDueForCollectionFromAndUpToAndIncluding(periodStart, periodEnd)) {
                    cumulative = cumulative.plus(loanCharge.amount());
                }
            }
        }

        return cumulative;
    }

    /**
     * Method preprocess the installments and transactions and sets the required
     * fields to generate the schedule
     */
    @Override
    public LoanScheduleDTO rescheduleNextInstallments(final MathContext mc, final LoanApplicationTerms loanApplicationTerms,
            final Set<LoanCharge> loanCharges, final HolidayDetailDTO holidayDetailDTO, final List<LoanTransaction> transactions,
            final LoanRepaymentScheduleTransactionProcessor loanRepaymentScheduleTransactionProcessor,
            final List<LoanRepaymentScheduleInstallment> repaymentScheduleInstallments, final LocalDate rescheduleFrom) {

        // Fixed schedule End Date for generating schedule
        final LocalDate scheduleTillDate = null;
        return rescheduleNextInstallments(mc, loanApplicationTerms, loanCharges, holidayDetailDTO, transactions,
                loanRepaymentScheduleTransactionProcessor, repaymentScheduleInstallments, rescheduleFrom, scheduleTillDate);

    }

    private LoanScheduleDTO rescheduleNextInstallments(final MathContext mc, final LoanApplicationTerms loanApplicationTerms,
            final Set<LoanCharge> loanCharges, final HolidayDetailDTO holidayDetailDTO, final List<LoanTransaction> transactions,
            final LoanRepaymentScheduleTransactionProcessor loanRepaymentScheduleTransactionProcessor,
            final List<LoanRepaymentScheduleInstallment> repaymentScheduleInstallments, final LocalDate rescheduleFrom,
            final LocalDate scheduleTillDate) {
        // Loan transactions to process and find the variation on payments
        Collection<RecalculationDetail> recalculationDetails = new ArrayList<>();
        for (LoanTransaction loanTransaction : transactions) {
            recalculationDetails.add(new RecalculationDetail(loanTransaction.getTransactionDate(), LoanTransaction
                    .copyTransactionProperties(loanTransaction)));
        }
        final boolean applyInterestRecalculation = loanApplicationTerms.isInterestRecalculationEnabled();

        LoanScheduleParams loanScheduleParams = null;
        Collection<LoanScheduleModelPeriod> periods = new ArrayList<>();
        final List<LoanRepaymentScheduleInstallment> retainedInstallments = new ArrayList<>();

        // this block is to retain the schedule installments prior to the
        // provided date and creates late and early payment details for further
        // calculations
        if (rescheduleFrom != null) {
            Money principalToBeScheduled = getPrincipalToBeScheduled(loanApplicationTerms);
            // actual outstanding balance for interest calculation
            Money outstandingBalance = principalToBeScheduled;
            // total outstanding balance as per rest for interest calculation.
            Money outstandingBalanceAsPerRest = outstandingBalance;

            // this is required to update total fee amounts in the
            // LoanScheduleModel
            final BigDecimal chargesDueAtTimeOfDisbursement = deriveTotalChargesDueAtTimeOfDisbursement(loanCharges);
            periods = createNewLoanScheduleListWithDisbursementDetails(loanApplicationTerms.fetchNumberOfRepaymentsAfterExceptions(),
                    loanApplicationTerms, chargesDueAtTimeOfDisbursement);
            final List<LoanRepaymentScheduleInstallment> newRepaymentScheduleInstallments = new ArrayList<>();
            MonetaryCurrency currency = outstandingBalance.getCurrency();

            // early payments will be added here and as per the selected
            // strategy
            // action will be performed on this value
            Money reducePrincipal = outstandingBalanceAsPerRest.zero();

            // principal changes will be added along with date(after applying
            // rest)
            // from when these amounts will effect the outstanding balance for
            // interest calculation
            final Map<LocalDate, Money> principalPortionMap = new HashMap<>();
            // compounding(principal) amounts will be added along with
            // date(after applying compounding frequency)
            // from when these amounts will effect the outstanding balance for
            // interest calculation
            final Map<LocalDate, Money> latePaymentMap = new HashMap<>();

            // compounding(interest/Fee) amounts will be added along with
            // date(after applying compounding frequency)
            // from when these amounts will effect the outstanding balance for
            // interest calculation
            final TreeMap<LocalDate, Money> compoundingMap = new TreeMap<>();
            LocalDate currentDate = DateUtils.getLocalDateOfTenant();
            LocalDate lastRestDate = currentDate;
            if (loanApplicationTerms.getRestCalendarInstance() != null) {
                lastRestDate = getNextRestScheduleDate(currentDate.minusDays(1), loanApplicationTerms, holidayDetailDTO);
            }
            LocalDate actualRepaymentDate = loanApplicationTerms.getExpectedDisbursementDate();
            boolean isFirstRepayment = true;

            // cumulative fields
            Money totalCumulativePrincipal = principalToBeScheduled.zero();
            Money totalCumulativeInterest = principalToBeScheduled.zero();
            Money totalFeeChargesCharged = principalToBeScheduled.zero().plus(chargesDueAtTimeOfDisbursement);
            Money totalPenaltyChargesCharged = principalToBeScheduled.zero();
            Money totalRepaymentExpected = principalToBeScheduled.zero();

            // Actual period Number as per the schedule
            int periodNumber = 1;
            // Actual period Number plus interest only repayments
            int instalmentNumber = 1;
            LocalDate lastInstallmentDate = actualRepaymentDate;
            LocalDate periodStartDate = loanApplicationTerms.getExpectedDisbursementDate();
            // Set fixed Amortization Amounts(either EMI or Principal )
            updateAmortization(mc, loanApplicationTerms, periodNumber, outstandingBalance);

            final Map<LocalDate, Money> disburseDetailMap = new HashMap<>();
            if (loanApplicationTerms.isMultiDisburseLoan()) {
                // fetches the first tranche amount and also updates other
                // tranche
                // details to map
                BigDecimal disburseAmt = getDisbursementAmount(loanApplicationTerms, loanApplicationTerms.getExpectedDisbursementDate(),
                        periods, chargesDueAtTimeOfDisbursement, disburseDetailMap, true);
                outstandingBalance = outstandingBalance.zero().plus(disburseAmt);
                outstandingBalanceAsPerRest = outstandingBalance;
                principalToBeScheduled = principalToBeScheduled.zero().plus(disburseAmt);
            }
            int loanTermInDays = 0;

            // Block process the installment and creates the period if it falls
            // before reschedule from date
            // This will create the recalculation details by applying the
            // transactions
            for (LoanRepaymentScheduleInstallment installment : repaymentScheduleInstallments) {
                // this will generate the next schedule due date and allows to
                // process the installment only if recalculate from date is
                // greater than due date
                if (installment.getDueDate().isAfter(lastInstallmentDate)) {
                    LocalDate previousRepaymentDate = actualRepaymentDate;
                    actualRepaymentDate = this.scheduledDateGenerator.generateNextRepaymentDate(actualRepaymentDate, loanApplicationTerms,
                            isFirstRepayment, holidayDetailDTO);
                    isFirstRepayment = false;
                    lastInstallmentDate = this.scheduledDateGenerator.adjustRepaymentDate(actualRepaymentDate, loanApplicationTerms,
                            holidayDetailDTO);
                    if (!lastInstallmentDate.isBefore(rescheduleFrom)) {
                        actualRepaymentDate = previousRepaymentDate;
                        break;
                    }
                    periodNumber++;
                    // check for date changes
                    while (loanApplicationTerms.getLoanTermVariations().hasDueDateVariation(lastInstallmentDate)) {
                        LoanTermVariationsData variation = loanApplicationTerms.getLoanTermVariations().nextDueDateVariation();
                        if (!variation.isSpecificToInstallment()) {
                            actualRepaymentDate = variation.getDateValue();
                        }
                        variation.setProcessed(true);
                    }
                }

                for (Map.Entry<LocalDate, Money> disburseDetail : disburseDetailMap.entrySet()) {
                    if (disburseDetail.getKey().isAfter(installment.getFromDate())
                            && !disburseDetail.getKey().isAfter(installment.getDueDate())) {
                        // creates and add disbursement detail to the repayments
                        // period
                        final LoanScheduleModelDisbursementPeriod disbursementPeriod = LoanScheduleModelDisbursementPeriod.disbursement(
                                disburseDetail.getKey(), disburseDetail.getValue(), chargesDueAtTimeOfDisbursement);
                        periods.add(disbursementPeriod);
                        // updates actual outstanding balance with new
                        // disbursement detail
                        outstandingBalance = outstandingBalance.plus(disburseDetail.getValue());
                        principalToBeScheduled = principalToBeScheduled.plus(disburseDetail.getValue());
                    }
                }

                // calculation of basic fields to start the schedule generation
                // from the middle
                periodStartDate = installment.getDueDate();
                installment.resetDerivedComponents();
                newRepaymentScheduleInstallments.add(installment);
                outstandingBalance = outstandingBalance.minus(installment.getPrincipal(currency));
                final LoanScheduleModelPeriod loanScheduleModelPeriod = createLoanScheduleModelPeriod(installment, outstandingBalance);
                periods.add(loanScheduleModelPeriod);
                totalCumulativePrincipal = totalCumulativePrincipal.plus(installment.getPrincipal(currency));
                totalCumulativeInterest = totalCumulativeInterest.plus(installment.getInterestCharged(currency));
                totalFeeChargesCharged = totalFeeChargesCharged.plus(installment.getFeeChargesCharged(currency));
                totalPenaltyChargesCharged = totalPenaltyChargesCharged.plus(installment.getPenaltyChargesCharged(currency));
                instalmentNumber++;
                loanTermInDays = Days.daysBetween(installment.getFromDate(), installment.getDueDate()).getDays();

                // populates the collection with transactions till the due date
                // of
                // the period for interest recalculation enabled loans
                Collection<RecalculationDetail> applicableTransactions = getApplicableTransactionsForPeriod(applyInterestRecalculation,
                        installment.getDueDate(), recalculationDetails);

                // calculates the expected principal value for this repayment
                // schedule
                Money principalPortionCalculated = principalToBeScheduled.zero();
                if (!installment.isRecalculatedInterestComponent()) {
                    principalPortionCalculated = calculateExpectedPrincipalPortion(installment.getInterestCharged(currency),
                            loanApplicationTerms);
                }

                // expected principal considering the previously paid excess
                // amount
                Money actualPrincipalPortion = principalPortionCalculated.minus(reducePrincipal);
                if (actualPrincipalPortion.isLessThanZero()) {
                    actualPrincipalPortion = principalPortionCalculated.zero();
                }

                Money unprocessed = updateEarlyPaidAmountsToMap(loanApplicationTerms, holidayDetailDTO,
                        loanRepaymentScheduleTransactionProcessor, newRepaymentScheduleInstallments, currency, principalPortionMap,
                        installment, applicableTransactions, actualPrincipalPortion);

                // this block is to adjust the period number based on the actual
                // schedule due date and installment due date
                // recalculatedInterestComponent installment shouldn't be
                // considered while calculating fixed EMI amounts
                int period = periodNumber;
                if (!lastInstallmentDate.isEqual(installment.getDueDate())) {
                    period--;
                }
                reducePrincipal = fetchEarlyPaidAmount(installment.getPrincipal(currency), principalPortionCalculated, reducePrincipal,
                        loanApplicationTerms, totalCumulativePrincipal, period, mc);
                // Updates principal paid map with efective date for reducing
                // the amount from outstanding balance(interest calculation)
                LocalDate amountApplicableDate = getNextRestScheduleDate(installment.getDueDate().minusDays(1), loanApplicationTerms,
                        holidayDetailDTO);
                // updates map with the installment principal amount excluding
                // unprocessed amount since this amount is already accounted.
                updateMapWithAmount(principalPortionMap, installment.getPrincipal(currency).minus(unprocessed), amountApplicableDate);
                // update outstanding balance for interest calculation
                outstandingBalanceAsPerRest = updateBalanceForInterestCalculation(principalPortionMap, installment.getDueDate(),
                        outstandingBalanceAsPerRest, false);
                outstandingBalanceAsPerRest = updateBalanceForInterestCalculation(disburseDetailMap, installment.getDueDate(),
                        outstandingBalanceAsPerRest, true);

            }
            totalRepaymentExpected = totalCumulativePrincipal.plus(totalCumulativeInterest).plus(totalFeeChargesCharged)
                    .plus(totalPenaltyChargesCharged);

            // updates the map with over due amounts
            updateLatePaymentsToMap(loanApplicationTerms, holidayDetailDTO, currency, latePaymentMap, lastInstallmentDate,
                    newRepaymentScheduleInstallments, true, lastRestDate, compoundingMap);

            // for partial schedule generation
            if (!newRepaymentScheduleInstallments.isEmpty() && totalCumulativeInterest.isGreaterThanZero()) {
                Money totalOutstandingInterestPaymentDueToGrace = Money.zero(currency);
                loanScheduleParams = LoanScheduleParams.createLoanScheduleParamsForPartialUpdate(periodNumber, instalmentNumber,
                        loanTermInDays, periodStartDate, actualRepaymentDate, totalCumulativePrincipal, totalCumulativeInterest,
                        totalFeeChargesCharged, totalPenaltyChargesCharged, totalRepaymentExpected,
                        totalOutstandingInterestPaymentDueToGrace, reducePrincipal, principalPortionMap, latePaymentMap, compoundingMap,
                        disburseDetailMap, principalToBeScheduled, outstandingBalance, outstandingBalanceAsPerRest,
                        newRepaymentScheduleInstallments, recalculationDetails, loanRepaymentScheduleTransactionProcessor,
                        scheduleTillDate, currency, applyInterestRecalculation);
                retainedInstallments.addAll(newRepaymentScheduleInstallments);
            }

        }
        // for complete schedule generation
        if (loanScheduleParams == null) {
            loanScheduleParams = LoanScheduleParams.createLoanScheduleParamsForCompleteUpdate(recalculationDetails,
                    loanRepaymentScheduleTransactionProcessor, scheduleTillDate, applyInterestRecalculation);
        }

        LoanScheduleModel loanScheduleModel = generate(mc, loanApplicationTerms, loanCharges, holidayDetailDTO, loanScheduleParams);
        for (LoanScheduleModelPeriod loanScheduleModelPeriod : loanScheduleModel.getPeriods()) {
            if (loanScheduleModelPeriod.isRepaymentPeriod()) {
                // adding newly created repayment periods to installments
                addLoanRepaymentScheduleInstallment(retainedInstallments, loanScheduleModelPeriod);
            }
        }
        periods.addAll(loanScheduleModel.getPeriods());
        LoanScheduleModel loanScheduleModelwithPeriodChanges = LoanScheduleModel.withLoanScheduleModelPeriods(periods, loanScheduleModel);
        return LoanScheduleDTO.from(retainedInstallments, loanScheduleModelwithPeriodChanges);
    }

    /**
     * Method identifies the early paid amounts for a installment and update the
     * principal map for further calculations
     */
    private Money updateEarlyPaidAmountsToMap(final LoanApplicationTerms loanApplicationTerms, final HolidayDetailDTO holidayDetailDTO,
            final LoanRepaymentScheduleTransactionProcessor loanRepaymentScheduleTransactionProcessor,
            final List<LoanRepaymentScheduleInstallment> newRepaymentScheduleInstallments, MonetaryCurrency currency,
            final Map<LocalDate, Money> principalPortionMap, LoanRepaymentScheduleInstallment installment,
            Collection<RecalculationDetail> applicableTransactions, Money actualPrincipalPortion) {
        Money unprocessed = Money.zero(currency);
        for (RecalculationDetail detail : applicableTransactions) {
            if (!detail.isProcessed()) {
                Money principalProcessed = installment.getPrincipalCompleted(currency);
                List<LoanTransaction> currentTransactions = new ArrayList<>(2);
                currentTransactions.add(detail.getTransaction());
                // applies the transaction as per transaction strategy
                // on scheduled installments to identify the
                // unprocessed(early payment ) amounts
                loanRepaymentScheduleTransactionProcessor.handleRepaymentSchedule(currentTransactions, currency,
                        newRepaymentScheduleInstallments);

                // Identifies totalEarlyPayment and early paid amount with this
                // transaction
                Money principalPaidWithTransaction = installment.getPrincipalCompleted(currency).minus(principalProcessed);
                Money totalEarlyPayment = installment.getPrincipalCompleted(currency).minus(actualPrincipalPortion);

                if (totalEarlyPayment.isGreaterThanZero()) {
                    unprocessed = principalPaidWithTransaction;
                    // will execute this block if partial amount paid as
                    // early
                    if (principalPaidWithTransaction.isGreaterThan(totalEarlyPayment)) {
                        unprocessed = totalEarlyPayment;
                    }
                }
                // updates principal portion map with the early
                // payment amounts and applicable date as per rest
                LocalDate applicableDate = getNextRestScheduleDate(detail.getTransactionDate().minusDays(1), loanApplicationTerms,
                        holidayDetailDTO);
                updateMapWithAmount(principalPortionMap, unprocessed, applicableDate);

            }
        }
        return unprocessed;
    }

    private void updateAmortization(final MathContext mc, final LoanApplicationTerms loanApplicationTerms, int periodNumber,
            Money outstandingBalance) {
        if (loanApplicationTerms.getAmortizationMethod().isEqualInstallment()) {
            updateFixedInstallmentAmount(mc, loanApplicationTerms, periodNumber, outstandingBalance);
        } else {
            loanApplicationTerms.updateFixedPrincipalAmount(mc, periodNumber, outstandingBalance);
        }
    }

    /**
     * Method identifies early paid amount and applies the early payment
     * strategy
     */
    private Money fetchEarlyPaidAmount(final Money principalPortion, final Money principalPortionCalculated, final Money reducePrincipal,
            final LoanApplicationTerms applicationTerms, final Money totalCumulativePrincipal, int periodNumber, final MathContext mc) {
        Money existingEarlyPayment = reducePrincipal.minus(principalPortionCalculated);
        Money earlyPaidAmount = principalPortion.plus(existingEarlyPayment);
        if (existingEarlyPayment.isLessThanZero()) {
            existingEarlyPayment = existingEarlyPayment.zero();
        }
        boolean isEarlyPaid = earlyPaidAmount.isGreaterThan(existingEarlyPayment);

        if (earlyPaidAmount.isLessThanZero()) {
            earlyPaidAmount = earlyPaidAmount.zero();
        }

        if (isEarlyPaid) {
            switch (applicationTerms.getRescheduleStrategyMethod()) {
                case REDUCE_EMI_AMOUNT:
                    adjustInstallmentOrPrincipalAmount(applicationTerms, totalCumulativePrincipal, periodNumber, mc);
                    earlyPaidAmount = earlyPaidAmount.zero();
                break;
                case REDUCE_NUMBER_OF_INSTALLMENTS:
                    // number of installments will reduce but emi amount won't
                    // get effected
                    earlyPaidAmount = earlyPaidAmount.zero();
                break;
                case RESCHEDULE_NEXT_REPAYMENTS:
                // will reduce principal from the reduce Principal for each
                // installment(means installments will have less emi amount)
                // until this
                // amount becomes zero
                break;
                default:
                break;
            }
        }

        return earlyPaidAmount;
    }

    private Money calculateExpectedPrincipalPortion(final Money interestPortion, final LoanApplicationTerms applicationTerms) {
        Money principalPortionCalculated = interestPortion.zero();
        if (applicationTerms.getAmortizationMethod().isEqualInstallment()) {
            principalPortionCalculated = principalPortionCalculated.plus(applicationTerms.getFixedEmiAmount()).minus(interestPortion);
        } else {
            principalPortionCalculated = principalPortionCalculated.plus(applicationTerms.getFixedPrincipalAmount());
        }
        return principalPortionCalculated;
    }

    private LoanRepaymentScheduleInstallment addLoanRepaymentScheduleInstallment(final List<LoanRepaymentScheduleInstallment> installments,
            final LoanScheduleModelPeriod scheduledLoanInstallment) {
        LoanRepaymentScheduleInstallment installment = null;
        if (scheduledLoanInstallment.isRepaymentPeriod()) {
            installment = new LoanRepaymentScheduleInstallment(null, scheduledLoanInstallment.periodNumber(),
                    scheduledLoanInstallment.periodFromDate(), scheduledLoanInstallment.periodDueDate(),
                    scheduledLoanInstallment.principalDue(), scheduledLoanInstallment.interestDue(),
                    scheduledLoanInstallment.feeChargesDue(), scheduledLoanInstallment.penaltyChargesDue(),
                    scheduledLoanInstallment.isRecalculatedInterestComponent());
            installments.add(installment);
        }
        return installment;
    }

    private LoanScheduleModelPeriod createLoanScheduleModelPeriod(final LoanRepaymentScheduleInstallment installment,
            final Money outstandingPrincipal) {
        final MonetaryCurrency currency = outstandingPrincipal.getCurrency();
        LoanScheduleModelPeriod scheduledLoanInstallment = LoanScheduleModelRepaymentPeriod
                .repayment(installment.getInstallmentNumber(), installment.getFromDate(), installment.getDueDate(),
                        installment.getPrincipal(currency), outstandingPrincipal, installment.getInterestCharged(currency),
                        installment.getFeeChargesCharged(currency), installment.getPenaltyChargesCharged(currency),
                        installment.getDue(currency), installment.isRecalculatedInterestComponent());
        return scheduledLoanInstallment;
    }

    private LocalDate getNextRestScheduleDate(LocalDate startDate, LoanApplicationTerms loanApplicationTerms,
            final HolidayDetailDTO holidayDetailDTO) {
        LocalDate nextScheduleDate = null;
        if (loanApplicationTerms.getRecalculationFrequencyType().isSameAsRepayment()) {
            nextScheduleDate = this.scheduledDateGenerator.generateNextScheduleDateStartingFromDisburseDate(startDate,
                    loanApplicationTerms, holidayDetailDTO);
        } else {
            CalendarInstance calendarInstance = loanApplicationTerms.getRestCalendarInstance();
            nextScheduleDate = CalendarUtils.getNextScheduleDate(calendarInstance.getCalendar(), startDate);
        }

        return nextScheduleDate;
    }

    private LocalDate getNextCompoundScheduleDate(LocalDate startDate, LoanApplicationTerms loanApplicationTerms,
            final HolidayDetailDTO holidayDetailDTO) {
        LocalDate nextScheduleDate = null;
        if (!loanApplicationTerms.getInterestRecalculationCompoundingMethod().isCompoundingEnabled()) { return null; }
        if (loanApplicationTerms.getCompoundingFrequencyType().isSameAsRepayment()) {
            nextScheduleDate = this.scheduledDateGenerator.generateNextScheduleDateStartingFromDisburseDate(startDate,
                    loanApplicationTerms, holidayDetailDTO);
        } else {
            CalendarInstance calendarInstance = loanApplicationTerms.getCompoundingCalendarInstance();
            nextScheduleDate = CalendarUtils.getNextScheduleDate(calendarInstance.getCalendar(), startDate);
        }

        return nextScheduleDate;
    }

    /**
     * Method returns the amount payable to close the loan account as of today.
     */
    @Override
    public LoanRepaymentScheduleInstallment calculatePrepaymentAmount(final MonetaryCurrency currency, final LocalDate onDate,
            final LoanApplicationTerms loanApplicationTerms, final MathContext mc, final Set<LoanCharge> charges,
            final HolidayDetailDTO holidayDetailDTO, final List<LoanTransaction> loanTransactions,
            final LoanRepaymentScheduleTransactionProcessor loanRepaymentScheduleTransactionProcessor,
            final List<LoanRepaymentScheduleInstallment> repaymentScheduleInstallments) {

        LocalDate calculateTill = onDate;
        if (loanApplicationTerms.getPreClosureInterestCalculationStrategy().calculateTillRestFrequencyEnabled()) {
            calculateTill = getNextRestScheduleDate(onDate.minusDays(1), loanApplicationTerms, holidayDetailDTO);
        }

        LoanScheduleDTO loanScheduleDTO = rescheduleNextInstallments(mc, loanApplicationTerms, charges, holidayDetailDTO, loanTransactions,
                loanRepaymentScheduleTransactionProcessor, repaymentScheduleInstallments, onDate, calculateTill);

        loanRepaymentScheduleTransactionProcessor.handleTransaction(loanApplicationTerms.getExpectedDisbursementDate(), loanTransactions,
                currency, loanScheduleDTO.getInstallments(), charges);
        Money feeCharges = Money.zero(currency);
        Money penaltyCharges = Money.zero(currency);
        Money totalPrincipal = Money.zero(currency);
        Money totalInterest = Money.zero(currency);
        for (final LoanRepaymentScheduleInstallment currentInstallment : loanScheduleDTO.getInstallments()) {
            if (currentInstallment.isNotFullyPaidOff()) {
                totalPrincipal = totalPrincipal.plus(currentInstallment.getPrincipalOutstanding(currency));
                totalInterest = totalInterest.plus(currentInstallment.getInterestOutstanding(currency));
                feeCharges = feeCharges.plus(currentInstallment.getFeeChargesOutstanding(currency));
                penaltyCharges = penaltyCharges.plus(currentInstallment.getPenaltyChargesOutstanding(currency));
            }
        }

        return new LoanRepaymentScheduleInstallment(null, 0, onDate, onDate, totalPrincipal.getAmount(), totalInterest.getAmount(),
                feeCharges.getAmount(), penaltyCharges.getAmount(), false);
    }

    /**
     * set the value to zero if the provided value is null
     * 
     * @return integer value equal/greater than 0
     **/
    private Integer defaultToZeroIfNull(Integer value) {

        return (value != null) ? value : 0;
    }

    private final class LoanTermVariationParams {

        private final boolean skipPeriod;
        private final boolean recalculateAmounts;
        private final LocalDate scheduledDueDate;

        public LoanTermVariationParams(final boolean skipPeriod, final boolean recalculateAmounts, final LocalDate scheduledDueDate) {
            this.skipPeriod = skipPeriod;
            this.recalculateAmounts = recalculateAmounts;
            this.scheduledDueDate = scheduledDueDate;
        }

        public boolean isSkipPeriod() {
            return this.skipPeriod;
        }

        public boolean isRecalculateAmounts() {
            return this.recalculateAmounts;
        }

        public LocalDate getScheduledDueDate() {
            return this.scheduledDueDate;
        }

    }

    private final class ScheduleCurrentPeriodParams {

        Money earlyPaidAmount;
        LoanScheduleModelPeriod lastInstallment;
        boolean skipCurrentLoop;
        Money interestForThisPeriod;
        Money principalForThisPeriod;
        Money feeChargesForInstallment;
        Money penaltyChargesForInstallment;
        // for adjusting outstandingBalances
        Money reducedBalance;
        boolean isEmiAmountChanged;
        double interestCalculationGraceOnRepaymentPeriodFraction;

        public ScheduleCurrentPeriodParams(final MonetaryCurrency currency, double interestCalculationGraceOnRepaymentPeriodFraction) {
            this.earlyPaidAmount = Money.zero(currency);
            this.lastInstallment = null;
            this.skipCurrentLoop = false;
            this.interestForThisPeriod = Money.zero(currency);
            this.principalForThisPeriod = Money.zero(currency);
            this.reducedBalance = Money.zero(currency);
            this.feeChargesForInstallment = Money.zero(currency);
            this.penaltyChargesForInstallment = Money.zero(currency);
            this.isEmiAmountChanged = false;
            this.interestCalculationGraceOnRepaymentPeriodFraction = interestCalculationGraceOnRepaymentPeriodFraction;
        }

        public Money getEarlyPaidAmount() {
            return this.earlyPaidAmount;
        }

        public void plusEarlyPaidAmount(Money earlyPaidAmount) {
            this.earlyPaidAmount = this.earlyPaidAmount.plus(earlyPaidAmount);
        }

        public void minusEarlyPaidAmount(Money earlyPaidAmount) {
            this.earlyPaidAmount = this.earlyPaidAmount.minus(earlyPaidAmount);
        }

        public LoanScheduleModelPeriod getLastInstallment() {
            return this.lastInstallment;
        }

        public void setLastInstallment(LoanScheduleModelPeriod lastInstallment) {
            this.lastInstallment = lastInstallment;
        }

        public boolean isSkipCurrentLoop() {
            return this.skipCurrentLoop;
        }

        public void setSkipCurrentLoop(boolean skipCurrentLoop) {
            this.skipCurrentLoop = skipCurrentLoop;
        }

        public Money getInterestForThisPeriod() {
            return this.interestForThisPeriod;
        }

        public void setInterestForThisPeriod(Money interestForThisPeriod) {
            this.interestForThisPeriod = interestForThisPeriod;
        }

        public void minusInterestForThisPeriod(Money interestForThisPeriod) {
            this.interestForThisPeriod = this.interestForThisPeriod.minus(interestForThisPeriod);
        }

        public Money getPrincipalForThisPeriod() {
            return this.principalForThisPeriod;
        }

        public void setPrincipalForThisPeriod(Money principalForThisPeriod) {
            this.principalForThisPeriod = principalForThisPeriod;
        }

        public void plusPrincipalForThisPeriod(Money principalForThisPeriod) {
            this.principalForThisPeriod = this.principalForThisPeriod.plus(principalForThisPeriod);
        }

        public void minusPrincipalForThisPeriod(Money principalForThisPeriod) {
            this.principalForThisPeriod = this.principalForThisPeriod.minus(principalForThisPeriod);
        }

        public Money getReducedBalance() {
            return this.reducedBalance;
        }

        public void setReducedBalance(Money reducedBalance) {
            this.reducedBalance = reducedBalance;
        }

        public Money getFeeChargesForInstallment() {
            return this.feeChargesForInstallment;
        }

        public void setFeeChargesForInstallment(Money feeChargesForInstallment) {
            this.feeChargesForInstallment = feeChargesForInstallment;
        }

        public Money getPenaltyChargesForInstallment() {
            return this.penaltyChargesForInstallment;
        }

        public void setPenaltyChargesForInstallment(Money penaltyChargesForInstallment) {
            this.penaltyChargesForInstallment = penaltyChargesForInstallment;
        }

        public Money fetchTotalAmountForPeriod() {
            return this.principalForThisPeriod.plus(interestForThisPeriod).plus(feeChargesForInstallment)
                    .plus(penaltyChargesForInstallment);
        }

        public boolean isEmiAmountChanged() {
            return this.isEmiAmountChanged;
        }

        public void setEmiAmountChanged(boolean isEmiAmountChanged) {
            this.isEmiAmountChanged = isEmiAmountChanged;
        }

        public double getInterestCalculationGraceOnRepaymentPeriodFraction() {
            return this.interestCalculationGraceOnRepaymentPeriodFraction;
        }

    }

}