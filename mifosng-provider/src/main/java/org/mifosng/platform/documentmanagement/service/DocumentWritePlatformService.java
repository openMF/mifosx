package org.mifosng.platform.documentmanagement.service;

import java.io.InputStream;

import org.mifosng.platform.api.commands.DocumentCommand;
import org.mifosng.platform.api.data.EntityIdentifier;
import org.springframework.security.access.prepost.PreAuthorize;

public interface DocumentWritePlatformService {

	@PreAuthorize(value = "hasAnyRole('PORTFOLIO_MANAGEMENT_SUPER_USER_ROLE')")
	Long createDocument(DocumentCommand documentCommand, InputStream inputStream);

	@PreAuthorize(value = "hasAnyRole('PORTFOLIO_MANAGEMENT_SUPER_USER_ROLE')")
	EntityIdentifier updateDocument(DocumentCommand documentCommand,
			InputStream inputStream);

	@PreAuthorize(value = "hasAnyRole('PORTFOLIO_MANAGEMENT_SUPER_USER_ROLE')")
	EntityIdentifier deleteDocument(DocumentCommand documentCommand);

}