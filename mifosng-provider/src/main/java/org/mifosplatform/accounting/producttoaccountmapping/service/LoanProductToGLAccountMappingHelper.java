/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.accounting.producttoaccountmapping.service;

import java.util.HashMap;
import java.util.Map;

import org.mifosplatform.accounting.common.AccountingConstants.ACCRUAL_ACCOUNTS_FOR_LOAN;
import org.mifosplatform.accounting.common.AccountingConstants.CASH_ACCOUNTS_FOR_LOAN;
import org.mifosplatform.accounting.common.AccountingConstants.LOAN_PRODUCT_ACCOUNTING_PARAMS;
import org.mifosplatform.accounting.common.AccountingRuleType;
import org.mifosplatform.accounting.glaccount.domain.GLAccountRepository;
import org.mifosplatform.accounting.glaccount.domain.GLAccountType;
import org.mifosplatform.accounting.producttoaccountmapping.domain.PortfolioProductType;
import org.mifosplatform.accounting.producttoaccountmapping.domain.ProductToGLAccountMappingRepository;
import org.mifosplatform.infrastructure.codes.domain.CodeValueRepositoryWrapper;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.serialization.FromJsonHelper;
import org.mifosplatform.portfolio.charge.domain.ChargeRepositoryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.JsonElement;

@Component
public class LoanProductToGLAccountMappingHelper extends ProductToGLAccountMappingHelper {

    @Autowired
    public LoanProductToGLAccountMappingHelper(final GLAccountRepository glAccountRepository,
            final ProductToGLAccountMappingRepository glAccountMappingRepository, final FromJsonHelper fromApiJsonHelper,
            final CodeValueRepositoryWrapper codeValueRepositoryWrapper, final ChargeRepositoryWrapper chargeRepositoryWrapper) {
        super(glAccountRepository, glAccountMappingRepository, fromApiJsonHelper, codeValueRepositoryWrapper, chargeRepositoryWrapper);
    }

    /*** Set of abstractions for saving Loan Products to GL Account Mappings ***/

    public void saveLoanToAssetAccountMapping(final JsonElement element, final String paramName, final Long productId,
            final int placeHolderTypeId) {
        saveProductToAccountMapping(element, paramName, productId, placeHolderTypeId, GLAccountType.ASSET, PortfolioProductType.LOAN);
    }

    public void saveLoanToIncomeAccountMapping(final JsonElement element, final String paramName, final Long productId,
            final int placeHolderTypeId) {
        saveProductToAccountMapping(element, paramName, productId, placeHolderTypeId, GLAccountType.INCOME, PortfolioProductType.LOAN);
    }

    public void saveLoanToExpenseAccountMapping(final JsonElement element, final String paramName, final Long productId,
            final int placeHolderTypeId) {
        saveProductToAccountMapping(element, paramName, productId, placeHolderTypeId, GLAccountType.EXPENSE, PortfolioProductType.LOAN);
    }

    /*** Set of abstractions for merging Savings Products to GL Account Mappings ***/
    public void mergeLoanToAssetAccountMappingChanges(final JsonElement element, final String paramName, final Long productId,
            final int accountTypeId, final String accountTypeName, final Map<String, Object> changes) {
        mergeProductToAccountMappingChanges(element, paramName, productId, accountTypeId, accountTypeName, changes, GLAccountType.ASSET,
                PortfolioProductType.LOAN);
    }

    public void mergeLoanToIncomeAccountMappingChanges(final JsonElement element, final String paramName, final Long productId,
            final int accountTypeId, final String accountTypeName, final Map<String, Object> changes) {
        mergeProductToAccountMappingChanges(element, paramName, productId, accountTypeId, accountTypeName, changes, GLAccountType.INCOME,
                PortfolioProductType.LOAN);
    }

    public void mergeLoanToExpenseAccountMappingChanges(final JsonElement element, final String paramName, final Long productId,
            final int accountTypeId, final String accountTypeName, final Map<String, Object> changes) {
        mergeProductToAccountMappingChanges(element, paramName, productId, accountTypeId, accountTypeName, changes, GLAccountType.EXPENSE,
                PortfolioProductType.LOAN);
    }

    /*** Abstractions for payments channel related to loan products ***/

    public void savePaymentChannelToFundSourceMappings(final JsonCommand command, final JsonElement element, final Long productId,
            final Map<String, Object> changes) {
        savePaymentChannelToFundSourceMappings(command, element, productId, changes, PortfolioProductType.LOAN);
    }

