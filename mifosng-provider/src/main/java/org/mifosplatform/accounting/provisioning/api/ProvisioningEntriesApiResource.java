/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.accounting.provisioning.api;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.mifosplatform.accounting.journalentry.data.JournalEntryData;
import org.mifosplatform.accounting.journalentry.service.JournalEntryReadPlatformService;
import org.mifosplatform.accounting.producttoaccountmapping.domain.PortfolioProductType;
import org.mifosplatform.accounting.provisioning.data.ProvisioningEntryData;
import org.mifosplatform.accounting.provisioning.service.ProvisioningEntriesReadPlatformService;
import org.mifosplatform.commands.domain.CommandWrapper;
import org.mifosplatform.commands.service.CommandWrapperBuilder;
import org.mifosplatform.commands.service.PortfolioCommandSourceWritePlatformService;
import org.mifosplatform.infrastructure.core.api.ApiRequestParameterHelper;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.exception.UnrecognizedQueryParamException;
import org.mifosplatform.infrastructure.core.serialization.ApiRequestJsonSerializationSettings;
import org.mifosplatform.infrastructure.core.serialization.DefaultToApiJsonSerializer;
import org.mifosplatform.infrastructure.core.service.Page;
import org.mifosplatform.infrastructure.core.service.SearchParameters;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Path("/provisioningentries")
@Component
@Scope("singleton")
public class ProvisioningEntriesApiResource {

    private final Set<String> PROVISIONING_ENTRY_PARAMETERS = new HashSet<>(Arrays.asList("provisioningentry", "entries"));
    
    private final Set<String> ALL_PROVISIONING_ENTRIES = new HashSet<>(Arrays.asList("provisioningentry"));

    private static final Set<String> RESPONSE_DATA_PARAMETERS = new HashSet<>(Arrays.asList("id", "officeId", "officeName",
            "glAccountName", "glAccountId", "glAccountCode", "glAccountType", "transactionDate", "entryType", "amount", "transactionId",
            "manualEntry", "entityType", "entityId", "createdByUserId", "createdDate", "createdByUserName", "comments", "reversed",
            "referenceNumber", "currency", "transactionDetails"));
    
    private final PlatformSecurityContext platformSecurityContext;
    private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;
    private final DefaultToApiJsonSerializer<ProvisioningEntryData> toApiJsonSerializer;
    private final ProvisioningEntriesReadPlatformService provisioningEntriesReadPlatformService ;
    private final ApiRequestParameterHelper apiRequestParameterHelper;
    private final JournalEntryReadPlatformService journalEntryReadPlatformService ;
    private final DefaultToApiJsonSerializer<JournalEntryData> apiJsonSerializerService;
    
    @Autowired
    public ProvisioningEntriesApiResource(final PlatformSecurityContext platformSecurityContext,
            final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService,
            final DefaultToApiJsonSerializer<ProvisioningEntryData> toApiJsonSerializer,
            final ProvisioningEntriesReadPlatformService provisioningEntriesReadPlatformService,
            final ApiRequestParameterHelper apiRequestParameterHelper,
            final JournalEntryReadPlatformService journalEntryReadPlatformService,
            final DefaultToApiJsonSerializer<JournalEntryData> apiJsonSerializerService) {
        this.platformSecurityContext = platformSecurityContext;
        this.commandsSourceWritePlatformService = commandsSourceWritePlatformService;
        this.toApiJsonSerializer = toApiJsonSerializer;
        this.provisioningEntriesReadPlatformService = provisioningEntriesReadPlatformService ;
        this.apiRequestParameterHelper = apiRequestParameterHelper ;
        this.journalEntryReadPlatformService = journalEntryReadPlatformService ;
        this.apiJsonSerializerService = apiJsonSerializerService ;
    }

    @POST
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String createProvisioningEntries(final String apiRequestBodyAsJson) {
        CommandWrapper commandWrapper = null;
        this.platformSecurityContext.authenticatedUser();
        commandWrapper = new CommandWrapperBuilder().createProvisioningEntries().withJson(apiRequestBodyAsJson).build();
        final CommandProcessingResult commandProcessingResult = this.commandsSourceWritePlatformService.logCommandSource(commandWrapper);
        return this.toApiJsonSerializer.serialize(commandProcessingResult);
    }
    
    @POST
    @Path("{entryId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String modifyProvisioningEntry(@PathParam("entryId") final Long entryId, @QueryParam("command") final String commandParam,
            final String apiRequestBodyAsJson) {
        CommandWrapper commandWrapper = null;
        this.platformSecurityContext.authenticatedUser();
        if("createjournalentry".equals(commandParam)) {
            commandWrapper = new CommandWrapperBuilder().createProvisioningJournalEntries(entryId).withJson(apiRequestBodyAsJson).build();
            final CommandProcessingResult commandProcessingResult = this.commandsSourceWritePlatformService.logCommandSource(commandWrapper);
            return this.toApiJsonSerializer.serialize(commandProcessingResult);    
        }else if("recreateprovisioningentry".equals(commandParam)) {
            commandWrapper = new CommandWrapperBuilder().reCreateProvisioningEntries(entryId).withJson(apiRequestBodyAsJson).build();
            final CommandProcessingResult commandProcessingResult = this.commandsSourceWritePlatformService.logCommandSource(commandWrapper);
            return this.toApiJsonSerializer.serialize(commandProcessingResult);    
        }
        throw new UnrecognizedQueryParamException("command", commandParam); 
    }
    
    @GET
    @Path("journalentry")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveJournalEntries(@QueryParam("offset") final Integer offset, @QueryParam("limit") final Integer limit,
            @QueryParam("entryId") final Long entryId, @Context final UriInfo uriInfo) {
        this.platformSecurityContext.authenticatedUser();
        String transactionId = "P"+entryId ;
        SearchParameters params = SearchParameters.forPagination(offset, limit) ;
                Page<JournalEntryData> entries = this.journalEntryReadPlatformService.retrieveAll(params, null, null, null, null, transactionId, PortfolioProductType.PROVISIONING.getValue(), null) ;
        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.apiJsonSerializerService.serialize(settings, entries, RESPONSE_DATA_PARAMETERS);    
    }
    
    
    @GET
    @Path("{entryId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveProvisioningEntry(@PathParam("entryId") final Long entryId, @Context final UriInfo uriInfo) {
        platformSecurityContext.authenticatedUser() ;
        ProvisioningEntryData data = this.provisioningEntriesReadPlatformService.retrieveProvisioningEntryData(entryId) ;
        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.toApiJsonSerializer.serialize(settings, data, this.PROVISIONING_ENTRY_PARAMETERS); 
    }
    
    @GET
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveAllProvisioningEntries(@Context final UriInfo uriInfo) {
        platformSecurityContext.authenticatedUser() ;
        Collection<ProvisioningEntryData> data = this.provisioningEntriesReadPlatformService.retrieveAllProvisioningEntries() ;
        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.toApiJsonSerializer.serialize(settings, data, this.ALL_PROVISIONING_ENTRIES); 
    }
}
