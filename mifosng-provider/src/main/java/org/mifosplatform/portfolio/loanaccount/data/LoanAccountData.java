/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. .0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/.0/.
 */
package org.mifosplatform.portfolio.loanaccount.data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;

import javax.persistence.Transient;

import org.joda.time.LocalDate;
import org.mifosplatform.infrastructure.codes.data.CodeValueData;
import org.mifosplatform.infrastructure.core.data.EnumOptionData;
import org.mifosplatform.organisation.monetary.data.CurrencyData;
import org.mifosplatform.organisation.staff.data.StaffData;
import org.mifosplatform.portfolio.charge.data.ChargeData;
import org.mifosplatform.portfolio.collateral.data.CollateralData;
import org.mifosplatform.portfolio.fund.data.FundData;
import org.mifosplatform.portfolio.group.data.GroupGeneralData;
import org.mifosplatform.portfolio.loanaccount.guarantor.data.GuarantorData;
import org.mifosplatform.portfolio.loanaccount.loanschedule.data.LoanScheduleData;
import org.mifosplatform.portfolio.loanproduct.data.LoanProductData;
import org.mifosplatform.portfolio.loanproduct.data.TransactionProcessingStrategyData;
import org.springframework.util.CollectionUtils;

/**
 * Immutable data object representing loan account data.
 */
@SuppressWarnings("unused")
public class LoanAccountData {

    // basic loan details

    // identity
    private final Long id;
    private final String accountNo;
    private final String externalId;

    // status
    private final LoanStatusEnumData status;

    // related to
    private final Long clientId;
    private final String clientName;
    private final Long clientOfficeId;
    private final GroupGeneralData group;
    private final Long loanProductId;
    private final String loanProductName;
    private final String loanProductDescription;
    private final Long fundId;
    private final String fundName;
    private final Long loanPurposeId;
    private final String loanPurposeName;
    private final Long loanOfficerId;
    private final String loanOfficerName;

    // terms
    private final CurrencyData currency;
    private final BigDecimal principal;
    private final Integer termFrequency;
    private final EnumOptionData termPeriodFrequencyType;
    private final Integer numberOfRepayments;
    private final Integer repaymentEvery;
    private final EnumOptionData repaymentFrequencyType;
    private final BigDecimal interestRatePerPeriod;
    private final EnumOptionData interestRateFrequencyType;
    private final BigDecimal annualInterestRate;

    // settings
    private final EnumOptionData amortizationType;
    private final EnumOptionData interestType;
    private final EnumOptionData interestCalculationPeriodType;
    private final BigDecimal inArrearsTolerance;
    private final Long transactionProcessingStrategyId;
    private final LocalDate expectedFirstRepaymentOnDate;
    private final LocalDate interestChargedFromDate;

    // timeline
    private final LoanApplicationTimelineData timeline;

    // totals
    private final LoanSummaryData summary;

    // associations
    private final LoanScheduleData repaymentSchedule;
    private final Collection<LoanTransactionData> transactions;
    private final Collection<LoanChargeData> charges;
    private final Collection<CollateralData> collateral;
    private final Collection<GuarantorData> guarantors;

    // template
    private final Collection<LoanProductData> productOptions;
    private final Collection<StaffData> loanOfficerOptions;
    private final Collection<CodeValueData> loanPurposeOptions;
    private final Collection<FundData> fundOptions;
    private final Collection<EnumOptionData> termFrequencyTypeOptions;
    private final Collection<EnumOptionData> repaymentFrequencyTypeOptions;
    private final Collection<EnumOptionData> interestRateFrequencyTypeOptions;
    private final Collection<EnumOptionData> amortizationTypeOptions;
    private final Collection<EnumOptionData> interestTypeOptions;
    private final Collection<EnumOptionData> interestCalculationPeriodTypeOptions;
    private final Collection<TransactionProcessingStrategyData> transactionProcessingStrategyOptions;
    private final Collection<ChargeData> chargeOptions;
    private final Collection<CodeValueData> loanCollateralOptions;

    @Transient
    private final BigDecimal feeChargesAtDisbursementCharged;

