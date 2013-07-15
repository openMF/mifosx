package org.mifosplatform.xbrl.report.service;

import org.mifosplatform.xbrl.report.data.NamespaceData;

public interface ReadNamespaceService {

	public NamespaceData retrieveNamespaceById(Long id);
	
	public NamespaceData retrieveNamespaceByPrefix(String prefix);
}
