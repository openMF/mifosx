package org.mifosplatform.integrationtests;

import com.jayway.restassured.builder.RequestSpecBuilder;
import com.jayway.restassured.builder.ResponseSpecBuilder;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.specification.RequestSpecification;
import com.jayway.restassured.specification.ResponseSpecification;

import static org.hamcrest.Matchers.hasItem;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.*;

import org.mifosplatform.integrationtests.common.ClientHelper;
import org.mifosplatform.integrationtests.common.CommonConstants;
import org.mifosplatform.integrationtests.common.LoanRescheduleRequestHelper;
import org.mifosplatform.integrationtests.common.loans.LoanApplicationTestBuilder;
import org.mifosplatform.integrationtests.common.loans.LoanProductTestBuilder;
import org.mifosplatform.integrationtests.common.loans.LoanRescheduleRequestTestBuilder;
import org.mifosplatform.integrationtests.common.loans.LoanTransactionHelper;
import org.mifosplatform.integrationtests.common.savings.SavingsProductHelper;
import org.mifosplatform.integrationtests.common.savings.SavingsAccountHelper;
import org.mifosplatform.integrationtests.common.system.AccountNumberPreferencesHelper;
import org.mifosplatform.integrationtests.common.system.CodeHelper;
import org.mifosplatform.integrationtests.common.Utils;

public class AccountNumberPreferencesTest {
	
	private ResponseSpecification statusOkResponseSpec;
    private RequestSpecification requestSpec;
    private ResponseSpecification responseSpec;
    private Integer clientId;
    private Integer loanProductId;
    private Integer loanId;
    private Integer savingsProductId;
    private Integer savingsId;
    private String loanPrincipalAmount = "100000.00";
    private String numberOfRepayments = "12";
    private String interestRatePerPeriod = "18";
    private String dateString = "4 September 2014";
    private String minBalanceForInterestCalculation = null;
    private String minRequiredBalance = null;
    private String enforceMinRequiredBalance = "false";
    private LoanTransactionHelper loanTransactionHelper;
    private SavingsProductHelper savingsProductHelper;
    private SavingsAccountHelper savingsAccountHelper;
    private AccountNumberPreferencesHelper accountNumberPreferencesHelper;
    private Integer clientAccountNumberPreferenceId;
    private Integer loanAccountNumberPreferenceId;
    private Integer savingsAccountNumberPreferenceId;
    private final String MINIMUM_OPENING_BALANCE = "1000.0";
    private final String ACCOUNT_TYPE_INDIVIDUAL = "INDIVIDUAL";  
    private Boolean isAccountPreferenceSetUp = false;
    private Integer clientTypeId;
    private String clientCodeValueName;
	private String clientCodeValue = "IND";
	private String clientTypeName = "CLIENT_TYPE";
	private String officeName = "OFFICE_NAME";
	private String loanShortName = "LOAN_PRODUCT_SHORT_NAME";
	private String savingsShortName = "SAVINGS_PRODUCT_SHORT_NAME";

    //private 

    @Before
    public void setup() {
        Utils.initializeRESTAssured();
        this.requestSpec = new RequestSpecBuilder().setContentType(ContentType.JSON).build();
        this.requestSpec.header("Authorization", "Basic " + Utils.loginIntoServerAndGetBase64EncodedAuthenticationKey());
        this.responseSpec = new ResponseSpecBuilder().expectStatusCode(200).build();
        this.loanTransactionHelper = new LoanTransactionHelper(this.requestSpec, this.responseSpec);
        this.accountNumberPreferencesHelper = new AccountNumberPreferencesHelper(this.requestSpec, this.responseSpec);

    }

    /** 
     * Creates the client, loan product,savings product and loan/savings entities 
     **/
    @Test
    public void createProducts(){
    	this.createLoanProduct();
    	this.createSavingsProduct();
    	this.createRequiredEntities(this.isAccountPreferenceSetUp);
    	this.testAccountNumberPreferences(this.isAccountPreferenceSetUp);
    }
    
