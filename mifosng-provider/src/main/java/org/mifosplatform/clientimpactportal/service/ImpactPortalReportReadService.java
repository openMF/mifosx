package org.mifosplatform.clientimpactportal.service;

import org.mifosplatform.clientimpactportal.data.ImpactPortalSqlData;

import java.util.Collection;


public interface ImpactPortalReportReadService {

    Collection<ImpactPortalSqlData> retrieveSqlForPortalReports();
}
