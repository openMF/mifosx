package org.mifosplatform.infrastructure.smsgateway.domain;

import org.mifosplatform.infrastructure.smsgateway.domain.SmsGateway;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface SmsGatewayRepository extends JpaRepository<SmsGateway, Long>, JpaSpecificationExecutor<SmsGateway> {
    // no extra behavior
}
