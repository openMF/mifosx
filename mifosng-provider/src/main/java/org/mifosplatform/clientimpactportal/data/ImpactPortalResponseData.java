package org.mifosplatform.clientimpactportal.data;

import java.util.ArrayList;
import java.util.Date;


public class ImpactPortalResponseData {


    private String dataPointName;
    private Date dateCaptured;
    private ArrayList<cachedDataResult> dataPointValues;

    public ImpactPortalResponseData(ArrayList<cachedDataResult> clientImpactPortalData, String dataPointName, Date dateCaptured) {

        this.dataPointName=dataPointName;
        this.dataPointValues=clientImpactPortalData;
        this.dateCaptured=dateCaptured;
    }






}
