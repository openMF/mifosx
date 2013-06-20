package org.mifosplatform.infrastructure.xbrl.service;

import java.util.List;

import org.mifosplatform.infrastructure.xbrl.data.TaxonomyData;

public interface ReadTaxonomyService {
	List<TaxonomyData> retrieveAllTaxonomyMapping();
}
