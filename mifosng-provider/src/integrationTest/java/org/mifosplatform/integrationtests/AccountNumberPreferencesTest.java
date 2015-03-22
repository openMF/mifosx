/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.integrationtests;

import java.util.ArrayList;
import java.util.HashMap;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mifosplatform.integrationtests.common.ClientHelper;
import org.mifosplatform.integrationtests.common.CommonConstants;
import org.mifosplatform.integrationtests.common.Utils;
import org.mifosplatform.integrationtests.common.loans.LoanApplicationTestBuilder;
import org.mifosplatform.integrationtests.common.loans.LoanProductTestBuilder;
import org.mifosplatform.integrationtests.common.loans.LoanTransactionHelper;
import org.mifosplatform.integrationtests.common.savings.SavingsAccountHelper;
import org.mifosplatform.integrationtests.common.savings.SavingsProductHelper;
import org.mifosplatform.integrationtests.common.system.AccountNumberPreferencesHelper;
import org.mifosplatform.integrationtests.common.system.CodeHelper;

import com.jayway.restassured.builder.RequestSpecBuilder;
import com.jayway.restassured.builder.ResponseSpecBuilder;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.specification.RequestSpecification;
import com.jayway.restassured.specification.ResponseSpecification;

public class AccountNumberPreferencesTest {

    private RequestSpecification requestSpec;
    private ResponseSpecification responseSpec;
    private ResponseSpecification responseValidationError;
    private ResponseSpecification responseNotFoundError;
    private ResponseSpecification responseForbiddenError;
    private Integer clientId;
    private Integer loanProductId;
    private Integer loanId;
    private Integer savingsProductId;
    private Integer savingsId;
    private final String loanPrincipalAmount = "100000.00";
    private final String numberOfRepayments = "12";
    private final String interestRatePerPeriod = "18";
    private final String dateString = "4 September 2014";
    private final String minBalanceForInterestCalculation = null;
    private final String minRequiredBalance = null;
    private final String enforceMinRequiredBalance = "false";
    private LoanTransactionHelper loanTransactionHelper;
    private SavingsAccountHelper savingsAccountHelper;
    private AccountNumberPreferencesHelper accountNumberPreferencesHelper;
    private Integer clientAccountNumberPreferenceId;
    private Integer loanAccountNumberPreferenceId;
    private Integer savingsAccountNumberPreferenceId;
    private final String MINIMUM_OPENING_BALANCE = "1000.0";
    private final String ACCOUNT_TYPE_INDIVIDUAL = "INDIVIDUAL";
    private Boolean isAccountPreferenceSetUp = false;
    private Integer clientTypeCodeId;
    private String clientCodeValueName;
    private Integer clientCodeValueId;
    private final String clientTypeName = "CLIENT_TYPE";
    private final String officeName = "OFFICE_NAME";
    private final String loanShortName = "LOAN_PRODUCT_SHORT_NAME";
    private final String savingsShortName = "SAVINGS_PRODUCT_SHORT_NAME";

    @Before
    public void setup() {
        Utils.initializeRESTAssured();
        this.requestSpec = new RequestSpecBuilder().setContentType(ContentType.JSON).build();
        this.requestSpec.header("Authorization", "Basic " + Utils.loginIntoServerAndGetBase64EncodedAuthenticationKey());
        this.responseSpec = new ResponseSpecBuilder().expectStatusCode(200).build();
        this.responseValidationError = new ResponseSpecBuilder().expectStatusCode(400).build();
        this.responseNotFoundError = new ResponseSpecBuilder().expectStatusCode(404).build();
        this.responseForbiddenError = new ResponseSpecBuilder().expectStatusCode(403).build();
        this.loanTransactionHelper = new LoanTransactionHelper(this.requestSpec, this.responseSpec);
        this.accountNumberPreferencesHelper = new AccountNumberPreferencesHelper(this.requestSpec, this.responseSpec);

    }

