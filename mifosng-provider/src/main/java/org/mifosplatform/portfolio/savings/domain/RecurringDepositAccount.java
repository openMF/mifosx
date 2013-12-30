/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.savings.domain;

import java.math.BigDecimal;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

import org.joda.time.LocalDate;
import org.mifosplatform.organisation.staff.domain.Staff;
import org.mifosplatform.portfolio.accountdetails.domain.AccountType;
import org.mifosplatform.portfolio.client.domain.Client;
import org.mifosplatform.portfolio.group.domain.Group;
import org.mifosplatform.portfolio.interestratechart.service.InterestRateChartAssembler;
import org.mifosplatform.portfolio.savings.SavingsCompoundingInterestPeriodType;
import org.mifosplatform.portfolio.savings.SavingsInterestCalculationDaysInYearType;
import org.mifosplatform.portfolio.savings.SavingsInterestCalculationType;
import org.mifosplatform.portfolio.savings.SavingsPeriodFrequencyType;
import org.mifosplatform.portfolio.savings.SavingsPostingInterestPeriodType;
import org.mifosplatform.useradministration.domain.AppUser;

@Entity
@DiscriminatorValue("300")
public class RecurringDepositAccount extends SavingsAccount {

    @OneToOne(mappedBy = "account", cascade = CascadeType.ALL)
    private DepositAccountTermAndPreClosure accountTermAndPreClosure;

    @OneToOne(mappedBy = "account", cascade = CascadeType.ALL)
    private DepositAccountRecurringDetail recurringDetail;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "account")
    protected DepositAccountInterestRateChart chart;

    @Transient
    protected InterestRateChartAssembler chartAssembler;

    protected RecurringDepositAccount() {
        //
    }

    public static RecurringDepositAccount createNewApplicationForSubmittal(final Client client, final Group group,
            final SavingsProduct product, final Staff fieldOfficer, final String accountNo, final String externalId,
            final AccountType accountType, final LocalDate submittedOnDate, final AppUser submittedBy, final BigDecimal interestRate,
            final SavingsCompoundingInterestPeriodType interestCompoundingPeriodType,
            final SavingsPostingInterestPeriodType interestPostingPeriodType, final SavingsInterestCalculationType interestCalculationType,
            final SavingsInterestCalculationDaysInYearType interestCalculationDaysInYearType, final BigDecimal minRequiredOpeningBalance,
            final Integer lockinPeriodFrequency, final SavingsPeriodFrequencyType lockinPeriodFrequencyType,
            final boolean withdrawalFeeApplicableForTransfer, final Set<SavingsAccountCharge> savingsAccountCharges,
            final DepositAccountTermAndPreClosure accountTermAndPreClosure, final DepositAccountRecurringDetail recurringDetail,
            final DepositAccountInterestRateChart chart) {

        final boolean allowOverdraft = false;
        final BigDecimal overdraftLimit = new BigDecimal(0);
        
        final SavingsAccountStatusType status = SavingsAccountStatusType.SUBMITTED_AND_PENDING_APPROVAL;
        return new RecurringDepositAccount(client, group, product, fieldOfficer, accountNo, externalId, status, accountType,
                submittedOnDate, submittedBy, interestRate, interestCompoundingPeriodType, interestPostingPeriodType,
                interestCalculationType, interestCalculationDaysInYearType, minRequiredOpeningBalance, lockinPeriodFrequency,
                lockinPeriodFrequencyType, withdrawalFeeApplicableForTransfer, savingsAccountCharges, accountTermAndPreClosure,
                recurringDetail, chart, allowOverdraft, overdraftLimit);
    }

    private RecurringDepositAccount(final Client client, final Group group, final SavingsProduct product, final Staff fieldOfficer,
            final String accountNo, final String externalId, final SavingsAccountStatusType status, final AccountType accountType,
            final LocalDate submittedOnDate, final AppUser submittedBy, final BigDecimal nominalAnnualInterestRate,
            final SavingsCompoundingInterestPeriodType interestCompoundingPeriodType,
            final SavingsPostingInterestPeriodType interestPostingPeriodType, final SavingsInterestCalculationType interestCalculationType,
            final SavingsInterestCalculationDaysInYearType interestCalculationDaysInYearType, final BigDecimal minRequiredOpeningBalance,
            final Integer lockinPeriodFrequency, final SavingsPeriodFrequencyType lockinPeriodFrequencyType,
            final boolean withdrawalFeeApplicableForTransfer, final Set<SavingsAccountCharge> savingsAccountCharges,
            final DepositAccountTermAndPreClosure accountTermAndPreClosure, final DepositAccountRecurringDetail recurringDetail,
            final DepositAccountInterestRateChart chart, final boolean allowOverdraft, final BigDecimal overdraftLimit) {

        super(client, group, product, fieldOfficer, accountNo, externalId, status, accountType, submittedOnDate, submittedBy,
                nominalAnnualInterestRate, interestCompoundingPeriodType, interestPostingPeriodType, interestCalculationType,
                interestCalculationDaysInYearType, minRequiredOpeningBalance, lockinPeriodFrequency, lockinPeriodFrequencyType,
                withdrawalFeeApplicableForTransfer, savingsAccountCharges, allowOverdraft, overdraftLimit);

        this.accountTermAndPreClosure = accountTermAndPreClosure;
        this.recurringDetail = recurringDetail;
        this.chart = chart;
        if (this.chart != null) {
            this.chart.updateDepositAccountReference(this);
        }
    }

}