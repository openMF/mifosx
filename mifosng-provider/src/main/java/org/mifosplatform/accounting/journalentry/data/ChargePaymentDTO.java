package org.mifosplatform.accounting.journalentry.data;

import java.math.BigDecimal;

public class ChargePaymentDTO {

    private final Long chargeId;
    private final BigDecimal amount;
    private final Long loanChargeId;

    public ChargePaymentDTO(final Long chargeId, final Long loanChargeId, final BigDecimal amount) {
        this.chargeId = chargeId;
        this.amount = amount;
        this.loanChargeId = loanChargeId;
    }

    public Long getChargeId() {
        return this.chargeId;
    }

    public BigDecimal getAmount() {
        return this.amount;
    }

    public Long getLoanChargeId() {
        return this.loanChargeId;
    }

}