    @Test
    public void testAccountNumberPreferences() {

        /* Create Loan and Savings Product */
        this.createLoanAndSavingsProduct();

        /* Ensure no account number preferences are present in the system */
        this.deleteAllAccountNumberPreferences();

        /*
         * Validate the default account number generation rules for clients,
         * loans and savings accounts.
         */
        this.validateDefaultAccountNumberGeneration();

        /* Create and Validate account number preferences */
        this.createAccountNumberPreference();

        /*
         * Validate account number preference rules apply to Clients,Loans and
         * Saving Accounts
         */
        this.validateAccountNumberGenerationWithPreferences();

        /* Validate account number preferences Updation */
        this.updateAccountNumberPreference();

        /*
         * Validate account number preference rules apply to Clients,Loans and
         * Saving Accounts after Updation
         */
        this.validateAccountNumberGenerationWithPreferences();

        /* Delete all account number preferences */
        this.deleteAllAccountNumberPreferences();

    }

    private void createLoanAndSavingsProduct() {
        this.createLoanProduct();
        this.createSavingsProduct();
    }

    private void deleteAllAccountNumberPreferences() {
        ArrayList<HashMap<String, Object>> preferenceIds = this.accountNumberPreferencesHelper.getAllAccountNumberPreferences();
        /* Deletion of valid account preference ID */
        for (HashMap<String, Object> preferenceId : preferenceIds) {
            Integer id = (Integer) preferenceId.get("id");
            HashMap<String, Object> delResponse = this.accountNumberPreferencesHelper.deleteAccountNumberPreference(id, this.responseSpec,
                    "");
            System.out.println("Successfully deleted account number preference (ID: " + delResponse.get("resourceId") + ")");
        }
        /* Deletion of invalid account preference ID should fail */
        System.out
                .println("---------------------------------DELETING ACCOUNT NUMBER PREFERENCE WITH INVALID ID------------------------------------------");

        HashMap<String, Object> deletionError = this.accountNumberPreferencesHelper.deleteAccountNumberPreference(10,
                this.responseNotFoundError, "");
        Assert.assertEquals("error.msg.resource.not.found", deletionError.get(CommonConstants.RESPONSE_ERROR_MESSAGE_CODE));
    }

    private void validateDefaultAccountNumberGeneration() {
        this.createAndValidateClientEntity(this.isAccountPreferenceSetUp);
        this.createAndValidateLoanEntity(this.isAccountPreferenceSetUp);
        this.createAndValidateSavingsEntity(this.isAccountPreferenceSetUp);
    }

    private void validateAccountNumberGenerationWithPreferences() {
        this.isAccountPreferenceSetUp = true;
        this.createAndValidateClientEntity(this.isAccountPreferenceSetUp);
        this.createAndValidateLoanEntity(this.isAccountPreferenceSetUp);
        this.createAndValidateSavingsEntity(this.isAccountPreferenceSetUp);
    }

    private void createAccountNumberPreference() {
        this.clientAccountNumberPreferenceId = (Integer) this.accountNumberPreferencesHelper.createClientAccountNumberPreference(
                this.responseSpec, "resourceId");
        System.out.println("Successfully created account number preferences for Client (ID: " + this.clientAccountNumberPreferenceId);

        this.loanAccountNumberPreferenceId = (Integer) this.accountNumberPreferencesHelper.createLoanAccountNumberPreference(
                this.responseSpec, "resourceId");
        System.out.println("Successfully created account number preferences for Loan (ID: " + this.loanAccountNumberPreferenceId);

        this.savingsAccountNumberPreferenceId = (Integer) this.accountNumberPreferencesHelper.createSavingsAccountNumberPreference(
                this.responseSpec, "resourceId");
        System.out.println("Successfully created account number preferences for Savings (ID: " + this.savingsAccountNumberPreferenceId);

        this.accountNumberPreferencesHelper.verifyCreationOfAccountNumberPreferences(this.clientAccountNumberPreferenceId,
                this.loanAccountNumberPreferenceId, this.savingsAccountNumberPreferenceId, this.responseSpec, this.requestSpec);

        this.createAccountNumberPreferenceInvalidData("1000", "1001");
        this.createAccountNumberPreferenceDuplicateData("1", "101");

    }

    private void createAccountNumberPreferenceDuplicateData(final String accountType, final String prefixType) {
        /* Creating account Preference with duplicate data should fail */
        System.out
                .println("---------------------------------CREATING ACCOUNT NUMBER PREFERENCE WITH DUPLICATE DATA------------------------------------------");

        HashMap<String, Object> creationError = this.accountNumberPreferencesHelper.createAccountNumberPreferenceWithInvalidData(
                this.responseForbiddenError, accountType, prefixType, "");

        Assert.assertEquals("error.msg.account.number.format.duplicate.account.type",
                creationError.get(CommonConstants.RESPONSE_ERROR_MESSAGE_CODE));

    }

