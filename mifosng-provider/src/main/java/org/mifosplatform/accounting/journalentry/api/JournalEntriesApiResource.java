/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.accounting.journalentry.api;

import java.util.Arrays;
import java.util.Date;
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

import org.apache.commons.lang.StringUtils;
import org.mifosplatform.accounting.journalentry.data.JournalEntryData;
import org.mifosplatform.accounting.journalentry.service.JournalEntryReadPlatformService;
import org.mifosplatform.commands.domain.CommandWrapper;
import org.mifosplatform.commands.service.CommandWrapperBuilder;
import org.mifosplatform.commands.service.PortfolioCommandSourceWritePlatformService;
import org.mifosplatform.infrastructure.core.api.ApiRequestParameterHelper;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.exception.UnrecognizedQueryParamException;
import org.mifosplatform.infrastructure.core.serialization.ApiRequestJsonSerializationSettings;
import org.mifosplatform.infrastructure.core.serialization.DefaultToApiJsonSerializer;
import org.mifosplatform.infrastructure.core.service.Page;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.portfolio.group.service.SearchParameters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Path("/journalentries")
@Component
@Scope("singleton")
public class JournalEntriesApiResource {

    private static final Set<String> RESPONSE_DATA_PARAMETERS = new HashSet<String>(Arrays.asList("id", "officeId", "officeName",
            "glAccountName", "glAccountId", "glAccountCode", "glAccountType", "transactionDate", "entryType", "amount", "transactionId",
            "manualEntry", "entityType", "entityId", "createdByUserId", "createdDate", "createdByUserName", "comments", "reversed",
            "referenceNumber"));

    private final String resourceNameForPermission = "JOURNALENTRY";

    private final JournalEntryReadPlatformService journalEntryReadPlatformService;
    private final DefaultToApiJsonSerializer<JournalEntryData> apiJsonSerializerService;
    private final ApiRequestParameterHelper apiRequestParameterHelper;
    private final PlatformSecurityContext context;
    private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;

    @Autowired
    public JournalEntriesApiResource(final PlatformSecurityContext context,
            final JournalEntryReadPlatformService journalEntryReadPlatformService,
            final DefaultToApiJsonSerializer<JournalEntryData> toApiJsonSerializer,
            final ApiRequestParameterHelper apiRequestParameterHelper,
            final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService) {
        this.context = context;
        this.apiRequestParameterHelper = apiRequestParameterHelper;
        this.commandsSourceWritePlatformService = commandsSourceWritePlatformService;
        this.apiJsonSerializerService = toApiJsonSerializer;
        this.journalEntryReadPlatformService = journalEntryReadPlatformService;
    }

    @GET
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveAll(@Context final UriInfo uriInfo, @QueryParam("officeId") final Long officeId,
            @QueryParam("glAccountId") final Long glAccountId, @QueryParam("manualEntriesOnly") final Boolean onlyManualEntries,
            @QueryParam("fromDate") final DateParam fromDateParam, @QueryParam("toDate") final DateParam toDateParam,
            @QueryParam("transactionId") final String transactionId, @QueryParam("offset") final Integer offset,
            @QueryParam("limit") final Integer limit, @QueryParam("orderBy") final String orderBy,
            @QueryParam("sortOrder") final String sortOrder) {

        this.context.authenticatedUser().validateHasReadPermission(this.resourceNameForPermission);

        Date fromDate = null;
        if (fromDateParam != null) {
            fromDate = fromDateParam.getDate();
        }
        Date toDate = null;
        if (toDateParam != null) {
            toDate = toDateParam.getDate();
        }

        final SearchParameters searchParameters = SearchParameters.forJournalEntries(officeId, offset, limit, orderBy, sortOrder);

        Page<JournalEntryData> glJournalEntries = this.journalEntryReadPlatformService.retrieveAll(searchParameters, glAccountId,
                onlyManualEntries, fromDate, toDate, transactionId);
        final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.apiJsonSerializerService.serialize(settings, glJournalEntries, RESPONSE_DATA_PARAMETERS);
    }

    @GET
    @Path("{journalEntryId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retreiveJournalEntryById(@PathParam("journalEntryId") final Long journalEntryId, @Context final UriInfo uriInfo) {

        this.context.authenticatedUser().validateHasReadPermission(this.resourceNameForPermission);
        final JournalEntryData glJournalEntryData = this.journalEntryReadPlatformService.retrieveGLJournalEntryById(journalEntryId);

        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.apiJsonSerializerService.serialize(settings, glJournalEntryData, RESPONSE_DATA_PARAMETERS);
    }

    @POST
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String createGLJournalEntry(final String jsonRequestBody) {

        final CommandWrapper commandRequest = new CommandWrapperBuilder().createJournalEntry().withJson(jsonRequestBody).build();

        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);

        return this.apiJsonSerializerService.serialize(result);
    }

    @POST
    @Path("{transactionId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String createReversalJournalEntry(@PathParam("transactionId") final String transactionId,
            @QueryParam("command") final String commandParam) {
        CommandProcessingResult result = null;
        if (is(commandParam, "reverse")) {
            final CommandWrapper commandRequest = new CommandWrapperBuilder().reverseJournalEntry(transactionId).build();
            result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
        } else {
            throw new UnrecognizedQueryParamException("command", commandParam);
        }

        return this.apiJsonSerializerService.serialize(result);
    }

    private boolean is(final String commandParam, final String commandValue) {
        return StringUtils.isNotBlank(commandParam) && commandParam.trim().equalsIgnoreCase(commandValue);
    }
}