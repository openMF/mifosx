/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.integrationtests.common;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;
import org.mifosplatform.integrationtests.common.accounting.Account;
import org.mifosplatform.integrationtests.common.accounting.AccountHelper;
import org.mifosplatform.integrationtests.common.loans.LoanApplicationTestBuilder;
import org.mifosplatform.integrationtests.common.loans.LoanProductTestBuilder;
import org.mifosplatform.integrationtests.common.loans.LoanStatusChecker;
import org.mifosplatform.integrationtests.common.loans.LoanTransactionHelper;
import org.mifosplatform.integrationtests.common.provisioning.ProvisioningHelper;
import org.mifosplatform.integrationtests.common.provisioning.ProvisioningTransactionHelper;

import com.google.gson.Gson;
import com.jayway.restassured.builder.RequestSpecBuilder;
import com.jayway.restassured.builder.ResponseSpecBuilder;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.specification.RequestSpecification;
import com.jayway.restassured.specification.ResponseSpecification;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class ProvisioningIntegrationTest {

    private static final String NONE = "1";
    private final static int LOANPRODUCTS_SIZE = 10;
    private final static int LOANS_SIZE = 5;

    private RequestSpecification requestSpec;
    private ResponseSpecification responseSpec;
    private AccountHelper accountHelper;
    private LoanTransactionHelper loanTransactionHelper;

    @Before
    public void setup() {
        Utils.initializeRESTAssured();
        this.requestSpec = new RequestSpecBuilder().setContentType(ContentType.JSON).build();
        this.requestSpec.header("Authorization", "Basic " + Utils.loginIntoServerAndGetBase64EncodedAuthenticationKey());
        this.responseSpec = new ResponseSpecBuilder().expectStatusCode(200).build();
        this.loanTransactionHelper = new LoanTransactionHelper(this.requestSpec, this.responseSpec);
        this.accountHelper = new AccountHelper(this.requestSpec, this.responseSpec);
        Assume.assumeTrue(!isAlreadyProvisioningEntriesCreated());
    }

    @Test
    public void testCreateProvisioningCriteria() {
        ProvisioningTransactionHelper transactionHelper = new ProvisioningTransactionHelper(requestSpec, responseSpec);
        ArrayList<Integer> loanProducts = new ArrayList<>(LOANPRODUCTS_SIZE);
        List<Integer> loans = new ArrayList<>();
        final Integer clientID = ClientHelper.createClient(this.requestSpec, this.responseSpec);
        ClientHelper.verifyClientCreatedOnServer(this.requestSpec, this.responseSpec, clientID);
        for (int i = 0; i < LOANPRODUCTS_SIZE; i++) {
            final Integer loanProductID = createLoanProduct(false, NONE);
            loanProducts.add(loanProductID);
            Assert.assertNotNull(loanProductID);
            final Integer loanID = applyForLoanApplication(clientID, loanProductID, null, null, "1,00,000.00");
            HashMap loanStatusHashMap = LoanStatusChecker.getStatusOfLoan(this.requestSpec, this.responseSpec, loanID);
            LoanStatusChecker.verifyLoanIsPending(loanStatusHashMap);
            loanStatusHashMap = this.loanTransactionHelper.approveLoan("20 September 2011", loanID);
            LoanStatusChecker.verifyLoanIsApproved(loanStatusHashMap);
            LoanStatusChecker.verifyLoanIsWaitingForDisbursal(loanStatusHashMap);
            System.out.println("-------------------------------DISBURSE LOAN-------------------------------------------");
            loanStatusHashMap = this.loanTransactionHelper.disburseLoan("20 September 2011", loanID);
            LoanStatusChecker.verifyLoanIsActive(loanStatusHashMap);
            loans.add(loanID);
            Assert.assertNotNull(loanID);
        }

        ArrayList categories = transactionHelper.retrieveAllProvisioningCategories();
        Assert.assertTrue(categories.size() > 0) ;
        Account liability = accountHelper.createLiabilityAccount() ;
        Account expense = accountHelper.createExpenseAccount() ;
        Map requestCriteria = ProvisioningHelper.createProvisioingCriteriaJson(loanProducts, categories, liability, expense);
        String provisioningCriteriaCreateJson = new Gson().toJson(requestCriteria);
        Integer criteriaId = transactionHelper.createProvisioningCriteria(provisioningCriteriaCreateJson);
        Assert.assertNotNull(criteriaId);

        Map newCriteria = transactionHelper.retrieveProvisioningCriteria(criteriaId) ;
        validateProvisioningCriteria(requestCriteria, newCriteria) ;
        
        ArrayList definitions = (ArrayList)newCriteria.get("definitions") ;
        for(int i = 0 ; i < definitions.size(); i++) {
            Map criteriadefinition = (Map) definitions.get(i) ;
            criteriadefinition.put("provisioningPercentage", new Integer(20)) ;
        }
        newCriteria.put("locale", "en");
        String updateCriteriaString = new Gson().toJson(newCriteria) ;
        Integer criteriaId1 = transactionHelper.updateProvisioningCriteria(criteriaId, updateCriteriaString) ;
        Map updatedCriteria = transactionHelper.retrieveProvisioningCriteria(criteriaId1) ;
        validateProvisioningCriteria(newCriteria, updatedCriteria) ;
        
        transactionHelper.deleteProvisioningCriteria(criteriaId1) ;
 
        categories = transactionHelper.retrieveAllProvisioningCategories();
        liability = accountHelper.createLiabilityAccount() ;
        expense = accountHelper.createExpenseAccount() ;
        requestCriteria = ProvisioningHelper.createProvisioingCriteriaJson(loanProducts, categories, liability, expense);
        provisioningCriteriaCreateJson = new Gson().toJson(requestCriteria);
        criteriaId = transactionHelper.createProvisioningCriteria(provisioningCriteriaCreateJson);
        Assert.assertNotNull(criteriaId);

        String provisioningEntryJson = ProvisioningHelper.createProvisioningEntryJson();
        Integer provisioningEntryId = transactionHelper.createProvisioningEntries(provisioningEntryJson);
        Assert.assertNotNull(provisioningEntryId);
        
        transactionHelper.updateProvisioningEntry("recreateprovisioningentry", provisioningEntryId, "") ;
        transactionHelper.updateProvisioningEntry("createjournalentry", provisioningEntryId, "") ;
        Map entry = transactionHelper.retrieveProvisioningEntry(provisioningEntryId) ;
        Assert.assertTrue((Boolean)entry.get("journalEntry")) ;
        Map provisioningEntry = transactionHelper.retrieveProvisioningEntries(provisioningEntryId) ;
        Assert.assertTrue(((ArrayList)provisioningEntry.get("pageItems")).size() > 0) ;
    }

    private void validateProvisioningCriteria(Map requestCriteria, Map newCriteria) {
        
        //criteria name validation
        String requestCriteriaName = (String)requestCriteria.get("criteriaName") ;
        String criteriaName = (String)newCriteria.get("criteriaName") ;
        Assert.assertEquals(criteriaName, requestCriteriaName) ;
        
        //loan products validation
        ArrayList requestProducts = (ArrayList)requestCriteria.get("loanProducts") ;
        ArrayList products = (ArrayList)newCriteria.get("loanProducts") ;
        Assert.assertEquals(products.size(), requestProducts.size()) ;
        
        ArrayList requestedDefinitions = (ArrayList)requestCriteria.get("definitions") ;
        ArrayList newdefintions = (ArrayList) newCriteria.get("definitions") ;
        Assert.assertEquals(newdefintions.size(), requestedDefinitions.size()) ;
        for(int i = 0 ; i < newdefintions.size() ; i++) {
            Map requestedMap = (Map)requestedDefinitions.get(i) ;
            Map newMap = (Map)newdefintions.get(i) ;
            checkProperty("categoryId", requestedMap, newMap) ;
            checkProperty("categoryName", requestedMap, newMap) ;
            checkProperty("minAge", requestedMap, newMap) ;
            checkProperty("maxAge", requestedMap, newMap) ;
            checkProperty("provisioningPercentage", requestedMap, newMap) ;
            checkProperty("liabilityAccount", requestedMap, newMap) ;
            checkProperty("expenseAccount", requestedMap, newMap) ;
        }
    }
    
    private void checkProperty(String propertyName, Map requestMap, Map newMap) {
        Object requested = requestMap.get(propertyName) ;
        Object modified = newMap.get(propertyName) ;
        Assert.assertEquals(requested, modified) ;
    }
    
    private Integer createLoanProduct(final boolean multiDisburseLoan, final String accountingRule, final Account... accounts) {
        System.out.println("------------------------------CREATING NEW LOAN PRODUCT ---------------------------------------");
        final String loanProductJSON = new LoanProductTestBuilder() //
                .withPrincipal("1,00,000.00") //
                .withNumberOfRepayments("4") //
                .withRepaymentAfterEvery("1") //
                .withRepaymentTypeAsMonth() //
                .withinterestRatePerPeriod("1") //
                .withInterestRateFrequencyTypeAsMonths() //
                .withAmortizationTypeAsEqualInstallments() //
                .withInterestTypeAsDecliningBalance() //
                .withTranches(multiDisburseLoan) //
                .withAccounting(accountingRule, accounts).build(null);
        return this.loanTransactionHelper.getLoanProductId(loanProductJSON);
    }

    private Integer applyForLoanApplication(final Integer clientID, final Integer loanProductID, List<HashMap> charges,
            final String savingsId, String principal) {
        System.out.println("--------------------------------APPLYING FOR LOAN APPLICATION--------------------------------");
        final String loanApplicationJSON = new LoanApplicationTestBuilder() //
                .withPrincipal(principal) //
                .withLoanTermFrequency("4") //
                .withLoanTermFrequencyAsMonths() //
                .withNumberOfRepayments("4") //
                .withRepaymentEveryAfter("1") //
                .withRepaymentFrequencyTypeAsMonths() //
                .withInterestRatePerPeriod("2") //
                .withAmortizationTypeAsEqualInstallments() //
                .withInterestTypeAsDecliningBalance() //
                .withInterestCalculationPeriodTypeSameAsRepaymentPeriod() //
                .withExpectedDisbursementDate("20 September 2011") //
                .withSubmittedOnDate("20 September 2011") //
                .withCharges(charges).build(clientID.toString(), loanProductID.toString(), savingsId);
        return this.loanTransactionHelper.getLoanId(loanApplicationJSON);
    }
    
    private boolean isAlreadyProvisioningEntriesCreated() {
        ProvisioningTransactionHelper transactionHelper = new ProvisioningTransactionHelper(requestSpec, responseSpec);
        Map entries = transactionHelper.retrieveAllProvisioningEntries() ;
        ArrayList<Map> pageItems = (ArrayList)entries.get("pageItems") ;
        boolean provisioningetryAlreadyCreated = false ;
        if(pageItems != null) {
            for(Map item: pageItems) {
                String date = (String)item.get("createdDate") ;
                DateFormat formatter = new SimpleDateFormat("MMM dd, yyyy");
                try {
                    Date date1 = formatter.parse(date) ;
                    DateFormat simple = new SimpleDateFormat("dd MMMM yyyy");
                    String formattedString = simple.format(new Date());
                    Date currentDate = simple.parse(formattedString) ;
                    if(date1.getTime() == currentDate.getTime()) {
                        provisioningetryAlreadyCreated = true ;
                        break ;
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }
        return provisioningetryAlreadyCreated ;
    }
}
