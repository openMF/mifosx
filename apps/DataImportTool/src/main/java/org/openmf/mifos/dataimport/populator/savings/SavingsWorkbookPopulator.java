package org.openmf.mifos.dataimport.populator.savings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFDataValidationHelper;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.ss.SpreadsheetVersion;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataValidation;
import org.apache.poi.ss.usermodel.DataValidationConstraint;
import org.apache.poi.ss.usermodel.DataValidationHelper;
import org.apache.poi.ss.usermodel.Name;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.ss.util.CellReference;
import org.openmf.mifos.dataimport.dto.SavingsProduct;
import org.openmf.mifos.dataimport.handler.Result;
import org.openmf.mifos.dataimport.http.RestClient;
import org.openmf.mifos.dataimport.populator.AbstractWorkbookPopulator;
import org.openmf.mifos.dataimport.populator.ClientSheetPopulator;
import org.openmf.mifos.dataimport.populator.PersonnelSheetPopulator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SavingsWorkbookPopulator extends AbstractWorkbookPopulator {

	private static final Logger logger = LoggerFactory.getLogger(SavingsWorkbookPopulator.class);
	
	private ClientSheetPopulator clientSheetPopulator;
	private PersonnelSheetPopulator personnelSheetPopulator;
	private SavingsProductSheetPopulator productSheetPopulator;
	
	@SuppressWarnings("CPD-START")
	private static final int OFFICE_NAME_COL = 0;
    private static final int CLIENT_NAME_COL = 1;
    private static final int PRODUCT_COL = 2;
    private static final int FIELD_OFFICER_NAME_COL = 3;
    private static final int SUBMITTED_ON_DATE_COL = 4;
    private static final int APPROVED_DATE_COL = 5;  
    private static final int ACTIVATION_DATE_COL = 6;
    private static final int CURRENCY_COL = 7;
    private static final int DECIMAL_PLACES_COL = 8;
    private static final int IN_MULTIPLES_OF_COL = 9;
    private static final int NOMINAL_ANNUAL_INTEREST_RATE_COL = 10;
	private static final int INTEREST_COMPOUNDING_PERIOD_COL = 11;
	private static final int INTEREST_POSTING_PERIOD_COL = 12;
	private static final int INTEREST_CALCULATION_COL = 13;
	private static final int INTEREST_CALCULATION_DAYS_IN_YEAR_COL = 14;
	private static final int MIN_OPENING_BALANCE_COL = 15;
	private static final int LOCKIN_PERIOD_COL = 16;
	private static final int LOCKIN_PERIOD_FREQUENCY_COL = 17;
	private static final int WITHDRAWAL_FEE_AMOUNT_COL = 18;
	private static final int WITHDRAWAL_FEE_TYPE_COL = 19;
	private static final int ANNUAL_FEE_COL = 20;
	private static final int ANNUAL_FEE_ON_MONTH_DAY_COL = 21;
    private static final int LOOKUP_CLIENT_NAME_COL = 31;
    private static final int LOOKUP_ACTIVATION_DATE_COL = 32;
    @SuppressWarnings("CPD-END")
	
	public SavingsWorkbookPopulator(RestClient restClient) {
    	clientSheetPopulator = new ClientSheetPopulator(restClient);
    	personnelSheetPopulator = new PersonnelSheetPopulator(Boolean.TRUE, restClient);
    	productSheetPopulator = new SavingsProductSheetPopulator(restClient);
    }
	
	  @Override
	    public Result downloadAndParse() {
	    	Result result =  clientSheetPopulator.downloadAndParse();
	    	if(result.isSuccess())
	    		result = personnelSheetPopulator.downloadAndParse();
	    	if(result.isSuccess())
	    		result = productSheetPopulator.downloadAndParse();
	    	return result;
	    }
	  
	  @Override
	    public Result populate(Workbook workbook) {
	    	Sheet savingsSheet = workbook.createSheet("Savings");
	    	Result result = clientSheetPopulator.populate(workbook);
	    	if(result.isSuccess())
	    		result = personnelSheetPopulator.populate(workbook);
	    	if(result.isSuccess())
	    		result = productSheetPopulator.populate(workbook);
	    	if(result.isSuccess())
	            result = setRules(savingsSheet);
	    	if(result.isSuccess())
	            result = setDefaults(savingsSheet);
	    	if(result.isSuccess())
	    	    setDateLookupTable(savingsSheet, clientSheetPopulator.getClients(), LOOKUP_CLIENT_NAME_COL, LOOKUP_ACTIVATION_DATE_COL);
	    	setLayout(savingsSheet);
	        return result;
	    }
	  
	  private void setLayout(Sheet worksheet) {
	    	Row rowHeader = worksheet.createRow(0);
	        rowHeader.setHeight((short)500);
	        worksheet.setColumnWidth(OFFICE_NAME_COL, 4000);
            worksheet.setColumnWidth(CLIENT_NAME_COL, 4000);
            worksheet.setColumnWidth(PRODUCT_COL, 4000);
            worksheet.setColumnWidth(FIELD_OFFICER_NAME_COL, 4000);
            worksheet.setColumnWidth(SUBMITTED_ON_DATE_COL, 3200);
            worksheet.setColumnWidth(APPROVED_DATE_COL, 3200);
            worksheet.setColumnWidth(ACTIVATION_DATE_COL, 3700);
            worksheet.setColumnWidth(CURRENCY_COL, 2500);
            worksheet.setColumnWidth(DECIMAL_PLACES_COL, 2500);
            worksheet.setColumnWidth(IN_MULTIPLES_OF_COL, 3000);
            
            worksheet.setColumnWidth(NOMINAL_ANNUAL_INTEREST_RATE_COL, 3000);
            worksheet.setColumnWidth(INTEREST_COMPOUNDING_PERIOD_COL, 3000);
            worksheet.setColumnWidth(INTEREST_POSTING_PERIOD_COL, 3000);
            worksheet.setColumnWidth(INTEREST_CALCULATION_COL, 4000);
            worksheet.setColumnWidth(INTEREST_CALCULATION_DAYS_IN_YEAR_COL, 3000);
            worksheet.setColumnWidth(MIN_OPENING_BALANCE_COL, 4000);
            worksheet.setColumnWidth(LOCKIN_PERIOD_COL, 3000);
            worksheet.setColumnWidth(LOCKIN_PERIOD_FREQUENCY_COL, 3000);
            worksheet.setColumnWidth(WITHDRAWAL_FEE_AMOUNT_COL, 3000);
            worksheet.setColumnWidth(WITHDRAWAL_FEE_TYPE_COL, 3000);
            worksheet.setColumnWidth(ANNUAL_FEE_COL, 3000);
            worksheet.setColumnWidth(ANNUAL_FEE_ON_MONTH_DAY_COL, 3000);
            
            worksheet.setColumnWidth(LOOKUP_CLIENT_NAME_COL, 6000);
            worksheet.setColumnWidth(LOOKUP_ACTIVATION_DATE_COL, 6000);
            
            writeString(OFFICE_NAME_COL, rowHeader, "Office Name*");
            writeString(CLIENT_NAME_COL, rowHeader, "Client Name*");
            writeString(PRODUCT_COL, rowHeader, "Product*");
            writeString(FIELD_OFFICER_NAME_COL, rowHeader, "Field Officer*");
            writeString(SUBMITTED_ON_DATE_COL, rowHeader, "Submitted On*");
            writeString(APPROVED_DATE_COL, rowHeader, "Approved On*");
            writeString(ACTIVATION_DATE_COL, rowHeader, "Activation Date*");
            writeString(CURRENCY_COL, rowHeader, "Currency");
            writeString(DECIMAL_PLACES_COL, rowHeader, "Decimal Places");
            writeString(IN_MULTIPLES_OF_COL, rowHeader, "In Multiples Of");
            writeString(NOMINAL_ANNUAL_INTEREST_RATE_COL, rowHeader, "Interest Rate %*");
            writeString(INTEREST_COMPOUNDING_PERIOD_COL, rowHeader, "Interest Compounding Period*");
            writeString(INTEREST_POSTING_PERIOD_COL, rowHeader, "Interest Posting Period*");
            writeString(INTEREST_CALCULATION_COL, rowHeader, "Interest Calculated*");
            writeString(INTEREST_CALCULATION_DAYS_IN_YEAR_COL, rowHeader, "# Days in Year*");
            writeString(MIN_OPENING_BALANCE_COL, rowHeader, "Min Opening Balance");
            writeString(LOCKIN_PERIOD_COL, rowHeader, "Locked In For");
            writeString(WITHDRAWAL_FEE_AMOUNT_COL, rowHeader, "Withdrawal Fee");
            writeString(ANNUAL_FEE_COL, rowHeader, "Annual Fee");
            writeString(ANNUAL_FEE_ON_MONTH_DAY_COL, rowHeader, "On Date");
            
            writeString(LOOKUP_CLIENT_NAME_COL, rowHeader, "Client Name");
            writeString(LOOKUP_ACTIVATION_DATE_COL, rowHeader, "Client Activation Date");
	  }
	  
	  private Result setRules(Sheet worksheet) {
	    	Result result = new Result();
	    	try {
	    		CellRangeAddressList officeNameRange = new  CellRangeAddressList(1, SpreadsheetVersion.EXCEL97.getLastRowIndex(), OFFICE_NAME_COL, OFFICE_NAME_COL);
	        	CellRangeAddressList clientNameRange = new  CellRangeAddressList(1, SpreadsheetVersion.EXCEL97.getLastRowIndex(), CLIENT_NAME_COL, CLIENT_NAME_COL);
	        	CellRangeAddressList productNameRange = new  CellRangeAddressList(1, SpreadsheetVersion.EXCEL97.getLastRowIndex(), PRODUCT_COL, PRODUCT_COL);
	        	CellRangeAddressList fieldOfficerRange = new CellRangeAddressList(1, SpreadsheetVersion.EXCEL97.getLastRowIndex(), FIELD_OFFICER_NAME_COL, FIELD_OFFICER_NAME_COL);
	        	CellRangeAddressList submittedDateRange = new CellRangeAddressList(1, SpreadsheetVersion.EXCEL97.getLastRowIndex(), SUBMITTED_ON_DATE_COL, SUBMITTED_ON_DATE_COL);
	        	CellRangeAddressList approvedDateRange = new CellRangeAddressList(1, SpreadsheetVersion.EXCEL97.getLastRowIndex(), APPROVED_DATE_COL, APPROVED_DATE_COL);
	        	CellRangeAddressList activationDateRange = new CellRangeAddressList(1, SpreadsheetVersion.EXCEL97.getLastRowIndex(), ACTIVATION_DATE_COL, ACTIVATION_DATE_COL);
	        	CellRangeAddressList interestCompudingPeriodRange = new CellRangeAddressList(1, SpreadsheetVersion.EXCEL97.getLastRowIndex(), INTEREST_COMPOUNDING_PERIOD_COL, INTEREST_COMPOUNDING_PERIOD_COL);
	        	CellRangeAddressList interestPostingPeriodRange = new CellRangeAddressList(1, SpreadsheetVersion.EXCEL97.getLastRowIndex(), INTEREST_POSTING_PERIOD_COL, INTEREST_POSTING_PERIOD_COL);
	        	CellRangeAddressList interestCalculationRange = new CellRangeAddressList(1, SpreadsheetVersion.EXCEL97.getLastRowIndex(), INTEREST_CALCULATION_COL, INTEREST_CALCULATION_COL);
	        	CellRangeAddressList interestCalculationDaysInYearRange = new CellRangeAddressList(1, SpreadsheetVersion.EXCEL97.getLastRowIndex(), INTEREST_CALCULATION_DAYS_IN_YEAR_COL, INTEREST_CALCULATION_DAYS_IN_YEAR_COL);
	        	CellRangeAddressList lockinPeriodFrequencyRange = new CellRangeAddressList(1, SpreadsheetVersion.EXCEL97.getLastRowIndex(), LOCKIN_PERIOD_FREQUENCY_COL, LOCKIN_PERIOD_FREQUENCY_COL);
	        	CellRangeAddressList withdrawalFeeTypeRange = new CellRangeAddressList(1, SpreadsheetVersion.EXCEL97.getLastRowIndex(), WITHDRAWAL_FEE_TYPE_COL, WITHDRAWAL_FEE_TYPE_COL);
	        	
	        	DataValidationHelper validationHelper = new HSSFDataValidationHelper((HSSFSheet)worksheet);
	        	Workbook savingsWorkbook = worksheet.getWorkbook();
	        	ArrayList<String> officeNames = new ArrayList<String>(Arrays.asList(clientSheetPopulator.getOfficeNames()));
	        	List<SavingsProduct> products = productSheetPopulator.getProducts();
	        	
	        	//Client and Loan Officer Names for each office
	        	for(Integer i = 0, j = 2; i < officeNames.size(); i++, j = j + 2) {
	        		String lastColumnLettersOfClients = CellReference.convertNumToColString(clientSheetPopulator.getLastColumnLetters().get(i));
	        		String lastColumnLettersOfStaff = CellReference.convertNumToColString(personnelSheetPopulator.getLastColumnLetters().get(i));
	        		Name clientName = savingsWorkbook.createName();
	        		Name fieldOfficerName = savingsWorkbook.createName();
	        	    clientName.setNameName(officeNames.get(i));
	        	    fieldOfficerName.setNameName(officeNames.get(i)+"X");
	        	    clientName.setRefersToFormula("Clients!$B$" + j + ":$" + lastColumnLettersOfClients + "$" + j);
	        	    fieldOfficerName.setRefersToFormula("Staff!$B$" + j + ":$" + lastColumnLettersOfStaff + "$" + j);
	        	}
	        	
	        	//Product Name
	        	Name productGroup = savingsWorkbook.createName();
	        	productGroup.setNameName("Products");
	        	productGroup.setRefersToFormula("Products!$B$2:$B$" + (productSheetPopulator.getProductsSize() + 1));
	        	
	        	//Default Interest Rate, Interest Compounding Period, Interest Posting Period, Interest Calculation, Interest Calculation Days In Year,
	        	//Minimum Opening Balance, Lockin Period, Lockin Period Frequency, Withdrawal Fee Amount, Withdrawal Fee Type, Annual Fee, Annual Fee on Date
	        	//Names for each product
	        	for(Integer i = 0; i < products.size(); i++) {
	        		Name interestRateName = savingsWorkbook.createName();
	        		Name interestCompoundingPeriodName = savingsWorkbook.createName();
	        		Name interestPostingPeriodName = savingsWorkbook.createName();
	        		Name interestCalculationName = savingsWorkbook.createName();
	        		Name daysInYearName = savingsWorkbook.createName();
	        		Name minOpeningBalanceName = savingsWorkbook.createName();
	        		Name lockinPeriodName = savingsWorkbook.createName();
	        		Name lockinPeriodFrequencyName = savingsWorkbook.createName();
	        		Name withdrawalFeeAmountName = savingsWorkbook.createName();
	        		Name withdrawalFeeTypeName = savingsWorkbook.createName();
	        		Name annualFeeName = savingsWorkbook.createName();
	        		Name annualFeeOnDateName = savingsWorkbook.createName();
	        		Name currencyName = savingsWorkbook.createName();
	        		Name decimalPlacesName = savingsWorkbook.createName();
	        		Name inMultiplesOfName = savingsWorkbook.createName();
	        		SavingsProduct product = products.get(i);
	        		String productName = product.getName();
	        		if(product.getNominalAnnualInterestRate() != null) {
	        		   interestRateName.setNameName(productName + "_Interest_Rate");
	        		   interestRateName.setRefersToFormula("Products!$C$" + (i + 2));
	        		}
	        		interestCompoundingPeriodName.setNameName(productName + "_Interest_Compouding");
	        		interestPostingPeriodName.setNameName(productName + "_Interest_Posting");
	        		interestCalculationName.setNameName(productName + "_Interest_Calculation");
	        		daysInYearName.setNameName(productName + "_Days_In_Year");
	        		currencyName.setNameName(productName + "_Currency");
	        		decimalPlacesName.setNameName(productName + "_Decimal_Places");
	        		interestCompoundingPeriodName.setRefersToFormula("Products!$D$" + (i + 2));
	        		interestPostingPeriodName.setRefersToFormula("Products!$E$" + (i + 2));
	        		interestCalculationName.setRefersToFormula("Products!$F$" + (i + 2));
	        		daysInYearName.setRefersToFormula("Products!$G$" + (i + 2));
	        		currencyName.setRefersToFormula("Products!$O$" + (i + 2));
	        		decimalPlacesName.setRefersToFormula("Products!$P$" + (i + 2));
	        		if(product.getMinRequiredOpeningBalance() != null) {
	        		   minOpeningBalanceName.setNameName(productName + "_Min_Balance");
	        		   minOpeningBalanceName.setRefersToFormula("Products!$H$" + (i + 2));
	        		}
	        		if(product.getLockinPeriodFrequency() != null) {
	        		   lockinPeriodName.setNameName(productName + "_Lockin_Period");
	        		   lockinPeriodName.setRefersToFormula("Products!$I$" + (i + 2));
	        		}
	        		if(product.getLockinPeriodFrequencyType() != null) {
	        		   lockinPeriodFrequencyName.setNameName(productName + "_Lockin_Frequency");
	        		   lockinPeriodFrequencyName.setRefersToFormula("Products!$J$" + (i + 2));
	        		}
	        		if(product.getWithdrawalFeeAmount() != null) {
	        		   withdrawalFeeAmountName.setNameName(productName + "_Withdrawal_Fee");
	        		   withdrawalFeeAmountName.setRefersToFormula("Products!$K$" + (i + 2));
	        		}
	        		if(product.getWithdrawalFeeType() != null) {
	        		   withdrawalFeeTypeName.setNameName(productName + "_Withdrawal_Fee_Type");
	        		   withdrawalFeeTypeName.setRefersToFormula("Products!$L$" + (i + 2));
	        		}
	        		if(product.getAnnualFeeAmount() != null) {
	        		   annualFeeName.setNameName(productName + "_Annual_Fee");
	        		   annualFeeName.setRefersToFormula("Products!$M$" + (i + 2));
	        		}
	        		if(product.getAnnualFeeOnMonthDay() != null) {
	        		   annualFeeOnDateName.setNameName(productName + "_Annual_Fee_Date");
	        		   annualFeeOnDateName.setRefersToFormula("Products!$N$" + (i + 2));
	        		}
	        		
	        		if(product.getCurrency().getInMultiplesOf() != null) {
	        			inMultiplesOfName.setNameName(productName + "_In_Multiples");
	        			inMultiplesOfName.setRefersToFormula("Products!$Q$" + (i + 2));
	        		}
	        	}
	        	
	        	DataValidationConstraint officeNameConstraint = validationHelper.createExplicitListConstraint(clientSheetPopulator.getOfficeNames());
	        	DataValidationConstraint clientNameConstraint = validationHelper.createFormulaListConstraint("INDIRECT($A1)");
	        	DataValidationConstraint productNameConstraint = validationHelper.createFormulaListConstraint("Products");
	        	DataValidationConstraint fieldOfficerNameConstraint = validationHelper.createFormulaListConstraint("INDIRECT(CONCATENATE($A1,\"X\"))");
	        	DataValidationConstraint submittedDateConstraint = validationHelper.createDateConstraint(DataValidationConstraint.OperatorType.BETWEEN, "=VLOOKUP($B1,$AF$2:$AG$" + (clientSheetPopulator.getClientsSize() + 1) + ",2,FALSE)", "=TODAY()", "dd/mm/yy");
	        	DataValidationConstraint approvalDateConstraint = validationHelper.createDateConstraint(DataValidationConstraint.OperatorType.BETWEEN, "=$E1", "=TODAY()", "dd/mm/yy");
	        	DataValidationConstraint activationDateConstraint = validationHelper.createDateConstraint(DataValidationConstraint.OperatorType.BETWEEN, "=$F1", "=TODAY()", "dd/mm/yy");
	        	DataValidationConstraint interestCompudingPeriodConstraint = validationHelper.createExplicitListConstraint(new String[] {"Daily","Monthly"});
	        	DataValidationConstraint interestPostingPeriodConstraint = validationHelper.createExplicitListConstraint(new String[] {"Monthly","Quarterly","Annually"});
	        	DataValidationConstraint interestCalculationConstraint = validationHelper.createExplicitListConstraint(new String[] {"Daily Balance","Average Daily Balance"});
	        	DataValidationConstraint interestCalculationDaysInYearConstraint = validationHelper.createExplicitListConstraint(new String[] {"360 Days","365 Days"});
	        	DataValidationConstraint lockinPeriodFrequencyConstraint = validationHelper.createExplicitListConstraint(new String[] {"Days","Weeks","Months","Years"});
	        	DataValidationConstraint withdrawalFeeTypeConstraint = validationHelper.createExplicitListConstraint(new String[] {"Flat","% of Amount"});
	        	
	        	
	        	DataValidation officeValidation = validationHelper.createValidation(officeNameConstraint, officeNameRange);
	        	officeValidation.setSuppressDropDownArrow(false);
	        	DataValidation clientValidation = validationHelper.createValidation(clientNameConstraint, clientNameRange);
	        	clientValidation.setSuppressDropDownArrow(false);
	        	DataValidation productNameValidation = validationHelper.createValidation(productNameConstraint, productNameRange);
	        	productNameValidation.setSuppressDropDownArrow(false);
	        	DataValidation fieldOfficerValidation = validationHelper.createValidation(fieldOfficerNameConstraint, fieldOfficerRange);
	        	fieldOfficerValidation.setSuppressDropDownArrow(false);
	        	DataValidation interestCompudingPeriodValidation = validationHelper.createValidation(interestCompudingPeriodConstraint, interestCompudingPeriodRange);
	        	fieldOfficerValidation.setSuppressDropDownArrow(false);
	        	DataValidation interestPostingPeriodValidation = validationHelper.createValidation(interestPostingPeriodConstraint, interestPostingPeriodRange);
	        	fieldOfficerValidation.setSuppressDropDownArrow(false);
	        	DataValidation interestCalculationValidation = validationHelper.createValidation(interestCalculationConstraint, interestCalculationRange);
	        	fieldOfficerValidation.setSuppressDropDownArrow(false);
	        	DataValidation interestCalculationDaysInYearValidation = validationHelper.createValidation(interestCalculationDaysInYearConstraint, interestCalculationDaysInYearRange);
	        	fieldOfficerValidation.setSuppressDropDownArrow(false);
	        	DataValidation lockinPeriodFrequencyValidation = validationHelper.createValidation(lockinPeriodFrequencyConstraint, lockinPeriodFrequencyRange);
	        	fieldOfficerValidation.setSuppressDropDownArrow(false);
	        	DataValidation withdrawalFeeTypeValidation = validationHelper.createValidation(withdrawalFeeTypeConstraint, withdrawalFeeTypeRange);
	        	fieldOfficerValidation.setSuppressDropDownArrow(false);
	        	DataValidation submittedDateValidation = validationHelper.createValidation(submittedDateConstraint, submittedDateRange);
	        	DataValidation approvalDateValidation = validationHelper.createValidation(approvalDateConstraint, approvedDateRange);
	        	DataValidation activationDateValidation = validationHelper.createValidation(activationDateConstraint, activationDateRange);
	        	
	        	worksheet.addValidationData(officeValidation);
	            worksheet.addValidationData(clientValidation);
	            worksheet.addValidationData(productNameValidation);
	            worksheet.addValidationData(fieldOfficerValidation);
	            worksheet.addValidationData(submittedDateValidation);
	            worksheet.addValidationData(approvalDateValidation);
	            worksheet.addValidationData(activationDateValidation);
	            worksheet.addValidationData(interestCompudingPeriodValidation);
	            worksheet.addValidationData(interestPostingPeriodValidation);
	            worksheet.addValidationData(interestCalculationValidation);
	            worksheet.addValidationData(interestCalculationDaysInYearValidation);
	            worksheet.addValidationData(lockinPeriodFrequencyValidation);
	            worksheet.addValidationData(withdrawalFeeTypeValidation);
	        	
	    	} catch (RuntimeException re) {
	    		result.addError(re.getMessage());
	    	}
	       return result;
	    }
	  
	  private Result setDefaults(Sheet worksheet) {
	    	Result result = new Result();
	    	Workbook workbook =  worksheet.getWorkbook();
	    	CellStyle dateCellStyle = workbook.createCellStyle();
            short df = workbook.createDataFormat().getFormat("dd-mmm");
            dateCellStyle.setDataFormat(df);
	    	try {
	    		for(Integer rowNo = 1; rowNo < 1000; rowNo++)
	    		{
	    			Row row = worksheet.createRow(rowNo);
	    			writeFormula(CURRENCY_COL, row, "IF(ISERROR(INDIRECT(CONCATENATE($C" + (rowNo + 1) + ",\"_Currency\"))),\"\",INDIRECT(CONCATENATE($C"+ (rowNo + 1) + ",\"_Currency\")))");
	    			writeFormula(DECIMAL_PLACES_COL, row, "IF(ISERROR(INDIRECT(CONCATENATE($C" + (rowNo + 1) + ",\"_Decimal_Places\"))),\"\",INDIRECT(CONCATENATE($C"+ (rowNo + 1) + ",\"_Decimal_Places\")))");
	    			writeFormula(IN_MULTIPLES_OF_COL, row, "IF(ISERROR(INDIRECT(CONCATENATE($C" + (rowNo + 1) + ",\"_In_Multiples\"))),\"\",INDIRECT(CONCATENATE($C"+ (rowNo + 1) + ",\"_In_Multiples\")))");
	    			writeFormula(NOMINAL_ANNUAL_INTEREST_RATE_COL, row, "IF(ISERROR(INDIRECT(CONCATENATE($C" + (rowNo + 1) + ",\"_Interest_Rate\"))),\"\",INDIRECT(CONCATENATE($C"+ (rowNo + 1) + ",\"_Interest_Rate\")))");
	    			writeFormula(INTEREST_COMPOUNDING_PERIOD_COL, row, "IF(ISERROR(INDIRECT(CONCATENATE($C" + (rowNo + 1) + ",\"_Interest_Compouding\"))),\"\",INDIRECT(CONCATENATE($C"+ (rowNo + 1) + ",\"_Interest_Compouding\")))");
	    			writeFormula(INTEREST_POSTING_PERIOD_COL, row, "IF(ISERROR(INDIRECT(CONCATENATE($C" + (rowNo + 1) + ",\"_Interest_Posting\"))),\"\",INDIRECT(CONCATENATE($C"+ (rowNo + 1) + ",\"_Interest_Posting\")))");
	    			writeFormula(INTEREST_CALCULATION_COL, row, "IF(ISERROR(INDIRECT(CONCATENATE($C" + (rowNo + 1) + ",\"_Interest_Calculation\"))),\"\",INDIRECT(CONCATENATE($C"+ (rowNo + 1) + ",\"_Interest_Calculation\")))");
	    			writeFormula(INTEREST_CALCULATION_DAYS_IN_YEAR_COL, row, "IF(ISERROR(INDIRECT(CONCATENATE($C" + (rowNo + 1) + ",\"_Days_In_Year\"))),\"\",INDIRECT(CONCATENATE($C"+ (rowNo + 1) + ",\"_Days_In_Year\")))");
	    			writeFormula(MIN_OPENING_BALANCE_COL, row, "IF(ISERROR(INDIRECT(CONCATENATE($C" + (rowNo + 1) + ",\"_Min_Balance\"))),\"\",INDIRECT(CONCATENATE($C"+ (rowNo + 1) + ",\"_Min_Balance\")))");
	    			writeFormula(LOCKIN_PERIOD_COL, row, "IF(ISERROR(INDIRECT(CONCATENATE($C" + (rowNo + 1) + ",\"_Lockin_Period\"))),\"\",INDIRECT(CONCATENATE($C"+ (rowNo + 1) + ",\"_Lockin_Period\")))");
	    			writeFormula(LOCKIN_PERIOD_FREQUENCY_COL, row, "IF(ISERROR(INDIRECT(CONCATENATE($C" + (rowNo + 1) + ",\"_Lockin_Frequency\"))),\"\",INDIRECT(CONCATENATE($C"+ (rowNo + 1) + ",\"_Lockin_Frequency\")))");
	    			writeFormula(WITHDRAWAL_FEE_AMOUNT_COL, row, "IF(ISERROR(INDIRECT(CONCATENATE($C" + (rowNo + 1) + ",\"_Withdrawal_Fee\"))),\"\",INDIRECT(CONCATENATE($C"+ (rowNo + 1) + ",\"_Withdrawal_Fee\")))");
	    			writeFormula(WITHDRAWAL_FEE_TYPE_COL, row, "IF(ISERROR(INDIRECT(CONCATENATE($C" + (rowNo + 1) + ",\"_Withdrawal_Fee_Type\"))),\"\",INDIRECT(CONCATENATE($C"+ (rowNo + 1) + ",\"_Withdrawal_Fee_Type\")))");
	    			writeFormula(ANNUAL_FEE_COL, row, "IF(ISERROR(INDIRECT(CONCATENATE($C" + (rowNo + 1) + ",\"_Annual_Fee\"))),\"\",INDIRECT(CONCATENATE($C"+ (rowNo + 1) + ",\"_Annual_Fee\")))");
	    			writeFormula(ANNUAL_FEE_ON_MONTH_DAY_COL, row, "IF(ISERROR(INDIRECT(CONCATENATE($C" + (rowNo + 1) + ",\"_Annual_Fee_Date\"))),\"\",INDIRECT(CONCATENATE($C"+ (rowNo + 1) + ",\"_Annual_Fee_Date\")))");
	    			row.getCell(ANNUAL_FEE_ON_MONTH_DAY_COL).setCellStyle(dateCellStyle);
	    		}
	    		
	    	} catch (RuntimeException re) {
	    		logger.error(re.getMessage());
	    		result.addError(re.getMessage());
	    	}
	       return result;
	    	}
	    	
}
