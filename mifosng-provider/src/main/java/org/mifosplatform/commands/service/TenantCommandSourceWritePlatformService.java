package org.mifosplatform.commands.service;

import org.mifosplatform.commands.domain.CommandWrapper;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;

public interface TenantCommandSourceWritePlatformService {

	CommandProcessingResult logCommandSource(CommandWrapper commandRequest);

	CommandProcessingResult approveEntry(Long id);

	Long deleteEntry(Long makerCheckerId);
}
