/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.savings.data;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashSet;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.mifosplatform.infrastructure.core.data.EnumOptionData;
import org.mifosplatform.organisation.monetary.data.CurrencyData;
import org.mifosplatform.organisation.staff.data.StaffData;
import org.mifosplatform.portfolio.charge.data.ChargeData;

/**
 * Immutable data object representing a savings account.
 */
public class SavingsAccountData {

    private final Long id;
    private final String accountNo;
    private final String externalId;
    private final Long groupId;
    private final String groupName;
    private final Long clientId;
    private final String clientName;
    private final Long savingsProductId;
    private final String savingsProductName;
    private final Long fieldOfficerId;
    private final String fieldOfficerName;
    private final SavingsAccountStatusEnumData status;
    private final SavingsAccountApplicationTimelineData timeline;
    private final CurrencyData currency;
    private final BigDecimal nominalAnnualInterestRate;
    private final EnumOptionData interestCompoundingPeriodType;
    private final EnumOptionData interestPostingPeriodType;
    private final EnumOptionData interestCalculationType;
    private final EnumOptionData interestCalculationDaysInYearType;
    private final BigDecimal minRequiredOpeningBalance;
    private final Integer lockinPeriodFrequency;
    private final EnumOptionData lockinPeriodFrequencyType;
    private final boolean withdrawalFeeForTransfers;

    // associations
    private final SavingsAccountSummaryData summary;
    @SuppressWarnings("unused")
    private final Collection<SavingsAccountTransactionData> transactions;

    private final Collection<SavingsAccountChargeData> charges;

    // template
    private final Collection<SavingsProductData> productOptions;
    private final Collection<StaffData> fieldOfficerOptions;
    private final Collection<EnumOptionData> interestCompoundingPeriodTypeOptions;
    private final Collection<EnumOptionData> interestPostingPeriodTypeOptions;
    private final Collection<EnumOptionData> interestCalculationTypeOptions;
    private final Collection<EnumOptionData> interestCalculationDaysInYearTypeOptions;
    private final Collection<EnumOptionData> lockinPeriodFrequencyTypeOptions;
    private final Collection<EnumOptionData> withdrawalFeeTypeOptions;
    private final Collection<ChargeData> chargeOptions;

    @SuppressWarnings("unused")
    private final SavingsAccountChargeData withdrawalFee;
    @SuppressWarnings("unused")
    private final SavingsAccountChargeData annualFee;
    
    public static SavingsAccountData instance(final Long id, final String accountNo, final String externalId, final Long groupId,
            final String groupName, final Long clientId, final String clientName, final Long productId, final String productName,
            final Long fieldOfficerId, final String fieldOfficerName, final SavingsAccountStatusEnumData status,
            final SavingsAccountApplicationTimelineData timeline, final CurrencyData currency, final BigDecimal interestRate,
            final EnumOptionData interestCompoundingPeriodType, final EnumOptionData interestPostingPeriodType,
            final EnumOptionData interestCalculationType, final EnumOptionData interestCalculationDaysInYearType,
            final BigDecimal minRequiredOpeningBalance, final Integer lockinPeriodFrequency,
            final EnumOptionData lockinPeriodFrequencyType, final boolean withdrawalFeeForTransfers, final SavingsAccountSummaryData summary) {

        final Collection<SavingsProductData> productOptions = null;
        final Collection<StaffData> fieldOfficerOptions = null;
        final Collection<EnumOptionData> interestCompoundingPeriodTypeOptions = null;
        final Collection<EnumOptionData> interestPostingPeriodTypeOptions = null;
        final Collection<EnumOptionData> interestCalculationTypeOptions = null;
        final Collection<EnumOptionData> interestCalculationDaysInYearTypeOptions = null;
        final Collection<EnumOptionData> lockinPeriodFrequencyTypeOptions = null;
        final Collection<EnumOptionData> withdrawalFeeTypeOptions = null;

        final Collection<SavingsAccountTransactionData> transactions = null;
        final Collection<SavingsAccountChargeData> charges = null;
        final Collection<ChargeData> chargeOptions = null;

        return new SavingsAccountData(id, accountNo, externalId, groupId, groupName, clientId, clientName, productId, productName,
                fieldOfficerId, fieldOfficerName, status, timeline, currency, interestRate, interestCompoundingPeriodType,
                interestPostingPeriodType, interestCalculationType, interestCalculationDaysInYearType, minRequiredOpeningBalance,
                lockinPeriodFrequency, lockinPeriodFrequencyType, withdrawalFeeForTransfers, summary, transactions, productOptions,
                fieldOfficerOptions, interestCompoundingPeriodTypeOptions, interestPostingPeriodTypeOptions,
                interestCalculationTypeOptions, interestCalculationDaysInYearTypeOptions, lockinPeriodFrequencyTypeOptions,
                withdrawalFeeTypeOptions, charges, chargeOptions);
    }

