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
import org.openmf.mifos.dataimport.dto.Office;
import org.openmf.mifos.dataimport.handler.Result;
import org.openmf.mifos.dataimport.http.RestClient;
import org.openmf.mifos.dataimport.populator.client.OfficeSheetPopulator;

@RunWith(MockitoJUnitRunner.class)
public class OfficeSheetPopulatorTest {

	 // SUT - System Under Test
    OfficeSheetPopulator populator;

    @Mock
	RestClient restClient;
    
    @Test
    public void shouldDownloadAndParseOffices() {
    	
        Mockito.when(restClient.get("offices")).thenReturn("[{\"id\":1,\"name\":\"Head Office\",\"nameDecorated\":\"Head Office\",\"externalId\": \"1\"," +
        		"\"openingDate\":[2009,1,1],\"hierarchy\": \".\"},{\"id\": 2,\"name\": \"Office1\",\"nameDecorated\": \"....Office1\",\"openingDate\":[2013,4,1]," +
        		"\"hierarchy\": \".2.\",\"parentId\": 1,\"parentName\": \"Head Office\"}]");

    	populator = new OfficeSheetPopulator(restClient);
    	Result result = populator.downloadAndParse();
    	
    	Assert.assertTrue(result.isSuccess());
    	Mockito.verify(restClient, Mockito.atLeastOnce()).get("offices");
    	List<Office> offices = populator.getOffices();
    	Assert.assertEquals(2, offices.size());
    	Office office = offices.get(1);
    	Assert.assertEquals("2", office.getId().toString());
    	Assert.assertEquals("Office1", office.getName());
    	Assert.assertEquals("Head Office", office.getParentName());
    	Assert.assertEquals(".2.", office.getHierarchy());
    	Assert.assertEquals("2013", office.getOpeningDate().get(0).toString());
    	Assert.assertEquals("4", office.getOpeningDate().get(1).toString());
    	Assert.assertEquals("1", office.getOpeningDate().get(2).toString());
    	Assert.assertEquals(null, office.getExternalId());
    }
    
    @Test
    public void shouldPopulateOfficeSheet() {
    	
        Mockito.when(restClient.get("offices")).thenReturn("[{\"id\":1,\"name\":\"Head Office\",\"nameDecorated\":\"Head Office\",\"externalId\": \"1\"," +
        		"\"openingDate\":[2009,1,1],\"hierarchy\": \".\"},{\"id\": 2,\"name\": \"Office1\",\"nameDecorated\": \"....Office1\",\"openingDate\":[2013,4,1]," +
        		"\"hierarchy\": \".2.\",\"parentId\": 1,\"parentName\": \"Head Office\"}]");

    	populator = new OfficeSheetPopulator(restClient);
    	Result result = populator.downloadAndParse();
    	Workbook book = new HSSFWorkbook();
    	result = populator.populate(book);
    	
    	Assert.assertTrue(result.isSuccess());
    	Mockito.verify(restClient, Mockito.atLeastOnce()).get("offices");
    	
    	Sheet officeSheet = book.getSheet("Offices");
    	Row row = officeSheet.getRow(2);
    	Assert.assertEquals("2.0", "" + row.getCell(0).getNumericCellValue());
    	Assert.assertEquals("Office1", row.getCell(1).getStringCellValue());
    	Assert.assertEquals("Head Office", row.getCell(4).getStringCellValue());
    	Assert.assertEquals(".2.", row.getCell(5).getStringCellValue());
    	DateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy");
    	Assert.assertEquals("01 April 2013" , dateFormat.format(row.getCell(3).getDateCellValue()));
    }
}
