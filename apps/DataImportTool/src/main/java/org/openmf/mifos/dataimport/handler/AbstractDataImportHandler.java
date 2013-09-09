package org.openmf.mifos.dataimport.handler;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Iterator;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public abstract class AbstractDataImportHandler implements DataImportHandler {

    protected Integer getNumberOfRows(Sheet sheet, int primaryColumn) {
        Integer noOfEntries = 1;
        // getLastRowNum and getPhysicalNumberOfRows showing false values
        // sometimes
           while (sheet.getRow(noOfEntries) !=null && sheet.getRow(noOfEntries).getCell(primaryColumn) != null) {
               noOfEntries++;
           }
        	
        return noOfEntries;
    }
    
    protected boolean isNotImported(Row row, int statusColumn) {
		return !readAsString(statusColumn, row).equals("Imported");
	}

    protected String readAsInt(int colIndex, Row row) {
        try {
        	Cell c = row.getCell(colIndex);
        	if (c == null || c.getCellType() == Cell.CELL_TYPE_BLANK)
        		return "";
        	return ((Double) c.getNumericCellValue()).intValue() + "";
        } catch (RuntimeException re) {
            return row.getCell(colIndex).getStringCellValue();
        }
    }
    
    protected Double readAsDouble(int colIndex, Row row) {
    	return row.getCell(colIndex).getNumericCellValue();
    }

    protected String readAsString(int colIndex, Row row) {
        try {
        	Cell c = row.getCell(colIndex);
        	if (c == null || c.getCellType() == Cell.CELL_TYPE_BLANK)
        		return "";
            return c.getStringCellValue();
        } catch (Exception e) {
            return ((Double)row.getCell(colIndex).getNumericCellValue()).intValue() + "";
        }
    }

    protected String readAsDate(int colIndex, Row row) {
    	try{
    		Cell c = row.getCell(colIndex);
    		if(c == null || c.getCellType() == Cell.CELL_TYPE_BLANK)
    			return "";
    		DateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy");
            return dateFormat.format(c.getDateCellValue());
    	}  catch  (Exception e) {
    		return "";
    	}
    }
    
    protected String readAsDateWithoutYear(int colIndex, Row row) {
    	try{
    		Cell c = row.getCell(colIndex);
    		if(c == null || c.getCellType() == Cell.CELL_TYPE_BLANK)
    			return "";
    		DateFormat dateFormat = new SimpleDateFormat("dd MMMM");
            return dateFormat.format(c.getDateCellValue());
    	}  catch  (Exception e) {
    		return "";
    	}
    }
    
    protected Boolean readAsBoolean(int colIndex, Row row) {
    	return row.getCell(colIndex).getBooleanCellValue();
    }
    
    protected void writeString(int colIndex, Row row, String value) {
        row.createCell(colIndex).setCellValue(value);
    }
    
    protected CellStyle getCellStyle(Workbook workbook, IndexedColors color) {
    	CellStyle style = workbook.createCellStyle();
        style.setFillForegroundColor(color.getIndex());
        style.setFillPattern(CellStyle.SOLID_FOREGROUND);
        return style;
    }
    
    protected String parseStatus(String errorMessage) {
    	StringBuffer message = new StringBuffer();
    	JsonObject obj = new JsonParser().parse(errorMessage.trim()).getAsJsonObject();
        JsonArray array = obj.getAsJsonArray("errors");
        Iterator<JsonElement> iterator = array.iterator();
        while(iterator.hasNext()) {
        	JsonObject json = iterator.next().getAsJsonObject();
        	String parameterName = json.get("parameterName").toString();
        	String defaultUserMessage = json.get("defaultUserMessage").toString();
        	message = message.append(parameterName.substring(1, parameterName.length() - 1) + ":" + defaultUserMessage.substring(1, defaultUserMessage.length() - 1) + "\t");
         }
    	 return message.toString();
    }
    
    public Integer getIdByName (Sheet sheet, String name) {
    	String sheetName = sheet.getSheetName();
    	if(sheetName.equals("Offices") || sheetName.equals("Clients") || sheetName.equals("Staff") || sheetName.equals("Extras")) {
    	for (Row row : sheet) {
            for (Cell cell : row) {
                if (cell.getCellType() == Cell.CELL_TYPE_STRING && cell.getRichStringCellValue().getString().trim().equals(name)) {
                    	if(sheet.getSheetName().equals("Offices"))
                            return ((Double)row.getCell(cell.getColumnIndex() - 1).getNumericCellValue()).intValue(); 
                    	else if(sheet.getSheetName().equals("Staff") || sheet.getSheetName().equals("Clients"))
                           return ((Double)sheet.getRow(row.getRowNum() + 1).getCell(cell.getColumnIndex()).getNumericCellValue()).intValue();
                    	else if(sheet.getSheetName().equals("Extras"))
                    		return ((Double)row.getCell(cell.getColumnIndex() - 1).getNumericCellValue()).intValue();
                    }
            }
          }
    	} else if (sheetName.equals("Products")) {
    		for(Row row : sheet) {
    			for(int i = 0; i < 2; i++) {
    				Cell cell = row.getCell(i);
    				if(cell.getCellType() == Cell.CELL_TYPE_STRING && cell.getRichStringCellValue().getString().trim().equals(name)) {
    						return ((Double)row.getCell(cell.getColumnIndex() - 1).getNumericCellValue()).intValue();
    				}
    			}
    		}
    	}
        return 0;
    }

}
