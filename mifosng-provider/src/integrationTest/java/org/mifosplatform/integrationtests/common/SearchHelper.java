package org.mifosplatform.integrationtests.common;

import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.path.json.JsonPath.from;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import org.junit.Test;

import com.jayway.restassured.specification.RequestSpecification;
import com.jayway.restassured.specification.ResponseSpecification;

public class SearchHelper {
	
	private final RequestSpecification requestSpec;
    private final ResponseSpecification responseSpec;
    
    private static final String SEARCH_CLIENT_URL = "/mifosng-provider/api/v1/search?"+ Utils.TENANT_IDENTIFIER;
    private static final String GET_CLIENT_URL = "/mifosng-provider/api/v1/clients/"  + Utils.TENANT_IDENTIFIER;
    static HashMap<String, Object> detail=new HashMap<String,Object>(); 
    
    public SearchHelper(final RequestSpecification requestSpec, final ResponseSpecification responseSpec) {
        this.requestSpec = requestSpec;
        this.responseSpec = responseSpec;
    }
   
    public static Object exactClientSearch (final RequestSpecification requestSpec, final ResponseSpecification responseSpec, final String searchParameter,
            final String jsonReturn) {
        final String SEARCH_CLIENT_URL = "/mifosng-provider/api/v1/search?exactMatch="+true+"&query="+searchParameter+"&resource="+"clients,clientIdentifiers"+"&"+ Utils.TENANT_IDENTIFIER;       
        return Utils.performServerGet(requestSpec, responseSpec, SEARCH_CLIENT_URL, jsonReturn);
    }     
    
    public static ArrayList<HashMap> exactMatchSearchClient(final RequestSpecification requestSpec, final ResponseSpecification responseSpec,
    final String searchParameter){
    	    	
   	ArrayList<HashMap> searchResults = (ArrayList<HashMap>) exactClientSearch(requestSpec, responseSpec, searchParameter, "" );
    return searchResults;	   
   }
    public static Object likeClientSearch (final RequestSpecification requestSpec, final ResponseSpecification responseSpec, final String searchParameter,
            final String jsonReturn) {
        final String SEARCH_CLIENT_URL = "/mifosng-provider/api/v1/search?exactMatch="+false+"&query="+searchParameter+"&resource="+"clients,clientIdentifiers"+"&"+ Utils.TENANT_IDENTIFIER;       
        return Utils.performServerGet(requestSpec, responseSpec, SEARCH_CLIENT_URL, jsonReturn);
    }  
    
    public static ArrayList<HashMap> likeMatchSearchClient(final RequestSpecification requestSpec, final ResponseSpecification responseSpec,
    	    final String searchParameter){
    	   	ArrayList<HashMap> searchResults = (ArrayList<HashMap>) likeClientSearch(requestSpec, responseSpec, searchParameter, "" );
    	    return searchResults;	   
    	   } 
    /**********************************************GROUP SEARCH**************************************************/
    
    public static Object exactGroupSearch (final RequestSpecification requestSpec, final ResponseSpecification responseSpec, final String searchParameter,
            final String jsonReturn) {
        final String SEARCH_CLIENT_URL = "/mifosng-provider/api/v1/search?exactMatch="+true+"&query="+searchParameter+"&resource="+"Groups"+"&"+ Utils.TENANT_IDENTIFIER;       
        return Utils.performServerGet(requestSpec, responseSpec, SEARCH_CLIENT_URL, jsonReturn);
    }  
    public static ArrayList<HashMap> exactMatchGroupSearch(final RequestSpecification requestSpec, final ResponseSpecification responseSpec,
    	    final String searchParameter){
    	   	ArrayList<HashMap> searchResults = (ArrayList<HashMap>) exactGroupSearch(requestSpec, responseSpec, searchParameter, "" );
    	    return searchResults;	   
    	   }
    public static Object likeGroupSearch (final RequestSpecification requestSpec, final ResponseSpecification responseSpec, final String searchParameter,
            final String jsonReturn) {
        final String SEARCH_CLIENT_URL = "/mifosng-provider/api/v1/search?exactMatch="+false+"&query="+searchParameter+"&resource="+"Groups"+"&"+ Utils.TENANT_IDENTIFIER;       
        return Utils.performServerGet(requestSpec, responseSpec, SEARCH_CLIENT_URL, jsonReturn);
    }   
    public static ArrayList<HashMap> likeMatchGroupsSearch(final RequestSpecification requestSpec, final ResponseSpecification responseSpec,
    	    final String searchParameter){
    	   	ArrayList<HashMap> searchResults = (ArrayList<HashMap>) likeGroupSearch(requestSpec, responseSpec, searchParameter, "" );
    	    return searchResults;	   
    	   } 
  
