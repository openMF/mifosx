/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.integrationtests.common.system;

import static com.jayway.restassured.RestAssured.given;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.mifosplatform.integrationtests.common.Utils;

import com.google.gson.Gson;
import com.jayway.restassured.path.json.JsonPath;
import com.jayway.restassured.specification.RequestSpecification;
import com.jayway.restassured.specification.ResponseSpecification;

public class CodeHelper {

	public static final String CODE_ID_ATTRIBUTE_NAME = "id";
	public static final String RESPONSE_ID_ATTRIBUTE_NAME = "resourceId";
	public static final String SUBRESPONSE_ID_ATTRIBUTE_NAME = "subResourceId";
	public static final String CODE_NAME_ATTRIBUTE_NAME = "name";
	public static final String CODE_SYSTEM_DEFINED_ATTRIBUTE_NAME = "systemDefined";
	public static final String CODE_PARENT_ID_ATTRIBUTE_NAME = "parentId";	
	public static final String CODE_URL = "/mifosng-provider/api/v1/codes";

	public static final String CODE_VALUE_ID_ATTRIBUTE_NAME = "id";
	public static final String CODE_VALUE_NAME_ATTRIBUTE_NAME = "name";
	public static final String CODE_VALUE_DESCRIPTION_ATTRIBUTE_NAME = "description";
	public static final String CODE_VALUE_POSITION_ATTRIBUTE_NAME = "position";
	public static final String CODE_VALUE_PARENT_ID_ATTRIBUTE_NAME = "parentId";
	public static final String CODE_VALUE_URL = "/mifosng-provider/api/v1/codes/[codeId]/codevalues";

	public static Object createCode(final RequestSpecification requestSpec,
			final ResponseSpecification responseSpec, final String codeName, final Integer parentId,
			final String jsonAttributeToGetback) {

		return Utils.performServerPost(requestSpec, responseSpec, CODE_URL
				+ "?" + Utils.TENANT_IDENTIFIER, getTestCodeAsJSON(codeName,parentId),
				jsonAttributeToGetback);
	}

	public static Object updateCode(final RequestSpecification requestSpec,
			final ResponseSpecification responseSpec, final Integer codeId,
			final String codeName,final Integer parentId, final String jsonAttributeToGetback) {

		return Utils.performServerPut(requestSpec, responseSpec, CODE_URL + "/"
				+ codeId + "?" + Utils.TENANT_IDENTIFIER,
				getTestCodeAsJSON(codeName,parentId), jsonAttributeToGetback);
	}

	public static Object getCodeById(final RequestSpecification requestSpec,
			final ResponseSpecification responseSpec, final Integer codeId,
			final String jsonAttributeToGetback) {

		return Utils.performServerGet(requestSpec, responseSpec, CODE_URL + "/"
				+ codeId + "?" + Utils.TENANT_IDENTIFIER,
				jsonAttributeToGetback);

	}

	public static HashMap<String, Object> getCodeByName(
			final RequestSpecification requestSpec,
			final ResponseSpecification responseSpec, final String codeName) {

		final HashMap<String, Object> code = new HashMap<>();

		ArrayList<HashMap<String, Object>> getAllCodes = CodeHelper
				.getAllCodes(requestSpec, responseSpec);
		for (HashMap<String, Object> map : getAllCodes) {
			String name = (String) map.get("name");
			if (name.equals(codeName)) {
				code.put("id", map.get("id"));
				code.put("name", map.get("name"));
				code.put("systemDefined", map.get("systemDefined"));
				break;
			}
		}
		return code;
	}

	public static HashMap<String, Object> retrieveOrCreateCodeValue(
			Integer codeId, final RequestSpecification requestSpec,
			final ResponseSpecification responseSpec) {
		Integer codeValueId = null;
		final List<HashMap<String, Object>> codeValuesList = CodeHelper
				.getCodeValuesForCode(requestSpec, responseSpec, codeId, "");
		/* If Code Values doesn't exist,then create Code value */
		if (codeValuesList.size() == 0) {
			final Integer codeValuePosition = 0;
			final String codeValue = Utils.randomNameGenerator("", 3);
			codeValueId = (Integer) CodeHelper.createCodeValue(requestSpec,
					responseSpec, codeId, codeValue, codeValuePosition, null,
					"subResourceId");
			
		} else {
			return codeValuesList.get(0);
		}
		return Utils.performServerGet(requestSpec, responseSpec,
				CODE_VALUE_URL.replace("[codeId]", codeId.toString()) + "/"
						+ codeValueId.toString() + "?"
						+ Utils.TENANT_IDENTIFIER, "");
		
	}

	public static ArrayList<HashMap<String, Object>> getAllCodes(
			final RequestSpecification requestSpec,
			final ResponseSpecification responseSpec) {

		return Utils.performServerGet(requestSpec, responseSpec, CODE_URL + "?"
				+ Utils.TENANT_IDENTIFIER, "");

	}

	public static Object getSystemDefinedCodes(
			final RequestSpecification requestSpec,
			final ResponseSpecification responseSpec) {

		final String getResponse = given().spec(requestSpec).expect()
				.spec(responseSpec).when()
				.get(CodeHelper.CODE_URL + "?" + Utils.TENANT_IDENTIFIER)
				.asString();

		final JsonPath getResponseJsonPath = new JsonPath(getResponse);

		// get any systemDefined code
		return getResponseJsonPath.get("find { e -> e.systemDefined == true }");

	}

