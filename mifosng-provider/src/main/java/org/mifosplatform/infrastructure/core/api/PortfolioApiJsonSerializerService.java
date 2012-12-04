package org.mifosplatform.infrastructure.core.api;

import java.util.Collection;
import java.util.Set;

import org.mifosplatform.commands.data.CommandSourceData;
import org.mifosplatform.infrastructure.core.data.EntityIdentifier;

import org.mifosplatform.infrastructure.dataqueries.data.DatatableData;
import org.mifosplatform.infrastructure.dataqueries.data.GenericResultsetData;
import org.mifosplatform.infrastructure.documentmanagement.data.DocumentData;
import org.mifosplatform.organisation.monetary.data.ConfigurationData;
import org.mifosplatform.organisation.office.data.OfficeData;
import org.mifosplatform.organisation.office.data.OfficeTransactionData;
import org.mifosplatform.organisation.staff.data.BulkTransferLoanOfficerData;
import org.mifosplatform.organisation.staff.data.StaffData;
import org.mifosplatform.portfolio.charge.data.ChargeData;
import org.mifosplatform.portfolio.client.data.ClientAccountSummaryCollectionData;
import org.mifosplatform.portfolio.client.data.ClientData;
import org.mifosplatform.portfolio.client.data.ClientIdentifierData;
import org.mifosplatform.portfolio.client.data.NoteData;
import org.mifosplatform.portfolio.fund.data.FundData;
import org.mifosplatform.portfolio.group.data.GroupAccountSummaryCollectionData;
import org.mifosplatform.portfolio.group.data.GroupData;
import org.mifosplatform.portfolio.loanaccount.data.LoanAccountData;
import org.mifosplatform.portfolio.loanaccount.data.LoanChargeData;
import org.mifosplatform.portfolio.loanaccount.data.LoanTransactionData;
import org.mifosplatform.portfolio.loanaccount.gaurantor.data.GuarantorData;
import org.mifosplatform.portfolio.loanaccount.loanschedule.data.LoanScheduleData;
import org.mifosplatform.portfolio.loanproduct.data.LoanProductData;
import org.mifosplatform.portfolio.savingsaccount.data.SavingAccountData;
import org.mifosplatform.portfolio.savingsaccount.data.SavingScheduleData;
import org.mifosplatform.portfolio.savingsaccountproduct.data.SavingProductData;
import org.mifosplatform.portfolio.savingsdepositaccount.data.DepositAccountData;
import org.mifosplatform.portfolio.savingsdepositproduct.data.DepositProductData;
import org.mifosplatform.useradministration.data.AppUserData;
import org.mifosplatform.useradministration.data.AuthenticatedUserData;
import org.mifosplatform.useradministration.data.PermissionUsageData;
import org.mifosplatform.useradministration.data.RoleData;
import org.mifosplatform.useradministration.data.RolePermissionsData;

public interface PortfolioApiJsonSerializerService {

    String serializeAuthenticatedUserDataToJson(boolean prettyPrint, AuthenticatedUserData authenticatedUserData);

    String serializeGenericResultsetDataToJson(boolean prettyPrint, GenericResultsetData result);

    String serializeDatatableDataToJson(boolean prettyPrint, Collection<DatatableData> result);

    String serializePermissionDataToJson(boolean prettyPrint, Set<String> responseParameters, Collection<PermissionUsageData> permissions);

    String serializeRoleDataToJson(boolean prettyPrint, Set<String> responseParameters, Collection<RoleData> roles);

    String serializeRoleDataToJson(boolean prettyPrint, Set<String> responseParameters, RoleData role);

    String serializeRolePermissionDataToJson(boolean prettyPrint, Set<String> responseParameters, RolePermissionsData rolePermissionData);

    String serializeAppUserDataToJson(boolean prettyPrint, Set<String> responseParameters, Collection<AppUserData> users);

    String serializeAppUserDataToJson(boolean prettyPrint, Set<String> responseParameters, AppUserData user);

    String serializeOfficeDataToJson(boolean prettyPrint, Set<String> responseParameters, Collection<OfficeData> offices);

    String serializeOfficeDataToJson(boolean prettyPrint, Set<String> responseParameters, OfficeData office);

    String serializeOfficeTransactionDataToJson(boolean prettyPrint, Set<String> responseParameters,
            Collection<OfficeTransactionData> officeTransactions);

    String serializeOfficeTransactionDataToJson(boolean prettyPrint, Set<String> responseParameters, OfficeTransactionData officeTransaction);

    String serializeConfigurationDataToJson(boolean prettyPrint, Set<String> responseParameters, ConfigurationData configuration);

    String serializeFundDataToJson(boolean prettyPrint, Set<String> responseParameters, Collection<FundData> funds);

    String serializeFundDataToJson(boolean prettyPrint, Set<String> responseParameters, FundData fund);

