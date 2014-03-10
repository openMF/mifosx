/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.savings.data;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.mifosplatform.accounting.glaccount.data.GLAccountData;
import org.mifosplatform.accounting.producttoaccountmapping.data.ChargeToGLAccountMapper;
import org.mifosplatform.accounting.producttoaccountmapping.data.PaymentTypeToGLAccountMapper;
import org.mifosplatform.infrastructure.codes.data.CodeValueData;
import org.mifosplatform.infrastructure.core.data.EnumOptionData;
import org.mifosplatform.organisation.monetary.data.CurrencyData;
import org.mifosplatform.portfolio.charge.data.ChargeData;
import org.mifosplatform.portfolio.interestratechart.data.InterestRateChartData;

/**
 * Immutable data object representing a Fixed Deposit product.
 */
public class FixedDepositProductData extends DepositProductData {

    // additional fields
    private boolean interestFreePeriodApplicable;
    private Integer interestFreeFromPeriod;
    private Integer interestFreeToPeriod;
    protected EnumOptionData interestFreePeriodFrequencyType;
    private boolean preClosurePenalApplicable;
    protected BigDecimal preClosurePenalInterest;
    protected EnumOptionData preClosurePenalInterestOnType;
    protected Integer minDepositTerm;
    protected Integer maxDepositTerm;
    protected EnumOptionData depositTermType;
    protected Integer inMultiplesOfDepositTerm;
    protected EnumOptionData inMultiplesOfDepositTermType;

    private Collection<EnumOptionData> interestFreePeriodTypeOptions;
    private Collection<EnumOptionData> preClosurePenalInterestOnTypeOptions;
    private Collection<EnumOptionData> depositTermTypeOptions;
    private Collection<EnumOptionData> inMultiplesOfDepositTermTypeOptions;

    public static FixedDepositProductData template(final CurrencyData currency, final EnumOptionData interestCompoundingPeriodType,
            final EnumOptionData interestPostingPeriodType, final EnumOptionData interestCalculationType,
            final EnumOptionData interestCalculationDaysInYearType, final EnumOptionData accountingRule,
            final Collection<CurrencyData> currencyOptions, final Collection<EnumOptionData> interestCompoundingPeriodTypeOptions,
            final Collection<EnumOptionData> interestPostingPeriodTypeOptions,
            final Collection<EnumOptionData> interestCalculationTypeOptions,
            final Collection<EnumOptionData> interestCalculationDaysInYearTypeOptions,
            final Collection<EnumOptionData> lockinPeriodFrequencyTypeOptions, final Collection<EnumOptionData> withdrawalFeeTypeOptions,
            final Collection<CodeValueData> paymentTypeOptions, final Collection<EnumOptionData> accountingRuleOptions,
            final Map<String, List<GLAccountData>> accountingMappingOptions, final Collection<ChargeData> chargeOptions,
            final Collection<ChargeData> penaltyOptions, final InterestRateChartData chartTemplate,
            final Collection<EnumOptionData> interestFreePeriodTypes,
            final Collection<EnumOptionData> preClosurePenalInterestOnTypeOptions, final Collection<EnumOptionData> depositTermTypeOptions,
            final Collection<EnumOptionData> inMultiplesOfDepositTermTypeOptions) {

        final Long id = null;
        final String name = null;
        final String shortName = null;
        final String description = null;
        final BigDecimal nominalAnnualInterestRate = null;
        final Integer lockinPeriodFrequency = null;
        final EnumOptionData lockinPeriodFrequencyType = null;
        final Map<String, Object> accountingMappings = null;
        final Collection<PaymentTypeToGLAccountMapper> paymentChannelToFundSourceMappings = null;
        final Collection<ChargeData> charges = null;
        final Collection<ChargeToGLAccountMapper> feeToIncomeAccountMappings = null;
        final Collection<ChargeToGLAccountMapper> penaltyToIncomeAccountMappings = null;
        final Collection<InterestRateChartData> interestRateCharts = null;
        final boolean interestFreePeriodApplicable = false;
        final Integer interestFreeFromPeriod = null;
        final Integer interestFreeToPeriod = null;
        final EnumOptionData interestFreePeriodFrequencyType = null;
        final boolean preClosurePenalApplicable = false;
        final BigDecimal preClosurePenalInterest = null;
        final EnumOptionData preClosurePenalInterestOnType = null;
        final Integer minDepositTerm = null;
        final Integer maxDepositTerm = null;
        final EnumOptionData depositTermType = null;
        final Integer inMultiplesOfDepositTerm = null;
        final EnumOptionData inMultiplesOfDepositTermType = null;

        return new FixedDepositProductData(id, name, shortName, description, currency, nominalAnnualInterestRate,
                interestCompoundingPeriodType, interestPostingPeriodType, interestCalculationType, interestCalculationDaysInYearType,
                lockinPeriodFrequency, lockinPeriodFrequencyType, accountingRule, accountingMappings, paymentChannelToFundSourceMappings,
                currencyOptions, interestCompoundingPeriodTypeOptions, interestPostingPeriodTypeOptions, interestCalculationTypeOptions,
                interestCalculationDaysInYearTypeOptions, lockinPeriodFrequencyTypeOptions, withdrawalFeeTypeOptions, paymentTypeOptions,
                accountingRuleOptions, accountingMappingOptions, charges, chargeOptions, penaltyOptions, feeToIncomeAccountMappings,
                penaltyToIncomeAccountMappings, interestRateCharts, chartTemplate, interestFreePeriodApplicable, interestFreeFromPeriod,
                interestFreeToPeriod, interestFreePeriodFrequencyType, preClosurePenalApplicable, preClosurePenalInterest,
                preClosurePenalInterestOnType, interestFreePeriodTypes, preClosurePenalInterestOnTypeOptions, minDepositTerm,
                maxDepositTerm, depositTermType, inMultiplesOfDepositTerm, inMultiplesOfDepositTermType, depositTermTypeOptions,
                inMultiplesOfDepositTermTypeOptions);
    }

