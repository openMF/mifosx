package org.mifosng.platform.api;

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
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.mifosng.platform.api.commands.SavingProductCommand;
import org.mifosng.platform.api.data.CurrencyData;
import org.mifosng.platform.api.data.EntityIdentifier;
import org.mifosng.platform.api.data.EnumOptionData;
import org.mifosng.platform.api.data.SavingProductData;
import org.mifosng.platform.api.infrastructure.ApiDataConversionService;
import org.mifosng.platform.api.infrastructure.ApiJsonSerializerService;
import org.mifosng.platform.api.infrastructure.ApiParameterHelper;
import org.mifosng.platform.currency.service.CurrencyReadPlatformService;
import org.mifosng.platform.savingproduct.domain.SavingFrequencyType;
import org.mifosng.platform.savingproduct.domain.SavingInterestCalculationMethod;
import org.mifosng.platform.savingproduct.domain.SavingProductType;
import org.mifosng.platform.savingproduct.domain.SavingsInterestType;
import org.mifosng.platform.savingproduct.domain.SavingsLockinPeriodEnum;
import org.mifosng.platform.savingproduct.domain.TenureTypeEnum;
import org.mifosng.platform.savingproduct.service.SavingProductEnumerations;
import org.mifosng.platform.savingproduct.service.SavingProductReadPlatformService;
import org.mifosng.platform.savingproduct.service.SavingProductWritePlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Path("/savingproducts")
@Component
@Scope("singleton")
public class SavingProductsApiResource {
	
	@Autowired
	private SavingProductReadPlatformService savingProductReadPlatformService;
	
	@Autowired
	private SavingProductWritePlatformService savingProductWritePlatformService;

	@Autowired
	private ApiDataConversionService apiDataConversionService;
	
    @Autowired
    private ApiJsonSerializerService apiJsonSerializerService;
    
    @Autowired
	private CurrencyReadPlatformService currencyReadPlatformService;
	
	@POST
	@Consumes({MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_JSON})
	public Response createSavingProduct(final String jsonRequestBody){
		
		final SavingProductCommand command=this.apiDataConversionService.convertJsonToSavingProductCommand(null, jsonRequestBody);
		
		EntityIdentifier entityIdentifier=this.savingProductWritePlatformService.createSavingProduct(command);
		
		return Response.ok().entity(entityIdentifier).build();
	}
	
	@PUT
	@Path("{productId}")
	@Consumes({MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_JSON})
	public Response updateSavingProduct(@PathParam("productId") final Long productId, final String jsonRequestBody){
		
		SavingProductCommand command=this.apiDataConversionService.convertJsonToSavingProductCommand(productId, jsonRequestBody);
		EntityIdentifier entityIdentifier=this.savingProductWritePlatformService.updateSavingProduct(command);
		return Response.ok().entity(entityIdentifier).build();
	}
	
