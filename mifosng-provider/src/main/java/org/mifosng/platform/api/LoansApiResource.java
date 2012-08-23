package org.mifosng.platform.api;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.apache.commons.lang.StringUtils;
import org.mifosng.platform.api.commands.AdjustLoanTransactionCommand;
import org.mifosng.platform.api.commands.CalculateLoanScheduleCommand;
import org.mifosng.platform.api.commands.LoanStateTransitionCommand;
import org.mifosng.platform.api.commands.LoanTransactionCommand;
import org.mifosng.platform.api.commands.SubmitLoanApplicationCommand;
import org.mifosng.platform.api.commands.UndoStateTransitionCommand;
import org.mifosng.platform.api.data.EntityIdentifier;
import org.mifosng.platform.api.data.LoanAccountData;
import org.mifosng.platform.api.data.LoanAccountSummaryData;
import org.mifosng.platform.api.data.LoanBasicDetailsData;
import org.mifosng.platform.api.data.LoanPermissionData;
import org.mifosng.platform.api.data.LoanRepaymentPeriodData;
import org.mifosng.platform.api.data.LoanRepaymentTransactionData;
import org.mifosng.platform.api.data.LoanSchedule;
import org.mifosng.platform.api.data.LoanTransactionData;
import org.mifosng.platform.api.data.NewLoanData;
import org.mifosng.platform.api.infrastructure.ApiDataConversionService;
import org.mifosng.platform.api.infrastructure.ApiJsonSerializerService;
import org.mifosng.platform.api.infrastructure.ApiParameterHelper;
import org.mifosng.platform.exceptions.UnrecognizedQueryParamException;
import org.mifosng.platform.loan.service.CalculationPlatformService;
import org.mifosng.platform.loan.service.LoanReadPlatformService;
import org.mifosng.platform.loan.service.LoanWritePlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Path("/loans")
@Component
@Scope("singleton")
public class LoansApiResource {

	@Autowired
	private LoanReadPlatformService loanReadPlatformService;

	@Autowired
	private LoanWritePlatformService loanWritePlatformService;

	@Autowired
	private CalculationPlatformService calculationPlatformService;

	@Autowired
	private ApiDataConversionService apiDataConversionService;
	
	@Autowired
	private ApiJsonSerializerService apiJsonSerializerService;

	@GET
	@Path("template")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String retrieveDetailsForNewLoanApplicationStepOne(
			@QueryParam("clientId") final Long clientId,
			@QueryParam("productId") final Long productId,
			@Context final UriInfo uriInfo) {
		
		Set<String> typicalResponseParameters = new HashSet<String>(Arrays.asList("clientId", "clientName", "productId", "productName",
				"selectedProduct", "expectedDisbursementDate", "allowedProducts"));
		
		Set<String> responseParameters = ApiParameterHelper.extractFieldsForResponseIfProvided(uriInfo.getQueryParameters());
		if (responseParameters.isEmpty()) {
			responseParameters.addAll(typicalResponseParameters);
		}
		boolean prettyPrint = ApiParameterHelper.prettyPrint(uriInfo.getQueryParameters());
		
		NewLoanData workflowData = this.loanReadPlatformService.retrieveClientAndProductDetails(clientId, productId);

		return this.apiJsonSerializerService.serializeNewLoanDataToJson(prettyPrint, responseParameters, workflowData);
	}