    public static FixedDepositProductData withCharges(final FixedDepositProductData existingProduct, final Collection<ChargeData> charges) {
        return new FixedDepositProductData(existingProduct.id, existingProduct.name, existingProduct.shortName,
                existingProduct.description, existingProduct.currency, existingProduct.nominalAnnualInterestRate,
                existingProduct.interestCompoundingPeriodType, existingProduct.interestPostingPeriodType,
                existingProduct.interestCalculationType, existingProduct.interestCalculationDaysInYearType,
                existingProduct.lockinPeriodFrequency, existingProduct.lockinPeriodFrequencyType, existingProduct.accountingRule,
                existingProduct.accountingMappings, existingProduct.paymentChannelToFundSourceMappings, existingProduct.currencyOptions,
                existingProduct.interestCompoundingPeriodTypeOptions, existingProduct.interestPostingPeriodTypeOptions,
                existingProduct.interestCalculationTypeOptions, existingProduct.interestCalculationDaysInYearTypeOptions,
                existingProduct.lockinPeriodFrequencyTypeOptions, existingProduct.withdrawalFeeTypeOptions,
                existingProduct.paymentTypeOptions, existingProduct.accountingRuleOptions, existingProduct.accountingMappingOptions,
                charges, existingProduct.chargeOptions, existingProduct.penaltyOptions, existingProduct.feeToIncomeAccountMappings,
                existingProduct.penaltyToIncomeAccountMappings, existingProduct.interestRateCharts, existingProduct.chartTemplate,
                existingProduct.interestFreePeriodApplicable, existingProduct.interestFreeFromPeriod, existingProduct.interestFreeToPeriod,
                existingProduct.interestFreePeriodFrequencyType, existingProduct.preClosurePenalApplicable,
                existingProduct.preClosurePenalInterest, existingProduct.preClosurePenalInterestOnType,
                existingProduct.interestFreePeriodTypeOptions, existingProduct.preClosurePenalInterestOnTypeOptions,
                existingProduct.minDepositTerm, existingProduct.maxDepositTerm, existingProduct.depositTermType,
                existingProduct.inMultiplesOfDepositTerm, existingProduct.inMultiplesOfDepositTermType,
                existingProduct.depositTermTypeOptions, existingProduct.inMultiplesOfDepositTermTypeOptions);
    }

