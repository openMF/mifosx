/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.savings.domain;

import static org.mifosplatform.portfolio.savings.DepositsApiConstants.depositTermTypeIdParamName;
import static org.mifosplatform.portfolio.savings.DepositsApiConstants.inMultiplesOfDepositTermTypeIdParamName;
import static org.mifosplatform.portfolio.savings.DepositsApiConstants.maxDepositTermParamName;
import static org.mifosplatform.portfolio.savings.DepositsApiConstants.minDepositTermParamName;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.DataValidatorBuilder;
import org.mifosplatform.portfolio.savings.SavingsPeriodFrequencyType;
import org.mifosplatform.portfolio.savings.service.SavingsEnumerations;

/**
 * DepositTermDetail encapsulates all the details of a
 * {@link FixedDepositProduct} that are also used and persisted by a
 * {@link FixedDepositAccount}.
 */
@Embeddable
public class DepositTermDetail {

    @Column(name = "min_deposit_term", nullable = true)
    private Integer minDepositTerm;

    @Column(name = "max_deposit_term", nullable = true)
    private Integer maxDepositTerm;

    @Column(name = "deposit_term_enum", nullable = true)
    private Integer depositTermType;

    @Column(name = "in_multiples_of_deposit_term", nullable = true)
    private Integer inMultiplesOfDepositTerm;

    @Column(name = "in_multiples_of_deposit_term_enum", nullable = true)
    private Integer inMultiplesOfDepositTermType;

    public static DepositTermDetail createFrom(final Integer minDepositTerm, final Integer maxDepositTerm,
            final SavingsPeriodFrequencyType depositTermType, final Integer inMultiplesOfDepositTerm,
            final SavingsPeriodFrequencyType inMultiplesOfDepositTermType) {

        return new DepositTermDetail(minDepositTerm, maxDepositTerm, depositTermType, inMultiplesOfDepositTerm,
                inMultiplesOfDepositTermType);
    }

    protected DepositTermDetail() {
        //
    }

    private DepositTermDetail(final Integer minDepositTerm, final Integer maxDepositTerm, final SavingsPeriodFrequencyType depositTermType,
            final Integer inMultiplesOfDepositTerm, final SavingsPeriodFrequencyType inMultiplesOfDepositTermType) {
        this.minDepositTerm = minDepositTerm;
        this.maxDepositTerm = maxDepositTerm;
        this.depositTermType = (depositTermType == null) ? null : depositTermType.getValue();
        this.inMultiplesOfDepositTerm = inMultiplesOfDepositTerm;
        this.inMultiplesOfDepositTermType = (inMultiplesOfDepositTermType == null) ? null : inMultiplesOfDepositTermType.getValue();
    }

    public Map<String, Object> update(final JsonCommand command, final DataValidatorBuilder baseDataValidator) {
        final Map<String, Object> actualChanges = new LinkedHashMap<String, Object>(10);

        if (command.isChangeInIntegerParameterNamed(minDepositTermParamName, this.minDepositTerm)) {
            final Integer newValue = command.integerValueOfParameterNamed(minDepositTermParamName);
            actualChanges.put(minDepositTermParamName, newValue);
            this.minDepositTerm = newValue;
        }

        if (command.isChangeInIntegerParameterNamed(maxDepositTermParamName, this.maxDepositTerm)) {
            final Integer newValue = command.integerValueOfParameterNamed(maxDepositTermParamName);
            actualChanges.put(maxDepositTermParamName, newValue);
            this.maxDepositTerm = newValue;
        }

        if (command.isChangeInIntegerParameterNamed(depositTermTypeIdParamName, this.depositTermType)) {
            final Integer newValue = command.integerValueOfParameterNamed(depositTermTypeIdParamName);
            actualChanges.put(depositTermTypeIdParamName, SavingsEnumerations.depositTermFrequencyType(newValue));
            this.depositTermType = newValue;
        }

        if (command.isChangeInIntegerParameterNamed(inMultiplesOfDepositTermTypeIdParamName, this.inMultiplesOfDepositTerm)) {
            final Integer newValue = command.integerValueOfParameterNamed(inMultiplesOfDepositTermTypeIdParamName);
            actualChanges.put(inMultiplesOfDepositTermTypeIdParamName, newValue);
            this.inMultiplesOfDepositTerm = newValue;
        }

        if (command.isChangeInIntegerParameterNamed(inMultiplesOfDepositTermTypeIdParamName, this.inMultiplesOfDepositTermType)) {
            final Integer newValue = command.integerValueOfParameterNamed(inMultiplesOfDepositTermTypeIdParamName);
            actualChanges.put(inMultiplesOfDepositTermTypeIdParamName, SavingsEnumerations.inMultiplesOfDepositTermFrequencyType(newValue));
            this.inMultiplesOfDepositTermType = newValue;
        }
        
        if(isMinDepositTermGreaterThanMaxDepositTerm()){
            baseDataValidator.parameter(minDepositTermParamName).value(this.minDepositTerm)
            .failWithCode(".greater.than.maxDepositTerm", this.minDepositTerm,this.maxDepositTerm);
        }
        
        return actualChanges;
    }
    
    private boolean isMinDepositTermGreaterThanMaxDepositTerm() {
        if (this.minDepositTerm != null && this.maxDepositTerm != null) {
            if (this.minDepositTerm.compareTo(maxDepositTerm) > 0) { return true; }
        }
        return false;
    }

    
    public Integer minDepositTerm() {
        return this.minDepositTerm;
    }

    
    public Integer maxDepositTerm() {
        return this.maxDepositTerm;
    }

    
    public Integer depositTermType() {
        return this.depositTermType;
    }

    
    public Integer inMultiplesOfDepositTerm() {
        return this.inMultiplesOfDepositTerm;
    }

    
    public Integer inMultiplesOfDepositTermType() {
        return this.inMultiplesOfDepositTermType;
    }

    
}