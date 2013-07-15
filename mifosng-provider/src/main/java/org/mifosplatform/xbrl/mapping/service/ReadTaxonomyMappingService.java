package org.mifosplatform.xbrl.mapping.service;

import java.math.BigDecimal;
import java.util.HashMap;

import org.mifosplatform.xbrl.mapping.data.TaxonomyMappingData;
import org.mifosplatform.xbrl.taxonomy.data.TaxonomyData;

public interface ReadTaxonomyMappingService {
	TaxonomyMappingData retrieveTaxonomyMapping();
	HashMap<TaxonomyData, BigDecimal> retrieveTaxonomyConfig();
}