    /**
     * Returns a {@link FixedDepositProductData} that contains and exist
     * {@link FixedDepositProductData} data with further template data for
     * dropdowns.
     */
    public static FixedDepositProductData withTemplate(final FixedDepositProductData existingProduct,
            final Collection<CurrencyData> currencyOptions, final Collection<EnumOptionData> interestCompoundingPeriodTypeOptions,
            final Collection<EnumOptionData> interestPostingPeriodTypeOptions,
            final Collection<EnumOptionData> interestCalculationTypeOptions,
            final Collection<EnumOptionData> interestCalculationDaysInYearTypeOptions,
            final Collection<EnumOptionData> lockinPeriodFrequencyTypeOptions, final Collection<EnumOptionData> withdrawalFeeTypeOptions,
            final Collection<CodeValueData> paymentTypeOptions, final Collection<EnumOptionData> accountingRuleOptions,
            final Map<String, List<GLAccountData>> accountingMappingOptions, final Collection<ChargeData> chargeOptions,
            final Collection<ChargeData> penaltyOptions, final InterestRateChartData chartTemplate,
            final Collection<EnumOptionData> interestFreePeriodTypes,
            final Collection<EnumOptionData> preClosurePenalInterestOnTypeOptions, final Collection<EnumOptionData> depositTermTypeOptions,
            final Collection<EnumOptionData> inMultiplesOfDepositTermTypeOptions) {

        return new FixedDepositProductData(existingProduct.id, existingProduct.name, existingProduct.shortName,
                existingProduct.description, existingProduct.currency, existingProduct.nominalAnnualInterestRate,
                existingProduct.interestCompoundingPeriodType, existingProduct.interestPostingPeriodType,
                existingProduct.interestCalculationType, existingProduct.interestCalculationDaysInYearType,
                existingProduct.lockinPeriodFrequency, existingProduct.lockinPeriodFrequencyType, existingProduct.accountingRule,
                existingProduct.accountingMappings, existingProduct.paymentChannelToFundSourceMappings, currencyOptions,
                interestCompoundingPeriodTypeOptions, interestPostingPeriodTypeOptions, interestCalculationTypeOptions,
                interestCalculationDaysInYearTypeOptions, lockinPeriodFrequencyTypeOptions, withdrawalFeeTypeOptions, paymentTypeOptions,
                accountingRuleOptions, accountingMappingOptions, existingProduct.charges, chargeOptions, penaltyOptions,
                existingProduct.feeToIncomeAccountMappings, existingProduct.penaltyToIncomeAccountMappings,
                existingProduct.interestRateCharts, chartTemplate, existingProduct.interestFreePeriodApplicable,
                existingProduct.interestFreeFromPeriod, existingProduct.interestFreeToPeriod,
                existingProduct.interestFreePeriodFrequencyType, existingProduct.preClosurePenalApplicable,
                existingProduct.preClosurePenalInterest, existingProduct.preClosurePenalInterestOnType, interestFreePeriodTypes,
                preClosurePenalInterestOnTypeOptions, existingProduct.minDepositTerm, existingProduct.maxDepositTerm,
                existingProduct.depositTermType, existingProduct.inMultiplesOfDepositTerm, existingProduct.inMultiplesOfDepositTermType,
                depositTermTypeOptions, inMultiplesOfDepositTermTypeOptions);
    }

