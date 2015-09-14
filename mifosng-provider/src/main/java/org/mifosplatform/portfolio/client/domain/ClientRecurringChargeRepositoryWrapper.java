/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.client.domain;

import org.mifosplatform.portfolio.charge.exception.ChargeNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class ClientRecurringChargeRepositoryWrapper {



    private final ClientRecurringChargeRepository repository;

    @Autowired
    public ClientRecurringChargeRepositoryWrapper(final ClientRecurringChargeRepository repository) {
        
        this.repository = repository;
    }

    public ClientRecurringCharge findOneWithNotFoundDetection(final Long id) {
        final ClientRecurringCharge clientRecurringCharge = this.repository.findOne(id);
        if (clientRecurringCharge == null) { throw new ChargeNotFoundException(id); }
        return clientRecurringCharge;
    }

}
