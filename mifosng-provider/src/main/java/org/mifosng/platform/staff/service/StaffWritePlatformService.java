package org.mifosng.platform.staff.service;

import org.mifosng.platform.api.commands.StaffCommand;
import org.springframework.security.access.prepost.PreAuthorize;

public interface StaffWritePlatformService {

	@PreAuthorize(value = "hasRole('ORGANISATION_ADMINISTRATION_SUPER_USER_ROLE')")
	Long createStaff(final StaffCommand command);
	
	@PreAuthorize(value = "hasRole('ORGANISATION_ADMINISTRATION_SUPER_USER_ROLE')")
	Long updateStaff(final StaffCommand command);
}