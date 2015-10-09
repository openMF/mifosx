/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.paymenttowhom.api;

import java.util.Collection;

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
import org.mifosplatform.portfolio.paymenttowhom.data.PaymentToWhomData;
import org.mifosplatform.portfolio.paymenttowhom.domain.PaymentToWhomRepositoryWrapper;
import org.mifosplatform.portfolio.paymenttowhom.service.PaymentToWhomReadPaltformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Path("/paymenttowhom")
@Component
public class PaymentToWhomApiResource {

    private final PlatformSecurityContext securityContext;
    private final DefaultToApiJsonSerializer<PaymentToWhomData> jsonSerializer;
    private final PaymentToWhomReadPaltformService readPlatformService;
    private final PortfolioCommandSourceWritePlatformService commandWritePlatformService;
    private final ApiRequestParameterHelper apiRequestParameterHelper;
    // private final String resourceNameForPermissions = "PAYMENT_TYPE";
    private final PaymentToWhomRepositoryWrapper paymentToWhomRepositoryWrapper;

    // private final Set<String> RESPONSE_DATA_PARAMETERS = new
    // HashSet<>(Arrays.asList("id", "value", "description", "isCashPayment"));

    @Autowired
    public PaymentToWhomApiResource(PlatformSecurityContext securityContext, DefaultToApiJsonSerializer<PaymentToWhomData> jsonSerializer,
    		PaymentToWhomReadPaltformService readPlatformService, PaymentToWhomRepositoryWrapper paymentToWhomRepositoryWrapper,
            ApiRequestParameterHelper apiRequestParameterHelper, PortfolioCommandSourceWritePlatformService commandWritePlatformService) {
        super();
        this.securityContext = securityContext;
        this.jsonSerializer = jsonSerializer;
        this.readPlatformService = readPlatformService;
        this.paymentToWhomRepositoryWrapper = paymentToWhomRepositoryWrapper;
        this.apiRequestParameterHelper = apiRequestParameterHelper;
        this.commandWritePlatformService = commandWritePlatformService;
    }

    @GET
    @Consumes({ MediaType.TEXT_HTML, MediaType.APPLICATION_JSON })
    @Produces(MediaType.APPLICATION_JSON)
    public String getAllPaymentToWhom(@Context final UriInfo uriInfo) {
        this.securityContext.authenticatedUser().validateHasReadPermission(PaymentToWhomApiResourceConstants.resourceNameForPermissions);
        final Collection<PaymentToWhomData> paymentToWhom = this.readPlatformService.retrieveAllPaymentToWhom();
        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.jsonSerializer.serialize(settings, paymentToWhom, PaymentToWhomApiResourceConstants.RESPONSE_DATA_PARAMETERS);
    }

    @GET
    @Path("{paymentToWhomId}")
    @Consumes({ MediaType.TEXT_HTML, MediaType.APPLICATION_JSON })
    @Produces(MediaType.APPLICATION_JSON)
    public String retrieveOnePaymentToWhom(@PathParam("paymentToWhomId") final Long paymentToWhomId, @Context final UriInfo uriInfo) {
        this.securityContext.authenticatedUser().validateHasReadPermission(PaymentToWhomApiResourceConstants.resourceNameForPermissions);
        this.paymentToWhomRepositoryWrapper.findOneWithNotFoundDetection(paymentToWhomId);
        final PaymentToWhomData paymentToWhom = this.readPlatformService.retrieveOne(paymentToWhomId);
        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.jsonSerializer.serialize(settings, paymentToWhom, PaymentToWhomApiResourceConstants.RESPONSE_DATA_PARAMETERS);
    }

    @POST
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String createPaymentToWhom(final String apiRequestBodyAsJson) {

        final CommandWrapper commandRequest = new CommandWrapperBuilder().createPaymentToWhom().withJson(apiRequestBodyAsJson).build();

        final CommandProcessingResult result = this.commandWritePlatformService.logCommandSource(commandRequest);

        return this.jsonSerializer.serialize(result);
    }

    @PUT
    @Path("{paymentToWhomId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String updatePaymentType(@PathParam("paymentToWhomId") final Long paymentToWhomId, final String apiRequestBodyAsJson) {

        final CommandWrapper commandRequest = new CommandWrapperBuilder().updatePaymentToWhom(paymentToWhomId).withJson(apiRequestBodyAsJson)
                .build();

        final CommandProcessingResult result = this.commandWritePlatformService.logCommandSource(commandRequest);

        return this.jsonSerializer.serialize(result);
    }

    @DELETE
    @Path("{paymentToWhomId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String deleteCode(@PathParam("paymentToWhomId") final Long paymentToWhomId) {

        final CommandWrapper commandRequest = new CommandWrapperBuilder().deletePaymentToWhom(paymentToWhomId).build();

        final CommandProcessingResult result = this.commandWritePlatformService.logCommandSource(commandRequest);

        return this.jsonSerializer.serialize(result);
    }

}
