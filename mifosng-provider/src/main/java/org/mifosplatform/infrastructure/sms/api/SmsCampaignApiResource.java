/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.infrastructure.sms.api;

import com.google.gson.JsonElement;
import org.apache.commons.lang.StringUtils;
import org.mifosplatform.commands.domain.CommandWrapper;
import org.mifosplatform.commands.service.CommandWrapperBuilder;
import org.mifosplatform.commands.service.PortfolioCommandSourceWritePlatformService;
import org.mifosplatform.infrastructure.core.api.ApiRequestParameterHelper;
import org.mifosplatform.infrastructure.core.api.JsonQuery;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.serialization.ApiRequestJsonSerializationSettings;
import org.mifosplatform.infrastructure.core.serialization.DefaultToApiJsonSerializer;
import org.mifosplatform.infrastructure.core.serialization.FromJsonHelper;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.infrastructure.sms.data.PreviewCampaignMessage;
import org.mifosplatform.infrastructure.sms.data.SmsBusinessRulesData;
import org.mifosplatform.infrastructure.sms.data.SmsCampaignData;
import org.mifosplatform.infrastructure.sms.service.SmsCampaignReadPlatformService;
import org.mifosplatform.infrastructure.sms.service.SmsCampaignWritePlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;
import java.util.Collection;
import java.util.HashSet;

/**
 * Created with IntelliJ IDEA.
 * User: andrew
 * Date: 19-5-14
 * Time: 15:17
 * To change this template use File | Settings | File Templates.
 */
@Path("/sms/campaign")
@Consumes({ MediaType.APPLICATION_JSON })
@Produces({ MediaType.APPLICATION_JSON })
@Component
@Scope("singleton")
public class SmsCampaignApiResource {


    //change name to sms campaign
    private final String resourceNameForPermissions = "SMS_CAMPAIGN";

    private final PlatformSecurityContext context;

    private final DefaultToApiJsonSerializer<SmsBusinessRulesData> toApiJsonSerializer;

    private final ApiRequestParameterHelper apiRequestParameterHelper;

    private final SmsCampaignReadPlatformService smsCampaignReadPlatformService;
    private final FromJsonHelper fromJsonHelper;


    private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;
    private final DefaultToApiJsonSerializer<SmsCampaignData> smsCampaignDataDefaultToApiJsonSerializer;
    private final SmsCampaignWritePlatformService smsCampaignWritePlatformService;

    private final DefaultToApiJsonSerializer<PreviewCampaignMessage> previewCampaignMessageDefaultToApiJsonSerializer;


    @Autowired
    public SmsCampaignApiResource(final PlatformSecurityContext context,final DefaultToApiJsonSerializer<SmsBusinessRulesData> toApiJsonSerializer, final ApiRequestParameterHelper apiRequestParameterHelper,
                                  final SmsCampaignReadPlatformService smsCampaignReadPlatformService, final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService,
                                  final DefaultToApiJsonSerializer<SmsCampaignData> smsCampaignDataDefaultToApiJsonSerializer,
                                  final FromJsonHelper fromJsonHelper, final SmsCampaignWritePlatformService smsCampaignWritePlatformService,
                                  final DefaultToApiJsonSerializer<PreviewCampaignMessage> previewCampaignMessageDefaultToApiJsonSerializer) {
        this.context = context;
        this.toApiJsonSerializer = toApiJsonSerializer;
        this.apiRequestParameterHelper = apiRequestParameterHelper;
        this.smsCampaignReadPlatformService = smsCampaignReadPlatformService;
        this.commandsSourceWritePlatformService = commandsSourceWritePlatformService;
        this.smsCampaignDataDefaultToApiJsonSerializer = smsCampaignDataDefaultToApiJsonSerializer;
        this.fromJsonHelper = fromJsonHelper;
        this.smsCampaignWritePlatformService = smsCampaignWritePlatformService;
        this.previewCampaignMessageDefaultToApiJsonSerializer = previewCampaignMessageDefaultToApiJsonSerializer;
    }


    @GET
    @Path("{resourceId}")
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveOneCampaign(@PathParam("resourceId") final Long resourceId,@Context final UriInfo uriInfo){
        this.context.authenticatedUser().validateHasReadPermission(this.resourceNameForPermissions);

        SmsCampaignData smsCampaignData = this.smsCampaignReadPlatformService.retrieveOne(resourceId);
        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.smsCampaignDataDefaultToApiJsonSerializer.serialize(settings,smsCampaignData);

    }

