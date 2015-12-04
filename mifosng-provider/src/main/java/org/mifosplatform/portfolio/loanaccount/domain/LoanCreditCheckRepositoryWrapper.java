/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.loanaccount.domain;

import org.mifosplatform.portfolio.loanaccount.exception.LoanCreditCheckNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/** 
 * A wrapper class for the LoanCreditCheckRepository that provides a method that returns a LoanCreditCheck entity if it exists, 
 * else throws "LoanCreditCheckNotFoundException" exception if the loan credit check does not exist
 **/
@Service
public class LoanCreditCheckRepositoryWrapper {
    private final LoanCreditCheckRepository loanCreditCheckRepository;
    
    @Autowired
    public LoanCreditCheckRepositoryWrapper(final LoanCreditCheckRepository loanCreditCheckRepository) {
        this.loanCreditCheckRepository = loanCreditCheckRepository;
    }
    
    public LoanCreditCheck findOneThrowExceptionIfNotFound(final Long id) {
        final LoanCreditCheck loanCreditCheck = this.loanCreditCheckRepository.findOne(id);
        
        if (loanCreditCheck == null) {
            throw new LoanCreditCheckNotFoundException(id);
        }
        
        return loanCreditCheck;
    }
    
    public LoanCreditCheckRepository getLoanCreditCheckRepository() {
        return this.loanCreditCheckRepository;
    }
}