	public static String getTestCodeAsJSON(final String codeName, final Integer parentId) {
		final HashMap<String, String> map = new HashMap<>();
		map.put(CODE_NAME_ATTRIBUTE_NAME, codeName);
		if(parentId != null){
			map.put(CODE_PARENT_ID_ATTRIBUTE_NAME, parentId.toString());
		}
		return new Gson().toJson(map);
	}

	public static String getTestCodeValueAsJSON(final String codeValueName,
			final String description, final Integer position, final Integer parentId) {
		final HashMap<String, Object> map = new HashMap<>();
		map.put(CODE_VALUE_NAME_ATTRIBUTE_NAME, codeValueName);
		if (description != null) {
			map.put(CODE_VALUE_DESCRIPTION_ATTRIBUTE_NAME, description);
		}
		map.put(CODE_VALUE_POSITION_ATTRIBUTE_NAME, position);
		if(parentId != null){
			map.put(CODE_VALUE_PARENT_ID_ATTRIBUTE_NAME, parentId);
		}
		return new Gson().toJson(map);
	}

	public static Object deleteCodeById(final RequestSpecification requestSpec,
			final ResponseSpecification responseSpec, final Integer codeId,
			final String jsonAttributeToGetback) {

		return Utils.performServerDelete(requestSpec, responseSpec, CODE_URL
				+ "/" + codeId + "?" + Utils.TENANT_IDENTIFIER,
				jsonAttributeToGetback);

	}

	public static Object createCodeValue(
			final RequestSpecification requestSpec,
			final ResponseSpecification responseSpec, final Integer codeId,
			final String codeValueName, final Integer position, final Integer parentId,
			final String jsonAttributeToGetback) {
		String description = null;
		return createCodeValue(requestSpec, responseSpec, codeId,
				codeValueName, description, position, parentId, jsonAttributeToGetback);
	}

	public static Object createCodeValue(
			final RequestSpecification requestSpec,
			final ResponseSpecification responseSpec, final Integer codeId,
			final String codeValueName, final String description,
			final Integer position, final Integer parentId, final String jsonAttributeToGetback) {

		return Utils.performServerPost(requestSpec, responseSpec,
				CODE_VALUE_URL.replace("[codeId]", codeId.toString()) + "?"
						+ Utils.TENANT_IDENTIFIER,
				getTestCodeValueAsJSON(codeValueName, description, position, parentId),
				jsonAttributeToGetback);
	}

	public static List<HashMap<String, Object>> getCodeValuesForCode(
			final RequestSpecification requestSpec,
			final ResponseSpecification responseSpec, final Integer codeId,
			final String jsonAttributeToGetback) {

		return Utils.performServerGet(requestSpec, responseSpec,
				CODE_VALUE_URL.replace("[codeId]", codeId.toString()) + "?"
						+ Utils.TENANT_IDENTIFIER, jsonAttributeToGetback);
		
	}

	public static Object getCodeValueById(
			final RequestSpecification requestSpec,
			final ResponseSpecification responseSpec, final Integer codeId,
			final Integer codeValueId, final String jsonAttributeToGetback) {

		return Utils.performServerGet(requestSpec, responseSpec,
				CODE_VALUE_URL.replace("[codeId]", codeId.toString()) + "/"
						+ codeValueId.toString() + "?"
						+ Utils.TENANT_IDENTIFIER, jsonAttributeToGetback);
	}

	public static Object deleteCodeValueById(
			final RequestSpecification requestSpec,
			final ResponseSpecification responseSpec, final Integer codeId,
			final Integer codeValueId, final String jsonAttributeToGetback) {

		return Utils.performServerDelete(requestSpec, responseSpec,
				CODE_VALUE_URL.replace("[codeId]", codeId.toString()) + "/"
						+ codeValueId.toString() + "?"
						+ Utils.TENANT_IDENTIFIER, jsonAttributeToGetback);
	}

	public static Object updateCodeValue(
			final RequestSpecification requestSpec,
			final ResponseSpecification responseSpec, final Integer codeId,
			final Integer codeValueId, final String codeValueName,
			final Integer position, final Integer parentId, final String jsonAttributeToGetback) {
		String description = null;
		return updateCodeValue(requestSpec, responseSpec, codeId, codeValueId,
				codeValueName, description, position, parentId, jsonAttributeToGetback);
	}

	public static Object updateCodeValue(
			final RequestSpecification requestSpec,
			final ResponseSpecification responseSpec, final Integer codeId,
			final Integer codeValueId, final String codeValueName,
			final String description, final Integer position, final Integer parentId,
			final String jsonAttributeToGetback) {

		return Utils.performServerPut(requestSpec, responseSpec,
				CODE_VALUE_URL.replace("[codeId]", codeId.toString()) + "/"
						+ codeValueId + "?" + Utils.TENANT_IDENTIFIER,
				getTestCodeValueAsJSON(codeValueName, description, position, parentId),
				jsonAttributeToGetback);
	}

}