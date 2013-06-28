package org.mifosplatform.infrastructure.xbrl.handler;

import org.mifosplatform.commands.handler.NewCommandSourceHandler;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.xbrl.service.WriteTaxonomyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UpdateTaxonomyMappingCommandHandler implements
		NewCommandSourceHandler {

	private final WriteTaxonomyService writeTaxonomyService;
	
	@Autowired
	public UpdateTaxonomyMappingCommandHandler(
			WriteTaxonomyService writeTaxonomyService) {
		this.writeTaxonomyService = writeTaxonomyService;
	}

	@Transactional
	@Override
	public CommandProcessingResult processCommand(JsonCommand command) {
		return this.writeTaxonomyService.updateMapping(command);
	}

}
