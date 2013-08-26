/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.loanproduct.service;

import org.mifosplatform.infrastructure.core.data.EnumOptionData;
import org.mifosplatform.portfolio.accountdetails.service.AccountEnumerations;
import org.mifosplatform.portfolio.loanaccount.data.LoanStatusEnumData;
import org.mifosplatform.portfolio.loanaccount.data.LoanTransactionEnumData;
import org.mifosplatform.portfolio.loanaccount.domain.LoanStatus;
import org.mifosplatform.portfolio.loanaccount.domain.LoanTransactionType;
import org.mifosplatform.portfolio.loanproduct.domain.AmortizationMethod;
import org.mifosplatform.portfolio.loanproduct.domain.InterestCalculationPeriodMethod;
import org.mifosplatform.portfolio.loanproduct.domain.InterestMethod;
import org.mifosplatform.portfolio.loanproduct.domain.PeriodFrequencyType;

public class LoanEnumerations {

    public static final String LOAN_TERM_FREQUENCY_TYPE = "loanTermFrequencyType";
    public static final String TERM_FREQUENCY_TYPE = "termFrequencyType";
    public static final String REPAYMENT_FREQUENCY_TYPE = "repaymentFrequencyType";
    public static final String INTEREST_RATE_FREQUENCY_TYPE = "interestRateFrequencyType";
    public static final String AMORTIZATION_TYPE = "amortizationType";
    public static final String INTEREST_TYPE = "interestType";
    public static final String INTEREST_CALCULATION_PERIOD_TYPE = "interestCalculationPeriodType";
    public static final String PAYMENT_TYPE = "paymentType";
    public static final String ACCOUNTING_RULE_TYPE = "accountingRuleType";
    public static final String LOAN_TYPE = "loanType";

    public static EnumOptionData loanEnumueration(final String typeName, final int id) {
        if (typeName.equals(LOAN_TERM_FREQUENCY_TYPE)) {
            return loanTermFrequencyType(id);
        } else if (typeName.equals(TERM_FREQUENCY_TYPE)) {
            return termFrequencyType(id);
        } else if (typeName.equals(REPAYMENT_FREQUENCY_TYPE)) {
            return repaymentFrequencyType(id);
        } else if (typeName.equals(INTEREST_RATE_FREQUENCY_TYPE)) {
            return interestRateFrequencyType(id);
        } else if (typeName.equals(AMORTIZATION_TYPE)) {
            return amortizationType(id);
        } else if (typeName.equals(INTEREST_TYPE)) {
            return interestType(id);
        } else if (typeName.equals(INTEREST_CALCULATION_PERIOD_TYPE)) {
            return interestCalculationPeriodType(id);
        } else if (typeName.equals(LOAN_TYPE)) { return AccountEnumerations.loanType(id); }
        return null;
    }

    public static EnumOptionData loanTermFrequencyType(final int id) {
        return loanTermFrequencyType(PeriodFrequencyType.fromInt(id));
    }

    public static EnumOptionData loanTermFrequencyType(final PeriodFrequencyType type) {
        final String codePrefix = "loanTermFrequency.";
        EnumOptionData optionData = null;
        switch (type) {
            case DAYS:
                optionData = new EnumOptionData(PeriodFrequencyType.DAYS.getValue().longValue(), codePrefix
                        + PeriodFrequencyType.DAYS.getCode(), "Days");
            break;
            case WEEKS:
                optionData = new EnumOptionData(PeriodFrequencyType.WEEKS.getValue().longValue(), codePrefix
                        + PeriodFrequencyType.WEEKS.getCode(), "Weeks");
            break;
            case MONTHS:
                optionData = new EnumOptionData(PeriodFrequencyType.MONTHS.getValue().longValue(), codePrefix
                        + PeriodFrequencyType.MONTHS.getCode(), "Months");
            break;
            case YEARS:
                optionData = new EnumOptionData(PeriodFrequencyType.YEARS.getValue().longValue(), codePrefix
                        + PeriodFrequencyType.YEARS.getCode(), "Years");
            break;
            default:
                optionData = new EnumOptionData(PeriodFrequencyType.INVALID.getValue().longValue(), PeriodFrequencyType.INVALID.getCode(),
                        "Invalid");
            break;
        }
        return optionData;
    }

    public static EnumOptionData termFrequencyType(final int id) {
        return termFrequencyType(PeriodFrequencyType.fromInt(id));
    }