    private void createRequiredEntities(Boolean isAccountPreferenceSetUp) {
    	this.createClientEntity(isAccountPreferenceSetUp);
    	this.createLoanEntity(isAccountPreferenceSetUp);
    	this.createSavingsEntity(isAccountPreferenceSetUp);
    }
    
    
    private void testAccountNumberPreferences(Boolean isAccountPreferenceSetUp){
    	this.isAccountPreferenceSetUp = true;
    	this.deleteAllAccountNumberPreferences();
    	this.createAccountNumberPreference();
    	this.createRequiredEntities(this.isAccountPreferenceSetUp);
    	this.updateAccountNumberPreference();
    	this.createRequiredEntities(this.isAccountPreferenceSetUp);
    }
    
    private void createAccountNumberPreference()
    {    	    	
    	this.clientAccountNumberPreferenceId = (Integer)this.accountNumberPreferencesHelper.createClientAccountNumberPreference(this.responseSpec,"resourceId");
    	System.out.println("Successfully created account number preferences for Client (ID: " + this.clientAccountNumberPreferenceId);

    	this.loanAccountNumberPreferenceId = (Integer)this.accountNumberPreferencesHelper.createLoanAccountNumberPreference(this.responseSpec,"resourceId");
    	System.out.println("Successfully created account number preferences for Loan (ID: " + this.loanAccountNumberPreferenceId );

    	this.savingsAccountNumberPreferenceId = (Integer)this.accountNumberPreferencesHelper.createSavingsAccountNumberPreference(this.responseSpec,"resourceId");
    	System.out.println("Successfully created account number preferences for Savings (ID: " + this.savingsAccountNumberPreferenceId);
    	
    	this.accountNumberPreferencesHelper.verifyCreationOfAccountNumberPreferences(this.clientAccountNumberPreferenceId,this.loanAccountNumberPreferenceId,this.savingsAccountNumberPreferenceId);
    	
    	/* Creating account Preference with invalid data should fail */
    	ResponseSpecification responseSpecForCreation = new ResponseSpecBuilder().expectStatusCode(400).build();

    	System.out.println("---------------------------------CREATING ACCOUNT NUMBER PREFERENCE WITH INVALID DATA------------------------------------------");

    	List<HashMap> creationError = (List<HashMap>) this.accountNumberPreferencesHelper.
    				createAccountNumberPreferenceWithInvalidData(responseSpecForCreation,"1000","1001",CommonConstants.RESPONSE_ERROR);
    	
    	if(creationError.get(0).get(CommonConstants.RESPONSE_ERROR_MESSAGE_CODE).equals("validation.msg.accountNumberFormat.accountType.is.not.within.expected.range")){
    	Assert.assertEquals("validation.msg.accountNumberFormat.accountType.is.not.within.expected.range",
    			creationError.get(0).get(CommonConstants.RESPONSE_ERROR_MESSAGE_CODE));
    	}else if(creationError.get(0).get(CommonConstants.RESPONSE_ERROR_MESSAGE_CODE).equals("validation.msg.accountNumberFormat.prefixType.is.not.one.of.expected.enumerations")){
    	Assert.assertEquals("validation.msg.accountNumberFormat.prefixType.is.not.one.of.expected.enumerations",
    			creationError.get(0).get(CommonConstants.RESPONSE_ERROR_MESSAGE_CODE));}
    	
    }
    