    public static SavingsAccountData lookup(final Long accountId, final String accountNo) {

        final String externalId = null;
        final Long productId = null;
        final Long groupId = null;
        final Long clientId = null;
        final String clientName = null;
        final String groupName = null;
        final String productName = null;
        final Long fieldOfficerId = null;
        final String fieldOfficerName = null;
        final SavingsAccountStatusEnumData status = null;
        final SavingsAccountApplicationTimelineData timeline = null;
        final CurrencyData currency = null;
        final BigDecimal nominalAnnualInterestRate = null;
        final EnumOptionData interestPeriodType = null;
        final EnumOptionData interestPostingPeriodType = null;
        final EnumOptionData interestCalculationType = null;
        final EnumOptionData interestCalculationDaysInYearType = null;
        final BigDecimal minRequiredOpeningBalance = null;
        final Integer lockinPeriodFrequency = null;
        final EnumOptionData lockinPeriodFrequencyType = null;
//        final BigDecimal withdrawalFeeAmount = null;
//        final EnumOptionData withdrawalFeeType = null;
        final boolean withdrawalFeeForTransfers = false;
//        final BigDecimal annualFeeAmount = null;
//        final MonthDay annualFeeOnMonthDay = null;
//        final LocalDate annualFeeNextDueDate = null;

        final SavingsAccountSummaryData summary = null;
        final Collection<SavingsAccountTransactionData> transactions = null;

        final Collection<SavingsProductData> productOptions = null;
        final Collection<StaffData> fieldOfficerOptions = null;
        final Collection<EnumOptionData> interestCompoundingPeriodTypeOptions = null;
        final Collection<EnumOptionData> interestPostingPeriodTypeOptions = null;
        final Collection<EnumOptionData> interestCalculationTypeOptions = null;
        final Collection<EnumOptionData> interestCalculationDaysInYearTypeOptions = null;
        final Collection<EnumOptionData> lockinPeriodFrequencyTypeOptions = null;
        final Collection<EnumOptionData> withdrawalFeeTypeOptions = null;

        final Collection<SavingsAccountChargeData> charges = null;
        final Collection<ChargeData> chargeOptions = null;

        return new SavingsAccountData(accountId, accountNo, externalId, groupId, groupName, clientId, clientName, productId, productName,
                fieldOfficerId , fieldOfficerName, status, timeline, currency, nominalAnnualInterestRate, interestPeriodType,
                interestPostingPeriodType, interestCalculationType, interestCalculationDaysInYearType, minRequiredOpeningBalance,
                lockinPeriodFrequency, lockinPeriodFrequencyType, withdrawalFeeForTransfers, summary, transactions, productOptions,
                fieldOfficerOptions, interestCompoundingPeriodTypeOptions, interestPostingPeriodTypeOptions,
                interestCalculationTypeOptions, interestCalculationDaysInYearTypeOptions, lockinPeriodFrequencyTypeOptions,
                withdrawalFeeTypeOptions, charges, chargeOptions);
    }