    public static EnumOptionData termFrequencyType(final PeriodFrequencyType type) {
        final String codePrefix = "termFrequency.";
        EnumOptionData optionData = null;
        switch (type) {
            case DAYS:
                optionData = new EnumOptionData(PeriodFrequencyType.DAYS.getValue().longValue(), codePrefix
                        + PeriodFrequencyType.DAYS.getCode(), "Days");
            break;
            case WEEKS:
                optionData = new EnumOptionData(PeriodFrequencyType.WEEKS.getValue().longValue(), codePrefix
                        + PeriodFrequencyType.WEEKS.getCode(), "Weeks");
            break;
            case MONTHS:
                optionData = new EnumOptionData(PeriodFrequencyType.MONTHS.getValue().longValue(), codePrefix
                        + PeriodFrequencyType.MONTHS.getCode(), "Months");
            break;
            default:
                optionData = new EnumOptionData(PeriodFrequencyType.INVALID.getValue().longValue(), PeriodFrequencyType.INVALID.getCode(),
                        "Invalid");
            break;
        }
        return optionData;
    }

    public static EnumOptionData repaymentFrequencyType(final int id) {
        return repaymentFrequencyType(PeriodFrequencyType.fromInt(id));
    }

    public static EnumOptionData repaymentFrequencyType(final PeriodFrequencyType type) {
        final String codePrefix = "repaymentFrequency.";
        EnumOptionData optionData = null;
        switch (type) {
            case DAYS:
                optionData = new EnumOptionData(PeriodFrequencyType.DAYS.getValue().longValue(), codePrefix
                        + PeriodFrequencyType.DAYS.getCode(), "Days");
            break;
            case WEEKS:
                optionData = new EnumOptionData(PeriodFrequencyType.WEEKS.getValue().longValue(), codePrefix
                        + PeriodFrequencyType.WEEKS.getCode(), "Weeks");
            break;
            case MONTHS:
                optionData = new EnumOptionData(PeriodFrequencyType.MONTHS.getValue().longValue(), codePrefix
                        + PeriodFrequencyType.MONTHS.getCode(), "Months");
            break;
            default:
                optionData = new EnumOptionData(PeriodFrequencyType.INVALID.getValue().longValue(), PeriodFrequencyType.INVALID.getCode(),
                        "Invalid");
            break;
        }
        return optionData;
    }

    public static EnumOptionData interestRateFrequencyType(final int id) {
        return interestRateFrequencyType(PeriodFrequencyType.fromInt(id));
    }

    public static EnumOptionData interestRateFrequencyType(final PeriodFrequencyType type) {
        final String codePrefix = "interestRateFrequency.";
        EnumOptionData optionData = null;
        switch (type) {
            case MONTHS:
                optionData = new EnumOptionData(PeriodFrequencyType.MONTHS.getValue().longValue(), codePrefix
                        + PeriodFrequencyType.MONTHS.getCode(), "Per month");
            break;
            case YEARS:
                optionData = new EnumOptionData(PeriodFrequencyType.YEARS.getValue().longValue(), codePrefix
                        + PeriodFrequencyType.YEARS.getCode(), "Per year");
            break;
            default:
                optionData = new EnumOptionData(PeriodFrequencyType.INVALID.getValue().longValue(), PeriodFrequencyType.INVALID.getCode(),
                        "Invalid");
            break;
        }
        return optionData;
    }

    public static EnumOptionData amortizationType(final Integer id) {
        return amortizationType(AmortizationMethod.fromInt(id));
    }

    public static EnumOptionData amortizationType(final AmortizationMethod amortizationMethod) {
        EnumOptionData optionData = null;
        switch (amortizationMethod) {
            case EQUAL_INSTALLMENTS:
                optionData = new EnumOptionData(AmortizationMethod.EQUAL_INSTALLMENTS.getValue().longValue(),
                        AmortizationMethod.EQUAL_INSTALLMENTS.getCode(), "Equal installments");
            break;
            case EQUAL_PRINCIPAL:
                optionData = new EnumOptionData(AmortizationMethod.EQUAL_PRINCIPAL.getValue().longValue(),
                        AmortizationMethod.EQUAL_PRINCIPAL.getCode(), "Equal principal payments");
            break;
            default:
                optionData = new EnumOptionData(AmortizationMethod.INVALID.getValue().longValue(), AmortizationMethod.INVALID.getCode(),
                        "Invalid");
            break;
        }
        return optionData;
    }

    public static EnumOptionData interestType(final Integer id) {
        return interestType(InterestMethod.fromInt(id));
    }

    public static EnumOptionData interestType(final InterestMethod type) {
        EnumOptionData optionData = null;
        switch (type) {
            case FLAT:
                optionData = new EnumOptionData(InterestMethod.FLAT.getValue().longValue(), InterestMethod.FLAT.getCode(), "Flat");
            break;
            case DECLINING_BALANCE:
                optionData = new EnumOptionData(InterestMethod.DECLINING_BALANCE.getValue().longValue(),
                        InterestMethod.DECLINING_BALANCE.getCode(), "Declining Balance");
            break;
            default:
                optionData = new EnumOptionData(InterestMethod.INVALID.getValue().longValue(), InterestMethod.INVALID.getCode(), "Invalid");
            break;
        }
        return optionData;
    }