    /****************************************************CENTER  SEARCH***********************************************************/
    
    public static Object exactCenterSearch (final RequestSpecification requestSpec, final ResponseSpecification responseSpec, final String searchParameter,
            final String jsonReturn) {
        final String SEARCH_CLIENT_URL = "/mifosng-provider/api/v1/search?exactMatch="+true+"&query="+searchParameter+"&resource="+"groups"+"&"+ Utils.TENANT_IDENTIFIER;       
        return Utils.performServerGet(requestSpec, responseSpec, SEARCH_CLIENT_URL, jsonReturn);
    }  
    public static ArrayList<HashMap> exactMatchCenterSearch(final RequestSpecification requestSpec, final ResponseSpecification responseSpec,
    	    final String searchParameter){
    	   	ArrayList<HashMap> searchResults = (ArrayList<HashMap>) exactCenterSearch(requestSpec, responseSpec, searchParameter, "" );
    	    return searchResults;	   
    	   }
    public static Object likeCenterSearch (final RequestSpecification requestSpec, final ResponseSpecification responseSpec, final String searchParameter,
            final String jsonReturn) {
        final String SEARCH_CLIENT_URL = "/mifosng-provider/api/v1/search?exactMatch="+false+"&query="+searchParameter+"&resource="+"groups"+"&"+ Utils.TENANT_IDENTIFIER;       
        return Utils.performServerGet(requestSpec, responseSpec, SEARCH_CLIENT_URL, jsonReturn);
    }   
    public static ArrayList<HashMap> likeMatchCenterSearch(final RequestSpecification requestSpec, final ResponseSpecification responseSpec,
    	    final String searchParameter){
    	   	ArrayList<HashMap> searchResults = (ArrayList<HashMap>) likeCenterSearch(requestSpec, responseSpec, searchParameter, "" );
    	    return searchResults;	   
    	   } 
    /**************************************SAVING  SEARCH***************************************************************/
   
    public static Object exactSavingSearch (final RequestSpecification requestSpec, final ResponseSpecification responseSpec, final String searchParameter,
            final String jsonReturn) {
        final String SEARCH_CLIENT_URL = "/mifosng-provider/api/v1/search?exactMatch="+true+"&query="+searchParameter+"&resource="+"savings"+"&"+ Utils.TENANT_IDENTIFIER;       
        return Utils.performServerGet(requestSpec, responseSpec, SEARCH_CLIENT_URL, jsonReturn);
    }  
    public static ArrayList<HashMap> exactMatchSavingSearch(final RequestSpecification requestSpec, final ResponseSpecification responseSpec,
    	    final String searchParameter){
    	   	ArrayList<HashMap> searchResults = (ArrayList<HashMap>) exactSavingSearch(requestSpec, responseSpec, searchParameter, "" );
    	    return searchResults;	   
    	   }
    public static Object likeSavingSearch (final RequestSpecification requestSpec, final ResponseSpecification responseSpec, final String searchParameter,
            final String jsonReturn) {
        final String SEARCH_CLIENT_URL = "/mifosng-provider/api/v1/search?exactMatch="+false+"&query="+searchParameter+"&resource="+"savings"+"&"+ Utils.TENANT_IDENTIFIER;       
        return Utils.performServerGet(requestSpec, responseSpec, SEARCH_CLIENT_URL, jsonReturn);
    }   
    public static ArrayList<HashMap> likeMatchSavingSearch(final RequestSpecification requestSpec, final ResponseSpecification responseSpec,
    	    final String searchParameter){
    	   	ArrayList<HashMap> searchResults = (ArrayList<HashMap>) likeSavingSearch(requestSpec, responseSpec, searchParameter, "" );
    	    return searchResults;	   
    	   } 
    
    
    /**********************************************LOAN SEARCH***********************************/ 
    
    
    
