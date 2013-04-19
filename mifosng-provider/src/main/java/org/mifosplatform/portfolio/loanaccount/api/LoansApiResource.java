/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.loanaccount.api;

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
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.apache.commons.lang.StringUtils;
import org.joda.time.LocalDate;
import org.mifosplatform.commands.domain.CommandWrapper;
import org.mifosplatform.commands.service.CommandWrapperBuilder;
import org.mifosplatform.commands.service.PortfolioCommandSourceWritePlatformService;
import org.mifosplatform.infrastructure.codes.data.CodeValueData;
import org.mifosplatform.infrastructure.codes.service.CodeValueReadPlatformService;
import org.mifosplatform.infrastructure.core.api.ApiParameterHelper;
import org.mifosplatform.infrastructure.core.api.ApiRequestParameterHelper;
import org.mifosplatform.infrastructure.core.api.JsonQuery;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.data.EnumOptionData;
import org.mifosplatform.infrastructure.core.exception.UnrecognizedQueryParamException;
import org.mifosplatform.infrastructure.core.serialization.ApiRequestJsonSerializationSettings;
import org.mifosplatform.infrastructure.core.serialization.DefaultToApiJsonSerializer;
import org.mifosplatform.infrastructure.core.serialization.FromJsonHelper;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.organisation.staff.data.BulkTransferLoanOfficerData;
import org.mifosplatform.organisation.staff.data.StaffData;
import org.mifosplatform.organisation.staff.service.StaffReadPlatformService;
import org.mifosplatform.portfolio.charge.data.ChargeData;
import org.mifosplatform.portfolio.charge.service.ChargeReadPlatformService;
import org.mifosplatform.portfolio.collateral.data.CollateralData;
import org.mifosplatform.portfolio.collateral.service.CollateralReadPlatformService;
import org.mifosplatform.portfolio.fund.data.FundData;
import org.mifosplatform.portfolio.fund.service.FundReadPlatformService;
import org.mifosplatform.portfolio.group.data.GroupGeneralData;
import org.mifosplatform.portfolio.group.service.GroupReadPlatformService;
import org.mifosplatform.portfolio.loanaccount.data.LoanAccountData;
import org.mifosplatform.portfolio.loanaccount.data.LoanChargeData;
import org.mifosplatform.portfolio.loanaccount.data.LoanTransactionData;
import org.mifosplatform.portfolio.loanaccount.data.RepaymentScheduleRelatedLoanData;
import org.mifosplatform.portfolio.loanaccount.guarantor.data.GuarantorData;
import org.mifosplatform.portfolio.loanaccount.guarantor.service.GuarantorReadPlatformService;
import org.mifosplatform.portfolio.loanaccount.loanschedule.data.LoanScheduleData;
import org.mifosplatform.portfolio.loanaccount.loanschedule.service.LoanScheduleCalculationPlatformService;
import org.mifosplatform.portfolio.loanaccount.service.LoanChargeReadPlatformService;
import org.mifosplatform.portfolio.loanaccount.service.LoanReadPlatformService;
import org.mifosplatform.portfolio.loanproduct.data.LoanProductData;
import org.mifosplatform.portfolio.loanproduct.data.TransactionProcessingStrategyData;
import org.mifosplatform.portfolio.loanproduct.domain.LendingStrategy;
import org.mifosplatform.portfolio.loanproduct.exception.InvalidLendingStrategy;
import org.mifosplatform.portfolio.loanproduct.service.LoanDropdownReadPlatformService;
import org.mifosplatform.portfolio.loanproduct.service.LoanProductReadPlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.google.gson.JsonElement;

@Path("/loans")
@Component
@Scope("singleton")
public class LoansApiResource {

    private final Set<String> LOAN_DATA_PARAMETERS = new HashSet<String>(Arrays.asList("id", "accountNo", "status", "externalId",
            "clientId", "group", "loanProductId", "loanProductName", "loanProductDescription", "fundId", "fundName", "loanPurposeId",
            "loanPurposeName", "loanOfficerId", "loanOfficerName", "currency", "principal", "inArrearsTolerance", "termFrequency",
            "termPeriodFrequencyType", "numberOfRepayments", "repaymentEvery", "interestRatePerPeriod", "annualInterestRate",
            "repaymentFrequencyType", "transactionProcessingStrategyId", "interestRateFrequencyType", "amortizationType", "interestType",
            "interestCalculationPeriodType", "expectedFirstRepaymentOnDate", "interestChargedFromDate", "timeline",
            "totalFeeChargesAtDisbursement", "summary", "repaymentSchedule", "transactions", "charges", "collateral", "guarantors",
            "productOptions", "amortizationTypeOptions", "interestTypeOptions", "interestCalculationPeriodTypeOptions",
            "repaymentFrequencyTypeOptions", "termFrequencyTypeOptions", "interestRateFrequencyTypeOptions", "fundOptions",
            "repaymentStrategyOptions", "chargeOptions", "loanOfficerOptions", "loanPurposeOptions", "loanCollateralOptions",
            "chargeTemplate"));

