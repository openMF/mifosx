package org.openmf.mifos.dataimport.populator.loan;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFDataValidationHelper;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.ss.SpreadsheetVersion;
import org.apache.poi.ss.usermodel.Cell;
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
import org.openmf.mifos.dataimport.dto.LoanProduct;
import org.openmf.mifos.dataimport.handler.Result;
import org.openmf.mifos.dataimport.http.RestClient;
import org.openmf.mifos.dataimport.populator.AbstractWorkbookPopulator;
import org.openmf.mifos.dataimport.populator.ClientSheetPopulator;
import org.openmf.mifos.dataimport.populator.ExtrasSheetPopulator;
import org.openmf.mifos.dataimport.populator.PersonnelSheetPopulator;

public class LoanWorkbookPopulator extends AbstractWorkbookPopulator {
	
//	private static final Logger logger = LoggerFactory.getLogger(LoanWorkbookPopulator.class);
	
	private ClientSheetPopulator clientSheetPopulator;
	private PersonnelSheetPopulator personnelSheetPopulator;
	private LoanProductSheetPopulator productSheetPopulator;
	private ExtrasSheetPopulator extrasSheetPopulator;
	
	@SuppressWarnings("CPD-START")
	private static final int OFFICE_NAME_COL = 0;
    private static final int CLIENT_NAME_COL = 1;
    private static final int PRODUCT_COL = 2;
    private static final int LOAN_OFFICER_NAME_COL = 3;
    private static final int SUBMITTED_ON_DATE_COL = 4;
    private static final int APPROVED_DATE_COL = 5;
    private static final int DISBURSED_DATE_COL = 6;
    private static final int DISBURSED_PAYMENT_TYPE_COL = 7;
    private static final int FUND_NAME_COL = 8;   
    private static final int PRINCIPAL_COL = 9;
    private static final int NO_OF_REPAYMENTS_COL = 10;
    private static final int REPAID_EVERY_COL = 11;
    private static final int REPAID_EVERY_FREQUENCY_COL = 12;
    private static final int LOAN_TERM_COL = 13;
    private static final int LOAN_TERM_FREQUENCY_COL = 14;
    private static final int NOMINAL_INTEREST_RATE_COL = 15;
    private static final int NOMINAL_INTEREST_RATE_FREQUENCY_COL = 16;
    private static final int AMORTIZATION_COL = 17;
    private static final int INTEREST_METHOD_COL = 18;
    private static final int INTEREST_CALCULATION_PERIOD_COL = 19;
    private static final int ARREARS_TOLERANCE_COL = 20;
    private static final int REPAYMENT_STRATEGY_COL = 21;
    private static final int GRACE_ON_PRINCIPAL_PAYMENT_COL = 22;
    private static final int GRACE_ON_INTEREST_PAYMENT_COL = 23;
    private static final int GRACE_ON_INTEREST_CHARGED_COL = 24;
    private static final int INTEREST_CHARGED_FROM_COL = 25;
    private static final int FIRST_REPAYMENT_COL = 26;
    private static final int TOTAL_AMOUNT_REPAID_COL = 27;
    private static final int LAST_REPAYMENT_DATE_COL = 28;
    private static final int REPAYMENT_TYPE_COL = 29;
    private static final int LOOKUP_CLIENT_NAME_COL = 42;
    private static final int LOOKUP_ACTIVATION_DATE_COL = 43;
    @SuppressWarnings("CPD-END")
	
	public LoanWorkbookPopulator(RestClient restClient) {
		clientSheetPopulator = new ClientSheetPopulator(restClient);
    	personnelSheetPopulator = new PersonnelSheetPopulator(Boolean.TRUE, restClient);
    	productSheetPopulator = new LoanProductSheetPopulator(restClient);
    	extrasSheetPopulator = new ExtrasSheetPopulator(restClient);
    }
	
	    @Override
	    public Result downloadAndParse() {
	    	Result result =  clientSheetPopulator.downloadAndParse();
	    	if(result.isSuccess())
	    		result = personnelSheetPopulator.downloadAndParse();
	    	if(result.isSuccess())
	    		result = productSheetPopulator.downloadAndParse();
	    	if(result.isSuccess())
	    		result = extrasSheetPopulator.downloadAndParse();
	    	return result;
	    }

	    @Override
	    public Result populate(Workbook workbook) {
	    	Sheet loanSheet = workbook.createSheet("Loans");
	    	Result result = clientSheetPopulator.populate(workbook);
	    	if(result.isSuccess())
	    		result = personnelSheetPopulator.populate(workbook);
	    	if(result.isSuccess())
	    		result = productSheetPopulator.populate(workbook);
	    	if(result.isSuccess())
	    		result = extrasSheetPopulator.populate(workbook);
	    	setLayout(loanSheet);
	    	if(result.isSuccess())
	            result = setRules(loanSheet);
	    	if(result.isSuccess())
	            result = setDefaults(loanSheet);
	    	setDateLookupTable(loanSheet, clientSheetPopulator.getClients(), LOOKUP_CLIENT_NAME_COL, LOOKUP_ACTIVATION_DATE_COL);
	        return result;
	    }
	    