	@GET
	@Path("{loanId}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String retrieveLoanAccountDetails(
			@PathParam("loanId") final Long loanId,
			@Context final UriInfo uriInfo) {

		Set<String> typicalResponseParameters = new HashSet<String>(
				Arrays.asList("id", "externalId", "fundId", "fundName", "loanProductId", "loanProductName", "principal", "inArrearsTolerance", "numberOfRepayments",
						"repaymentEvery", "interestRatePerPeriod", "annualInterestRate", 
						"repaymentFrequencyType", "interestRateFrequencyType", "amortizationType", "interestType", "interestCalculationPeriodType",
						"submittedOnDate", "approvedOnDate", "expectedDisbursementDate", "actualDisbursementDate", 
						"expectedFirstRepaymentOnDate", "interestChargedFromDate", "closedOnDate", "expectedMaturityDate", 
						"lifeCycleStatusId", "lifeCycleStatusText", "lifeCycleStatusDate")
				);
		
		Set<String> responseParameters = ApiParameterHelper.extractFieldsForResponseIfProvided(uriInfo.getQueryParameters());
		if (responseParameters.isEmpty()) {
			responseParameters.addAll(typicalResponseParameters);
		}
		boolean prettyPrint = ApiParameterHelper.prettyPrint(uriInfo.getQueryParameters());
		
		LoanBasicDetailsData loanBasicDetails = this.loanReadPlatformService.retrieveLoanAccountDetails(loanId);
		
		Collection<LoanRepaymentTransactionData> loanRepayments = null;
		Collection<LoanRepaymentPeriodData> repaymentSchedule = null;
		LoanAccountSummaryData summary = null;
		LoanPermissionData permissions = null;
		
		Set<String> associationParameters = ApiParameterHelper.extractAssociationsForResponseIfProvided(uriInfo.getQueryParameters());
		if (!associationParameters.isEmpty()) {
			if (associationParameters.contains("all")) {
				responseParameters.addAll(Arrays.asList("summary", "repaymentSchedule", "loanRepayments", "permissions", "convenienceData"));
			} else {
				responseParameters.addAll(associationParameters);
			}
			
			loanRepayments = this.loanReadPlatformService.retrieveLoanPayments(loanId);
			repaymentSchedule = this.loanReadPlatformService.retrieveRepaymentSchedule(loanId);

			summary = this.loanReadPlatformService.retrieveSummary(loanBasicDetails.getPrincipal(), repaymentSchedule, loanRepayments);

			boolean isWaiveAllowed = summary.isWaiveAllowed(loanBasicDetails.getInArrearsTolerance());
			permissions = this.loanReadPlatformService.retrieveLoanPermissions(loanBasicDetails, isWaiveAllowed, loanRepayments.size());
		}

		LoanAccountData loanAccount = new LoanAccountData(loanBasicDetails, summary, repaymentSchedule, loanRepayments, permissions);
		
		return this.apiJsonSerializerService.serialzieLoanAccountDataToJson(prettyPrint, responseParameters, loanAccount);
	}

	@POST
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public Response calculateLoanScheduleOrSubmitLoanApplication(
			@QueryParam("command") final String commandParam,
			final String jsonRequestBody) {

		SubmitLoanApplicationCommand command = this.apiDataConversionService
				.convertJsonToSubmitLoanApplicationCommand(jsonRequestBody);

		CalculateLoanScheduleCommand calculateLoanScheduleCommand = command.toCalculateLoanScheduleCommand();
		LoanSchedule loanSchedule = this.calculationPlatformService.calculateLoanSchedule(calculateLoanScheduleCommand);

		// for now just auto generating the loan schedule and setting support
		// for 'manual' loan schedule creation later.
		command.setLoanSchedule(loanSchedule);

		if (is(commandParam, "calculateLoanSchedule")) {
			return Response.ok().entity(loanSchedule).build();
		}

		EntityIdentifier identifier = this.loanWritePlatformService.submitLoanApplication(command);

		return Response.ok().entity(identifier).build();
	}

	@DELETE
	@Path("{loanId}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public Response deleteLoanApplication(@PathParam("loanId") final Long loanId) {

		EntityIdentifier identifier = this.loanWritePlatformService
				.deleteLoan(loanId);

		return Response.ok().entity(identifier).build();
	}

	@POST
	@Path("{loanId}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public Response stateTransitions(@PathParam("loanId") final Long loanId,
			@QueryParam("command") final String commandParam,
			final String jsonRequestBody) {

		LoanStateTransitionCommand command = this.apiDataConversionService.convertJsonToLoanStateTransitionCommand(loanId, jsonRequestBody);

		Response response = null;

		if (is(commandParam, "reject")) {
			EntityIdentifier identifier = this.loanWritePlatformService
					.rejectLoan(command);
			response = Response.ok().entity(identifier).build();
		} else if (is(commandParam, "withdrewbyclient")) {
			EntityIdentifier identifier = this.loanWritePlatformService
					.withdrawLoan(command);
			response = Response.ok().entity(identifier).build();
		} else if (is(commandParam, "approve")) {
			EntityIdentifier identifier = this.loanWritePlatformService
					.approveLoanApplication(command);
			response = Response.ok().entity(identifier).build();
		} else if (is(commandParam, "disburse")) {
			EntityIdentifier identifier = this.loanWritePlatformService
					.disburseLoan(command);
			response = Response.ok().entity(identifier).build();
		}

		UndoStateTransitionCommand undoCommand = new UndoStateTransitionCommand(
				loanId, command.getNote());

		if (is(commandParam, "undoapproval")) {
			EntityIdentifier identifier = this.loanWritePlatformService
					.undoLoanApproval(undoCommand);
			response = Response.ok().entity(identifier).build();
		} else if (is(commandParam, "undodisbursal")) {
			EntityIdentifier identifier = this.loanWritePlatformService
					.undoLoanDisbursal(undoCommand);
			response = Response.ok().entity(identifier).build();
		}

		if (response == null) {
			throw new UnrecognizedQueryParamException("command", commandParam);
		}

		return response;
	}

