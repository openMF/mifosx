package org.mifosplatform.clientimpactportal.service;

import org.mifosplatform.clientimpactportal.data.ImpactPortalData;

import java.util.Collection;


public interface ImpactPortalReadService {

    public ImpactPortalData getDataByNameForYesterday(String name);
    
    public ImpactPortalData getDataByDate(String name, String date);

    public Collection<ImpactPortalData> getDataByDateRange(String name, String startDate, String endDate);
}
