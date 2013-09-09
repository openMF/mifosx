package org.openmf.mifos.dataimport.handler.loan;

import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.openmf.mifos.dataimport.dto.Approval;
import org.openmf.mifos.dataimport.dto.Loan;
import org.openmf.mifos.dataimport.dto.LoanDisbursal;
import org.openmf.mifos.dataimport.dto.LoanRepayment;
import org.openmf.mifos.dataimport.handler.AbstractDataImportHandler;
import org.openmf.mifos.dataimport.handler.Result;
import org.openmf.mifos.dataimport.http.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class LoanDataImportHandler extends AbstractDataImportHandler {
	
	private static final Logger logger = LoggerFactory.getLogger(LoanDataImportHandler.class);
	
	@SuppressWarnings("CPD-START")
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
    private static final int STATUS_COL = 30;
    private static final int LOAN_ID_COL = 31;
    private static final int FAILURE_REPORT_COL = 32;
    @SuppressWarnings("CPD-END")
    
    private List<Loan> loans = new ArrayList<Loan>();
    private List<Approval> approvalDates = new ArrayList<Approval>();
    private List<LoanDisbursal> disbursalDates = new ArrayList<LoanDisbursal>();
    private List<LoanRepayment> loanRepayments = new ArrayList<LoanRepayment>();
    
    private final RestClient restClient;
    
    private final Workbook workbook;

    public LoanDataImportHandler(Workbook workbook, RestClient client) {
        this.workbook = workbook;
        this.restClient = client;
    }
    
    @Override
    public Result parse() {
        Result result = new Result();
        Sheet loanSheet = workbook.getSheet("Loans");
        Integer noOfEntries = getNumberOfRows(loanSheet, 0);
        for (int rowIndex = 1; rowIndex < noOfEntries; rowIndex++) {
            Row row;
            try {
                row = loanSheet.getRow(rowIndex);
                if(isNotImported(row, STATUS_COL)) {
                    loans.add(parseAsLoan(row));
                    approvalDates.add(parseAsLoanApproval(row));
                    disbursalDates.add(parseAsLoanDisbursal(row));
                    loanRepayments.add(parseAsLoanRepayment(row));
                }
            } catch (RuntimeException re) {
                logger.error("row = " + rowIndex, re);
                result.addError("Row = " + rowIndex + " , " + re.getMessage());
            }
        }
    
     return result;
   }
    
    private Loan parseAsLoan(Row row) {
    	String status = readAsString(STATUS_COL, row);
    	String clientName = readAsString(CLIENT_NAME_COL, row);
        String clientId = getIdByName(workbook.getSheet("Clients"), clientName).toString();
        String productName = readAsString(PRODUCT_COL, row);
        String productId = getIdByName(workbook.getSheet("Products"), productName).toString();
        String loanOfficerName = readAsString(LOAN_OFFICER_NAME_COL, row);
        String loanOfficerId = getIdByName(workbook.getSheet("Staff"), loanOfficerName).toString();
        String submittedOnDate = readAsDate(SUBMITTED_ON_DATE_COL, row);
        String fundName = readAsString(FUND_NAME_COL, row);
        String fundId;
        if(fundName.equals(""))
        	fundId = "";
        else
            fundId = getIdByName(workbook.getSheet("Extras"), fundName).toString();
        String principal = readAsDouble(PRINCIPAL_COL, row).toString();
        String numberOfRepayments = readAsString(NO_OF_REPAYMENTS_COL, row);
        String repaidEvery = readAsString(REPAID_EVERY_COL, row);
        String repaidEveryFrequency = readAsString(REPAID_EVERY_FREQUENCY_COL, row);
        String repaidEveryFrequencyId = "";
        if(repaidEveryFrequency.equals("Days"))
        	repaidEveryFrequencyId = "0";
        else if(repaidEveryFrequency.equals("Weeks"))
        	repaidEveryFrequencyId = "1";
        else if(repaidEveryFrequency.equals("Months"))
        	repaidEveryFrequencyId = "2";
        String loanTerm = readAsString(LOAN_TERM_COL, row);
        String loanTermFrequency = readAsString(LOAN_TERM_FREQUENCY_COL, row);
        String loanTermFrequencyId = "";
        if(loanTermFrequency.equals("Days"))
        	loanTermFrequencyId = "0";
        else if(loanTermFrequency.equals("Weeks"))
        	loanTermFrequencyId = "1";
        else if(loanTermFrequency.equals("Months"))
        	loanTermFrequencyId = "2";
        String nominalInterestRate = readAsString(NOMINAL_INTEREST_RATE_COL, row);
        String amortization = readAsString(AMORTIZATION_COL, row);
        String amortizationId = "";
        if(amortization.equals("Equal principal payments"))
        	amortizationId = "0";
        else if(amortization.equals("Equal installments"))
        	amortizationId = "1";
        String interestMethod = readAsString(INTEREST_METHOD_COL, row);
        String interestMethodId = "";
        if(interestMethod.equals("Flat"))
        	interestMethodId = "1";
        else if(interestMethod.equals("Declining Balance"))
        	interestMethodId = "0";
        String interestCalculationPeriod = readAsString(INTEREST_CALCULATION_PERIOD_COL, row);
        String interestCalculationPeriodId = "";
        if(interestCalculationPeriod.equals("Daily"))
        	interestCalculationPeriodId = "0";
        else if(interestCalculationPeriod.equals("Same as repayment period"))
        	interestCalculationPeriodId = "1";
        String arrearsTolerance = readAsString(ARREARS_TOLERANCE_COL, row);
        String repaymentStrategy = readAsString(REPAYMENT_STRATEGY_COL, row);
        String repaymentStrategyId = "";
        if(repaymentStrategy.equals("Mifos style"))
        	repaymentStrategyId = "1";
        else if(repaymentStrategy.equals("Heavensfamily"))
        	repaymentStrategyId = "2";
        else if(repaymentStrategy.equals("Creocore"))
        	repaymentStrategyId = "3";
        else if(repaymentStrategy.equals("RBI (India)"))
        	repaymentStrategyId = "4";
        else if(repaymentStrategy.equals("Principal Interest Penalties Fees Order"))
        	repaymentStrategyId = "5";
        else if(repaymentStrategy.equals("Interest Principal Penalties Fees Order"))
        	repaymentStrategyId = "6";
        String graceOnPrincipalPayment = readAsString(GRACE_ON_PRINCIPAL_PAYMENT_COL, row);
        String graceOnInterestPayment = readAsString(GRACE_ON_INTEREST_PAYMENT_COL, row);
        String graceOnInterestCharged = readAsString(GRACE_ON_INTEREST_CHARGED_COL, row);
        String interestChargedFromDate = readAsDate(INTEREST_CHARGED_FROM_COL, row);
        String firstRepaymentOnDate = readAsDate(FIRST_REPAYMENT_COL, row);
       
        
        return new Loan(clientId, productId, loanOfficerId, submittedOnDate, fundId, principal, numberOfRepayments, repaidEvery, repaidEveryFrequencyId, loanTerm,
        		loanTermFrequencyId, nominalInterestRate, submittedOnDate, amortizationId, interestMethodId, interestCalculationPeriodId, arrearsTolerance, repaymentStrategyId,
        		graceOnPrincipalPayment, graceOnInterestPayment, graceOnInterestCharged, interestChargedFromDate, firstRepaymentOnDate, row.getRowNum(), status);
    }
    
    private Approval parseAsLoanApproval(Row row) {
        String approvedDate = readAsDate(APPROVED_DATE_COL, row);
        if(!approvedDate.equals(""))
           return new Approval(approvedDate, row.getRowNum());
         else
            return null;	
    }
    
    private LoanDisbursal parseAsLoanDisbursal(Row row) {
    	String disbursedDate = readAsDate(DISBURSED_DATE_COL, row);
    	String paymentType = readAsString(DISBURSED_PAYMENT_TYPE_COL, row);
        String paymentTypeId = getIdByName(workbook.getSheet("Extras"), paymentType).toString();
        if(!disbursedDate.equals(""))
            return new LoanDisbursal(disbursedDate, paymentTypeId, row.getRowNum());
         else
            return null;
    }
    
    private LoanRepayment parseAsLoanRepayment(Row row) {
    	String repaymentAmount = readAsDouble(TOTAL_AMOUNT_REPAID_COL, row).toString();
        String lastRepaymentDate = readAsDate(LAST_REPAYMENT_DATE_COL, row);
        String repaymentType = readAsString(REPAYMENT_TYPE_COL, row);
        String repaymentTypeId = getIdByName(workbook.getSheet("Extras"), repaymentType).toString();
        if(!repaymentAmount.equals(""))
            return new LoanRepayment(repaymentAmount, lastRepaymentDate, repaymentTypeId, row.getRowNum());
         else
            return null;	
    }
    @Override
    public Result upload() {
        Result result = new Result();
        Sheet loanSheet = workbook.getSheet("Loans");
        restClient.createAuthToken();
        int progressLevel = 0;
        String loanId = "";
        for (int i = 0; i < loans.size(); i++) {
            try {
            	progressLevel = 0;
                Gson gson = new Gson();
                String payload ="", response = "";
                String status = loans.get(i).getStatus();
                if(status.equals("Creation failed.") || status.equals(""))
                {
                  payload = gson.toJson(loans.get(i));
                  logger.info(payload);
                  response = restClient.post("loans", payload);
                  logger.info(response);
                  progressLevel = 1;
                
                  JsonParser parser = new JsonParser();
                  JsonObject obj = parser.parse(response).getAsJsonObject();
                  loanId = obj.get("loanId").getAsString();
                } else {
                	loanId = readAsInt(LOAN_ID_COL, loanSheet.getRow(loans.get(i).getRowIndex()));
                }
                
                if(status.equals("Approval failed.") || status.equals("Creation failed.") || status.equals(""))
                {
                  if(approvalDates.get(i) != null) {
                     payload = gson.toJson(approvalDates.get(i));
                     logger.info(payload);
                     restClient.post("loans/" + loanId + "?command=approve", payload);
                  }
                  progressLevel = 2;
                }  
                
                if(status.equals("Disbursal failed.") || status.equals("Approval failed.") || status.equals("Creation failed.") || status.equals(""))
                {
                  if(approvalDates.get(i) != null && disbursalDates.get(i) != null) {
                  payload = gson.toJson(disbursalDates.get(i));
                  logger.info(payload);
                  restClient.post("loans/" + loanId + "?command=disburse", payload);
                  }
                  progressLevel = 3;
                }
                 
                
                if(loanRepayments.get(i) != null && approvalDates.get(i) != null && disbursalDates.get(i) != null) {
                	  payload = gson.toJson(loanRepayments.get(i));
                	  logger.info(payload);
                	  restClient.post("loans/" + loanId + "/transactions?command=repayment", payload);
                }
                
                Cell statusCell = loanSheet.getRow(loans.get(i).getRowIndex()).createCell(STATUS_COL);
                statusCell.setCellValue("Imported");
                statusCell.setCellStyle(getCellStyle(workbook, IndexedColors.LIGHT_GREEN));
            } catch (RuntimeException re) {
            	String message = parseStatus(re.getMessage());
            	String status = "";
            	Row row = loanSheet.getRow(loans.get(i).getRowIndex());
            	Cell statusCell = row.createCell(STATUS_COL);
            	if(progressLevel == 0)
            		status = "Creation";
            	else if(progressLevel == 1)
            		status = "Approval";
            	else if(progressLevel == 2)
            		status = "Disbursal";
            	else if(progressLevel == 3)
            		status = "Repayment";
                statusCell.setCellValue(status + " failed.");
                statusCell.setCellStyle(getCellStyle(workbook, IndexedColors.RED));
                if(progressLevel>0)
                	row.createCell(LOAN_ID_COL).setCellValue(loanId);
            	Cell errorReportCell = row.createCell(FAILURE_REPORT_COL);
            	errorReportCell.setCellValue(message);
                result.addError("Row = " + loans.get(i).getRowIndex() + " ," + message);
            }
        }
        loanSheet.setColumnWidth(STATUS_COL, 4000);
        Row rowHeader = loanSheet.getRow(0);
    	writeString(STATUS_COL, rowHeader, "Status");
    	writeString(LOAN_ID_COL, rowHeader, "Loan ID");
    	writeString(FAILURE_REPORT_COL, rowHeader, "Report");
        return result;
    }
    
    public List<Loan> getLoans() {
    	return loans;
    }
    
    public List<Approval> getApprovalDates() {
    	return approvalDates;
    }
    
    public List<LoanDisbursal> getDisbursalDates() {
    	return disbursalDates;
    }
    
    public List<LoanRepayment> getLoanRepayments() {
    	return loanRepayments;
    }
}
