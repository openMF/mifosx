/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.savingsaccountproduct.api;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
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

import org.mifosplatform.infrastructure.core.api.ApiParameterHelper;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.data.EnumOptionData;
import org.mifosplatform.infrastructure.core.serialization.DefaultToApiJsonSerializer;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.organisation.monetary.data.CurrencyData;
import org.mifosplatform.organisation.monetary.service.CurrencyReadPlatformService;
import org.mifosplatform.portfolio.savingsaccount.PortfolioApiDataConversionService;
import org.mifosplatform.portfolio.savingsaccount.PortfolioApiJsonSerializerService;
import org.mifosplatform.portfolio.savingsaccountproduct.command.SavingProductCommand;
import org.mifosplatform.portfolio.savingsaccountproduct.data.SavingProductData;
import org.mifosplatform.portfolio.savingsaccountproduct.domain.SavingFrequencyType;
import org.mifosplatform.portfolio.savingsaccountproduct.domain.SavingInterestCalculationMethod;
import org.mifosplatform.portfolio.savingsaccountproduct.domain.SavingProductType;
import org.mifosplatform.portfolio.savingsaccountproduct.domain.SavingsInterestType;
import org.mifosplatform.portfolio.savingsaccountproduct.domain.SavingsLockinPeriodEnum;
import org.mifosplatform.portfolio.savingsaccountproduct.service.SavingProductEnumerations;
import org.mifosplatform.portfolio.savingsaccountproduct.service.SavingProductReadPlatformService;
import org.mifosplatform.portfolio.savingsaccountproduct.service.SavingProductWritePlatformService;
import org.mifosplatform.portfolio.savingsdepositproduct.domain.TenureTypeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Path("/savingproducts")
@Component
@Scope("singleton")
public class SavingProductsApiResource {

    private final SavingProductReadPlatformService savingProductReadPlatformService;
    private final SavingProductWritePlatformService savingProductWritePlatformService;
    private final PortfolioApiDataConversionService apiDataConversionService;
    private final PortfolioApiJsonSerializerService apiJsonSerializerService;
    private final CurrencyReadPlatformService currencyReadPlatformService;
    private final String entityType = "SAVINGSPRODUCT";
    private final PlatformSecurityContext context;
    private final DefaultToApiJsonSerializer<SavingProductData> toApiJsonSerializer;
    
    @Autowired
    public SavingProductsApiResource(final SavingProductReadPlatformService savingProductReadPlatformService,
    		final SavingProductWritePlatformService savingProductWritePlatformService,
    		final PortfolioApiDataConversionService apiDataConversionService,
    		final PortfolioApiJsonSerializerService apiJsonSerializerService,
    		final CurrencyReadPlatformService currencyReadPlatformService,
    		final PlatformSecurityContext context,
    		final DefaultToApiJsonSerializer<SavingProductData> toApiJsonSerializer) {
    	this.savingProductReadPlatformService = savingProductReadPlatformService;
    	this.savingProductWritePlatformService = savingProductWritePlatformService;
    	this.apiDataConversionService = apiDataConversionService;
    	this.apiJsonSerializerService = apiJsonSerializerService;
    	this.currencyReadPlatformService = currencyReadPlatformService;
    	this.context = context;
    	this.toApiJsonSerializer = toApiJsonSerializer;
	}

    @POST
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String createSavingProduct(final String jsonRequestBody) {

        final SavingProductCommand command = this.apiDataConversionService.convertJsonToSavingProductCommand(null, jsonRequestBody);

        CommandProcessingResult entityIdentifier = this.savingProductWritePlatformService.createSavingProduct(command);

        return this.toApiJsonSerializer.serialize(entityIdentifier);
    }

    @PUT
    @Path("{productId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String updateSavingProduct(@PathParam("productId") final Long productId, final String jsonRequestBody) {

        SavingProductCommand command = this.apiDataConversionService.convertJsonToSavingProductCommand(productId, jsonRequestBody);
        CommandProcessingResult entityIdentifier = this.savingProductWritePlatformService.updateSavingProduct(command);
        return this.toApiJsonSerializer.serialize(entityIdentifier);
    }

    @GET
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveAllSavingProducts(@Context final UriInfo uriInfo) {

        context.authenticatedUser().validateHasReadPermission(entityType);

        Set<String> typicalResponseParameters = new HashSet<String>(Arrays.asList("id", "createdOn", "lastModifedOn", "locale", "name",
                "description", "currencyCode", "digitsAfterDecimal", "interstRate", "minInterestRate", "maxInterestRate",
                "savingsDepositAmount", "savingProductType", "tenureType", "tenure", "frequency", "interestType",
                "interestCalculationMethod", "minimumBalanceForWithdrawal", "isPartialDepositAllowed", "isLockinPeriodAllowed",
                "lockinPeriod", "lockinPeriodType", "depositEvery"));

        Set<String> responseParameters = ApiParameterHelper.extractFieldsForResponseIfProvided(uriInfo.getQueryParameters());
        if (responseParameters.isEmpty()) {
            responseParameters.addAll(typicalResponseParameters);
        }
        boolean prettyPrint = ApiParameterHelper.prettyPrint(uriInfo.getQueryParameters());

        Collection<SavingProductData> products = this.savingProductReadPlatformService.retrieveAllSavingProducts();

        return this.apiJsonSerializerService.serializeSavingProductDataToJson(prettyPrint, responseParameters, products);
    }

