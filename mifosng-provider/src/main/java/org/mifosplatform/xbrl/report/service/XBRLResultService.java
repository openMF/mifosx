package org.mifosplatform.xbrl.report.service;

import java.sql.Date;

import org.mifosplatform.xbrl.taxonomy.data.XBRLData;

public interface XBRLResultService {

	public abstract XBRLData getXBRLResult(Date startDate, Date endDate,
			String currency);

}