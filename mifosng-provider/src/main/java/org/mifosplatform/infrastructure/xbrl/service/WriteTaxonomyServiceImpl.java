package org.mifosplatform.infrastructure.xbrl.service;

import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.infrastructure.xbrl.domain.TaxonomyMapping;
import org.mifosplatform.infrastructure.xbrl.domain.TaxonomyMappingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
public class WriteTaxonomyServiceImpl implements WriteTaxonomyService {

	private final static Logger logger = LoggerFactory.getLogger(WriteTaxonomyServiceImpl.class);
	
	private final PlatformSecurityContext context;
	private final TaxonomyMappingRepository mappingRepository;
	
	
	@Autowired
	public WriteTaxonomyServiceImpl(final PlatformSecurityContext context,
			final TaxonomyMappingRepository mappingRepository) {
		this.context = context;
		this.mappingRepository = mappingRepository;
	}
	
	@Override
	public CommandProcessingResult updateMapping(JsonCommand command) {
		try {
			context.authenticatedUser();
			
			final TaxonomyMapping mapping = TaxonomyMapping.fromJson(command);
			
		} catch (DataIntegrityViolationException dve) {
			
		}
		return null;
	}

}
