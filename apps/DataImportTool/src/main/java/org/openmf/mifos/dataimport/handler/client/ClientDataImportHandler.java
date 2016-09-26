package org.openmf.mifos.dataimport.handler.client;

import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.openmf.mifos.dataimport.dto.Client;
import org.openmf.mifos.dataimport.dto.CorporateClient;
import org.openmf.mifos.dataimport.handler.AbstractDataImportHandler;
import org.openmf.mifos.dataimport.handler.Result;
import org.openmf.mifos.dataimport.http.RestClient;
import org.openmf.mifos.dataimport.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

public class ClientDataImportHandler extends AbstractDataImportHandler {
           
	private static final Logger logger = LoggerFactory.getLogger(ClientDataImportHandler.class);
	
	private static final int FIRST_NAME_COL = 0;
	private static final int FULL_NAME_COL = 0;
    private static final int LAST_NAME_COL = 1;
    private static final int MIDDLE_NAME_COL = 2;
    private static final int OFFICE_NAME_COL = 3;
    private static final int STAFF_NAME_COL = 4;
    private static final int EXTERNAL_ID_COL = 5;
    private static final int ACTIVATION_DATE_COL = 6;
    private static final int ACTIVE_COL = 7;
    private static final int STATUS_COL = 8;

    private List<Client> clients = new ArrayList<Client>();
    private String clientType;
    
    private final RestClient restClient;
    
    private final Workbook workbook;

    public ClientDataImportHandler(Workbook workbook, RestClient client) {
        this.workbook = workbook;
        this.restClient = client;
    }
    
    @Override
    public Result parse() {
        Result result = new Result();
        Sheet clientSheet = workbook.getSheet("Clients");
        Integer noOfEntries = getNumberOfRows(clientSheet, 0);
        clientType = getClientType(clientSheet);
        for (int rowIndex = 1; rowIndex < noOfEntries; rowIndex++) {
            Row row;
            try {
                row = clientSheet.getRow(rowIndex);
                if(isNotImported(row, STATUS_COL)) {
                    clients.add(parseAsClient(row));
                }
            } catch (Exception e) {
                logger.error("row = " + rowIndex, e);
                result.addError("Row = " + rowIndex + " , " + e.getMessage());
            }
        }
        return result;
    }

	private String getClientType(Sheet clientSheet) {
		if(readAsString(FIRST_NAME_COL, clientSheet.getRow(0)).equals("First Name*"))
        	return "Individual";
        else
        	return "Corporate";
	}


    private Client parseAsClient(Row row) {
    	String officeName = readAsString(OFFICE_NAME_COL, row);
        String officeId = getIdByName(workbook.getSheet("Offices"), officeName).toString();
        String staffName = readAsString(STAFF_NAME_COL, row);
        String staffId = getIdByName(workbook.getSheet("Staff"), staffName).toString();
        String externalId = readAsString(EXTERNAL_ID_COL, row);
        String activationDate = readAsDate(ACTIVATION_DATE_COL, row);
        String active = readAsBoolean(ACTIVE_COL, row).toString();
        if(clientType.equals("Individual")) {
            String firstName = readAsString(FIRST_NAME_COL, row);
            String lastName = readAsString(LAST_NAME_COL, row);
            String middleName = readAsString(MIDDLE_NAME_COL, row);
            if(StringUtils.isBlank(firstName)) {
            	throw new IllegalArgumentException("Name is blank");
            }
            return new Client(firstName, lastName, middleName, activationDate, active, externalId, officeId, staffId, row.getRowNum());
        } else {
            String fullName = readAsString(FULL_NAME_COL, row);
            if(StringUtils.isBlank(fullName)) {
            	throw new IllegalArgumentException("Name is blank");
            }
            return new CorporateClient(fullName, activationDate, active, externalId, officeId, staffId, row.getRowNum());
        }
	}

	@Override
    public Result upload() {
        Result result = new Result();
        Sheet clientSheet = workbook.getSheet("Clients");
        restClient.createAuthToken();
        for (Client client : clients) {
            try {
                Gson gson = new Gson();
                String payload = gson.toJson(client);
                logger.info(payload);
                restClient.post("clients", payload);
                Cell statusCell = clientSheet.getRow(client.getRowIndex()).createCell(STATUS_COL);
                statusCell.setCellValue("Imported");
                statusCell.setCellStyle(getCellStyle(workbook, IndexedColors.LIGHT_GREEN));
            } catch (Exception e) {
            	String message = parseStatus(e.getMessage());
            	Cell statusCell = clientSheet.getRow(client.getRowIndex()).createCell(STATUS_COL);
            	statusCell.setCellValue(message);
                statusCell.setCellStyle(getCellStyle(workbook, IndexedColors.RED));
                result.addError("Row = " + client.getRowIndex() + " ," + message);
            }
        }
        clientSheet.setColumnWidth(STATUS_COL, 15000);
    	writeString(STATUS_COL, clientSheet.getRow(0), "Status");
        return result;
    }
    
    public List<Client> getClients() {
        return clients;
    }
}