    public static Object exactLoanSearch (final RequestSpecification requestSpec, final ResponseSpecification responseSpec, final String searchParameter,
            final String jsonReturn) {
        final String SEARCH_CLIENT_URL = "/mifosng-provider/api/v1/search?exactMatch="+true+"&query="+searchParameter+"&resource="+"loans"+"&"+ Utils.TENANT_IDENTIFIER;       
        return Utils.performServerGet(requestSpec, responseSpec, SEARCH_CLIENT_URL, jsonReturn);
    }  
    public static ArrayList<HashMap> exactMatchLoanSearch(final RequestSpecification requestSpec, final ResponseSpecification responseSpec,
    	    final String searchParameter){
    	   	ArrayList<HashMap> searchResults = (ArrayList<HashMap>) exactLoanSearch(requestSpec, responseSpec, searchParameter, "" );
    	    return searchResults;	   
    	   }
    public static Object likeLoanSearch (final RequestSpecification requestSpec, final ResponseSpecification responseSpec, final String searchParameter,
            final String jsonReturn) {
        final String SEARCH_CLIENT_URL = "/mifosng-provider/api/v1/search?exactMatch="+false+"&query="+searchParameter+"&resource="+"loans"+"&"+ Utils.TENANT_IDENTIFIER;       
        return Utils.performServerGet(requestSpec, responseSpec, SEARCH_CLIENT_URL, jsonReturn);
    }   
    public static ArrayList<HashMap> likeMatchLoanSearch(final RequestSpecification requestSpec, final ResponseSpecification responseSpec,
    	    final String searchParameter){
    	   	ArrayList<HashMap> searchResults = (ArrayList<HashMap>) likeLoanSearch(requestSpec, responseSpec, searchParameter, "" );
    	    return searchResults;	   
    	   } 
/***************************************************************Global Search **************************************************************/
    public static Object exactGlobalSearch (final RequestSpecification requestSpec, final ResponseSpecification responseSpec, final String searchParameter,
            final String jsonReturn) {
        final String SEARCH_CLIENT_URL = "/mifosng-provider/api/v1/search?exactMatch="+true+"&query="+searchParameter+"&resource="+"clients/clientidentifiers/groups/savings/loans"+"&"+ Utils.TENANT_IDENTIFIER;       
        return Utils.performServerGet(requestSpec, responseSpec, SEARCH_CLIENT_URL, jsonReturn);
    }  
    public static ArrayList<HashMap> exactMatchGlobalSearch(final RequestSpecification requestSpec, final ResponseSpecification responseSpec,
    	    final String searchParameter){
    	   	ArrayList<HashMap> searchResults = (ArrayList<HashMap>) exactGlobalSearch(requestSpec, responseSpec, searchParameter, "" );
    	    return searchResults;	   
    	   }
    public static Object likeGlobalSearch (final RequestSpecification requestSpec, final ResponseSpecification responseSpec, final String searchParameter,
            final String jsonReturn) {
        final String SEARCH_CLIENT_URL = "/mifosng-provider/api/v1/search?exactMatch="+false+"&query="+searchParameter+"&resource="+"clients/clientidentifiers/groups/savings/loans"+"&"+ Utils.TENANT_IDENTIFIER;       
        return Utils.performServerGet(requestSpec, responseSpec, SEARCH_CLIENT_URL, jsonReturn);
    }   
    public static ArrayList<HashMap> likeMatchGlobalSearch(final RequestSpecification requestSpec, final ResponseSpecification responseSpec,
    	    final String searchParameter){
    	   	ArrayList<HashMap> searchResults = (ArrayList<HashMap>) likeGlobalSearch(requestSpec, responseSpec, searchParameter, "" );
    	    return searchResults;	   
    	   } 
    
    
    
    
    
    
    public static Integer getEntityIDAfterExactSearchByID (final RequestSpecification requestSpec, final ResponseSpecification responseSpec,
            final String clientId) {
    	ArrayList<HashMap> searchResults = (ArrayList<HashMap>) likeCenterSearch(requestSpec, responseSpec, clientId, "" );
    	Integer foundClientId = 0;
    	if (searchResults != null) {
    		for (HashMap oneResult : searchResults) {
    			Integer entityId = (Integer) oneResult.get("entityId"); 
    			String entityType = (String) oneResult.get("entityType");
    			if ( (entityId == Integer.parseInt(clientId)) && 
    					(entityType.equals("CLIENT")) ) {
    				foundClientId = entityId;
    			}
    		}
    	}
    	return foundClientId;
    }
    
    
}
