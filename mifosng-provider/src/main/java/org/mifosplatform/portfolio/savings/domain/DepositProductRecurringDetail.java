/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.savings.domain;

import static org.mifosplatform.portfolio.savings.DepositsApiConstants.depositEveryParamName;
import static org.mifosplatform.portfolio.savings.DepositsApiConstants.depositEveryTypeParamName;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Table(name = "m_deposit_product_recurring_detail")
public class DepositProductRecurringDetail extends AbstractPersistable<Long> {

    @Embedded
    private DepositRecurringDetail recurringDetail;
    
    @Column(name = "deposit_every", nullable = true)
    private Integer depositEvery;

	@Column(name = "deposit_every_type_enum", nullable = true)
    private Integer depositEveryType;

    @OneToOne
    @JoinColumn(name = "savings_product_id", nullable = false)
    private RecurringDepositProduct product;

    protected DepositProductRecurringDetail() {
        super();
    }

    public static DepositProductRecurringDetail createNew(DepositRecurringDetail recurringDetail, SavingsProduct product,Integer depositEvery,Integer depositEveryType) {

        return new DepositProductRecurringDetail(recurringDetail, product,depositEvery,depositEveryType);
    }

    private DepositProductRecurringDetail(DepositRecurringDetail recurringDetail, SavingsProduct product,Integer depositEvery,Integer depositEveryType) {
        this.recurringDetail = recurringDetail;
        this.product = (RecurringDepositProduct) product;
        this.depositEvery=depositEvery;
        this.depositEveryType=depositEveryType;
    }

    public Map<String, Object> update(final JsonCommand command) {
        final Map<String, Object> actualChanges = new LinkedHashMap<>(10);
        if (this.recurringDetail != null) {
            actualChanges.putAll(this.recurringDetail.update(command));
        }
        
        
        if (command.isChangeInIntegerParameterNamed(depositEveryParamName, this.depositEvery)){
        	final Integer newValue=command.integerValueOfParameterNamed(depositEveryParamName);
        	actualChanges.put(depositEveryParamName, newValue);
        	this.depositEvery=newValue;
        }
        
        if (command.isChangeInIntegerParameterNamed(depositEveryTypeParamName, this.depositEveryType)){
        	final Integer newValue=command.integerValueOfParameterNamed(depositEveryTypeParamName);
        	actualChanges.put(depositEveryTypeParamName, newValue);
        	this.depositEveryType=newValue;
        }
        return actualChanges;
    }

    public DepositRecurringDetail recurringDetail() {
        return this.recurringDetail;
    }

    public void updateProductReference(final SavingsProduct product) {
        this.product = (RecurringDepositProduct) product;
    }
    

    public Integer getDepositEvery() {
		return depositEvery;
	}

	public void setDepositEvery(Integer depositEvery) {
		this.depositEvery = depositEvery;
	}

	public Integer getDepositEveryType() {
		return depositEveryType;
	}

	public void setDepositEveryType(Integer depositEveryType) {
		this.depositEveryType = depositEveryType;
	}

}