    /**
     * Used to produce a {@link LoanAccountData} with only collateral options
     * for now.
     */
    public static LoanAccountData collateralTemplate(final Collection<CodeValueData> loanCollateralOptions) {
        final Long id = null;
        final String accountNo = null;
        final LoanStatusEnumData status = null;
        final String externalId = null;
        final Long clientId = null;
        final String clientName = null;
        final Long clientOfficeId = null;
        final GroupGeneralData group = null;
        final Long loanProductId = null;
        final String loanProductName = null;
        final String loanProductDescription = null;
        final Long fundId = null;
        final String fundName = null;
        final Long loanPurposeId = null;
        final String loanPurposeName = null;
        final Long loanOfficerId = null;
        final String loanOfficerName = null;
        final CurrencyData currencyData = null;
        final BigDecimal principal = null;
        final BigDecimal inArrearsTolerance = null;
        final Integer termFrequency = null;
        final EnumOptionData termPeriodFrequencyType = null;
        final Integer numberOfRepayments = null;
        final Integer repaymentEvery = null;
        final EnumOptionData repaymentFrequencyType = null;
        final Long transactionProcessingStrategyId = null;
        final EnumOptionData amortizationType = null;
        final BigDecimal interestRatePerPeriod = null;
        final EnumOptionData interestRateFrequencyType = null;
        final BigDecimal annualInterestRate = null;
        final EnumOptionData interestType = null;
        final EnumOptionData interestCalculationPeriodType = null;
        final LocalDate expectedFirstRepaymentOnDate = null;
        final LocalDate interestChargedFromDate = null;
        final LoanApplicationTimelineData timeline = null;
        final LoanSummaryData summary = null;
        final BigDecimal feeChargesDueAtDisbursementCharged = null;

        final LoanScheduleData repaymentSchedule = null;
        final Collection<LoanTransactionData> transactions = null;
        final Collection<LoanChargeData> charges = null;
        final Collection<CollateralData> collateral = null;
        final Collection<GuarantorData> guarantors = null;
        final Collection<LoanProductData> productOptions = null;
        final Collection<EnumOptionData> termFrequencyTypeOptions = null;
        final Collection<EnumOptionData> repaymentFrequencyTypeOptions = null;
        final Collection<TransactionProcessingStrategyData> transactionProcessingStrategyOptions = null;
        final Collection<EnumOptionData> interestRateFrequencyTypeOptions = null;
        final Collection<EnumOptionData> amortizationTypeOptions = null;
        final Collection<EnumOptionData> interestTypeOptions = null;
        final Collection<EnumOptionData> interestCalculationPeriodTypeOptions = null;
        final Collection<FundData> fundOptions = null;
        final Collection<ChargeData> chargeOptions = null;
        final ChargeData chargeTemplate = null;
        final Collection<StaffData> loanOfficerOptions = null;
        final Collection<CodeValueData> loanPurposeOptions = null;

        return new LoanAccountData(id, accountNo, status, externalId, clientId, clientName, clientOfficeId, group, loanProductId,
                loanProductName, loanProductDescription, fundId, fundName, loanPurposeId, loanPurposeName, loanOfficerId, loanOfficerName,
                currencyData, principal, inArrearsTolerance, termFrequency, termPeriodFrequencyType, numberOfRepayments, repaymentEvery,
                repaymentFrequencyType, transactionProcessingStrategyId, amortizationType, interestRatePerPeriod,
                interestRateFrequencyType, annualInterestRate, interestType, interestCalculationPeriodType, expectedFirstRepaymentOnDate,
                interestChargedFromDate, timeline, summary, feeChargesDueAtDisbursementCharged, repaymentSchedule, transactions, charges,
                collateral, guarantors, productOptions, termFrequencyTypeOptions, repaymentFrequencyTypeOptions,
                transactionProcessingStrategyOptions, interestRateFrequencyTypeOptions, amortizationTypeOptions, interestTypeOptions,
                interestCalculationPeriodTypeOptions, fundOptions, chargeOptions, chargeTemplate, loanOfficerOptions, loanPurposeOptions,
                loanCollateralOptions);
    }

