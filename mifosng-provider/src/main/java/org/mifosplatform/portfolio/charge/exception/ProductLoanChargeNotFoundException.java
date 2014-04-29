package org.mifosplatform.portfolio.charge.exception;

import org.mifosplatform.infrastructure.core.exception.AbstractPlatformResourceNotFoundException;


public class ProductLoanChargeNotFoundException extends AbstractPlatformResourceNotFoundException {

    public ProductLoanChargeNotFoundException(final Long id) {
        super("error.msg.productLoanCharge.id.invalid", "Product loan charge with identifier " + id + " does not exist", id);
    }
}
