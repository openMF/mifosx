package org.openmf.mifos.dataimport.populator;

import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.openmf.mifos.dataimport.dto.Fund;
import org.openmf.mifos.dataimport.dto.PaymentType;
import org.openmf.mifos.dataimport.handler.Result;
import org.openmf.mifos.dataimport.http.RestClient;
import org.openmf.mifos.dataimport.populator.ExtrasSheetPopulator;

@RunWith(MockitoJUnitRunner.class)
public class ExtrasSheetPopulatorTest {
	
	 // SUT - System Under Test
    ExtrasSheetPopulator populator;

    @Mock
	RestClient restClient;
    
    @Test
    public void shouldDownloadAndParseFundsAndPaymentTypes() {
    	
        Mockito.when(restClient.get("funds")).thenReturn("[{\"id\": 1,\"name\": \"Fund1\"}]");
        Mockito.when(restClient.get("codes/12/codevalues")).thenReturn("[{\"id\": 10,\"name\": \"Cash\",\"position\": 1},{\"id\": 11,\"name\": \"MPesa\",\"position\": 2}]");

    	populator = new ExtrasSheetPopulator(restClient);
    	Result result = populator.downloadAndParse();
    	List<Fund> funds = populator.getFunds();
    	List<PaymentType> paymentTypes = populator.getPaymentTypes();
    	Assert.assertEquals(1, funds.size());
    	Assert.assertEquals(2, paymentTypes.size());
    	Fund fund = funds.get(0);
    	PaymentType paymentType = paymentTypes.get(1);
    	
    	Assert.assertTrue(result.isSuccess());
    	Mockito.verify(restClient, Mockito.atLeastOnce()).get("funds");
    	Mockito.verify(restClient, Mockito.atLeastOnce()).get("codes/12/codevalues");
    	Assert.assertEquals("1", fund.getId().toString());
    	Assert.assertEquals("Fund1", fund.getName());
    	Assert.assertEquals("11", paymentType.getId().toString());
    	Assert.assertEquals("MPesa", paymentType.getName());
    }
    
    @Test
    public void shouldPopulateExtrasSheet() {
    	
    	Mockito.when(restClient.get("funds")).thenReturn("[{\"id\": 1,\"name\": \"Fund1\"}]");
        Mockito.when(restClient.get("codes/12/codevalues")).thenReturn("[{\"id\": 10,\"name\": \"Cash\",\"position\": 1},{\"id\": 11,\"name\": \"MPesa\",\"position\": 2}]");

    	populator = new ExtrasSheetPopulator(restClient);
    	Result result = populator.downloadAndParse();
    	Workbook book = new HSSFWorkbook();
    	result = populator.populate(book);
    	
    	Assert.assertTrue(result.isSuccess());
    	Mockito.verify(restClient, Mockito.atLeastOnce()).get("funds");
    	Mockito.verify(restClient, Mockito.atLeastOnce()).get("codes/12/codevalues");
    	
    	Sheet extrasSheet = book.getSheet("Extras");
    	Row row = extrasSheet.getRow(1);
    	Assert.assertEquals("1.0", "" + row.getCell(0).getNumericCellValue());
    	Assert.assertEquals("Fund1", row.getCell(1).getStringCellValue());
    	Assert.assertEquals("10.0", "" + row.getCell(2).getNumericCellValue());
    	Assert.assertEquals("Cash", row.getCell(3).getStringCellValue());
    }

}
