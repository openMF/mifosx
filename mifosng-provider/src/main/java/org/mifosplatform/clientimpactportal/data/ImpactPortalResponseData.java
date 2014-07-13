package org.mifosplatform.clientimpactportal.data;

import java.util.ArrayList;
import java.util.Date;


public class ImpactPortalResponseData {

    private String tenantIdentifier;
    private String dataPointName;
    private Date dateCaptured;
    private ArrayList<cachedDataResult> dataPointValues;

    public ImpactPortalResponseData(ArrayList<cachedDataResult> clientImpactPortalData, String dataPointName, Date dateCaptured,String tenantIdentifier) {
        this.tenantIdentifier=tenantIdentifier;
        this.dataPointName=dataPointName;
        this.dataPointValues=clientImpactPortalData;
        this.dateCaptured=dateCaptured;
    }






}
