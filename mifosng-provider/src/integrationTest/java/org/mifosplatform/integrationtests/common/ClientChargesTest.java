/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
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
    public void clientChargeTest() {

        // Creates clientCharge
        final Integer chargeId = ChargesHelper.createCharges(this.requestSpec, this.responseSpec,
                ChargesHelper.getChargeSpecifiedDueDateJSON());
        Assert.assertNotNull(chargeId);

        // creates client with activation date
        final Integer clientId = ClientHelper.createClient(this.requestSpec, this.responseSpec, "01 November 2012");
        Assert.assertNotNull(clientId);

        // creates added charge for client
        final Integer clientChargeId = ClientHelper.addChargesForClient(this.requestSpec, this.responseSpec, clientId,
                ClientHelper.getSpecifiedDueDateChargesClientAsJSON(chargeId.toString(), "29 October 2011"));
        Assert.assertNotNull(clientChargeId);

        // paid 10 USD charge
        final String responseId = ClientHelper.payChargesForClients(this.requestSpec, this.responseSpec, clientId, clientChargeId,
                ClientHelper.getPayChargeJSON("25 AUGUST 2015", "10"));
        Assert.assertNotNull(responseId);
        isValidOutstandingAmount(ClientHelper.getClientCharge(requestSpec, responseSpec, clientId.toString()), (float) 190.0);
        // undo paid charge transaction
        final Integer undoTrxnId = ClientHelper.undoTransaction(this.requestSpec, this.responseSpec, clientId.toString(),
                responseId.toString());
        Assert.assertNotNull(undoTrxnId);

        // check weather the transaction is undo or not
        isUndoTransaction(clientId.toString(), undoTrxnId.toString());

        // paid 20 USD charge
        ResponseSpecification responseSpecFailure = new ResponseSpecBuilder().expectStatusCode(400).build();
        final String responseId_futureDate_failure = ClientHelper.payChargesForClients(this.requestSpec, responseSpecFailure, clientId,
                clientChargeId, ClientHelper.getPayChargeJSON("28 AUGUST 2016", "20"));
        Assert.assertNull(responseId_futureDate_failure);

        // waived off 100 USD charge
        final String waiveChargeRsponseId = ClientHelper.waiveChargesForClients(this.requestSpec, this.responseSpec, clientId,
                clientChargeId, ClientHelper.getWaiveChargeJSON("100", clientChargeId.toString()));
        Assert.assertNotNull(waiveChargeRsponseId);

        // undo client charge transaction
        final Integer undoWaiveTrxnId = ClientHelper.undoTransaction(this.requestSpec, this.responseSpec, clientId.toString(),
                waiveChargeRsponseId.toString());
        Assert.assertNotNull(undoWaiveTrxnId);
        isUndoTransaction(clientId.toString(), undoWaiveTrxnId.toString());

        // pay charge before client activation date---failure test case
        final String responseId_activationDate_failure = ClientHelper.payChargesForClients(this.requestSpec, responseSpecFailure, clientId,
                clientChargeId, ClientHelper.getPayChargeJSON("30 October 2011", "20"));
        Assert.assertNull(responseId_activationDate_failure);

        // pay charge more than outstanding ---failure test case
        final String responseId_moreAmount_failure = ClientHelper.payChargesForClients(this.requestSpec, responseSpecFailure, clientId,
                clientChargeId, ClientHelper.getPayChargeJSON("25 AUGUST 2015", "300"));
        Assert.assertNull(responseId_moreAmount_failure);

        // create charge for loan
        final Integer loanChargeId = ChargesHelper.createCharges(this.requestSpec, this.responseSpec,
                ChargesHelper.getLoanSpecifiedDueDateJSON());
        Assert.assertNotNull(loanChargeId);

        // apply loan charge to client
        ResponseSpecification responseLoanChargeFailure = new ResponseSpecBuilder().expectStatusCode(403).build();
        final Integer clientLoanChargeId = ClientHelper.addChargesForClient(this.requestSpec, responseLoanChargeFailure, clientId,
                ClientHelper.getSpecifiedDueDateChargesClientAsJSON(loanChargeId.toString(), "29 October 2011"));
        Assert.assertNull(clientLoanChargeId);

        // paid 10 USD charge
        final String chargePaid_responseId = ClientHelper.payChargesForClients(this.requestSpec, this.responseSpec, clientId,
                clientChargeId, ClientHelper.getPayChargeJSON("25 AUGUST 2015", "100"));
        Assert.assertNotNull(chargePaid_responseId);

        // validate oustandingAmount
        isValidOutstandingAmount(ClientHelper.getClientCharge(requestSpec, responseSpec, clientId.toString()), (float) 100.0);

    }

    // undo transaction
    private void isUndoTransaction(String clientId, String transactionId) {
        final Boolean isReversed = ClientHelper.getClientTransactions(this.requestSpec, this.responseSpec, clientId.toString(),
                transactionId);
        Assert.assertTrue(isReversed);
    }

    private void isValidOutstandingAmount(Object outStandingAmount, Object expectedAmount) {
        Assert.assertEquals((float) outStandingAmount, expectedAmount);
    }

}
