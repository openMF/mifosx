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
import org.openmf.mifos.dataimport.dto.GeneralClient;
import org.openmf.mifos.dataimport.handler.Result;
import org.openmf.mifos.dataimport.http.RestClient;
import org.openmf.mifos.dataimport.populator.ClientSheetPopulator;

@RunWith(MockitoJUnitRunner.class)
public class ClientSheetPopulatorTest {

    ClientSheetPopulator populator;

    @Mock
	RestClient restClient;
    
    @Test
    public void shouldDownloadAndParseClients() {
    	
    	 Mockito.when(restClient.get("clients")).thenReturn("{\"totalFilteredRecords\": 2,\"pageItems\": [{\"id\": 1,\"accountNo\": \"000000001\"," +
    	 		"\"status\": {\"id\": 300,\"code\": \"clientStatusType.active\",\"value\": \"Active\"},\"active\": true,\"activationDate\": [2013,7,1]," +
    	 		"\"firstname\": \"Arsene\",\"middlename\": \"K\",\"lastname\": \"Wenger\",\"displayName\": \"Arsene K Wenger\",\"officeId\": 1," +
    	 		"\"officeName\": \"Head Office\",\"staffId\": 1,\"staffName\": \"Chatta, Sahil\"},{\"id\": 2,\"accountNo\": \"000000002\"," +
    	 		"\"status\": {\"id\": 300,\"code\": \"clientStatusType.active\",\"value\": \"Active\"},\"active\": true,\"activationDate\": [2013,7,1]," +
    	 		"\"firstname\": \"Billy\",\"middlename\": \"T\",\"lastname\": \"Bob\",\"displayName\": \"Billy T Bob\",\"officeId\": 2,\"officeName\": \"Office1\"," +
    	 		"\"staffId\": 2,\"staffName\": \"Dzeko, Edin\"}]}");
        Mockito.when(restClient.get("offices")).thenReturn("[{\"id\":1,\"name\":\"Head Office\",\"nameDecorated\":\"Head Office\",\"externalId\": \"1\"," +
        		"\"openingDate\":[2009,1,1],\"hierarchy\": \".\"},{\"id\": 2,\"name\": \"Office1\",\"nameDecorated\": \"....Office1\",\"openingDate\":[2013,4,1]," +
        		"\"hierarchy\": \".2.\",\"parentId\": 1,\"parentName\": \"Head Office\"}]");

    	populator = new ClientSheetPopulator(restClient);
    	Result result = populator.downloadAndParse();
    	
    	Assert.assertTrue(result.isSuccess());
    	Mockito.verify(restClient, Mockito.atLeastOnce()).get("clients");
    	Mockito.verify(restClient, Mockito.atLeastOnce()).get("offices");
    	String[] officeNames = populator.getOfficeNames();
    	List<GeneralClient> clients = populator.getClients();
    	GeneralClient client = clients.get(1);
    	Assert.assertEquals(2, officeNames.length);
    	Assert.assertEquals(2, clients.size());
    	
    	Assert.assertEquals("Office1", officeNames[1]);
    	
    	Assert.assertEquals("2", client.getId().toString());
    	Assert.assertEquals("Billy T Bob", client.getDisplayName());
    	Assert.assertEquals("Office1", client.getOfficeName());
    	Assert.assertEquals("2013", client.getActivationDate().get(0).toString());
    	Assert.assertEquals("7", client.getActivationDate().get(1).toString());
    	Assert.assertEquals("1", client.getActivationDate().get(2).toString());
    }
    
    @Test
    public void shouldPopulateClientSheet() {
    	
    	 Mockito.when(restClient.get("clients")).thenReturn("{\"totalFilteredRecords\": 2,\"pageItems\": [{\"id\": 1,\"accountNo\": \"000000001\"," +
     	 		"\"status\": {\"id\": 300,\"code\": \"clientStatusType.active\",\"value\": \"Active\"},\"active\": true,\"activationDate\": [2013,7,1]," +
     	 		"\"firstname\": \"Arsene\",\"middlename\": \"K\",\"lastname\": \"Wenger\",\"displayName\": \"Arsene K Wenger\",\"officeId\": 1," +
     	 		"\"officeName\": \"Head Office\",\"staffId\": 1,\"staffName\": \"Chatta, Sahil\"},{\"id\": 2,\"accountNo\": \"000000002\"," +
     	 		"\"status\": {\"id\": 300,\"code\": \"clientStatusType.active\",\"value\": \"Active\"},\"active\": true,\"activationDate\": [2013,7,1]," +
     	 		"\"firstname\": \"Billy\",\"middlename\": \"T\",\"lastname\": \"Bob\",\"displayName\": \"Billy T Bob\",\"officeId\": 2,\"officeName\": \"Office1\"," +
     	 		"\"staffId\": 2,\"staffName\": \"Dzeko, Edin\"}]}");
         Mockito.when(restClient.get("offices")).thenReturn("[{\"id\":1,\"name\":\"Head Office\",\"nameDecorated\":\"Head Office\",\"externalId\": \"1\"," +
         		"\"openingDate\":[2009,1,1],\"hierarchy\": \".\"},{\"id\": 2,\"name\": \"Office1\",\"nameDecorated\": \"....Office1\",\"openingDate\":[2013,4,1]," +
         		"\"hierarchy\": \".2.\",\"parentId\": 1,\"parentName\": \"Head Office\"}]");

        populator = new ClientSheetPopulator(restClient);
     	Result result = populator.downloadAndParse();
    	Workbook book = new HSSFWorkbook();
    	result = populator.populate(book);
    	Map<Integer, Integer> lastColumnLetters = populator.getLastColumnLetters();
    	Map<String, ArrayList<String>> officeToClients = populator.getOfficeToClients();
    	Map<String, Integer> clientNameToClientId = populator.getClientNameToClientId();
    	
    	Assert.assertTrue(result.isSuccess());
    	Mockito.verify(restClient, Mockito.atLeastOnce()).get("clients");
    	Mockito.verify(restClient, Mockito.atLeastOnce()).get("offices");
    	
    	Sheet clientSheet = book.getSheet("Clients");
    	Row row = clientSheet.getRow(3);
    	Assert.assertEquals("Office1", row.getCell(0).getStringCellValue());
    	Assert.assertEquals("Billy T Bob", row.getCell(1).getStringCellValue());
    	row = clientSheet.getRow(4);
    	Assert.assertEquals("2.0", "" + row.getCell(1).getNumericCellValue());
    	
    	Assert.assertEquals(2, lastColumnLetters.size());
    	Assert.assertEquals("1", "" + lastColumnLetters.get(1));
    	Assert.assertEquals("2", "" + officeToClients.size());
    	Assert.assertEquals("1", "" + officeToClients.get("Head_Office").size());
    	Assert.assertEquals("1", "" + officeToClients.get("Office1").size());
    	Assert.assertEquals("Billy T Bob", "" + officeToClients.get("Office1").get(0));
    	Assert.assertEquals("2", "" + clientNameToClientId.size());
    	Assert.assertEquals("2", "" + clientNameToClientId.get("Billy T Bob"));
    }
}