    public void updatePaymentChannelToFundSourceMappings(final JsonCommand command, final JsonElement element, final Long productId,
            final Map<String, Object> changes) {
        updatePaymentChannelToFundSourceMappings(command, element, productId, changes, PortfolioProductType.LOAN);
    }

    public void saveChargesToIncomeAccountMappings(final JsonCommand command, final JsonElement element, final Long productId,
            final Map<String, Object> changes) {
        // save both fee and penalty charges
        saveChargesToIncomeAccountMappings(command, element, productId, changes, PortfolioProductType.LOAN, true);
        saveChargesToIncomeAccountMappings(command, element, productId, changes, PortfolioProductType.LOAN, false);
    }

    public void updateChargesToIncomeAccountMappings(final JsonCommand command, final JsonElement element, final Long productId,
            final Map<String, Object> changes) {
        // update both fee and penalty charges
        updateChargeToIncomeAccountMappings(command, element, productId, changes, PortfolioProductType.LOAN, true);
        updateChargeToIncomeAccountMappings(command, element, productId, changes, PortfolioProductType.LOAN, false);
    }

    public Map<String, Object> populateChangesForNewLoanProductToGLAccountMappingCreation(final JsonElement element,
            final AccountingRuleType accountingRuleType) {
        final Map<String, Object> changes = new HashMap<String, Object>();

        final Long fundAccountId = this.fromApiJsonHelper.extractLongNamed(LOAN_PRODUCT_ACCOUNTING_PARAMS.FUND_SOURCE.getValue(), element);
        final Long loanPortfolioAccountId = this.fromApiJsonHelper.extractLongNamed(
                LOAN_PRODUCT_ACCOUNTING_PARAMS.LOAN_PORTFOLIO.getValue(), element);
        final Long incomeFromInterestId = this.fromApiJsonHelper.extractLongNamed(
                LOAN_PRODUCT_ACCOUNTING_PARAMS.INTEREST_ON_LOANS.getValue(), element);
        final Long incomeFromFeeId = this.fromApiJsonHelper.extractLongNamed(LOAN_PRODUCT_ACCOUNTING_PARAMS.INCOME_FROM_FEES.getValue(),
                element);
        final Long incomeFromPenaltyId = this.fromApiJsonHelper.extractLongNamed(
                LOAN_PRODUCT_ACCOUNTING_PARAMS.INCOME_FROM_PENALTIES.getValue(), element);
        final Long writeOffAccountId = this.fromApiJsonHelper.extractLongNamed(
                LOAN_PRODUCT_ACCOUNTING_PARAMS.LOSSES_WRITTEN_OFF.getValue(), element);

        final Long receivableInterestAccountId = this.fromApiJsonHelper.extractLongNamed(
                LOAN_PRODUCT_ACCOUNTING_PARAMS.INTEREST_RECEIVABLE.getValue(), element);
        final Long receivableFeeAccountId = this.fromApiJsonHelper.extractLongNamed(
                LOAN_PRODUCT_ACCOUNTING_PARAMS.FEES_RECEIVABLE.getValue(), element);
        final Long receivablePenaltyAccountId = this.fromApiJsonHelper.extractLongNamed(
                LOAN_PRODUCT_ACCOUNTING_PARAMS.PENALTIES_RECEIVABLE.getValue(), element);

        switch (accountingRuleType) {
            case NONE:
            break;
            case CASH_BASED:
                changes.put(LOAN_PRODUCT_ACCOUNTING_PARAMS.FUND_SOURCE.getValue(), fundAccountId);
                changes.put(LOAN_PRODUCT_ACCOUNTING_PARAMS.LOAN_PORTFOLIO.getValue(), loanPortfolioAccountId);
                changes.put(LOAN_PRODUCT_ACCOUNTING_PARAMS.INTEREST_ON_LOANS.getValue(), incomeFromInterestId);
                changes.put(LOAN_PRODUCT_ACCOUNTING_PARAMS.INCOME_FROM_FEES.getValue(), incomeFromFeeId);
                changes.put(LOAN_PRODUCT_ACCOUNTING_PARAMS.INCOME_FROM_PENALTIES.getValue(), incomeFromPenaltyId);
                changes.put(LOAN_PRODUCT_ACCOUNTING_PARAMS.LOSSES_WRITTEN_OFF.getValue(), writeOffAccountId);
            break;
            case ACCRUAL_BASED:
                changes.put(LOAN_PRODUCT_ACCOUNTING_PARAMS.FUND_SOURCE.getValue(), fundAccountId);
                changes.put(LOAN_PRODUCT_ACCOUNTING_PARAMS.LOAN_PORTFOLIO.getValue(), loanPortfolioAccountId);
                changes.put(LOAN_PRODUCT_ACCOUNTING_PARAMS.INTEREST_ON_LOANS.getValue(), incomeFromInterestId);
                changes.put(LOAN_PRODUCT_ACCOUNTING_PARAMS.INCOME_FROM_FEES.getValue(), incomeFromFeeId);
                changes.put(LOAN_PRODUCT_ACCOUNTING_PARAMS.INCOME_FROM_PENALTIES.getValue(), incomeFromPenaltyId);
                changes.put(LOAN_PRODUCT_ACCOUNTING_PARAMS.LOSSES_WRITTEN_OFF.getValue(), writeOffAccountId);
                changes.put(LOAN_PRODUCT_ACCOUNTING_PARAMS.INTEREST_RECEIVABLE.getValue(), receivableInterestAccountId);
                changes.put(LOAN_PRODUCT_ACCOUNTING_PARAMS.FEES_RECEIVABLE.getValue(), receivableFeeAccountId);
                changes.put(LOAN_PRODUCT_ACCOUNTING_PARAMS.PENALTIES_RECEIVABLE.getValue(), receivablePenaltyAccountId);
            break;
        }

        return changes;
    }

