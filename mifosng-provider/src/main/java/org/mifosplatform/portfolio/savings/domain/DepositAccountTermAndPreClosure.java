/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.savings.domain;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.DataValidatorBuilder;
import org.mifosplatform.portfolio.savings.SavingsPeriodFrequencyType;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Table(name = "m_deposit_account_term_and_preclosure")
public class DepositAccountTermAndPreClosure extends AbstractPersistable<Long> {

    @Column(name = "deposit_amount", scale = 6, precision = 19, nullable = true)
    private BigDecimal depositAmount;

    @Column(name = "maturity_amount", scale = 6, precision = 19, nullable = true)
    private BigDecimal maturityAmount;

    @Column(name = "deposit_period", nullable = true)
    private Integer depositPeriod;

    @Column(name = "deposit_period_frequency_enum", nullable = true)
    private Integer depositPeriodFrequency;

    @Embedded
    private DepositPreClosureDetail preClosureDetail;

    @Embedded
    protected DepositTermDetail depositTermDetail;

    @OneToOne
    @JoinColumn(name = "savings_account_id", nullable = false)
    private SavingsAccount account;

    protected DepositAccountTermAndPreClosure() {
        super();
    }

    public static DepositAccountTermAndPreClosure createNew(DepositPreClosureDetail preClosureDetail, DepositTermDetail depositTermDetail,
            SavingsAccount account, BigDecimal depositAmount, BigDecimal maturityAmount, Integer depositPeriod,
            final SavingsPeriodFrequencyType depositPeriodFrequency) {

        return new DepositAccountTermAndPreClosure(preClosureDetail, depositTermDetail, account, depositAmount, maturityAmount,
                depositPeriod, depositPeriodFrequency);
    }

    private DepositAccountTermAndPreClosure(DepositPreClosureDetail preClosureDetail, DepositTermDetail depositTermDetail,
            SavingsAccount account, BigDecimal depositAmount, BigDecimal maturityAmount, Integer depositPeriod,
            final SavingsPeriodFrequencyType depositPeriodFrequency) {
        this.depositAmount = depositAmount;
        this.maturityAmount = maturityAmount;
        this.depositPeriod = depositPeriod;
        this.depositPeriodFrequency = (depositPeriodFrequency == null) ? null : depositPeriodFrequency.getValue();
        this.preClosureDetail = preClosureDetail;
        this.depositTermDetail = depositTermDetail;
        this.account = account;
    }

    public Map<String, Object> update(final JsonCommand command, final DataValidatorBuilder baseDataValidator) {
        final Map<String, Object> actualChanges = new LinkedHashMap<String, Object>(10);
        if (this.preClosureDetail != null) {
            actualChanges.putAll(this.preClosureDetail.update(command, baseDataValidator));
        }

        if (this.depositTermDetail != null) {
            actualChanges.putAll(this.depositTermDetail.update(command, baseDataValidator));
        }
        return actualChanges;
    }

    public DepositPreClosureDetail depositPreClosureDetail() {
        return this.preClosureDetail;
    }

    public DepositTermDetail depositTermDetail() {
        return this.depositTermDetail;
    }

    public void updateAccountReference(final SavingsAccount account) {
        this.account = account;
    }
}