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
import org.openmf.mifos.dataimport.dto.LoanProduct;
import org.openmf.mifos.dataimport.handler.Result;
import org.openmf.mifos.dataimport.http.RestClient;
import org.openmf.mifos.dataimport.populator.loan.LoanProductSheetPopulator;

@RunWith(MockitoJUnitRunner.class)
public class LoanProductSheetPopulatorTest {

	 // SUT - System Under Test
    LoanProductSheetPopulator populator;

    @Mock
	RestClient restClient;
    
    @Test
    public void shouldDownloadAndParseLoanProducts() {
    	
        Mockito.when(restClient.get("loanproducts")).thenReturn("[{\"id\": 1,\"name\": \"HM\",\"description\": \"HM\",\"fundId\": 1,\"fundName\": \"Fund1\",\"includeInBorrowerCycle\": true," +
    "\"startDate\":[2012,4,1],\"closeDate\":[2014,6,1],\"status\": \"loanProduct.active\",\"currency\":{\"code\": \"USD\",\"name\": \"US Dollar\",\"decimalPlaces\": 2,\"inMultiplesOf\": 5," +
    "\"displaySymbol\": \"$\",\"nameCode\": \"currency.USD\",\"displayLabel\": \"US Dollar ($)\"},\"principal\": 20000.000000,\"minPrincipal\": 10000.000000,\"maxPrincipal\": 30000.000000," +
    "\"numberOfRepayments\": 12,\"minNumberOfRepayments\": 5,\"maxNumberOfRepayments\": 24,\"repaymentEvery\": 1,\"repaymentFrequencyType\":{\"id\":2,\"code\": \"repaymentFrequency.periodFrequencyType.months\"," +
    "\"value\":\"Months\"},\"interestRatePerPeriod\":7.000000,\"minInterestRatePerPeriod\":5.000000,\"maxInterestRatePerPeriod\": 9.000000,\"interestRateFrequencyType\":{\"id\": 3," +
    "\"code\": \"interestRateFrequency.periodFrequencyType.years\",\"value\": \"Per year\"},\"annualInterestRate\": 7.000000,\"amortizationType\":{\"id\": 1,\"code\": \"amortizationType.equal.installments\"," +
    "\"value\": \"Equal installments\"},\"interestType\":{\"id\": 0,\"code\":\"interestType.declining.balance\",\"value\": \"Declining Balance\"},\"interestCalculationPeriodType\": {\"id\": 1," +
    "\"code\": \"interestCalculationPeriodType.same.as.repayment.period\",\"value\": \"Same as repayment period\"},\"inArrearsTolerance\":3.000000,\"transactionProcessingStrategyId\": 4," +
    "\"transactionProcessingStrategyName\": \"RBI (India)\",\"graceOnPrincipalPayment\": 1,\"graceOnInterestPayment\": 2,\"graceOnInterestCharged\": 1,\"accountingRule\": {\"id\": 1," +
    "\"code\": \"accountingRuleType.none\",\"value\": \"NONE\"}}]");

    	populator = new LoanProductSheetPopulator(restClient);
    	Result result = populator.downloadAndParse();
    	
    	Assert.assertTrue(result.isSuccess());
    	Mockito.verify(restClient, Mockito.atLeastOnce()).get("loanproducts");
    	List<LoanProduct> products = populator.getProducts();
    	Assert.assertEquals(1, products.size());
    	LoanProduct product = products.get(0);
    	Assert.assertEquals("1", product.getId().toString());
    	Assert.assertEquals("HM", product.getName());
    	Assert.assertEquals("Fund1", product.getFundName());
    	Assert.assertEquals("20000", product.getPrincipal().toString());
    	Assert.assertEquals("10000", product.getMinPrincipal().toString());
    	Assert.assertEquals("30000", product.getMaxPrincipal().toString());
    	Assert.assertEquals("12", product.getNumberOfRepayments().toString());
    	Assert.assertEquals("5", product.getMinNumberOfRepayments().toString());
    	Assert.assertEquals("24", product.getMaxNumberOfRepayments().toString());
    	Assert.assertEquals("1", product.getRepaymentEvery().toString());
    	Assert.assertEquals("Months", product.getRepaymentFrequencyType().getValue());
    	Assert.assertEquals("7", product.getInterestRatePerPeriod().toString());
    	Assert.assertEquals("5", product.getMinInterestRatePerPeriod().toString());
    	Assert.assertEquals("9", product.getMaxInterestRatePerPeriod().toString());
    	Assert.assertEquals("Per year", product.getInterestRateFrequencyType().getValue());
    	Assert.assertEquals("Equal installments", product.getAmortizationType().getValue());
    	Assert.assertEquals("Declining Balance", product.getInterestType().getValue());
    	Assert.assertEquals("Same as repayment period", product.getInterestCalculationPeriodType().getValue());
    	Assert.assertEquals("3", product.getInArrearsTolerance().toString());
    	Assert.assertEquals("RBI (India)", product.getTransactionProcessingStrategyName());
    	Assert.assertEquals("1", product.getGraceOnPrincipalPayment().toString());
    	Assert.assertEquals("2", product.getGraceOnInterestPayment().toString());
    	Assert.assertEquals("1", product.getGraceOnInterestCharged().toString());
    	Assert.assertEquals("loanProduct.active", product.getStatus());
    	Assert.assertEquals("2012", product.getStartDate().get(0).toString());
    	Assert.assertEquals("4", product.getStartDate().get(1).toString());
    	Assert.assertEquals("1", product.getStartDate().get(2).toString());
    	Assert.assertEquals("2014", product.getCloseDate().get(0).toString());
    	Assert.assertEquals("6", product.getCloseDate().get(1).toString());
    	Assert.assertEquals("1", product.getCloseDate().get(2).toString());
    }
    