	    private void setLayout(Sheet worksheet) {
	    	Row rowHeader = worksheet.createRow(0);
	        rowHeader.setHeight((short)500);
	        worksheet.setColumnWidth(OFFICE_NAME_COL, 4000);
            worksheet.setColumnWidth(CLIENT_NAME_COL, 4000);
            worksheet.setColumnWidth(PRODUCT_COL, 4000);
            worksheet.setColumnWidth(LOAN_OFFICER_NAME_COL, 4000);
            worksheet.setColumnWidth(SUBMITTED_ON_DATE_COL, 3200);
            worksheet.setColumnWidth(APPROVED_DATE_COL, 3200);
            worksheet.setColumnWidth(DISBURSED_DATE_COL, 3700);
            worksheet.setColumnWidth(DISBURSED_PAYMENT_TYPE_COL, 4000);
            worksheet.setColumnWidth(FUND_NAME_COL, 3000);
            worksheet.setColumnWidth(PRINCIPAL_COL, 3000);
            worksheet.setColumnWidth(LOAN_TERM_COL, 2000);
            worksheet.setColumnWidth(LOAN_TERM_FREQUENCY_COL, 2500);
            worksheet.setColumnWidth(NO_OF_REPAYMENTS_COL, 3800);
            worksheet.setColumnWidth(REPAID_EVERY_COL, 2000);
            worksheet.setColumnWidth(REPAID_EVERY_FREQUENCY_COL, 2000);
            worksheet.setColumnWidth(NOMINAL_INTEREST_RATE_COL, 2000);
            worksheet.setColumnWidth(NOMINAL_INTEREST_RATE_FREQUENCY_COL, 3000);
            worksheet.setColumnWidth(AMORTIZATION_COL, 6000);
            worksheet.setColumnWidth(INTEREST_METHOD_COL, 4000);
            worksheet.setColumnWidth(INTEREST_CALCULATION_PERIOD_COL, 4000);
            worksheet.setColumnWidth(ARREARS_TOLERANCE_COL, 4000);
            worksheet.setColumnWidth(REPAYMENT_STRATEGY_COL, 4700);
            worksheet.setColumnWidth(GRACE_ON_PRINCIPAL_PAYMENT_COL, 3500);
            worksheet.setColumnWidth(GRACE_ON_INTEREST_PAYMENT_COL, 3500);
            worksheet.setColumnWidth(GRACE_ON_INTEREST_CHARGED_COL, 3500);
            worksheet.setColumnWidth(INTEREST_CHARGED_FROM_COL, 4000);
            worksheet.setColumnWidth(FIRST_REPAYMENT_COL, 4700);
            worksheet.setColumnWidth(TOTAL_AMOUNT_REPAID_COL, 3500);
            worksheet.setColumnWidth(LAST_REPAYMENT_DATE_COL, 3000);
            worksheet.setColumnWidth(REPAYMENT_TYPE_COL, 4300);
            worksheet.setColumnWidth(LOOKUP_CLIENT_NAME_COL, 6000);
            worksheet.setColumnWidth(LOOKUP_ACTIVATION_DATE_COL, 6000);
            writeString(OFFICE_NAME_COL, rowHeader, "Office Name*");
            writeString(CLIENT_NAME_COL, rowHeader, "Client Name*");
            writeString(PRODUCT_COL, rowHeader, "Product*");
            writeString(LOAN_OFFICER_NAME_COL, rowHeader, "Loan Officer*");
            writeString(SUBMITTED_ON_DATE_COL, rowHeader, "Submitted On*");
            writeString(APPROVED_DATE_COL, rowHeader, "Approved On*");
            writeString(DISBURSED_DATE_COL, rowHeader, "Disbursed Date*");
            writeString(DISBURSED_PAYMENT_TYPE_COL, rowHeader, "Payment Type*");
            writeString(FUND_NAME_COL, rowHeader, "Fund Name");
            writeString(PRINCIPAL_COL, rowHeader, "Principal*");
            writeString(LOAN_TERM_COL, rowHeader, "Loan Term*");
            writeString(NO_OF_REPAYMENTS_COL, rowHeader, "# of Repayments*");
            writeString(REPAID_EVERY_COL, rowHeader, "Repaid Every*");
            writeString(NOMINAL_INTEREST_RATE_COL, rowHeader, "Nominal Interest %*");
            writeString(AMORTIZATION_COL, rowHeader, "Amortization*");
            writeString(INTEREST_METHOD_COL, rowHeader, "Interest Method*");
            writeString(INTEREST_CALCULATION_PERIOD_COL, rowHeader, "Interest Calculation Period*");
            writeString(ARREARS_TOLERANCE_COL, rowHeader, "Arrears Tolerance");
            writeString(REPAYMENT_STRATEGY_COL, rowHeader, "Repayment Strategy*");
            writeString(GRACE_ON_PRINCIPAL_PAYMENT_COL, rowHeader, "Grace-Principal Payment");
            writeString(GRACE_ON_INTEREST_PAYMENT_COL, rowHeader, "Grace-Interest Payment");
            writeString(GRACE_ON_INTEREST_CHARGED_COL, rowHeader, "Interest-Free Period(s)");
            writeString(INTEREST_CHARGED_FROM_COL, rowHeader, "Interest Charged From");
            writeString(FIRST_REPAYMENT_COL, rowHeader, "First Repayment On");
            writeString(TOTAL_AMOUNT_REPAID_COL, rowHeader, "Amount Repaid");
            writeString(LAST_REPAYMENT_DATE_COL, rowHeader, "Date-Last Repayment");
            writeString(REPAYMENT_TYPE_COL, rowHeader, "Repayment Type");
            writeString(LOOKUP_CLIENT_NAME_COL, rowHeader, "Client Name");
            writeString(LOOKUP_ACTIVATION_DATE_COL, rowHeader, "Client Activation Date");
            CellStyle borderStyle = worksheet.getWorkbook().createCellStyle();
            CellStyle doubleBorderStyle = worksheet.getWorkbook().createCellStyle();
            borderStyle.setBorderBottom(CellStyle.BORDER_THIN);
            doubleBorderStyle.setBorderBottom(CellStyle.BORDER_THIN);
            doubleBorderStyle.setBorderRight(CellStyle.BORDER_THICK);
            for(int colNo = 0; colNo < 30; colNo++) {
            	Cell cell = rowHeader.getCell(colNo);
            	if(cell == null)
            		rowHeader.createCell(colNo);
            	rowHeader.getCell(colNo).setCellStyle(borderStyle);
            }
            rowHeader.getCell(FIRST_REPAYMENT_COL).setCellStyle(doubleBorderStyle);
            rowHeader.getCell(REPAYMENT_TYPE_COL).setCellStyle(doubleBorderStyle);
	    }
	    