    /**
     * Used to produce a {@link LoanAccountData} with only client information
     * defaulted.
     */
    public static LoanAccountData clientDefaults(final Long clientId, final String clientName, final Long clientOfficeId,
            final LocalDate expectedDisbursementDate) {
        final Long id = null;
        final String accountNo = null;
        final LoanStatusEnumData status = null;
        final String externalId = null;
        final GroupGeneralData group = null;
        final String groupName = null;
        final Long loanProductId = null;
        final String loanProductName = null;
        final String loanProductDescription = null;
        final Long fundId = null;
        final String fundName = null;
        final Long loanPurposeId = null;
        final String loanPurposeName = null;
        final Long loanOfficerId = null;
        final String loanOfficerName = null;
        final CurrencyData currencyData = null;
        final BigDecimal principal = null;
        final BigDecimal inArrearsTolerance = null;
        final Integer termFrequency = null;
        final EnumOptionData termPeriodFrequencyType = null;
        final Integer numberOfRepayments = null;
        final Integer repaymentEvery = null;
        final EnumOptionData repaymentFrequencyType = null;
        final Long transactionProcessingStrategyId = null;
        final EnumOptionData amortizationType = null;
        final BigDecimal interestRatePerPeriod = null;
        final EnumOptionData interestRateFrequencyType = null;
        final BigDecimal annualInterestRate = null;
        final EnumOptionData interestType = null;
        final EnumOptionData interestCalculationPeriodType = null;
        final LocalDate expectedFirstRepaymentOnDate = null;
        final LocalDate interestChargedFromDate = null;
        final LoanApplicationTimelineData timeline = LoanApplicationTimelineData.templateDefault(expectedDisbursementDate);
        final LoanSummaryData summary = null;
        final BigDecimal feeChargesDueAtDisbursementCharged = null;

        final LoanScheduleData repaymentSchedule = null;
        final Collection<LoanTransactionData> transactions = null;
        final Collection<LoanChargeData> charges = null;
        final Collection<CollateralData> collateral = null;
        final Collection<GuarantorData> guarantors = null;
        final Collection<LoanProductData> productOptions = null;
        final Collection<EnumOptionData> termFrequencyTypeOptions = null;
        final Collection<EnumOptionData> repaymentFrequencyTypeOptions = null;
        final Collection<TransactionProcessingStrategyData> repaymentStrategyOptions = null;
        final Collection<EnumOptionData> interestRateFrequencyTypeOptions = null;
        final Collection<EnumOptionData> amortizationTypeOptions = null;
        final Collection<EnumOptionData> interestTypeOptions = null;
        final Collection<EnumOptionData> interestCalculationPeriodTypeOptions = null;
        final Collection<FundData> fundOptions = null;
        final Collection<ChargeData> chargeOptions = null;
        final ChargeData chargeTemplate = null;
        final Collection<StaffData> loanOfficerOptions = null;
        final Collection<CodeValueData> loanPurposeOptions = null;
        final Collection<CodeValueData> loanCollateralOptions = null;

        return new LoanAccountData(id, accountNo, status, externalId, clientId, clientName, clientOfficeId, group, loanProductId,
                loanProductName, loanProductDescription, fundId, fundName, loanPurposeId, loanPurposeName, loanOfficerId, loanOfficerName,
                currencyData, principal, inArrearsTolerance, termFrequency, termPeriodFrequencyType, numberOfRepayments, repaymentEvery,
                repaymentFrequencyType, transactionProcessingStrategyId, amortizationType, interestRatePerPeriod,
                interestRateFrequencyType, annualInterestRate, interestType, interestCalculationPeriodType, expectedFirstRepaymentOnDate,
                interestChargedFromDate, timeline, summary, feeChargesDueAtDisbursementCharged, repaymentSchedule, transactions, charges,
                collateral, guarantors, productOptions, termFrequencyTypeOptions, repaymentFrequencyTypeOptions, repaymentStrategyOptions,
                interestRateFrequencyTypeOptions, amortizationTypeOptions, interestTypeOptions, interestCalculationPeriodTypeOptions,
                fundOptions, chargeOptions, chargeTemplate, loanOfficerOptions, loanPurposeOptions, loanCollateralOptions);
    }