    private void updateAccountNumberPreference()
    {    	    	
    	Integer accountNumberPreferenceId = (Integer) this.accountNumberPreferencesHelper.updateAccountNumberPreference(this.clientAccountNumberPreferenceId,"101",this.responseSpec,"resourceId");
    	System.out.println("--------------------------UPDATION SUCCESSFUL FOR ACCOUNT NUMBER PREFERENCE ID " + accountNumberPreferenceId );
    	this.accountNumberPreferencesHelper.verifyUpdationOfAccountNumberPreferences(accountNumberPreferenceId);

    	/* Update invalid account preference id should fail */
    	System.out.println("---------------------------------UPDATING ACCOUNT NUMBER PREFERENCE WITH INVALID DATA------------------------------------------");

    	/* Invalid Account Type*/
    	ResponseSpecification responseSpecUpdation = new ResponseSpecBuilder().expectStatusCode(404).build();
    	List<HashMap> updationError = (List<HashMap>)this.accountNumberPreferencesHelper.updateAccountNumberPreference(5,"101",responseSpecUpdation,CommonConstants.RESPONSE_ERROR);
    	Assert.assertEquals("error.msg.account.number.format.id.invalid",
    			updationError.get(0).get(CommonConstants.RESPONSE_ERROR_MESSAGE_CODE));
    	
    	/* Invalid Prefix Type*/
    	ResponseSpecification responseSpecUpdation1 = new ResponseSpecBuilder().expectStatusCode(400).build();
    	List<HashMap> updationError1 = (List<HashMap>)this.accountNumberPreferencesHelper.updateAccountNumberPreference(this.clientAccountNumberPreferenceId,"11",responseSpecUpdation1,CommonConstants.RESPONSE_ERROR);
    	Assert.assertEquals("validation.msg.accountNumberFormat.prefixType.is.not.one.of.expected.enumerations",
        			updationError1.get(0).get(CommonConstants.RESPONSE_ERROR_MESSAGE_CODE));
    	 	    	
    }
    
    private void deleteAllAccountNumberPreferences(){
    	ArrayList<HashMap> preferenceIds = this.accountNumberPreferencesHelper.getAllAccountNumberPreferences();
    	/* Deletion of valid account preference ID */
    	for(HashMap preferenceId:preferenceIds){
            Integer id = (Integer) preferenceId.get("id");
            Integer resourceId = (Integer)this.accountNumberPreferencesHelper.deleteAccountNumberPreference(id,this.responseSpec,"resourceId");
        		System.out.println("Successfully deleted account number preference (ID: " + resourceId + ")");	
    	}
    	/* Deletion of invalid account preference ID should fail */
    	System.out.println("---------------------------------DELETING ACCOUNT NUMBER PREFERENCE WITH INVALID ID------------------------------------------");

    	ResponseSpecification responseSpecForDeletion = new ResponseSpecBuilder().expectStatusCode(404).build();
    	List<HashMap> deletionError = (List<HashMap>) this.accountNumberPreferencesHelper.deleteAccountNumberPreference(10,responseSpecForDeletion,CommonConstants.RESPONSE_ERROR);
    	Assert.assertEquals("error.msg.account.number.format.id.invalid",
    			deletionError.get(0).get(CommonConstants.RESPONSE_ERROR_MESSAGE_CODE));
    }

    private void createClientEntity(Boolean isAccountPreferenceSetUp) {
        this.responseSpec = new ResponseSpecBuilder().expectStatusCode(200).build();
        if(isAccountPreferenceSetUp){
        	this.clientId = this.createClientBasedOnAccountPreference();
        }else{
        	this.clientId = ClientHelper.createClient(this.requestSpec, this.responseSpec);
            Assert.assertNotNull(clientId);        
            String client = (String)ClientHelper.getClient(requestSpec, responseSpec, this.clientId.toString(), "accountNo");
            Assert.assertEquals(client.length(), 9);
        }
        ClientHelper.verifyClientCreatedOnServer(this.requestSpec, this.responseSpec, this.clientId);        
    }
    
