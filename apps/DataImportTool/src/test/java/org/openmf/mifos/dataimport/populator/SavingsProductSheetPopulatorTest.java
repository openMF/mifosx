package org.openmf.mifos.dataimport.populator;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
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
import org.openmf.mifos.dataimport.dto.SavingsProduct;
import org.openmf.mifos.dataimport.handler.Result;
import org.openmf.mifos.dataimport.http.RestClient;
import org.openmf.mifos.dataimport.populator.savings.SavingsProductSheetPopulator;

@RunWith(MockitoJUnitRunner.class)
public class SavingsProductSheetPopulatorTest {

	 // SUT - System Under Test
    SavingsProductSheetPopulator populator;

    @Mock
	RestClient restClient;
    
    @Test
    public void shouldDownloadAndParseSavingsProducts() {
    	
        Mockito.when(restClient.get("savingsproducts")).thenReturn("[{\"id\": 2,\"name\": \"SP2\",\"description\": \"SP2\",\"currency\": {\"code\": \"USD\",\"name\": \"US Dollar\",\"decimalPlaces\": 2," +
      "\"inMultiplesOf\": 5,\"displaySymbol\": \"$\",\"nameCode\": \"currency.USD\",\"displayLabel\": \"US Dollar ($)\"},\"nominalAnnualInterestRate\": 10.000000,\"interestCompoundingPeriodType\": {" +
      "\"id\": 1,\"code\": \"savings.interest.period.savingsCompoundingInterestPeriodType.daily\",\"value\": \"Daily\"},\"interestPostingPeriodType\": {\"id\": 4,\"code\": \"savings.interest.posting.period.savingsPostingInterestPeriodType.monthly\"," +
      "\"value\": \"Monthly\"},\"interestCalculationType\": {\"id\": 1,\"code\": \"savingsInterestCalculationType.dailybalance\",\"value\": \"Daily Balance\"},\"interestCalculationDaysInYearType\": {" +
      "\"id\": 365,\"code\": \"savingsInterestCalculationDaysInYearType.days365\",\"value\": \"365 Days\"},\"minRequiredOpeningBalance\": 870.000000,\"lockinPeriodFrequency\": 1,\"lockinPeriodFrequencyType\": {" +
      "\"id\": 0,\"code\": \"savings.lockin.savingsPeriodFrequencyType.days\",\"value\": \"Days\"},\"withdrawalFeeAmount\": 1.000000,\"withdrawalFeeType\": {\"id\": 1,\"code\": \"savingsWithdrawalFeesType.flat\"," +
      "\"value\": \"Flat\"},\"annualFeeAmount\": 3.000000,\"annualFeeOnMonthDay\": [9,1],\"accountingRule\": {\"id\": 1,\"code\": \"accountingRuleType.none\",\"value\": \"NONE\"}}]");

    	populator = new SavingsProductSheetPopulator(restClient);
    	Result result = populator.downloadAndParse();
    	
    	Assert.assertTrue(result.isSuccess());
    	Mockito.verify(restClient, Mockito.atLeastOnce()).get("savingsproducts");
    	List<SavingsProduct> products = populator.getProducts();
    	Assert.assertEquals(1, products.size());
    	SavingsProduct product = products.get(0);
    	Assert.assertEquals("2", product.getId().toString());
    	Assert.assertEquals("SP2", product.getName());
    	Assert.assertEquals("USD", product.getCurrency().getCode());
    	Assert.assertEquals("2", product.getCurrency().getDecimalPlaces().toString());
    	Assert.assertEquals("5", product.getCurrency().getInMultiplesOf().toString());
    	Assert.assertEquals("10.0", product.getNominalAnnualInterestRate().toString());
    	Assert.assertEquals("Daily", product.getInterestCompoundingPeriodType().getValue());
    	Assert.assertEquals("Monthly", product.getInterestPostingPeriodType().getValue());
    	Assert.assertEquals("Daily Balance", product.getInterestCalculationType().getValue());
    	Assert.assertEquals("365 Days", product.getInterestCalculationDaysInYearType().getValue());
    	Assert.assertEquals("870.0", product.getMinRequiredOpeningBalance().toString());
    	Assert.assertEquals("1", product.getLockinPeriodFrequency().toString());
    	Assert.assertEquals("Days", product.getLockinPeriodFrequencyType().getValue());
    	Assert.assertEquals("1.0", product.getWithdrawalFeeAmount().toString());
    	Assert.assertEquals("Flat", product.getWithdrawalFeeType().getValue());
    	Assert.assertEquals("3.0", product.getAnnualFeeAmount().toString());
    	Assert.assertEquals("9", product.getAnnualFeeOnMonthDay().get(0).toString());
    	Assert.assertEquals("1", product.getAnnualFeeOnMonthDay().get(1).toString());
    }
    