    /**
     * Used to produce a {@link LoanAccountData} with only group information
     * defaulted.
     */
    public static LoanAccountData groupDefaults(final GroupGeneralData group, final LocalDate expectedDisbursementDate) {

        final Long id = null;
        final String accountNo = null;
        final LoanStatusEnumData status = null;
        final String externalId = null;
        final Long clientId = null;
        final String clientName = null;
        final Long clientOfficeId = null;
        final Long loanProductId = null;
        final String loanProductName = null;
        final String loanProductDescription = null;
        final Long fundId = null;
        final String fundName = null;
        final Long loanPurposeId = null;
        final String loanPurposeName = null;
        final Long loanOfficerId = null;
        final String loanOfficerName = null;
        final CurrencyData currencyData = null;
        final BigDecimal principal = null;
        final BigDecimal inArrearsTolerance = null;
        final Integer termFrequency = null;
        final EnumOptionData termPeriodFrequencyType = null;
        final Integer numberOfRepayments = null;
        final Integer repaymentEvery = null;
        final EnumOptionData repaymentFrequencyType = null;
        final Long transactionProcessingStrategyId = null;
        final EnumOptionData amortizationType = null;
        final BigDecimal interestRatePerPeriod = null;
        final EnumOptionData interestRateFrequencyType = null;
        final BigDecimal annualInterestRate = null;
        final EnumOptionData interestType = null;
        final EnumOptionData interestCalculationPeriodType = null;
        final LocalDate expectedFirstRepaymentOnDate = null;
        final LocalDate interestChargedFromDate = null;
        final LoanApplicationTimelineData timeline = LoanApplicationTimelineData.templateDefault(expectedDisbursementDate);
        final LoanSummaryData summary = null;
        final BigDecimal feeChargesDueAtDisbursementCharged = null;

        final LoanScheduleData repaymentSchedule = null;
        final Collection<LoanTransactionData> transactions = null;
        final Collection<LoanChargeData> charges = null;
        final Collection<CollateralData> collateral = null;
        final Collection<GuarantorData> guarantors = null;
        final Collection<LoanProductData> productOptions = null;
        final Collection<EnumOptionData> termFrequencyTypeOptions = null;
        final Collection<EnumOptionData> repaymentFrequencyTypeOptions = null;
        final Collection<TransactionProcessingStrategyData> repaymentStrategyOptions = null;
        final Collection<EnumOptionData> interestRateFrequencyTypeOptions = null;
        final Collection<EnumOptionData> amortizationTypeOptions = null;
        final Collection<EnumOptionData> interestTypeOptions = null;
        final Collection<EnumOptionData> interestCalculationPeriodTypeOptions = null;
        final Collection<FundData> fundOptions = null;
        final Collection<ChargeData> chargeOptions = null;
        final ChargeData chargeTemplate = null;
        final Collection<StaffData> loanOfficerOptions = null;
        final Collection<CodeValueData> loanPurposeOptions = null;
        final Collection<CodeValueData> loanCollateralOptions = null;

        return new LoanAccountData(id, accountNo, status, externalId, clientId, clientName, clientOfficeId, group, loanProductId,
                loanProductName, loanProductDescription, fundId, fundName, loanPurposeId, loanPurposeName, loanOfficerId, loanOfficerName,
                currencyData, principal, inArrearsTolerance, termFrequency, termPeriodFrequencyType, numberOfRepayments, repaymentEvery,
                repaymentFrequencyType, transactionProcessingStrategyId, amortizationType, interestRatePerPeriod,
                interestRateFrequencyType, annualInterestRate, interestType, interestCalculationPeriodType, expectedFirstRepaymentOnDate,
                interestChargedFromDate, timeline, summary, feeChargesDueAtDisbursementCharged, repaymentSchedule, transactions, charges,
                collateral, guarantors, productOptions, termFrequencyTypeOptions, repaymentFrequencyTypeOptions, repaymentStrategyOptions,
                interestRateFrequencyTypeOptions, amortizationTypeOptions, interestTypeOptions, interestCalculationPeriodTypeOptions,
                fundOptions, chargeOptions, chargeTemplate, loanOfficerOptions, loanPurposeOptions, loanCollateralOptions);
    }