    public static SavingsAccountData withTemplateOptions(final SavingsAccountData account, final SavingsAccountData template,
            final Collection<SavingsAccountTransactionData> transactions, final Collection<SavingsAccountChargeData> charges) {

        if (template == null) {
            final Collection<SavingsProductData> productOptions = null;
            final Collection<StaffData> fieldOfficerOptions = null;
            final Collection<EnumOptionData> interestCompoundingPeriodTypeOptions = null;
            final Collection<EnumOptionData> interestPostingPeriodTypeOptions = null;
            final Collection<EnumOptionData> interestCalculationTypeOptions = null;
            final Collection<EnumOptionData> interestCalculationDaysInYearTypeOptions = null;
            final Collection<EnumOptionData> lockinPeriodFrequencyTypeOptions = null;
            final Collection<EnumOptionData> withdrawalFeeTypeOptions = null;
            final Collection<ChargeData> chargeOptions = null;

            return withTemplateOptions(account, productOptions, fieldOfficerOptions, interestCompoundingPeriodTypeOptions,
                    interestPostingPeriodTypeOptions, interestCalculationTypeOptions, interestCalculationDaysInYearTypeOptions,
                    lockinPeriodFrequencyTypeOptions, withdrawalFeeTypeOptions, transactions, charges, chargeOptions);
        }

        return new SavingsAccountData(account.id, account.accountNo, account.externalId, account.groupId, account.groupName,
                account.clientId, account.clientName, account.savingsProductId, account.savingsProductName, account.fieldOfficerId,
                account.fieldOfficerName, account.status, account.timeline, account.currency, account.nominalAnnualInterestRate,
                account.interestCompoundingPeriodType, account.interestPostingPeriodType, account.interestCalculationType,
                account.interestCalculationDaysInYearType, account.minRequiredOpeningBalance, account.lockinPeriodFrequency,
                account.lockinPeriodFrequencyType, account.withdrawalFeeForTransfers, account.summary, transactions,
                template.productOptions, template.fieldOfficerOptions, template.interestCompoundingPeriodTypeOptions,
                template.interestPostingPeriodTypeOptions, template.interestCalculationTypeOptions,
                template.interestCalculationDaysInYearTypeOptions, template.lockinPeriodFrequencyTypeOptions,
                template.withdrawalFeeTypeOptions, charges, template.chargeOptions);
    }

    public static SavingsAccountData withTemplateOptions(final SavingsAccountData account,
            final Collection<SavingsProductData> productOptions, final Collection<StaffData> fieldOfficerOptions,
            final Collection<EnumOptionData> interestCompoundingPeriodTypeOptions,
            final Collection<EnumOptionData> interestPostingPeriodTypeOptions,
            final Collection<EnumOptionData> interestCalculationTypeOptions,
            final Collection<EnumOptionData> interestCalculationDaysInYearTypeOptions,
            final Collection<EnumOptionData> lockinPeriodFrequencyTypeOptions, final Collection<EnumOptionData> withdrawalFeeTypeOptions,
            final Collection<SavingsAccountTransactionData> transactions, final Collection<SavingsAccountChargeData> charges,
            final Collection<ChargeData> chargeOptions) {

        return new SavingsAccountData(account.id, account.accountNo, account.externalId, account.groupId, account.groupName,
                account.clientId, account.clientName, account.savingsProductId, account.savingsProductName, account.fieldOfficerId,
                account.fieldOfficerName, account.status, account.timeline, account.currency, account.nominalAnnualInterestRate,
                account.interestCompoundingPeriodType, account.interestPostingPeriodType, account.interestCalculationType,
                account.interestCalculationDaysInYearType, account.minRequiredOpeningBalance, account.lockinPeriodFrequency,
                account.lockinPeriodFrequencyType, account.withdrawalFeeForTransfers, account.summary, transactions, productOptions,
                fieldOfficerOptions, interestCompoundingPeriodTypeOptions, interestPostingPeriodTypeOptions,
                interestCalculationTypeOptions, interestCalculationDaysInYearTypeOptions, lockinPeriodFrequencyTypeOptions,
                withdrawalFeeTypeOptions, charges, chargeOptions);
    }