    private Integer createClientBasedOnAccountPreference(){
    	Integer clientCodeValueId = null;
    	final String codeName = "ClientType";

    	String clientPrefixName = (String)this.accountNumberPreferencesHelper.getAccountNumberPreference(this.clientAccountNumberPreferenceId,"prefixType.value");
    	if(clientPrefixName.equals(this.clientTypeName)){
			ArrayList<HashMap> getAllCodes = (ArrayList)CodeHelper.getAllCodes(this.requestSpec, this.responseSpec);
			for(HashMap code:getAllCodes){
			    String name = (String) code.get("name");
			    Integer id = (Integer)code.get("id");
			    if(name.equals(codeName)){
			    	this.clientTypeId = id;
			    	clientCodeValueId = this.getCodeValueForEntity(this.clientTypeId,this.clientCodeValue);
			    	break;
			    }
			}
	    	this.clientId = (Integer)ClientHelper.createClientForAccountPreference(this.requestSpec, this.responseSpec,clientCodeValueId.toString(),"clientId");
	        Assert.assertNotNull(clientId);        
	        String client = (String)ClientHelper.getClient(requestSpec, responseSpec, this.clientId.toString(), "accountNo");
	        Assert.assertEquals(client.length(), this.clientCodeValueName.length()+9);
	        Assert.assertTrue(client.startsWith(this.clientCodeValueName));
	        /* Create Client with invalid Client Type Code Value */
	        ResponseSpecification responseSpecForClient = new ResponseSpecBuilder().expectStatusCode(404).build();
	        List<HashMap> clientError = (List<HashMap>)ClientHelper.createClientForAccountPreference(this.requestSpec, responseSpecForClient,"8900",CommonConstants.RESPONSE_ERROR);
	        Assert.assertEquals("error.msg.codevalue.codename.id.combination.invalid",
	        		clientError.get(0).get(CommonConstants.RESPONSE_ERROR_MESSAGE_CODE));

    	}
    	else if(clientPrefixName.equals(this.officeName)){
        	this.clientId = ClientHelper.createClient(this.requestSpec, this.responseSpec);
            Assert.assertNotNull(clientId);        
	        String client = (String)ClientHelper.getClient(requestSpec, responseSpec, this.clientId.toString(), "accountNo");
            String officeName = (String)ClientHelper.getClient(requestSpec, responseSpec, this.clientId.toString(), "officeName");
            Assert.assertEquals(client.length(), officeName.length()+9);
	        Assert.assertTrue(client.startsWith(officeName));
    	}

    	return this.clientId;
    }
    
    private Integer getCodeValueForEntity(Integer id,String codeValue){
    	final Integer codeValueId;
    	final List<HashMap> codeValuesList = (List) CodeHelper.getCodeValuesForCode(this.requestSpec, this.responseSpec,
    			id, "");
    	if(codeValuesList.size() == 0){
    		final Integer codeValuePosition = 0;
    		codeValueId = (Integer) CodeHelper.createCodeValue(this.requestSpec, this.responseSpec,
    				id, codeValue, codeValuePosition, "resourceId");

    	}
    	else{
    		codeValueId = (Integer)codeValuesList.get(0).get("id");
    	}
    	this.clientCodeValueName = (String)CodeHelper.getCodeValueById(this.requestSpec, this.responseSpec, id, codeValueId, "name");
    	return codeValueId;
    }

    private void createLoanProduct(){
        this.responseSpec = new ResponseSpecBuilder().expectStatusCode(200).build();

    	System.out.println("---------------------------------CREATING LOAN PRODUCT------------------------------------------");
    	
    	final String loanProductJSON = new LoanProductTestBuilder()
    			.withPrincipal(loanPrincipalAmount)
    			.withNumberOfRepayments(numberOfRepayments)
    			.withinterestRatePerPeriod(interestRatePerPeriod)
    			.withInterestRateFrequencyTypeAsYear()
    			.build(null);
    	
    	this.loanProductId = this.loanTransactionHelper.getLoanProductId(loanProductJSON);
    	System.out.println("Successfully created loan product  (ID: " + this.loanProductId + ")");
    }
    
