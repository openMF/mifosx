package org.mifosng.platform.api.infrastructure;

import java.util.Collection;
import java.util.Set;

import org.mifosng.platform.accounting.api.data.ChartOfAccountsData;
import org.mifosng.platform.api.commands.ClientCommand;
import org.mifosng.platform.api.data.AdditionalFieldsSetData;
import org.mifosng.platform.api.data.AppUserData;
import org.mifosng.platform.api.data.AuthenticatedUserData;
import org.mifosng.platform.api.data.ChargeData;
import org.mifosng.platform.api.data.ClientAccountSummaryCollectionData;
import org.mifosng.platform.api.data.ClientData;
import org.mifosng.platform.api.data.ClientIdentifierData;
import org.mifosng.platform.api.data.CodeData;
import org.mifosng.platform.api.data.ConfigurationData;
import org.mifosng.platform.api.data.DatatableData;
import org.mifosng.platform.api.data.DepositAccountData;
import org.mifosng.platform.api.data.DepositProductData;
import org.mifosng.platform.api.data.DocumentData;
import org.mifosng.platform.api.data.EntityIdentifier;
import org.mifosng.platform.api.data.FundData;
import org.mifosng.platform.api.data.GenericResultsetData;
import org.mifosng.platform.api.data.GroupAccountSummaryCollectionData;
import org.mifosng.platform.api.data.GroupData;
import org.mifosng.platform.api.data.GuarantorData;
import org.mifosng.platform.api.data.LoanAccountData;
import org.mifosng.platform.api.data.LoanChargeData;
import org.mifosng.platform.api.data.LoanProductData;
import org.mifosng.platform.api.data.LoanReassignmentData;
import org.mifosng.platform.api.data.LoanScheduleData;
import org.mifosng.platform.api.data.LoanTransactionData;
import org.mifosng.platform.api.data.CommandSourceData;
import org.mifosng.platform.api.data.NoteData;
import org.mifosng.platform.api.data.OfficeData;
import org.mifosng.platform.api.data.OfficeTransactionData;
import org.mifosng.platform.api.data.PermissionData;
import org.mifosng.platform.api.data.RoleData;
import org.mifosng.platform.api.data.RolePermissionData;
import org.mifosng.platform.api.data.SavingAccountData;
import org.mifosng.platform.api.data.SavingProductData;
import org.mifosng.platform.api.data.StaffData;

public interface PortfolioApiJsonSerializerService {

    String serializeAuthenticatedUserDataToJson(boolean prettyPrint, AuthenticatedUserData authenticatedUserData);

    String serializeGenericResultsetDataToJson(boolean prettyPrint, GenericResultsetData result);

    String serializeAdditionalFieldsSetDataToJson(boolean prettyPrint, Collection<AdditionalFieldsSetData> result);

    String serializeDatatableDataToJson(boolean prettyPrint, Collection<DatatableData> result);

    String serializePermissionDataToJson(boolean prettyPrint, Set<String> responseParameters, Collection<PermissionData> permissions);

    String serializeRoleDataToJson(boolean prettyPrint, Set<String> responseParameters, Collection<RoleData> roles);

    String serializeRoleDataToJson(boolean prettyPrint, Set<String> responseParameters, RoleData role);

    String serializeRolePermissionDataToJson(boolean prettyPrint, Set<String> responseParameters, RolePermissionData rolePermissionData);

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

    String serializeClientCommandToJson(ClientCommand command);

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
            LoanReassignmentData loanReassignmentData);

    String serializeChargeDataToJson(boolean prettyPrint, Set<String> responseParameters, Collection<ChargeData> charges);

    String serializeChargeDataToJson(boolean prettyPrint, Set<String> responseParameters, ChargeData charge);

    String serializeLoanChargeDataToJson(boolean prettyPrint, Set<String> responseParameters, LoanChargeData charge);

    String serializeStaffDataToJson(boolean prettyPrint, Set<String> responseParameters, StaffData staff);

    String serializeStaffDataToJson(boolean prettyPrint, Set<String> responseParameters, Collection<StaffData> staff);

    String serializeEntityIdentifier(EntityIdentifier identifier);

    String serializeClientIdentifierDataToJson(boolean prettyPrint, Set<String> responseParameters,
            Collection<ClientIdentifierData> clientIdentifiers);

    String serializeClientIdentifierDataToJson(boolean prettyPrint, Set<String> responseParameters,
            ClientIdentifierData clientIdentifierData);

    String serializeDocumentDataToJson(boolean prettyPrint, Set<String> responseParameters, Collection<DocumentData> documentDatas);

    String serializeDocumentDataToJson(boolean prettyPrint, Set<String> responseParameters, DocumentData documentData);

    String serializeCodeDataToJson(boolean prettyPrint, Set<String> responseParameters, Collection<CodeData> codes);

    String serializeCodeDataToJson(boolean prettyPrint, Set<String> responseParameters, CodeData code);

    String serializeMakerCheckerDataToJson(boolean prettyPrint, Set<String> responseParameters, Collection<CommandSourceData> entries);

    String serializeSavingAccountsDataToJson(boolean prettyPrint, Set<String> responseParameters, SavingAccountData account);

    String serializeSavingAccountsDataToJson(boolean prettyPrint, Set<String> responseParameters, Collection<SavingAccountData> accounts);

	// TODO - KW - might devide serialize up into portfolio and account to reduce interface
	String serializeChartOfAccountDataToJson(boolean prettyPrint, Set<String> responseParameters, ChartOfAccountsData chartOfAccounts);
	
	String serializeGuarantorDataToJson(boolean prettyPrint,Set<String> responseParameters, GuarantorData guarantorData);

}