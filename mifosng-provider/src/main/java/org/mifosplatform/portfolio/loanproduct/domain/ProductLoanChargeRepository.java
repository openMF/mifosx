package org.mifosplatform.portfolio.loanproduct.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;


public interface ProductLoanChargeRepository extends JpaRepository<ProductLoanCharge, Long>, JpaSpecificationExecutor<ProductLoanCharge>{
 // no added behaviour
}