    public static FixedDepositProductData withAccountingDetails(final FixedDepositProductData existingProduct,
            final Map<String, Object> accountingMappings,
            final Collection<PaymentTypeToGLAccountMapper> paymentChannelToFundSourceMappings,
            final Collection<ChargeToGLAccountMapper> feeToIncomeAccountMappings,
            final Collection<ChargeToGLAccountMapper> penaltyToIncomeAccountMappings) {

        final Collection<CurrencyData> currencyOptions = null;
        final Collection<EnumOptionData> interestCompoundingPeriodTypeOptions = null;
        final Collection<EnumOptionData> interestPostingPeriodTypeOptions = null;
        final Collection<EnumOptionData> interestCalculationTypeOptions = null;
        final Collection<EnumOptionData> interestCalculationDaysInYearTypeOptions = null;
        final Collection<EnumOptionData> lockinPeriodFrequencyTypeOptions = null;
        final Collection<EnumOptionData> withdrawalFeeTypeOptions = null;
        final Collection<CodeValueData> paymentTypeOptions = null;
        final Collection<EnumOptionData> accountingRuleOptions = null;
        final Map<String, List<GLAccountData>> accountingMappingOptions = null;
        final Collection<ChargeData> chargeOptions = null;
        final Collection<ChargeData> penaltyOptions = null;

        return new FixedDepositProductData(existingProduct.id, existingProduct.name, existingProduct.shortName,
                existingProduct.description, existingProduct.currency, existingProduct.nominalAnnualInterestRate,
                existingProduct.interestCompoundingPeriodType, existingProduct.interestPostingPeriodType,
                existingProduct.interestCalculationType, existingProduct.interestCalculationDaysInYearType,
                existingProduct.lockinPeriodFrequency, existingProduct.lockinPeriodFrequencyType, existingProduct.accountingRule,
                accountingMappings, paymentChannelToFundSourceMappings, currencyOptions, interestCompoundingPeriodTypeOptions,
                interestPostingPeriodTypeOptions, interestCalculationTypeOptions, interestCalculationDaysInYearTypeOptions,
                lockinPeriodFrequencyTypeOptions, withdrawalFeeTypeOptions, paymentTypeOptions, accountingRuleOptions,
                accountingMappingOptions, existingProduct.charges, chargeOptions, penaltyOptions, feeToIncomeAccountMappings,
                penaltyToIncomeAccountMappings, existingProduct.interestRateCharts, existingProduct.chartTemplate,
                existingProduct.interestFreePeriodApplicable, existingProduct.interestFreeFromPeriod, existingProduct.interestFreeToPeriod,
                existingProduct.interestFreePeriodFrequencyType, existingProduct.preClosurePenalApplicable,
                existingProduct.preClosurePenalInterest, existingProduct.preClosurePenalInterestOnType,
                existingProduct.interestFreePeriodTypeOptions, existingProduct.preClosurePenalInterestOnTypeOptions,
                existingProduct.minDepositTerm, existingProduct.maxDepositTerm, existingProduct.depositTermType,
                existingProduct.inMultiplesOfDepositTerm, existingProduct.inMultiplesOfDepositTermType,
                existingProduct.depositTermTypeOptions, existingProduct.inMultiplesOfDepositTermTypeOptions);
    }

    public static FixedDepositProductData instance(final DepositProductData depositProductData, final boolean interestFreePeriodApplicable,
            final Integer interestFreeFromPeriod, final Integer interestFreeToPeriod, final EnumOptionData interestFreePeriodFrequencyType,
            final boolean preClosurePenalApplicable, final BigDecimal preClosurePenalInterest,
            final EnumOptionData preClosurePenalInterestOnType, final Integer minDepositTerm, final Integer maxDepositTerm,
            final EnumOptionData depositTermType, final Integer inMultiplesOfDepositTerm, final EnumOptionData inMultiplesOfDepositTermType) {

        final Map<String, Object> accountingMappings = null;
        final Collection<PaymentTypeToGLAccountMapper> paymentChannelToFundSourceMappings = null;

        final Collection<CurrencyData> currencyOptions = null;
        final Collection<EnumOptionData> interestCompoundingPeriodTypeOptions = null;
        final Collection<EnumOptionData> interestPostingPeriodTypeOptions = null;
        final Collection<EnumOptionData> interestCalculationTypeOptions = null;
        final Collection<EnumOptionData> interestCalculationDaysInYearTypeOptions = null;
        final Collection<EnumOptionData> lockinPeriodFrequencyTypeOptions = null;
        final Collection<EnumOptionData> withdrawalFeeTypeOptions = null;
        final Collection<CodeValueData> paymentTypeOptions = null;
        final Collection<EnumOptionData> accountingRuleOptions = null;
        final Map<String, List<GLAccountData>> accountingMappingOptions = null;
        final Collection<ChargeData> chargeOptions = null;
        final Collection<ChargeData> penaltyOptions = null;
        final Collection<ChargeData> charges = null;
        final Collection<ChargeToGLAccountMapper> feeToIncomeAccountMappings = null;
        final Collection<ChargeToGLAccountMapper> penaltyToIncomeAccountMappings = null;
        final Collection<InterestRateChartData> interestRateCharts = null;
        final InterestRateChartData chartTemplate = null;
        final Collection<EnumOptionData> interestFreePeriodTypes = null;
        final Collection<EnumOptionData> preClosurePenalInterestOnTypeOptions = null;
        final Collection<EnumOptionData> depositTermTypeOptions = null;
        final Collection<EnumOptionData> inMultiplesOfDepositTermTypeOptions = null;

        return new FixedDepositProductData(depositProductData.id, depositProductData.name, depositProductData.shortName,
                depositProductData.description, depositProductData.currency, depositProductData.nominalAnnualInterestRate,
                depositProductData.interestCompoundingPeriodType, depositProductData.interestPostingPeriodType,
                depositProductData.interestCalculationType, depositProductData.interestCalculationDaysInYearType,
                depositProductData.lockinPeriodFrequency, depositProductData.lockinPeriodFrequencyType, depositProductData.accountingRule,
                accountingMappings, paymentChannelToFundSourceMappings, currencyOptions, interestCompoundingPeriodTypeOptions,
                interestPostingPeriodTypeOptions, interestCalculationTypeOptions, interestCalculationDaysInYearTypeOptions,
                lockinPeriodFrequencyTypeOptions, withdrawalFeeTypeOptions, paymentTypeOptions, accountingRuleOptions,
                accountingMappingOptions, charges, chargeOptions, penaltyOptions, feeToIncomeAccountMappings,
                penaltyToIncomeAccountMappings, interestRateCharts, chartTemplate, interestFreePeriodApplicable, interestFreeFromPeriod,
                interestFreeToPeriod, interestFreePeriodFrequencyType, preClosurePenalApplicable, preClosurePenalInterest,
                preClosurePenalInterestOnType, interestFreePeriodTypes, preClosurePenalInterestOnTypeOptions, minDepositTerm,
                maxDepositTerm, depositTermType, inMultiplesOfDepositTerm, inMultiplesOfDepositTermType, depositTermTypeOptions,
                inMultiplesOfDepositTermTypeOptions);
    }

