package org.mifosng.platform.chartaccount.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ChartAccountRepository extends JpaRepository<ChartAccount, Long>, JpaSpecificationExecutor<ChartAccount> {
	// no added behaviour


}
