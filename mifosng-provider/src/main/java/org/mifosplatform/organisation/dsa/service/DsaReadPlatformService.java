package org.mifosplatform.organisation.dsa.service;

import java.util.Collection;

import org.mifosplatform.organisation.dsa.data.DsaData;

public interface DsaReadPlatformService {

	DsaData retrieveDsa(Long dsaId);
	
	Collection<DsaData>retrieveAllDsaForDropdown(Long officeId);
	
	Collection<DsaData>retrieveAllDsaOfficersInOfficeById(final Long officeId);
	
	/**
     * returns all dsa in offices that are above the provided
     * <code>officeId</code>.
     */
	
	
	
	Collection<DsaData>retrieveAllDsa(String sqlSearch, Long officeId, boolean activeOnly, String status);
	
	
	Collection<DsaData>retrieveAllDsaInOfficeAndItsParentOfficeHierarchy(
			Long officeId, boolean activeOnly);
	
	
	
}