    @Test
    public void shouldPopulateLoanProductSheet() {
    	
    	Mockito.when(restClient.get("loanproducts")).thenReturn("[{\"id\": 1,\"name\": \"HM\",\"description\": \"HM\",\"fundId\": 1,\"fundName\": \"Fund1\",\"includeInBorrowerCycle\": true," +
    		    "\"startDate\":[2012,4,1],\"closeDate\":[2014,6,1],\"status\": \"loanProduct.active\",\"currency\":{\"code\": \"USD\",\"name\": \"US Dollar\",\"decimalPlaces\": 2,\"inMultiplesOf\": 5," +
    		    "\"displaySymbol\": \"$\",\"nameCode\": \"currency.USD\",\"displayLabel\": \"US Dollar ($)\"},\"principal\": 20000.000000,\"minPrincipal\": 10000.000000,\"maxPrincipal\": 30000.000000," +
    		    "\"numberOfRepayments\": 12,\"minNumberOfRepayments\": 5,\"maxNumberOfRepayments\": 24,\"repaymentEvery\": 1,\"repaymentFrequencyType\":{\"id\":2,\"code\": \"repaymentFrequency.periodFrequencyType.months\"," +
    		    "\"value\":\"Months\"},\"interestRatePerPeriod\":7.000000,\"minInterestRatePerPeriod\":5.000000,\"maxInterestRatePerPeriod\": 9.000000,\"interestRateFrequencyType\":{\"id\": 3," +
    		    "\"code\": \"interestRateFrequency.periodFrequencyType.years\",\"value\": \"Per year\"},\"annualInterestRate\": 7.000000,\"amortizationType\":{\"id\": 1,\"code\": \"amortizationType.equal.installments\"," +
    		    "\"value\": \"Equal installments\"},\"interestType\":{\"id\": 0,\"code\":\"interestType.declining.balance\",\"value\": \"Declining Balance\"},\"interestCalculationPeriodType\": {\"id\": 1," +
    		    "\"code\": \"interestCalculationPeriodType.same.as.repayment.period\",\"value\": \"Same as repayment period\"},\"inArrearsTolerance\":3.000000,\"transactionProcessingStrategyId\": 4," +
    		    "\"transactionProcessingStrategyName\": \"RBI (India)\",\"graceOnPrincipalPayment\": 1,\"graceOnInterestPayment\": 2,\"graceOnInterestCharged\": 1,\"accountingRule\": {\"id\": 1," +
    		    "\"code\": \"accountingRuleType.none\",\"value\": \"NONE\"}}]");

    	populator = new LoanProductSheetPopulator(restClient);
    	Result result = populator.downloadAndParse();
    	Workbook book = new HSSFWorkbook();
    	result = populator.populate(book);
    	
    	Assert.assertTrue(result.isSuccess());
    	Mockito.verify(restClient, Mockito.atLeastOnce()).get("loanproducts");
    	
    	Sheet officeSheet = book.getSheet("Products");
    	Row row = officeSheet.getRow(1);
    	Assert.assertEquals("1.0", "" + row.getCell(0).getNumericCellValue());
    	Assert.assertEquals("HM", row.getCell(1).getStringCellValue());
    	Assert.assertEquals("Fund1", row.getCell(2).getStringCellValue());
    	Assert.assertEquals("20000.0", "" + row.getCell(3).getNumericCellValue());
    	Assert.assertEquals("10000.0", "" + row.getCell(4).getNumericCellValue());
    	Assert.assertEquals("30000.0", "" + row.getCell(5).getNumericCellValue());
    	Assert.assertEquals("12.0", "" + row.getCell(6).getNumericCellValue());
    	Assert.assertEquals("5.0", "" + row.getCell(7).getNumericCellValue());
    	Assert.assertEquals("24.0", "" + row.getCell(8).getNumericCellValue());
    	Assert.assertEquals("1.0", "" + row.getCell(9).getNumericCellValue());
    	Assert.assertEquals("Months", row.getCell(10).getStringCellValue());
    	Assert.assertEquals("7.0", "" + row.getCell(11).getNumericCellValue());
    	Assert.assertEquals("5.0", "" + row.getCell(12).getNumericCellValue());
    	Assert.assertEquals("9.0", "" + row.getCell(13).getNumericCellValue());
    	Assert.assertEquals("Per year", row.getCell(14).getStringCellValue());
    	Assert.assertEquals("Equal installments", row.getCell(15).getStringCellValue());
    	Assert.assertEquals("Declining Balance", row.getCell(16).getStringCellValue());
    	Assert.assertEquals("Same as repayment period", row.getCell(17).getStringCellValue());
    	Assert.assertEquals("3.0", "" + row.getCell(18).getNumericCellValue());
    	Assert.assertEquals("RBI (India)", row.getCell(19).getStringCellValue());
    	Assert.assertEquals("1.0", "" + row.getCell(20).getNumericCellValue());
    	Assert.assertEquals("2.0", "" + row.getCell(21).getNumericCellValue());
    	Assert.assertEquals("1.0", "" + row.getCell(22).getNumericCellValue());
    	
    	DateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy");
    	Assert.assertEquals("01 April 2012", dateFormat.format(row.getCell(23).getDateCellValue()));
    	Assert.assertEquals("01 June 2014" , dateFormat.format(row.getCell(24).getDateCellValue()));
    }
}
