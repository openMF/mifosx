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
import org.openmf.mifos.dataimport.dto.LoanRepayment;
import org.openmf.mifos.dataimport.handler.Result;
import org.openmf.mifos.dataimport.handler.loan.LoanRepaymentDataImportHandler;
import org.openmf.mifos.dataimport.http.RestClient;

@RunWith(MockitoJUnitRunner.class)
public class LoanRepaymentImportHandlerTest {

	 @Mock
	    RestClient restClient;
	    
	    @Test
	    public void shouldParseLoanRepayment() throws IOException {
	        
	        InputStream is = this.getClass().getClassLoader().getResourceAsStream("loan/loanRepaymentHistory.xls");
	        Workbook book = new HSSFWorkbook(is);
	        LoanRepaymentDataImportHandler handler = new LoanRepaymentDataImportHandler(book, restClient);
	        Result result = handler.parse();
	        Assert.assertTrue(result.isSuccess());
	        Assert.assertEquals(2, handler.getLoanRepayments().size());
	        LoanRepayment loanRepayment = handler.getLoanRepayments().get(0);
	        LoanRepayment loanRepaymentWithoutId = handler.getLoanRepayments().get(1);
	        Assert.assertEquals("1", loanRepayment.getLoanAccountId().toString());
	        Assert.assertEquals("1", loanRepaymentWithoutId.getLoanAccountId().toString());
	        Assert.assertEquals("1300.0", loanRepayment.getTransactionAmount());
	        Assert.assertEquals("700.0", loanRepaymentWithoutId.getTransactionAmount());
	        Assert.assertEquals("01 September 2013", loanRepayment.getTransactionDate());
	        Assert.assertEquals("03 September 2013", loanRepaymentWithoutId.getTransactionDate());
	        Assert.assertEquals("11", loanRepayment.getPaymentTypeId());
	        Assert.assertEquals("11", handler.getIdByName(book.getSheet("Extras"), "MPesa").toString());
	        Assert.assertEquals("10", loanRepaymentWithoutId.getPaymentTypeId());
	        Assert.assertEquals("10", handler.getIdByName(book.getSheet("Extras"), "Cash").toString());
	        Assert.assertEquals("12", loanRepayment.getAccountNumber());
	        Assert.assertEquals("13", loanRepayment.getCheckNumber());
	        Assert.assertEquals("14", loanRepayment.getRoutingCode());
	        Assert.assertEquals("15", loanRepayment.getReceiptNumber());
	        Assert.assertEquals("16", loanRepayment.getBankNumber());
	    }
}
