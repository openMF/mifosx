package org.openmf.mifos.dataimport.populator.client;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.openmf.mifos.dataimport.dto.Office;
import org.openmf.mifos.dataimport.handler.Result;
import org.openmf.mifos.dataimport.http.RestClient;
import org.openmf.mifos.dataimport.populator.AbstractWorkbookPopulator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class OfficeSheetPopulator extends AbstractWorkbookPopulator {
	
	private static final Logger logger = LoggerFactory.getLogger(OfficeSheetPopulator.class);
	
	private final RestClient client;
	
	private String content;
	
	private List<Office> offices;
	
	private static final int ID_COL = 0;
	private static final int OFFICE_NAME_COL = 1;
	private static final int EXTERNAL_ID_COL = 2;
	private static final int OPENING_DATE_COL = 3;
    private static final int PARENT_NAME_COL = 4;
    private static final int HIERARCHY_COL = 5;

    public OfficeSheetPopulator(RestClient client) {
        this.client = client;
    }
    
    @Override
    public Result downloadAndParse() {
    	Result result = new Result();
        try {
        	client.createAuthToken();
        	offices = new ArrayList<Office>();
            content = client.get("offices");
            Gson gson = new Gson();
            JsonElement json = new JsonParser().parse(content);
            JsonArray array = json.getAsJsonArray();
            Iterator<JsonElement> iterator = array.iterator();
            while(iterator.hasNext()) {
            	json = iterator.next();
            	Office office = gson.fromJson(json, Office.class);
            	offices.add(office);
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
    	try{
        int rowIndex = 1;
        Sheet officeSheet = workbook.createSheet("Offices");
        setLayout(officeSheet);
        CellStyle dateCellStyle = workbook.createCellStyle();
        short df = workbook.createDataFormat().getFormat("dd/mm/yy");
        dateCellStyle.setDataFormat(df);
        for(Office office:offices) {
        	Row row = officeSheet.createRow(rowIndex);
        	writeInt(ID_COL, row, office.getId());
        	writeString(OFFICE_NAME_COL, row, office.getName().trim().replaceAll("[ )(]", "_"));
        	writeString(EXTERNAL_ID_COL, row, office.getExternalId());
        	writeDate(OPENING_DATE_COL, row, office.getOpeningDate().get(2) + "/" + office.getOpeningDate().get(1) + "/" + office.getOpeningDate().get(0), dateCellStyle);
        	writeString(PARENT_NAME_COL,row,office.getParentName());
        	writeString(HIERARCHY_COL, row, office.getHierarchy());
        	rowIndex++;
        }
        officeSheet.protectSheet("");
    	} catch (Exception e) {
    		result.addError(e.getMessage());
    		logger.error(e.getMessage());
    	}
        return result;
    }
    
    private void setLayout(Sheet worksheet) {
    	worksheet.setColumnWidth(ID_COL, 2000);
        worksheet.setColumnWidth(OFFICE_NAME_COL, 7000);
        worksheet.setColumnWidth(EXTERNAL_ID_COL, 7000);
        worksheet.setColumnWidth(OPENING_DATE_COL, 4000);
        worksheet.setColumnWidth(PARENT_NAME_COL, 7000);
        worksheet.setColumnWidth(HIERARCHY_COL, 6000);
        Row rowHeader = worksheet.createRow(0);
        rowHeader.setHeight((short)500);
        writeString(ID_COL, rowHeader, "ID");
        writeString(OFFICE_NAME_COL, rowHeader, "Name");
        writeString(EXTERNAL_ID_COL, rowHeader, "External ID");
        writeString(OPENING_DATE_COL, rowHeader, "Opening Date");
        writeString(PARENT_NAME_COL, rowHeader, "Parent Name");
        writeString(HIERARCHY_COL, rowHeader, "Hierarchy");
    }
    
    public List<Office> getOffices() {
        return offices;
    }
    
 

}