    @GET
    @Path("{productId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveSavingProductDetails(@PathParam("productId") final Long productId, @Context final UriInfo uriInfo) {

        context.authenticatedUser().validateHasReadPermission(entityType);

        Set<String> typicalResponseParameters = new HashSet<String>(Arrays.asList("id", "createdOn", "lastModifedOn", "locale", "name",
                "description", "currencyCode", "digitsAfterDecimal", "interstRate", "minInterestRate", "maxInterestRate",
                "savingsDepositAmount", "savingProductType", "tenureType", "tenure", "frequency", "interestType",
                "interestCalculationMethod", "minimumBalanceForWithdrawal", "isPartialDepositAllowed", "isLockinPeriodAllowed",
                "lockinPeriod", "lockinPeriodType", "depositEvery"));

        Set<String> responseParameters = ApiParameterHelper.extractFieldsForResponseIfProvided(uriInfo.getQueryParameters());
        if (responseParameters.isEmpty()) {
            responseParameters.addAll(typicalResponseParameters);
        }

        SavingProductData savingProduct = this.savingProductReadPlatformService.retrieveSavingProduct(productId);

        boolean prettyPrint = ApiParameterHelper.prettyPrint(uriInfo.getQueryParameters());
        boolean template = ApiParameterHelper.template(uriInfo.getQueryParameters());
        if (template) {
            savingProduct = handleTemplateRelatedData(responseParameters, savingProduct);
        }

        return this.apiJsonSerializerService.serializeSavingProductDataToJson(prettyPrint, responseParameters, savingProduct);
    }

    @GET
    @Path("template")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveNewSavingProductDetails(@Context final UriInfo uriInfo) {

        context.authenticatedUser().validateHasReadPermission(entityType);

        Set<String> typicalResponseParameters = new HashSet<String>(Arrays.asList("id", "createdOn", "lastModifedOn", "locale", "name",
                "description", "currencyCode", "digitsAfterDecimal", "interstRate", "savingsDepositAmount", "savingProductType",
                "tenureType", "tenure", "frequency", "interestType", "interestCalculationMethod", "minimumBalanceForWithdrawal",
                "isPartialDepositAllowed", "isLockinPeriodAllowed", "lockinPeriod", "lockinPeriodType", "currencyOptions", "depositEvery"));

        Set<String> responseParameters = ApiParameterHelper.extractFieldsForResponseIfProvided(uriInfo.getQueryParameters());
        if (responseParameters.isEmpty()) {
            responseParameters.addAll(typicalResponseParameters);
        }
        boolean prettyPrint = ApiParameterHelper.prettyPrint(uriInfo.getQueryParameters());

        SavingProductData savingProduct = this.savingProductReadPlatformService.retrieveNewSavingProductDetails();
        savingProduct = handleTemplateRelatedData(responseParameters, savingProduct);
        return this.apiJsonSerializerService.serializeSavingProductDataToJson(prettyPrint, responseParameters, savingProduct);
    }

    private SavingProductData handleTemplateRelatedData(Set<String> responseParameters, SavingProductData savingProduct) {

        responseParameters.addAll(Arrays.asList("currencyOptions", "savingsProductTypeOptions", "tenureTypeOptions",
                "savingFrequencyOptions", "savingsInterestTypeOptions", "lockinPeriodTypeOptions", "interestCalculationOptions"));
        Collection<CurrencyData> currencyOptions = this.currencyReadPlatformService.retrieveAllowedCurrencies();

        EnumOptionData reccuring = SavingProductEnumerations.savingProductType(SavingProductType.RECURRING);
        EnumOptionData regular = SavingProductEnumerations.savingProductType(SavingProductType.REGULAR);
        List<EnumOptionData> savingsProductTypeOptions = Arrays.asList(reccuring, regular);

        EnumOptionData fixed = SavingProductEnumerations.tenureTypeEnum(TenureTypeEnum.FIXED_PERIOD);
        EnumOptionData perpetual = SavingProductEnumerations.tenureTypeEnum(TenureTypeEnum.PERPETUAL);
        List<EnumOptionData> tenureTypeOptions = Arrays.asList(fixed, perpetual);

        EnumOptionData monthly = SavingProductEnumerations.interestFrequencyType(SavingFrequencyType.MONTHLY);
        List<EnumOptionData> savingFrequencyOptions = Arrays.asList(monthly);

        EnumOptionData simple = SavingProductEnumerations.savingInterestType(SavingsInterestType.SIMPLE);
        EnumOptionData compounding = SavingProductEnumerations.savingInterestType(SavingsInterestType.COMPOUNDING);
        List<EnumOptionData> savingsInterestTypeOptions = Arrays.asList(simple, compounding);

        EnumOptionData months = SavingProductEnumerations.savingsLockinPeriod(SavingsLockinPeriodEnum.MONTHS);
        List<EnumOptionData> lockinPeriodTypeOptions = Arrays.asList(months);

        EnumOptionData averagebal = SavingProductEnumerations.savingInterestCalculationMethod(SavingInterestCalculationMethod.AVERAGEBAL);
        EnumOptionData minbal = SavingProductEnumerations.savingInterestCalculationMethod(SavingInterestCalculationMethod.MINBAL);
        EnumOptionData monthlyCollection = SavingProductEnumerations
                .savingInterestCalculationMethod(SavingInterestCalculationMethod.MONTHLYCOLLECTION);
        List<EnumOptionData> interestCalculationOptions = Arrays.asList(averagebal, minbal, monthlyCollection);

        return new SavingProductData(savingProduct, currencyOptions, savingsProductTypeOptions, tenureTypeOptions, savingFrequencyOptions,
                savingsInterestTypeOptions, lockinPeriodTypeOptions, interestCalculationOptions);
    }

    @DELETE
    @Path("{productId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String deleteProduct(@PathParam("productId") final Long productId) {

        this.savingProductWritePlatformService.deleteSavingProduct(productId);

        return this.toApiJsonSerializer.serialize(productId);
    }
}