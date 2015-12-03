/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.creditcheck.domain;

import org.mifosplatform.portfolio.creditcheck.exception.CreditCheckNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/** 
 *  A wrapper class for the CreditCheckRepository that provides a method that returns a CreditCheck entity if it exists, 
 *  else throws "CreditCheckNotFoundException" exception if the credit check does not exist or has been deleted 
 **/
@Service
public class CreditCheckRepositoryWrapper {
    private final CreditCheckRepository creditCheckRepository;
    
    @Autowired
    public CreditCheckRepositoryWrapper(final CreditCheckRepository creditCheckRepository) {
        this.creditCheckRepository = creditCheckRepository;
    }
    
    public CreditCheck findOneThrowExceptionIfNotFound(final Long id) {
        final CreditCheck creditCheck = this.creditCheckRepository.findOne(id);
        
        if (creditCheck == null || creditCheck.isDeleted()) {
            throw new CreditCheckNotFoundException(id);
        }
        
        return creditCheck;
    }
    
    public CreditCheckRepository getCreditCheckRepository() {
        return this.creditCheckRepository;
    }
}
