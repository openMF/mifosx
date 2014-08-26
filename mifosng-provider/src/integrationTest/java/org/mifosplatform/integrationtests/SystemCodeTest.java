/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.integrationtests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.HashMap;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mifosplatform.integrationtests.common.CommonConstants;
import org.mifosplatform.integrationtests.common.Utils;
import org.mifosplatform.integrationtests.common.system.CodeHelper;

import com.jayway.restassured.builder.RequestSpecBuilder;
import com.jayway.restassured.builder.ResponseSpecBuilder;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.specification.RequestSpecification;
import com.jayway.restassured.specification.ResponseSpecification;

/**
 * Test for creating, updating, deleting codes and code values
 * 
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class SystemCodeTest {

    private ResponseSpecification responseSpec;
    private ResponseSpecification generalResponseSpec;
    private RequestSpecification requestSpec;

    @Before
    public void setup() {
        Utils.initializeRESTAssured();
        this.requestSpec = new RequestSpecBuilder().setContentType(ContentType.JSON).build();
        this.requestSpec.header("Authorization", "Basic " + Utils.loginIntoServerAndGetBase64EncodedAuthenticationKey());
        this.responseSpec = new ResponseSpecBuilder().expectStatusCode(200).build();

        this.generalResponseSpec = new ResponseSpecBuilder().build();

    }

    // @Ignore()
    @Test
    // scenario 57, 58, 59, 60
    public void testCreateCode() {
        final String codeName = "Client Marital Status";

        final Integer createResponseId = (Integer) CodeHelper.createCode(this.requestSpec, this.responseSpec, codeName,
                CodeHelper.RESPONSE_ID_ATTRIBUTE_NAME);

        // verify code created

        final HashMap newCodeAttributes = (HashMap) CodeHelper.getCodeById(this.requestSpec, this.responseSpec, createResponseId, "");

        Assert.assertNotNull(newCodeAttributes);
        assertEquals("Verify value of codeId", createResponseId, newCodeAttributes.get(CodeHelper.CODE_ID_ATTRIBUTE_NAME));

        assertEquals("Verify code name", codeName, newCodeAttributes.get(CodeHelper.CODE_NAME_ATTRIBUTE_NAME));
        assertEquals("Verify system defined is false", false, newCodeAttributes.get(CodeHelper.CODE_SYSTEM_DEFINED_ATTRIBUTE_NAME));

        // update code
        final HashMap updateChangeResponse = (HashMap) CodeHelper.updateCode(this.requestSpec, this.responseSpec, createResponseId,
                codeName + "(CHANGE)", "changes");

        assertEquals("Verify code name updated", codeName + "(CHANGE)", updateChangeResponse.get(CodeHelper.CODE_NAME_ATTRIBUTE_NAME));

        // delete code
        final Integer deleteResponseId = (Integer) CodeHelper.deleteCodeById(this.requestSpec, this.responseSpec, createResponseId,
                CodeHelper.RESPONSE_ID_ATTRIBUTE_NAME);
        assertEquals("Verify code deleted", createResponseId, deleteResponseId);

        // verify code deleted
        final HashMap deletedCodeValues = (HashMap) CodeHelper
                .getCodeById(this.requestSpec, this.generalResponseSpec, deleteResponseId, "");

        Assert.assertNotNull(deletedCodeValues);
        assertNull("Verify value of codeId", deletedCodeValues.get(CodeHelper.CODE_ID_ATTRIBUTE_NAME));

        assertNull("Verify code name", deletedCodeValues.get(CodeHelper.CODE_NAME_ATTRIBUTE_NAME));
        assertNull("Verify system defined is false", deletedCodeValues.get(CodeHelper.CODE_SYSTEM_DEFINED_ATTRIBUTE_NAME));
    }

    // @Ignore()
    @Test
    // scenario 57, 60
    public void testPreventCreateDuplicateCode() {
        final String codeName = "Client Marital Status";

        // create code
        final Integer createResponseId = (Integer) CodeHelper.createCode(this.requestSpec, this.responseSpec, codeName,
                CodeHelper.RESPONSE_ID_ATTRIBUTE_NAME);

        // verify code created
        final HashMap newCodeAttributes = (HashMap) CodeHelper.getCodeById(this.requestSpec, this.responseSpec, createResponseId, "");

        Assert.assertNotNull(newCodeAttributes);
        assertEquals("Verify value of codeId", createResponseId, newCodeAttributes.get(CodeHelper.CODE_ID_ATTRIBUTE_NAME));

        assertEquals("Verify code name", codeName, newCodeAttributes.get(CodeHelper.CODE_NAME_ATTRIBUTE_NAME));
        assertEquals("Verify system defined is false", false, newCodeAttributes.get(CodeHelper.CODE_SYSTEM_DEFINED_ATTRIBUTE_NAME));

        // try to create duplicate-- should fail
        final List<HashMap> error = (List) CodeHelper.createCode(this.requestSpec, this.generalResponseSpec, codeName,
                CommonConstants.RESPONSE_ERROR);

        assertEquals("Verify duplication error", "error.msg.code.duplicate.name", error.get(0).get("userMessageGlobalisationCode"));

        // delete code that was just created

        final Integer deleteResponseId = (Integer) CodeHelper.deleteCodeById(this.requestSpec, this.responseSpec, createResponseId,
                CodeHelper.RESPONSE_ID_ATTRIBUTE_NAME);
        assertEquals("Verify code deleted", createResponseId, deleteResponseId);

        // verify code deleted
        final HashMap deletedCodeAttributes = (HashMap) CodeHelper.getCodeById(this.requestSpec, this.generalResponseSpec,
                deleteResponseId, "");

        Assert.assertNotNull(deletedCodeAttributes);
        assertNull("Verify value of codeId", deletedCodeAttributes.get(CodeHelper.CODE_ID_ATTRIBUTE_NAME));

        assertNull("Verify code name", deletedCodeAttributes.get(CodeHelper.CODE_NAME_ATTRIBUTE_NAME));
        assertNull("Verify system defined is false", deletedCodeAttributes.get(CodeHelper.CODE_SYSTEM_DEFINED_ATTRIBUTE_NAME));

    }

    // @Ignore
    @Test
    public void testUpdateDeleteSystemDefinedCode() {

        // get any systemDefined code
        final HashMap systemDefinedCode = (HashMap) CodeHelper.getSystemDefinedCodes(this.requestSpec, this.responseSpec);

        // delete system-defined code should fail
        final List<HashMap> error = (List) CodeHelper.deleteCodeById(this.requestSpec, this.generalResponseSpec,
                (Integer) systemDefinedCode.get(CodeHelper.CODE_ID_ATTRIBUTE_NAME), CommonConstants.RESPONSE_ERROR);

        assertEquals("Cannot delete system-defined code", "error.msg.code.systemdefined", error.get(0).get("userMessageGlobalisationCode"));

        // update system-defined code should fail

        final List<HashMap> updateError = (List) CodeHelper.updateCode(this.requestSpec, this.generalResponseSpec,
                (Integer) systemDefinedCode.get(CodeHelper.CODE_ID_ATTRIBUTE_NAME),
                systemDefinedCode.get(CodeHelper.CODE_NAME_ATTRIBUTE_NAME) + "CHANGE", CommonConstants.RESPONSE_ERROR);

        assertEquals("Cannot update system-defined code", "error.msg.code.systemdefined",
                updateError.get(0).get("userMessageGlobalisationCode"));

    }

    // @Ignore
    @Test
    public void testCodeValuesNotAssignedToTable() {

        final String codeName = "Client Marital Status";

        final String codeValue1 = "Married";
        final String codeValue2 = "Unmarried";

        final int codeValue1Position = 1;
        final int codeValue2Position = 2;

        // create code
        final Integer createCodeResponseId = (Integer) CodeHelper.createCode(this.requestSpec, this.responseSpec, codeName,
                CodeHelper.RESPONSE_ID_ATTRIBUTE_NAME);

        // create first code value
        final Integer createCodeValueResponseId1 = (Integer) CodeHelper.createCodeValue(this.requestSpec, this.responseSpec,
                createCodeResponseId, codeValue1, codeValue1Position, CodeHelper.SUBRESPONSE_ID_ATTRIBUTE_NAME);

        // create second code value
        final Integer createCodeValueResponseId2 = (Integer) CodeHelper.createCodeValue(this.requestSpec, this.responseSpec,
                createCodeResponseId, codeValue2, codeValue2Position, CodeHelper.SUBRESPONSE_ID_ATTRIBUTE_NAME);

        // verify two code values created

        final List<HashMap> codeValuesList = (List) CodeHelper.getCodeValuesForCode(this.requestSpec, this.responseSpec,
                createCodeResponseId, "");

        assertEquals("Number of code values returned matches number created", 2, codeValuesList.size());

        // verify values of first code value
        final HashMap codeValuesAttributes1 = (HashMap) CodeHelper.getCodeValueById(this.requestSpec, this.responseSpec,
                createCodeResponseId, createCodeValueResponseId1, "");

        Assert.assertNotNull(codeValuesAttributes1);
        assertEquals("Verify value of codeValueId", createCodeValueResponseId1,
                codeValuesAttributes1.get(CodeHelper.CODE_VALUE_ID_ATTRIBUTE_NAME));

        assertEquals("Verify value of code name", codeValue1, codeValuesAttributes1.get(CodeHelper.CODE_VALUE_NAME_ATTRIBUTE_NAME));

        assertEquals("Verify position of code value", codeValue1Position,
                codeValuesAttributes1.get(CodeHelper.CODE_VALUE_POSITION_ATTRIBUTE_NAME));

        // verify values of second code value
        final HashMap codeValuesAttributes2 = (HashMap) CodeHelper.getCodeValueById(this.requestSpec, this.responseSpec,
                createCodeResponseId, createCodeValueResponseId2, "");

        Assert.assertNotNull(codeValuesAttributes2);
        assertEquals("Verify value of codeValueId", createCodeValueResponseId2,
                codeValuesAttributes2.get(CodeHelper.CODE_VALUE_ID_ATTRIBUTE_NAME));

        assertEquals("Verify value of code name", codeValue2, codeValuesAttributes2.get(CodeHelper.CODE_VALUE_NAME_ATTRIBUTE_NAME));

        assertEquals("Verify position of code value", codeValue2Position,
                codeValuesAttributes2.get(CodeHelper.CODE_VALUE_POSITION_ATTRIBUTE_NAME));

        // update code value 1

        final HashMap codeValueChanges = (HashMap) CodeHelper.updateCodeValue(this.requestSpec, this.responseSpec, createCodeResponseId,
                createCodeValueResponseId1, codeValue1 + "CHANGE", 4, "changes");

        assertEquals("Verify changed code value name", codeValueChanges.get("name"), codeValue1 + "CHANGE");

        // delete code value
        final Integer deletedCodeValueResponseId1 = (Integer) CodeHelper.deleteCodeValueById(this.requestSpec, this.generalResponseSpec,
                createCodeResponseId, createCodeValueResponseId1, CodeHelper.SUBRESPONSE_ID_ATTRIBUTE_NAME);

        // Verify code value deleted

        final HashMap deletedCodeValueAttributes1 = (HashMap) CodeHelper.getCodeValueById(this.requestSpec, this.generalResponseSpec,
                createCodeResponseId, deletedCodeValueResponseId1, "");

        // Assert.assertNotNull(deletedCodeValueAttributes1);
        assertNull("Verify value of codeValueId", deletedCodeValueAttributes1.get(CodeHelper.CODE_VALUE_ID_ATTRIBUTE_NAME));

        assertNull("Verify value of code name", deletedCodeValueAttributes1.get(CodeHelper.CODE_VALUE_NAME_ATTRIBUTE_NAME));

        assertNull("Verify position of code value", deletedCodeValueAttributes1.get(CodeHelper.CODE_VALUE_POSITION_ATTRIBUTE_NAME));

        final List<HashMap> deletedCodeValuesList = (List) CodeHelper.getCodeValuesForCode(this.requestSpec, this.responseSpec,
                createCodeResponseId, "");

        assertEquals("Number of code values is 1", 1, deletedCodeValuesList.size());

        // delete code, remaining code values automatically deleted

        final Integer deleteCodeResponseId = (Integer) CodeHelper.deleteCodeById(this.requestSpec, this.generalResponseSpec,
                createCodeResponseId, CodeHelper.RESPONSE_ID_ATTRIBUTE_NAME);
        assertEquals("Verify code deleted", createCodeResponseId, deleteCodeResponseId);

        // verify code deleted
        final HashMap deletedCodeAttributes = (HashMap) CodeHelper.getCodeById(this.requestSpec, this.generalResponseSpec,
                createCodeResponseId, "");

        Assert.assertNotNull(deletedCodeAttributes);
        assertNull("Verify value of codeId is null", deletedCodeAttributes.get(CodeHelper.CODE_ID_ATTRIBUTE_NAME));

        assertNull("Verify code name is null", deletedCodeAttributes.get(CodeHelper.CODE_NAME_ATTRIBUTE_NAME));
        assertNull("Verify system defined is null", deletedCodeAttributes.get(CodeHelper.CODE_SYSTEM_DEFINED_ATTRIBUTE_NAME));

        // Verify remaining code value deleted
        final HashMap deletedCodeValues2 = (HashMap) CodeHelper.getCodeValueById(this.requestSpec, this.generalResponseSpec,
                createCodeResponseId, createCodeValueResponseId2, "");

        assertNull("Verify value of codeValueId is null", deletedCodeValues2.get(CodeHelper.CODE_VALUE_ID_ATTRIBUTE_NAME));

        assertNull("Verify value of code name is null", deletedCodeValues2.get(CodeHelper.CODE_VALUE_NAME_ATTRIBUTE_NAME));

        assertNull("Verify position of code value null", deletedCodeValues2.get(CodeHelper.CODE_VALUE_POSITION_ATTRIBUTE_NAME));

    }

    @Ignore
    @Test
    public void testCodeValuesAssignedToTable() {

    }

}