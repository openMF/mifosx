package org.mifosplatform.portfolio.loanaccount.gaurantor.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface GuarantorRepository extends JpaRepository<Guarantor, Long>, JpaSpecificationExecutor<Guarantor> {
    // no behaviour
}