	@GET
	@Consumes({MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_JSON})
	public String retrieveAllSavingProducts(@Context final UriInfo uriInfo) {

		Set<String> typicalResponseParameters = new HashSet<String>(Arrays.asList("id", "createdOn", "lastModifedOn",
				"locale", "name", "description","currencyCode", "digitsAfterDecimal","interstRate", "minInterestRate","maxInterestRate",
				"savingsDepositAmount","savingProductType","tenureType","tenure", "frequency","interestType","interestCalculationMethod",
				"minimumBalanceForWithdrawal","isPartialDepositAllowed","isLockinPeriodAllowed","lockinPeriod","lockinPeriodType"));
		
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
	@Consumes({MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_JSON})
	public String retrieveSavingProductDetails(@PathParam("productId") final Long productId, @Context final UriInfo uriInfo) {

		Set<String> typicalResponseParameters = new HashSet<String>(Arrays.asList("id", "createdOn", "lastModifedOn",
				"locale", "name", "description","currencyCode", "digitsAfterDecimal","interstRate", "minInterestRate","maxInterestRate",
				"savingsDepositAmount","savingProductType","tenureType","tenure", "frequency","interestType","interestCalculationMethod",
				"minimumBalanceForWithdrawal","isPartialDepositAllowed","isLockinPeriodAllowed","lockinPeriod","lockinPeriodType"));
		
		Set<String> responseParameters = ApiParameterHelper.extractFieldsForResponseIfProvided(uriInfo.getQueryParameters());
		if (responseParameters.isEmpty()) {
			responseParameters.addAll(typicalResponseParameters);
		}
		boolean prettyPrint = ApiParameterHelper.prettyPrint(uriInfo.getQueryParameters());
		boolean template = ApiParameterHelper.template(uriInfo.getQueryParameters());
		if (template) {
			responseParameters.addAll(Arrays.asList("currencyOptions"));
		}
		
		SavingProductData savingProduct = this.savingProductReadPlatformService.retrieveSavingProduct(productId);
		
		return this.apiJsonSerializerService.serializeSavingProductDataToJson(prettyPrint, responseParameters, savingProduct);
	}
	
	@GET
	@Path("template")
	@Consumes({MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_JSON})
	public String retrieveNewSavingProductDetails(@Context final UriInfo uriInfo) {
		
		Set<String> typicalResponseParameters = new HashSet<String>(
				Arrays.asList("id", "createdOn", "lastModifedOn",
						"locale", "name", "description","currencyCode", "digitsAfterDecimal","interstRate", 
						"savingsDepositAmount","savingProductType","tenureType","tenure", "frequency","interestType","interestCalculationMethod",
						"minimumBalanceForWithdrawal","isPartialDepositAllowed","isLockinPeriodAllowed","lockinPeriod","lockinPeriodType","currencyOptions"));
		
		Set<String> responseParameters = ApiParameterHelper.extractFieldsForResponseIfProvided(uriInfo.getQueryParameters());
		if (responseParameters.isEmpty()) {
			responseParameters.addAll(typicalResponseParameters);
		}
		boolean prettyPrint = ApiParameterHelper.prettyPrint(uriInfo.getQueryParameters());

		SavingProductData savingProduct = this.savingProductReadPlatformService.retrieveNewSavingProductDetails();
		savingProduct = handleTemplateRelatedData(responseParameters,savingProduct);
		return this.apiJsonSerializerService.serializeSavingProductDataToJson(prettyPrint, responseParameters, savingProduct);
	}
	
	private SavingProductData handleTemplateRelatedData(Set<String> responseParameters, SavingProductData savingProduct) {
		
		responseParameters.addAll(Arrays.asList("currencyOptions", "savingsProductTypeOptions", "tenureTypeOptions", "savingFrequencyOptions", "savingsInterestTypeOptions",
				"lockinPeriodTypeOptions", "interestCalculationOptions"));
		List<CurrencyData> currencyOptions = this.currencyReadPlatformService.retrieveAllowedCurrencies();
		
		EnumOptionData reccuring = SavingProductEnumerations.savingProductType(SavingProductType.RECCURING);
		EnumOptionData regular = SavingProductEnumerations.savingProductType(SavingProductType.REGULAR);
		List<EnumOptionData> savingsProductTypeOptions = Arrays.asList(reccuring,regular);
		
		EnumOptionData fixed = SavingProductEnumerations.tenureTypeEnum(TenureTypeEnum.FIXED_PERIOD);
		EnumOptionData perpetual = SavingProductEnumerations.tenureTypeEnum(TenureTypeEnum.PERPETUAL);
		List<EnumOptionData> tenureTypeOptions = Arrays.asList(fixed,perpetual);
		
		EnumOptionData monthly = SavingProductEnumerations.interestFrequencyType(SavingFrequencyType.MONTHLY);
		List<EnumOptionData> savingFrequencyOptions = Arrays.asList(monthly);
		
		EnumOptionData simple = SavingProductEnumerations.savingInterestType(SavingsInterestType.SIMPLE);
		EnumOptionData compounding = SavingProductEnumerations.savingInterestType(SavingsInterestType.COMPOUNDING);
		List<EnumOptionData> savingsInterestTypeOptions = Arrays.asList(simple,compounding);
		
		EnumOptionData months = SavingProductEnumerations.savingsLockinPeriod(SavingsLockinPeriodEnum.MONTHS);
		List<EnumOptionData> lockinPeriodTypeOptions = Arrays.asList(months);
		
		EnumOptionData averagebal = SavingProductEnumerations.savingInterestCalculationMethod(SavingInterestCalculationMethod.AVERAGEBAL); 
		EnumOptionData minbal = SavingProductEnumerations.savingInterestCalculationMethod(SavingInterestCalculationMethod.MINBAL);
		EnumOptionData monthlyCollection = SavingProductEnumerations.savingInterestCalculationMethod(SavingInterestCalculationMethod.MONTHLYCOLLECTION);
		List<EnumOptionData> interestCalculationOptions= Arrays.asList(averagebal,minbal,monthlyCollection);
		
		return new SavingProductData(savingProduct,currencyOptions,savingsProductTypeOptions,tenureTypeOptions,savingFrequencyOptions,savingsInterestTypeOptions,lockinPeriodTypeOptions,interestCalculationOptions);
	}

	@DELETE
	@Path("{productId}")
	@Consumes({MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_JSON})
	public Response deleteProduct(@PathParam("productId") final Long productId) {

		this.savingProductWritePlatformService.deleteSavingProduct(productId);

		return Response.ok(new EntityIdentifier(productId)).build();
	}
}