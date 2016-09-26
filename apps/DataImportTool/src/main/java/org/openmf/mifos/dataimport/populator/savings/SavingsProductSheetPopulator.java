package org.openmf.mifos.dataimport.populator.savings;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.openmf.mifos.dataimport.dto.Currency;
import org.openmf.mifos.dataimport.dto.SavingsProduct;
import org.openmf.mifos.dataimport.handler.Result;
import org.openmf.mifos.dataimport.http.RestClient;
import org.openmf.mifos.dataimport.populator.AbstractWorkbookPopulator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class SavingsProductSheetPopulator extends AbstractWorkbookPopulator {
	
private static final Logger logger = LoggerFactory.getLogger(SavingsProductSheetPopulator.class);
	
    private final RestClient client;
	
	private String content;
	
	private static final int ID_COL = 0;
	private static final int NAME_COL = 1;
	private static final int NOMINAL_ANNUAL_INTEREST_RATE_COL = 2;
	private static final int INTEREST_COMPOUNDING_PERIOD_COL = 3;
	private static final int INTEREST_POSTING_PERIOD_COL = 4;
	private static final int INTEREST_CALCULATION_COL = 5;
	private static final int INTEREST_CALCULATION_DAYS_IN_YEAR_COL = 6;
	private static final int MIN_OPENING_BALANCE_COL = 7;
	private static final int LOCKIN_PERIOD_COL = 8;
	private static final int LOCKIN_PERIOD_FREQUENCY_COL = 9;
	private static final int WITHDRAWAL_FEE_AMOUNT_COL = 10;
	private static final int WITHDRAWAL_FEE_TYPE_COL = 11;
	private static final int ANNUAL_FEE_COL = 12;
	private static final int ANNUAL_FEE_ON_MONTH_DAY_COL = 13;
	private static final int CURRENCY_COL = 14;
	private static final int DECIMAL_PLACES_COL = 15;
	private static final int IN_MULTIPLES_OF_COL = 16;
	
	private List<SavingsProduct> products;
	
	public SavingsProductSheetPopulator(RestClient client) {
        this.client = client;
    }
	
	@Override
    public Result downloadAndParse() {
    	Result result = new Result();
        try {
        	client.createAuthToken();
        	products = new ArrayList<SavingsProduct>();
            content = client.get("savingsproducts");
            Gson gson = new Gson();
            JsonElement json = new JsonParser().parse(content);
            JsonArray array = json.getAsJsonArray();
            Iterator<JsonElement> iterator = array.iterator();
            while(iterator.hasNext()) {
            	json = iterator.next();
            	SavingsProduct product = gson.fromJson(json, SavingsProduct.class);
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
	            short df = workbook.createDataFormat().getFormat("dd-mmm");
	            dateCellStyle.setDataFormat(df);
	            for(SavingsProduct product : products) {
	            	Row row = productSheet.createRow(rowIndex++);
	            	writeInt(ID_COL, row, product.getId());
	            	writeString(NAME_COL, row, product.getName().trim().replaceAll("[ )(]", "_"));
	            	writeDouble(NOMINAL_ANNUAL_INTEREST_RATE_COL, row, product.getNominalAnnualInterestRate());
	            	writeString(INTEREST_COMPOUNDING_PERIOD_COL, row, product.getInterestCompoundingPeriodType().getValue());
	            	writeString(INTEREST_POSTING_PERIOD_COL, row, product.getInterestPostingPeriodType().getValue());
	            	writeString(INTEREST_CALCULATION_COL, row, product.getInterestCalculationType().getValue());
	            	writeString(INTEREST_CALCULATION_DAYS_IN_YEAR_COL, row, product.getInterestCalculationDaysInYearType().getValue());
	            	if(product.getMinRequiredOpeningBalance() != null)
	            	    writeDouble(MIN_OPENING_BALANCE_COL, row, product.getMinRequiredOpeningBalance());
	            	if(product.getLockinPeriodFrequency() != null)
	            	    writeInt(LOCKIN_PERIOD_COL, row, product.getLockinPeriodFrequency());
	            	if(product.getLockinPeriodFrequencyType() != null)
	            	    writeString(LOCKIN_PERIOD_FREQUENCY_COL, row, product.getLockinPeriodFrequencyType().getValue());
	            	if(product.getWithdrawalFeeAmount() != null)
	            	    writeDouble(WITHDRAWAL_FEE_AMOUNT_COL, row, product.getWithdrawalFeeAmount());
	            	if(product.getWithdrawalFeeType() != null)
	            	    writeString(WITHDRAWAL_FEE_TYPE_COL, row, product.getWithdrawalFeeType().getValue());
	            	if(product.getAnnualFeeAmount() != null)
	            	    writeDouble(ANNUAL_FEE_COL, row, product.getAnnualFeeAmount());
	            	if(product.getAnnualFeeOnMonthDay() != null)
	            	    writeDate(ANNUAL_FEE_ON_MONTH_DAY_COL, row, product.getAnnualFeeOnMonthDay().get(1) + "/" + product.getAnnualFeeOnMonthDay().get(0) + "/2010" , dateCellStyle);
	            	Currency currency = product.getCurrency();
	            	writeString(CURRENCY_COL, row, currency.getCode());
	            	writeInt(DECIMAL_PLACES_COL, row, currency.getDecimalPlaces());
	            	if(currency.getInMultiplesOf() != null)
	            		writeInt(IN_MULTIPLES_OF_COL, row, currency.getInMultiplesOf());
	            }
	        	productSheet.protectSheet("");
    	} catch (RuntimeException re) {
    		result.addError(re.getMessage());
    		logger.error(re.getMessage());
    	}
        return result;
      }
	
	private void setLayout(Sheet worksheet) {
		Row rowHeader = worksheet.createRow(0);
        rowHeader.setHeight((short)500);
        worksheet.setColumnWidth(ID_COL, 2000);
        worksheet.setColumnWidth(NAME_COL, 5000);
        worksheet.setColumnWidth(NOMINAL_ANNUAL_INTEREST_RATE_COL, 2000);
        worksheet.setColumnWidth(INTEREST_COMPOUNDING_PERIOD_COL, 3000);
        worksheet.setColumnWidth(INTEREST_POSTING_PERIOD_COL, 3000);
        worksheet.setColumnWidth(INTEREST_CALCULATION_COL, 3000);
        worksheet.setColumnWidth(INTEREST_CALCULATION_DAYS_IN_YEAR_COL, 3000);
        worksheet.setColumnWidth(MIN_OPENING_BALANCE_COL, 3000);
        worksheet.setColumnWidth(LOCKIN_PERIOD_COL, 3000);
        worksheet.setColumnWidth(LOCKIN_PERIOD_FREQUENCY_COL, 3000);
        worksheet.setColumnWidth(WITHDRAWAL_FEE_AMOUNT_COL, 3000);
        worksheet.setColumnWidth(WITHDRAWAL_FEE_TYPE_COL, 3000);
        worksheet.setColumnWidth(ANNUAL_FEE_COL, 3000);
        worksheet.setColumnWidth(ANNUAL_FEE_ON_MONTH_DAY_COL, 3000);
        worksheet.setColumnWidth(CURRENCY_COL, 2000);
        worksheet.setColumnWidth(DECIMAL_PLACES_COL, 3000);
        worksheet.setColumnWidth(IN_MULTIPLES_OF_COL, 3500);
        
        writeString(ID_COL, rowHeader, "ID");
        writeString(NAME_COL, rowHeader, "Name");
        writeString(NOMINAL_ANNUAL_INTEREST_RATE_COL, rowHeader, "Interest");
        writeString(INTEREST_COMPOUNDING_PERIOD_COL, rowHeader, "Interest Compounding Period");
        writeString(INTEREST_POSTING_PERIOD_COL, rowHeader, "Interest Posting Period");
        writeString(INTEREST_CALCULATION_COL, rowHeader, "Interest Calculated Using");
        writeString(INTEREST_CALCULATION_DAYS_IN_YEAR_COL, rowHeader, "# Days In Year");
        writeString(MIN_OPENING_BALANCE_COL, rowHeader, "Min Opening Balance");
        writeString(LOCKIN_PERIOD_COL, rowHeader, "Locked In For");
        writeString(LOCKIN_PERIOD_FREQUENCY_COL, rowHeader, "Frequency");
        writeString(WITHDRAWAL_FEE_AMOUNT_COL, rowHeader, "Withdrawal Fee");
        writeString(WITHDRAWAL_FEE_TYPE_COL, rowHeader, "Type");
        writeString(ANNUAL_FEE_COL, rowHeader, "Annual Fee");
        writeString(ANNUAL_FEE_ON_MONTH_DAY_COL, rowHeader, "On");
        writeString(CURRENCY_COL, rowHeader, "Currency");
        writeString(DECIMAL_PLACES_COL, rowHeader, "Decimal Places");
        writeString(IN_MULTIPLES_OF_COL, rowHeader, "In Multiples Of");
	}
	
	public List<SavingsProduct> getProducts() {
		 return products;
	 }
	
	public Integer getProductsSize() {
		 return products.size();
	 }
}