    public static LoanAccountData populateLoanProductDefaults(final LoanAccountData acc, final LoanProductData product) {

        final LoanScheduleData repaymentSchedule = null;
        final Collection<LoanTransactionData> transactions = null;
        final Collection<CollateralData> collateral = null;
        final Collection<GuarantorData> guarantors = null;
        final Collection<LoanProductData> productOptions = null;
        final Collection<EnumOptionData> termFrequencyTypeOptions = null;
        final Collection<EnumOptionData> repaymentFrequencyTypeOptions = null;
        final Collection<TransactionProcessingStrategyData> repaymentStrategyOptions = null;
        final Collection<EnumOptionData> interestRateFrequencyTypeOptions = null;
        final Collection<EnumOptionData> amortizationTypeOptions = null;
        final Collection<EnumOptionData> interestTypeOptions = null;
        final Collection<EnumOptionData> interestCalculationPeriodTypeOptions = null;
        final Collection<FundData> fundOptions = null;
        final Collection<ChargeData> chargeOptions = null;
        final ChargeData chargeTemplate = null;
        final Collection<StaffData> loanOfficerOptions = null;
        final Collection<CodeValueData> loanPurposeOptions = null;
        final Collection<CodeValueData> loanCollateralOptions = null;

        final Integer termFrequency = product.getNumberOfRepayments() * product.getRepaymentEvery();
        final EnumOptionData termPeriodFrequencyType = product.getRepaymentFrequencyType();

        final Collection<LoanChargeData> charges = new ArrayList<LoanChargeData>();
        for (ChargeData charge : product.charges()) {
            charges.add(charge.toLoanChargeData());
        }

        return new LoanAccountData(acc.id, acc.accountNo, acc.status, acc.externalId, acc.clientId, acc.clientName, acc.clientOfficeId,
                acc.group, product.getId(), product.getName(), product.getDescription(), product.getFundId(), product.getFundName(),
                acc.loanPurposeId, acc.loanPurposeName, acc.loanOfficerId, acc.loanOfficerName, product.getCurrency(),
                product.getPrincipal(), product.getInArrearsTolerance(), termFrequency, termPeriodFrequencyType,
                product.getNumberOfRepayments(), product.getRepaymentEvery(), product.getRepaymentFrequencyType(),
                product.getTransactionProcessingStrategyId(), product.getAmortizationType(), product.getInterestRatePerPeriod(),
                product.getInterestRateFrequencyType(), product.getAnnualInterestRate(), product.getInterestType(),
                product.getInterestCalculationPeriodType(), acc.expectedFirstRepaymentOnDate, acc.interestChargedFromDate, acc.timeline,
                acc.summary, acc.feeChargesAtDisbursementCharged, repaymentSchedule, transactions, charges, collateral, guarantors,
                productOptions, termFrequencyTypeOptions, repaymentFrequencyTypeOptions, repaymentStrategyOptions,
                interestRateFrequencyTypeOptions, amortizationTypeOptions, interestTypeOptions, interestCalculationPeriodTypeOptions,
                fundOptions, chargeOptions, chargeTemplate, loanOfficerOptions, loanPurposeOptions, loanCollateralOptions);
    }

