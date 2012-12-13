package org.mifosplatform.portfolio.loanaccount.api;

import java.util.Arrays;
import java.util.Collection;
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
import org.mifosplatform.infrastructure.core.api.ApiParameterHelper;
import org.mifosplatform.infrastructure.core.api.ApiRequestParameterHelper;
import org.mifosplatform.infrastructure.core.api.PortfolioApiDataConversionService;
import org.mifosplatform.infrastructure.core.api.PortfolioApiJsonSerializerService;
import org.mifosplatform.infrastructure.core.data.EntityIdentifier;
import org.mifosplatform.infrastructure.core.data.EnumOptionData;
import org.mifosplatform.infrastructure.core.serialization.ApiRequestJsonSerializationSettings;
import org.mifosplatform.infrastructure.core.serialization.DefaultToApiJsonSerializer;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.organisation.staff.data.StaffData;
import org.mifosplatform.organisation.staff.service.StaffReadPlatformService;
import org.mifosplatform.portfolio.charge.data.ChargeData;
import org.mifosplatform.portfolio.charge.service.ChargeReadPlatformService;
import org.mifosplatform.portfolio.client.data.ClientLookup;
import org.mifosplatform.portfolio.fund.data.FundData;
import org.mifosplatform.portfolio.fund.service.FundReadPlatformService;
import org.mifosplatform.portfolio.group.service.GroupReadPlatformService;
import org.mifosplatform.portfolio.loanaccount.command.GroupLoanApplicationCommand;
import org.mifosplatform.portfolio.loanaccount.data.DisbursementData;
import org.mifosplatform.portfolio.loanaccount.data.GroupLoanAccountData;
import org.mifosplatform.portfolio.loanaccount.data.GroupLoanBasicDetailsData;
import org.mifosplatform.portfolio.loanaccount.data.LoanAccountData;
import org.mifosplatform.portfolio.loanaccount.data.LoanBasicDetailsData;
import org.mifosplatform.portfolio.loanaccount.data.LoanChargeData;
import org.mifosplatform.portfolio.loanaccount.data.LoanPermissionData;
import org.mifosplatform.portfolio.loanaccount.loanschedule.command.CalculateLoanScheduleCommand;
import org.mifosplatform.portfolio.loanaccount.loanschedule.data.LoanScheduleData;
import org.mifosplatform.portfolio.loanaccount.loanschedule.service.CalculationPlatformService;
import org.mifosplatform.portfolio.loanaccount.service.LoanReadPlatformService;
import org.mifosplatform.portfolio.loanaccount.service.LoanWritePlatformService;
import org.mifosplatform.portfolio.loanproduct.data.LoanProductData;
import org.mifosplatform.portfolio.loanproduct.data.TransactionProcessingStrategyData;
import org.mifosplatform.portfolio.loanproduct.service.LoanDropdownReadPlatformService;
import org.mifosplatform.portfolio.loanproduct.service.LoanProductReadPlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Path("/grouploans")
@Component
@Scope("singleton")
public class GroupLoansApiResource {

    private final Set<String> GROUP_LOAN_DATA_TEMPLATE_PARAMETERS = new HashSet<String>(Arrays.asList("id", "externalId", "groupId",
            "groupName", "fundId", "fundName", "loanProductId", "loanProductName", "loanProductDescription", "currency", "principal",
            "inArrearsTolerance", "numberOfRepayments", "repaymentEvery", "interestRatePerPeriod", "annualInterestRate",
            "repaymentFrequencyType", "interestRateFrequencyType", "amortizationType", "interestType", "interestCalculationPeriodType",
            "submittedOnDate", "approvedOnDate", "expectedDisbursementDate", "actualDisbursementDate", "expectedFirstRepaymentOnDate",
            "interestChargedFromDate", "closedOnDate", "expectedMaturityDate", "status", "lifeCycleStatusDate", "repaymentSchedule",
            "charges", "productOptions", "amortizationTypeOptions", "interestTypeOptions", "interestCalculationPeriodTypeOptions",
            "repaymentFrequencyTypeOptions", "interestRateFrequencyTypeOptions", "fundOptions", "repaymentStrategyOptions",
            "chargeOptions", "loanOfficerId", "loanOfficerName", "loanOfficerOptions", "chargeTemplate", "allowedClients"));

