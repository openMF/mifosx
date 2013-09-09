package org.openmf.mifos.dataimport.populator.loan;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFDataValidationHelper;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.ss.SpreadsheetVersion;
import org.apache.poi.ss.usermodel.DataValidation;
import org.apache.poi.ss.usermodel.DataValidationConstraint;
import org.apache.poi.ss.usermodel.DataValidationHelper;
import org.apache.poi.ss.usermodel.Name;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.ss.util.CellReference;
import org.openmf.mifos.dataimport.dto.CompactLoan;
import org.openmf.mifos.dataimport.handler.Result;
import org.openmf.mifos.dataimport.http.RestClient;
import org.openmf.mifos.dataimport.populator.AbstractWorkbookPopulator;
import org.openmf.mifos.dataimport.populator.ClientSheetPopulator;
import org.openmf.mifos.dataimport.populator.ExtrasSheetPopulator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class LoanRepaymentWorkbookPopulator extends AbstractWorkbookPopulator {
	
    private static final Logger logger = LoggerFactory.getLogger(LoanRepaymentWorkbookPopulator.class);
	
	private final RestClient restClient;
	
	private String content;
	
	private ClientSheetPopulator clientSheetPopulator;
	private ExtrasSheetPopulator extrasSheetPopulator;
	private List<CompactLoan> loans = new ArrayList<CompactLoan>();
	
	private static final int OFFICE_NAME_COL = 0;
    private static final int CLIENT_NAME_COL = 1;
    private static final int LOAN_ACCOUNT_NO_COL = 2;
    private static final int PRODUCT_COL = 3;
    private static final int PRINCIPAL_COL = 4;
    private static final int AMOUNT_COL = 5;
    private static final int REPAID_ON_DATE_COL = 6;
    private static final int REPAYMENT_TYPE_COL = 7;
    private static final int ACCOUNT_NO_COL = 8;
    private static final int CHECK_NO_COL = 9;
    private static final int ROUTING_CODE_COL = 10;	
    private static final int RECEIPT_NO_COL = 11;
    private static final int BANK_NO_COL = 12;
    private static final int LOOKUP_CLIENT_NAME_COL = 14;
    private static final int LOOKUP_ACCOUNT_NO_COL = 15;
    private static final int LOOKUP_PRODUCT_COL = 16;
    private static final int LOOKUP_PRINCIPAL_COL = 17;
	
	public LoanRepaymentWorkbookPopulator(RestClient restClient) {
        this.restClient = restClient;
        clientSheetPopulator = new ClientSheetPopulator(restClient);
		extrasSheetPopulator = new ExtrasSheetPopulator(restClient);
    }
	
	@Override
    public Result downloadAndParse() {
		Result result =  clientSheetPopulator.downloadAndParse();
		if(result.isSuccess())
    		result = extrasSheetPopulator.downloadAndParse();
		if(result.isSuccess()) {
			try {
	        	restClient.createAuthToken();
	            content = restClient.get("loans");
	            Gson gson = new Gson();
	            JsonParser parser = new JsonParser();
	            JsonObject obj = parser.parse(content).getAsJsonObject();
	            JsonArray array = obj.getAsJsonArray("pageItems");
	            Iterator<JsonElement> iterator = array.iterator();
	            while(iterator.hasNext()) {
	            	JsonElement json = iterator.next();
	            	CompactLoan loan = gson.fromJson(json, CompactLoan.class);
	            	if(loan.isActive())
	            	  loans.add(loan);
	            } 
	       } catch (Exception e) {
	           result.addError(e.getMessage());
	           logger.error(e.getMessage());
	       }
		}
    	return result;
    }

    @Override
    public Result populate(Workbook workbook) {
    	Sheet loanRepaymentSheet = workbook.createSheet("LoanRepayment");
    	setLayout(loanRepaymentSheet);
    	Result result = clientSheetPopulator.populate(workbook);
    	if(result.isSuccess())
    		result = extrasSheetPopulator.populate(workbook);
    	if(result.isSuccess()) {
    		int rowIndex = 1;
        	Row row;
        	Collections.sort(loans, CompactLoan.ClientNameComparator);
        	try{
        		for(CompactLoan loan : loans) {
        			row = loanRepaymentSheet.createRow(rowIndex++);
        			writeString(LOOKUP_CLIENT_NAME_COL, row, loan.getClientName());
        			writeInt(LOOKUP_ACCOUNT_NO_COL, row, Integer.parseInt(loan.getAccountNo()));
        			writeString(LOOKUP_PRODUCT_COL, row, loan.getLoanProductName());
        			writeDouble(LOOKUP_PRINCIPAL_COL, row, loan.getPrincipal());
        		}
    	   } catch (Exception e) {
    		result.addError(e.getMessage());
    		logger.error(e.getMessage());
    	    }
    	}
        if(result.isSuccess())
            result = setRules(loanRepaymentSheet);
        setDefaults(loanRepaymentSheet);
        return result;
    }

    private void setLayout(Sheet worksheet) {
    	Row rowHeader = worksheet.createRow(0);
        rowHeader.setHeight((short)500);
        worksheet.setColumnWidth(OFFICE_NAME_COL, 4000);
        worksheet.setColumnWidth(CLIENT_NAME_COL, 5000);
        worksheet.setColumnWidth(LOAN_ACCOUNT_NO_COL, 3000);
        worksheet.setColumnWidth(PRODUCT_COL, 4000);
        worksheet.setColumnWidth(PRINCIPAL_COL, 4000);
        worksheet.setColumnWidth(AMOUNT_COL, 4000);
        worksheet.setColumnWidth(REPAID_ON_DATE_COL, 3000);
        worksheet.setColumnWidth(REPAYMENT_TYPE_COL, 3000);
        worksheet.setColumnWidth(ACCOUNT_NO_COL, 3000);
        worksheet.setColumnWidth(CHECK_NO_COL, 3000);
        worksheet.setColumnWidth(RECEIPT_NO_COL, 3000);
        worksheet.setColumnWidth(ROUTING_CODE_COL, 3000);
        worksheet.setColumnWidth(BANK_NO_COL, 3000);
        worksheet.setColumnWidth(LOOKUP_CLIENT_NAME_COL, 5000);
        worksheet.setColumnWidth(LOOKUP_ACCOUNT_NO_COL, 3000);
        worksheet.setColumnWidth(LOOKUP_PRODUCT_COL, 3000);
        worksheet.setColumnWidth(LOOKUP_PRINCIPAL_COL, 3700);
        writeString(OFFICE_NAME_COL, rowHeader, "Office Name*");
        writeString(CLIENT_NAME_COL, rowHeader, "Client Name*");
        writeString(LOAN_ACCOUNT_NO_COL, rowHeader, "Account No.*");
        writeString(PRODUCT_COL, rowHeader, "Product Name");
        writeString(PRINCIPAL_COL, rowHeader, "Principal");
        writeString(AMOUNT_COL, rowHeader, "Amount Repaid*");
        writeString(REPAID_ON_DATE_COL, rowHeader, "Date*");
        writeString(REPAYMENT_TYPE_COL, rowHeader, "Type*");
        writeString(ACCOUNT_NO_COL, rowHeader, "Account No");
        writeString(CHECK_NO_COL, rowHeader, "Check No");
        writeString(RECEIPT_NO_COL, rowHeader, "Receipt No");
        writeString(ROUTING_CODE_COL, rowHeader, "Routing Code");
        writeString(BANK_NO_COL, rowHeader, "Bank No");
        writeString(LOOKUP_CLIENT_NAME_COL, rowHeader, "Lookup Client");
        writeString(LOOKUP_ACCOUNT_NO_COL, rowHeader, "Lookup Account");
        writeString(LOOKUP_PRODUCT_COL, rowHeader, "Lookup Product");
        writeString(LOOKUP_PRINCIPAL_COL, rowHeader, "Lookup Principal");
        
        
    }
    
    private Result setRules(Sheet worksheet) {
    	Result result = new Result();
    	try {
    		CellRangeAddressList officeNameRange = new  CellRangeAddressList(1, SpreadsheetVersion.EXCEL97.getLastRowIndex(), OFFICE_NAME_COL, OFFICE_NAME_COL);
        	CellRangeAddressList clientNameRange = new  CellRangeAddressList(1, SpreadsheetVersion.EXCEL97.getLastRowIndex(), CLIENT_NAME_COL, CLIENT_NAME_COL);
        	CellRangeAddressList accountNumberRange = new  CellRangeAddressList(1, SpreadsheetVersion.EXCEL97.getLastRowIndex(), LOAN_ACCOUNT_NO_COL, LOAN_ACCOUNT_NO_COL);
        	CellRangeAddressList repaymentTypeRange = new CellRangeAddressList(1, SpreadsheetVersion.EXCEL97.getLastRowIndex(), REPAYMENT_TYPE_COL, REPAYMENT_TYPE_COL);
        	
        	DataValidationHelper validationHelper = new HSSFDataValidationHelper((HSSFSheet)worksheet);
        	Workbook loanRepaymentWorkbook = worksheet.getWorkbook();
        	ArrayList<String> officeNames = new ArrayList<String>(Arrays.asList(clientSheetPopulator.getOfficeNames()));
        	
        	//Clients Named after Offices
        	for(Integer i = 0, j = 2; i < officeNames.size(); i++, j = j + 2) {
        		String lastColumnLetters = CellReference.convertNumToColString(clientSheetPopulator.getLastColumnLetters().get(i));
        		Name name = loanRepaymentWorkbook.createName();
        	    name.setNameName(officeNames.get(i));
        	    name.setRefersToFormula("Clients!$B$" + j + ":$" + lastColumnLetters + "$" + j);
        	}
        	
        	//Counting clients with active loans and starting and end addresses of cells
        	HashMap<String, Integer[]> hm = new HashMap<String, Integer[]>();
        	ArrayList<String> clientsWithActiveLoans = new ArrayList<String>();
        	int startIndex = 1, endIndex = 1;
        	String clientName = "";
        	
        	for(int i = 0; i < loans.size(); i++){
        		if(!clientName.equals(loans.get(i).getClientName())) {
        			endIndex = i + 1;
        			hm.put(clientName, new Integer[]{startIndex, endIndex});
        			startIndex = i + 2;
        			clientName = loans.get(i).getClientName();
        			clientsWithActiveLoans.add(clientName);
        		}
        		if(i == loans.size()-1) {
        			endIndex = i + 2;
        			hm.put(clientName, new Integer[]{startIndex, endIndex});
        		}
        	}
        	
        	//Account Number Named  after Clients
        	for(int j = 0; j < clientsWithActiveLoans.size(); j++) {
        		Name name = loanRepaymentWorkbook.createName();
        		name.setNameName(clientsWithActiveLoans.get(j).replaceAll(" ", "_"));
        		name.setRefersToFormula("LoanRepayment!$P$" + hm.get(clientsWithActiveLoans.get(j))[0] + ":$P$" + hm.get(clientsWithActiveLoans.get(j))[1]);
        	}
        	
        	//Payment Type Name
        	Name paymentTypeGroup = loanRepaymentWorkbook.createName();
        	paymentTypeGroup.setNameName("PaymentTypes");
        	paymentTypeGroup.setRefersToFormula("Extras!$D$2:$D$" + (extrasSheetPopulator.getPaymentTypesSize() + 1));
        	
        	DataValidationConstraint officeNameConstraint = validationHelper.createExplicitListConstraint(clientSheetPopulator.getOfficeNames());
        	DataValidationConstraint clientNameConstraint = validationHelper.createFormulaListConstraint("INDIRECT($A1)");
        	DataValidationConstraint accountNumberConstraint = validationHelper.createFormulaListConstraint("INDIRECT(SUBSTITUTE($B1,\" \",\"_\"))");
        	DataValidationConstraint paymentTypeConstraint = validationHelper.createFormulaListConstraint("PaymentTypes");
        	
        	DataValidation officeValidation = validationHelper.createValidation(officeNameConstraint, officeNameRange);
        	officeValidation.setSuppressDropDownArrow(false);
        	DataValidation clientValidation = validationHelper.createValidation(clientNameConstraint, clientNameRange);
        	clientValidation.setSuppressDropDownArrow(false);
        	DataValidation accountNumberValidation = validationHelper.createValidation(accountNumberConstraint, accountNumberRange);
        	accountNumberValidation.setSuppressDropDownArrow(false);
        	DataValidation repaymentTypeValidation = validationHelper.createValidation(paymentTypeConstraint, repaymentTypeRange);
        	repaymentTypeValidation.setSuppressDropDownArrow(false);
        	
        	worksheet.addValidationData(officeValidation);
            worksheet.addValidationData(clientValidation);
            worksheet.addValidationData(accountNumberValidation);
            worksheet.addValidationData(repaymentTypeValidation);
        	
    	} catch (RuntimeException re) {
    		result.addError(re.getMessage());
    	}
       return result;
    }
    
    private void setDefaults(Sheet worksheet) {
    	try {
    		for(Integer rowNo = 1; rowNo < 3000; rowNo++)
    		{
    			Row row = worksheet.getRow(rowNo);
    			if(row == null)
    				row = worksheet.createRow(rowNo);
    			writeFormula(PRODUCT_COL, row, "IF(ISERROR(VLOOKUP($C"+ (rowNo+1) +",$P$2:$R$27,2,FALSE)),\"\",VLOOKUP($C"+ (rowNo+1) +",$P$2:$R$27,2,FALSE))");
    			writeFormula(PRINCIPAL_COL, row, "IF(ISERROR(VLOOKUP($C"+ (rowNo+1) +",$P$2:$R$27,3,FALSE)),\"\",VLOOKUP($C"+ (rowNo+1) +",$P$2:$R$27,3,FALSE))");
    			
    		}
    	} catch (Exception e) {
    		logger.error(e.getMessage());
    	}
    }

}
