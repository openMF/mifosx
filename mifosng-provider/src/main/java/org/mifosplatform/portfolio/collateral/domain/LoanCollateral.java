/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.collateral.domain;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.mifosplatform.infrastructure.codes.data.CodeValueData;
import org.mifosplatform.infrastructure.codes.domain.CodeValue;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.portfolio.collateral.data.CollateralData;
import org.mifosplatform.portfolio.loanaccount.domain.Loan;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Table(name = "m_loan_collateral")
public class LoanCollateral extends AbstractPersistable<Long> {

    @SuppressWarnings("unused")
    @ManyToOne(optional = false)
    @JoinColumn(name = "loan_id", nullable = false)
    private Loan loan;

    @ManyToOne
    @JoinColumn(name = "type_cv_id", nullable = false)
    private CodeValue type;

    @Column(name = "value", scale = 6, precision = 19)
    private BigDecimal value;

    @Column(name = "description", length = 500)
    private String description;

    public static LoanCollateral from(final CodeValue collateralType, final BigDecimal value, final String description) {
        return new LoanCollateral(null, collateralType, value, description);
    }

    protected LoanCollateral() {
        //
    }

    private LoanCollateral(final Loan loan, final CodeValue collateralType, final BigDecimal value, final String description) {
        this.loan = loan;
        this.type = collateralType;
        this.value = value;
        this.description = StringUtils.defaultIfEmpty(description, null);
    }

    public void assembleFrom(final CodeValue collateralType, final BigDecimal value, final String description) {
        this.type = collateralType;
        this.description = description;
        this.value = value;
    }

    public void associateWith(final Loan loan) {
        this.loan = loan;
    }

    public static LoanCollateral fromJson(final Loan loan, final CodeValue collateralType, final JsonCommand command) {
        final String description = command.stringValueOfParameterNamed("description");
        final BigDecimal value = command.bigDecimalValueOfParameterNamed("value");
        return new LoanCollateral(loan, collateralType, value, description);
    }

    public Map<String, Object> update(final JsonCommand command) {

        final Map<String, Object> actualChanges = new LinkedHashMap<String, Object>(7);

        final String collateralTypeIdParamName = "type";
        if (command.isChangeInLongParameterNamed(collateralTypeIdParamName, this.type.getId())) {
            final Long newValue = command.longValueOfParameterNamed(collateralTypeIdParamName);
            actualChanges.put(collateralTypeIdParamName, newValue);
        }

        final String descriptionParamName = "description";
        if (command.isChangeInStringParameterNamed(descriptionParamName, this.description)) {
            final String newValue = command.stringValueOfParameterNamed(descriptionParamName);
            actualChanges.put(descriptionParamName, newValue);
            this.description = StringUtils.defaultIfEmpty(newValue, null);
        }

        final String valueParamName = "value";
        if (command.isChangeInBigDecimalParameterNamed(valueParamName, this.value)) {
            final BigDecimal newValue = command.bigDecimalValueOfParameterNamed(valueParamName);
            actualChanges.put(valueParamName, newValue);
            this.value = newValue;
        }

        return actualChanges;
    }

    public CollateralData toData() {
        final CodeValueData typeData = this.type.toData();
        return CollateralData.instance(this.getId(), typeData, this.value, this.description);
    }

    public void setCollateralType(CodeValue type) {
        this.type = type;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null) { return false; }
        if (obj == this) { return true; }
        if (obj.getClass() != getClass()) { return false; }
        LoanCollateral rhs = (LoanCollateral) obj;
        return new EqualsBuilder().appendSuper(super.equals(obj)) //
                .append(this.getId(), rhs.getId()) //
                .append(this.type.getId(), rhs.type.getId()) //
                .append(this.description, rhs.description) //
                .append(this.value, this.value)//
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(3, 5) //
                .append(this.getId()) //
                .append(this.type.getId()) //
                .append(this.description) //
                .append(this.value)//
                .toHashCode();
    }
}