    private final String resourceNameForPermissions = "LOAN";

    private final PlatformSecurityContext context;
    private final LoanReadPlatformService loanReadPlatformService;
    private final LoanProductReadPlatformService loanProductReadPlatformService;
    private final LoanDropdownReadPlatformService dropdownReadPlatformService;
    private final FundReadPlatformService fundReadPlatformService;
    private final ChargeReadPlatformService chargeReadPlatformService;
    private final LoanChargeReadPlatformService loanChargeReadPlatformService;
    private final CollateralReadPlatformService loanCollateralReadPlatformService;
    private final LoanScheduleCalculationPlatformService calculationPlatformService;
    private final StaffReadPlatformService staffReadPlatformService;
    private final GuarantorReadPlatformService guarantorReadPlatformService;
    private final CodeValueReadPlatformService codeValueReadPlatformService;
    private final GroupReadPlatformService groupReadPlatformService;
    private final DefaultToApiJsonSerializer<LoanAccountData> toApiJsonSerializer;
    private final DefaultToApiJsonSerializer<LoanScheduleData> loanScheduleToApiJsonSerializer;
    private final DefaultToApiJsonSerializer<BulkTransferLoanOfficerData> loanOfficeTransferToApiJsonSerializer;
    private final ApiRequestParameterHelper apiRequestParameterHelper;
    private final FromJsonHelper fromJsonHelper;
    private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;

    @Autowired
    public LoansApiResource(final PlatformSecurityContext context, final LoanReadPlatformService loanReadPlatformService,
            final LoanProductReadPlatformService loanProductReadPlatformService,
            final LoanDropdownReadPlatformService dropdownReadPlatformService, final FundReadPlatformService fundReadPlatformService,
            final ChargeReadPlatformService chargeReadPlatformService, final LoanChargeReadPlatformService loanChargeReadPlatformService,
            final CollateralReadPlatformService loanCollateralReadPlatformService,
            final LoanScheduleCalculationPlatformService calculationPlatformService,
            final StaffReadPlatformService staffReadPlatformService, final GuarantorReadPlatformService guarantorReadPlatformService,
            final CodeValueReadPlatformService codeValueReadPlatformService,
            final GroupReadPlatformService groupReadPlatformService,
            final DefaultToApiJsonSerializer<LoanAccountData> toApiJsonSerializer,
            final DefaultToApiJsonSerializer<LoanScheduleData> loanScheduleToApiJsonSerializer,
            final DefaultToApiJsonSerializer<BulkTransferLoanOfficerData> loanOfficeTransferToApiJsonSerializer,
            final ApiRequestParameterHelper apiRequestParameterHelper, final FromJsonHelper fromJsonHelper,
            final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService) {
        this.context = context;
        this.loanReadPlatformService = loanReadPlatformService;
        this.loanProductReadPlatformService = loanProductReadPlatformService;
        this.dropdownReadPlatformService = dropdownReadPlatformService;
        this.fundReadPlatformService = fundReadPlatformService;
        this.chargeReadPlatformService = chargeReadPlatformService;
        this.loanChargeReadPlatformService = loanChargeReadPlatformService;
        this.loanCollateralReadPlatformService = loanCollateralReadPlatformService;
        this.calculationPlatformService = calculationPlatformService;
        this.staffReadPlatformService = staffReadPlatformService;
        this.guarantorReadPlatformService = guarantorReadPlatformService;
        this.codeValueReadPlatformService = codeValueReadPlatformService;
        this.groupReadPlatformService = groupReadPlatformService;
        this.toApiJsonSerializer = toApiJsonSerializer;
        this.loanScheduleToApiJsonSerializer = loanScheduleToApiJsonSerializer;
        this.loanOfficeTransferToApiJsonSerializer = loanOfficeTransferToApiJsonSerializer;
        this.apiRequestParameterHelper = apiRequestParameterHelper;
        this.fromJsonHelper = fromJsonHelper;
        this.commandsSourceWritePlatformService = commandsSourceWritePlatformService;
    }

