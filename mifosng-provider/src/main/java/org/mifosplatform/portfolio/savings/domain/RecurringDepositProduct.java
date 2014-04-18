/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.savings.domain;

import static org.mifosplatform.portfolio.savings.DepositsApiConstants.RECURRING_DEPOSIT_PRODUCT_RESOURCE_NAME;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.OneToOne;

import org.mifosplatform.accounting.common.AccountingRuleType;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.ApiParameterError;
import org.mifosplatform.infrastructure.core.data.DataValidatorBuilder;
import org.mifosplatform.infrastructure.core.exception.PlatformApiDataValidationException;
import org.mifosplatform.organisation.monetary.domain.MonetaryCurrency;
import org.mifosplatform.portfolio.charge.domain.Charge;
import org.mifosplatform.portfolio.interestratechart.domain.InterestRateChart;
import org.mifosplatform.portfolio.savings.SavingsCompoundingInterestPeriodType;
import org.mifosplatform.portfolio.savings.SavingsInterestCalculationDaysInYearType;
import org.mifosplatform.portfolio.savings.SavingsInterestCalculationType;
import org.mifosplatform.portfolio.savings.SavingsPeriodFrequencyType;
import org.mifosplatform.portfolio.savings.SavingsPostingInterestPeriodType;

@Entity
@DiscriminatorValue("300")
public class RecurringDepositProduct extends FixedDepositProduct {

    @OneToOne(mappedBy = "product", cascade = CascadeType.ALL)
    private DepositProductRecurringDetail recurringDetail;

    protected RecurringDepositProduct() {
        super();
    }

    public static RecurringDepositProduct createNew(final String name, final String shortName, final String description,
            final MonetaryCurrency currency, final BigDecimal interestRate,
            final SavingsCompoundingInterestPeriodType interestCompoundingPeriodType,
            final SavingsPostingInterestPeriodType interestPostingPeriodType, final SavingsInterestCalculationType interestCalculationType,
            final SavingsInterestCalculationDaysInYearType interestCalculationDaysInYearType, final Integer lockinPeriodFrequency,
            final SavingsPeriodFrequencyType lockinPeriodFrequencyType, final AccountingRuleType accountingRuleType,
            final Set<Charge> charges, final DepositProductTermAndPreClosure productTermAndPreClosure,
            final DepositProductRecurringDetail recurringDetail, final Set<InterestRateChart> charts) {

        final BigDecimal minRequiredOpeningBalance = null;
        final boolean withdrawalFeeApplicableForTransfer = false;
        final boolean allowOverdraft = false;
        final BigDecimal overdraftLimit = null;

        return new RecurringDepositProduct(name, shortName, description, currency, interestRate, interestCompoundingPeriodType,
                interestPostingPeriodType, interestCalculationType, interestCalculationDaysInYearType, minRequiredOpeningBalance,
                lockinPeriodFrequency, lockinPeriodFrequencyType, withdrawalFeeApplicableForTransfer, accountingRuleType, charges,
                productTermAndPreClosure, recurringDetail, charts, allowOverdraft, overdraftLimit);
    }

    protected RecurringDepositProduct(final String name, final String shortName, final String description, final MonetaryCurrency currency,
            final BigDecimal interestRate, final SavingsCompoundingInterestPeriodType interestCompoundingPeriodType,
            final SavingsPostingInterestPeriodType interestPostingPeriodType, final SavingsInterestCalculationType interestCalculationType,
            final SavingsInterestCalculationDaysInYearType interestCalculationDaysInYearType, final BigDecimal minRequiredOpeningBalance,
            final Integer lockinPeriodFrequency, final SavingsPeriodFrequencyType lockinPeriodFrequencyType,
            final boolean withdrawalFeeApplicableForTransfer, final AccountingRuleType accountingRuleType, final Set<Charge> charges,
            final DepositProductTermAndPreClosure productTermAndPreClosure, final DepositProductRecurringDetail recurringDetail,
            final Set<InterestRateChart> charts, final boolean allowOverdraft, final BigDecimal overdraftLimit) {

        super(name, shortName, description, currency, interestRate, interestCompoundingPeriodType, interestPostingPeriodType,
                interestCalculationType, interestCalculationDaysInYearType, minRequiredOpeningBalance, lockinPeriodFrequency,
                lockinPeriodFrequencyType, withdrawalFeeApplicableForTransfer, accountingRuleType, charges, productTermAndPreClosure,
                charts, allowOverdraft, overdraftLimit);

        this.recurringDetail = recurringDetail;
    }

