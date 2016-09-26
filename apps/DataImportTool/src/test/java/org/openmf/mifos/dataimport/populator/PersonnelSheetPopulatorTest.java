package org.openmf.mifos.dataimport.populator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
import org.openmf.mifos.dataimport.dto.Personnel;
import org.openmf.mifos.dataimport.handler.Result;
import org.openmf.mifos.dataimport.http.RestClient;
import org.openmf.mifos.dataimport.populator.PersonnelSheetPopulator;

@RunWith(MockitoJUnitRunner.class)
public class PersonnelSheetPopulatorTest {

	 // SUT - System Under Test
    PersonnelSheetPopulator populator;

    @Mock
	RestClient restClient;
    
    @Test
    public void shouldDownloadAndParseStaff() {
    	
        Mockito.when(restClient.get("staff")).thenReturn("[{\"id\": 1, \"firstname\": \"Sahil\", \"lastname\": \"Chatta\", \"displayName\": \"Chatta, Sahil\"," +
        		" \"officeId\": 1,\"officeName\": \"Head Office\", \"isLoanOfficer\": true },{\"id\": 2, \"firstname\": \"Edin\", \"lastname\": \"Dzeko\",\"displayName\":" +
        		" \"Dzeko, Edin\",\"officeId\": 2,\"officeName\": \"Office1\",\"isLoanOfficer\": true}]");
        Mockito.when(restClient.get("offices")).thenReturn("[{\"id\":1,\"name\":\"Head Office\",\"nameDecorated\":\"Head Office\",\"externalId\": \"1\"," +
        		"\"openingDate\":[2009,1,1],\"hierarchy\": \".\"},{\"id\": 2,\"name\": \"Office1\",\"nameDecorated\": \"....Office1\",\"openingDate\":[2013,4,1]," +
        		"\"hierarchy\": \".2.\",\"parentId\": 1,\"parentName\": \"Head Office\"}]");
        
    	populator = new PersonnelSheetPopulator(Boolean.FALSE, restClient);
    	Result result = populator.downloadAndParse();
    	
    	Assert.assertTrue(result.isSuccess());
    	Mockito.verify(restClient, Mockito.atLeastOnce()).get("staff");
    	Mockito.verify(restClient, Mockito.atLeastOnce()).get("offices");
    	List<Personnel> personnel = populator.getPersonnel();
    	List<Office> offices = populator.getOffices();
    	
    	Assert.assertEquals(2, personnel.size());
    	Assert.assertEquals(2, offices.size());
    	Personnel staff = personnel.get(1);
    	Office office = offices.get(1);
    	
    	Assert.assertEquals("2", office.getId().toString());
    	Assert.assertEquals("Office1", office.getName());
    	Assert.assertEquals("Head Office", office.getParentName());
    	Assert.assertEquals(".2.", office.getHierarchy());
    	Assert.assertEquals("2013", office.getOpeningDate().get(0).toString());
    	Assert.assertEquals("4", office.getOpeningDate().get(1).toString());
    	Assert.assertEquals("1", office.getOpeningDate().get(2).toString());
    	Assert.assertEquals(null, office.getExternalId());
    	
    	Assert.assertEquals("2", staff.getId().toString());
    	Assert.assertEquals("Edin", staff.getFirstName());
    	Assert.assertEquals("Dzeko", staff.getLastName());
    	Assert.assertEquals("2", staff.getOfficeId().toString());
    	Assert.assertEquals("Office1", staff.getOfficeName());
    }
    
    @Test
    public void shouldPopulateStaffSheet() {
    	
    	Mockito.when(restClient.get("staff")).thenReturn("[{\"id\": 1, \"firstname\": \"Sahil\", \"lastname\": \"Chatta\", \"displayName\": \"Chatta, Sahil\"," +
        		" \"officeId\": 1,\"officeName\": \"Head Office\", \"isLoanOfficer\": true },{\"id\": 2, \"firstname\": \"Edin\", \"lastname\": \"Dzeko\",\"displayName\":" +
        		" \"Dzeko, Edin\",\"officeId\": 2,\"officeName\": \"Office1\",\"isLoanOfficer\": true}]");
        Mockito.when(restClient.get("offices")).thenReturn("[{\"id\":1,\"name\":\"Head Office\",\"nameDecorated\":\"Head Office\",\"externalId\": \"1\"," +
        		"\"openingDate\":[2009,1,1],\"hierarchy\": \".\"},{\"id\": 2,\"name\": \"Office1\",\"nameDecorated\": \"....Office1\",\"openingDate\":[2013,4,1]," +
        		"\"hierarchy\": \".2.\",\"parentId\": 1,\"parentName\": \"Head Office\"}]");

        populator = new PersonnelSheetPopulator(Boolean.FALSE, restClient);
    	Result result = populator.downloadAndParse();
    	Workbook book = new HSSFWorkbook();
    	result = populator.populate(book);
    	
    	Map<String, ArrayList<String>> officeToPersonnel = populator.getOfficeToPersonnel();
    	Map<Integer, Integer> lastColumnLetters = populator.getLastColumnLetters();
    	Map<Integer,String> officeIdToOfficeName = populator.getOfficeIdToOfficeName();
    	Map<String, Integer> staffNameToStaffId = populator.getStaffNameToStaffId();
    	
    	Assert.assertTrue(result.isSuccess());
    	Mockito.verify(restClient, Mockito.atLeastOnce()).get("staff");
    	Mockito.verify(restClient, Mockito.atLeastOnce()).get("offices");
    	
    	Sheet staffSheet = book.getSheet("Staff");
    	Row row = staffSheet.getRow(3);
    	Assert.assertEquals("Office1", row.getCell(0).getStringCellValue());
    	Assert.assertEquals("Sahil Chatta", row.getCell(1).getStringCellValue());
    	Assert.assertEquals("Edin Dzeko", row.getCell(2).getStringCellValue());
    	row = staffSheet.getRow(4);
    	Assert.assertEquals("1.0", "" + row.getCell(1).getNumericCellValue());
    	Assert.assertEquals("2.0", "" + row.getCell(2).getNumericCellValue());
    	
    	Assert.assertEquals(1, officeToPersonnel.get("Office1").size());
    	Assert.assertEquals(1, officeToPersonnel.get("Head Office").size());
    	Assert.assertEquals("Edin Dzeko", officeToPersonnel.get("Office1").get(0));
    	Assert.assertEquals("2", "" + lastColumnLetters.get(1));
    	Assert.assertEquals(2, officeIdToOfficeName.size());
    	Assert.assertEquals(2, staffNameToStaffId.size());
    	Assert.assertEquals("Office1", officeIdToOfficeName.get(2));
    	Assert.assertEquals("2", staffNameToStaffId.get("Edin Dzeko").toString());
    }
}
