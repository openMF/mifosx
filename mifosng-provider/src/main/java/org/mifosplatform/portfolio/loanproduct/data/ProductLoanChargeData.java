package org.mifosplatform.portfolio.loanproduct.data;

public class ProductLoanChargeData {

    private final Long id;
    private final Boolean isMandatory;
    @SuppressWarnings("unused")
    private final Long chargeId;

    private ProductLoanChargeData(final Long id, final Boolean isMandatory, final Long chargeId) {
        this.id = id;
        this.isMandatory = isMandatory;
        this.chargeId = chargeId;
    }

    public Long getId() {
        return this.id;
    }

    public Boolean getIsMandatory() {
        return this.isMandatory;
    }

    public static ProductLoanChargeData instance(final Long id, final Boolean isMandatory, final Long chargeId) {
        return new ProductLoanChargeData(id, isMandatory, chargeId);
    }
}
