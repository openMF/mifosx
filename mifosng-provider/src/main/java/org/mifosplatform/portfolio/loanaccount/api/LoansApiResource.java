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
import org.mifosplatform.commands.service.PortfolioCommandSourceWritePlatformService;
import org.mifosplatform.infrastructure.core.api.ApiParameterHelper;
import org.mifosplatform.infrastructure.core.api.ApiRequestParameterHelper;
import org.mifosplatform.infrastructure.core.api.JsonQuery;
import org.mifosplatform.infrastructure.core.data.EntityIdentifier;
import org.mifosplatform.infrastructure.core.data.EnumOptionData;
import org.mifosplatform.infrastructure.core.exception.UnrecognizedQueryParamException;
import org.mifosplatform.infrastructure.core.serialization.ApiRequestJsonSerializationSettings;
import org.mifosplatform.infrastructure.core.serialization.DefaultToApiJsonSerializer;
import org.mifosplatform.infrastructure.core.serialization.FromJsonHelper;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.organisation.monetary.domain.MonetaryCurrency;
import org.mifosplatform.organisation.monetary.domain.Money;
import org.mifosplatform.organisation.staff.data.BulkTransferLoanOfficerData;
import org.mifosplatform.organisation.staff.data.StaffData;
import org.mifosplatform.organisation.staff.service.StaffReadPlatformService;
import org.mifosplatform.portfolio.charge.data.ChargeData;
import org.mifosplatform.portfolio.charge.service.ChargeReadPlatformService;
import org.mifosplatform.portfolio.fund.data.FundData;
import org.mifosplatform.portfolio.fund.service.FundReadPlatformService;
import org.mifosplatform.portfolio.loanaccount.data.DisbursementData;
import org.mifosplatform.portfolio.loanaccount.data.LoanAccountData;
import org.mifosplatform.portfolio.loanaccount.data.LoanBasicDetailsData;
import org.mifosplatform.portfolio.loanaccount.data.LoanChargeData;
import org.mifosplatform.portfolio.loanaccount.data.LoanPermissionData;
import org.mifosplatform.portfolio.loanaccount.data.LoanTransactionData;
import org.mifosplatform.portfolio.loanaccount.gaurantor.data.GuarantorData;
import org.mifosplatform.portfolio.loanaccount.gaurantor.service.GuarantorReadPlatformService;
import org.mifosplatform.portfolio.loanaccount.loanschedule.data.LoanScheduleData;
import org.mifosplatform.portfolio.loanaccount.loanschedule.service.LoanScheduleCalculationPlatformService;
import org.mifosplatform.portfolio.loanaccount.service.LoanReadPlatformService;
import org.mifosplatform.portfolio.loanproduct.data.LoanProductData;
import org.mifosplatform.portfolio.loanproduct.data.TransactionProcessingStrategyData;
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

    private final Set<String> LOAN_DATA_PARAMETERS = new HashSet<String>(Arrays.asList("id", "externalId", "clientId", "groupId",
            "clientName", "groupName", "fundId", "fundName", "loanProductId", "loanProductName", "loanProductDescription", "currency",
            "principal", "inArrearsTolerance", "numberOfRepayments", "repaymentEvery", "interestRatePerPeriod", "annualInterestRate",
            "repaymentFrequencyType", "interestRateFrequencyType", "amortizationType", "interestType", "interestCalculationPeriodType",
            "submittedOnDate", "approvedOnDate", "expectedDisbursementDate", "actualDisbursementDate", "expectedFirstRepaymentOnDate",
            "interestChargedFromDate", "closedOnDate", "expectedMaturityDate", "status", "lifeCycleStatusDate", "repaymentSchedule",
            "transactions", "permissions", "convenienceData", "charges", "productOptions", "amortizationTypeOptions",
            "interestTypeOptions", "interestCalculationPeriodTypeOptions", "repaymentFrequencyTypeOptions",
            "interestRateFrequencyTypeOptions", "fundOptions", "repaymentStrategyOptions", "chargeOptions", "loanOfficerId",
            "loanOfficerName", "loanOfficerOptions", "chargeTemplate"));

    private final String resourceNameForPermissions = "LOAN";

    private final PlatformSecurityContext context;
    private final LoanReadPlatformService loanReadPlatformService;
    private final LoanProductReadPlatformService loanProductReadPlatformService;
    private final LoanDropdownReadPlatformService dropdownReadPlatformService;
    private final FundReadPlatformService fundReadPlatformService;
    private final ChargeReadPlatformService chargeReadPlatformService;
    private final LoanScheduleCalculationPlatformService calculationPlatformService;
    private final StaffReadPlatformService staffReadPlatformService;
    private final GuarantorReadPlatformService guarantorReadPlatformService;
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
            final ChargeReadPlatformService chargeReadPlatformService,
            final LoanScheduleCalculationPlatformService calculationPlatformService,
            final StaffReadPlatformService staffReadPlatformService, final GuarantorReadPlatformService guarantorReadPlatformService,
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
        this.calculationPlatformService = calculationPlatformService;
        this.staffReadPlatformService = staffReadPlatformService;
        this.guarantorReadPlatformService = guarantorReadPlatformService;
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
    public String retrieveDetailsForNewLoanApplicationStepOne(@QueryParam("clientId") final Long clientId,
            @QueryParam("groupId") final Long groupId, @QueryParam("productId") final Long productId, @Context final UriInfo uriInfo) {

        context.authenticatedUser().validateHasReadPermission(resourceNameForPermissions);

        // tempate related
        Collection<LoanProductData> productOptions = this.loanProductReadPlatformService.retrieveAllLoanProductsForLookup();
        Collection<EnumOptionData> loanTermFrequencyTypeOptions = dropdownReadPlatformService.retrieveLoanTermFrequencyTypeOptions();
        Collection<EnumOptionData> repaymentFrequencyTypeOptions = dropdownReadPlatformService.retrieveRepaymentFrequencyTypeOptions();
        Collection<EnumOptionData> interestRateFrequencyTypeOptions = dropdownReadPlatformService
                .retrieveInterestRateFrequencyTypeOptions();

        Collection<EnumOptionData> amortizationTypeOptions = dropdownReadPlatformService.retrieveLoanAmortizationTypeOptions();
        Collection<EnumOptionData> interestTypeOptions = dropdownReadPlatformService.retrieveLoanInterestTypeOptions();
        Collection<EnumOptionData> interestCalculationPeriodTypeOptions = dropdownReadPlatformService
                .retrieveLoanInterestRateCalculatedInPeriodOptions();

        Collection<FundData> fundOptions = this.fundReadPlatformService.retrieveAllFunds();
        Collection<TransactionProcessingStrategyData> repaymentStrategyOptions = this.dropdownReadPlatformService
                .retreiveTransactionProcessingStrategies();

        final boolean feeChargesOnly = false;
        Collection<ChargeData> chargeOptions = this.chargeReadPlatformService.retrieveLoanApplicableCharges(feeChargesOnly);
        ChargeData chargeTemplate = this.chargeReadPlatformService.retrieveLoanChargeTemplate();

        LoanBasicDetailsData loanBasicDetails;
        Long officeId;

        if (clientId != null) {
            loanBasicDetails = this.loanReadPlatformService.retrieveClientAndProductDetails(clientId, productId);
            officeId = loanBasicDetails.getClientOfficeId();
        } else {
            loanBasicDetails = this.loanReadPlatformService.retrieveGroupAndProductDetails(groupId, productId);
            officeId = loanBasicDetails.getGroupOfficeId();
        }

        final boolean convenienceDataRequired = false;
        Collection<LoanChargeData> charges = loanBasicDetails.getCharges();

        Collection<StaffData> allowedLoanOfficers = this.staffReadPlatformService.retrieveAllLoanOfficersByOffice(officeId);

        final LoanAccountData newLoanAccount = new LoanAccountData(loanBasicDetails, convenienceDataRequired, null, null, null, charges,
                productOptions, loanTermFrequencyTypeOptions, repaymentFrequencyTypeOptions, repaymentStrategyOptions,
                interestRateFrequencyTypeOptions, amortizationTypeOptions, interestTypeOptions, interestCalculationPeriodTypeOptions,
                fundOptions, chargeOptions, chargeTemplate, allowedLoanOfficers, null);

        final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.toApiJsonSerializer.serialize(settings, newLoanAccount, LOAN_DATA_PARAMETERS);
    }

    @GET
    @Path("{loanId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveLoanAccountDetails(@PathParam("loanId") final Long loanId, @Context final UriInfo uriInfo) {

        context.authenticatedUser().validateHasReadPermission(resourceNameForPermissions);

        final LoanBasicDetailsData loanBasicDetails = this.loanReadPlatformService.retrieveLoanAccountDetails(loanId);

        int loanRepaymentsCount = 0;
        Collection<LoanTransactionData> loanRepayments = null;
        LoanScheduleData repaymentSchedule = null;
        LoanPermissionData permissions = null;
        Collection<LoanChargeData> charges = null;
        GuarantorData guarantorData = null;

        boolean convenienceDataRequired = false;
        final Set<String> associationParameters = ApiParameterHelper.extractAssociationsForResponseIfProvided(uriInfo.getQueryParameters());
        if (!associationParameters.isEmpty()) {

            if (associationParameters.contains("all")) {
                associationParameters.addAll(Arrays.asList("repaymentSchedule", "transactions", "permissions", "convenienceData",
                        "charges", "guarantor"));
            }

            boolean existsGuarantor = false;
            if (associationParameters.contains("guarantor")) {
                if (guarantorReadPlatformService.existsGuarantor(loanId)) {
                    guarantorData = this.guarantorReadPlatformService.retrieveGuarantor(loanId);
                    existsGuarantor = true;
                }
            }

            if (associationParameters.contains("transactions")) {
                final Collection<LoanTransactionData> currentLoanRepayments = this.loanReadPlatformService.retrieveLoanTransactions(loanId);
                if (!CollectionUtils.isEmpty(currentLoanRepayments)) {
                    loanRepayments = currentLoanRepayments;
                }
            }

            if (associationParameters.contains("repaymentSchedule") || associationParameters.contains("permissions")) {

                DisbursementData singleDisbursement = loanBasicDetails.toDisburementData();
                repaymentSchedule = this.loanReadPlatformService.retrieveRepaymentSchedule(loanId, loanBasicDetails.getCurrency(),
                        singleDisbursement, loanBasicDetails.getTotalDisbursementCharges(), loanBasicDetails.getInArrearsTolerance());

                convenienceDataRequired = true;
            }

            if (associationParameters.contains("permissions")) {
                // FIXME - KW - Waive feature was changed to waive interest at
                // anytime so this permission checking is probably not needed -
                // look into.
                final MonetaryCurrency currency = new MonetaryCurrency(loanBasicDetails.getCurrency().code(), loanBasicDetails
                        .getCurrency().decimalPlaces());
                final Money tolerance = Money.of(currency, loanBasicDetails.getInArrearsTolerance());

                final Money totalOutstandingMoney = Money.of(currency, repaymentSchedule.totalOutstanding());

                boolean isWaiveAllowed = totalOutstandingMoney.isGreaterThanZero()
                        && (tolerance.isGreaterThan(totalOutstandingMoney) || tolerance.isEqualTo(totalOutstandingMoney));

                loanRepaymentsCount = retrieveNonDisbursementTransactions(loanRepayments);
                permissions = this.loanReadPlatformService.retrieveLoanPermissions(loanBasicDetails, isWaiveAllowed,
                        loanRepaymentsCount, existsGuarantor);

                // clear parent data which wasn't requested
                if (!associationParameters.contains("repaymentSchedule")) {
                    repaymentSchedule = null;
                }
            }

            if (associationParameters.contains("charges")) {
                charges = this.chargeReadPlatformService.retrieveLoanCharges(loanId);
                if (CollectionUtils.isEmpty(charges)) {
                    charges = null; // set back to null so doesnt appear in JSON
                                    // is no charges exist.
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
        Collection<ChargeData> chargeOptions = null;
        ChargeData chargeTemplate = null;

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
            chargeTemplate = this.chargeReadPlatformService.retrieveLoanChargeTemplate();
        }

        final LoanAccountData loanAccount = new LoanAccountData(loanBasicDetails, convenienceDataRequired, repaymentSchedule,
                loanRepayments, permissions, charges, productOptions, loanTermFrequencyTypeOptions, repaymentFrequencyTypeOptions,
                repaymentStrategyOptions, interestRateFrequencyTypeOptions, amortizationTypeOptions, interestTypeOptions,
                interestCalculationPeriodTypeOptions, fundOptions, chargeOptions, chargeTemplate, null, guarantorData);

        final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.toApiJsonSerializer.serialize(settings, loanAccount, LOAN_DATA_PARAMETERS);
    }

    private int retrieveNonDisbursementTransactions(final Collection<LoanTransactionData> loanRepayments) {
        int loanRepaymentsCount = 0;
        if (!CollectionUtils.isEmpty(loanRepayments)) {
            for (LoanTransactionData transaction : loanRepayments) {
                if (transaction.isNotDisbursement()) {
                    // use this to decide if undo disbural should permission
                    // should be set to true.
                    loanRepaymentsCount++;
                }
            }
        }
        return loanRepaymentsCount;
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

        final EntityIdentifier result = this.commandsSourceWritePlatformService.logCommandSource("CREATE", "LOAN", "CREATE", "loans", null,
                apiRequestBodyAsJson);

        return this.toApiJsonSerializer.serialize(result);
    }

    @PUT
    @Path("{loanId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String modifyLoanApplication(@PathParam("loanId") final Long loanId, final String apiRequestBodyAsJson) {

        final EntityIdentifier result = this.commandsSourceWritePlatformService.logCommandSource("UPDATE", "LOAN", "UPDATE", "loans",
                loanId, apiRequestBodyAsJson);

        return this.toApiJsonSerializer.serialize(result);
    }

    @DELETE
    @Path("{loanId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String deleteLoanApplication(@PathParam("loanId") final Long loanId) {

        final EntityIdentifier result = this.commandsSourceWritePlatformService.logCommandSource("DELETE", "LOAN", "DELETE", "loans",
                loanId, "{}");

        return this.toApiJsonSerializer.serialize(result);
    }

    @POST
    @Path("{loanId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String stateTransitions(@PathParam("loanId") final Long loanId, @QueryParam("command") final String commandParam,
            final String apiRequestBodyAsJson) {

        EntityIdentifier result = null;

        if (is(commandParam, "reject")) {
            result = this.commandsSourceWritePlatformService.logCommandSource("REJECT", "LOAN", "N/A", "loans", loanId,
                    apiRequestBodyAsJson);
        } else if (is(commandParam, "withdrewbyclient")) {
            result = this.commandsSourceWritePlatformService.logCommandSource("WITHDRAW", "LOAN", "N/A", "loans", loanId,
                    apiRequestBodyAsJson);
        } else if (is(commandParam, "approve")) {
            result = this.commandsSourceWritePlatformService.logCommandSource("APPROVE", "LOAN", "N/A", "loans", loanId,
                    apiRequestBodyAsJson);
        } else if (is(commandParam, "disburse")) {
            result = this.commandsSourceWritePlatformService.logCommandSource("DISBURSE", "LOAN", "N/A", "loans", loanId,
                    apiRequestBodyAsJson);
        }

        if (is(commandParam, "undoapproval")) {
            result = this.commandsSourceWritePlatformService.logCommandSource("APPROVALUNDO", "LOAN", "N/A", "loans", loanId,
                    apiRequestBodyAsJson);
        } else if (is(commandParam, "undodisbursal")) {
            result = this.commandsSourceWritePlatformService.logCommandSource("DISBURSALUNDO", "LOAN", "N/A", "loans", loanId,
                    apiRequestBodyAsJson);
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

        final LoanBasicDetailsData loanBasicDetails = this.loanReadPlatformService.retrieveLoanAccountDetails(loanId);

        final Collection<StaffData> allowedLoanOfficers = this.staffReadPlatformService.retrieveAllLoanOfficersByOffice(loanBasicDetails
                .getOfficeId());
        final Long fromLoanOfficerId = loanBasicDetails.getLoanOfficerId();

        final BulkTransferLoanOfficerData loanReassignmentData = BulkTransferLoanOfficerData.template(fromLoanOfficerId,
                allowedLoanOfficers, new LocalDate());

        final Set<String> RESPONSE_DATA_PARAMETERS = new HashSet<String>(Arrays.asList("officeId", "fromLoanOfficerId", "assignmentDate",
                "officeOptions", "loanOfficerOptions", "accountSummaryCollection"));

        final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.loanOfficeTransferToApiJsonSerializer.serialize(settings, loanReassignmentData, RESPONSE_DATA_PARAMETERS);
    }

    @POST
    @Path("{loanId}/assign")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String assignLoanOfficer(@PathParam("loanId") final Long loanId, final String apiRequestBodyAsJson) {

        final EntityIdentifier result = this.commandsSourceWritePlatformService.logCommandSource("UPDATELOANOFFICER", "LOAN", "N/A",
                "loans", loanId, apiRequestBodyAsJson);

        return this.toApiJsonSerializer.serialize(result);
    }
}