    public static FixedDepositProductData lookup(final Long id, final String name) {

        final String shortName = null;
        final CurrencyData currency = null;
        final String description = null;
        final BigDecimal nominalAnnualInterestRate = null;
        final EnumOptionData interestCompoundingPeriodType = null;
        final EnumOptionData interestPostingPeriodType = null;
        final EnumOptionData interestCalculationType = null;
        final EnumOptionData interestCalculationDaysInYearType = null;
        final Integer lockinPeriodFrequency = null;
        final EnumOptionData lockinPeriodFrequencyType = null;
        final EnumOptionData accountingType = null;
        final Map<String, Object> accountingMappings = null;
        final Collection<PaymentTypeToGLAccountMapper> paymentChannelToFundSourceMappings = null;

        final Collection<CurrencyData> currencyOptions = null;
        final Collection<EnumOptionData> interestCompoundingPeriodTypeOptions = null;
        final Collection<EnumOptionData> interestPostingPeriodTypeOptions = null;
        final Collection<EnumOptionData> interestCalculationTypeOptions = null;
        final Collection<EnumOptionData> interestCalculationDaysInYearTypeOptions = null;
        final Collection<EnumOptionData> lockinPeriodFrequencyTypeOptions = null;
        final Collection<EnumOptionData> withdrawalFeeTypeOptions = null;
        final Collection<CodeValueData> paymentTypeOptions = null;
        final Collection<EnumOptionData> accountingRuleOptions = null;
        final Map<String, List<GLAccountData>> accountingMappingOptions = null;
        final Collection<ChargeData> charges = null;
        final Collection<ChargeData> chargeOptions = null;
        final Collection<ChargeData> penaltyOptions = null;
        final Collection<ChargeToGLAccountMapper> feeToIncomeAccountMappings = null;
        final Collection<ChargeToGLAccountMapper> penaltyToIncomeAccountMappings = null;
        final Collection<InterestRateChartData> interestRateCharts = null;
        final InterestRateChartData chartTemplate = null;
        final boolean interestFreePeriodApplicable = false;
        final Integer interestFreeFromPeriod = null;
        final Integer interestFreeToPeriod = null;
        final EnumOptionData interestFreePeriodFrequencyType = null;
        final boolean preClosurePenalApplicable = false;
        final BigDecimal preClosurePenalInterest = null;
        final EnumOptionData preClosurePenalInterestOnType = null;
        final Collection<EnumOptionData> interestFreePeriodTypes = null;
        final Collection<EnumOptionData> preClosurePenalInterestOnTypeOptions = null;
        final Integer minDepositTerm = null;
        final Integer maxDepositTerm = null;
        final EnumOptionData depositTermType = null;
        final Integer inMultiplesOfDepositTerm = null;
        final EnumOptionData inMultiplesOfDepositTermType = null;
        final Collection<EnumOptionData> depositTermTypeOptions = null;
        final Collection<EnumOptionData> inMultiplesOfDepositTermTypeOptions = null;

        return new FixedDepositProductData(id, name, shortName, description, currency, nominalAnnualInterestRate,
                interestCompoundingPeriodType, interestPostingPeriodType, interestCalculationType, interestCalculationDaysInYearType,
                lockinPeriodFrequency, lockinPeriodFrequencyType, accountingType, accountingMappings, paymentChannelToFundSourceMappings,
                currencyOptions, interestCompoundingPeriodTypeOptions, interestPostingPeriodTypeOptions, interestCalculationTypeOptions,
                interestCalculationDaysInYearTypeOptions, lockinPeriodFrequencyTypeOptions, withdrawalFeeTypeOptions, paymentTypeOptions,
                accountingRuleOptions, accountingMappingOptions, charges, chargeOptions, penaltyOptions, feeToIncomeAccountMappings,
                penaltyToIncomeAccountMappings, interestRateCharts, chartTemplate, interestFreePeriodApplicable, interestFreeFromPeriod,
                interestFreeToPeriod, interestFreePeriodFrequencyType, preClosurePenalApplicable, preClosurePenalInterest,
                preClosurePenalInterestOnType, interestFreePeriodTypes, preClosurePenalInterestOnTypeOptions, minDepositTerm,
                maxDepositTerm, depositTermType, inMultiplesOfDepositTerm, inMultiplesOfDepositTermType, depositTermTypeOptions,
                inMultiplesOfDepositTermTypeOptions);
    }

