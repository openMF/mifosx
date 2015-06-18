package org.mifosplatform.integrationtests;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mifosplatform.integrationtests.common.CenterHelper;
import org.mifosplatform.integrationtests.common.ClientHelper;
import org.mifosplatform.integrationtests.common.SearchHelper;
import org.mifosplatform.integrationtests.common.GroupHelper;
import org.mifosplatform.integrationtests.common.loans.LoanTransactionHelper;
import org.mifosplatform.integrationtests.common.savings.SavingsAccountHelper;
import org.mifosplatform.integrationtests.common.Utils;
import org.mifosplatform.integrationtests.LoanApplicationApprovalTest;

import com.jayway.restassured.builder.RequestSpecBuilder;
import com.jayway.restassured.builder.ResponseSpecBuilder;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.specification.RequestSpecification;
import com.jayway.restassured.specification.ResponseSpecification;

public class SearchTest {
	 private ResponseSpecification responseSpec;
	 private RequestSpecification requestSpec;
	 private LoanTransactionHelper loanTransactionHelper;
	   
	   
	@Before   
	public void setup() {
	        Utils.initializeRESTAssured();
	        this.requestSpec = new RequestSpecBuilder().setContentType(ContentType.JSON).build();
	        this.requestSpec.header("Authorization", "Basic " + Utils.loginIntoServerAndGetBase64EncodedAuthenticationKey());
	        this.responseSpec = new ResponseSpecBuilder().expectStatusCode(200).build();
	        this.loanTransactionHelper = new LoanTransactionHelper(this.requestSpec, this.responseSpec);
            
	    }


