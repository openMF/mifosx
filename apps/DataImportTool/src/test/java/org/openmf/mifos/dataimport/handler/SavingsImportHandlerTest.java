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
import org.openmf.mifos.dataimport.dto.Savings;
import org.openmf.mifos.dataimport.dto.SavingsActivation;
import org.openmf.mifos.dataimport.handler.Result;
import org.openmf.mifos.dataimport.handler.savings.SavingsDataImportHandler;
import org.openmf.mifos.dataimport.http.RestClient;

@RunWith(MockitoJUnitRunner.class)
public class SavingsImportHandlerTest {

	@Mock
    RestClient restClient;
    
    @Test
    public void shouldParseSavingsAccount() throws IOException {
        
        InputStream is = this.getClass().getClassLoader().getResourceAsStream("savings/savings.xls");
        Workbook book = new HSSFWorkbook(is);
        SavingsDataImportHandler handler = new SavingsDataImportHandler(book, restClient);
        Result result = handler.parse();
        Assert.assertTrue(result.isSuccess());
        Assert.assertEquals(1, handler.getSavings().size());
        Assert.assertEquals(1, handler.getApprovalDates().size());
        Assert.assertEquals(1, handler.getActivationDates().size());
        Savings savings = handler.getSavings().get(0);
        Approval savingsApproval = handler.getApprovalDates().get(0);
        SavingsActivation savingsActivation = handler.getActivationDates().get(0);
        
        Assert.assertEquals("1", savings.getClientId());
        Assert.assertEquals("1", handler.getIdByName(book.getSheet("Clients"), "Arsene K Wenger").toString());
        Assert.assertEquals("2", savings.getProductId());
        Assert.assertEquals("2", handler.getIdByName(book.getSheet("Products"), "SP2").toString());
        Assert.assertEquals("1", savings.getFieldOfficerId());
        Assert.assertEquals("1", handler.getIdByName(book.getSheet("Staff"), "Sahil Chatta").toString());
        Assert.assertEquals("01 August 2013", savings.getSubmittedOnDate());
        Assert.assertEquals("10", savings.getNominalAnnualInterestRate());
        Assert.assertEquals("4", savings.getInterestCompoundingPeriodType());
        Assert.assertEquals("5", savings.getInterestPostingPeriodType());
        Assert.assertEquals("2", savings.getInterestCalculationType());
        Assert.assertEquals("365", savings.getInterestCalculationDaysInYearType());
        Assert.assertEquals("1000", savings.getMinRequiredOpeningBalance());
        Assert.assertEquals("1", savings.getLockinPeriodFrequency());
        Assert.assertEquals("0", savings.getLockinPeriodFrequencyType());
        Assert.assertEquals("1", savings.getWithdrawalFeeAmount());
        Assert.assertEquals("1", savings.getWithdrawalFeeType());
        Assert.assertEquals("3", savings.getAnnualFeeAmount());
        Assert.assertEquals("01 September", savings.getAnnualFeeOnMonthDay());
        
        Assert.assertEquals("02 August 2013", savingsApproval.getApprovedOnDate());
        Assert.assertEquals("03 August 2013", savingsActivation.getActivatedOnDate());
    }
}