    /*
     * Used to send back loan account data with the basic details coming from
     * query.
     */
    public static LoanAccountData basicLoanDetails(final Long id, final String accountNo, final LoanStatusEnumData status,
            final String externalId, final Long clientId, final String clientName, final Long clientOfficeId, final GroupGeneralData group,
            final Long loanProductId, final String loanProductName, final String loanProductDescription, final Long fundId,
            final String fundName, final Long loanPurposeId, final String loanPurposeName, final Long loanOfficerId,
            final String loanOfficerName, final CurrencyData currencyData, final BigDecimal principal, final BigDecimal inArrearsTolerance,
            final Integer termFrequency, final EnumOptionData termPeriodFrequencyType, final Integer numberOfRepayments,
            final Integer repaymentEvery, final EnumOptionData repaymentFrequencyType, final Long transactionStrategyId,
            final EnumOptionData amortizationType, final BigDecimal interestRatePerPeriod, final EnumOptionData interestRateFrequencyType,
            final BigDecimal annualInterestRate, final EnumOptionData interestType, final EnumOptionData interestCalculationPeriodType,
            final LocalDate expectedFirstRepaymentOnDate, final LocalDate interestChargedFromDate,
            final LoanApplicationTimelineData timeline, final LoanSummaryData loanSummary,
            final BigDecimal feeChargesDueAtDisbursementCharged) {

        final LoanScheduleData repaymentSchedule = null;
        final Collection<LoanTransactionData> transactions = null;
        final Collection<LoanChargeData> charges = null;
        final Collection<CollateralData> collateral = null;
        final Collection<GuarantorData> guarantors = null;
        final Collection<LoanProductData> productOptions = null;
        final Collection<EnumOptionData> termFrequencyTypeOptions = null;
        final Collection<EnumOptionData> repaymentFrequencyTypeOptions = null;
        final Collection<TransactionProcessingStrategyData> repaymentStrategyOptions = null;
        final Collection<EnumOptionData> interestRateFrequencyTypeOptions = null;
        final Collection<EnumOptionData> amortizationTypeOptions = null;
        final Collection<EnumOptionData> interestTypeOptions = null;
        final Collection<EnumOptionData> interestCalculationPeriodTypeOptions = null;
        final Collection<FundData> fundOptions = null;
        final Collection<ChargeData> chargeOptions = null;
        final ChargeData chargeTemplate = null;
        final Collection<StaffData> loanOfficerOptions = null;
        final Collection<CodeValueData> loanPurposeOptions = null;
        final Collection<CodeValueData> loanCollateralOptions = null;

        return new LoanAccountData(id, accountNo, status, externalId, clientId, clientName, clientOfficeId, group, loanProductId,
                loanProductName, loanProductDescription, fundId, fundName, loanPurposeId, loanPurposeName, loanOfficerId, loanOfficerName,
                currencyData, principal, inArrearsTolerance, termFrequency, termPeriodFrequencyType, numberOfRepayments, repaymentEvery,
                repaymentFrequencyType, transactionStrategyId, amortizationType, interestRatePerPeriod, interestRateFrequencyType,
                annualInterestRate, interestType, interestCalculationPeriodType, expectedFirstRepaymentOnDate, interestChargedFromDate,
                timeline, loanSummary, feeChargesDueAtDisbursementCharged, repaymentSchedule, transactions, charges, collateral,
                guarantors, productOptions, termFrequencyTypeOptions, repaymentFrequencyTypeOptions, repaymentStrategyOptions,
                interestRateFrequencyTypeOptions, amortizationTypeOptions, interestTypeOptions, interestCalculationPeriodTypeOptions,
                fundOptions, chargeOptions, chargeTemplate, loanOfficerOptions, loanPurposeOptions, loanCollateralOptions);
    }

    /*
     * Used to combine the associations and template data on top of exist loan
     * account data
     */
    public static LoanAccountData associationsAndTemplate(final LoanAccountData acc, final LoanScheduleData repaymentSchedule,
            final Collection<LoanTransactionData> transactions, final Collection<LoanChargeData> charges,
            final Collection<CollateralData> collateral, final Collection<GuarantorData> guarantors,
            final Collection<LoanProductData> productOptions, final Collection<EnumOptionData> termFrequencyTypeOptions,
            final Collection<EnumOptionData> repaymentFrequencyTypeOptions,
            final Collection<TransactionProcessingStrategyData> transactionProcessingStrategyOptions,
            final Collection<EnumOptionData> interestRateFrequencyTypeOptions, final Collection<EnumOptionData> amortizationTypeOptions,
            final Collection<EnumOptionData> interestTypeOptions, final Collection<EnumOptionData> interestCalculationPeriodTypeOptions,
            final Collection<FundData> fundOptions, final Collection<ChargeData> chargeOptions, final ChargeData chargeTemplate,
            final Collection<StaffData> loanOfficerOptions, final Collection<CodeValueData> loanPurposeOptions,
            final Collection<CodeValueData> loanCollateralOptions) {
        return new LoanAccountData(acc.id, acc.accountNo, acc.status, acc.externalId, acc.clientId, acc.clientName, acc.clientOfficeId,
                acc.group, acc.loanProductId, acc.loanProductName, acc.loanProductDescription, acc.fundId, acc.fundName, acc.loanPurposeId,
                acc.loanPurposeName, acc.loanOfficerId, acc.loanOfficerName, acc.currency, acc.principal, acc.inArrearsTolerance,
                acc.termFrequency, acc.termPeriodFrequencyType, acc.numberOfRepayments, acc.repaymentEvery, acc.repaymentFrequencyType,
                acc.transactionProcessingStrategyId, acc.amortizationType, acc.interestRatePerPeriod, acc.interestRateFrequencyType,
                acc.annualInterestRate, acc.interestType, acc.interestCalculationPeriodType, acc.expectedFirstRepaymentOnDate,
                acc.interestChargedFromDate, acc.timeline, acc.summary, acc.feeChargesAtDisbursementCharged, repaymentSchedule,
                transactions, charges, collateral, guarantors, productOptions, termFrequencyTypeOptions, repaymentFrequencyTypeOptions,
                transactionProcessingStrategyOptions, interestRateFrequencyTypeOptions, amortizationTypeOptions, interestTypeOptions,
                interestCalculationPeriodTypeOptions, fundOptions, chargeOptions, chargeTemplate, loanOfficerOptions, loanPurposeOptions,
                loanCollateralOptions);
    }

