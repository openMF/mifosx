package org.mifosplatform.clientimpactportal.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;


public interface ImpactPortalCacheRepository extends JpaRepository<ImpactPortalCacheData, Long>, JpaSpecificationExecutor<ImpactPortalCacheData> {


}
