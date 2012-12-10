package org.mifosplatform.portfolio.loanaccount.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface GroupLoanRepository extends JpaRepository<GroupLoan, Long>,
        JpaSpecificationExecutor<GroupLoan> {
    // no added behaviour
}