    public static LoanAccountData associateGroup(final LoanAccountData acc, final GroupGeneralData group) {
        return new LoanAccountData(acc.id, acc.accountNo, acc.status, acc.externalId, acc.clientId, acc.clientName, acc.clientOfficeId,
                group, acc.loanProductId, acc.loanProductName, acc.loanProductDescription, acc.fundId, acc.fundName, acc.loanPurposeId,
                acc.loanPurposeName, acc.loanOfficerId, acc.loanOfficerName, acc.currency, acc.principal, acc.inArrearsTolerance,
                acc.termFrequency, acc.termPeriodFrequencyType, acc.numberOfRepayments, acc.repaymentEvery, acc.repaymentFrequencyType,
                acc.transactionProcessingStrategyId, acc.amortizationType, acc.interestRatePerPeriod, acc.interestRateFrequencyType,
                acc.annualInterestRate, acc.interestType, acc.interestCalculationPeriodType, acc.expectedFirstRepaymentOnDate,
                acc.interestChargedFromDate, acc.timeline, acc.summary, acc.feeChargesAtDisbursementCharged, acc.repaymentSchedule,
                acc.transactions, acc.charges, acc.collateral, acc.guarantors, acc.productOptions, acc.termFrequencyTypeOptions, acc.repaymentFrequencyTypeOptions,
                acc.transactionProcessingStrategyOptions, acc.interestRateFrequencyTypeOptions, acc.amortizationTypeOptions, acc.interestTypeOptions,
                acc.interestCalculationPeriodTypeOptions, acc.fundOptions, acc.chargeOptions, null, acc.loanOfficerOptions, acc.loanPurposeOptions,
                acc.loanCollateralOptions);
    }
    
