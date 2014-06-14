package org.mifosplatform.clientimpactportal.api;

import org.mifosplatform.clientimpactportal.data.ImpactPortalData;
import org.mifosplatform.clientimpactportal.data.ImpactPortalResponseData;
import org.mifosplatform.clientimpactportal.service.ImpactPortalReadService;
import org.mifosplatform.commands.service.PortfolioCommandSourceWritePlatformService;
import org.mifosplatform.infrastructure.core.api.ApiRequestParameterHelper;
import org.mifosplatform.infrastructure.core.serialization.ApiRequestJsonSerializationSettings;
import org.mifosplatform.infrastructure.core.serialization.ToApiJsonSerializer;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;


@Path("/client_impact_portal")
@Component
@Scope("singleton")
public class ClientImpactPortalApiResource {

    private final static Logger logger = LoggerFactory.getLogger(ClientImpactPortalApiResource.class);

    private final ImpactPortalReadService impactPortalReadService;
    private final ApiRequestParameterHelper apiRequestParameterHelper;
    private final ToApiJsonSerializer<ImpactPortalResponseData> toApiJsonSerializer;
    private final PlatformSecurityContext context;


    @Autowired
    public ClientImpactPortalApiResource(final ImpactPortalReadService impactPortalReadService, final ToApiJsonSerializer<ImpactPortalResponseData> toApiJsonSerializer,
                                         final ApiRequestParameterHelper apiRequestParameterHelper,
                                         final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService,
                                         final PlatformSecurityContext context) {
        this.impactPortalReadService = impactPortalReadService;
        this.toApiJsonSerializer = toApiJsonSerializer;
        this.apiRequestParameterHelper = apiRequestParameterHelper;
        this.context = context;

    }


    @GET
    @Path("latestReport")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveLatestReport(@Context final UriInfo uriInfo, @QueryParam("reportName") final String reportName) {


        final ImpactPortalData impactPortalData= this.impactPortalReadService.getDataByNameForYesterday(reportName);
        final ImpactPortalResponseData impactPortalResponseData= new ImpactPortalResponseData(impactPortalData.getResultValues(),impactPortalData.getDataPointLabel(),impactPortalData.getDateCaptured());//final ImpactPortalResponseData impactPortalResponseData= new ImpactPortalResponseData(impactPortalData.getValues(),impactPortalData.getDataPointLabel());
        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.toApiJsonSerializer.serialize(settings, impactPortalResponseData);

    }

    @GET
    @Path("reportByDate")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveReportsByDate(@Context final UriInfo uriInfo, @QueryParam("reportName") final String reportName,
                                        @QueryParam("reportDate") final String reportDate) {

        final ImpactPortalData impactPortalData= this.impactPortalReadService.getDataByDate(reportName, reportDate);
        final ImpactPortalResponseData impactPortalResponseData= new ImpactPortalResponseData(impactPortalData.getResultValues(),impactPortalData.getDataPointLabel(),impactPortalData.getDateCaptured());
        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.toApiJsonSerializer.serialize(settings, impactPortalResponseData);
    }

    @GET
    @Path("reportByDateRange")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveReportsByDateRange(@Context final UriInfo uriInfo, @QueryParam("reportName") final String reportName,@QueryParam("reportStartDate") final String reportStartDate,
                                        @QueryParam("reportEndDate") final String reportEndDate) {
        final Collection<ImpactPortalData> impactPortalData= this.impactPortalReadService.getDataByDateRange(reportName, reportStartDate,reportEndDate);
        Collection<ImpactPortalResponseData> impactPortalResponseDataCollection=new ArrayList<ImpactPortalResponseData>();
            for(ImpactPortalData i: impactPortalData){
                  impactPortalResponseDataCollection.add(new ImpactPortalResponseData(i.getResultValues(),i.getDataPointLabel(),i.getDateCaptured()));
            }
        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.toApiJsonSerializer.serialize(settings, impactPortalResponseDataCollection);
    }





}