    public static FixedDepositProductData withInterestChart(final FixedDepositProductData existingProduct,
            final Collection<InterestRateChartData> interestRateCharts) {
        return new FixedDepositProductData(existingProduct.id, existingProduct.name, existingProduct.shortName,
                existingProduct.description, existingProduct.currency, existingProduct.nominalAnnualInterestRate,
                existingProduct.interestCompoundingPeriodType, existingProduct.interestPostingPeriodType,
                existingProduct.interestCalculationType, existingProduct.interestCalculationDaysInYearType,
                existingProduct.lockinPeriodFrequency, existingProduct.lockinPeriodFrequencyType, existingProduct.accountingRule,
                existingProduct.accountingMappings, existingProduct.paymentChannelToFundSourceMappings, existingProduct.currencyOptions,
                existingProduct.interestCompoundingPeriodTypeOptions, existingProduct.interestPostingPeriodTypeOptions,
                existingProduct.interestCalculationTypeOptions, existingProduct.interestCalculationDaysInYearTypeOptions,
                existingProduct.lockinPeriodFrequencyTypeOptions, existingProduct.withdrawalFeeTypeOptions,
                existingProduct.paymentTypeOptions, existingProduct.accountingRuleOptions, existingProduct.accountingMappingOptions,
                existingProduct.charges, existingProduct.chargeOptions, existingProduct.penaltyOptions,
                existingProduct.feeToIncomeAccountMappings, existingProduct.penaltyToIncomeAccountMappings, interestRateCharts,
                existingProduct.chartTemplate, existingProduct.interestFreePeriodApplicable, existingProduct.interestFreeFromPeriod,
                existingProduct.interestFreeToPeriod, existingProduct.interestFreePeriodFrequencyType,
                existingProduct.preClosurePenalApplicable, existingProduct.preClosurePenalInterest,
                existingProduct.preClosurePenalInterestOnType, existingProduct.interestFreePeriodTypeOptions,
                existingProduct.preClosurePenalInterestOnTypeOptions, existingProduct.minDepositTerm, existingProduct.maxDepositTerm,
                existingProduct.depositTermType, existingProduct.inMultiplesOfDepositTerm, existingProduct.inMultiplesOfDepositTermType,
                existingProduct.depositTermTypeOptions, existingProduct.inMultiplesOfDepositTermTypeOptions);
    }