    public static EnumOptionData interestCalculationPeriodType(final Integer id) {
        return interestCalculationPeriodType(InterestCalculationPeriodMethod.fromInt(id));
    }

    public static EnumOptionData interestCalculationPeriodType(final InterestCalculationPeriodMethod type) {
        EnumOptionData optionData = null;
        switch (type) {
            case DAILY:
                optionData = new EnumOptionData(InterestCalculationPeriodMethod.DAILY.getValue().longValue(),
                        InterestCalculationPeriodMethod.DAILY.getCode(), "Daily");
            break;
            case SAME_AS_REPAYMENT_PERIOD:
                optionData = new EnumOptionData(InterestCalculationPeriodMethod.SAME_AS_REPAYMENT_PERIOD.getValue().longValue(),
                        InterestCalculationPeriodMethod.SAME_AS_REPAYMENT_PERIOD.getCode(), "Same as repayment period");
            break;
            default:
                optionData = new EnumOptionData(InterestCalculationPeriodMethod.INVALID.getValue().longValue(),
                        InterestCalculationPeriodMethod.INVALID.getCode(), "Invalid");
            break;
        }
        return optionData;
    }

    public static LoanTransactionEnumData transactionType(final Integer id) {
        return transactionType(LoanTransactionType.fromInt(id));
    }

    public static LoanTransactionEnumData transactionType(final LoanTransactionType type) {
        LoanTransactionEnumData optionData = null;
        switch (type) {
            case INVALID:
                optionData = new LoanTransactionEnumData(LoanTransactionType.INVALID.getValue().longValue(),
                        LoanTransactionType.INVALID.getCode(), "Invalid");
            break;
            case DISBURSEMENT:
                optionData = new LoanTransactionEnumData(LoanTransactionType.DISBURSEMENT.getValue().longValue(),
                        LoanTransactionType.DISBURSEMENT.getCode(), "Dibursement");
            break;
            case REPAYMENT:
                optionData = new LoanTransactionEnumData(LoanTransactionType.REPAYMENT.getValue().longValue(),
                        LoanTransactionType.REPAYMENT.getCode(), "Repayment");
            break;
            case REPAYMENT_AT_DISBURSEMENT:
                optionData = new LoanTransactionEnumData(LoanTransactionType.REPAYMENT_AT_DISBURSEMENT.getValue().longValue(),
                        LoanTransactionType.REPAYMENT_AT_DISBURSEMENT.getCode(), "Repayment (at time of disbursement)");
            break;
            case CONTRA:
                optionData = new LoanTransactionEnumData(LoanTransactionType.CONTRA.getValue().longValue(),
                        LoanTransactionType.CONTRA.getCode(), "Reversal");
            break;
            case WAIVE_INTEREST:
                optionData = new LoanTransactionEnumData(LoanTransactionType.WAIVE_INTEREST.getValue().longValue(),
                        LoanTransactionType.WAIVE_INTEREST.getCode(), "Waive interest");
            break;
            case MARKED_FOR_RESCHEDULING:
                optionData = new LoanTransactionEnumData(LoanTransactionType.MARKED_FOR_RESCHEDULING.getValue().longValue(),
                        LoanTransactionType.MARKED_FOR_RESCHEDULING.getCode(), "Close (as rescheduled)");
            break;
            case WRITEOFF:
                optionData = new LoanTransactionEnumData(LoanTransactionType.WRITEOFF.getValue().longValue(),
                        LoanTransactionType.WRITEOFF.getCode(), "Close (as written-off)");
            break;
            case RECOVERY_REPAYMENT:
                optionData = new LoanTransactionEnumData(LoanTransactionType.RECOVERY_REPAYMENT.getValue().longValue(),
                        LoanTransactionType.RECOVERY_REPAYMENT.getCode(), "Repayment (after write-off)");
            break;
            case WAIVE_CHARGES:
                optionData = new LoanTransactionEnumData(LoanTransactionType.WAIVE_CHARGES.getValue().longValue(),
                        LoanTransactionType.WAIVE_CHARGES.getCode(), "Waive loan charges");
            break;
            case APPLY_CHARGES:
                optionData = new LoanTransactionEnumData(LoanTransactionType.APPLY_CHARGES.getValue().longValue(),
                        LoanTransactionType.APPLY_CHARGES.getCode(), "Apply Charge");
            break;
            case APPLY_INTEREST:
                optionData = new LoanTransactionEnumData(LoanTransactionType.APPLY_INTEREST.getValue().longValue(),
                        LoanTransactionType.APPLY_INTEREST.getCode(), "Apply Interest");
            break;
            case APPROVE_TRANSFER:
                optionData = new LoanTransactionEnumData(LoanTransactionType.APPROVE_TRANSFER.getValue().longValue(),
                        LoanTransactionType.APPROVE_TRANSFER.getCode(), "Transfer approved");
            break;
            case INITIATE_TRANSFER:
                optionData = new LoanTransactionEnumData(LoanTransactionType.INITIATE_TRANSFER.getValue().longValue(),
                        LoanTransactionType.INITIATE_TRANSFER.getCode(), "Transfer initiated");
            break;
            case WITHDRAW_TRANSFER:
                optionData = new LoanTransactionEnumData(LoanTransactionType.WITHDRAW_TRANSFER.getValue().longValue(),
                        LoanTransactionType.WITHDRAW_TRANSFER.getCode(), "Transfer Withdrawn");
            break;
            case REJECT_TRANSFER:
                optionData = new LoanTransactionEnumData(LoanTransactionType.REJECT_TRANSFER.getValue().longValue(),
                        LoanTransactionType.REJECT_TRANSFER.getCode(), "Transfer Rejected");
            break;
            default:
            break;
        }
        return optionData;
    }

