package org.openmf.mifos.dataimport.populator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.openmf.mifos.dataimport.dto.Fund;
import org.openmf.mifos.dataimport.dto.PaymentType;
import org.openmf.mifos.dataimport.handler.Result;
import org.openmf.mifos.dataimport.http.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class ExtrasSheetPopulator extends AbstractWorkbookPopulator {

    private static final Logger logger = LoggerFactory.getLogger(ExtrasSheetPopulator.class);
	
	private final RestClient client;
	
	private String content;
	
	private List<Fund> funds;
	private List<PaymentType> paymentTypes;
	
	private static final int FUND_ID_COL = 0;
	private static final int FUND_NAME_COL = 1;
	private static final int PAYMENT_TYPE_ID_COL = 2;
	private static final int PAYMENT_TYPE_NAME_COL = 3;
	
	public ExtrasSheetPopulator(RestClient client) {
        this.client = client;
    }
    
    @Override
    public Result downloadAndParse() {
    	Result result = new Result();
        try {
        	client.createAuthToken();
        	funds = new ArrayList<Fund>();
            content = client.get("funds");
            Gson gson = new Gson();
            JsonElement json = new JsonParser().parse(content);
            JsonArray array = json.getAsJsonArray();
            Iterator<JsonElement> iterator = array.iterator();
            while(iterator.hasNext()) {
            	json = iterator.next();
            	Fund fund = gson.fromJson(json, Fund.class);
            	funds.add(fund);
            }
            paymentTypes = new ArrayList<PaymentType>();
            content = client.get("codes/12/codevalues");
            json = new JsonParser().parse(content);
            array = json.getAsJsonArray();
            iterator = array.iterator();
            while(iterator.hasNext()) {
            	json = iterator.next();
            	PaymentType paymentType = gson.fromJson(json, PaymentType.class);
            	paymentTypes.add(paymentType);
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
        int fundRowIndex = 1;
        Sheet extrasSheet = workbook.createSheet("Extras");
        setLayout(extrasSheet);
        for(Fund fund : funds) {
        	Row row = extrasSheet.createRow(fundRowIndex++);
        	writeInt(FUND_ID_COL, row, fund.getId());
        	writeString(FUND_NAME_COL, row, fund.getName().trim().replaceAll("[ )(]", "_"));
         }
        int paymentTypeRowIndex = 1;
        for(PaymentType paymentType: paymentTypes) {
        	Row row;
        	if(paymentTypeRowIndex<fundRowIndex)
        	  row = extrasSheet.getRow(paymentTypeRowIndex++);
        	else
        	  row = extrasSheet.createRow(paymentTypeRowIndex++);	
        	writeInt(PAYMENT_TYPE_ID_COL, row, paymentType.getId());
        	writeString(PAYMENT_TYPE_NAME_COL, row, paymentType.getName().trim().replaceAll("[ )(]", "_"));
        }
        extrasSheet.protectSheet("");
    	} catch (Exception e) {
    		result.addError(e.getMessage());
    		logger.error(e.getMessage());
    	}
        return result;
    }
    
    private void setLayout(Sheet worksheet) {
    	worksheet.setColumnWidth(FUND_ID_COL, 4000);
        worksheet.setColumnWidth(FUND_NAME_COL, 7000);
        worksheet.setColumnWidth(PAYMENT_TYPE_ID_COL, 4000);
        worksheet.setColumnWidth(PAYMENT_TYPE_NAME_COL, 7000);
        Row rowHeader = worksheet.createRow(0);
        rowHeader.setHeight((short)500);
        writeString(FUND_ID_COL, rowHeader, "Fund ID");
        writeString(FUND_NAME_COL, rowHeader, "Name");
        writeString(PAYMENT_TYPE_ID_COL, rowHeader, "Payment Type ID");
        writeString(PAYMENT_TYPE_NAME_COL, rowHeader, "Payment Type Name");
    }
    
    public Integer getFundsSize() {
		 return funds.size();
	 }
    
    public Integer getPaymentTypesSize() {
    	return paymentTypes.size();
    }
    
    public List<Fund> getFunds() {
    	return funds;
    }
    
	public List<PaymentType> getPaymentTypes() {
		return paymentTypes;
	}
}
