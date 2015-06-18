/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.integrationtests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
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
        
        final Integer parentId = null;

        final Integer createResponseId = (Integer) CodeHelper.createCode(this.requestSpec, this.responseSpec, codeName, parentId ,
                CodeHelper.RESPONSE_ID_ATTRIBUTE_NAME);

        // verify code created

        final HashMap newCodeAttributes = (HashMap) CodeHelper.getCodeById(this.requestSpec, this.responseSpec, createResponseId, "");

        Assert.assertNotNull(newCodeAttributes);
        assertEquals("Verify value of codeId", createResponseId, newCodeAttributes.get(CodeHelper.CODE_ID_ATTRIBUTE_NAME));

        assertEquals("Verify code name", codeName, newCodeAttributes.get(CodeHelper.CODE_NAME_ATTRIBUTE_NAME));
        assertEquals("Verify system defined is false", false, newCodeAttributes.get(CodeHelper.CODE_SYSTEM_DEFINED_ATTRIBUTE_NAME));

        // update code
        final HashMap updateChangeResponse = (HashMap) CodeHelper.updateCode(this.requestSpec, this.responseSpec, createResponseId,
                codeName + "(CHANGE)", parentId, "changes");

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
        
        final Integer parentId = null;

        // create code
        final Integer createResponseId = (Integer) CodeHelper.createCode(this.requestSpec, this.responseSpec, codeName, parentId,
                CodeHelper.RESPONSE_ID_ATTRIBUTE_NAME);

        // verify code created
        final HashMap newCodeAttributes = (HashMap) CodeHelper.getCodeById(this.requestSpec, this.responseSpec, createResponseId, "");

        Assert.assertNotNull(newCodeAttributes);
        assertEquals("Verify value of codeId", createResponseId, newCodeAttributes.get(CodeHelper.CODE_ID_ATTRIBUTE_NAME));

        assertEquals("Verify code name", codeName, newCodeAttributes.get(CodeHelper.CODE_NAME_ATTRIBUTE_NAME));
        assertEquals("Verify system defined is false", false, newCodeAttributes.get(CodeHelper.CODE_SYSTEM_DEFINED_ATTRIBUTE_NAME));

        // try to create duplicate-- should fail
        final List<HashMap> error = (List) CodeHelper.createCode(this.requestSpec, this.generalResponseSpec, codeName, parentId,
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
                systemDefinedCode.get(CodeHelper.CODE_NAME_ATTRIBUTE_NAME) + "CHANGE", null, CommonConstants.RESPONSE_ERROR);

        assertEquals("Cannot update system-defined code", "error.msg.code.systemdefined",
                updateError.get(0).get("userMessageGlobalisationCode"));

    }

    // @Ignore
    @Test
    public void testCodeValuesNotAssignedToTable() {

        final String codeName = Utils.randomNameGenerator("Marital Status1", 10);

        final String codeValue1 = "Married1";
        final String codeValue2 = "Unmarried1";

        final int codeValue1Position = 1;
        final int codeValue2Position = 1;

        final String codeDescription1 = "Description11";
        final String codeDescription2 = "Description22";
        
        final Integer parentId = null;

        // create code
        final Integer createCodeResponseId = (Integer) CodeHelper.createCode(this.requestSpec, this.responseSpec, codeName, parentId ,
                CodeHelper.RESPONSE_ID_ATTRIBUTE_NAME);

        // create first code value
        final Integer createCodeValueResponseId1 = (Integer) CodeHelper.createCodeValue(this.requestSpec, this.responseSpec,
                createCodeResponseId, codeValue1, codeDescription1, codeValue1Position, parentId, CodeHelper.SUBRESPONSE_ID_ATTRIBUTE_NAME);

        // create second code value
        final Integer createCodeValueResponseId2 = (Integer) CodeHelper.createCodeValue(this.requestSpec, this.responseSpec,
                createCodeResponseId, codeValue2, codeDescription2, codeValue1Position, parentId, CodeHelper.SUBRESPONSE_ID_ATTRIBUTE_NAME);

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

        assertEquals("Verify value of code description", codeDescription1,
                codeValuesAttributes1.get(CodeHelper.CODE_VALUE_DESCRIPTION_ATTRIBUTE_NAME));

        assertEquals("Verify position of code value", codeValue1Position,
                codeValuesAttributes1.get(CodeHelper.CODE_VALUE_POSITION_ATTRIBUTE_NAME));

        // verify values of second code value
        final HashMap codeValuesAttributes2 = (HashMap) CodeHelper.getCodeValueById(this.requestSpec, this.responseSpec,
                createCodeResponseId, createCodeValueResponseId2, "");

        Assert.assertNotNull(codeValuesAttributes2);
        assertEquals("Verify value of codeValueId", createCodeValueResponseId2,
                codeValuesAttributes2.get(CodeHelper.CODE_VALUE_ID_ATTRIBUTE_NAME));

        assertEquals("Verify value of code name", codeValue2, codeValuesAttributes2.get(CodeHelper.CODE_VALUE_NAME_ATTRIBUTE_NAME));

        assertEquals("Verify value of code description", codeDescription2,
                codeValuesAttributes2.get(CodeHelper.CODE_VALUE_DESCRIPTION_ATTRIBUTE_NAME));

        assertEquals("Verify position of code value", codeValue2Position,
                codeValuesAttributes2.get(CodeHelper.CODE_VALUE_POSITION_ATTRIBUTE_NAME));

        // update code value 1
        final HashMap codeValueChanges = (HashMap) CodeHelper.updateCodeValue(this.requestSpec, this.responseSpec, createCodeResponseId,
                createCodeValueResponseId1, codeValue1 + "CHANGE", codeDescription1 + "CHANGE", 4, parentId, "changes");

        assertEquals("Verify changed code value name", codeValueChanges.get("name"), codeValue1 + "CHANGE");

        assertEquals("Verify changed code value description", codeValueChanges.get("description"), codeDescription1 + "CHANGE");

        // delete code value
        Integer deletedCodeValueResponseId1 = (Integer) CodeHelper.deleteCodeValueById(this.requestSpec, this.generalResponseSpec,
                createCodeResponseId, createCodeValueResponseId1, CodeHelper.SUBRESPONSE_ID_ATTRIBUTE_NAME);

        // Verify code value deleted

        final ArrayList<HashMap> deletedCodeValueAttributes1 = (ArrayList<HashMap>) CodeHelper.getCodeValueById(this.requestSpec,
                this.generalResponseSpec, createCodeResponseId, deletedCodeValueResponseId1, CommonConstants.RESPONSE_ERROR);

        assertEquals("error.msg.codevalue.id.invalid", deletedCodeValueAttributes1.get(0).get(CommonConstants.RESPONSE_ERROR_MESSAGE_CODE));

        final List<HashMap> deletedCodeValuesList = (List) CodeHelper.getCodeValuesForCode(this.requestSpec, this.responseSpec,
                createCodeResponseId, "");

        assertEquals("Number of code values is 1", 1, deletedCodeValuesList.size());

        final Integer deletedCodeValueResponseId2 = (Integer) CodeHelper.deleteCodeValueById(this.requestSpec, this.generalResponseSpec,
                createCodeResponseId, createCodeValueResponseId2, CodeHelper.SUBRESPONSE_ID_ATTRIBUTE_NAME);

        final ArrayList<HashMap> deletedCodeValueAttributes2 = (ArrayList<HashMap>) CodeHelper.getCodeValueById(this.requestSpec,
                this.generalResponseSpec, createCodeResponseId, deletedCodeValueResponseId2, CommonConstants.RESPONSE_ERROR);

        assertEquals("error.msg.codevalue.id.invalid", deletedCodeValueAttributes2.get(0).get(CommonConstants.RESPONSE_ERROR_MESSAGE_CODE));

        final List<HashMap> deletedCodeValuesList1 = (List) CodeHelper.getCodeValuesForCode(this.requestSpec, this.responseSpec,
                createCodeResponseId, "");

        assertEquals("Number of code values is 0", 0, deletedCodeValuesList1.size());

    }

    @Ignore
    @Test
    public void testCodeValuesAssignedToTable() {

    }
    
    @Test
    public void testCreateCodeWithDependencyColumn(){

        final String codeName = "CountryListTest";
        Integer parentId = null;
        
        final Integer createResponseParentId = (Integer) CodeHelper.createCode(this.requestSpec, this.responseSpec, codeName, parentId ,
                CodeHelper.RESPONSE_ID_ATTRIBUTE_NAME);
        if(createResponseParentId != null){
        	parentId = createResponseParentId;
        }
        
        final String dependentcodeName = "StateListTest";
 
        final Integer createResponseId = (Integer) CodeHelper.createCode(this.requestSpec, this.responseSpec, dependentcodeName, parentId,
                CodeHelper.RESPONSE_ID_ATTRIBUTE_NAME);

        // verify code created

       final HashMap newCodeAttributes = (HashMap) CodeHelper.getCodeById(this.requestSpec, this.responseSpec, createResponseId, "");

        Assert.assertNotNull(newCodeAttributes);
        assertEquals("Verify value of codeId", createResponseId, newCodeAttributes.get(CodeHelper.CODE_ID_ATTRIBUTE_NAME));

        assertEquals("Verify code name", dependentcodeName, newCodeAttributes.get(CodeHelper.CODE_NAME_ATTRIBUTE_NAME));
        assertEquals("Verify system defined is false", false, newCodeAttributes.get(CodeHelper.CODE_SYSTEM_DEFINED_ATTRIBUTE_NAME));
        assertEquals("Verify value of parentId", parentId, newCodeAttributes.get(CodeHelper.CODE_PARENT_ID_ATTRIBUTE_NAME));

        // update code
        final HashMap updateChangeResponse = (HashMap) CodeHelper.updateCode(this.requestSpec, this.responseSpec, createResponseId,
        		dependentcodeName+ "(CHANGE)", parentId, "changes");

        assertEquals("Verify dependent code name updated", dependentcodeName + "(CHANGE)", updateChangeResponse.get(CodeHelper.CODE_NAME_ATTRIBUTE_NAME));

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
    
    @Test
    public void testCreateCodeValueWithDepenedenyColumn(){
    	
    	final String codeName = Utils.randomNameGenerator("CountryListTest", 5);
        Integer parentId = null;
        
        final Integer createCodeResponseParentId = (Integer) CodeHelper.createCode(this.requestSpec, this.responseSpec, codeName, parentId ,
                CodeHelper.RESPONSE_ID_ATTRIBUTE_NAME);
        
        final String codeValue1 = "India_Country";
        final String codeValue2 = "USA_Country";

        final int codeValue1Position = 1;
        final int codeValue2Position = 1;

        final String codeDescription1 = "Descrip11";
        final String codeDescription2 = "Descrip22";
        
        // create first code value
        final Integer createCodeValueResponseId1 = (Integer) CodeHelper.createCodeValue(this.requestSpec, this.responseSpec,
        		createCodeResponseParentId, codeValue1, codeDescription1, codeValue1Position, parentId, CodeHelper.SUBRESPONSE_ID_ATTRIBUTE_NAME);

        // create second code value
        final Integer createCodeValueResponseId2 = (Integer) CodeHelper.createCodeValue(this.requestSpec, this.responseSpec,
        		createCodeResponseParentId, codeValue2, codeDescription2, codeValue2Position, parentId, CodeHelper.SUBRESPONSE_ID_ATTRIBUTE_NAME);

        // verify two code values created

        final List<HashMap> codeValuesList1 = (List) CodeHelper.getCodeValuesForCode(this.requestSpec, this.responseSpec,
        		createCodeResponseParentId, "");

        assertEquals("Number of code values returned matches number created", 2, codeValuesList1.size());

       
        final String dependentcodeName = Utils.randomNameGenerator("StateListTest", 5);
        
        
 
        final Integer createResponseId = (Integer) CodeHelper.createCode(this.requestSpec, this.responseSpec, dependentcodeName, createCodeResponseParentId,
                CodeHelper.RESPONSE_ID_ATTRIBUTE_NAME);
        
        // verify code created
        final HashMap newCodeAttributes = (HashMap) CodeHelper.getCodeById(this.requestSpec, this.responseSpec, createResponseId, "");

        Assert.assertNotNull(newCodeAttributes);
        
        final String codeValue3 = "Kerala";
        final String codeValue4 = "LV";

        final int codeValue3Position = 1;
        final int codeValue4Position = 1;

        final String codeDescription3 = "Description1";
        final String codeDescription4 = "Description2";
        
        // create first code value
        final Integer createCodeValueResponseId3 = (Integer) CodeHelper.createCodeValue(this.requestSpec, this.responseSpec,
        		createResponseId, codeValue3, codeDescription3, codeValue3Position,  createCodeValueResponseId1, CodeHelper.SUBRESPONSE_ID_ATTRIBUTE_NAME);

        // create second code value
        final Integer createCodeValueResponseId4 = (Integer) CodeHelper.createCodeValue(this.requestSpec, this.responseSpec,
        		createResponseId, codeValue4, codeDescription4, codeValue4Position, createCodeValueResponseId2, CodeHelper.SUBRESPONSE_ID_ATTRIBUTE_NAME);
        
        // verify two code values created

        final List<HashMap> codeValuesList2 = (List) CodeHelper.getCodeValuesForCode(this.requestSpec, this.responseSpec,
        		createResponseId, "");

        assertEquals("Number of code values returned matches number created", 2, codeValuesList2.size());
        
        
    	
    }

}