package org.mifosplatform.xbrl.report.service;

import java.util.Map;

import javax.ws.rs.core.Response;

public interface ReadXBRLReportService {
	String processXBRLReport(Map<String, String> queryParams);
}