    @GET
    @Path("template")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String template(@QueryParam("clientId") final Long clientId, @QueryParam("groupId") final Long groupId,
            @QueryParam("productId") final Long productId, @QueryParam("lendingStrategy") final Integer lendingStrategy, @Context final UriInfo uriInfo) {

        context.authenticatedUser().validateHasReadPermission(resourceNameForPermissions);

        // template
        final Collection<LoanProductData> productOptions = this.loanProductReadPlatformService.retrieveAllLoanProductsForLookup();

        // associations
        final LoanScheduleData repaymentSchedule = null;
        final Collection<LoanTransactionData> loanRepayments = null;
        final Collection<CollateralData> collateral = null;
        final Collection<GuarantorData> guarantors = null;

        // options
        Collection<EnumOptionData> loanTermFrequencyTypeOptions = null;
        Collection<EnumOptionData> repaymentFrequencyTypeOptions = null;
        Collection<EnumOptionData> interestRateFrequencyTypeOptions = null;
        Collection<EnumOptionData> amortizationTypeOptions = null;
        Collection<EnumOptionData> interestTypeOptions = null;
        Collection<EnumOptionData> interestCalculationPeriodTypeOptions = null;
        Collection<FundData> fundOptions = null;
        Collection<TransactionProcessingStrategyData> repaymentStrategyOptions = null;
        Collection<StaffData> allowedLoanOfficers = null;
        Collection<CodeValueData> loanPurposeOptions = null;
        Collection<CodeValueData> loanCollateralOptions = null;
        Collection<ChargeData> chargeOptions = null;
        ChargeData chargeTemplate = null;

        if (productId != null) {
            loanTermFrequencyTypeOptions = dropdownReadPlatformService.retrieveLoanTermFrequencyTypeOptions();
            repaymentFrequencyTypeOptions = dropdownReadPlatformService.retrieveRepaymentFrequencyTypeOptions();
            interestRateFrequencyTypeOptions = dropdownReadPlatformService.retrieveInterestRateFrequencyTypeOptions();

            amortizationTypeOptions = dropdownReadPlatformService.retrieveLoanAmortizationTypeOptions();
            interestTypeOptions = dropdownReadPlatformService.retrieveLoanInterestTypeOptions();
            interestCalculationPeriodTypeOptions = dropdownReadPlatformService.retrieveLoanInterestRateCalculatedInPeriodOptions();

            fundOptions = this.fundReadPlatformService.retrieveAllFunds();
            repaymentStrategyOptions = this.dropdownReadPlatformService.retreiveTransactionProcessingStrategies();
            loanPurposeOptions = this.codeValueReadPlatformService.retrieveCodeValuesByCode("LoanPurpose");
            loanCollateralOptions = this.codeValueReadPlatformService.retrieveCodeValuesByCode("LoanCollateral");

            final boolean feeChargesOnly = false;
            chargeOptions = this.chargeReadPlatformService.retrieveLoanApplicableCharges(feeChargesOnly);
        }

        LoanAccountData newLoanAccount = null;
        if (clientId != null) {
            LoanAccountData loanBasicDetails = this.loanReadPlatformService.retrieveTemplateWithClientAndProductDetails(clientId,
                    productId);
            final Collection<LoanChargeData> charges = loanBasicDetails.charges();
            
            if (productId != null) {
                final Long officeId = loanBasicDetails.officeId();
                allowedLoanOfficers = this.staffReadPlatformService.retrieveAllLoanOfficersByOffice(officeId);
            }

            //If JLG loan then attach group
            if(groupId != null){
                final GroupGeneralData group = this.groupReadPlatformService.retrieveOne(groupId);
                loanBasicDetails = LoanAccountData.associateGroup(loanBasicDetails, group);
            }
            
            
            newLoanAccount = LoanAccountData.associationsAndTemplate(loanBasicDetails, repaymentSchedule, loanRepayments, charges,
                    collateral, guarantors, productOptions, loanTermFrequencyTypeOptions, repaymentFrequencyTypeOptions,
                    repaymentStrategyOptions, interestRateFrequencyTypeOptions, amortizationTypeOptions, interestTypeOptions,
                    interestCalculationPeriodTypeOptions, fundOptions, chargeOptions, chargeTemplate, allowedLoanOfficers,
                    loanPurposeOptions, loanCollateralOptions);

        } else if (groupId != null ) {

            LoanAccountData loanBasicDetails = null;
            
            if (LendingStrategy.JOINT_LIABILITY_LOAN.getId().equals(lendingStrategy)){
                loanBasicDetails = this.loanReadPlatformService.retrieveTemplateWithCompleteGroupAndProductDetails(groupId,
                        productId);
            }
            else if( LendingStrategy.GROUP_LOAN.getId().equals(lendingStrategy)){
                loanBasicDetails = this.loanReadPlatformService.retrieveTemplateWithGroupAndProductDetails(groupId,
                        productId);
            }else{
                throw new InvalidLendingStrategy(lendingStrategy);
            }
            
            
            final Collection<LoanChargeData> charges = loanBasicDetails.charges();
            if (productId != null) {
                final Long officeId = loanBasicDetails.groupOfficeId();
                if(officeId != null) allowedLoanOfficers = this.staffReadPlatformService.retrieveAllLoanOfficersByOffice(officeId);
            }

            newLoanAccount = LoanAccountData.associationsAndTemplate(loanBasicDetails, repaymentSchedule, loanRepayments, charges,
                    collateral, guarantors, productOptions, loanTermFrequencyTypeOptions, repaymentFrequencyTypeOptions,
                    repaymentStrategyOptions, interestRateFrequencyTypeOptions, amortizationTypeOptions, interestTypeOptions,
                    interestCalculationPeriodTypeOptions, fundOptions, chargeOptions, chargeTemplate, allowedLoanOfficers,
                    loanPurposeOptions, loanCollateralOptions);
        }
        else {
            newLoanAccount = LoanAccountData.collateralTemplate(loanCollateralOptions);
        }

        final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.toApiJsonSerializer.serialize(settings, newLoanAccount, LOAN_DATA_PARAMETERS);
    }

