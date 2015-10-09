/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.organisation.dsa.api;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
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

import org.mifosplatform.commands.domain.CommandWrapper;
import org.mifosplatform.commands.service.CommandWrapperBuilder;
import org.mifosplatform.commands.service.PortfolioCommandSourceWritePlatformService;
import org.mifosplatform.infrastructure.core.api.ApiRequestParameterHelper;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.serialization.ApiRequestJsonSerializationSettings;
import org.mifosplatform.infrastructure.core.serialization.DefaultToApiJsonSerializer;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.organisation.office.data.OfficeData;
import org.mifosplatform.organisation.office.service.OfficeReadPlatformService;
import org.mifosplatform.organisation.dsa.data.DsaData;
import org.mifosplatform.organisation.dsa.service.DsaReadPlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Path("/dsa")
@Component
@Scope("singleton")
public class DirectSalesAssociateApiResource {

    /**
     * The set of parameters that are supported in response for
     * {@link StaffData}.
     */
    private final Set<String> RESPONSE_DATA_PARAMETERS = new HashSet<>(Arrays.asList("id", "firstname", "lastname", "displayName",
            "officeId", "officeName","mobileNo", "allowedOffices", "isActive"));

    private final String resourceNameForPermissions = "DSA";

    private final PlatformSecurityContext context;
    private final DsaReadPlatformService readPlatformService;
    private final OfficeReadPlatformService officeReadPlatformService;
    private final DefaultToApiJsonSerializer<DsaData> toApiJsonSerializer;
    private final ApiRequestParameterHelper apiRequestParameterHelper;
    private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;

    @Autowired
    public DirectSalesAssociateApiResource(final PlatformSecurityContext context, final DsaReadPlatformService readPlatformService,
            final OfficeReadPlatformService officeReadPlatformService, final DefaultToApiJsonSerializer<DsaData> toApiJsonSerializer,
            final ApiRequestParameterHelper apiRequestParameterHelper,
            final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService) {
        this.context = context;
        this.readPlatformService = readPlatformService;
        this.officeReadPlatformService = officeReadPlatformService;
        this.toApiJsonSerializer = toApiJsonSerializer;
        this.apiRequestParameterHelper = apiRequestParameterHelper;
        this.commandsSourceWritePlatformService = commandsSourceWritePlatformService;
    }

    @GET
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveStaff(@Context final UriInfo uriInfo, @QueryParam("sqlSearch") final String sqlSearch,
            @QueryParam("officeId") final Long officeId,
            @DefaultValue("false") @QueryParam("dsaInOfficeHierarchy") final boolean dsaInOfficeHierarchy,
            @DefaultValue("false") @QueryParam("dsaOfficersOnly") final boolean dsaOfficersOnly,
            @DefaultValue("active") @QueryParam("status") final String status) {

        this.context.authenticatedUser().validateHasReadPermission(this.resourceNameForPermissions);

        final Collection<DsaData> dsa;
        if (dsaInOfficeHierarchy) {
            dsa = this.readPlatformService.retrieveAllDsaInOfficeAndItsParentOfficeHierarchy(officeId, dsaOfficersOnly);
        } else {
            dsa = this.readPlatformService.retrieveAllDsa(sqlSearch, officeId, dsaOfficersOnly, status);
        }

        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.toApiJsonSerializer.serialize(settings, dsa, this.RESPONSE_DATA_PARAMETERS);
    }

    @POST
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String createStaff(final String apiRequestBodyAsJson) {

        final CommandWrapper commandRequest = new CommandWrapperBuilder().createDsa().withJson(apiRequestBodyAsJson).build();

        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);

        return this.toApiJsonSerializer.serialize(result);
    }

    @GET
    @Path("{dsaId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retreiveStaff(@PathParam("dsaId") final Long dsaId, @Context final UriInfo uriInfo) {

        this.context.authenticatedUser().validateHasReadPermission(this.resourceNameForPermissions);

        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());

        DsaData dsa = this.readPlatformService.retrieveDsa(dsaId);
        if (settings.isTemplate()) {
            final Collection<OfficeData> allowedOffices = this.officeReadPlatformService.retrieveAllOfficesForDropdown();
            dsa = DsaData.templateData(dsa, allowedOffices);
        }
        return this.toApiJsonSerializer.serialize(settings, dsa, this.RESPONSE_DATA_PARAMETERS);
    }

    @PUT
    @Path("{dsaId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String updateStaff(@PathParam("staffId") final Long dsaId, final String apiRequestBodyAsJson) {

        final CommandWrapper commandRequest = new CommandWrapperBuilder().updateDsa(dsaId).withJson(apiRequestBodyAsJson).build();

        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);

        return this.toApiJsonSerializer.serialize(result);
    }
}