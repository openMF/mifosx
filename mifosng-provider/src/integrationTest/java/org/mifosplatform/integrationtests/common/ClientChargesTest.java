/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.integrationtests.common;

import java.util.HashMap;
import java.util.Locale;

import org.joda.time.LocalDate;
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
/**
 * @author lenovo
 * 
 */
public class ClientChargesTest {

    private ResponseSpecification responseSpec;
    private ResponseSpecification responseSpecForSchedulerJob;
    private RequestSpecification requestSpec;
    private SchedulerJobHelper schedulerJobHelper;

    @Before
    public void setup() {
        Utils.initializeRESTAssured();
        this.requestSpec = new RequestSpecBuilder().setContentType(ContentType.JSON).build();
        this.requestSpec.header("Authorization", "Basic " + Utils.loginIntoServerAndGetBase64EncodedAuthenticationKey());
        this.responseSpec = new ResponseSpecBuilder().expectStatusCode(200).build();
        this.schedulerJobHelper = new SchedulerJobHelper(this.requestSpec, this.responseSpec);
    }

    // @Test
    public void clientChargeTest() {

        // Creates clientCharge
        final Integer chargeId = ChargesHelper.createCharges(this.requestSpec, this.responseSpec,
                ChargesHelper.getChargeSpecifiedDueDateJSON());
        Assert.assertNotNull(chargeId);

        // creates client with activation date
        final Integer clientId = ClientHelper.createClient(this.requestSpec, this.responseSpec, "01 November 2012");
        Assert.assertNotNull(clientId);

        /**
         * create a charge for loan and try to associate to client created in
         * the above lines.it will be an invalid scenario the reason is client
         * is not allowed to have only client charge.
         * 
         */
        final Integer loanChargeId = ChargesHelper.createCharges(this.requestSpec, this.responseSpec,
                ChargesHelper.getLoanSpecifiedDueDateJSON());
        Assert.assertNotNull(loanChargeId);
        ResponseSpecification responseLoanChargeFailure = new ResponseSpecBuilder().expectStatusCode(403).build();
        final Integer clientLoanChargeId = ClientHelper.addChargesForClient(this.requestSpec, responseLoanChargeFailure, clientId,
                ClientHelper.getSpecifiedDueDateChargesClientAsJSON(loanChargeId.toString(), "29 October 2011"));
        Assert.assertNull(clientLoanChargeId);

        /**
         * associates a clientCharge to a client and pay client charge for 10
         * USD--success scenario
         **/
        final Integer clientChargeId = ClientHelper.addChargesForClient(this.requestSpec, this.responseSpec, clientId,
                ClientHelper.getSpecifiedDueDateChargesClientAsJSON(chargeId.toString(), "29 October 2011"));
        Assert.assertNotNull(clientChargeId);
        final String clientChargePaidTransactionId = ClientHelper.payChargesForClients(this.requestSpec, this.responseSpec, clientId,
                clientChargeId, ClientHelper.getPayChargeJSON("25 AUGUST 2015", "10"));
        Assert.assertNotNull(clientChargePaidTransactionId);
        isValidOutstandingAmount(ClientHelper.getClientCharge(requestSpec, responseSpec, clientId.toString(), clientChargeId.toString()),
                (float) 190.0);

        /**
         * Revert the paid client charge transaction by passing the
         * clientChargePaidTransactionId and ensure the same is reverted.
         */
        final Integer undoTrxnId = ClientHelper.revertClientChargeTransaction(this.requestSpec, this.responseSpec, clientId.toString(),
                clientChargePaidTransactionId);
        Assert.assertNotNull(undoTrxnId);
        isReversedTransaction(clientId.toString(), undoTrxnId.toString());
        /**
         * Now pay client charge for 20 USD and ensure the outstanding amount is
         * updated properly
         */
        ResponseSpecification responseSpecFailure = new ResponseSpecBuilder().expectStatusCode(400).build();
        final String responseId_futureDate_failure = ClientHelper.payChargesForClients(this.requestSpec, responseSpecFailure, clientId,
                clientChargeId, ClientHelper.getPayChargeJSON("28 AUGUST 2016", "20"));
        Assert.assertNull(responseId_futureDate_failure);

        // waived off the outstanding client charge
        final String waiveOffClientChargeTransactionId = ClientHelper.waiveChargesForClients(this.requestSpec, this.responseSpec, clientId,
                clientChargeId, ClientHelper.getWaiveChargeJSON("100", clientChargeId.toString()));
        Assert.assertNotNull(waiveOffClientChargeTransactionId);

        /**
         * Revert the waived off client charge transaction by passing the
         * waiveOffClientChargeTransactionId and ensured the transaction is
         * reversed.
         */
        final Integer undoWaiveTrxnId = ClientHelper.revertClientChargeTransaction(this.requestSpec, this.responseSpec,
                clientId.toString(), waiveOffClientChargeTransactionId);
        Assert.assertNotNull(undoWaiveTrxnId);
        isReversedTransaction(clientId.toString(), undoWaiveTrxnId.toString());
        /**
         * pay client charge before client activation date and ensured its a
         * failure test case
         */

        final String responseId_activationDate_failure = ClientHelper.payChargesForClients(this.requestSpec, responseSpecFailure, clientId,
                clientChargeId, ClientHelper.getPayChargeJSON("30 October 2011", "20"));
        Assert.assertNull(responseId_activationDate_failure);
        /**
         * pay client charge more than outstanding amount amount and ensured its
         * a failure test case
         */
        final String responseId_moreAmount_failure = ClientHelper.payChargesForClients(this.requestSpec, responseSpecFailure, clientId,
                clientChargeId, ClientHelper.getPayChargeJSON("25 AUGUST 2015", "300"));
        Assert.assertNull(responseId_moreAmount_failure);
        /**
         * pay client charge for 10 USD and ensure outstanding amount is updated
         * properly
         */
        final String chargePaid_responseId = ClientHelper.payChargesForClients(this.requestSpec, this.responseSpec, clientId,
                clientChargeId, ClientHelper.getPayChargeJSON("25 AUGUST 2015", "100"));
        Assert.assertNotNull(chargePaid_responseId);

        isValidOutstandingAmount(ClientHelper.getClientCharge(requestSpec, responseSpec, clientId.toString(), clientChargeId.toString()),
                (float) 100.0);

    }

