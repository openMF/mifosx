/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.integrationtests;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertEquals;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mifosplatform.integrationtests.common.Utils;
import org.mifosplatform.integrationtests.common.office.OfficeDOHelper;
import org.mifosplatform.integrationtests.common.office.OfficeResourceHandler;
import org.mifosplatform.integrationtests.common.office.OfficeDOHelperSerializer;

import com.jayway.restassured.builder.RequestSpecBuilder;
import com.jayway.restassured.builder.ResponseSpecBuilder;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.specification.RequestSpecification;
import com.jayway.restassured.specification.ResponseSpecification;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.*;

public class OfficeIntegrationsTest {

    private ResponseSpecification statusOkResponseSpec;
    private RequestSpecification requestSpec;

    @Before
    public void setup() {
        Utils.initializeRESTAssured();
        this.requestSpec = new RequestSpecBuilder().setContentType(ContentType.JSON).build();
        this.requestSpec.header("Authorization", "Basic " + Utils.loginIntoServerAndGetBase64EncodedAuthenticationKey());
        this.statusOkResponseSpec = new ResponseSpecBuilder().expectStatusCode(200).build();
    }

    @Test
    public void testCreateOffice() throws ParseException {
        final Gson gson = new GsonBuilder()
                .registerTypeAdapter(OfficeDOHelper.class, new OfficeDOHelperSerializer())
                .create();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date date = sdf.parse("21/12/2012");
        OfficeDOHelper oh = OfficeDOHelper                                            // oh ---> Office Helper
                .create(Utils.randomNameGenerator("", 5))
                .externalId(Utils.randomNameGenerator("office-", 7))
                .nameDecorated(Utils.randomNameGenerator("", 8))
                .openingDate(date)
                .hierarchy(".")
                .build();
        String JsonData = oh.toJSON();
        final Long officeID = createOffice(JsonData, this.requestSpec, this.statusOkResponseSpec);
        Assert.assertNotNull(officeID);

    }

    private Long createOffice(final String officeJSON,
                              final RequestSpecification requestSpec,
                              final ResponseSpecification responseSpec) {
        String officeId = String.valueOf(OfficeResourceHandler.createOffice(officeJSON, requestSpec, responseSpec));
        if (officeId.equals("null")) {
            // Invalid JSON data parameters
            return null;
        }

        return new Long(officeId);
    }

    @Test
    public void testCreateOfficeWithEmptyName() throws ParseException {
        final Gson gson = new GsonBuilder()
                .registerTypeAdapter(OfficeDOHelper.class, new OfficeDOHelperSerializer())
                .create();

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date date = sdf.parse("21/12/2012");
        OfficeDOHelper oh = OfficeDOHelper
                .create(null)
                .externalId(Utils.randomNameGenerator("", 5))
                .nameDecorated(Utils.randomNameGenerator("office-", 6))
                .openingDate(date)
                .hierarchy(".")
                .build();
        String JsonData = oh.toJSON();
        final Long officeID = createOffice(JsonData, this.requestSpec, this.statusOkResponseSpec);
        Assert.assertNull(officeID);
    }

