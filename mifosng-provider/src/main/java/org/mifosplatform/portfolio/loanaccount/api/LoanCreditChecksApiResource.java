/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.loanaccount.api;

import java.util.Collection;

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
import org.mifosplatform.commands.domain.CommandWrapper;
import org.mifosplatform.commands.service.CommandWrapperBuilder;
import org.mifosplatform.commands.service.PortfolioCommandSourceWritePlatformService;
import org.mifosplatform.infrastructure.core.api.ApiRequestParameterHelper;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.exception.UnrecognizedQueryParamException;
import org.mifosplatform.infrastructure.core.serialization.ApiRequestJsonSerializationSettings;
import org.mifosplatform.infrastructure.core.serialization.DefaultToApiJsonSerializer;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.portfolio.loanaccount.data.LoanCreditCheckData;
import org.mifosplatform.portfolio.loanaccount.service.LoanCreditCheckReadPlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Path("/loans/{loanId}/creditchecks")
@Component
@Scope("singleton")
public class LoanCreditChecksApiResource {
    private final PlatformSecurityContext platformSecurityContext;
    private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;
    private final DefaultToApiJsonSerializer<LoanCreditCheckData> loanCreditCheckToApiJsonSerializer;
    private final ApiRequestParameterHelper apiRequestParameterHelper;
    private final LoanCreditCheckReadPlatformService loanCreditCheckReadPlatformService;
    
    @Autowired
    public LoanCreditChecksApiResource(final PlatformSecurityContext platformSecurityContext, 
            final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService, 
            final DefaultToApiJsonSerializer<LoanCreditCheckData> loanCreditCheckToApiJsonSerializer, 
            final ApiRequestParameterHelper apiRequestParameterHelper, 
            final LoanCreditCheckReadPlatformService loanCreditCheckReadPlatformService) {
        this.platformSecurityContext = platformSecurityContext;
        this.commandsSourceWritePlatformService = commandsSourceWritePlatformService;
        this.loanCreditCheckToApiJsonSerializer = loanCreditCheckToApiJsonSerializer;
        this.apiRequestParameterHelper = apiRequestParameterHelper;
        this.loanCreditCheckReadPlatformService = loanCreditCheckReadPlatformService;
    }
    
    @GET
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveAllLoanCreditChecks(@PathParam("loanId") final Long loanId, @Context final UriInfo uriInfo) {
        this.platformSecurityContext.authenticatedUser().validateHasReadPermission(LoanApiConstants.LOAN_CREDIT_CHECK_ENTITY_NAME);
        
        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        final Collection<LoanCreditCheckData> loanCreditCheckData = this.loanCreditCheckReadPlatformService.retrieveLoanCreditChecks(loanId);
        
        return this.loanCreditCheckToApiJsonSerializer.serialize(settings, loanCreditCheckData, LoanApiConstants.LOAN_CREDIT_CHECK_RESPONSE_DATA);
    }
    
    @GET
    @Path("template")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveLoanCreditCheckTemplate(@PathParam("loanId") final Long loanId, @Context final UriInfo uriInfo) {
        this.platformSecurityContext.authenticatedUser().validateHasReadPermission(LoanApiConstants.LOAN_CREDIT_CHECK_ENTITY_NAME);
        
        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        final LoanCreditCheckData loanCreditCheckData = this.loanCreditCheckReadPlatformService.retrieveLoanCreditCheckEnumOptions();
        
        return this.loanCreditCheckToApiJsonSerializer.serialize(settings, loanCreditCheckData, LoanApiConstants.LOAN_CREDIT_CHECK_RESPONSE_DATA);
    }
    
    @GET
    @Path("{loanCreditCheckId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveLoanCreditCheck(@PathParam("loanId") final Long loanId, @PathParam("loanCreditCheckId") final Long loanCreditCheckId,
            @Context final UriInfo uriInfo) {
        this.platformSecurityContext.authenticatedUser().validateHasReadPermission(LoanApiConstants.LOAN_CREDIT_CHECK_ENTITY_NAME);
        
        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        final LoanCreditCheckData loanCreditCheckData = this.loanCreditCheckReadPlatformService.retrieveLoanCreditCheck(loanId, loanCreditCheckId);
        
        return this.loanCreditCheckToApiJsonSerializer.serialize(settings, loanCreditCheckData, LoanApiConstants.LOAN_CREDIT_CHECK_RESPONSE_DATA);
    }
}
