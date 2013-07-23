/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.loanaccount.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LoanChargeRepository extends JpaRepository<LoanCharge, Long>, JpaSpecificationExecutor<LoanCharge> {

    @Query("select count(*) from LoanCharge lc where lc.charge.id = :chargeId")
    Long countLoansByChargeId(@Param("chargeId") Long chargeId);
}
