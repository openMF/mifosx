package org.openmf.mifos.dataimport.populator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.openmf.mifos.dataimport.dto.Office;
import org.openmf.mifos.dataimport.dto.Personnel;
import org.openmf.mifos.dataimport.handler.Result;
import org.openmf.mifos.dataimport.http.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class PersonnelSheetPopulator extends AbstractWorkbookPopulator {

    private static final Logger logger = LoggerFactory.getLogger(PersonnelSheetPopulator.class);
	
	private final RestClient client;
	private final Boolean onlyLoanOfficers;
	
	private String content;
	
	private List<Personnel> personnel;
	private List<Office> offices;
	
	//Maintaining the one to many relationship
	private Map<String, ArrayList<String>> officeToPersonnel;
	private Map<String, Integer> staffNameToStaffId;
	
	private Map<Integer, Integer> lastColumnLetters;
	private Map<Integer,String> officeIdToOfficeName;
	
	private static final int OFFICE_NAME_COL = 0;
	private static final int STAFF_LIST_START_COL = 1;
	private static final int NOTICE_COL = 2;
	
	public PersonnelSheetPopulator(Boolean onlyLoanOfficers, RestClient client) {
		this.onlyLoanOfficers = onlyLoanOfficers;
        this.client = client;
    }
	
	 @Override
	    public Result downloadAndParse() {
	        Result result = new Result();
	        try {
	        	client.createAuthToken();
	        	personnel = new ArrayList<Personnel>();
	            content = client.get("staff");
	            Gson gson = new Gson();
	            JsonElement json = new JsonParser().parse(content);
	            JsonArray array = json.getAsJsonArray();
	            Iterator<JsonElement> iterator = array.iterator();
	            staffNameToStaffId = new HashMap<String, Integer>();
	            while(iterator.hasNext()) {
	            	json = iterator.next();
	            	Personnel person = gson.fromJson(json, Personnel.class);
	            	if(!onlyLoanOfficers)
	            	    personnel.add(person);
	            	else{
	            	   if(person.isLoanOfficer())
	            		   personnel.add(person);
	            	}
	            	staffNameToStaffId.put(person.getFirstName() + " " +person.getLastName(), person.getId());
	            }
	            offices = new ArrayList<Office>();
	            content = client.get("offices");
	            json = new JsonParser().parse(content);
	            array = json.getAsJsonArray();
	            iterator = array.iterator();
	            officeIdToOfficeName = new HashMap<Integer,String>();
	            while(iterator.hasNext()) {
	            	json = iterator.next();
	            	Office office = gson.fromJson(json, Office.class);
	            	officeIdToOfficeName.put(office.getId(), office.getName());
	            	offices.add(office);
	            }
	        } catch (RuntimeException re) {
	            result.addError(re.getMessage());
	            logger.error(re.getMessage());
	        }
	        return result;
	    }

	    @Override
	    public Result populate(Workbook workbook) {
	    	Result result = new Result();
	    	try{
	        int rowIndex = 1, officeIndex = 0;
	        Sheet staffSheet = workbook.createSheet("Staff");
	        
	        setLayout(staffSheet);
	        
	        setOfficeToPersonnelMap();
	        
	        lastColumnLetters = new HashMap<Integer, Integer>();
	        for(Office office : offices) {
	        	Row row = staffSheet.createRow(rowIndex);
	        	writeString(OFFICE_NAME_COL, row, office.getName().replaceAll("[ )(]", "_"));
	        	
	        	Integer colIndex = 0;
	        	ArrayList<String> fullStaffList = getStaffList(office.getHierarchy());
	        	ArrayList<Integer> staffIdList = new ArrayList<Integer> ();
	        	if(!fullStaffList.isEmpty())
	        		for(String staffName : fullStaffList) {
	        			staffIdList.add(staffNameToStaffId.get(staffName));
	        			colIndex++;
	        		    writeString(colIndex, row, staffName);
	        		}
	        	row = staffSheet.createRow(++rowIndex);	
	        	colIndex=0;
	        	    for(Integer staffId : staffIdList) {
	        	    	writeInt(++colIndex, row, staffId);
	        	    }
	        	lastColumnLetters.put(officeIndex++, colIndex);
	        	rowIndex++;
	        }
	        staffSheet.protectSheet("");
	    	} catch (RuntimeException re) {
	    		result.addError(re.getMessage());
	    		logger.error(re.getMessage());
	    	}
	        return result;
	    }
	    
	    private void setOfficeToPersonnelMap() {
	    	officeToPersonnel = new HashMap<String, ArrayList<String>>();
	    	for(Personnel person : personnel) {
	    		add(person.getOfficeName(), person.getFirstName() + " " + person.getLastName());
	    	}
	    }
	    
	    //Guava Multi-map can reduce this.
	    private void add(String key, String value) {
	        ArrayList<String> values = officeToPersonnel.get(key);
	        if (values == null) {
	            values = new ArrayList<String>();
	        }
	        values.add(value);
	        officeToPersonnel.put(key, values);
	    }
	    
	    private ArrayList<String> getStaffList(String hierarchy) {
	    	ArrayList<String> fullStaffList = new ArrayList<String>();
	    	Integer hierarchyLength = hierarchy.length();
			String[] officeIds = hierarchy.substring(1, hierarchyLength).split("\\.");
			if(officeToPersonnel.containsKey("Head Office"))
			    fullStaffList.addAll(officeToPersonnel.get("Head Office"));
			if(officeIds[0].isEmpty())
				return fullStaffList;
			for(int i=0; i<officeIds.length; i++) {
				String officeName = getOfficeNameFromOfficeId(Integer.parseInt(officeIds[i]));
				if(officeToPersonnel.containsKey(officeName))
	    	        fullStaffList.addAll(officeToPersonnel.get(officeName));
			}
	    	return fullStaffList;
	    }
	    
	    private String getOfficeNameFromOfficeId(Integer officeId) {
	    	return officeIdToOfficeName.get(officeId);
	    }
	    
	    
	    private void setLayout(Sheet worksheet) {
	    	for(Integer i=0; i<100; i++)
	    		worksheet.setColumnWidth(i, 6000);
	        Row rowHeader = worksheet.createRow(0);
	        rowHeader.setHeight((short)500);
	        writeString(OFFICE_NAME_COL, rowHeader, "Office Name");
	        writeString(STAFF_LIST_START_COL, rowHeader, "Staff List");
	        writeString(NOTICE_COL, rowHeader, "Every alternating Row consists of corresponding Staff IDs.");
	    }
	    
	    public List<Personnel> getPersonnel() {
	        return personnel;
	    }
	    
	    public List<Office> getOffices() {
	    	return offices;
	    }
	    
	    public Map<String, ArrayList<String>> getOfficeToPersonnel() {
	    	return officeToPersonnel;
	    }
	    
	    public Map<Integer, Integer> getLastColumnLetters() {
	    	return lastColumnLetters;
	    }
	    
	    public Map<Integer,String> getOfficeIdToOfficeName() {
	    	return officeIdToOfficeName;
	    }
	    
	    public Map<String, Integer> getStaffNameToStaffId() {
	    	return staffNameToStaffId;
	    }
}
