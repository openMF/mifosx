package org.openmf.mifos.dataimport.handler.savings;

import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.openmf.mifos.dataimport.dto.Approval;
import org.openmf.mifos.dataimport.dto.Savings;
import org.openmf.mifos.dataimport.dto.SavingsActivation;
import org.openmf.mifos.dataimport.handler.AbstractDataImportHandler;
import org.openmf.mifos.dataimport.handler.Result;
import org.openmf.mifos.dataimport.http.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class SavingsDataImportHandler extends AbstractDataImportHandler {
	
	private static final Logger logger = LoggerFactory.getLogger(SavingsDataImportHandler.class);
	
	@SuppressWarnings("CPD-START")
	 private static final int CLIENT_NAME_COL = 1;
    private static final int PRODUCT_COL = 2;
    private static final int FIELD_OFFICER_NAME_COL = 3;
    private static final int SUBMITTED_ON_DATE_COL = 4;
    private static final int APPROVED_DATE_COL = 5;  
    private static final int ACTIVATION_DATE_COL = 6;
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
	private static final int STATUS_COL = 22;
	private static final int SAVINGS_ID_COL = 23;
    private static final int FAILURE_REPORT_COL = 24;
    @SuppressWarnings("CPD-END")

    private final RestClient restClient;
    
    private final Workbook workbook;
    
    private List<Savings> savings = new ArrayList<Savings>();
    private List<Approval> approvalDates = new ArrayList<Approval>();
    private List<SavingsActivation> activationDates = new ArrayList<SavingsActivation>();

    public SavingsDataImportHandler(Workbook workbook, RestClient client) {
        this.workbook = workbook;
        this.restClient = client;
    }
    
    @Override
    public Result parse() {
        Result result = new Result();
        Sheet savingsSheet = workbook.getSheet("Savings");
        Integer noOfEntries = getNumberOfRows(savingsSheet, 0);
        for (int rowIndex = 1; rowIndex < noOfEntries; rowIndex++) {
            Row row;
            try {
                row = savingsSheet.getRow(rowIndex);
                if(isNotImported(row, STATUS_COL)) {
                    savings.add(parseAsSavings(row));
                    approvalDates.add(parseAsSavingsApproval(row));
                    activationDates.add(parseAsSavingsActivation(row));
                }
            } catch (RuntimeException re) {
                logger.error("row = " + rowIndex, re);
                result.addError("Row = " + rowIndex + " , " + re.getMessage());
            }
        }
        return result;
    }
    
    private Savings parseAsSavings(Row row) {
    	String status = readAsString(STATUS_COL, row);
    	String clientName = readAsString(CLIENT_NAME_COL, row);
        String clientId = getIdByName(workbook.getSheet("Clients"), clientName).toString();
        String productName = readAsString(PRODUCT_COL, row);
        String productId = getIdByName(workbook.getSheet("Products"), productName).toString();
        String fieldOfficerName = readAsString(FIELD_OFFICER_NAME_COL, row);
        String fieldOfficerId = getIdByName(workbook.getSheet("Staff"), fieldOfficerName).toString();
        String submittedOnDate = readAsDate(SUBMITTED_ON_DATE_COL, row);
        String nominalAnnualInterestRate = readAsString(NOMINAL_ANNUAL_INTEREST_RATE_COL, row);
        String interestCompoundingPeriodType = readAsString(INTEREST_COMPOUNDING_PERIOD_COL, row);
        String interestCompoundingPeriodTypeId = "";
        if(interestCompoundingPeriodType.equals("Daily"))
        	interestCompoundingPeriodTypeId = "1";
        else if(interestCompoundingPeriodType.equals("Monthly"))
        	interestCompoundingPeriodTypeId = "4";
        String interestPostingPeriodType = readAsString(INTEREST_POSTING_PERIOD_COL, row);
        String interestPostingPeriodTypeId = "";
        if(interestPostingPeriodType.equals("Monthly"))
        	interestPostingPeriodTypeId = "4";
        else if(interestPostingPeriodType.equals("Quarterly"))
        	interestPostingPeriodTypeId = "5";
        else if(interestPostingPeriodType.equals("Annually"))
        	interestPostingPeriodTypeId = "7";
        String interestCalculationType = readAsString(INTEREST_CALCULATION_COL, row);
        String interestCalculationTypeId = "";
        if(interestCalculationType.equals("Daily Balance"))
        	interestCalculationTypeId = "1";
        else if(interestCalculationType.equals("Average Daily Balance"))
        	interestCalculationTypeId = "2";
        String interestCalculationDaysInYearType = readAsString(INTEREST_CALCULATION_DAYS_IN_YEAR_COL, row);
        String interestCalculationDaysInYearTypeId = "";
        if(interestCalculationDaysInYearType.equals("360 Days"))
        	interestCalculationDaysInYearTypeId = "360";
        else if(interestCalculationDaysInYearType.equals("365 Days"))
        	interestCalculationDaysInYearTypeId = "365";
        String minRequiredOpeningBalance = readAsString(MIN_OPENING_BALANCE_COL, row);
        String lockinPeriodFrequency = readAsString(LOCKIN_PERIOD_COL, row);
        String lockinPeriodFrequencyType = readAsString(LOCKIN_PERIOD_FREQUENCY_COL, row);
        String lockinPeriodFrequencyTypeId = "";
        if(lockinPeriodFrequencyType.equals("Days"))
        	lockinPeriodFrequencyTypeId = "0";
        else if(lockinPeriodFrequencyType.equals("Weeks"))
        	lockinPeriodFrequencyTypeId = "1";
        else if(lockinPeriodFrequencyType.equals("Months"))
        	lockinPeriodFrequencyTypeId = "2";
        else if(lockinPeriodFrequencyType.equals("Years"))
        	lockinPeriodFrequencyTypeId = "3";
        String withdrawalFeeAmount = readAsString(WITHDRAWAL_FEE_AMOUNT_COL, row);
        String withdrawalFeeType = readAsString(WITHDRAWAL_FEE_TYPE_COL, row);
        String withdrawalFeeTypeId = "";
        if(withdrawalFeeType.equals("Flat"))
        	withdrawalFeeTypeId = "1";
        else if(withdrawalFeeType.equals("% of Amount"))
        	withdrawalFeeTypeId = "2";
        String annualFeeAmount = readAsString(ANNUAL_FEE_COL, row);
        String annualFeeOnMonthDay = readAsDateWithoutYear(ANNUAL_FEE_ON_MONTH_DAY_COL, row);
        return new Savings(clientId, productId, fieldOfficerId, submittedOnDate, nominalAnnualInterestRate, interestCompoundingPeriodTypeId, interestPostingPeriodTypeId,
        		interestCalculationTypeId, interestCalculationDaysInYearTypeId, minRequiredOpeningBalance, lockinPeriodFrequency, lockinPeriodFrequencyTypeId, withdrawalFeeAmount,
        		withdrawalFeeTypeId, annualFeeAmount, annualFeeOnMonthDay, row.getRowNum(), status);
    }
    private Approval parseAsSavingsApproval(Row row) {
    	String approvalDate = readAsDate(APPROVED_DATE_COL, row);
    	if(!approvalDate.equals(""))
            return new Approval(approvalDate, row.getRowNum());
         else
            return null;	
    }
    private SavingsActivation parseAsSavingsActivation(Row row) {
    	String activationDate = readAsDate(ACTIVATION_DATE_COL, row);
    	 if(!activationDate.equals(""))
             return new SavingsActivation(activationDate, row.getRowNum());
          else
             return null;
    }
    
    @Override
    public Result upload() {
        Result result = new Result();
        Sheet savingsSheet = workbook.getSheet("Savings");
        restClient.createAuthToken();
        int progressLevel = 0;
        String savingsId = "";
        for (int i = 0; i < savings.size(); i++) {
            try {
            	progressLevel = 0;
                Gson gson = new Gson();
                String payload ="", response = "";
                String status = savings.get(i).getStatus();

                if(status.equals("Creation failed.") || status.equals(""))
                {
                  payload = gson.toJson(savings.get(i));
                  logger.info(payload);
                  response = restClient.post("savingsaccounts", payload);
                  progressLevel = 1;
                
                  JsonParser parser = new JsonParser();
                  JsonObject obj = parser.parse(response).getAsJsonObject();
                  savingsId = obj.get("savingsId").getAsString();
                } else {
                	savingsId = readAsInt(SAVINGS_ID_COL, savingsSheet.getRow(savings.get(i).getRowIndex()));
                }
                
                if(status.equals("Approval failed.") || status.equals("Creation failed.") || status.equals(""))
                {
                  if(approvalDates.get(i) != null) {
                     payload = gson.toJson(approvalDates.get(i));
                     logger.info(payload);
                     restClient.post("savingsaccounts/" + savingsId + "?command=approve", payload);
                  }
                  progressLevel = 2;
                }  
                
                if((status.equals("Activation failed.") || status.equals("Approval failed.") || status.equals("Creation failed.") || status.equals("")) && approvalDates.get(i) != null && activationDates.get(i) != null)
                {
                  payload = gson.toJson(activationDates.get(i));
                  logger.info(payload);
                  restClient.post("savingsaccounts/" + savingsId + "?command=activate", payload);
                }
                
                Cell statusCell = savingsSheet.getRow(savings.get(i).getRowIndex()).createCell(STATUS_COL);
                statusCell.setCellValue("Imported");
                statusCell.setCellStyle(getCellStyle(workbook, IndexedColors.LIGHT_GREEN));
            } catch (RuntimeException e) {
            	logger.info(e.getMessage());
            	String message = parseStatus(e.getMessage());
            	String status = "";
            	Row row = savingsSheet.getRow(savings.get(i).getRowIndex());
            	Cell statusCell = row.createCell(STATUS_COL);
            	if(progressLevel == 0)
            		status = "Creation";
            	else if(progressLevel == 1)
            		status = "Approval";
            	else if(progressLevel == 2)
            		status = "Activation";
                statusCell.setCellValue(status + " failed.");
                statusCell.setCellStyle(getCellStyle(workbook, IndexedColors.RED));
                if(progressLevel>0)
                	row.createCell(SAVINGS_ID_COL).setCellValue(savingsId);
                Cell errorReportCell = row.createCell(FAILURE_REPORT_COL);
            	errorReportCell.setCellValue(message);
                result.addError("Row = " + savings.get(i).getRowIndex() + " ," + message);
            }
        }
        savingsSheet.setColumnWidth(STATUS_COL, 4000);
        Row rowHeader = savingsSheet.getRow(0);
    	writeString(STATUS_COL, rowHeader, "Status");
    	writeString(SAVINGS_ID_COL, rowHeader, "Loan ID");
    	writeString(FAILURE_REPORT_COL, rowHeader, "Report");
        return result;
    }
    
    public List<Savings> getSavings() {
    	return savings;
    }
    
    public List<Approval> getApprovalDates() {
    	return approvalDates;
    }
    
    public List<SavingsActivation> getActivationDates() {
    	return activationDates;
    }
    
}