	    private Result setRules(Sheet worksheet) {
	    	Result result = new Result();
	    	try {
	    		//TODO: Clean this.
	    		CellRangeAddressList officeNameRange = new  CellRangeAddressList(1, SpreadsheetVersion.EXCEL97.getLastRowIndex(), OFFICE_NAME_COL, OFFICE_NAME_COL);
	        	CellRangeAddressList clientNameRange = new  CellRangeAddressList(1, SpreadsheetVersion.EXCEL97.getLastRowIndex(), CLIENT_NAME_COL, CLIENT_NAME_COL);
	        	CellRangeAddressList productNameRange = new  CellRangeAddressList(1, SpreadsheetVersion.EXCEL97.getLastRowIndex(), PRODUCT_COL, PRODUCT_COL);
	        	CellRangeAddressList loanOfficerRange = new CellRangeAddressList(1, SpreadsheetVersion.EXCEL97.getLastRowIndex(), LOAN_OFFICER_NAME_COL, LOAN_OFFICER_NAME_COL);
	        	CellRangeAddressList submittedDateRange = new CellRangeAddressList(1, SpreadsheetVersion.EXCEL97.getLastRowIndex(), SUBMITTED_ON_DATE_COL, SUBMITTED_ON_DATE_COL);
	        	CellRangeAddressList fundNameRange = new CellRangeAddressList(1, SpreadsheetVersion.EXCEL97.getLastRowIndex(), FUND_NAME_COL, FUND_NAME_COL);
	        	CellRangeAddressList principalRange = new CellRangeAddressList(1, SpreadsheetVersion.EXCEL97.getLastRowIndex(), PRINCIPAL_COL, PRINCIPAL_COL);
	        	CellRangeAddressList noOfRepaymentsRange = new CellRangeAddressList(1, SpreadsheetVersion.EXCEL97.getLastRowIndex(), NO_OF_REPAYMENTS_COL, NO_OF_REPAYMENTS_COL);
	        	CellRangeAddressList repaidFrequencyRange = new CellRangeAddressList(1, SpreadsheetVersion.EXCEL97.getLastRowIndex(), REPAID_EVERY_FREQUENCY_COL, REPAID_EVERY_FREQUENCY_COL);
	        	CellRangeAddressList loanTermRange = new CellRangeAddressList(1, SpreadsheetVersion.EXCEL97.getLastRowIndex(), LOAN_TERM_COL, LOAN_TERM_COL);
	        	CellRangeAddressList loanTermFrequencyRange = new CellRangeAddressList(1, SpreadsheetVersion.EXCEL97.getLastRowIndex(), LOAN_TERM_FREQUENCY_COL, LOAN_TERM_FREQUENCY_COL);
	        	CellRangeAddressList interestFrequencyRange = new CellRangeAddressList(1, SpreadsheetVersion.EXCEL97.getLastRowIndex(), NOMINAL_INTEREST_RATE_FREQUENCY_COL, NOMINAL_INTEREST_RATE_FREQUENCY_COL);
	        	CellRangeAddressList interestRange = new CellRangeAddressList(1, SpreadsheetVersion.EXCEL97.getLastRowIndex(), NOMINAL_INTEREST_RATE_COL, NOMINAL_INTEREST_RATE_COL);
	        	CellRangeAddressList amortizationRange = new CellRangeAddressList(1, SpreadsheetVersion.EXCEL97.getLastRowIndex(), AMORTIZATION_COL, AMORTIZATION_COL);
	        	CellRangeAddressList interestMethodRange = new CellRangeAddressList(1, SpreadsheetVersion.EXCEL97.getLastRowIndex(), INTEREST_METHOD_COL, INTEREST_METHOD_COL);
	        	CellRangeAddressList intrestCalculationPeriodRange = new CellRangeAddressList(1, SpreadsheetVersion.EXCEL97.getLastRowIndex(), INTEREST_CALCULATION_PERIOD_COL, INTEREST_CALCULATION_PERIOD_COL);
	        	CellRangeAddressList repaymentStrategyRange = new CellRangeAddressList(1, SpreadsheetVersion.EXCEL97.getLastRowIndex(), REPAYMENT_STRATEGY_COL, REPAYMENT_STRATEGY_COL);
	        	CellRangeAddressList arrearsToleranceRange = new CellRangeAddressList(1, SpreadsheetVersion.EXCEL97.getLastRowIndex(), ARREARS_TOLERANCE_COL, ARREARS_TOLERANCE_COL);
	        	CellRangeAddressList graceOnPrincipalPaymentRange = new CellRangeAddressList(1, SpreadsheetVersion.EXCEL97.getLastRowIndex(), GRACE_ON_PRINCIPAL_PAYMENT_COL, GRACE_ON_PRINCIPAL_PAYMENT_COL);
	        	CellRangeAddressList graceOnInterestPaymentRange = new CellRangeAddressList(1, SpreadsheetVersion.EXCEL97.getLastRowIndex(), GRACE_ON_INTEREST_PAYMENT_COL, GRACE_ON_INTEREST_PAYMENT_COL);
	        	CellRangeAddressList graceOnInterestChargedRange = new CellRangeAddressList(1, SpreadsheetVersion.EXCEL97.getLastRowIndex(), GRACE_ON_INTEREST_CHARGED_COL, GRACE_ON_INTEREST_CHARGED_COL);
	        	CellRangeAddressList approvedDateRange = new CellRangeAddressList(1, SpreadsheetVersion.EXCEL97.getLastRowIndex(), APPROVED_DATE_COL, APPROVED_DATE_COL);
	        	CellRangeAddressList disbursedDateRange = new CellRangeAddressList(1, SpreadsheetVersion.EXCEL97.getLastRowIndex(), DISBURSED_DATE_COL, DISBURSED_DATE_COL);
	        	CellRangeAddressList paymentTypeRange = new CellRangeAddressList(1, SpreadsheetVersion.EXCEL97.getLastRowIndex(), DISBURSED_PAYMENT_TYPE_COL, DISBURSED_PAYMENT_TYPE_COL);
	        	CellRangeAddressList repaymentTypeRange = new CellRangeAddressList(1, SpreadsheetVersion.EXCEL97.getLastRowIndex(), REPAYMENT_TYPE_COL, REPAYMENT_TYPE_COL);
	        	CellRangeAddressList lastrepaymentDateRange = new CellRangeAddressList(1, SpreadsheetVersion.EXCEL97.getLastRowIndex(), LAST_REPAYMENT_DATE_COL, LAST_REPAYMENT_DATE_COL);
	        	
	        	DataValidationHelper validationHelper = new HSSFDataValidationHelper((HSSFSheet)worksheet);
	        	Workbook loanWorkbook = worksheet.getWorkbook();
	        	
	        	ArrayList<String> officeNames = new ArrayList<String>(Arrays.asList(clientSheetPopulator.getOfficeNames()));
	        	List<LoanProduct> products = productSheetPopulator.getProducts();
	        	
	        	//Client and Loan Officer Names for each office
	        	for(Integer i = 0, j = 2; i < officeNames.size(); i++, j = j + 2) {
	        		String lastColumnLettersOfClients = CellReference.convertNumToColString(clientSheetPopulator.getLastColumnLetters().get(i));
	        		String lastColumnLettersOfStaff = CellReference.convertNumToColString(personnelSheetPopulator.getLastColumnLetters().get(i));
	        		Name clientName = loanWorkbook.createName();
	        		Name loanOfficerName = loanWorkbook.createName();
	        	    clientName.setNameName(officeNames.get(i));
	        	    loanOfficerName.setNameName(officeNames.get(i)+"X");
	        	    clientName.setRefersToFormula("Clients!$B$" + j + ":$" + lastColumnLettersOfClients + "$" + j);
	        	    loanOfficerName.setRefersToFormula("Staff!$B$" + j + ":$" + lastColumnLettersOfStaff + "$" + j);
	        	}
	        	
	        	//Product Name
	        	Name productGroup = loanWorkbook.createName();
	        	productGroup.setNameName("Products");
	        	productGroup.setRefersToFormula("Products!$B$2:$B$" + (productSheetPopulator.getProductsSize() + 1));
	        	
	        	//Fund Name
	        	Name fundGroup = loanWorkbook.createName();
	        	fundGroup.setNameName("Funds");
	        	fundGroup.setRefersToFormula("Extras!$B$2:$B$" + (extrasSheetPopulator.getFundsSize() + 1));
	        	
	        	//Payment Type Name
	        	Name paymentTypeGroup = loanWorkbook.createName();
	        	paymentTypeGroup.setNameName("PaymentTypes");
	        	paymentTypeGroup.setRefersToFormula("Extras!$D$2:$D$" + (extrasSheetPopulator.getPaymentTypesSize() + 1));
	        	
	        	//Default Fund, Default Principal, Min Principal, Max Principal, Default No. of Repayments, Min Repayments, Max Repayments, Repayment Every,
	        	//Repayment Every Frequency, Interest Rate, Min Interest Rate, Max Interest Rate, Interest Frequency, Amortization, Interest Type,
	        	//Interest Calculation Period, Transaction Processing Strategy, Arrears Tolerance, GraceOnPrincipalPayment, GraceOnInterestPayment, 
	        	//GraceOnInterestCharged, StartDate Names for each loan product
	        	for(Integer i = 0; i < products.size(); i++) {
	        		Name fundName = loanWorkbook.createName();
	        		Name principalName = loanWorkbook.createName();
	        		Name minPrincipalName = loanWorkbook.createName();
	        		Name maxPrincipalName = loanWorkbook.createName();
	        		Name noOfRepaymentName = loanWorkbook.createName();
	        		Name minNoOfRepayment = loanWorkbook.createName();
	        		Name maxNoOfRepaymentName = loanWorkbook.createName();
	        		Name repaymentEveryName = loanWorkbook.createName();
	        		Name repaymentFrequencyName = loanWorkbook.createName();
	        		Name interestName = loanWorkbook.createName();
	        		Name minInterestName = loanWorkbook.createName();
	        		Name maxInterestName = loanWorkbook.createName();
	        		Name interestFrequencyName = loanWorkbook.createName();
	        		Name amortizationName = loanWorkbook.createName();
	        		Name interestTypeName = loanWorkbook.createName();
	        		Name interestCalculationPeriodName = loanWorkbook.createName();
	        		Name transactionProcessingStrategyName = loanWorkbook.createName();
	        		Name arrearsToleranceName = loanWorkbook.createName();
	        		Name graceOnPrincipalPaymentName = loanWorkbook.createName();
	        		Name graceOnInterestPaymentName = loanWorkbook.createName();
	        		Name graceOnInterestChargedName = loanWorkbook.createName();
	        		Name startDateName = loanWorkbook.createName();
	        	    fundName.setNameName(products.get(i).getName() + "_FUND");
	        	    principalName.setNameName(products.get(i).getName() + "_PRINCIPAL");
	        	    minPrincipalName.setNameName(products.get(i).getName() + "_MIN_PRINCIPAL");
	        	    maxPrincipalName.setNameName(products.get(i).getName() + "_MAX_PRINCIPAL");
	        	    noOfRepaymentName.setNameName(products.get(i).getName() + "_NO_REPAYMENT");
	        	    minNoOfRepayment.setNameName(products.get(i).getName() + "_MIN_REPAYMENT");
	        	    maxNoOfRepaymentName.setNameName(products.get(i).getName() + "_MAX_REPAYMENT");
	        	    repaymentEveryName.setNameName(products.get(i).getName() + "_REPAYMENT_EVERY");
	        	    repaymentFrequencyName.setNameName(products.get(i).getName() + "_REPAYMENT_FREQUENCY");
	        	    interestName.setNameName(products.get(i).getName() + "_INTEREST");
	        	    minInterestName.setNameName(products.get(i).getName() + "_MIN_INTEREST");
	        	    maxInterestName.setNameName(products.get(i).getName() + "_MAX_INTEREST");
	        	    interestFrequencyName .setNameName(products.get(i).getName() + "_INTEREST_FREQUENCY");
	        	    amortizationName.setNameName(products.get(i).getName() + "_AMORTIZATION");
	        	    interestTypeName.setNameName(products.get(i).getName() + "_INTEREST_TYPE");
	        	    interestCalculationPeriodName.setNameName(products.get(i).getName() + "_INTEREST_CALCULATION");
	        	    transactionProcessingStrategyName.setNameName(products.get(i).getName() + "_STRATEGY");
	        	    arrearsToleranceName.setNameName(products.get(i).getName() + "_ARREARS_TOLERANCE");
	        	    graceOnPrincipalPaymentName.setNameName(products.get(i).getName() + "_GRACE_PRINCIPAL");
	        	    graceOnInterestPaymentName.setNameName(products.get(i).getName() + "_GRACE_INTEREST_PAYMENT");
	        	    graceOnInterestChargedName.setNameName(products.get(i).getName() + "_GRACE_INTEREST_CHARGED");
	        	    startDateName.setNameName(products.get(i).getName() + "_START_DATE");
	        	    if(products.get(i).getFundName() != null)
	        	        fundName.setRefersToFormula("Products!$C$" + (i + 2));
	        	    principalName.setRefersToFormula("Products!$D$" + (i + 2));
	        	    minPrincipalName.setRefersToFormula("Products!$E$" + (i + 2));
	        	    maxPrincipalName.setRefersToFormula("Products!$F$" + (i + 2));
	        	    noOfRepaymentName.setRefersToFormula("Products!$G$" + (i + 2));
	        	    minNoOfRepayment.setRefersToFormula("Products!$H$" + (i + 2));
	        	    maxNoOfRepaymentName.setRefersToFormula("Products!$I$" + (i + 2));
	        	    repaymentEveryName.setRefersToFormula("Products!$J$" + (i + 2));
	        	    repaymentFrequencyName.setRefersToFormula("Products!$K$" + (i + 2));
	        	    interestName.setRefersToFormula("Products!$L$" + (i + 2));
	        	    minInterestName.setRefersToFormula("Products!$M$" + (i + 2));
	        	    maxInterestName.setRefersToFormula("Products!$N$" + (i + 2));
	        	    interestFrequencyName .setRefersToFormula("Products!$O$" + (i + 2));
	        	    amortizationName.setRefersToFormula("Products!$P$" + (i + 2));
	        	    interestTypeName.setRefersToFormula("Products!$Q$" + (i + 2));
	        	    interestCalculationPeriodName.setRefersToFormula("Products!$R$" + (i + 2));
	        	    transactionProcessingStrategyName.setRefersToFormula("Products!$T$" + (i + 2));
	        	    arrearsToleranceName.setRefersToFormula("Products!$S$" + (i + 2));
	        	    graceOnPrincipalPaymentName.setRefersToFormula("Products!$U$" + (i + 2));
	        	    graceOnInterestPaymentName.setRefersToFormula("Products!$V$" + (i + 2));
	        	    graceOnInterestChargedName.setRefersToFormula("Products!$W$" + (i + 2));
	        	    startDateName.setRefersToFormula("Products!$X$" + (i + 2));
	        	}
	        	
	        	DataValidationConstraint officeNameConstraint = validationHelper.createExplicitListConstraint(clientSheetPopulator.getOfficeNames());
	        	DataValidationConstraint clientNameConstraint = validationHelper.createFormulaListConstraint("INDIRECT($A1)");
	        	DataValidationConstraint productNameConstraint = validationHelper.createFormulaListConstraint("Products");
	        	DataValidationConstraint loanOfficerNameConstraint = validationHelper.createFormulaListConstraint("INDIRECT(CONCATENATE($A1,\"X\"))");
	        	DataValidationConstraint submittedDateConstraint = validationHelper.createDateConstraint(DataValidationConstraint.OperatorType.BETWEEN, "=IF(INDIRECT(CONCATENATE($C1,\"_START_DATE\"))>VLOOKUP($B1,$AQ$2:$AR$" + (clientSheetPopulator.getClientsSize() + 1) + ",2,FALSE),INDIRECT(CONCATENATE($C1,\"_START_DATE\")),VLOOKUP($B1,$AQ$2:$AR$" + (clientSheetPopulator.getClientsSize() + 1) + ",2,FALSE))", "=TODAY()", "dd/mm/yy");
	        	DataValidationConstraint approvalDateConstraint = validationHelper.createDateConstraint(DataValidationConstraint.OperatorType.BETWEEN, "=$E1", "=TODAY()", "dd/mm/yy");
	        	DataValidationConstraint disbursedDateConstraint = validationHelper.createDateConstraint(DataValidationConstraint.OperatorType.BETWEEN, "=$F1", "=TODAY()", "dd/mm/yy");
	        	DataValidationConstraint paymentTypeConstraint = validationHelper.createFormulaListConstraint("PaymentTypes");
	        	DataValidationConstraint fundNameConstraint = validationHelper.createFormulaListConstraint("Funds");
	        	DataValidationConstraint principalConstraint = validationHelper.createDecimalConstraint(DataValidationConstraint.OperatorType.BETWEEN, "=INDIRECT(CONCATENATE($C1,\"_MIN_PRINCIPAL\"))", "=INDIRECT(CONCATENATE($C1,\"_MAX_PRINCIPAL\"))");
	        	DataValidationConstraint noOfRepaymentsConstraint = validationHelper.createIntegerConstraint(DataValidationConstraint.OperatorType.BETWEEN, "=INDIRECT(CONCATENATE($C1,\"_MIN_REPAYMENT\"))", "=INDIRECT(CONCATENATE($C1,\"_MAX_REPAYMENT\"))");
	        	DataValidationConstraint frequencyConstraint = validationHelper.createExplicitListConstraint(new String[] {"Days","Weeks","Months"});
	        	DataValidationConstraint loanTermConstraint = validationHelper.createIntegerConstraint(DataValidationConstraint.OperatorType.GREATER_OR_EQUAL, "=$K1*$L1", null);
	        	DataValidationConstraint interestFrequencyConstraint = validationHelper.createFormulaListConstraint("INDIRECT(CONCATENATE($C1,\"_INTEREST_FREQUENCY\"))");
	        	DataValidationConstraint interestConstraint = validationHelper.createIntegerConstraint(DataValidationConstraint.OperatorType.BETWEEN, "=INDIRECT(CONCATENATE($C1,\"_MIN_INTEREST\"))", "=INDIRECT(CONCATENATE($C1,\"_MAX_INTEREST\"))");
	        	DataValidationConstraint amortizationConstraint = validationHelper.createExplicitListConstraint(new String[] {"Equal principal payments","Equal installments"});
	        	DataValidationConstraint interestMethodConstraint = validationHelper.createExplicitListConstraint(new String[] {"Flat","Declining Balance"});
	        	DataValidationConstraint interestCalculationPeriodConstraint = validationHelper.createExplicitListConstraint(new String[] {"Daily","Same as repayment period"});
	        	DataValidationConstraint repaymentStrategyConstraint = validationHelper.createExplicitListConstraint(new String[] {"Mifos style","Heavensfamily","Creocore","RBI (India)","Principal Interest Penalties Fees Order","Interest Principal Penalties Fees Order"});
	        	DataValidationConstraint arrearsToleranceConstraint = validationHelper.createIntegerConstraint(DataValidationConstraint.OperatorType.GREATER_OR_EQUAL, "0", null);
	        	DataValidationConstraint graceOnPrincipalPaymentConstraint = validationHelper.createIntegerConstraint(DataValidationConstraint.OperatorType.GREATER_OR_EQUAL, "0", null);
	        	DataValidationConstraint graceOnInterestPaymentConstraint = validationHelper.createIntegerConstraint(DataValidationConstraint.OperatorType.GREATER_OR_EQUAL, "0", null);
	        	DataValidationConstraint graceOnInterestChargedConstraint = validationHelper.createIntegerConstraint(DataValidationConstraint.OperatorType.GREATER_OR_EQUAL, "0", null);
	        	DataValidationConstraint lastRepaymentDateConstraint = validationHelper.createDateConstraint(DataValidationConstraint.OperatorType.BETWEEN, "=$G1", "=TODAY()", "dd/mm/yy");
	        	
	        	DataValidation officeValidation = validationHelper.createValidation(officeNameConstraint, officeNameRange);
	        	officeValidation.setSuppressDropDownArrow(false);
	        	DataValidation clientValidation = validationHelper.createValidation(clientNameConstraint, clientNameRange);
	        	clientValidation.setSuppressDropDownArrow(false);
	        	DataValidation productNameValidation = validationHelper.createValidation(productNameConstraint, productNameRange);
	        	productNameValidation.setSuppressDropDownArrow(false);
	        	DataValidation loanOfficerValidation = validationHelper.createValidation(loanOfficerNameConstraint, loanOfficerRange);
	        	loanOfficerValidation.setSuppressDropDownArrow(false);
	        	DataValidation fundNameValidation = validationHelper.createValidation(fundNameConstraint, fundNameRange);
	        	fundNameValidation.setSuppressDropDownArrow(false);
	        	DataValidation repaidFrequencyValidation = validationHelper.createValidation(frequencyConstraint, repaidFrequencyRange);
	        	repaidFrequencyValidation.setSuppressDropDownArrow(false);
	        	DataValidation loanTermFrequencyValidation = validationHelper.createValidation(frequencyConstraint, loanTermFrequencyRange);
	        	loanTermFrequencyValidation.setSuppressDropDownArrow(false);
	        	DataValidation amortizationValidation = validationHelper.createValidation(amortizationConstraint, amortizationRange);
	        	amortizationValidation.setSuppressDropDownArrow(false);
	        	DataValidation interestMethodValidation = validationHelper.createValidation(interestMethodConstraint, interestMethodRange);
	        	interestMethodValidation.setSuppressDropDownArrow(false);
	        	DataValidation interestCalculationPeriodValidation = validationHelper.createValidation(interestCalculationPeriodConstraint, intrestCalculationPeriodRange);
	        	interestCalculationPeriodValidation.setSuppressDropDownArrow(false);
	        	DataValidation repaymentStrategyValidation = validationHelper.createValidation(repaymentStrategyConstraint, repaymentStrategyRange);
	        	repaymentStrategyValidation.setSuppressDropDownArrow(false);
	        	DataValidation paymentTypeValidation = validationHelper.createValidation(paymentTypeConstraint, paymentTypeRange);
	        	paymentTypeValidation.setSuppressDropDownArrow(false);
	        	DataValidation repaymentTypeValidation = validationHelper.createValidation(paymentTypeConstraint, repaymentTypeRange);
	        	repaymentTypeValidation.setSuppressDropDownArrow(false);
	        	DataValidation submittedDateValidation = validationHelper.createValidation(submittedDateConstraint, submittedDateRange);
	        	DataValidation approvalDateValidation = validationHelper.createValidation(approvalDateConstraint, approvedDateRange);
	        	DataValidation disbursedDateValidation = validationHelper.createValidation(disbursedDateConstraint, disbursedDateRange);
	        	DataValidation lastRepaymentDateValidation = validationHelper.createValidation(lastRepaymentDateConstraint, lastrepaymentDateRange);
	        	DataValidation principalValidation = validationHelper.createValidation(principalConstraint, principalRange);
	        	DataValidation loanTermValidation = validationHelper.createValidation(loanTermConstraint, loanTermRange);
	        	DataValidation noOfRepaymentsValidation = validationHelper.createValidation(noOfRepaymentsConstraint, noOfRepaymentsRange);
	        	DataValidation interestValidation = validationHelper.createValidation(interestConstraint, interestRange);
	        	DataValidation arrearsToleranceValidation = validationHelper.createValidation(arrearsToleranceConstraint, arrearsToleranceRange);
	        	DataValidation graceOnPrincipalPaymentValidation = validationHelper.createValidation(graceOnPrincipalPaymentConstraint, graceOnPrincipalPaymentRange);
	        	DataValidation graceOnInterestPaymentValidation = validationHelper.createValidation(graceOnInterestPaymentConstraint, graceOnInterestPaymentRange);
	        	DataValidation graceOnInterestChargedValidation = validationHelper.createValidation(graceOnInterestChargedConstraint, graceOnInterestChargedRange);
	        	DataValidation interestFrequencyValidation = validationHelper.createValidation(interestFrequencyConstraint, interestFrequencyRange);
	        	interestFrequencyValidation.setSuppressDropDownArrow(true);
	        	
	        	
	        	
	        	worksheet.addValidationData(officeValidation);
	            worksheet.addValidationData(clientValidation);
	            worksheet.addValidationData(productNameValidation);
	            worksheet.addValidationData(loanOfficerValidation);
	            worksheet.addValidationData(submittedDateValidation);
	            worksheet.addValidationData(approvalDateValidation);
	            worksheet.addValidationData(disbursedDateValidation);
	            worksheet.addValidationData(paymentTypeValidation);
	            worksheet.addValidationData(fundNameValidation);
	            worksheet.addValidationData(principalValidation);
	            worksheet.addValidationData(repaidFrequencyValidation);
	            worksheet.addValidationData(loanTermFrequencyValidation);
	            worksheet.addValidationData(noOfRepaymentsValidation);
	            worksheet.addValidationData(loanTermValidation);
	            worksheet.addValidationData(interestValidation);
	            worksheet.addValidationData(interestFrequencyValidation);
	            worksheet.addValidationData(amortizationValidation);
	            worksheet.addValidationData(interestMethodValidation);
	            worksheet.addValidationData(interestCalculationPeriodValidation);
	            worksheet.addValidationData(repaymentStrategyValidation);
	            worksheet.addValidationData(arrearsToleranceValidation);
	            worksheet.addValidationData(graceOnPrincipalPaymentValidation);
	            worksheet.addValidationData(graceOnInterestPaymentValidation);
	            worksheet.addValidationData(graceOnInterestChargedValidation);
	            worksheet.addValidationData(lastRepaymentDateValidation);
	            worksheet.addValidationData(repaymentTypeValidation);
	    	} catch (RuntimeException re) {
	    		result.addError(re.getMessage());
	    	}
	       return result;
	    }
	    
