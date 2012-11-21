package org.mifosng.platform.guarantor;

import org.mifosng.platform.api.commands.GuarantorCommand;
import org.mifosng.platform.api.data.GuarantorData;
import org.springframework.security.access.prepost.PreAuthorize;

public interface GuarantorReadWritePlatformService {

	GuarantorData retrieveGuarantor(Long loanId);

	boolean existsGuarantor(Long loanId);

	@PreAuthorize(value = "hasRole('PORTFOLIO_MANAGEMENT_SUPER_USER_ROLE')")
	void createGuarantor(final Long loanId, final GuarantorCommand command);

	@PreAuthorize(value = "hasRole('PORTFOLIO_MANAGEMENT_SUPER_USER_ROLE')")
	void updateGuarantor(final Long loanId, final GuarantorCommand command);

	@PreAuthorize(value = "hasRole('PORTFOLIO_MANAGEMENT_SUPER_USER_ROLE')")
	void removeGuarantor(final Long loanId);

}