    @Test
    public void clientRecurringChargeTest() throws InterruptedException {

        // creates client with activation date
        final Integer clientId = ClientHelper.createClient(this.requestSpec, this.responseSpec, "01 November 2012");
        Assert.assertNotNull(clientId);

        String month = getMonth(LocalDate.now().getMonthOfYear());
        StringBuilder date = new StringBuilder(20);
        date.append(String.valueOf(LocalDate.now().getDayOfMonth())).append(" " + month).append(" " + LocalDate.now().getYear());

        /**
         * create a charge with charge time type as isWeekly ,isMonthly and is
         * Annual and add to a client respectively.
         */

        final Integer weeklyChargeId = ChargesHelper.createCharges(this.requestSpec, this.responseSpec,
                ChargesHelper.getChargeforWeeklyJSON());
        Assert.assertNotNull(weeklyChargeId);

        final Integer weeklyClientRecurringChargeId = ClientHelper.addChargesForClient(this.requestSpec, responseSpec, clientId,
                ClientHelper.getSpecifiedDueDateChargesClientAsJSON(weeklyChargeId.toString(), date.toString()));
        Assert.assertNotNull(weeklyClientRecurringChargeId);

        final Integer monthlyChargeId = ChargesHelper.createCharges(this.requestSpec, this.responseSpec,
                ChargesHelper.getChargeforMonthlyJSON());
        Assert.assertNotNull(monthlyChargeId);

        final Integer monthlyClientRecurringChargeId = ClientHelper.addChargesForClient(this.requestSpec, responseSpec, clientId,
                ClientHelper.getSpecifiedDueDateChargesClientAsJSON(monthlyChargeId.toString(), date.toString()));
        Assert.assertNotNull(monthlyClientRecurringChargeId);

        final Integer annualChargeId = ChargesHelper.createCharges(this.requestSpec, this.responseSpec,
                ChargesHelper.getChargeforAnnualJSON());
        Assert.assertNotNull(annualChargeId);

        final Integer annualClientRecurringChargeId = ClientHelper.addChargesForClient(this.requestSpec, responseSpec, clientId,
                ClientHelper.getSpecifiedDueDateChargesClientAsJSON(annualChargeId.toString(), date.toString()));
        Assert.assertNotNull(annualClientRecurringChargeId);

        String JobName = "Apply Recurring Charge On Client";

        this.schedulerJobHelper.executeJob(JobName);

    }

    private String getMonth(int monthOfYear) {
        String month = "";
        switch (monthOfYear) {
            case 1:
                month = "January";
            break;
            case 2:
                month = "February";
            break;
            case 3:
                month = "March";
            break;
            case 4:
                month = "April";
            break;
            case 5:
                month = "May";
            break;
            case 6:
                month = "June";
            break;
            case 7:
                month = "July";
            break;
            case 8:
                month = "August";
            break;
            case 9:
                month = "September";
            break;
            case 10:
                month = "October";
            break;
            case 11:
                month = "November";
            break;
            case 12:
                month = "December";
            break;

            default:
            break;
        }
        return month;
    }

    /**
     * It checks whether the client charge transaction is reversed or not.
     * 
     * @param clientId
     * @param transactionId
     */
    private void isReversedTransaction(String clientId, String transactionId) {
        final Boolean isReversed = ClientHelper.getClientTransactions(this.requestSpec, this.responseSpec, clientId.toString(),
                transactionId);
        Assert.assertTrue(isReversed);
    }

    /**
     * Check whether the outStandingAmount is equal to expected Amount or not
     * after paying or after waiving off the client charge.
     * 
     * @param outStandingAmount
     * @param expectedAmount
     */
    private void isValidOutstandingAmount(Object outStandingAmount, Object expectedAmount) {
        Assert.assertEquals((float) outStandingAmount, expectedAmount);
    }

}
