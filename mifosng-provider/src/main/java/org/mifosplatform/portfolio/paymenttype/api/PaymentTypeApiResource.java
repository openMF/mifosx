/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.paymenttype.api;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
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
import org.mifosplatform.portfolio.paymenttype.data.PaymentTypeData;
import org.mifosplatform.portfolio.paymenttype.domain.PaymentTypeRepositoryWrapper;
import org.mifosplatform.portfolio.paymenttype.service.PaymentTypeReadPlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Path("/paymenttypes")
@Component
public class PaymentTypeApiResource {

    private final PlatformSecurityContext securityContext;
    private final DefaultToApiJsonSerializer<PaymentTypeData> jsonSerializer;
    private final PaymentTypeReadPlatformService readPlatformService;
    private final PortfolioCommandSourceWritePlatformService commandWritePlatformService;
    private final ApiRequestParameterHelper apiRequestParameterHelper;
    // private final String resourceNameForPermissions = "PAYMENT_TYPE";
    private final PaymentTypeRepositoryWrapper paymentTypeRepositoryWrapper;

    // private final Set<String> RESPONSE_DATA_PARAMETERS = new
    // HashSet<>(Arrays.asList("id", "value", "description", "isCashPayment"));

    @Autowired
    public PaymentTypeApiResource(PlatformSecurityContext securityContext, DefaultToApiJsonSerializer<PaymentTypeData> jsonSerializer,
            PaymentTypeReadPlatformService readPlatformService, PaymentTypeRepositoryWrapper paymentTypeRepositoryWrapper,
            ApiRequestParameterHelper apiRequestParameterHelper, PortfolioCommandSourceWritePlatformService commandWritePlatformService) {
        super();
        this.securityContext = securityContext;
        this.jsonSerializer = jsonSerializer;
        this.readPlatformService = readPlatformService;
        this.paymentTypeRepositoryWrapper = paymentTypeRepositoryWrapper;
        this.apiRequestParameterHelper = apiRequestParameterHelper;
        this.commandWritePlatformService = commandWritePlatformService;
    }

    @GET
    @Consumes({ MediaType.TEXT_HTML, MediaType.APPLICATION_JSON })
    @Produces(MediaType.APPLICATION_JSON)
    public String getAllPaymentTypes(@Context final UriInfo uriInfo) {
        this.securityContext.authenticatedUser().validateHasReadPermission(PaymentTypeApiResourceConstants.resourceNameForPermissions);
        final Collection<PaymentTypeData> paymentTypes = this.readPlatformService.retrieveAllPaymentTypes();
        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.jsonSerializer.serialize(settings, paymentTypes, PaymentTypeApiResourceConstants.RESPONSE_DATA_PARAMETERS);
    }

    @GET
    @Path("{paymentTypeId}")
    @Consumes({ MediaType.TEXT_HTML, MediaType.APPLICATION_JSON })
    @Produces(MediaType.APPLICATION_JSON)
    public String retrieveOnePaymentType(@PathParam("paymentTypeId") final Long paymentTypeId, @Context final UriInfo uriInfo) {
        this.securityContext.authenticatedUser().validateHasReadPermission(PaymentTypeApiResourceConstants.resourceNameForPermissions);
        this.paymentTypeRepositoryWrapper.findOneWithNotFoundDetection(paymentTypeId);
        final PaymentTypeData paymentTypes = this.readPlatformService.retrieveOne(paymentTypeId);
        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.jsonSerializer.serialize(settings, paymentTypes, PaymentTypeApiResourceConstants.RESPONSE_DATA_PARAMETERS);
    }

    @POST
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String createPaymentType(final String apiRequestBodyAsJson) {

        final CommandWrapper commandRequest = new CommandWrapperBuilder().createPaymentType().withJson(apiRequestBodyAsJson).build();

        final CommandProcessingResult result = this.commandWritePlatformService.logCommandSource(commandRequest);

        return this.jsonSerializer.serialize(result);
    }

    @PUT
    @Path("{paymentTypeId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String updatePaymentType(@PathParam("paymentTypeId") final Long paymentTypeId, final String apiRequestBodyAsJson) {

        final CommandWrapper commandRequest = new CommandWrapperBuilder().updatePaymentType(paymentTypeId).withJson(apiRequestBodyAsJson)
                .build();

        final CommandProcessingResult result = this.commandWritePlatformService.logCommandSource(commandRequest);

        return this.jsonSerializer.serialize(result);
    }

    @DELETE
    @Path("{paymentTypeId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String deleteCode(@PathParam("paymentTypeId") final Long paymentTypeId) {

        final CommandWrapper commandRequest = new CommandWrapperBuilder().deletePaymentType(paymentTypeId).build();

        final CommandProcessingResult result = this.commandWritePlatformService.logCommandSource(commandRequest);

        return this.jsonSerializer.serialize(result);
    }

}