    private void createLoanEntity(Boolean isAccountPreferenceSetUp){
        this.responseSpec = new ResponseSpecBuilder().expectStatusCode(200).build();

    	System.out.println("---------------------------------NEW LOAN APPLICATION------------------------------------------");
    	
    	final String loanApplicationJSON = new LoanApplicationTestBuilder()
    			.withPrincipal(loanPrincipalAmount)
    			.withLoanTermFrequency(numberOfRepayments)
                .withLoanTermFrequencyAsMonths()
                .withNumberOfRepayments(numberOfRepayments)
                .withRepaymentEveryAfter("1")
                .withRepaymentFrequencyTypeAsMonths()
                .withAmortizationTypeAsEqualInstallments()
                .withInterestCalculationPeriodTypeAsDays()
                .withInterestRatePerPeriod(interestRatePerPeriod)
                .withLoanTermFrequencyAsMonths()
                .withSubmittedOnDate(dateString)
                .withExpectedDisbursementDate(dateString)
                .withPrincipalGrace("2")
                .withInterestGrace("2")
    			.build(this.clientId.toString(), this.loanProductId.toString(), null);
    	
    	this.loanId = this.loanTransactionHelper.getLoanId(loanApplicationJSON);
    	String loan = (String)this.loanTransactionHelper.getLoanDetail(this.requestSpec, this.responseSpec, this.loanId, "accountNo");
    	if(isAccountPreferenceSetUp){
        	String loanPrefixName = (String)this.accountNumberPreferencesHelper.getAccountNumberPreference(this.loanAccountNumberPreferenceId,"prefixType.value");
	    	if(loanPrefixName.equals(this.officeName)){
	            String loanOfficeName = (String)ClientHelper.getClient(requestSpec, responseSpec, this.clientId.toString(), "officeName");
	        	Assert.assertEquals(loan.length(), loanOfficeName.length()+9);
	        	Assert.assertTrue(loan.startsWith(loanOfficeName));
	    	}
	    	else if(loanPrefixName.equals(this.loanShortName)){
	        	String loanShortName = (String)this.loanTransactionHelper.getLoanProductDetail(this.requestSpec, this.responseSpec, this.loanProductId, "shortName");
	        	Assert.assertEquals(loan.length(), loanShortName.length()+9);
	        	Assert.assertTrue(loan.startsWith(loanShortName));
	    	}
	    	System.out.println("SUCCESSFULLY CREATED LOAN APPLICATION BASED ON ACCOUNT PREFERENCES (ID: " + this.loanId + ")");
    	}
    	else{
    		System.out.println("SUCCESSFULLY CREATED LOAN APPLICATION (ID: " + loanId + ")");
    	}
    }
    
    private void createSavingsProduct(){
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
    private void createSavingsEntity(Boolean isAccountPreferenceSetUp){
        this.responseSpec = new ResponseSpecBuilder().expectStatusCode(200).build();

        this.savingsAccountHelper = new SavingsAccountHelper(this.requestSpec, this.responseSpec);
    	this.savingsId = this.savingsAccountHelper.applyForSavingsApplication(this.clientId, this.savingsProductId, ACCOUNT_TYPE_INDIVIDUAL);
    	String savings = (String)this.savingsAccountHelper.getSavingsAccountDetail(this.savingsId, "accountNo");
    	if(isAccountPreferenceSetUp){
        	String savingsPrefixName = (String)this.accountNumberPreferencesHelper.getAccountNumberPreference(this.savingsAccountNumberPreferenceId,"prefixType.value");
	    	if(savingsPrefixName.equals(this.officeName)){
	            String savingsOfficeName = (String)ClientHelper.getClient(requestSpec, responseSpec, this.clientId.toString(), "officeName");
	        	Assert.assertEquals(savings.length(), savingsOfficeName.length()+9);
	    	}
	    	else if(savingsPrefixName.equals(this.savingsShortName)){
	        	String loanShortName = (String)this.loanTransactionHelper.getLoanProductDetail(this.requestSpec, this.responseSpec, this.loanProductId, "shortName");
	        	Assert.assertEquals(savings.length(), loanShortName.length()+9);
	    	}
	    	System.out.println("SUCCESSFULLY CREATED SAVINGS APPLICATION BASED ON ACCOUNT PREFERENCES (ID: " + this.loanId + ")");
    	}
    else{
     	System.out.println("SUCCESSFULLY CREATED SAVINGS APPLICATION (ID: " + this.savingsId + ")");
    }
    }
}