    String serializeLoanProductDataToJson(boolean prettyPrint, Set<String> responseParameters, Collection<LoanProductData> products);

    String serializeLoanProductDataToJson(boolean prettyPrint, Set<String> responseParameters, LoanProductData loanProduct);

    String serializeSavingProductDataToJson(boolean prettyPrint, Set<String> responseParameters, Collection<SavingProductData> products);

    String serializeSavingProductDataToJson(boolean prettyPrint, Set<String> responseParameters, SavingProductData savingProduct);

    String serializeDepositProductDataToJson(boolean prettyPrint, Set<String> responseParameters, Collection<DepositProductData> products);

    String serializeDepositProductDataToJson(boolean prettyPrint, Set<String> responseParameters, DepositProductData depositProduct);

    String serializeDepositAccountDataToJson(boolean prettyPrint, Set<String> responseParameters, Collection<DepositAccountData> accounts);

    String serializeDepositAccountDataToJson(boolean prettyPrint, Set<String> responseParameters, DepositAccountData account);

    String serializeClientDataToJson(ClientData clientData);

    String serializeClientDataToJson(boolean prettyPrint, Set<String> responseParameters, ClientData clientData);

    String serializeClientDataToJson(boolean prettyPrint, Set<String> responseParameters, Collection<ClientData> clients);

    String serializeClientAccountSummaryCollectionDataToJson(boolean prettyPrint, Set<String> responseParameters,
            ClientAccountSummaryCollectionData clientAccount);

    String serializeGroupAccountSummaryCollectionDataToJson(boolean prettyPrint, Set<String> responseParameters,
            GroupAccountSummaryCollectionData groupAccount);

    String serializeGroupDataToJson(boolean prettyPrint, Set<String> responseParameters, Collection<GroupData> groups);

    String serializeGroupDataToJson(boolean prettyPrint, Set<String> responseParameters, GroupData group);

    String serializeNoteDataToJson(boolean prettyPrint, Set<String> responseParameters, Collection<NoteData> notes);

    String serializeNoteDataToJson(boolean prettyPrint, Set<String> responseParameters, NoteData note);

    String serializeLoanScheduleDataToJson(boolean prettyPrint, Set<String> responseParameters, LoanScheduleData loanSchedule);

    String serializeLoanAccountDataToJson(boolean prettyPrint, Set<String> responseParameters, LoanAccountData loanAccount);

    String serializeLoanTransactionDataToJson(boolean prettyPrint, Set<String> responseParameters, LoanTransactionData newTransactionData);

    String serializeLoanReassignmentDataToJson(boolean prettyPrint, Set<String> responseParameters,
            BulkTransferLoanOfficerData loanReassignmentData);

    String serializeChargeDataToJson(boolean prettyPrint, Set<String> responseParameters, Collection<ChargeData> charges);

    String serializeChargeDataToJson(boolean prettyPrint, Set<String> responseParameters, ChargeData charge);

    String serializeLoanChargeDataToJson(boolean prettyPrint, Set<String> responseParameters, LoanChargeData charge);

    String serializeStaffDataToJson(boolean prettyPrint, Set<String> responseParameters, StaffData staff);

    String serializeStaffDataToJson(boolean prettyPrint, Set<String> responseParameters, Collection<StaffData> staff);

    /**
     * @deprecated see
     *             {@link CodesToApiJsonSerializer#serialize(EntityIdentifier)}
     *             for example for what to use now.
     */
    @Deprecated
    String serializeEntityIdentifier(EntityIdentifier identifier);

    String serializeClientIdentifierDataToJson(boolean prettyPrint, Set<String> responseParameters,
            Collection<ClientIdentifierData> clientIdentifiers);

    String serializeClientIdentifierDataToJson(boolean prettyPrint, Set<String> responseParameters,
            ClientIdentifierData clientIdentifierData);

    String serializeDocumentDataToJson(boolean prettyPrint, Set<String> responseParameters, Collection<DocumentData> documentDatas);

    String serializeDocumentDataToJson(boolean prettyPrint, Set<String> responseParameters, DocumentData documentData);

    String serializeMakerCheckerDataToJson(boolean prettyPrint, Set<String> responseParameters, Collection<CommandSourceData> entries);

    String serializeSavingAccountsDataToJson(boolean prettyPrint, Set<String> responseParameters, SavingAccountData account);

    String serializeSavingAccountsDataToJson(boolean prettyPrint, Set<String> responseParameters, Collection<SavingAccountData> accounts);

    String serializeGuarantorDataToJson(boolean prettyPrint, Set<String> responseParameters, GuarantorData guarantorData);

	String serializeSavingScheduleDataToJson(boolean prettyPrint, Set<String> responseParameters, SavingScheduleData savingScheduleData);

}