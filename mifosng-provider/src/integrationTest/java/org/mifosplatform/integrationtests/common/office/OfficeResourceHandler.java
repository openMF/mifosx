/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.integrationtests.common.office;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.jayway.restassured.specification.RequestSpecification;
import com.jayway.restassured.specification.ResponseSpecification;
import org.mifosplatform.integrationtests.common.Utils;

import java.util.HashMap;
import java.util.List;

public class OfficeResourceHandler {

    private static final String OFFICE_URL = "/mifosng-provider/api/v1/office";
    private static final String CREATE_OFFICE_URL = OFFICE_URL + "?" + Utils.TENANT_IDENTIFIER;

    public static Integer createOffice(final String officeJSON,
                                       final RequestSpecification requestSpec,
                                       final ResponseSpecification responseSpec) {
        return Utils.performServerPost(requestSpec, responseSpec, CREATE_OFFICE_URL, officeJSON, "resourceId");
    }

    public static String retrieveOffice(final Long officeID,
                                        final RequestSpecification requestSpec,
                                        final ResponseSpecification responseSpec) {
        final String URL = OFFICE_URL + "/" + officeID + "?" + Utils.TENANT_IDENTIFIER;
        final HashMap response = Utils.performServerGet(requestSpec, responseSpec, URL, "");
        return new Gson().toJson(response);

    }

    public static List<OfficeDOHelper> retrieveAllOffice(final RequestSpecification requestSpec,
                                                         final ResponseSpecification responseSpec) {
        final String URL = OFFICE_URL + "?" + Utils.TENANT_IDENTIFIER;
        List<HashMap<String, Object>> list = Utils.performServerGet(requestSpec, responseSpec, URL, "");
        final String jsonData = new Gson().toJson(list);
        return new Gson().fromJson(jsonData, new TypeToken<List<OfficeDOHelper>>() {
        }.getType());
    }

    public static OfficeDOHelper updateOffice(final Long officeID,
                                         final String newName,
                                         final String newExternalId,
                                         final RequestSpecification requestSpec,
                                         final ResponseSpecification responseSpec) {
        OfficeDOHelper oh = OfficeDOHelper.create(newName).externalId(newExternalId).build();
        String updateJSON = new Gson().toJson(oh);

        final String URL = OFFICE_URL + "/" + officeID + "?" + Utils.TENANT_IDENTIFIER;
        final HashMap<String, String> response = Utils.performServerPut(requestSpec, responseSpec, URL, updateJSON, "changes");
        final String jsonData = new Gson().toJson(response);
        return new Gson().fromJson(jsonData, OfficeDOHelper.class);
    }


}
