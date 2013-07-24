package org.mifosplatform.portfolio.loanaccount;

import org.mifosplatform.organisation.monetary.domain.MonetaryCurrency;

public class MonetaryCurrencyBuilder {

    private String code = "XOF";
    private int digitsAfterDecimal = 0;

    public MonetaryCurrency build() {
        return new MonetaryCurrency(code, digitsAfterDecimal);
    }

    public MonetaryCurrencyBuilder withCode(final String withCode) {
        this.code = withCode;
        return this;
    }

    public MonetaryCurrencyBuilder withDigitsAfterDecimal(final int withDigitsAfterDecimal) {
        this.digitsAfterDecimal = withDigitsAfterDecimal;
        return this;
    }
}