    @GET
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveAllCampaign(@Context final UriInfo uriInfo) {

        this.context.authenticatedUser().validateHasReadPermission(this.resourceNameForPermissions);
        Collection<SmsCampaignData> smsCampaignDataCollection = this.smsCampaignReadPlatformService.retrieveAllCampaign();

        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.smsCampaignDataDefaultToApiJsonSerializer.serialize(settings,smsCampaignDataCollection);
    }



    @POST
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String createCampaign(final String apiRequestBodyAsJson,@Context final UriInfo uriInfo){

        final CommandWrapper commandRequest = new CommandWrapperBuilder().createSmsCampaign().withJson(apiRequestBodyAsJson).build();

        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);

        return this.toApiJsonSerializer.serialize(result);
    }

    @PUT
    @Path("{resourceId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String updateCampaign(@PathParam("resourceId") final Long campaignId,final String apiRequestBodyAsJson,@Context final UriInfo uriInfo){

        final CommandWrapper commandRequest = new CommandWrapperBuilder().updateSmsCampaign(campaignId).withJson(apiRequestBodyAsJson).build();

        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);

        return this.toApiJsonSerializer.serialize(result);
    }

    @POST
    @Path("{resourceId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String activate(@PathParam("resourceId") final Long campaignId, @QueryParam("command") final String commandParam,
                           final String apiRequestBodyAsJson){
        final CommandWrapperBuilder builder = new CommandWrapperBuilder().withJson(apiRequestBodyAsJson);

        CommandProcessingResult result = null;
        CommandWrapper commandRequest = null;
        if (is(commandParam, "activate")) {
            commandRequest = builder.activateSmsCampaign(campaignId).build();
            result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
        }else if (is(commandParam, "close")){
            commandRequest = builder.closeSmsCampaign(campaignId).build();
            result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
        }else if (is(commandParam,"reactivate")){
            commandRequest = builder.reactivateSmsCampaign(campaignId).build();
            result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
        }
        return this.toApiJsonSerializer.serialize(result);
    }

    @POST
    @Path("preview")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String preview(final String apiRequestBodyAsJson,@Context final UriInfo uriInfo){
        this.context.authenticatedUser().validateHasReadPermission(this.resourceNameForPermissions);

        PreviewCampaignMessage campaignMessage = null;
        final JsonElement parsedQuery = this.fromJsonHelper.parse(apiRequestBodyAsJson);
        final JsonQuery query = JsonQuery.from(apiRequestBodyAsJson, parsedQuery, this.fromJsonHelper);
        campaignMessage = this.smsCampaignWritePlatformService.previewMessage(query);
        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.previewCampaignMessageDefaultToApiJsonSerializer.serialize(settings,campaignMessage, new HashSet<String>());

    }


    @GET()
    @Path("template")
    public String retrieveAll(@Context final UriInfo uriInfo){
        this.context.authenticatedUser().validateHasReadPermission(this.resourceNameForPermissions);

        final Collection<SmsBusinessRulesData>  smsBusinessRulesDataCollection = this.smsCampaignReadPlatformService.retrieveAll();

        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.toApiJsonSerializer.serialize(settings,smsBusinessRulesDataCollection);
    }

    @GET
    @Path("template/{resourceId}")
    public String retrieveOneTemplate(@PathParam("resourceId") final Long resourceId,@Context final UriInfo uriInfo){
        this.context.authenticatedUser().validateHasReadPermission(this.resourceNameForPermissions);

        final SmsBusinessRulesData smsBusinessRulesData = this.smsCampaignReadPlatformService.retrieveOneTemplate(resourceId);
        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.toApiJsonSerializer.serialize(settings,smsBusinessRulesData);

    }

    @DELETE
    @Path("{resourceId}")
    public String delete(@PathParam("resourceId") final Long resourceId) {

        final CommandWrapper commandRequest = new CommandWrapperBuilder().deleteSmsCampaign(resourceId).build();

        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);

        return this.toApiJsonSerializer.serialize(result);
    }

    private boolean is(final String commandParam, final String commandValue) {
        return StringUtils.isNotBlank(commandParam) && commandParam.trim().equalsIgnoreCase(commandValue);
    }

}