    private FixedDepositProductData(final Long id, final String name, final String shortName, final String description,
            final CurrencyData currency, final BigDecimal nominalAnnualInterestRate, final EnumOptionData interestCompoundingPeriodType,
            final EnumOptionData interestPostingPeriodType, final EnumOptionData interestCalculationType,
            final EnumOptionData interestCalculationDaysInYearType, final Integer lockinPeriodFrequency,
            final EnumOptionData lockinPeriodFrequencyType, final EnumOptionData accountingType,
            final Map<String, Object> accountingMappings,
            final Collection<PaymentTypeToGLAccountMapper> paymentChannelToFundSourceMappings,
            final Collection<CurrencyData> currencyOptions, final Collection<EnumOptionData> interestCompoundingPeriodTypeOptions,
            final Collection<EnumOptionData> interestPostingPeriodTypeOptions,
            final Collection<EnumOptionData> interestCalculationTypeOptions,
            final Collection<EnumOptionData> interestCalculationDaysInYearTypeOptions,
            final Collection<EnumOptionData> lockinPeriodFrequencyTypeOptions, final Collection<EnumOptionData> withdrawalFeeTypeOptions,
            final Collection<CodeValueData> paymentTypeOptions, final Collection<EnumOptionData> accountingRuleOptions,
            final Map<String, List<GLAccountData>> accountingMappingOptions, final Collection<ChargeData> charges,
            final Collection<ChargeData> chargeOptions, final Collection<ChargeData> penaltyOptions,
            final Collection<ChargeToGLAccountMapper> feeToIncomeAccountMappings,
            final Collection<ChargeToGLAccountMapper> penaltyToIncomeAccountMappings,
            final Collection<InterestRateChartData> interestRateCharts, final InterestRateChartData chartTemplate,
            final boolean interestFreePeriodApplicable, final Integer interestFreeFromPeriod, final Integer interestFreeToPeriod,
            final EnumOptionData interestFreePeriodFrequencyType, final boolean preClosurePenalApplicable,
            final BigDecimal preClosurePenalInterest, final EnumOptionData preClosurePenalInterestOnType,
            final Collection<EnumOptionData> interestFreePeriodTypeOptions,
            final Collection<EnumOptionData> preClosurePenalInterestOnTypeOptions, final Integer minDepositTerm,
            final Integer maxDepositTerm, final EnumOptionData depositTermType, final Integer inMultiplesOfDepositTerm,
            final EnumOptionData inMultiplesOfDepositTermType, final Collection<EnumOptionData> depositTermTypeOptions,
            Collection<EnumOptionData> inMultiplesOfDepositTermTypeOptions) {

        super(id, name, shortName, description, currency, nominalAnnualInterestRate, interestCompoundingPeriodType,
                interestPostingPeriodType, interestCalculationType, interestCalculationDaysInYearType, lockinPeriodFrequency,
                lockinPeriodFrequencyType, accountingType, accountingMappings, paymentChannelToFundSourceMappings, currencyOptions,
                interestCompoundingPeriodTypeOptions, interestPostingPeriodTypeOptions, interestCalculationTypeOptions,
                interestCalculationDaysInYearTypeOptions, lockinPeriodFrequencyTypeOptions, withdrawalFeeTypeOptions, paymentTypeOptions,
                accountingRuleOptions, accountingMappingOptions, charges, chargeOptions, penaltyOptions, feeToIncomeAccountMappings,
                penaltyToIncomeAccountMappings, interestRateCharts, chartTemplate);

        // fixed deposit additional fields
        this.interestFreePeriodApplicable = interestFreePeriodApplicable;
        this.interestFreeFromPeriod = interestFreeFromPeriod;
        this.interestFreeToPeriod = interestFreeToPeriod;
        this.interestFreePeriodFrequencyType = interestFreePeriodFrequencyType;
        this.preClosurePenalApplicable = preClosurePenalApplicable;
        this.preClosurePenalInterest = preClosurePenalInterest;
        this.preClosurePenalInterestOnType = preClosurePenalInterestOnType;
        this.minDepositTerm = minDepositTerm;
        this.maxDepositTerm = maxDepositTerm;
        this.depositTermType = depositTermType;
        this.inMultiplesOfDepositTerm = inMultiplesOfDepositTerm;
        this.inMultiplesOfDepositTermType = inMultiplesOfDepositTermType;

        // template
        this.interestFreePeriodTypeOptions = interestFreePeriodTypeOptions;
        this.preClosurePenalInterestOnTypeOptions = preClosurePenalInterestOnTypeOptions;
        this.depositTermTypeOptions = depositTermTypeOptions;
        this.inMultiplesOfDepositTermTypeOptions = inMultiplesOfDepositTermTypeOptions;
    }

}