    private final Set<String> GROUP_LOAN_DATA_PARAMETERS = new HashSet<String>(Arrays.asList("id", "externalId", "groupId", "groupName",
            "fundId", "fundName", "loanProductId", "loanProductName", "loanProductDescription", "principal", "inArrearsTolerance",
            "status", "submittedOnDate", "approvedOnDate", "expectedDisbursementDate", "actualDisbursementDate",
            "expectedFirstRepaymentOnDate", "interestChargedFromDate", "closedOnDate", "expectedMaturityDate", "status",
            "lifeCycleStatusDate", "repaymentSchedule", "charges", "loanOfficerId", "loanOfficerName", "loanMembers", "permissions"));

    private final PlatformSecurityContext context;
    private final LoanProductReadPlatformService loanProductReadPlatformService;
    private final LoanDropdownReadPlatformService dropdownReadPlatformService;
    private final FundReadPlatformService fundReadPlatformService;
    private final ChargeReadPlatformService chargeReadPlatformService;
    private final LoanReadPlatformService loanReadPlatformService;
    private final LoanWritePlatformService loanWritePlatformService;
    private final CalculationPlatformService calculationPlatformService;
    private final GroupReadPlatformService groupReadPlatformService;
    private final StaffReadPlatformService staffReadPlatformService;
    private final PortfolioApiJsonSerializerService apiJsonSerializerService;
    private final PortfolioApiDataConversionService apiDataConversionService;
    private final DefaultToApiJsonSerializer<LoanAccountData> templateToApiJsonSerializer;
    private final DefaultToApiJsonSerializer<GroupLoanAccountData> toApiJsonSerializer;
    private final ApiRequestParameterHelper apiRequestParameterHelper;

    @Autowired
    public GroupLoansApiResource(final PlatformSecurityContext context, final LoanReadPlatformService loanReadPlatformService,
            final LoanWritePlatformService loanWritePlatformService, final LoanProductReadPlatformService loanProductReadPlatformService,
            final LoanDropdownReadPlatformService dropdownReadPlatformService, final FundReadPlatformService fundReadPlatformService,
            final ChargeReadPlatformService chargeReadPlatformService, final CalculationPlatformService calculationPlatformService,
            final StaffReadPlatformService staffReadPlatformService,
            final DefaultToApiJsonSerializer<LoanAccountData> templateToApiJsonSerializer,
            final DefaultToApiJsonSerializer<GroupLoanAccountData> toApiJsonSerializer,
            final PortfolioApiJsonSerializerService apiJsonSerializerService, final ApiRequestParameterHelper apiRequestParameterHelper,
            final PortfolioApiDataConversionService apiDataConversionService, final GroupReadPlatformService groupReadPlatformService) {
        this.context = context;
        this.loanProductReadPlatformService = loanProductReadPlatformService;
        this.dropdownReadPlatformService = dropdownReadPlatformService;
        this.fundReadPlatformService = fundReadPlatformService;
        this.chargeReadPlatformService = chargeReadPlatformService;
        this.loanReadPlatformService = loanReadPlatformService;
        this.loanWritePlatformService = loanWritePlatformService;
        this.calculationPlatformService = calculationPlatformService;
        this.groupReadPlatformService = groupReadPlatformService;
        this.staffReadPlatformService = staffReadPlatformService;
        this.apiJsonSerializerService = apiJsonSerializerService;
        this.apiDataConversionService = apiDataConversionService;
        this.templateToApiJsonSerializer = templateToApiJsonSerializer;
        this.toApiJsonSerializer = toApiJsonSerializer;
        this.apiRequestParameterHelper = apiRequestParameterHelper;
    }