    @Test
    public void testCreateOfficeWithEmptyExternalId() throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date date = sdf.parse("21/12/2012");
        final Gson gson = new GsonBuilder()
                .registerTypeAdapter(OfficeDOHelper.class, new OfficeDOHelperSerializer())
                .create();
        OfficeDOHelper oh = OfficeDOHelper
                .create(Utils.randomNameGenerator("", 3))
                .externalId(null)
                .nameDecorated(Utils.randomNameGenerator("office-", 5))
                .openingDate(date)
                .hierarchy(".")
                .build();
        String JsonData = oh.toJSON();
        final Long officeID = createOffice(JsonData, this.requestSpec, this.statusOkResponseSpec);
        Assert.assertNotNull(officeID);
    }

    @Test
    public void testCreateOfficeWithDuplicateName() throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date date = sdf.parse("21/12/2012");

        final Gson gson = new GsonBuilder()
                .registerTypeAdapter(OfficeDOHelper.class, new OfficeDOHelperSerializer())
                .create();

        OfficeDOHelper oh = OfficeDOHelper
                .create(Utils.randomNameGenerator("", 7))
                .externalId(Utils.randomNameGenerator("Office-", 4))
                .nameDecorated(Utils.randomNameGenerator("", 10))
                .openingDate(date)
                .hierarchy(".")
                .build();
        String JsonData = oh.toJSON();
        final Long officeID1 = createOffice(JsonData, this.requestSpec, this.statusOkResponseSpec);
        Assert.assertNotNull(officeID1);

        OfficeDOHelper oh2 = OfficeDOHelper
                .create(Utils.randomNameGenerator("", 7))
                .externalId(Utils.randomNameGenerator(oh.getName(), 6))
                .nameDecorated(Utils.randomNameGenerator("", 11))
                .openingDate(date)
                .hierarchy(".")
                .build();

        JsonData = oh2.toJSON();
        ResponseSpecification responseSpec = new ResponseSpecBuilder().expectStatusCode(400).build(); // 400 if the test works
        final Long officeID2 = createOffice(JsonData, this.requestSpec, this.statusOkResponseSpec);
        Assert.assertNull(officeID2);
    }

    @Test
    public void testCreateOfficeWithDuplicateExternalId() throws ParseException{
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date date = sdf.parse("21/12/2012");
        final Gson gson = new GsonBuilder()
                .registerTypeAdapter(OfficeDOHelper.class, new OfficeDOHelperSerializer())
                .create();
        OfficeDOHelper oh1 = OfficeDOHelper
                .create(Utils.randomNameGenerator("", 8))
                .externalId(Utils.randomNameGenerator("Office-", 4))
                .nameDecorated(Utils.randomNameGenerator("", 10))
                .openingDate(date)
                .hierarchy(".")
                .build();

        String JsonData = oh1.toJSON();
        final Long officeID1 = createOffice(JsonData, this.requestSpec, this.statusOkResponseSpec);
        Assert.assertNotNull(officeID1);

        OfficeDOHelper oh2 = OfficeDOHelper
                .create(Utils.randomNameGenerator("", 9))
                .externalId(Utils.randomNameGenerator("Office-", 5))
                .nameDecorated(Utils.randomNameGenerator("", 11))
                .openingDate(date)
                .hierarchy(".")
                .build();

        JsonData = oh2.toJSON();
        ResponseSpecification responseSpec = new ResponseSpecBuilder().expectStatusCode(402).build(); // 402 if the test works
        final Long officeID2 = createOffice(JsonData, this.requestSpec, this.statusOkResponseSpec);
        Assert.assertNull(officeID2);


    }

    @Test
    public void testCreateOfficeWithEmptyOpeningDate() {
        OfficeDOHelper oh = OfficeDOHelper
                .create(Utils.randomNameGenerator("", 9))
                .externalId(Utils.randomNameGenerator("Office-", 4))
                .nameDecorated(Utils.randomNameGenerator("", 3))
                .openingDate(null)
                .hierarchy(".")
                .build();

        String JsonData = oh.toJSON();
        final Long officeID = createOffice(JsonData, this.requestSpec, this.statusOkResponseSpec);
        Assert.assertNotNull(officeID);
    }

    @Test
    public void testRetrieveOffice() throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date date = sdf.parse("21/12/2012");
        final Gson gson = new GsonBuilder()
                .registerTypeAdapter(OfficeDOHelper.class, new OfficeDOHelperSerializer())
                .create();
        OfficeDOHelper oh = OfficeDOHelper
                .create(Utils.randomNameGenerator("", 7))
                .externalId(Utils.randomNameGenerator("Office-", 6))
                .nameDecorated(Utils.randomNameGenerator("", 5))
                .openingDate(date)
                .hierarchy(".")
                .build();
        String jsonData = oh.toJSON();
        final Long officeID = createOffice(jsonData, this.requestSpec, this.statusOkResponseSpec);
        Assert.assertNotNull(officeID);
        jsonData = OfficeResourceHandler.retrieveOffice(officeID, this.requestSpec, this.statusOkResponseSpec);

        OfficeDOHelper oh2 = OfficeDOHelper.fromJSON(jsonData);
        assertEquals(oh.getName(), oh2.getName());

    }

    @Test
    public void testRetrieveAllOffice() throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date date = sdf.parse("21/12/2012");
        final Gson gson = new GsonBuilder()
                .registerTypeAdapter(OfficeDOHelper.class, new OfficeDOHelperSerializer())
                .create();
        OfficeDOHelper oh = OfficeDOHelper
                .create(Utils.randomNameGenerator("", 9))
                .externalId(Utils.randomNameGenerator("Office-", 4))
                .nameDecorated(Utils.randomNameGenerator("", 2))
                .openingDate(date)
                .hierarchy(".")
                .build();
        String jsonData = oh.toJSON();
        final Long officeID = createOffice(jsonData, this.requestSpec, this.statusOkResponseSpec);
        Assert.assertNotNull(officeID);

        List<OfficeDOHelper> ohList = OfficeResourceHandler.retrieveAllOffice(this.requestSpec, this.statusOkResponseSpec);

        Assert.assertNotNull(ohList);
        Assert.assertThat(ohList.size(), greaterThanOrEqualTo(1));
        Assert.assertThat(ohList, hasItem(oh));

    }

    @Test
    public void testRetrieveUnknownOffice() {
        ResponseSpecification responseSpec = new ResponseSpecBuilder().expectStatusCode(419).build();
        String jsonData = OfficeResourceHandler.retrieveOffice(Long.MAX_VALUE, this.requestSpec, responseSpec);
        HashMap<String, String> map = new Gson().fromJson(jsonData, HashMap.class);
        assertEquals(map.get("userMessageGlobalisationCode"), "error.msg.resource.not.found");
    }

    @Test
    public void testUpdateOffice() throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date date = sdf.parse("21/12/2012");
        final Gson gson = new GsonBuilder()
                .registerTypeAdapter(OfficeDOHelper.class, new OfficeDOHelperSerializer())
                .create();
        OfficeDOHelper oh = OfficeDOHelper
                .create(Utils.randomNameGenerator("", 7))
                .externalId(Utils.randomNameGenerator("Office-", 5))
                .nameDecorated(Utils.randomNameGenerator("", 3))
                .openingDate(date)
                .hierarchy(".")
                .build();
        String jsonData = oh.toJSON();

        final long officeID = createOffice(jsonData, this.requestSpec, this.statusOkResponseSpec);
        Assert.assertNotNull(officeID);

        String newName = Utils.randomNameGenerator("", 9);
        String newExternalId = Utils.randomNameGenerator("Office-", 6);
        OfficeDOHelper oh2 = OfficeResourceHandler.updateOffice(officeID, newName, newExternalId, this.requestSpec, this.statusOkResponseSpec);
        Assert.assertEquals(newName, oh2.getName());
        Assert.assertEquals(newExternalId, oh2.getExternalId());
    }

    @Test
    public void testUpdateUnkownOffice() {
        String newName = Utils.randomNameGenerator("", 4);
        String newExternalId = Utils.randomNameGenerator("Office-", 9);
        ResponseSpecification responseSpec = new ResponseSpecBuilder().expectStatusCode(450).build();
        OfficeDOHelper oh = OfficeResourceHandler.updateOffice(Long.MAX_VALUE, newName, newExternalId, this.requestSpec, responseSpec);
        Assert.assertNull(oh);

    }

    @Test
    public void testUpdateOfficeWithInvalidName()  throws ParseException{
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date date = sdf.parse("21/12/2012");
        final Gson gson = new GsonBuilder()
                .registerTypeAdapter(OfficeDOHelper.class, new OfficeDOHelperSerializer())
                .create();
        OfficeDOHelper oh = OfficeDOHelper
                .create(Utils.randomNameGenerator("", 4))
                .externalId(Utils.randomNameGenerator("Office-", 3))
                .nameDecorated(Utils.randomNameGenerator("", 8))
                .openingDate(date)
                .hierarchy(".")
                .build();

        String jsonData = oh.toJSON();

        final long officeID = createOffice(jsonData, this.requestSpec, this.statusOkResponseSpec);
        Assert.assertNotNull(officeID);

        String newName = Utils.randomNameGenerator("", 500);
        String newExternalId = Utils.randomNameGenerator("Office-", 5);
        ResponseSpecification responseSpec = new ResponseSpecBuilder().expectStatusCode(475).build();
        OfficeDOHelper oh2 = OfficeResourceHandler.updateOffice(officeID, newName, newExternalId, this.requestSpec, responseSpec);
        Assert.assertNull(oh2);


    }

    @Test
    public void testUpdateOfficeWithNewExternalId() throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date date = sdf.parse("21/12/2012");
        final Gson gson = new GsonBuilder()
                .registerTypeAdapter(OfficeDOHelper.class, new OfficeDOHelperSerializer())
                .create();
        OfficeDOHelper oh = OfficeDOHelper
                .create(Utils.randomNameGenerator("", 5))
                .externalId(Utils.randomNameGenerator("Office-", 6))
                .nameDecorated(Utils.randomNameGenerator("", 9))
                .openingDate(date)
                .hierarchy(".")
                .build();

        String jsonData = oh.toJSON();

        final long officeID = createOffice(jsonData, this.requestSpec, this.statusOkResponseSpec);
        Assert.assertNotNull(officeID);

        String newExternalId = Utils.randomNameGenerator("", 4);
        OfficeDOHelper oh2 = OfficeResourceHandler.updateOffice(officeID, null, newExternalId, this.requestSpec, this.statusOkResponseSpec);

        Assert.assertEquals(newExternalId, oh2.getExternalId());

    }

    @Test
    public void testUpdateOfficeWithInvalidNewExternalId() throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date date = sdf.parse("21/12/2012");
        final Gson gson = new GsonBuilder()
                .registerTypeAdapter(OfficeDOHelper.class, new OfficeDOHelperSerializer())
                .create();
        OfficeDOHelper oh = OfficeDOHelper
                .create(Utils.randomNameGenerator("", 9))
                .externalId(Utils.randomNameGenerator("Office-", 3))
                .nameDecorated(Utils.randomNameGenerator("", 4))
                .openingDate(date)
                .hierarchy(".")
                .build();

        String jsonData = oh.toJSON();

        final long officeID = createOffice(jsonData, this.requestSpec, this.statusOkResponseSpec);
        Assert.assertNotNull(officeID);

        String newName = Utils.randomNameGenerator("", 5);
        String newExternalId = Utils.randomNameGenerator("Office-", 900);
        ResponseSpecification responseSpec = new ResponseSpecBuilder().expectStatusCode(480).build();

        OfficeDOHelper oh2 = OfficeResourceHandler.updateOffice(officeID, newName, newExternalId, this.requestSpec, responseSpec);
        Assert.assertNull(oh2);

    }

    @Test
    public void testUpdateOfficeWithNewName()  throws ParseException{
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date date = sdf.parse("21/12/2012");
        final Gson gson = new GsonBuilder()
                .registerTypeAdapter(OfficeDOHelper.class, new OfficeDOHelperSerializer())
                .create();
        OfficeDOHelper oh = OfficeDOHelper
                .create(Utils.randomNameGenerator("", 6))
                .externalId(Utils.randomNameGenerator("Office-", 7))
                .nameDecorated(Utils.randomNameGenerator("", 3))
                .openingDate(date)
                .hierarchy(".")
                .build();

        String jsonData = oh.toJSON();

        final long officeID = createOffice(jsonData, this.requestSpec, this.statusOkResponseSpec);
        Assert.assertNotNull(officeID);

        String newName = Utils.randomNameGenerator("", 9);
        OfficeDOHelper oh2 = OfficeResourceHandler.updateOffice(officeID, newName, null, this.requestSpec, this.statusOkResponseSpec);

        Assert.assertEquals(newName, oh2.getName());

    }

    public void testUpdateOfficeWithEmptyParams() throws ParseException
    {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date date = sdf.parse("21/12/2012");
        final Gson gson = new GsonBuilder()
                .registerTypeAdapter(OfficeDOHelper.class, new OfficeDOHelperSerializer())
                .create();
        OfficeDOHelper oh = OfficeDOHelper
                .create(Utils.randomNameGenerator("", 3))
                .externalId(Utils.randomNameGenerator("Office-", 4))
                .nameDecorated(Utils.randomNameGenerator("", 5))
                .openingDate(date)
                .hierarchy(".")
                .build();

        String jsonData = oh.toJSON();

        final Long officeID = createOffice(jsonData, this.requestSpec, this.statusOkResponseSpec);
        Assert.assertNotNull(officeID);

        OfficeDOHelper oh2 = OfficeResourceHandler.updateOffice(officeID, null, null, this.requestSpec, this.statusOkResponseSpec);

        Assert.assertNull(oh2.getName());
        Assert.assertNull(oh2.getExternalId());

        jsonData = OfficeResourceHandler.retrieveOffice(officeID, this.requestSpec, this.statusOkResponseSpec);
        OfficeDOHelper oh3 = new Gson().fromJson(jsonData, OfficeDOHelper.class);

        Assert.assertEquals(oh.getName(), oh3.getName());
        Assert.assertEquals(oh.getExternalId(), oh3.getExternalId());

    }

}










