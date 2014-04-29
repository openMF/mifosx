package org.mifosplatform.portfolio.loanproduct.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.mifosplatform.portfolio.charge.domain.Charge;
import org.mifosplatform.portfolio.loanproduct.data.ProductLoanChargeData;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Table(name = "m_product_loan_charge")
public class ProductLoanCharge extends AbstractPersistable<Long> {

    @ManyToOne(optional = false)
    @JoinColumn(name = "product_loan_id", nullable = false)
    private LoanProduct loanProduct;

    @ManyToOne(optional = false)
    @JoinColumn(name = "charge_id", nullable = false)
    private Charge charge;

    @Column(name = "is_mandatory", nullable = false)
    private Boolean isMandatory;

    public ProductLoanCharge() {
        // TODO Auto-generated constructor stub
    }

    public static ProductLoanCharge createNewFromJson(final Charge charge, final boolean isMandatory) {
        return new ProductLoanCharge(null, charge, isMandatory);
    }

    private ProductLoanCharge(final LoanProduct loanProduct, final Charge charge, final boolean isMandatory) {
        this.loanProduct = loanProduct;
        this.charge = charge;
        this.isMandatory = isMandatory;
    }

    public void update(final LoanProduct loanProduct) {
        this.loanProduct = loanProduct;
    }

    public Boolean isMandatory() {
        return this.isMandatory;
    }

    public ProductLoanChargeData toObject() {
        return ProductLoanChargeData.instance(getId(), this.isMandatory, this.charge.getId());
    }

    public void update(final Boolean isMandatory) {
        this.isMandatory = isMandatory;
    }

    public Charge getCharge() {
        return this.charge;
    }
}