    public static SavingsAccountData withClientTemplate(final Long clientId, final String clientName, final Long groupId,
            final String groupName) {

        final Long id = null;
        final String accountNo = null;
        final String externalId = null;
        final Long productId = null;
        final String productName = null;
        final Long fieldOfficerId = null;
        final String fieldOfficerName = null;
        final SavingsAccountStatusEnumData status = null;
        final SavingsAccountApplicationTimelineData timeline = null;
        final CurrencyData currency = null;
        final BigDecimal nominalAnnualInterestRate = null;
        final EnumOptionData interestPeriodType = null;
        final EnumOptionData interestPostingPeriodType = null;
        final EnumOptionData interestCalculationType = null;
        final EnumOptionData interestCalculationDaysInYearType = null;
        final BigDecimal minRequiredOpeningBalance = null;
        final Integer lockinPeriodFrequency = null;
        final EnumOptionData lockinPeriodFrequencyType = null;
//        final BigDecimal withdrawalFeeAmount = null;
//        final EnumOptionData withdrawalFeeType = null;
        final boolean withdrawalFeeForTransfers = false;
//        final BigDecimal annualFeeAmount = null;
//        final MonthDay annualFeeOnMonthDay = null;
//        final LocalDate annualFeeNextDueDate = null;

        final SavingsAccountSummaryData summary = null;
        final Collection<SavingsAccountTransactionData> transactions = null;

        final Collection<SavingsProductData> productOptions = null;
        final Collection<StaffData> fieldOfficerOptions = null;
        final Collection<EnumOptionData> interestCompoundingPeriodTypeOptions = null;
        final Collection<EnumOptionData> interestPostingPeriodTypeOptions = null;
        final Collection<EnumOptionData> interestCalculationTypeOptions = null;
        final Collection<EnumOptionData> interestCalculationDaysInYearTypeOptions = null;
        final Collection<EnumOptionData> lockinPeriodFrequencyTypeOptions = null;
        final Collection<EnumOptionData> withdrawalFeeTypeOptions = null;

        final Collection<SavingsAccountChargeData> charges = null;
        final Collection<ChargeData> chargeOptions = null;

        return new SavingsAccountData(id, accountNo, externalId, groupId, groupName, clientId, clientName, productId, productName,
                fieldOfficerId, fieldOfficerName, status, timeline, currency, nominalAnnualInterestRate, interestPeriodType,
                interestPostingPeriodType, interestCalculationType, interestCalculationDaysInYearType, minRequiredOpeningBalance,
                lockinPeriodFrequency, lockinPeriodFrequencyType, withdrawalFeeForTransfers, summary, transactions, productOptions,
                fieldOfficerOptions, interestCompoundingPeriodTypeOptions, interestPostingPeriodTypeOptions,
                interestCalculationTypeOptions, interestCalculationDaysInYearTypeOptions, lockinPeriodFrequencyTypeOptions,
                withdrawalFeeTypeOptions, charges, chargeOptions);
    }

