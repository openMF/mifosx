package org.mifosplatform.infrastructure.configuration.domain;

import org.mifosplatform.infrastructure.configuration.exception.ExternalServiceConfigurationNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ExternalServicesPropertiesRepositoryWrapper {

	private final ExternalServicesPropertiesRepository repository;
	
	@Autowired
	public ExternalServicesPropertiesRepositoryWrapper(final ExternalServicesPropertiesRepository repository){
		this.repository = repository;
	}
	
	public ExternalServicesProperties findOneByIdAndName(Long id, String name){
		final ExternalServicesProperties externalServicesProperties = this.repository.findOneByExternalServicePropertiesPK(new ExternalServicePropertiesPK(id, name));
		if(externalServicesProperties == null)
			throw new ExternalServiceConfigurationNotFoundException(id, name);
		return externalServicesProperties;
	}
}
