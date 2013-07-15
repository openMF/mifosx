package org.mifosplatform.integrationtests.common.xbrl;

import java.util.ArrayList;
import java.util.HashMap;

import org.mifosplatform.integrationtests.common.Utils;

import com.jayway.restassured.specification.RequestSpecification;
import com.jayway.restassured.specification.ResponseSpecification;

@SuppressWarnings("rawtypes")
public class XBRLHelper {
	
	private final RequestSpecification requestSpec;
    private final ResponseSpecification responseSpec;
	
	private static final String GET_TAXONOMY_LIST_URL = "/mifosng-provider/api/v1/taxonomy?tenantIdentifier=default";
	private static final String TAXONOMY_MAPPING_URL ="/mifosng-provider/api/v1/xbrlmapping?tenantIdentifier=default";
	private static final String GET_XBRL_URL = "/mifosng-provider/api/v1/xbrlreport?tenantIdentifier=default";
	
	
	public XBRLHelper(RequestSpecification requestSpec,
			ResponseSpecification responseSpec) {
		this.requestSpec = requestSpec;
		this.responseSpec = responseSpec;
	}


	
	public ArrayList getTaxonomyList() {
		ArrayList response = Utils.performServerGet(requestSpec, responseSpec, GET_TAXONOMY_LIST_URL, "");
		
		return response;
	}
	
	public HashMap getTaxonomyMapping() {
		HashMap response = Utils.performServerGet(requestSpec, responseSpec, TAXONOMY_MAPPING_URL, "config");
		return response;
	}
	
	
	 
	
}