    private void createAccountNumberPreferenceInvalidData(final String accountType, final String prefixType) {

        /* Creating account Preference with invalid data should fail */
        System.out
                .println("---------------------------------CREATING ACCOUNT NUMBER PREFERENCE WITH INVALID DATA------------------------------------------");

        HashMap<String, Object> creationError = this.accountNumberPreferencesHelper.createAccountNumberPreferenceWithInvalidData(
                this.responseValidationError, accountType, prefixType, "");

        if (creationError.get(CommonConstants.RESPONSE_ERROR_MESSAGE_CODE).equals(
                "validation.msg.accountNumberFormat.accountType.is.not.within.expected.range")) {
            Assert.assertEquals("validation.msg.accountNumberFormat.accountType.is.not.within.expected.range",
                    creationError.get(CommonConstants.RESPONSE_ERROR_MESSAGE_CODE));
        } else if (creationError.get(CommonConstants.RESPONSE_ERROR_MESSAGE_CODE).equals(
                "validation.msg.accountNumberFormat.prefixType.is.not.one.of.expected.enumerations")) {
            Assert.assertEquals("validation.msg.accountNumberFormat.prefixType.is.not.one.of.expected.enumerations",
                    creationError.get(CommonConstants.RESPONSE_ERROR_MESSAGE_CODE));
        }
    }

    private void updateAccountNumberPreference() {
        HashMap<String, Object> accountNumberPreferences = this.accountNumberPreferencesHelper.updateAccountNumberPreference(
                this.clientAccountNumberPreferenceId, "101", this.responseSpec, "");

        System.out.println("--------------------------UPDATION SUCCESSFUL FOR ACCOUNT NUMBER PREFERENCE ID "
                + accountNumberPreferences.get("resourceId"));

        this.accountNumberPreferencesHelper.verifyUpdationOfAccountNumberPreferences((Integer) accountNumberPreferences.get("resourceId"),
                this.responseSpec, this.requestSpec);

        /* Update invalid account preference id should fail */
        System.out
                .println("---------------------------------UPDATING ACCOUNT NUMBER PREFERENCE WITH INVALID DATA------------------------------------------");

        /* Invalid Account Type */
        HashMap<String, Object> updationError = this.accountNumberPreferencesHelper.updateAccountNumberPreference(9999, "101",
                this.responseNotFoundError, "");
        if (updationError.get(CommonConstants.RESPONSE_ERROR_MESSAGE_CODE).equals("error.msg.resource.not.found")) {
            Assert.assertEquals("error.msg.resource.not.found", updationError.get(CommonConstants.RESPONSE_ERROR_MESSAGE_CODE));
        }
        /* Invalid Prefix Type */
        HashMap<String, Object> updationError1 = this.accountNumberPreferencesHelper.updateAccountNumberPreference(
                this.clientAccountNumberPreferenceId, "103", this.responseValidationError, "");

        Assert.assertEquals("validation.msg.validation.errors.exist", updationError1.get(CommonConstants.RESPONSE_ERROR_MESSAGE_CODE));

    }

    private void createAndValidateClientEntity(Boolean isAccountPreferenceSetUp) {
        this.responseSpec = new ResponseSpecBuilder().expectStatusCode(200).build();
        if (isAccountPreferenceSetUp) {
            this.createAndValidateClientBasedOnAccountPreference();
        } else {
            this.createAndValidateClientWithoutAccountPreference();
        }
    }

    private void createAndValidateClientWithoutAccountPreference() {
        this.clientId = ClientHelper.createClient(this.requestSpec, this.responseSpec);
        Assert.assertNotNull(this.clientId);
        String clientAccountNo = (String) ClientHelper.getClient(requestSpec, responseSpec, this.clientId.toString(), "accountNo");
        validateAccountNumberLengthAndStartsWithPrefix(clientAccountNo, null);
    }

