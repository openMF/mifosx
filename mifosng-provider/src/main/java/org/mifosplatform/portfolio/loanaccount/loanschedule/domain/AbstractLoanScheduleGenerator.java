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
import java.util.List;
import java.util.Set;

import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.mifosplatform.organisation.holiday.domain.Holiday;
import org.mifosplatform.organisation.monetary.domain.ApplicationCurrency;
import org.mifosplatform.organisation.monetary.domain.MonetaryCurrency;
import org.mifosplatform.organisation.monetary.domain.Money;
import org.mifosplatform.organisation.workingdays.domain.WorkingDays;
import org.mifosplatform.portfolio.loanaccount.domain.LoanCharge;

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

        // 1. generate list of proposed schedule due dates
        final List<LocalDate> scheduledDates = this.scheduledDateGenerator.generate(loanApplicationTerms, isHolidayEnabled, holidays,
                workingDays);

        final LocalDate loanEndDate = scheduledDates.get(scheduledDates.size() - 1);
        loanApplicationTerms.updateLoanEndDate(loanEndDate);

        // 2. determine the total charges due at time of disbursement
        final BigDecimal chargesDueAtTimeOfDisbursement = deriveTotalChargesDueAtTimeOfDisbursement(loanCharges);

        // 3. setup variables for tracking important facts required for loan
        // schedule generation.
        final Money principalDisbursed = loanApplicationTerms.getPrincipal();
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

        // 4. Determine the total interest owed over the full loan.
        final Money totalInterestChargedForFullLoanTerm = loanApplicationTerms.calculateTotalInterestCharged(
                this.paymentPeriodsInOneYearCalculator, mc);

        // 5. loop over the known set of 'scheduled due dates'
        final LocalDate idealDisbursementDate = this.scheduledDateGenerator.idealDisbursementDateBasedOnFirstRepaymentDate(
                loanApplicationTerms.getLoanTermPeriodFrequencyType(), loanApplicationTerms.getRepaymentEvery(), scheduledDates);

        LocalDate periodStartDate = loanApplicationTerms.getExpectedDisbursementDate();
        LocalDate periodStartDateApplicableForInterest = periodStartDate;
        int periodNumber = 1;
        Money totalCumulativePrincipal = principalDisbursed.zero();
        Money totalCumulativeInterest = principalDisbursed.zero();
        Money totalOutstandingInterestPaymentDueToGrace = principalDisbursed.zero();
        Money outstandingBalance = principalDisbursed;
        for (final LocalDate scheduledDueDate : scheduledDates) {

            final int daysInPeriod = Days.daysBetween(periodStartDate, scheduledDueDate).getDays();
            int daysInPeriodApplicableForInterest = daysInPeriod;

            if (periodStartDate.isBefore(idealDisbursementDate)) {
                periodStartDateApplicableForInterest = idealDisbursementDate;
                daysInPeriodApplicableForInterest = Days.daysBetween(periodStartDateApplicableForInterest, scheduledDueDate).getDays();
            }

            final double interestCalculationGraceOnRepaymentPeriodFraction = this.paymentPeriodsInOneYearCalculator
                    .calculatePortionOfRepaymentPeriodInterestChargingGrace(periodStartDateApplicableForInterest, scheduledDueDate,
                            loanApplicationTerms.getInterestChargedFromLocalDate(), loanApplicationTerms.getLoanTermPeriodFrequencyType(),
                            loanApplicationTerms.getRepaymentEvery());

            // 5 determine principal,interest of repayment period
            final PrincipalInterest principalInterestForThisPeriod = calculatePrincipalInterestComponentsForPeriod(
                    this.paymentPeriodsInOneYearCalculator, interestCalculationGraceOnRepaymentPeriodFraction, totalCumulativePrincipal,
                    totalCumulativeInterest, totalInterestChargedForFullLoanTerm, totalOutstandingInterestPaymentDueToGrace,
                    daysInPeriodApplicableForInterest, outstandingBalance, loanApplicationTerms, periodNumber, mc);

            // update cumulative fields for principal & interest
            totalCumulativePrincipal = totalCumulativePrincipal.plus(principalInterestForThisPeriod.principal());
            totalCumulativeInterest = totalCumulativeInterest.plus(principalInterestForThisPeriod.interest());
            totalOutstandingInterestPaymentDueToGrace = principalInterestForThisPeriod.interestPaymentDueToGrace();

            // 6. update outstandingLoanBlance using correct 'principalDue'
            outstandingBalance = outstandingBalance.minus(principalInterestForThisPeriod.principal());

            // 7. determine fees and penalties
            final Money feeChargesForInstallment = cumulativeFeeChargesDueWithin(periodStartDate, scheduledDueDate, loanCharges, currency,
                    principalInterestForThisPeriod, principalDisbursed, totalInterestChargedForFullLoanTerm, numberOfRepayments);
            final Money penaltyChargesForInstallment = cumulativePenaltyChargesDueWithin(periodStartDate, scheduledDueDate, loanCharges,
                    currency, principalInterestForThisPeriod, principalDisbursed, totalInterestChargedForFullLoanTerm, numberOfRepayments);

            // 8. sum up real totalInstallmentDue from components
            final Money totalInstallmentDue = principalInterestForThisPeriod.principal() //
                    .plus(principalInterestForThisPeriod.interest()) //
                    .plus(feeChargesForInstallment) //
                    .plus(penaltyChargesForInstallment);

            // 9. create repayment period from parts
            final LoanScheduleModelPeriod installment = LoanScheduleModelRepaymentPeriod.repayment(periodNumber, periodStartDate,
                    scheduledDueDate, principalInterestForThisPeriod.principal(), outstandingBalance,
                    principalInterestForThisPeriod.interest(), feeChargesForInstallment, penaltyChargesForInstallment, totalInstallmentDue);
            periods.add(installment);

            // handle cumulative fields
            loanTermInDays += daysInPeriod;
            totalPrincipalExpected = totalPrincipalExpected.add(principalInterestForThisPeriod.principal().getAmount());
            totalInterestCharged = totalInterestCharged.add(principalInterestForThisPeriod.interest().getAmount());
            totalFeeChargesCharged = totalFeeChargesCharged.add(feeChargesForInstallment.getAmount());
            totalPenaltyChargesCharged = totalPenaltyChargesCharged.add(penaltyChargesForInstallment.getAmount());
            totalRepaymentExpected = totalRepaymentExpected.add(totalInstallmentDue.getAmount());
            periodStartDate = scheduledDueDate;
            periodStartDateApplicableForInterest = periodStartDate;

            periodNumber++;
        }

        return LoanScheduleModel.from(periods, applicationCurrency, loanTermInDays, principalDisbursed, totalPrincipalExpected,
                totalPrincipalPaid, totalInterestCharged, totalFeeChargesCharged, totalPenaltyChargesCharged, totalRepaymentExpected,
                totalOutstanding);
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

    private Collection<LoanScheduleModelPeriod> createNewLoanScheduleListWithDisbursementDetails(final int numberOfRepayments,
            final LoanApplicationTerms loanApplicationTerms, final BigDecimal chargesDueAtTimeOfDisbursement) {

        final Collection<LoanScheduleModelPeriod> periods = new ArrayList<LoanScheduleModelPeriod>(numberOfRepayments + 1);

        final LoanScheduleModelDisbursementPeriod disbursementPeriod = LoanScheduleModelDisbursementPeriod.disbursement(
                loanApplicationTerms, chargesDueAtTimeOfDisbursement);
        periods.add(disbursementPeriod);

        return periods;
    }

    private Money cumulativeFeeChargesDueWithin(final LocalDate periodStart, final LocalDate periodEnd, final Set<LoanCharge> loanCharges,
            final MonetaryCurrency monetaryCurrency, final PrincipalInterest principalInterestForThisPeriod,
            final Money principalDisbursed, final Money totalInterestChargedForFullLoanTerm, int numberOfRepayments) {

        Money cumulative = Money.zero(monetaryCurrency);

        for (final LoanCharge loanCharge : loanCharges) {
            if (loanCharge.isFeeCharge()) {
                if (loanCharge.isInstalmentFee()) {
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
            final Money totalInterestChargedForFullLoanTerm, int numberOfRepayments) {

        Money cumulative = Money.zero(monetaryCurrency);

        for (final LoanCharge loanCharge : loanCharges) {
            if (loanCharge.isPenaltyCharge()) {
                if (loanCharge.isInstalmentFee()) {
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
}