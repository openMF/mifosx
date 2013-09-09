package org.openmf.mifos.dataimport.populator.loan;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.openmf.mifos.dataimport.dto.LoanProduct;
import org.openmf.mifos.dataimport.handler.Result;
import org.openmf.mifos.dataimport.http.RestClient;
import org.openmf.mifos.dataimport.populator.AbstractWorkbookPopulator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class LoanProductSheetPopulator extends AbstractWorkbookPopulator {
	
	private static final Logger logger = LoggerFactory.getLogger(LoanProductSheetPopulator.class);
	
    private final RestClient client;
	
	private String content;
	
	private List<LoanProduct> products;
	
	private static final int ID_COL = 0;
	private static final int NAME_COL = 1;
	private static final int FUND_NAME_COL = 2;
	private static final int PRINCIPAL_COL = 3;
	private static final int MIN_PRINCIPAL_COL = 4;
	private static final int MAX_PRINCIPAL_COL = 5;
	private static final int NO_OF_REPAYMENTS_COL = 6;
	private static final int MIN_REPAYMENTS_COL = 7;
	private static final int MAX_REPAYMENTS_COL = 8;
	private static final int REPAYMENT_EVERY_COL = 9;
	private static final int REPAYMENT_FREQUENCY_COL = 10;
	private static final int INTEREST_RATE_COL = 11;
	private static final int MIN_INTEREST_RATE_COL = 12;
	private static final int MAX_INTEREST_RATE_COL = 13;
	private static final int INTEREST_RATE_FREQUENCY_COL = 14;
	private static final int AMORTIZATION_TYPE_COL = 15;
	private static final int INTEREST_TYPE_COL = 16;
	private static final int INTEREST_CALCULATION_PERIOD_TYPE_COL = 17;
	private static final int IN_ARREARS_TOLERANCE_COL = 18;
	private static final int TRANSACTION_PROCESSING_STRATEGY_NAME_COL = 19;
	private static final int GRACE_ON_PRINCIPAL_PAYMENT_COL = 20;
	private static final int GRACE_ON_INTEREST_PAYMENT_COL = 21;
	private static final int GRACE_ON_INTEREST_CHARGED_COL = 22;
	private static final int START_DATE_COL = 23;
	private static final int CLOSE_DATE_COL = 24;
	
	public LoanProductSheetPopulator(RestClient client) {
        this.client = client;
    }
	
	@Override
    public Result downloadAndParse() {
    	Result result = new Result();
        try {
        	client.createAuthToken();
        	products = new ArrayList<LoanProduct>();
            content = client.get("loanproducts");
            Gson gson = new Gson();
            JsonElement json = new JsonParser().parse(content);
            JsonArray array = json.getAsJsonArray();
            Iterator<JsonElement> iterator = array.iterator();
            while(iterator.hasNext()) {
            	json = iterator.next();
            	LoanProduct product = gson.fromJson(json, LoanProduct.class);
            	if(product.getStatus().equals("loanProduct.active"))
            	    products.add(product);
            }
        	
        } catch (Exception e) {
            result.addError(e.getMessage());
            logger.error(e.getMessage());
        }
        return result;
    }
	
	 @Override
	 public Result populate(Workbook workbook) {
	    	Result result = new Result();
	    	try{
	    		int rowIndex = 1;
	            Sheet productSheet = workbook.createSheet("Products");
	            setLayout(productSheet);
	            CellStyle dateCellStyle = workbook.createCellStyle();
	            short df = workbook.createDataFormat().getFormat("dd/mm/yy");
	            dateCellStyle.setDataFormat(df);
	            for(LoanProduct product : products) {
	            	Row row = productSheet.createRow(rowIndex++);
	            	writeInt(ID_COL, row, product.getId());
	            	writeString(NAME_COL, row, product.getName().trim().replaceAll("[ )(]", "_"));
	            	if(product.getFundName() != null)
	            	    writeString(FUND_NAME_COL, row, product.getFundName());
	            	writeInt(PRINCIPAL_COL, row, product.getPrincipal());
	            	if(product.getMinPrincipal()!= null)
	            	    writeInt(MIN_PRINCIPAL_COL, row, product.getMinPrincipal());
	            	else
	            		writeInt(MIN_PRINCIPAL_COL, row, 1);
	            	if(product.getMaxPrincipal() != null)
	            	    writeInt(MAX_PRINCIPAL_COL, row, product.getMaxPrincipal());
	            	else
	            		writeInt(MAX_PRINCIPAL_COL, row, 999999999);
	            	writeInt(NO_OF_REPAYMENTS_COL, row, product.getNumberOfRepayments());
	            	if(product.getMinNumberOfRepayments() != null)
	            		writeInt(MIN_REPAYMENTS_COL, row, product.getMinNumberOfRepayments());
	            	else
	            		writeInt(MIN_REPAYMENTS_COL, row, 1);
	            	if(product.getMaxNumberOfRepayments() != null)
	            		writeInt(MAX_REPAYMENTS_COL, row, product.getMaxNumberOfRepayments());
	            	else
	            		writeInt(MAX_REPAYMENTS_COL, row, 999999999);
	            	writeInt(REPAYMENT_EVERY_COL, row, product.getRepaymentEvery());
	            	writeString(REPAYMENT_FREQUENCY_COL, row, product.getRepaymentFrequencyType().getValue());
	            	writeInt(INTEREST_RATE_COL, row, product.getInterestRatePerPeriod());
	            	if(product.getMinInterestRatePerPeriod() != null)
	            	    writeInt(MIN_INTEREST_RATE_COL, row, product.getMinInterestRatePerPeriod());
	            	else
	            		writeInt(MIN_INTEREST_RATE_COL, row, 1);
	            	if(product.getMaxInterestRatePerPeriod() != null)
	            	    writeInt(MAX_INTEREST_RATE_COL, row, product.getMaxInterestRatePerPeriod());
	            	else
	            		writeInt(MAX_INTEREST_RATE_COL, row, 999999999);
	            	writeString(INTEREST_RATE_FREQUENCY_COL, row, product.getInterestRateFrequencyType().getValue());
	            	writeString(AMORTIZATION_TYPE_COL, row, product.getAmortizationType().getValue());
	            	writeString(INTEREST_TYPE_COL, row, product.getInterestType().getValue());
	            	writeString(INTEREST_CALCULATION_PERIOD_TYPE_COL, row, product.getInterestCalculationPeriodType().getValue());
	            	if(product.getInArrearsTolerance() != null)
	            	    writeInt(IN_ARREARS_TOLERANCE_COL, row, product.getInArrearsTolerance());
	            	writeString(TRANSACTION_PROCESSING_STRATEGY_NAME_COL, row, product.getTransactionProcessingStrategyName());
	            	if(product.getGraceOnPrincipalPayment() != null)
	            	    writeInt(GRACE_ON_PRINCIPAL_PAYMENT_COL, row, product.getGraceOnPrincipalPayment());
	            	if(product.getGraceOnInterestPayment() != null)
	            	    writeInt(GRACE_ON_INTEREST_PAYMENT_COL, row, product.getGraceOnInterestPayment());
	            	if(product.getGraceOnInterestCharged() != null)
	            	    writeInt(GRACE_ON_INTEREST_CHARGED_COL, row, product.getGraceOnInterestCharged());
	            	if(product.getStartDate() != null)
	            	    writeDate(START_DATE_COL, row, product.getStartDate().get(2) + "/" + product.getStartDate().get(1) + "/" + product.getStartDate().get(0), dateCellStyle);
	            	else
	            		writeDate(START_DATE_COL, row, "1/1/1970", dateCellStyle);
	            	if(product.getCloseDate() != null)
	            		writeDate(CLOSE_DATE_COL, row, product.getCloseDate().get(2) + "/" + product.getCloseDate().get(1) + "/" + product.getCloseDate().get(0), dateCellStyle);
	            	else
	            		writeDate(CLOSE_DATE_COL, row, "1/1/2040", dateCellStyle);
	            	productSheet.protectSheet("");
	            }
	    	} catch (RuntimeException re) {
	    		result.addError(re.getMessage());
	    		logger.error(re.getMessage());
	    	}
	        return result;
	 }
	 
	 private void setLayout(Sheet worksheet) {
		    worksheet.setColumnWidth(ID_COL, 2000);
	        worksheet.setColumnWidth(NAME_COL, 5000);
	        worksheet.setColumnWidth(FUND_NAME_COL, 3000);
	        worksheet.setColumnWidth(PRINCIPAL_COL, 3000);
	        worksheet.setColumnWidth(MIN_PRINCIPAL_COL, 3000);
	        worksheet.setColumnWidth(MAX_PRINCIPAL_COL, 3000);
	        worksheet.setColumnWidth(NO_OF_REPAYMENTS_COL, 4000);
	        worksheet.setColumnWidth(MIN_REPAYMENTS_COL, 4000);
	        worksheet.setColumnWidth(MAX_REPAYMENTS_COL, 4000);
	        worksheet.setColumnWidth(REPAYMENT_EVERY_COL, 4000);
	        worksheet.setColumnWidth(REPAYMENT_FREQUENCY_COL, 3000);
	        worksheet.setColumnWidth(INTEREST_RATE_COL, 3000);
	        worksheet.setColumnWidth(MIN_INTEREST_RATE_COL, 3000);
	        worksheet.setColumnWidth(MAX_INTEREST_RATE_COL, 3000);
	        worksheet.setColumnWidth(INTEREST_RATE_FREQUENCY_COL, 3000);
	        worksheet.setColumnWidth(AMORTIZATION_TYPE_COL, 5000);
	        worksheet.setColumnWidth(INTEREST_TYPE_COL, 4000);
	        worksheet.setColumnWidth(INTEREST_CALCULATION_PERIOD_TYPE_COL, 3000);
	        worksheet.setColumnWidth(IN_ARREARS_TOLERANCE_COL, 3000);
	        worksheet.setColumnWidth(TRANSACTION_PROCESSING_STRATEGY_NAME_COL, 6000);
	        worksheet.setColumnWidth(GRACE_ON_PRINCIPAL_PAYMENT_COL, 4000);
	        worksheet.setColumnWidth(GRACE_ON_INTEREST_PAYMENT_COL, 6000);
	        worksheet.setColumnWidth(GRACE_ON_INTEREST_CHARGED_COL, 6000);
	        worksheet.setColumnWidth(START_DATE_COL, 3000);
	        worksheet.setColumnWidth(CLOSE_DATE_COL, 3000);
	        
	        Row rowHeader = worksheet.createRow(0);
	        rowHeader.setHeight((short)500);
	        writeString(ID_COL, rowHeader, "ID");
	        writeString(NAME_COL, rowHeader, "Name");
	        writeString(FUND_NAME_COL, rowHeader, "Fund");
	        writeString(PRINCIPAL_COL, rowHeader, "Principal");
	        writeString(MIN_PRINCIPAL_COL, rowHeader, "Min Principal");
	        writeString(MAX_PRINCIPAL_COL, rowHeader, "Max Principal");
	        writeString(NO_OF_REPAYMENTS_COL, rowHeader, "# of Repayments");
	        writeString(MIN_REPAYMENTS_COL, rowHeader, "Min Repayments");
	        writeString(MAX_REPAYMENTS_COL, rowHeader, "Max Repayments");
	        writeString(REPAYMENT_EVERY_COL, rowHeader, "Repayment Every");
	        writeString(REPAYMENT_FREQUENCY_COL, rowHeader, "Frequency");
	        writeString(INTEREST_RATE_COL, rowHeader, "Interest");
	        writeString(MIN_INTEREST_RATE_COL, rowHeader, "Min Interest");
	        writeString(MAX_INTEREST_RATE_COL, rowHeader, "Max Interest");
	        writeString(INTEREST_RATE_FREQUENCY_COL, rowHeader, "Frequency");
	        writeString(AMORTIZATION_TYPE_COL, rowHeader, "Amortization Type");
	        writeString(INTEREST_TYPE_COL, rowHeader, "Interest Type");
	        writeString(INTEREST_CALCULATION_PERIOD_TYPE_COL, rowHeader, "Interest Calculation Period");
	        writeString(IN_ARREARS_TOLERANCE_COL, rowHeader, "In Arrears Tolerance");
	        writeString(TRANSACTION_PROCESSING_STRATEGY_NAME_COL, rowHeader, "Transaction Processing Strategy");
	        writeString(GRACE_ON_PRINCIPAL_PAYMENT_COL, rowHeader, "Grace On Principal Payment");
	        writeString(GRACE_ON_INTEREST_PAYMENT_COL, rowHeader, "Grace on Interest Payment");
	        writeString(GRACE_ON_INTEREST_CHARGED_COL, rowHeader, "Grace on Interest Charged");
	        writeString(START_DATE_COL, rowHeader, "Start Date");
	        writeString(CLOSE_DATE_COL, rowHeader, "End Date");
	 }
	 
	 public List<LoanProduct> getProducts() {
		 return products;
	 }
	 
	 public Integer getProductsSize() {
		 return products.size();
	 }
	 

}