    private SavingsAccountData(final Long id, final String accountNo, final String externalId, final Long groupId, final String groupName,
            final Long clientId, final String clientName, final Long productId, final String productName, final Long fieldofficerId,
            final String fieldofficerName, final SavingsAccountStatusEnumData status, final SavingsAccountApplicationTimelineData timeline,
            final CurrencyData currency, final BigDecimal nominalAnnualInterestRate, final EnumOptionData interestPeriodType,
            final EnumOptionData interestPostingPeriodType, final EnumOptionData interestCalculationType,
            final EnumOptionData interestCalculationDaysInYearType, final BigDecimal minRequiredOpeningBalance,
            final Integer lockinPeriodFrequency, final EnumOptionData lockinPeriodFrequencyType, final boolean withdrawalFeeForTransfers,
            final SavingsAccountSummaryData summary, final Collection<SavingsAccountTransactionData> transactions,
            final Collection<SavingsProductData> productOptions, final Collection<StaffData> fieldOfficerOptions,
            final Collection<EnumOptionData> interestCompoundingPeriodTypeOptions,
            final Collection<EnumOptionData> interestPostingPeriodTypeOptions,
            final Collection<EnumOptionData> interestCalculationTypeOptions,
            final Collection<EnumOptionData> interestCalculationDaysInYearTypeOptions,
            final Collection<EnumOptionData> lockinPeriodFrequencyTypeOptions, final Collection<EnumOptionData> withdrawalFeeTypeOptions,
            final Collection<SavingsAccountChargeData> charges, final Collection<ChargeData> chargeOptions) {
        this.id = id;
        this.accountNo = accountNo;
        this.externalId = externalId;
        this.groupId = groupId;
        this.groupName = groupName;
        this.clientId = clientId;
        this.clientName = clientName;
        this.savingsProductId = productId;
        this.savingsProductName = productName;
        this.fieldOfficerId = fieldofficerId;
        this.fieldOfficerName = fieldofficerName;
        this.status = status;
        this.timeline = timeline;
        this.currency = currency;
        this.nominalAnnualInterestRate = nominalAnnualInterestRate;
        this.interestCompoundingPeriodType = interestPeriodType;
        this.interestPostingPeriodType = interestPostingPeriodType;
        this.interestCalculationType = interestCalculationType;
        this.interestCalculationDaysInYearType = interestCalculationDaysInYearType;
        this.minRequiredOpeningBalance = minRequiredOpeningBalance;
        this.lockinPeriodFrequency = lockinPeriodFrequency;
        this.lockinPeriodFrequencyType = lockinPeriodFrequencyType;
//        this.withdrawalFeeAmount = withdrawalFeeAmount;
//        this.withdrawalFeeType = withdrawalFeeType;
        this.withdrawalFeeForTransfers = withdrawalFeeForTransfers;
//        this.annualFeeAmount = annualFeeAmount;
//        this.annualFeeOnMonthDay = annualFeeOnMonthDay;
//        this.annualFeeNextDueDate = annualFeeNextDueDate;

        this.summary = summary;
        this.transactions = transactions;

        this.productOptions = productOptions;
        this.fieldOfficerOptions = fieldOfficerOptions;
        this.interestCompoundingPeriodTypeOptions = interestCompoundingPeriodTypeOptions;
        this.interestPostingPeriodTypeOptions = interestPostingPeriodTypeOptions;
        this.interestCalculationTypeOptions = interestCalculationTypeOptions;
        this.interestCalculationDaysInYearTypeOptions = interestCalculationDaysInYearTypeOptions;
        this.lockinPeriodFrequencyTypeOptions = lockinPeriodFrequencyTypeOptions;
        this.withdrawalFeeTypeOptions = withdrawalFeeTypeOptions;

        this.charges = charges;// charges associated with Savings account
        // charges available for adding to Savings account
        this.chargeOptions = chargeOptions;
        
        this.withdrawalFee = getWithdrawalFee();
        
        this.annualFee = getAnnualFee();
    }

    private SavingsAccountChargeData getWithdrawalFee() {
        for (SavingsAccountChargeData charge : this.charges()) {
            if (charge.isWithdrawalFee()) return charge;
        }
        return null;
    }
    
    private SavingsAccountChargeData getAnnualFee() {
        for (SavingsAccountChargeData charge : this.charges()) {
            if (charge.isAnnualFee()) return charge;
        }
        return null;
    }    

    public Long id() {
        return this.id;
    }

    public Long clientId() {
        return this.clientId;
    }

    public Long groupId() {
        return this.groupId;
    }

    public Long productId() {
        return this.savingsProductId;
    }

    public CurrencyData currency() {
        return this.currency;
    }

    @Override
    public boolean equals(final Object obj) {

        if (obj == null) { return false; }
        if (obj == this) { return true; }
        if (obj.getClass() != getClass()) { return false; }
        final SavingsAccountData rhs = (SavingsAccountData) obj;
        return new EqualsBuilder().append(this.id, rhs.id).append(this.accountNo, rhs.accountNo).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(this.id).append(this.accountNo).toHashCode();
    }
    
    public Collection<SavingsAccountChargeData> charges() {
        return (this.charges == null) ? new HashSet<SavingsAccountChargeData>() : this.charges;
    }
}