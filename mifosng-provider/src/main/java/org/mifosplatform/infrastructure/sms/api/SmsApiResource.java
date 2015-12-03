/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.infrastructure.sms.api;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.mifosplatform.accounting.journalentry.api.DateParam;
import org.mifosplatform.commands.domain.CommandWrapper;
import org.mifosplatform.commands.service.CommandWrapperBuilder;
import org.mifosplatform.commands.service.PortfolioCommandSourceWritePlatformService;
import org.mifosplatform.infrastructure.core.api.ApiRequestParameterHelper;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.serialization.ApiRequestJsonSerializationSettings;
import org.mifosplatform.infrastructure.core.serialization.DefaultToApiJsonSerializer;
import org.mifosplatform.infrastructure.core.service.Page;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.infrastructure.sms.data.SmsConfigurationData;
import org.mifosplatform.infrastructure.sms.data.SmsData;
import org.mifosplatform.infrastructure.sms.service.SmsConfigurationReadPlatformService;
import org.mifosplatform.infrastructure.sms.service.SmsReadPlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Path("/sms")
@Consumes({ MediaType.APPLICATION_JSON })
@Produces({ MediaType.APPLICATION_JSON })
@Component
@Scope("singleton")
public class SmsApiResource {

    private final String resourceNameForPermissions = "SMS";

    private final PlatformSecurityContext context;
    private final SmsReadPlatformService readPlatformService;
    private final DefaultToApiJsonSerializer<SmsData> toApiJsonSerializer;
    private final ApiRequestParameterHelper apiRequestParameterHelper;
    private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;
    private final SmsConfigurationReadPlatformService smsConfigurationReadPlatformService;

    @Autowired
    public SmsApiResource(final PlatformSecurityContext context, final SmsReadPlatformService readPlatformService,
            final DefaultToApiJsonSerializer<SmsData> toApiJsonSerializer, final ApiRequestParameterHelper apiRequestParameterHelper,
            final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService,
            SmsConfigurationReadPlatformService smsConfigurationReadPlatformService) {
        this.context = context;
        this.readPlatformService = readPlatformService;
        this.toApiJsonSerializer = toApiJsonSerializer;
        this.apiRequestParameterHelper = apiRequestParameterHelper;
        this.commandsSourceWritePlatformService = commandsSourceWritePlatformService;
        this.smsConfigurationReadPlatformService = smsConfigurationReadPlatformService;
    }

    @GET
    public String retrieveAll(@Context final UriInfo uriInfo) {

        this.context.authenticatedUser().validateHasReadPermission(this.resourceNameForPermissions);

        final Collection<SmsData> smsMessages = this.readPlatformService.retrieveAll();

        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.toApiJsonSerializer.serialize(settings, smsMessages);
    }

    @GET
    @Path("pendingSms")
    public String retrievePendingSms(@Context final UriInfo uriInfo,@QueryParam("limit") final Long limit) {

        this.context.authenticatedUser().validateHasReadPermission(this.resourceNameForPermissions);

        final Collection<SmsData> smsMessages = this.readPlatformService.retrieveAllPending(limit.intValue());

        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.toApiJsonSerializer.serialize(settings, smsMessages);
    }
    
    @GET
    @Path("sentSms")
    public String retrieveSentSms(@Context final UriInfo uriInfo, @QueryParam("limit") final Long limit) {

        this.context.authenticatedUser().validateHasReadPermission(this.resourceNameForPermissions);

        final Collection<SmsData> smsMessages = this.readPlatformService.retrieveAllSent(limit.intValue());

        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.toApiJsonSerializer.serialize(settings, smsMessages);
    }
    
    @GET
    @Path("deliveredSms")
    public String retrieveDeliveredSms(@Context final UriInfo uriInfo, @QueryParam("limit") final Long limit) {

        this.context.authenticatedUser().validateHasReadPermission(this.resourceNameForPermissions);

        final Collection<SmsData> smsMessages = this.readPlatformService.retrieveAllDelivered(limit.intValue());

        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.toApiJsonSerializer.serialize(settings, smsMessages);
    }

    @GET
    @Path("messageByStatus")
    public String retrieveAllSmsByStatus(@Context final UriInfo uriInfo, @QueryParam("limit") final Long limit,@QueryParam("status") final Long status,
                                         @QueryParam("fromDate") final DateParam fromDateParam, @QueryParam("toDate") final DateParam toDateParam,
                                         @QueryParam("locale") final String locale, @QueryParam("dateFormat") final String dateFormat) {

        this.context.authenticatedUser().validateHasReadPermission(this.resourceNameForPermissions);
        Date fromDate = null;
        if (fromDateParam != null) {
            fromDate = fromDateParam.getDate("fromDate", dateFormat, locale);
        }
        Date toDate = null;
        if (toDateParam != null) {
            toDate = toDateParam.getDate("toDate", dateFormat, locale);
        }

        final Page<SmsData> smsMessages = this.readPlatformService.retrieveSmsByStatus(limit.intValue(),status.intValue(),fromDate,toDate);

        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.toApiJsonSerializer.serialize(settings, smsMessages);
    }
    
    @GET
    @Path("failedsms")
    public String retrieveFailedSms(@Context final UriInfo uriInfo, @QueryParam("limit") final Long limit) {

        this.context.authenticatedUser().validateHasReadPermission(this.resourceNameForPermissions);

        final Collection<SmsData> smsMessages = this.readPlatformService.retrieveAllFailed(limit.intValue());

        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.toApiJsonSerializer.serialize(settings, smsMessages);
    }
    
    @GET
    @Path("smscredits")
    public String retrieveSmsCredits() {
    	this.context.authenticatedUser().validateHasReadPermission(this.resourceNameForPermissions);
    	
    	SmsConfigurationData smsConfigurationData = this.smsConfigurationReadPlatformService.retrieveOne("SMS_CREDITS");
    	
    	Map<String, String> smsCreditsMap = new HashMap<String, String>();
    	smsCreditsMap.put("smsCredits", smsConfigurationData.getValue());
    	
    	return this.toApiJsonSerializer.serialize(smsCreditsMap);
    }
    
    @POST
    public String create(final String apiRequestBodyAsJson) {

        final CommandWrapper commandRequest = new CommandWrapperBuilder().createSms().withJson(apiRequestBodyAsJson).build();

        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);

        return this.toApiJsonSerializer.serialize(result);
    }

    @GET
    @Path("{resourceId}")
    public String retrieveOne(@PathParam("resourceId") final Long resourceId, @Context final UriInfo uriInfo) {

        final SmsData smsMessage = this.readPlatformService.retrieveOne(resourceId);

        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.toApiJsonSerializer.serialize(settings, smsMessage);
    }

    @PUT
    @Path("{resourceId}")
    public String update(@PathParam("resourceId") final Long resourceId, final String apiRequestBodyAsJson) {

        final CommandWrapper commandRequest = new CommandWrapperBuilder().updateSms(resourceId).withJson(apiRequestBodyAsJson).build();

        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);

        return this.toApiJsonSerializer.serialize(result);
    }

    @DELETE
    @Path("{resourceId}")
    public String delete(@PathParam("resourceId") final Long resourceId) {

        final CommandWrapper commandRequest = new CommandWrapperBuilder().deleteSms(resourceId).build();

        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);

        return this.toApiJsonSerializer.serialize(result);
    }
}