    public static LoanStatusEnumData status(final Integer statusId) {
        return status(LoanStatus.fromInt(statusId));
    }

    public static LoanStatusEnumData status(final LoanStatus status) {
        LoanStatusEnumData optionData = new LoanStatusEnumData(LoanStatus.INVALID.getValue().longValue(), LoanStatus.INVALID.getCode(),
                "Invalid");
        switch (status) {
            case INVALID:
                optionData = new LoanStatusEnumData(LoanStatus.INVALID.getValue().longValue(), LoanStatus.INVALID.getCode(), "Invalid");
            break;
            case SUBMITTED_AND_PENDING_APPROVAL:
                optionData = new LoanStatusEnumData(LoanStatus.SUBMITTED_AND_PENDING_APPROVAL.getValue().longValue(),
                        LoanStatus.SUBMITTED_AND_PENDING_APPROVAL.getCode(), "Submitted and pending approval");
            break;
            case APPROVED:
                optionData = new LoanStatusEnumData(LoanStatus.APPROVED.getValue().longValue(), LoanStatus.APPROVED.getCode(), "Approved");
            break;
            case ACTIVE:
                optionData = new LoanStatusEnumData(LoanStatus.ACTIVE.getValue().longValue(), LoanStatus.ACTIVE.getCode(), "Active");
            break;
            case REJECTED:
                optionData = new LoanStatusEnumData(LoanStatus.REJECTED.getValue().longValue(), LoanStatus.REJECTED.getCode(), "Rejected");
            break;
            case WITHDRAWN_BY_CLIENT:
                optionData = new LoanStatusEnumData(LoanStatus.WITHDRAWN_BY_CLIENT.getValue().longValue(),
                        LoanStatus.WITHDRAWN_BY_CLIENT.getCode(), "Withdrawn by applicant");
            break;
            case CLOSED_OBLIGATIONS_MET:
                optionData = new LoanStatusEnumData(LoanStatus.CLOSED_OBLIGATIONS_MET.getValue().longValue(),
                        LoanStatus.CLOSED_OBLIGATIONS_MET.getCode(), "Closed (obligations met)");
            break;
            case CLOSED_WRITTEN_OFF:
                optionData = new LoanStatusEnumData(LoanStatus.CLOSED_WRITTEN_OFF.getValue().longValue(),
                        LoanStatus.CLOSED_WRITTEN_OFF.getCode(), "Closed (written off)");
            break;
            case CLOSED_RESCHEDULE_OUTSTANDING_AMOUNT:
                optionData = new LoanStatusEnumData(LoanStatus.CLOSED_RESCHEDULE_OUTSTANDING_AMOUNT.getValue().longValue(),
                        LoanStatus.CLOSED_RESCHEDULE_OUTSTANDING_AMOUNT.getCode(), "Closed (rescheduled)");
            break;
            case OVERPAID:
                optionData = new LoanStatusEnumData(LoanStatus.OVERPAID.getValue().longValue(), LoanStatus.OVERPAID.getCode(), "Overpaid");
            break;
            case TRANSFER_IN_PROGRESS:
                optionData = new LoanStatusEnumData(LoanStatus.TRANSFER_IN_PROGRESS.getValue().longValue(),
                        LoanStatus.TRANSFER_IN_PROGRESS.getCode(), "Transfer in progress");
            break;
            case TRANSFER_ON_HOLD:
                optionData = new LoanStatusEnumData(LoanStatus.TRANSFER_ON_HOLD.getValue().longValue(),
                        LoanStatus.TRANSFER_ON_HOLD.getCode(), "Transfer in hold");
            break;
            default:
            break;
        }

        return optionData;
    }
}