    /**
     * Examines and updates each account mapping for given loan product with
     * changes passed in from the Json element
     * 
     * @param loanProductId
     * @param changes
     * @param element
     * @param accountingRuleType
     */
    public void handleChangesToLoanProductToGLAccountMappings(final Long loanProductId, final Map<String, Object> changes,
            final JsonElement element, final AccountingRuleType accountingRuleType) {
        switch (accountingRuleType) {
            case NONE:
            break;
            case CASH_BASED:
                // asset
                mergeLoanToAssetAccountMappingChanges(element, LOAN_PRODUCT_ACCOUNTING_PARAMS.FUND_SOURCE.getValue(), loanProductId,
                        CASH_ACCOUNTS_FOR_LOAN.FUND_SOURCE.getValue(), CASH_ACCOUNTS_FOR_LOAN.FUND_SOURCE.toString(), changes);
                mergeLoanToAssetAccountMappingChanges(element, LOAN_PRODUCT_ACCOUNTING_PARAMS.LOAN_PORTFOLIO.getValue(), loanProductId,
                        CASH_ACCOUNTS_FOR_LOAN.LOAN_PORTFOLIO.getValue(), CASH_ACCOUNTS_FOR_LOAN.LOAN_PORTFOLIO.toString(), changes);

                // income
                mergeLoanToIncomeAccountMappingChanges(element, LOAN_PRODUCT_ACCOUNTING_PARAMS.INTEREST_ON_LOANS.getValue(), loanProductId,
                        CASH_ACCOUNTS_FOR_LOAN.INTEREST_ON_LOANS.getValue(), CASH_ACCOUNTS_FOR_LOAN.INTEREST_ON_LOANS.toString(), changes);
                mergeLoanToIncomeAccountMappingChanges(element, LOAN_PRODUCT_ACCOUNTING_PARAMS.INCOME_FROM_FEES.getValue(), loanProductId,
                        CASH_ACCOUNTS_FOR_LOAN.INCOME_FROM_FEES.getValue(), CASH_ACCOUNTS_FOR_LOAN.INCOME_FROM_FEES.toString(), changes);
                mergeLoanToIncomeAccountMappingChanges(element, LOAN_PRODUCT_ACCOUNTING_PARAMS.INCOME_FROM_PENALTIES.getValue(),
                        loanProductId, CASH_ACCOUNTS_FOR_LOAN.INCOME_FROM_PENALTIES.getValue(),
                        CASH_ACCOUNTS_FOR_LOAN.INCOME_FROM_PENALTIES.toString(), changes);

                // expenses
                mergeLoanToExpenseAccountMappingChanges(element, LOAN_PRODUCT_ACCOUNTING_PARAMS.LOSSES_WRITTEN_OFF.getValue(),
                        loanProductId, CASH_ACCOUNTS_FOR_LOAN.LOSSES_WRITTEN_OFF.getValue(),
                        CASH_ACCOUNTS_FOR_LOAN.LOSSES_WRITTEN_OFF.toString(), changes);
            break;
            case ACCRUAL_BASED:
                // assets (including receivables)
                mergeLoanToAssetAccountMappingChanges(element, LOAN_PRODUCT_ACCOUNTING_PARAMS.FUND_SOURCE.getValue(), loanProductId,
                        ACCRUAL_ACCOUNTS_FOR_LOAN.FUND_SOURCE.getValue(), ACCRUAL_ACCOUNTS_FOR_LOAN.FUND_SOURCE.toString(), changes);
                mergeLoanToAssetAccountMappingChanges(element, LOAN_PRODUCT_ACCOUNTING_PARAMS.LOAN_PORTFOLIO.getValue(), loanProductId,
                        ACCRUAL_ACCOUNTS_FOR_LOAN.LOAN_PORTFOLIO.getValue(), ACCRUAL_ACCOUNTS_FOR_LOAN.LOAN_PORTFOLIO.toString(), changes);
                mergeLoanToAssetAccountMappingChanges(element, LOAN_PRODUCT_ACCOUNTING_PARAMS.INTEREST_RECEIVABLE.getValue(),
                        loanProductId, ACCRUAL_ACCOUNTS_FOR_LOAN.INTEREST_RECEIVABLE.getValue(),
                        ACCRUAL_ACCOUNTS_FOR_LOAN.INTEREST_RECEIVABLE.toString(), changes);
                mergeLoanToAssetAccountMappingChanges(element, LOAN_PRODUCT_ACCOUNTING_PARAMS.FEES_RECEIVABLE.getValue(), loanProductId,
                        ACCRUAL_ACCOUNTS_FOR_LOAN.FEES_RECEIVABLE.getValue(), ACCRUAL_ACCOUNTS_FOR_LOAN.FEES_RECEIVABLE.toString(), changes);
                mergeLoanToAssetAccountMappingChanges(element, LOAN_PRODUCT_ACCOUNTING_PARAMS.PENALTIES_RECEIVABLE.getValue(),
                        loanProductId, ACCRUAL_ACCOUNTS_FOR_LOAN.PENALTIES_RECEIVABLE.getValue(),
                        ACCRUAL_ACCOUNTS_FOR_LOAN.PENALTIES_RECEIVABLE.toString(), changes);

                // income
                mergeLoanToIncomeAccountMappingChanges(element, LOAN_PRODUCT_ACCOUNTING_PARAMS.INTEREST_ON_LOANS.getValue(), loanProductId,
                        ACCRUAL_ACCOUNTS_FOR_LOAN.INTEREST_ON_LOANS.getValue(), ACCRUAL_ACCOUNTS_FOR_LOAN.INTEREST_ON_LOANS.toString(),
                        changes);
                mergeLoanToIncomeAccountMappingChanges(element, LOAN_PRODUCT_ACCOUNTING_PARAMS.INCOME_FROM_FEES.getValue(), loanProductId,
                        ACCRUAL_ACCOUNTS_FOR_LOAN.INCOME_FROM_FEES.getValue(), ACCRUAL_ACCOUNTS_FOR_LOAN.INCOME_FROM_FEES.toString(),
                        changes);
                mergeLoanToIncomeAccountMappingChanges(element, LOAN_PRODUCT_ACCOUNTING_PARAMS.INCOME_FROM_PENALTIES.getValue(),
                        loanProductId, ACCRUAL_ACCOUNTS_FOR_LOAN.INCOME_FROM_PENALTIES.getValue(),
                        ACCRUAL_ACCOUNTS_FOR_LOAN.INCOME_FROM_PENALTIES.toString(), changes);

                // expenses
                mergeLoanToExpenseAccountMappingChanges(element, LOAN_PRODUCT_ACCOUNTING_PARAMS.LOSSES_WRITTEN_OFF.getValue(),
                        loanProductId, ACCRUAL_ACCOUNTS_FOR_LOAN.LOSSES_WRITTEN_OFF.getValue(),
                        ACCRUAL_ACCOUNTS_FOR_LOAN.LOSSES_WRITTEN_OFF.toString(), changes);
            break;
        }
    }

    public void deleteLoanProductToGLAccountMapping(final Long loanProductId) {
        deleteProductToGLAccountMapping(loanProductId, PortfolioProductType.LOAN);
    }

}