    private LoanAccountData(
            final Long id, //
            final String accountNo, //
            final LoanStatusEnumData status, //
            final String externalId, //
            final Long clientId,
            final String clientName,
            final Long clientOfficeId, //
            final GroupGeneralData group,
            final Long loanProductId,
            final String loanProductName,
            final String loanProductDescription, //
            final Long fundId,
            final String fundName,
            final Long loanPurposeId,
            final String loanPurposeName, //
            final Long loanOfficerId,
            final String loanOfficerName, //
            final CurrencyData currency,
            final BigDecimal principal,
            final BigDecimal inArrearsTolerance, //
            final Integer termFrequency,
            final EnumOptionData termPeriodFrequencyType, //
            final Integer numberOfRepayments,
            final Integer repaymentEvery,
            final EnumOptionData repaymentFrequencyType, //
            final Long transactionProcessingStrategyId, final EnumOptionData amortizationType, final BigDecimal interestRatePerPeriod,
            final EnumOptionData interestRateFrequencyType, final BigDecimal annualInterestRate, final EnumOptionData interestType,
            final EnumOptionData interestCalculationPeriodType, final LocalDate expectedFirstRepaymentOnDate,
            final LocalDate interestChargedFromDate, final LoanApplicationTimelineData timeline, final LoanSummaryData summary,
            final BigDecimal feeChargesDueAtDisbursementCharged, final LoanScheduleData repaymentSchedule,
            final Collection<LoanTransactionData> transactions, final Collection<LoanChargeData> charges,
            final Collection<CollateralData> collateral, final Collection<GuarantorData> guarantors,
            final Collection<LoanProductData> productOptions, final Collection<EnumOptionData> termFrequencyTypeOptions,
            final Collection<EnumOptionData> repaymentFrequencyTypeOptions,
            final Collection<TransactionProcessingStrategyData> transactionProcessingStrategyOptions,
            final Collection<EnumOptionData> interestRateFrequencyTypeOptions, final Collection<EnumOptionData> amortizationTypeOptions,
            final Collection<EnumOptionData> interestTypeOptions, final Collection<EnumOptionData> interestCalculationPeriodTypeOptions,
            final Collection<FundData> fundOptions, final Collection<ChargeData> chargeOptions, final ChargeData chargeTemplate,
            final Collection<StaffData> loanOfficerOptions, final Collection<CodeValueData> loanPurposeOptions,
            final Collection<CodeValueData> loanCollateralOptions) {
        this.id = id;
        this.accountNo = accountNo;
        this.status = status;
        this.externalId = externalId;
        this.clientId = clientId;
        this.clientName = clientName;
        this.clientOfficeId = clientOfficeId;
        this.group = group;
        this.loanProductId = loanProductId;
        this.loanProductName = loanProductName;
        this.loanProductDescription = loanProductDescription;
        this.fundId = fundId;
        this.fundName = fundName;
        this.loanPurposeId = loanPurposeId;
        this.loanPurposeName = loanPurposeName;
        this.loanOfficerId = loanOfficerId;
        this.loanOfficerName = loanOfficerName;
        this.currency = currency;
        this.principal = principal;
        this.inArrearsTolerance = inArrearsTolerance;
        this.termFrequency = termFrequency;
        this.termPeriodFrequencyType = termPeriodFrequencyType;
        this.numberOfRepayments = numberOfRepayments;
        this.repaymentEvery = repaymentEvery;
        this.repaymentFrequencyType = repaymentFrequencyType;
        this.transactionProcessingStrategyId = transactionProcessingStrategyId;
        this.amortizationType = amortizationType;
        this.interestRatePerPeriod = interestRatePerPeriod;
        this.interestRateFrequencyType = interestRateFrequencyType;
        this.annualInterestRate = annualInterestRate;
        this.interestType = interestType;
        this.interestCalculationPeriodType = interestCalculationPeriodType;
        this.expectedFirstRepaymentOnDate = expectedFirstRepaymentOnDate;
        this.interestChargedFromDate = interestChargedFromDate;
        this.timeline = timeline;
        this.feeChargesAtDisbursementCharged = feeChargesDueAtDisbursementCharged;

        // totals
        this.summary = summary;

        // associations
        this.repaymentSchedule = repaymentSchedule;
        this.transactions = transactions;
        this.charges = charges;
        this.collateral = collateral;
        this.guarantors = guarantors;

        // template
        this.productOptions = productOptions;
        this.termFrequencyTypeOptions = termFrequencyTypeOptions;
        this.repaymentFrequencyTypeOptions = repaymentFrequencyTypeOptions;
        this.interestRateFrequencyTypeOptions = interestRateFrequencyTypeOptions;
        this.amortizationTypeOptions = amortizationTypeOptions;
        this.interestTypeOptions = interestTypeOptions;
        this.interestCalculationPeriodTypeOptions = interestCalculationPeriodTypeOptions;

        if (CollectionUtils.isEmpty(transactionProcessingStrategyOptions)) {
            this.transactionProcessingStrategyOptions = null;
        } else {
            this.transactionProcessingStrategyOptions = transactionProcessingStrategyOptions;
        }

        if (CollectionUtils.isEmpty(fundOptions)) {
            this.fundOptions = null;
        } else {
            this.fundOptions = fundOptions;
        }

        if (CollectionUtils.isEmpty(chargeOptions)) {
            this.chargeOptions = null;
        } else {
            this.chargeOptions = chargeOptions;
        }

        if (CollectionUtils.isEmpty(loanOfficerOptions)) {
            this.loanOfficerOptions = null;
        } else {
            this.loanOfficerOptions = loanOfficerOptions;
        }

        if (CollectionUtils.isEmpty(loanPurposeOptions)) {
            this.loanPurposeOptions = null;
        } else {
            this.loanPurposeOptions = loanPurposeOptions;
        }

        if (CollectionUtils.isEmpty(loanCollateralOptions)) {
            this.loanCollateralOptions = null;
        } else {
            this.loanCollateralOptions = loanCollateralOptions;
        }
    }

    public RepaymentScheduleRelatedLoanData repaymentScheduleRelatedData() {
        return this.timeline.repaymentScheduleRelatedData(this.currency, this.principal, this.inArrearsTolerance,
                this.feeChargesAtDisbursementCharged);
    }

    public Long officeId() {
        return this.clientOfficeId;
    }

    public Long loanOfficerId() {
        return this.loanOfficerId;
    }

    public Collection<LoanChargeData> charges() {
        return this.charges;
    }
    
    public Long groupOfficeId(){
        return group == null? null : group.officeId();
    }
}