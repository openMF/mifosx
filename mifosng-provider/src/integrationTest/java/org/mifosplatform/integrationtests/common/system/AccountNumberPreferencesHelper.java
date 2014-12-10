package org.mifosplatform.integrationtests.common.system;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;

import org.joda.time.LocalDate;
import org.mifosplatform.integrationtests.common.Utils;
import org.mifosplatform.integrationtests.common.loans.LoanRescheduleRequestTestBuilder;

import com.google.gson.Gson;
import com.jayway.restassured.builder.ResponseSpecBuilder;
import com.jayway.restassured.specification.RequestSpecification;
import com.jayway.restassured.specification.ResponseSpecification;

public class AccountNumberPreferencesHelper {

	private final RequestSpecification requestSpec;
    private final ResponseSpecification responseSpec;
    
    private static final String ACCOUNT_NUMBER_FORMATS_REQUEST_URL = "/mifosng-provider/api/v1/accountnumberformats";
    
    public AccountNumberPreferencesHelper(final RequestSpecification requestSpec, final ResponseSpecification responseSpec) {
        this.requestSpec = requestSpec;
        this.responseSpec = responseSpec;
    }
    
    public Object createClientAccountNumberPreference(ResponseSpecification responseSpec,String jsonAttributeToGetBack) {
    	System.out.println("---------------------------------CREATING CLIENT ACCOUNT NUMBER PREFERENCE------------------------------------------");

    	final String requestJSON = new AccountNumberPreferencesTestBuilder().clientBuild();

    	final String URL = ACCOUNT_NUMBER_FORMATS_REQUEST_URL + "?" + Utils.TENANT_IDENTIFIER;

    	return Utils.performServerPost(this.requestSpec, responseSpec, URL, requestJSON, jsonAttributeToGetBack);
    }
    
    public Object createLoanAccountNumberPreference(ResponseSpecification responseSpec,String jsonAttributeToGetBack) {
    	System.out.println("---------------------------------CREATING LOAN ACCOUNT NUMBER PREFERENCE------------------------------------------");

    	final String requestJSON = new AccountNumberPreferencesTestBuilder().loanBuild();

    	final String URL = ACCOUNT_NUMBER_FORMATS_REQUEST_URL + "?" + Utils.TENANT_IDENTIFIER; 
    	return Utils.performServerPost(this.requestSpec, responseSpec, URL, requestJSON, jsonAttributeToGetBack);
    }
    
    public Object createSavingsAccountNumberPreference(ResponseSpecification responseSpec,String jsonAttributeToGetBack) {
    	System.out.println("---------------------------------CREATING SAVINGS ACCOUNT NUMBER PREFERENCE------------------------------------------");

    	final String requestJSON = new AccountNumberPreferencesTestBuilder().savingsBuild();

    	final String URL = ACCOUNT_NUMBER_FORMATS_REQUEST_URL + "?" + Utils.TENANT_IDENTIFIER; 
    	return Utils.performServerPost(this.requestSpec, responseSpec, URL, requestJSON, jsonAttributeToGetBack);

    }
    
    public Object createAccountNumberPreferenceWithInvalidData(ResponseSpecification responseSpec,
    		String accountType,String prefixType,String jsonAttributeToGetBack){

    	final String requestJSON = new AccountNumberPreferencesTestBuilder().invalidDataBuild(accountType,prefixType);

    	final String URL = ACCOUNT_NUMBER_FORMATS_REQUEST_URL + "?" + Utils.TENANT_IDENTIFIER; 
    	return Utils.performServerPost(this.requestSpec, responseSpec, URL, requestJSON, jsonAttributeToGetBack);

    }
    
    public Object updateAccountNumberPreference(final Integer accountNumberFormatId,final String prefixType,ResponseSpecification responseSpec,String jsonAttributeToGetBack) {

    	final String requestJSON = new AccountNumberPreferencesTestBuilder().updatePrefixType(prefixType);

    	final String URL = ACCOUNT_NUMBER_FORMATS_REQUEST_URL + "/" + accountNumberFormatId + "?" + Utils.TENANT_IDENTIFIER;
    	
    	return Utils.performServerPut(this.requestSpec, responseSpec, URL, requestJSON, jsonAttributeToGetBack);
    	
    }
    
    public Object deleteAccountNumberPreference(final Integer accountNumberFormatId,ResponseSpecification responseSpec,String jsonAttributeToGetBack) {

    	System.out.println("---------------------------------DELETING ACCOUNT NUMBER PREFERENCE------------------------------------------");

    	final String URL = ACCOUNT_NUMBER_FORMATS_REQUEST_URL + "/" + accountNumberFormatId + "?" + Utils.TENANT_IDENTIFIER;
    	
    	return Utils.performServerDelete(this.requestSpec, responseSpec, URL,jsonAttributeToGetBack);
    }
    
    public Object getAccountNumberPreference(final Integer accountNumberFormatId, final String jsonAttributeToGetBack) {
    	final String URL = ACCOUNT_NUMBER_FORMATS_REQUEST_URL + "/" + accountNumberFormatId + "?" + Utils.TENANT_IDENTIFIER;
    	
    	return Utils.performServerGet(requestSpec, responseSpec, URL, jsonAttributeToGetBack);
    }
    
    public ArrayList getAllAccountNumberPreferences() {
    	final String URL = ACCOUNT_NUMBER_FORMATS_REQUEST_URL + "?" +  Utils.TENANT_IDENTIFIER; 	
    	final ArrayList<HashMap> response = Utils.performServerGet(requestSpec, responseSpec, URL, "");
    	return response;
    }
       
    public void verifyCreationOfAccountNumberPreferences(final Integer clientAccountNumberPreferenceId,final Integer loanAccountNumberPreferenceId,final Integer savingsAccountNumberPreferenceId) {
    	final String clientURL = ACCOUNT_NUMBER_FORMATS_REQUEST_URL + "/" + clientAccountNumberPreferenceId + "?" + Utils.TENANT_IDENTIFIER;
    	
    	final Integer clientId = Utils.performServerGet(requestSpec, responseSpec, clientURL, "id");
    	
    	final String loanURL = ACCOUNT_NUMBER_FORMATS_REQUEST_URL + "/" + loanAccountNumberPreferenceId + "?" + Utils.TENANT_IDENTIFIER;
    	
    	final Integer loanId = Utils.performServerGet(requestSpec, responseSpec, loanURL, "id");
    	
    	final String savingsURL = ACCOUNT_NUMBER_FORMATS_REQUEST_URL + "/" + savingsAccountNumberPreferenceId + "?" + Utils.TENANT_IDENTIFIER;
    	
    	final Integer savigsId = Utils.performServerGet(requestSpec, responseSpec, savingsURL, "id");
    }
    
    public void verifyUpdationOfAccountNumberPreferences(final Integer accountNumberPreferenceId){
    	final String URL = ACCOUNT_NUMBER_FORMATS_REQUEST_URL + "/" + accountNumberPreferenceId + "?" + Utils.TENANT_IDENTIFIER;
    	final Integer id = Utils.performServerGet(requestSpec, responseSpec, URL, "id");

    }
}
