package org.openmf.mifos.dataimport.handler;

import java.io.IOException;
import java.io.InputStream;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openmf.mifos.dataimport.dto.Approval;
import org.openmf.mifos.dataimport.dto.Loan;
import org.openmf.mifos.dataimport.dto.LoanDisbursal;
import org.openmf.mifos.dataimport.dto.LoanRepayment;
import org.openmf.mifos.dataimport.handler.Result;
import org.openmf.mifos.dataimport.handler.loan.LoanDataImportHandler;
import org.openmf.mifos.dataimport.http.RestClient;

@RunWith(MockitoJUnitRunner.class)
public class LoanImportHandlerTest {

	@Mock
    RestClient restClient;
    
    @Test
    public void shouldParseLoan() throws IOException {
        
        InputStream is = this.getClass().getClassLoader().getResourceAsStream("loan/loan.xls");
        Workbook book = new HSSFWorkbook(is);
        LoanDataImportHandler handler = new LoanDataImportHandler(book, restClient);
        Result result = handler.parse();
        Assert.assertTrue(result.isSuccess());
        Assert.assertEquals(1, handler.getLoans().size());
        Assert.assertEquals(1, handler.getApprovalDates().size());
        Assert.assertEquals(1, handler.getDisbursalDates().size());
        Assert.assertEquals(1, handler.getLoanRepayments().size());
        Loan loan = handler.getLoans().get(0);
        Approval loanApproval = handler.getApprovalDates().get(0);
        LoanDisbursal loanDisbursal = handler.getDisbursalDates().get(0);
        LoanRepayment loanRepayment = handler.getLoanRepayments().get(0);
        Assert.assertEquals("1", loan.getClientId());
        Assert.assertEquals("1", handler.getIdByName(book.getSheet("Clients"), "Arsene K Wenger").toString());
        Assert.assertEquals("1", loan.getProductId());
        Assert.assertEquals("1", handler.getIdByName(book.getSheet("Products"), "HM").toString());
        Assert.assertEquals("1", loan.getLoanOfficerId());
        Assert.assertEquals("1", handler.getIdByName(book.getSheet("Staff"), "Sahil Chatta").toString());
        Assert.assertEquals("02 July 2013", loan.getSubmittedOnDate());
        Assert.assertEquals("1", loan.getFundId());
        Assert.assertEquals("1", handler.getIdByName(book.getSheet("Extras"), "Fund1").toString());
        Assert.assertEquals("15000.0", loan.getPrincipal());
        Assert.assertEquals("10", loan.getNumberOfRepayments());
        Assert.assertEquals("2", loan.getRepaymentEvery());
        Assert.assertEquals("2", loan.getRepaymentFrequencyType());
        Assert.assertEquals("20", loan.getLoanTermFrequency());
        Assert.assertEquals("2", loan.getLoanTermFrequencyType());
        Assert.assertEquals("7", loan.getInterestRatePerPeriod());
        Assert.assertEquals("1", loan.getAmortizationType());
        Assert.assertEquals("0", loan.getInterestType());
        Assert.assertEquals("1", loan.getInterestCalculationPeriodType());
        Assert.assertEquals("3", loan.getInArrearsTolerance());
        Assert.assertEquals("4", loan.getTransactionProcessingStrategyId());
        Assert.assertEquals("1", loan.getGraceOnPrincipalPayment());
        Assert.assertEquals("2", loan.getGraceOnInterestPayment());
        Assert.assertEquals("1", loan.getGraceOnInterestCharged());
        Assert.assertEquals("04 July 2013", loan.getInterestChargedFromDate());
        Assert.assertEquals("01 August 2013", loan.getRepaymentsStartingFromDate());
        Assert.assertEquals("03 July 2013", loanApproval.getApprovedOnDate());
        Assert.assertEquals("04 July 2013", loanDisbursal.getActualDisbursementDate());
        Assert.assertEquals("10", loanDisbursal.getPaymentTypeId());
        Assert.assertEquals("10", handler.getIdByName(book.getSheet("Extras"), "Cash").toString());
        Assert.assertEquals("3000.0", loanRepayment.getTransactionAmount());
        Assert.assertEquals("01 September 2013", loanRepayment.getTransactionDate());
        Assert.assertEquals("11", loanRepayment.getPaymentTypeId());
        Assert.assertEquals("11", handler.getIdByName(book.getSheet("Extras"), "MPesa").toString());
    }
}