	private boolean is(final String commandParam, final String commandValue) {
		return StringUtils.isNotBlank(commandParam)
				&& commandParam.trim().equalsIgnoreCase(commandValue);
	}

	@POST
	@Path("{loanId}/transactions")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public Response executeLoanTransaction(
			@PathParam("loanId") final Long loanId,
			@QueryParam("command") final String commandParam,
			final String jsonRequestBody) {

		final LoanTransactionCommand command = this.apiDataConversionService
				.convertJsonToLoanTransactionCommand(loanId, jsonRequestBody);

		Response response = null;

		if (is(commandParam, "repayment")) {
			EntityIdentifier identifier = this.loanWritePlatformService
					.makeLoanRepayment(command);
			response = Response.ok().entity(identifier).build();
		} else if (is(commandParam, "waiver")) {
			EntityIdentifier identifier = this.loanWritePlatformService
					.waiveLoanAmount(command);
			response = Response.ok().entity(identifier).build();
		}

		if (response == null) {
			throw new UnrecognizedQueryParamException("command", commandParam);
		}

		return response;
	}

	@GET
	@Path("{loanId}/transactions/template")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String retrieveNewRepaymentDetails(
			@PathParam("loanId") final Long loanId,
			@QueryParam("command") final String commandParam,
			@Context final UriInfo uriInfo) {
		
		Set<String> typicalResponseParameters = new HashSet<String>(
				Arrays.asList("id", "transactionType", "date", "principal", "interest", "total", "totalWaived", "overpaid")
				);
		
		Set<String> responseParameters = ApiParameterHelper.extractFieldsForResponseIfProvided(uriInfo.getQueryParameters());
		if (responseParameters.isEmpty()) {
			responseParameters.addAll(typicalResponseParameters);
		}
		boolean prettyPrint = ApiParameterHelper.prettyPrint(uriInfo.getQueryParameters());
		
		LoanTransactionData transactionData = null;
		if (is(commandParam, "repayment")) {
			transactionData = this.loanReadPlatformService.retrieveNewLoanRepaymentDetails(loanId);
		} else if (is(commandParam, "waiver")) {
			transactionData = this.loanReadPlatformService.retrieveNewLoanWaiverDetails(loanId);
		} else {
			throw new UnrecognizedQueryParamException("command", commandParam);
		}

		return this.apiJsonSerializerService.serializeLoanTransactionDataToJson(prettyPrint, responseParameters, transactionData);
	}

	@GET
	@Path("{loanId}/transactions/{transactionId}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String retrieveTransaction(@PathParam("loanId") final Long loanId,
			@PathParam("transactionId") final Long transactionId,
			@Context final UriInfo uriInfo) {
		
		Set<String> typicalResponseParameters = new HashSet<String>(
				Arrays.asList("id", "transactionType", "date", "principal", "interest", "total", "totalWaived", "overpaid")
				);
		
		Set<String> responseParameters = ApiParameterHelper.extractFieldsForResponseIfProvided(uriInfo.getQueryParameters());
		if (responseParameters.isEmpty()) {
			responseParameters.addAll(typicalResponseParameters);
		}
		boolean prettyPrint = ApiParameterHelper.prettyPrint(uriInfo.getQueryParameters());
		
		LoanTransactionData transactionData = this.loanReadPlatformService.retrieveLoanTransactionDetails(loanId, transactionId);

		return this.apiJsonSerializerService.serializeLoanTransactionDataToJson(prettyPrint, responseParameters, transactionData);
	}

	@POST
	@Path("{loanId}/transactions/{transactionId}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public Response adjustLoanTransaction(
			@PathParam("loanId") final Long loanId,
			@PathParam("transactionId") final Long transactionId,
			final String jsonRequestBody) {

		final AdjustLoanTransactionCommand command = this.apiDataConversionService
				.convertJsonToAdjustLoanTransactionCommand(loanId,
						transactionId, jsonRequestBody);

		EntityIdentifier identifier = this.loanWritePlatformService
				.adjustLoanTransaction(command);

		return Response.ok().entity(identifier).build();
	}
}