    @GET
    @Path("{loanId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveLoan(@PathParam("loanId") final Long loanId, @Context final UriInfo uriInfo) {

        context.authenticatedUser().validateHasReadPermission(resourceNameForPermissions);

        final LoanAccountData loanBasicDetails = this.loanReadPlatformService.retrieveLoanAccountDetails(loanId);

        Collection<LoanTransactionData> loanRepayments = null;
        LoanScheduleData repaymentSchedule = null;
        Collection<LoanChargeData> charges = null;
        Collection<GuarantorData> guarantors = null;
        Collection<CollateralData> collateral = null;

        final Set<String> mandatoryResponseParameters = new HashSet<String>();
        final Set<String> associationParameters = ApiParameterHelper.extractAssociationsForResponseIfProvided(uriInfo.getQueryParameters());
        if (!associationParameters.isEmpty()) {

            if (associationParameters.contains("all")) {
                associationParameters.addAll(Arrays.asList("repaymentSchedule", "transactions", "charges", "guarantors", "collateral"));
            }

            if (associationParameters.contains("guarantors")) {
                mandatoryResponseParameters.add("guarantors");
                guarantors = this.guarantorReadPlatformService.retrieveGuarantorsForLoan(loanId);
                if (CollectionUtils.isEmpty(guarantors)) {
                    guarantors = null;
                }
            }

            if (associationParameters.contains("transactions")) {
                mandatoryResponseParameters.add("transactions");
                final Collection<LoanTransactionData> currentLoanRepayments = this.loanReadPlatformService.retrieveLoanTransactions(loanId);
                if (!CollectionUtils.isEmpty(currentLoanRepayments)) {
                    loanRepayments = currentLoanRepayments;
                }
            }

            if (associationParameters.contains("repaymentSchedule")) {
                mandatoryResponseParameters.add("repaymentSchedule");

                final RepaymentScheduleRelatedLoanData repaymentScheduleRelatedData = loanBasicDetails.repaymentScheduleRelatedData();
                repaymentSchedule = this.loanReadPlatformService.retrieveRepaymentSchedule(loanId, repaymentScheduleRelatedData);
            }

            if (associationParameters.contains("charges")) {
                mandatoryResponseParameters.add("charges");
                charges = this.loanChargeReadPlatformService.retrieveLoanCharges(loanId);
                if (CollectionUtils.isEmpty(charges)) {
                    charges = null;
                }
            }

            if (associationParameters.contains("collateral")) {
                mandatoryResponseParameters.add("collateral");
                collateral = this.loanCollateralReadPlatformService.retrieveCollaterals(loanId);
                if (CollectionUtils.isEmpty(collateral)) {
                    collateral = null;
                }
            }
        }

        Collection<LoanProductData> productOptions = null;
        Collection<EnumOptionData> loanTermFrequencyTypeOptions = null;
        Collection<EnumOptionData> repaymentFrequencyTypeOptions = null;
        Collection<TransactionProcessingStrategyData> repaymentStrategyOptions = null;
        Collection<EnumOptionData> interestRateFrequencyTypeOptions = null;
        Collection<EnumOptionData> amortizationTypeOptions = null;
        Collection<EnumOptionData> interestTypeOptions = null;
        Collection<EnumOptionData> interestCalculationPeriodTypeOptions = null;
        Collection<FundData> fundOptions = null;
        Collection<StaffData> allowedLoanOfficers = null;
        Collection<ChargeData> chargeOptions = null;
        ChargeData chargeTemplate = null;
        Collection<CodeValueData> loanPurposeOptions = null;
        Collection<CodeValueData> loanCollateralOptions = null;

        final boolean template = ApiParameterHelper.template(uriInfo.getQueryParameters());
        if (template) {
            productOptions = this.loanProductReadPlatformService.retrieveAllLoanProductsForLookup();
            loanTermFrequencyTypeOptions = dropdownReadPlatformService.retrieveLoanTermFrequencyTypeOptions();
            repaymentFrequencyTypeOptions = dropdownReadPlatformService.retrieveRepaymentFrequencyTypeOptions();
            interestRateFrequencyTypeOptions = dropdownReadPlatformService.retrieveInterestRateFrequencyTypeOptions();

            amortizationTypeOptions = dropdownReadPlatformService.retrieveLoanAmortizationTypeOptions();
            interestTypeOptions = dropdownReadPlatformService.retrieveLoanInterestTypeOptions();
            interestCalculationPeriodTypeOptions = dropdownReadPlatformService.retrieveLoanInterestRateCalculatedInPeriodOptions();

            fundOptions = this.fundReadPlatformService.retrieveAllFunds();
            repaymentStrategyOptions = this.dropdownReadPlatformService.retreiveTransactionProcessingStrategies();
            final boolean feeChargesOnly = false;
            chargeOptions = this.chargeReadPlatformService.retrieveLoanApplicableCharges(feeChargesOnly);
            chargeTemplate = this.loanChargeReadPlatformService.retrieveLoanChargeTemplate();
            allowedLoanOfficers = this.staffReadPlatformService.retrieveAllLoanOfficersByOffice(loanBasicDetails.officeId());
            loanPurposeOptions = this.codeValueReadPlatformService.retrieveCodeValuesByCode("LoanPurpose");
            loanCollateralOptions = this.codeValueReadPlatformService.retrieveCodeValuesByCode("LoanCollateral");
        }

        final LoanAccountData loanAccount = LoanAccountData.associationsAndTemplate(loanBasicDetails, repaymentSchedule, loanRepayments,
                charges, collateral, guarantors, productOptions, loanTermFrequencyTypeOptions, repaymentFrequencyTypeOptions,
                repaymentStrategyOptions, interestRateFrequencyTypeOptions, amortizationTypeOptions, interestTypeOptions,
                interestCalculationPeriodTypeOptions, fundOptions, chargeOptions, chargeTemplate, allowedLoanOfficers, loanPurposeOptions,
                loanCollateralOptions);

        final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters(),
                mandatoryResponseParameters);
        return this.toApiJsonSerializer.serialize(settings, loanAccount, LOAN_DATA_PARAMETERS);
    }

    @POST
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String calculateLoanScheduleOrSubmitLoanApplication(@QueryParam("command") final String commandParam,
            @Context final UriInfo uriInfo, final String apiRequestBodyAsJson) {

        if (is(commandParam, "calculateLoanSchedule")) {

            final JsonElement parsedQuery = this.fromJsonHelper.parse(apiRequestBodyAsJson);
            final JsonQuery query = JsonQuery.from(apiRequestBodyAsJson, parsedQuery, this.fromJsonHelper);

            final LoanScheduleData loanSchedule = this.calculationPlatformService.calculateLoanSchedule(query);

            final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
            return this.loanScheduleToApiJsonSerializer.serialize(settings, loanSchedule, new HashSet<String>());
        }

        final CommandWrapper commandRequest = new CommandWrapperBuilder().createLoanApplication().withJson(apiRequestBodyAsJson).build();

        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);

        return this.toApiJsonSerializer.serialize(result);
    }

    @PUT
    @Path("{loanId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String modifyLoanApplication(@PathParam("loanId") final Long loanId, final String apiRequestBodyAsJson) {

        final CommandWrapper commandRequest = new CommandWrapperBuilder().updateLoanApplication(loanId).withJson(apiRequestBodyAsJson)
                .build();

        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);

        return this.toApiJsonSerializer.serialize(result);
    }

    @DELETE
    @Path("{loanId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String deleteLoanApplication(@PathParam("loanId") final Long loanId) {

        final CommandWrapper commandRequest = new CommandWrapperBuilder().deleteLoanApplication(loanId).build();

        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);

        return this.toApiJsonSerializer.serialize(result);
    }

    @POST
    @Path("{loanId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String stateTransitions(@PathParam("loanId") final Long loanId, @QueryParam("command") final String commandParam,
            final String apiRequestBodyAsJson) {

        final CommandWrapperBuilder builder = new CommandWrapperBuilder().withJson(apiRequestBodyAsJson);

        CommandProcessingResult result = null;

        if (is(commandParam, "reject")) {
            final CommandWrapper commandRequest = builder.rejectLoanApplication(loanId).build();
            result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
        } else if (is(commandParam, "withdrawnByApplicant")) {
            final CommandWrapper commandRequest = builder.withdrawLoanApplication(loanId).build();
            result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
        } else if (is(commandParam, "approve")) {
            final CommandWrapper commandRequest = builder.approveLoanApplication(loanId).build();
            result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
        } else if (is(commandParam, "disburse")) {
            final CommandWrapper commandRequest = builder.disburseLoanApplication(loanId).build();
            result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
        }

        if (is(commandParam, "undoapproval")) {
            final CommandWrapper commandRequest = builder.undoLoanApplicationApproval(loanId).build();
            result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
        } else if (is(commandParam, "undodisbursal")) {
            final CommandWrapper commandRequest = builder.undoLoanApplicationDisbursal(loanId).build();
            result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
        }

        if (is(commandParam, "assignloanofficer")) {
            final CommandWrapper commandRequest = builder.assignLoanOfficer(loanId).build();
            result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
        } else if (is(commandParam, "unassignloanofficer")) {
            final CommandWrapper commandRequest = builder.unassignLoanOfficer(loanId).build();
            result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
        }

        if (result == null) { throw new UnrecognizedQueryParamException("command", commandParam); }

        return this.toApiJsonSerializer.serialize(result);
    }

    private boolean is(final String commandParam, final String commandValue) {
        return StringUtils.isNotBlank(commandParam) && commandParam.trim().equalsIgnoreCase(commandValue);
    }

    @GET
    @Path("{loanId}/assign/template")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String assignLoanOfficerTemplate(@PathParam("loanId") final Long loanId, @Context final UriInfo uriInfo) {

        // TODO - KW - review if this template method can be put back into
        // normal
        // template method to avoid 'assign' on api. and avoid needing to query
        // for officeId and loanOfficerId in current way!
        final LoanAccountData loanBasicDetails = this.loanReadPlatformService.retrieveLoanAccountDetails(loanId);

        final Collection<StaffData> allowedLoanOfficers = this.staffReadPlatformService.retrieveAllLoanOfficersByOffice(loanBasicDetails
                .officeId());
        final Long fromLoanOfficerId = loanBasicDetails.loanOfficerId();

        final BulkTransferLoanOfficerData loanReassignmentData = BulkTransferLoanOfficerData.template(fromLoanOfficerId,
                allowedLoanOfficers, new LocalDate());

        final Set<String> RESPONSE_DATA_PARAMETERS = new HashSet<String>(Arrays.asList("officeId", "fromLoanOfficerId", "assignmentDate",
                "officeOptions", "loanOfficerOptions", "accountSummaryCollection"));

        final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.loanOfficeTransferToApiJsonSerializer.serialize(settings, loanReassignmentData, RESPONSE_DATA_PARAMETERS);
    }
}