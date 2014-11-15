/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.mifosplatform.portfolio.loanaccount.rescheduleloan.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LoanRescheduleRequestRepository extends JpaRepository<LoanRescheduleRequest, Long>, JpaSpecificationExecutor<LoanRescheduleRequest> {

	public static final String FIND_RESCHEDULE_REQUEST = "from LoanRescheduleRequest loanRescheduleRequest where loanRescheduleRequest.loan.id = :loanId"
														+ " and loanRescheduleRequest.statusEnum = :statusEnum";
	@Query(FIND_RESCHEDULE_REQUEST)
	LoanRescheduleRequest findLoanRescheduleRequestByLoanId(@Param("loanId") Long loanId, @Param("statusEnum") Integer statusEnum);
}
