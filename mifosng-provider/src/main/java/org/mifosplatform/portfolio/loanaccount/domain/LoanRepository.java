/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.loanaccount.domain;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LoanRepository extends JpaRepository<Loan, Long>,
		JpaSpecificationExecutor<Loan> {
        @Query("from Loan loan where loan.client.id = :clientId and loan.group.id = :groupId")
	List<Loan> findByClientIdAndGroupId(@Param("clientId") Long clientId, @Param("groupId") Long groupId);
        
        @Query("from Loan loan where loan.client.id = :clientId")
        List<Loan> findLoanByClientId(@Param("clientId") Long clientId);

        @Query("from Loan loan where loan.id IN :ids and loan.loanStatus IN :loanStatuses and loan.loanType IN :loanTypes")
        List<Loan> findByIdsAndLoanStatusAndLoanType(@Param("ids") Collection<Long> ids, @Param("loanStatuses") Collection<Integer> loanStatuses, @Param("loanTypes") Collection<Integer> loanTypes);
}