	    private Result setDefaults(Sheet worksheet) {
	    	Result result = new Result();
	    	try {
	    		for(Integer rowNo = 1; rowNo < 1000; rowNo++)
	    		{
	    			Row row = worksheet.createRow(rowNo);
	    			writeFormula(FUND_NAME_COL, row, "IF(ISERROR(INDIRECT(CONCATENATE($C" + (rowNo + 1) + ",\"_FUND\"))),\"\",INDIRECT(CONCATENATE($C"+ (rowNo + 1) + ",\"_FUND\")))");
	    			writeFormula(PRINCIPAL_COL, row, "IF(ISERROR(INDIRECT(CONCATENATE($C" + (rowNo + 1) + ",\"_PRINCIPAL\"))),\"\",INDIRECT(CONCATENATE($C"+ (rowNo + 1) + ",\"_PRINCIPAL\")))");
	    			writeFormula(REPAID_EVERY_COL, row, "IF(ISERROR(INDIRECT(CONCATENATE($C" + (rowNo + 1) + ",\"_REPAYMENT_EVERY\"))),\"\",INDIRECT(CONCATENATE($C"+ (rowNo + 1) + ",\"_REPAYMENT_EVERY\")))");
	    			writeFormula(REPAID_EVERY_FREQUENCY_COL, row, "IF(ISERROR(INDIRECT(CONCATENATE($C" + (rowNo + 1) + ",\"_REPAYMENT_FREQUENCY\"))),\"\",INDIRECT(CONCATENATE($C"+ (rowNo + 1) + ",\"_REPAYMENT_FREQUENCY\")))");
	    			writeFormula(NO_OF_REPAYMENTS_COL, row, "IF(ISERROR(INDIRECT(CONCATENATE($C" + (rowNo + 1) + ",\"_NO_REPAYMENT\"))),\"\",INDIRECT(CONCATENATE($C"+ (rowNo + 1) + ",\"_NO_REPAYMENT\")))");
	    			writeFormula(LOAN_TERM_COL, row, "IF(ISERROR($K" + (rowNo + 1) + "*$L" + (rowNo + 1) + "),\"\",$K" + (rowNo + 1) + "*$L" + (rowNo + 1) + ")");
	    			writeFormula(LOAN_TERM_FREQUENCY_COL, row, "$M" + (rowNo + 1));
	    			writeFormula(NOMINAL_INTEREST_RATE_FREQUENCY_COL, row, "IF(ISERROR(INDIRECT(CONCATENATE($C" + (rowNo + 1) + ",\"_INTEREST_FREQUENCY\"))),\"\",INDIRECT(CONCATENATE($C" + (rowNo + 1) + ",\"_INTEREST_FREQUENCY\")))");
	    			writeFormula(NOMINAL_INTEREST_RATE_COL, row, "IF(ISERROR(INDIRECT(CONCATENATE($C" + (rowNo + 1) + ",\"_INTEREST\"))),\"\",INDIRECT(CONCATENATE($C" + (rowNo + 1) + ",\"_INTEREST\")))");
	    			writeFormula(AMORTIZATION_COL, row, "IF(ISERROR(INDIRECT(CONCATENATE($C" + (rowNo + 1) + ",\"_AMORTIZATION\"))),\"\",INDIRECT(CONCATENATE($C" + (rowNo + 1) + ",\"_AMORTIZATION\")))");
	    			writeFormula(INTEREST_METHOD_COL, row, "IF(ISERROR(INDIRECT(CONCATENATE($C" + (rowNo + 1) + ",\"_INTEREST_TYPE\"))),\"\",INDIRECT(CONCATENATE($C" + (rowNo + 1) + ",\"_INTEREST_TYPE\")))");
	    			writeFormula(INTEREST_CALCULATION_PERIOD_COL, row, "IF(ISERROR(INDIRECT(CONCATENATE($C" + (rowNo + 1) + ",\"_INTEREST_CALCULATION\"))),\"\",INDIRECT(CONCATENATE($C" + (rowNo + 1) + ",\"_INTEREST_CALCULATION\")))");
	    			writeFormula(ARREARS_TOLERANCE_COL, row, "IF(ISERROR(INDIRECT(CONCATENATE($C" + (rowNo + 1) + ",\"_ARREARS_TOLERANCE\"))),\"\",INDIRECT(CONCATENATE($C" + (rowNo + 1) + ",\"_ARREARS_TOLERANCE\")))");
	    			writeFormula(REPAYMENT_STRATEGY_COL, row, "IF(ISERROR(INDIRECT(CONCATENATE($C" + (rowNo + 1) + ",\"_STRATEGY\"))),\"\",INDIRECT(CONCATENATE($C" + (rowNo + 1) + ",\"_STRATEGY\")))");
	    			writeFormula(GRACE_ON_PRINCIPAL_PAYMENT_COL, row, "IF(ISERROR(INDIRECT(CONCATENATE($C" + (rowNo + 1) + ",\"_GRACE_PRINCIPAL\"))),\"\",INDIRECT(CONCATENATE($C" + (rowNo + 1) + ",\"_GRACE_PRINCIPAL\")))");
	    			writeFormula(GRACE_ON_INTEREST_PAYMENT_COL, row, "IF(ISERROR(INDIRECT(CONCATENATE($C" + (rowNo + 1) + ",\"_GRACE_INTEREST_PAYMENT\"))),\"\",INDIRECT(CONCATENATE($C" + (rowNo + 1) + ",\"_GRACE_INTEREST_PAYMENT\")))");
	    			writeFormula(GRACE_ON_INTEREST_CHARGED_COL, row, "IF(ISERROR(INDIRECT(CONCATENATE($C" + (rowNo + 1) + ",\"_GRACE_INTEREST_CHARGED\"))),\"\",INDIRECT(CONCATENATE($C" + (rowNo + 1) + ",\"_GRACE_INTEREST_CHARGED\")))");
	    		}
	    	} catch (RuntimeException re) {
	    		result.addError(re.getMessage());
	    	}
	       return result;
	    	}
}
