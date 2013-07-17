package org.mifosplatform.xbrl.report.service;

import java.util.Map;

public interface ReadXBRLReportService {
	String processXBRLReport(Map<String, String> queryParams);
}
