package org.mifosplatform.integrationtests.common;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mifosplatform.integrationtests.common.charges.ChargesHelper;

import com.jayway.restassured.builder.RequestSpecBuilder;
import com.jayway.restassured.builder.ResponseSpecBuilder;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.specification.RequestSpecification;
import com.jayway.restassured.specification.ResponseSpecification;

/**
 * 
 * IntegrationTest for ClientCharges.
 *
 */
public class ClientChargesTest {
    
    private ResponseSpecification responseSpec;
    private RequestSpecification requestSpec;

    @Before
    public void setup() {
        Utils.initializeRESTAssured();
        this.requestSpec = new RequestSpecBuilder().setContentType(ContentType.JSON).build();
        this.requestSpec.header("Authorization", "Basic " + Utils.loginIntoServerAndGetBase64EncodedAuthenticationKey());
        this.responseSpec = new ResponseSpecBuilder().expectStatusCode(200).build();
    }
    @Test
    public void createClientCharge_Success(){
        //Creates clientCharge
        final Integer chargeId = ChargesHelper.createCharges(this.requestSpec, this.responseSpec,ChargesHelper.getChargeSpecifiedDueDateJSON());
        Assert.assertNotNull(chargeId);
        
        //creates client with activation date
        final Integer clientId = ClientHelper.createClient(this.requestSpec, this.responseSpec,"01 November 2012");
        Assert.assertNotNull(clientId);
        
        //creates added charge for client
        final Integer clientChargeId = ClientHelper.addChargesForClient(this.requestSpec,this.responseSpec,clientId, 
                ClientHelper.getSpecifiedDueDateChargesClientAsJSON(chargeId.toString(), "29 October 2011"));
        Assert.assertNotNull(clientChargeId); 
        
        //paid 10 USD  charge
       final Integer responseId_10USD=ClientHelper.payChargesForClients(this.requestSpec, this.responseSpec,clientId, clientChargeId,
               ClientHelper.getPayChargeJSON("25 AUGUST 2015", "10"));
       Assert.assertNotNull(responseId_10USD); 
       
       
       //paid 20 USD charge
       ResponseSpecification requestSpecFailure = new ResponseSpecBuilder().expectStatusCode(400).build();
       final Integer responseId_futureDate_failure=ClientHelper.payChargesForClients(this.requestSpec, requestSpecFailure,clientId, 
                   clientChargeId, ClientHelper.getPayChargeJSON("27 AUGUST 2015", "20"));
           Assert.assertNull(responseId_futureDate_failure);
       
       
       //pay charge before client activation date---failure test case
       final Integer responseId_activationDate_failure=ClientHelper.payChargesForClients(this.requestSpec,requestSpecFailure,clientId, 
               clientChargeId, ClientHelper.getPayChargeJSON("30 October 2011", "20"));
       Assert.assertNull(responseId_activationDate_failure);
        
    }


}
