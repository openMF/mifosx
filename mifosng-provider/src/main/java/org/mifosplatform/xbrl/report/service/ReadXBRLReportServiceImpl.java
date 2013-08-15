package org.mifosplatform.xbrl.report.service;

import java.util.Map;

import org.mifosplatform.xbrl.mapping.service.ReadTaxonomyMappingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReadXBRLReportServiceImpl implements ReadXBRLReportService {
	
	private final ReadTaxonomyMappingService readTaxonomyMappingService;

	@Autowired
	public ReadXBRLReportServiceImpl(
			ReadTaxonomyMappingService readTaxonomyMappingService) {
		this.readTaxonomyMappingService = readTaxonomyMappingService;
	}


	@Override
	public String processXBRLReport(Map<String, String> queryParams) {
		return null;
	}

}