    @GET
    @Path("template")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveDetailsForNewLoanApplicationStepOne(@QueryParam("groupId") final Long groupId,
            @QueryParam("productId") final Long productId, @Context final UriInfo uriInfo) {

        context.authenticatedUser().validateHasReadPermission("LOAN");

        // template related

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

        LoanBasicDetailsData loanBasicDetails = this.loanReadPlatformService.retrieveGroupAndProductDetails(groupId, productId);
        Long officeId = loanBasicDetails.getGroupOfficeId();

        final boolean convenienceDataRequired = false;
        Collection<LoanChargeData> charges = loanBasicDetails.getCharges();

        Collection<StaffData> allowedLoanOfficers = this.staffReadPlatformService.retrieveAllLoanOfficersByOffice(officeId);
        Collection<ClientLookup> allowedClients = this.groupReadPlatformService.retrieveClientMembers(groupId);

        final LoanAccountData newLoanAccount = new LoanAccountData(loanBasicDetails, convenienceDataRequired, null, null, null, charges,
                productOptions, loanTermFrequencyTypeOptions, repaymentFrequencyTypeOptions, repaymentStrategyOptions,
                interestRateFrequencyTypeOptions, amortizationTypeOptions, interestTypeOptions, interestCalculationPeriodTypeOptions,
                fundOptions, chargeOptions, chargeTemplate, allowedLoanOfficers, null, allowedClients);

        final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.templateToApiJsonSerializer.serialize(settings, newLoanAccount, GROUP_LOAN_DATA_TEMPLATE_PARAMETERS);
    }

    @GET
    @Path("{groupLoanId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveLoanAccountDetails(@PathParam("groupLoanId") final Long groupLoanId, @Context final UriInfo uriInfo) {

        context.authenticatedUser().validateHasReadPermission("LOAN");

        GroupLoanBasicDetailsData groupLoanBasicDetails = this.loanReadPlatformService.retrieveGroupLoanAccountDetails(groupLoanId);
        Collection<LoanBasicDetailsData> membersAccountsDetails = null;
        LoanScheduleData repaymentSchedule = null;

        final Set<String> associationParameters = ApiParameterHelper.extractAssociationsForResponseIfProvided(uriInfo.getQueryParameters());

        if (!associationParameters.isEmpty()) {
            if (associationParameters.contains("all")) {
                associationParameters.addAll(Arrays.asList("repaymentSchedule", "charges", "loanMembers"));
            }

            if (associationParameters.contains("loanMembers")) {
                membersAccountsDetails = this.loanReadPlatformService.retrieveGroupLoanMembersAccountsDetails(groupLoanId);
            }

            if (associationParameters.contains("repaymentSchedule")) {
                DisbursementData singleDisbursement = groupLoanBasicDetails.toDisbursementData();
                repaymentSchedule = this.loanReadPlatformService.retrieveGroupRepaymentSchedule(groupLoanId,
                        groupLoanBasicDetails.getCurrency(), singleDisbursement, groupLoanBasicDetails.getInArrearsTolerance());
            }
        }

        GroupLoanAccountData groupLoanAccount = new GroupLoanAccountData(groupLoanBasicDetails, membersAccountsDetails, repaymentSchedule);

        final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.toApiJsonSerializer.serialize(settings, groupLoanAccount, GROUP_LOAN_DATA_TEMPLATE_PARAMETERS);
    }

    @POST
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String calculateLoanScheduleOrSubmitLoanApplication(@QueryParam("command") final String commandParam,
            @Context final UriInfo uriInfo, final String jsonRequestBody) {

        context.authenticatedUser().validateHasReadPermission("LOAN");

        final GroupLoanApplicationCommand command = this.apiDataConversionService.convertJsonToGroupLoanApplicationCommand(null,
                jsonRequestBody);
        if (is(commandParam, "calculateLoanSchedule")) {
            CalculateLoanScheduleCommand calculateLoanScheduleCommand = command.toCalculateLoanScheduleCommand();
            return calculateLoanSchedule(uriInfo, calculateLoanScheduleCommand);
        }

        final EntityIdentifier identifier = this.loanWritePlatformService.submitGroupLoanApplication(command);

        return this.templateToApiJsonSerializer.serialize(identifier);
    }

    private String calculateLoanSchedule(final UriInfo uriInfo, final CalculateLoanScheduleCommand command) {

        final LoanScheduleData loanSchedule = this.calculationPlatformService.calculateLoanSchedule(command);

        final Set<String> responseParameters = ApiParameterHelper.extractFieldsForResponseIfProvided(uriInfo.getQueryParameters());
        boolean prettyPrint = ApiParameterHelper.prettyPrint(uriInfo.getQueryParameters());

        return this.apiJsonSerializerService.serializeLoanScheduleDataToJson(prettyPrint, responseParameters, loanSchedule);
    }

    private boolean is(final String commandParam, final String commandValue) {
        return StringUtils.isNotBlank(commandParam) && commandParam.trim().equalsIgnoreCase(commandValue);
    }
}
