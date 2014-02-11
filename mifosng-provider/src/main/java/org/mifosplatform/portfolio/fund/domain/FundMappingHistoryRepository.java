package org.mifosplatform.portfolio.fund.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FundMappingHistoryRepository extends JpaRepository<FundMappingHistory, Long>, JpaSpecificationExecutor<FundMappingHistory> {
	
	@Query("from FundMappingHistory  fundmap where fundmap.loan.id = :loanId and fundmap.fundTypeCodeValue.id = :fundTypeId")
    FundMappingHistory loanAccountFundMapChanged(@Param("loanId") Long loanId, @Param("fundTypeId") Long fundTypeId);
}
