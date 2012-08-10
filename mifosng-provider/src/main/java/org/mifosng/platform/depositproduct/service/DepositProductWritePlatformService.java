package org.mifosng.platform.depositproduct.service;

import org.mifosng.platform.api.commands.DepositProductCommand;
import org.mifosng.platform.api.data.EntityIdentifier;
import org.springframework.security.access.prepost.PreAuthorize;

public interface DepositProductWritePlatformService {
	
	@PreAuthorize(value = "hasRole('ORGANISATION_ADMINISTRATION_SUPER_USER_ROLE')")
	EntityIdentifier createDepositProduct(DepositProductCommand command);
	
	@PreAuthorize(value = "hasRole('ORGANISATION_ADMINISTRATION_SUPER_USER_ROLE')")
	EntityIdentifier updateDepositProduct(DepositProductCommand command);
	
	@PreAuthorize(value = "hasRole('ORGANISATION_ADMINISTRATION_SUPER_USER_ROLE')")
	EntityIdentifier deleteDepositProduct(Long productId);

}
