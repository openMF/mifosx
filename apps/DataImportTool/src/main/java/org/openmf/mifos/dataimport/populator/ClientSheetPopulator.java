package org.openmf.mifos.dataimport.populator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.openmf.mifos.dataimport.dto.GeneralClient;
import org.openmf.mifos.dataimport.handler.Result;
import org.openmf.mifos.dataimport.http.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class ClientSheetPopulator extends AbstractWorkbookPopulator {
	
	private static final Logger logger = LoggerFactory.getLogger(ClientSheetPopulator.class);
	
    private final RestClient restClient;

    private String content;
    
    private List<GeneralClient> clients;
    private ArrayList<String> officeNames;
    
    private Map<String, ArrayList<String>> officeToClients;
    private Map<Integer, Integer> lastColumnLetters;
    private Map<String, Integer> clientNameToClientId;
    
    private static final int OFFICE_NAME_COL = 0;
    private static final int CLIENT_NAME_COL = 1;
    private static final int NOTICE_COL = 2;
	
	public ClientSheetPopulator(RestClient restClient) {
    	this.restClient = restClient;
    }
	
	@Override
    public Result downloadAndParse() {
    	Result result = new Result();
    	try {
        	restClient.createAuthToken();
        	clients = new ArrayList<GeneralClient>();
            content = restClient.get("clients");
            Gson gson = new Gson();
            JsonParser parser = new JsonParser();
            JsonObject obj = parser.parse(content).getAsJsonObject();
            JsonArray array = obj.getAsJsonArray("pageItems");
            Iterator<JsonElement> iterator = array.iterator();
            clientNameToClientId = new HashMap<String, Integer>();
            while(iterator.hasNext()) {
            	JsonElement json = iterator.next();
            	GeneralClient client = gson.fromJson(json, GeneralClient.class);
            	if(client.isActive())
            	  clients.add(client);
            	clientNameToClientId.put(client.getDisplayName(), client.getId());
            }
            content = restClient.get("offices");
            JsonElement json2 = parser.parse(content);
            array = json2.getAsJsonArray();
            iterator = array.iterator();
            officeNames = new ArrayList<String>();
            while(iterator.hasNext()) {
            	String officeName = iterator.next().getAsJsonObject().get("name").toString();
            	officeName = officeName.substring(1, officeName.length()-1).replaceAll("[ )(]", "_");
             officeNames.add(officeName);
            }
        } catch (Exception e) {
            result.addError(e.getMessage());
            logger.error(e.getMessage());
        }
    	return result;
    }

    @Override
    public Result populate(Workbook workbook) {
    	Result result = new Result();
    	Sheet clientSheet = workbook.createSheet("Clients");
    	setLayout(clientSheet);
    	setOfficeToClientsMap();
    	CellStyle dateCellStyle = workbook.createCellStyle();
        short df = workbook.createDataFormat().getFormat("dd/mm/yy");
        dateCellStyle.setDataFormat(df);
    	int rowIndex = 1, officeIndex = 0, colIndex;
    	lastColumnLetters = new HashMap<Integer, Integer>();
    	Row row;
    	try{
    		for(String officeName : officeNames) {
    			colIndex = 0;
	        	row = clientSheet.createRow(rowIndex);
	        	writeString(OFFICE_NAME_COL, row, officeName);
	        	ArrayList<String> clientList = new ArrayList<String>();
	        	if(officeToClients.containsKey(officeName))
	        	   clientList = officeToClients.get(officeName);
	        	if(!clientList.isEmpty()) 
	        		for(String clientName : clientList) {
	        		    writeString(++colIndex, row, clientName);
	        		}
	        	row = clientSheet.createRow(++rowIndex);
	        	if(!clientList.isEmpty()) {
	        	  colIndex = 0;	
	        	  for(String clientName: clientList) 
	        	     writeInt(++colIndex, row, clientNameToClientId.get(clientName));
	        	}
	        	
	        	lastColumnLetters.put(officeIndex++, colIndex);
	        	rowIndex++;
    		}
    		clientSheet.protectSheet("");
    	} catch (Exception e) {
    		result.addError(e.getMessage());
    		logger.error(e.getMessage());
    	}
    	
        return result;
    }
    
    private void setOfficeToClientsMap() {
    	officeToClients = new HashMap<String, ArrayList<String>>();
    	for(GeneralClient person : clients) {
    		add(person.getOfficeName().replaceAll("[ )(]", "_"), person.getDisplayName());
    	}
    }
    
    //Guava Multi-map can reduce this.
    private void add(String key, String value) {
        ArrayList<String> values = officeToClients.get(key);
        if (values == null) {
            values = new ArrayList<String>();
        }
        values.add(value);
        officeToClients.put(key, values);
    }
    
    private void setLayout(Sheet worksheet) {
    	Row rowHeader = worksheet.createRow(0);
        rowHeader.setHeight((short)500);
        worksheet.setColumnWidth(OFFICE_NAME_COL, 6000);
        for(int colIndex = 1; colIndex<=10; colIndex++)
           worksheet.setColumnWidth(colIndex, 6000);
        writeString(OFFICE_NAME_COL, rowHeader, "Office Names");
        writeString(CLIENT_NAME_COL, rowHeader, "Client Names");
        writeString(NOTICE_COL, rowHeader, "Every alternating Row consists of corresponding Client IDs.");
    }
    
    public List<GeneralClient> getClients() {
        return clients;
    }
    

    public Integer getClientsSize() {
    	return clients.size();
    }
    
    public String[] getOfficeNames() {
        return officeNames.toArray(new String[officeNames.size()]);
    }
    
    public Map<Integer, Integer> getLastColumnLetters() {
    	return lastColumnLetters;
    }
    
    public Map<String, ArrayList<String>> getOfficeToClients() {
    	return officeToClients;
    }
    public Map<String, Integer> getClientNameToClientId() {
    	return clientNameToClientId;
    }
}
