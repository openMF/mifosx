package org.mifosplatform.integrationtests.common.savings;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;

import org.mifosplatform.integrationtests.common.Utils;

import com.google.gson.Gson;
import com.jayway.restassured.specification.RequestSpecification;
import com.jayway.restassured.specification.ResponseSpecification;

@SuppressWarnings("rawtypes")
public class SavingTransactionHelper {

    private final RequestSpecification requestSpec;
    private final ResponseSpecification responseSpec;

    private static final String CREATE_SAVING_PRODUCT_URL = "/mifosng-provider/api/v1/savingsproducts?tenantIdentifier=default";

    public SavingTransactionHelper(final RequestSpecification requestSpec, final ResponseSpecification responseSpec) {
        this.requestSpec = requestSpec;
        this.responseSpec = responseSpec;
    }

    public Integer getSavingProductId(final String savingProductJSON) {
        return Utils.performServerPost(this.requestSpec, this.responseSpec, CREATE_SAVING_PRODUCT_URL, savingProductJSON, "resourceId");
    }
}