	@Test
	public void Test() {			 
		
		 final Integer clientID= ClientHelper.createClient(this.requestSpec, this.responseSpec);
		 final Integer groupID = GroupHelper.createGroup(this.requestSpec, this.responseSpec);
		 final Integer centerId= CenterHelper.createCenter(this.requestSpec, this.responseSpec);
		 final Integer savingsAccountId=SavingsAccountHelper.openSavingsAccount(this.requestSpec, this.responseSpec,clientID,"10000");	     
		 String  externalId=null;
		 String  accountNo=null;
		 String  displayName=null; 		 
		 String likeMatch_externalId=null;
		 String likeMatch_accountNo=null;
		 String likeMatch_displayName=null;
		 String likeMatch_clientId=null;
		 String likeMatch_groupId=null;
		 
         ArrayList<HashMap> clientdetail=new ArrayList<HashMap>();
	     clientdetail =SearchHelper.likeMatchSearchClient(requestSpec, responseSpec, String.valueOf(clientID));   
	        if (clientdetail != null) {
	        for (HashMap oneResult : clientdetail) {	    		
	        externalId =(String)oneResult.get("entityExternalId");
	        accountNo  =(String)oneResult.get("entityAccountNo");	
	        displayName=(String)oneResult.get("entityName");	        	        
	        likeMatch_externalId=externalId.substring(1,3);
	        likeMatch_accountNo= accountNo.substring(1,3);
	        likeMatch_displayName=displayName.substring(1, 3);
	        likeMatch_clientId=String.valueOf(clientID).substring(1);
	    	}
	    	 
	         
	    	 ArrayList<HashMap> exactMatchClientDetail_Search_By_ExternalId=SearchHelper.exactMatchSearchClient(requestSpec, responseSpec, externalId);
	    	 System.out.println("------------------------EXACT SEARCH CLIENT BY"+"  "+"externalId"+"="+externalId+"------------------------------\n");
	    	 System.out.println(exactMatchClientDetail_Search_By_ExternalId +"\n");
		     
		     ArrayList<HashMap> exactMatchClientDetail_Search_By_AccountNo =SearchHelper.exactMatchSearchClient(requestSpec, responseSpec, accountNo);
		     System.out.println("------------------------EXACT SEARCH CLIENT BY"+"  "+"accountNo"+"="+accountNo+"------------------------------\n");
		     System.out.println(exactMatchClientDetail_Search_By_AccountNo +"\n");
		     
		     ArrayList<HashMap> exactMatchClientDetail_Search_By_Name=SearchHelper.exactMatchSearchClient(requestSpec, responseSpec, displayName);
		     System.out.println("------------------------EXACT SEARCH CLIENT BY"+"  "+"name"+"="+displayName+"------------------------------\n");
		     System.out.println(exactMatchClientDetail_Search_By_Name +"\n");		     
		     
	    	 ArrayList<HashMap> likeMatchClientDetail_Search_By_ExternalId=SearchHelper.likeMatchSearchClient(requestSpec, responseSpec,likeMatch_externalId );
	    	 System.out.println("------------------------LIKE SEARCH CLIENT BY"+"  "+"likeMatch_externalId"+"="+likeMatch_externalId+"------------------------------\n");
	    	 System.out.println(likeMatchClientDetail_Search_By_ExternalId +"\n");
		     
		     ArrayList<HashMap> likeMatchClientDetail_Search_By_AccountNo =SearchHelper.likeMatchSearchClient(requestSpec, responseSpec, likeMatch_accountNo);
		     System.out.println("------------------------LIKE SEARCH CLIENT BY"+"  "+"likeMatch_accountNo"+"="+likeMatch_accountNo+"------------------------------\n");
		     System.out.println(likeMatchClientDetail_Search_By_AccountNo+"\n");
		     
		     ArrayList<HashMap> likeMatchClientDetail_Search_By_Name=SearchHelper.likeMatchSearchClient(requestSpec, responseSpec, likeMatch_displayName);
		     System.out.println("------------------------LIKE SEARCH CLIENT BY"+"  "+"likeMatch_displayName"+"="+likeMatch_displayName+"------------------------------\n");
		     System.out.println(likeMatchClientDetail_Search_By_Name +"\n");
		     
		     
		     
	        }
		     /***************************************************Group search*******************************************************/
		     
		     ArrayList<HashMap> Groupdetail=new ArrayList<HashMap>();
		     Groupdetail =SearchHelper.exactMatchGroupSearch(requestSpec, responseSpec, String.valueOf(groupID));
		     if (Groupdetail != null) {
			        for (HashMap oneResult : Groupdetail) {	    		
			        externalId =(String)oneResult.get("entityExternalId");			        	
			        displayName=(String)oneResult.get("entityName");	        
			        likeMatch_externalId=externalId.substring(1,3);			        
			        likeMatch_displayName=displayName.substring(1,3);
			        likeMatch_groupId=String.valueOf(clientID).substring(1);
			    	}
			         ArrayList<HashMap> exactMatchGroupDetail_Search_By_GroupId=SearchHelper.exactMatchGroupSearch(requestSpec, responseSpec, String.valueOf(groupID));
			         System.out.println("------------------------EXACT SEARCH GROUP BY"+"  "+"GroupId"+"="+groupID+"------------------------------\n");
			         System.out.println(exactMatchGroupDetail_Search_By_GroupId +"\n");		     
			    	
			    	 ArrayList<HashMap> exactMatchGroupDetail_Search_By_ExternalId=SearchHelper.exactMatchGroupSearch(requestSpec, responseSpec, externalId);
			    	 System.out.println("------------------------EXACT SEARCH GROUP BY"+"  "+"ExternalId"+"="+externalId+"------------------------------\n");
			    	 System.out.println(exactMatchGroupDetail_Search_By_ExternalId +"\n");
				     
				     ArrayList<HashMap> exactMatchGroupDetail_Search_By_GroupName =SearchHelper.exactMatchGroupSearch(requestSpec, responseSpec, displayName);
				     System.out.println("------------------------EXACT SEARCH GROUP BY"+"  "+"GroupName"+"="+displayName+"------------------------------\n");
				     System.out.println(exactMatchGroupDetail_Search_By_GroupName +"\n");
				     
				     ArrayList<HashMap> LikeMatchGroupDetail_Search_By_GroupId=SearchHelper. likeMatchGroupsSearch(requestSpec, responseSpec, likeMatch_groupId);
				     System.out.println("-------------------------LIKE SEARCH GROUP BY"+"  "+"groupId"+"="+likeMatch_groupId+"------------------------------\n");
				     System.out.println(LikeMatchGroupDetail_Search_By_GroupId+"\n");
				     
				     ArrayList<HashMap> LikeMatchGroupDetail_Search_By_GroupName=SearchHelper. likeMatchGroupsSearch(requestSpec, responseSpec,likeMatch_groupId );
				     System.out.println("-------------------------LIKE SEARCH GROUP BY"+"  "+"groupNmae"+"="+likeMatch_groupId+"------------------------------\n");
				     System.out.println(LikeMatchGroupDetail_Search_By_GroupName +"\n");		     
			    	
			    	 ArrayList<HashMap> LikeMatchGroupDetail_Search_By_ExternalId=SearchHelper. likeMatchGroupsSearch(requestSpec, responseSpec,likeMatch_externalId );
			    	 System.out.println("-------------------------LIKE SEARCH GROUP BY"+"  "+"likeMatch_externalId"+"="+likeMatch_externalId+"------------------------------\n");
			    	 System.out.println(LikeMatchGroupDetail_Search_By_ExternalId +"\n");
		     }   
		     /*************************************************************Saving  Product Search******************************************************/					     
		     
		     ArrayList<HashMap> savingsdetail=new ArrayList<HashMap>();
		     savingsdetail =SearchHelper.likeMatchSavingSearch(requestSpec, responseSpec, String.valueOf(savingsAccountId));   
		        if (savingsdetail != null) {
		        for (HashMap oneResult : savingsdetail) { 	
		        accountNo  =(String)oneResult.get("entityAccountNo");               
		        likeMatch_accountNo= accountNo.substring(1,3);	        

		    	} 	     
              						         
		    	 ArrayList<HashMap> exactMatchSavingsDetail_Search_By_AccountNo=SearchHelper.exactMatchSavingSearch(requestSpec, responseSpec, accountNo);
		    	 System.out.println("------------------------EXACT SEARCH SAVING PRODUCTS BY"+"  "+"accountNo"+"="+accountNo+"------------------------------\n");    	
		    	 System.out.println(exactMatchSavingsDetail_Search_By_AccountNo +"\n");							  
			     
			     ArrayList<HashMap> LikeMatchSavingsDetail_Search_By_AccountNo=SearchHelper. likeMatchSavingSearch(requestSpec, responseSpec, likeMatch_accountNo);
			     System.out.println("------------------------EXACT SEARCH SAVING PRODUCTS BY"+"  "+"likeMatch_accountNo"+"="+likeMatch_accountNo+"------------------------------\n");    	
			     System.out.println(LikeMatchSavingsDetail_Search_By_AccountNo +"\n");
		        }
		        
		        /***********************************************************Loan  Product Search *******************************************************/
		     
		            accountNo  ="000000001" ;            
			        likeMatch_accountNo= accountNo.substring(1,3);        

			    	 
			         ArrayList<HashMap> exactMatchLoanDetail_Search_By_AccountNo=SearchHelper.exactMatchLoanSearch(requestSpec, responseSpec, accountNo);
		    	     System.out.println("------------------------EXACT LOAN SAVING PRODUCTS BY"+"  "+"accountNo"+"="+accountNo+"------------------------------\n");    	
 			         System.out.println(exactMatchLoanDetail_Search_By_AccountNo+"\n");							  
				     
				     ArrayList<HashMap> LikeMatchLoanDetail_Search_By_AccountNo=SearchHelper. likeMatchLoanSearch(requestSpec, responseSpec, likeMatch_accountNo);
		    	     System.out.println("------------------------EXACT LOAN SAVING PRODUCTS BY"+"  "+"likeMatch_accountNo"+"="+likeMatch_accountNo+"------------------------------\n");    	
				     System.out.println(LikeMatchLoanDetail_Search_By_AccountNo+"\n");   
		     	   		    	       
		   
		     		     
	       /*******************************************************Center Search****************************************************/ 	
	     
				     ArrayList<HashMap> Centerdetail=new ArrayList<HashMap>();
				     Centerdetail =SearchHelper.exactMatchCenterSearch(requestSpec, responseSpec, String.valueOf(centerId));
				     if (Centerdetail != null) {
					        for (HashMap oneResult : Centerdetail) {	    		
					        externalId =(String)oneResult.get("entityExternalId");			        	
					        displayName=(String)oneResult.get("entityName");	        
					        likeMatch_externalId=externalId.substring(1,3);			        
					        likeMatch_displayName=displayName.substring(1,3);
					        likeMatch_groupId=String.valueOf(clientID).substring(1);
					    	}
					         ArrayList<HashMap> exactMatchCenterDetail_Search_By_CenterId=SearchHelper.exactMatchCenterSearch(requestSpec, responseSpec, String.valueOf(centerId));
					         System.out.println("------------------------EXACT SEARCH CENTER BY"+"  "+"centerId"+"="+centerId+"------------------------------\n");
					         System.out.println(exactMatchCenterDetail_Search_By_CenterId+"\n");		     
					    	
					    	 ArrayList<HashMap> exactMatchCenterDetail_Search_By_ExternalId=SearchHelper.exactMatchCenterSearch(requestSpec, responseSpec, externalId);
					    	 System.out.println("------------------------EXACT SEARCH CENTER BY"+"  "+"ExternalId"+"="+externalId+"------------------------------\n");
					    	 System.out.println(exactMatchCenterDetail_Search_By_ExternalId+"\n");
						     
						     ArrayList<HashMap> exactMatchCenterDetail_Search_By_Name =SearchHelper.exactMatchCenterSearch(requestSpec, responseSpec, displayName);
						     System.out.println("------------------------EXACT SEARCH CENTER BY"+"  "+"groupName"+"="+displayName+"------------------------------\n");
						     System.out.println(exactMatchCenterDetail_Search_By_Name+"\n");
						     
						     ArrayList<HashMap> LikeMatchCenterDetail_Search_By_CenterId=SearchHelper. likeMatchCenterSearch(requestSpec, responseSpec, likeMatch_displayName);
				    	     System.out.println("-------------------------LIKE SEARCH CENTERS BY"+"  "+"likeMatch_displayName"+"="+likeMatch_displayName+"------------------------------\n");    	
						     System.out.println(LikeMatchCenterDetail_Search_By_CenterId+"\n");
						     
						     ArrayList<HashMap> LikeMatchCenterDetail_Search_By_CenterName=SearchHelper. likeMatchCenterSearch(requestSpec, responseSpec,likeMatch_groupId );
				    	     System.out.println("-------------------------LIKE SEARCH CENTERS BY"+"  "+"likeMatch_externalId"+"="+likeMatch_externalId+"------------------------------\n");    	
						     System.out.println(LikeMatchCenterDetail_Search_By_CenterName+"\n");		     
					    	
					    	 ArrayList<HashMap> LikeMatchCenterDetail_Search_By_ExternalId=SearchHelper. likeMatchCenterSearch(requestSpec, responseSpec,likeMatch_externalId );
				    	     System.out.println("-------------------------LIKE SEARCH CENTERS BY"+"  "+"likeMatch_externalId"+"="+likeMatch_externalId+"------------------------------\n");    	
					    	 System.out.println(LikeMatchCenterDetail_Search_By_ExternalId+"\n");
				     }
		/***********************************************************Global Search****************************************************************/
				     
				     String search_parameter="000000001";
				     String like_search_parameter=search_parameter.substring(1,3);
				     
				     ArrayList<HashMap> exactMatch_Global_Search_detail=SearchHelper.exactMatchGlobalSearch(requestSpec, responseSpec, search_parameter);
		    	     System.out.println("-------------------------LIKE GLOBAL SEARCH BY"+"  "+"search_parameter"+"="+search_parameter+"------------------------------");    	
				     System.out.println(exactMatch_Global_Search_detail);   

				     ArrayList<HashMap> LikeMatch_Global_Search_detail=SearchHelper.likeMatchGlobalSearch(requestSpec, responseSpec, like_search_parameter);
		    	     System.out.println("-------------------------LIKE GLOBAL SEARCH BY"+"  "+"like_search_parameter"+"="+like_search_parameter+"------------------------------");    	
				     System.out.println(LikeMatch_Global_Search_detail);   
		     	   		 
					
	              
			    }
							        
	}
		     

	
	