    @Test
    public void shouldPopulateSavingsProductSheet() {
    	
    	Mockito.when(restClient.get("savingsproducts")).thenReturn("[{\"id\": 2,\"name\": \"SP2\",\"description\": \"SP2\",\"currency\": {\"code\": \"USD\",\"name\": \"US Dollar\",\"decimalPlaces\": 2," +
    		      "\"inMultiplesOf\": 5,\"displaySymbol\": \"$\",\"nameCode\": \"currency.USD\",\"displayLabel\": \"US Dollar ($)\"},\"nominalAnnualInterestRate\": 10.000000,\"interestCompoundingPeriodType\": {" +
    		      "\"id\": 1,\"code\": \"savings.interest.period.savingsCompoundingInterestPeriodType.daily\",\"value\": \"Daily\"},\"interestPostingPeriodType\": {\"id\": 4,\"code\": \"savings.interest.posting.period.savingsPostingInterestPeriodType.monthly\"," +
    		      "\"value\": \"Monthly\"},\"interestCalculationType\": {\"id\": 1,\"code\": \"savingsInterestCalculationType.dailybalance\",\"value\": \"Daily Balance\"},\"interestCalculationDaysInYearType\": {" +
    		      "\"id\": 365,\"code\": \"savingsInterestCalculationDaysInYearType.days365\",\"value\": \"365 Days\"},\"minRequiredOpeningBalance\": 870.000000,\"lockinPeriodFrequency\": 1,\"lockinPeriodFrequencyType\": {" +
    		      "\"id\": 0,\"code\": \"savings.lockin.savingsPeriodFrequencyType.days\",\"value\": \"Days\"},\"withdrawalFeeAmount\": 1.000000,\"withdrawalFeeType\": {\"id\": 1,\"code\": \"savingsWithdrawalFeesType.flat\"," +
    		      "\"value\": \"Flat\"},\"annualFeeAmount\": 3.000000,\"annualFeeOnMonthDay\": [9,1],\"accountingRule\": {\"id\": 1,\"code\": \"accountingRuleType.none\",\"value\": \"NONE\"}}]");

    	populator = new SavingsProductSheetPopulator(restClient);
    	Result result = populator.downloadAndParse();
    	Workbook book = new HSSFWorkbook();
    	result = populator.populate(book);
    	
    	Assert.assertTrue(result.isSuccess());
    	Mockito.verify(restClient, Mockito.atLeastOnce()).get("savingsproducts");
    	
    	Sheet officeSheet = book.getSheet("Products");
    	Row row = officeSheet.getRow(1);
    	Assert.assertEquals("2.0", "" + row.getCell(0).getNumericCellValue());
    	Assert.assertEquals("SP2", row.getCell(1).getStringCellValue());
    	Assert.assertEquals("10.0", "" + row.getCell(2).getNumericCellValue());
    	Assert.assertEquals("Daily", row.getCell(3).getStringCellValue());
    	Assert.assertEquals("Monthly", row.getCell(4).getStringCellValue());
    	Assert.assertEquals("Daily Balance", row.getCell(5).getStringCellValue());
    	Assert.assertEquals("365 Days", row.getCell(6).getStringCellValue());
    	Assert.assertEquals("870.0", "" + row.getCell(7).getNumericCellValue());
    	Assert.assertEquals("1.0", "" + row.getCell(8).getNumericCellValue());
    	Assert.assertEquals("Days", row.getCell(9).getStringCellValue());
    	Assert.assertEquals("1.0", "" + row.getCell(10).getNumericCellValue());
    	Assert.assertEquals("Flat", row.getCell(11).getStringCellValue());
    	Assert.assertEquals("3.0", "" + row.getCell(12).getNumericCellValue());
    	Assert.assertEquals("USD", row.getCell(14).getStringCellValue());
    	Assert.assertEquals("2.0", "" + row.getCell(15).getNumericCellValue());
    	Assert.assertEquals("5.0", "" + row.getCell(16).getNumericCellValue());
    	DateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy");
    	Assert.assertEquals("01 September 2010" , dateFormat.format(row.getCell(13).getDateCellValue()));
    }
}