    @Override
    public Map<String, Object> update(final JsonCommand command) {
        final Map<String, Object> actualChanges = new LinkedHashMap<String, Object>(10);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<ApiParameterError>();
        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors)
                .resource(RECURRING_DEPOSIT_PRODUCT_RESOURCE_NAME);

        actualChanges.putAll(this.update(command, baseDataValidator));

        throwExceptionIfValidationWarningsExist(dataValidationErrors);

        return actualChanges;
    }

    @Override
    protected Map<String, Object> update(final JsonCommand command, final DataValidatorBuilder baseDataValidator) {
        final Map<String, Object> actualChanges = new LinkedHashMap<String, Object>(10);

        actualChanges.putAll(super.update(command, baseDataValidator));

        if (this.recurringDetail != null) {
            actualChanges.putAll(this.recurringDetail.update(command, baseDataValidator));
        }

        return actualChanges;
    }

    private void throwExceptionIfValidationWarningsExist(final List<ApiParameterError> dataValidationErrors) {
        if (!dataValidationErrors.isEmpty()) { throw new PlatformApiDataValidationException(dataValidationErrors); }
    }

    public DepositProductRecurringDetail depositRecurringDetail() {
        return this.recurringDetail;
    }
    
   	public void validatDepositPeriodAndTerm(final DataValidatorBuilder baseDataValidator) {
       
   			/*int depositTermTypeInt = depositProductTermAndPreClosure().depositTermDetail().depositTermType();
   			SavingsPeriodFrequencyType depositTermType  = SavingsPeriodFrequencyType.fromInt(depositTermTypeInt);
   			
   			int recurringPeriodFrequencyTypeInt = recurringDetail.recurringDetail().recurringDepositFrequencyTypeId();
   			SavingsPeriodFrequencyType recurringPeriodFrequencyType  = SavingsPeriodFrequencyType.fromInt(recurringPeriodFrequencyTypeInt);
           	
           	int depositTermFrequency = depositProductTermAndPreClosure().depositTermDetail().maxDepositTerm();
           	int recurringPeriodFrequency = recurringDetail.recurringDetail().recurringDepositFrequency();
           	
           	SavingsInterestCalculationDaysInYearType savingsInterestCalculationDaysInYearType = SavingsInterestCalculationDaysInYearType.fromInt(interestCalculationDaysInYearType);
           	*/
           	/*int totalTermAsDays = depositTermFrequency * PeriodFrequencyType.asDays
           			(depositTermTypeInt,savingsInterestCalculationDaysInYearType );
           	
           	int totalRecurringPeriodAsDays = recurringPeriodFrequency * PeriodFrequencyType.asDays
           			(recurringPeriodFrequencyTypeInt, savingsInterestCalculationDaysInYearType);
           	
           	if(totalTermAsDays < totalRecurringPeriodAsDays) {
           		baseDataValidator.failWithCodeNoParameterAddedToErrorCode("max.deposit.term.is.less.than.deposit.period",
           				depositTermFrequency,depositTermType.name(),
           				recurringPeriodFrequency,recurringPeriodFrequencyType.name());		
           } */
           	
         /*  	if(depositTermTypeInt != recurringPeriodFrequencyTypeInt) {
           		baseDataValidator.failWithCodeNoParameterAddedToErrorCode("max.deposit.term.is.less.than.deposit.period",
           				depositTermFrequency,depositTermType.name(),
           				recurringPeriodFrequency,recurringPeriodFrequencyType.name());		
           }*/
       }
}