    private void createAndValidateClientBasedOnAccountPreference() {
        final String codeName = "ClientType";
        String clientAccountNo = null;
        String clientPrefixName = (String) this.accountNumberPreferencesHelper.getAccountNumberPreference(
                this.clientAccountNumberPreferenceId, "prefixType.value");
        if (clientPrefixName.equals(this.clientTypeName)) {

            /* Retrieve Code id for the Code "ClientType" */
            HashMap<String, Object> code = CodeHelper.getCodeByName(this.requestSpec, this.responseSpec, codeName);
            this.clientTypeCodeId = (Integer) code.get("id");

            /* Retrieve/Create Code Values for the Code "ClientType" */
            HashMap<String, Object> codeValue = CodeHelper.retrieveOrCreateCodeValue(this.clientTypeCodeId, this.requestSpec,
                    this.responseSpec);

            this.clientCodeValueName = (String) codeValue.get("name");
            this.clientCodeValueId = (Integer) codeValue.get("id");

            /* Create Client with Client Type */
            this.clientId = ClientHelper.createClientForAccountPreference(this.requestSpec, this.responseSpec, this.clientCodeValueId,
                    "clientId");

            Assert.assertNotNull(clientId);

            clientAccountNo = (String) ClientHelper.getClient(this.requestSpec, this.responseSpec, this.clientId.toString(), "accountNo");
            this.validateAccountNumberLengthAndStartsWithPrefix(clientAccountNo, this.clientCodeValueName);

        } else if (clientPrefixName.equals(this.officeName)) {
            this.clientId = ClientHelper.createClient(this.requestSpec, this.responseSpec);
            Assert.assertNotNull(clientId);
            clientAccountNo = (String) ClientHelper.getClient(requestSpec, responseSpec, this.clientId.toString(), "accountNo");
            String officeName = (String) ClientHelper.getClient(requestSpec, responseSpec, this.clientId.toString(), "officeName");
            this.validateAccountNumberLengthAndStartsWithPrefix(clientAccountNo, officeName);
        }
    }

    private void validateAccountNumberLengthAndStartsWithPrefix(final String accountNumber, final String prefix) {
        if (prefix != null) {
            Assert.assertEquals(accountNumber.length(), prefix.length() + 9);
            Assert.assertTrue(accountNumber.startsWith(prefix));
        } else {
            Assert.assertEquals(accountNumber.length(), 9);
        }
    }

    private void createLoanProduct() {
        this.responseSpec = new ResponseSpecBuilder().expectStatusCode(200).build();

        System.out.println("---------------------------------CREATING LOAN PRODUCT------------------------------------------");

        final String loanProductJSON = new LoanProductTestBuilder().withPrincipal(loanPrincipalAmount)
                .withNumberOfRepayments(numberOfRepayments).withinterestRatePerPeriod(interestRatePerPeriod)
                .withInterestRateFrequencyTypeAsYear().build(null);

        this.loanProductId = this.loanTransactionHelper.getLoanProductId(loanProductJSON);
        System.out.println("Successfully created loan product  (ID: " + this.loanProductId + ")");
    }

    private void createAndValidateLoanEntity(Boolean isAccountPreferenceSetUp) {
        this.responseSpec = new ResponseSpecBuilder().expectStatusCode(200).build();

        System.out.println("---------------------------------NEW LOAN APPLICATION------------------------------------------");

        final String loanApplicationJSON = new LoanApplicationTestBuilder().withPrincipal(loanPrincipalAmount)
                .withLoanTermFrequency(numberOfRepayments).withLoanTermFrequencyAsMonths().withNumberOfRepayments(numberOfRepayments)
                .withRepaymentEveryAfter("1").withRepaymentFrequencyTypeAsMonths().withAmortizationTypeAsEqualInstallments()
                .withInterestCalculationPeriodTypeAsDays().withInterestRatePerPeriod(interestRatePerPeriod).withLoanTermFrequencyAsMonths()
                .withSubmittedOnDate(dateString).withExpectedDisbursementDate(dateString).withPrincipalGrace("2").withInterestGrace("2")
                .build(this.clientId.toString(), this.loanProductId.toString(), null);

        System.out.println("Loan Application :" + loanApplicationJSON);

        this.loanId = this.loanTransactionHelper.getLoanId(loanApplicationJSON);
        String loanAccountNo = (String) this.loanTransactionHelper.getLoanDetail(this.requestSpec, this.responseSpec, this.loanId,
                "accountNo");

        if (isAccountPreferenceSetUp) {
            String loanPrefixName = (String) this.accountNumberPreferencesHelper.getAccountNumberPreference(
                    this.loanAccountNumberPreferenceId, "prefixType.value");
            if (loanPrefixName.equals(this.officeName)) {
                String loanOfficeName = (String) ClientHelper.getClient(requestSpec, responseSpec, this.clientId.toString(), "officeName");
                this.validateAccountNumberLengthAndStartsWithPrefix(loanAccountNo, loanOfficeName);
            } else if (loanPrefixName.equals(this.loanShortName)) {
                String loanShortName = (String) this.loanTransactionHelper.getLoanProductDetail(this.requestSpec, this.responseSpec,
                        this.loanProductId, "shortName");
                this.validateAccountNumberLengthAndStartsWithPrefix(loanAccountNo, loanShortName);
            }
            System.out.println("SUCCESSFULLY CREATED LOAN APPLICATION BASED ON ACCOUNT PREFERENCES (ID: " + this.loanId + ")");
        } else {
            this.validateAccountNumberLengthAndStartsWithPrefix(loanAccountNo, null);
            System.out.println("SUCCESSFULLY CREATED LOAN APPLICATION (ID: " + loanId + ")");
        }
    }

