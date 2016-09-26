package org.mifosplatform.integrationtests;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;
import org.mifosplatform.integrationtests.common.ClientHelper;
import org.mifosplatform.integrationtests.common.Utils;
//import org.mifosplatform.integrationtests.common.savings.SavingApplicationTestBuilder;
import org.mifosplatform.integrationtests.common.savings.SavingProductTestBuilder;
import org.mifosplatform.integrationtests.common.savings.SavingTransactionHelper;

import com.jayway.restassured.builder.RequestSpecBuilder;
import com.jayway.restassured.builder.ResponseSpecBuilder;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.specification.RequestSpecification;
import com.jayway.restassured.specification.ResponseSpecification;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class ClientSavingIntegrationTest {

    private ResponseSpecification responseSpec;
    private RequestSpecification requestSpec;
	private SavingTransactionHelper savingTransactionHelper;

    @Before
    public void setup() {
        Utils.initializeRESTAssured();
        this.requestSpec = new RequestSpecBuilder().setContentType(ContentType.JSON).build();
        this.requestSpec.header("Authorization", "Basic " + Utils.loginIntoServerAndGetBase64EncodedAuthenticationKey());
        this.responseSpec = new ResponseSpecBuilder().expectStatusCode(200).build();
    }
    
    @Test
    public void checkClientSavingCreate() {
        this.savingTransactionHelper = new SavingTransactionHelper(this.requestSpec, this.responseSpec);

        final Integer clientID = ClientHelper.createClient(this.requestSpec, this.responseSpec);
        ClientHelper.verifyClientCreatedOnServer(this.requestSpec, this.responseSpec, clientID);
        final Integer savingProductID = createSavingProduct();
        //final Integer savingID = applyForSavingApplication(clientID, savingProductID);
    }
    
    private Integer createSavingProduct() {
        System.out.println("------------------------------CREATING NEW SAVING PRODUCT ---------------------------------------");
        final String savingProductJSON = new SavingProductTestBuilder() //
                .withInterestCompoundingPeriodTypeAsDaily() //
                .withInterestPostingPeriodTypeAsMonthly() //
                .withInterestCalculationPeriodTypeAsDailyBalance() //
                .build();
        return this.savingTransactionHelper.getSavingProductId(savingProductJSON);
    }
    
    /*private Integer applyForSavingApplication(final Integer clientID, final Integer loanProductID) {
        System.out.println("--------------------------------APPLYING FOR SAVING APPLICATION--------------------------------");
        final String savingApplicationJSON = new SavingApplicationTestBuilder() //
                .withPrincipal("12,000.00") //
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
                .build(clientID.toString(), loanProductID.toString());
        return this.savingTransactionHelper.getSavingId(savingApplicationJSON);
    }*/
}