    private void createSavingsProduct() {
        this.responseSpec = new ResponseSpecBuilder().expectStatusCode(200).build();

        System.out.println("------------------------------CREATING NEW SAVINGS PRODUCT ---------------------------------------");

        SavingsProductHelper savingsProductHelper = new SavingsProductHelper();

        final String savingsProductJSON = savingsProductHelper
                //
                .withInterestCompoundingPeriodTypeAsDaily()
                //
                .withInterestPostingPeriodTypeAsMonthly()
                //
                .withInterestCalculationPeriodTypeAsDailyBalance()
                //
                .withMinBalanceForInterestCalculation(minBalanceForInterestCalculation)
                //
                .withMinRequiredBalance(minRequiredBalance).withEnforceMinRequiredBalance(enforceMinRequiredBalance)
                .withMinimumOpenningBalance(this.MINIMUM_OPENING_BALANCE).build();
        this.savingsProductId = SavingsProductHelper.createSavingsProduct(savingsProductJSON, this.requestSpec, this.responseSpec);
        System.out.println("Sucessfully created savings product (ID: " + this.savingsProductId + ")");

    }

    private void createAndValidateSavingsEntity(Boolean isAccountPreferenceSetUp) {
        this.responseSpec = new ResponseSpecBuilder().expectStatusCode(200).build();

        this.savingsAccountHelper = new SavingsAccountHelper(this.requestSpec, this.responseSpec);

        this.savingsId = this.savingsAccountHelper
                .applyForSavingsApplication(this.clientId, this.savingsProductId, ACCOUNT_TYPE_INDIVIDUAL);

        String savingsAccountNo = (String) this.savingsAccountHelper.getSavingsAccountDetail(this.savingsId, "accountNo");

        if (isAccountPreferenceSetUp) {
            String savingsPrefixName = (String) this.accountNumberPreferencesHelper.getAccountNumberPreference(
                    this.savingsAccountNumberPreferenceId, "prefixType.value");

            if (savingsPrefixName.equals(this.officeName)) {
                String savingsOfficeName = (String) ClientHelper.getClient(requestSpec, responseSpec, this.clientId.toString(),
                        "officeName");
                this.validateAccountNumberLengthAndStartsWithPrefix(savingsAccountNo, savingsOfficeName);
            } else if (savingsPrefixName.equals(this.savingsShortName)) {
                String loanShortName = (String) this.savingsAccountHelper.getSavingsAccountDetail(this.savingsId, "shortName");
                this.validateAccountNumberLengthAndStartsWithPrefix(savingsAccountNo, loanShortName);
            }
            System.out.println("SUCCESSFULLY CREATED SAVINGS APPLICATION BASED ON ACCOUNT PREFERENCES (ID: " + this.loanId + ")");
        } else {
            this.validateAccountNumberLengthAndStartsWithPrefix(savingsAccountNo, null);
            System.out.println("SUCCESSFULLY CREATED SAVINGS APPLICATION (ID: " + this.savingsId + ")");
        }
    }
}
