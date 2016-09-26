package org.openmf.mifos.dataimport.populator.client;

import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFDataValidationHelper;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.ss.SpreadsheetVersion;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataValidation;
import org.apache.poi.ss.usermodel.DataValidationConstraint;
import org.apache.poi.ss.usermodel.DataValidationHelper;
import org.apache.poi.ss.usermodel.Name;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.ss.util.CellReference;
import org.openmf.mifos.dataimport.dto.Office;
import org.openmf.mifos.dataimport.handler.Result;
import org.openmf.mifos.dataimport.http.RestClient;
import org.openmf.mifos.dataimport.populator.AbstractWorkbookPopulator;
import org.openmf.mifos.dataimport.populator.PersonnelSheetPopulator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientWorkbookPopulator extends AbstractWorkbookPopulator {

	private static final Logger logger = LoggerFactory.getLogger(ClientWorkbookPopulator.class);

	private static final int FIRST_NAME_COL = 0;
    private static final int LAST_NAME_COL = 1;
    private static final int MIDDLE_NAME_COL = 2;
    private static final int FULL_NAME_COL = 0;
    private static final int OFFICE_NAME_COL = 3;
    private static final int STAFF_NAME_COL = 4;
    private static final int EXTERNAL_ID_COL = 5;
    private static final int ACTIVATION_DATE_COL = 6;
    private static final int ACTIVE_COL = 7;
    private static final int WARNING_COL = 9;
    private static final int RELATIONAL_OFFICE_NAME_COL = 16;
    private static final int RELATIONAL_OFFICE_OPENING_DATE_COL = 17;
    
	private final String clientType;

	private OfficeSheetPopulator officeSheetPopulator;

	private PersonnelSheetPopulator personnelSheetPopulator;

    public ClientWorkbookPopulator(String clientType, RestClient client) {
    	this.clientType = clientType;
    	officeSheetPopulator = new OfficeSheetPopulator(client);
        personnelSheetPopulator = new PersonnelSheetPopulator(Boolean.FALSE, client);
    }

    @Override
    public Result downloadAndParse() {
    	
    	Result result = officeSheetPopulator.downloadAndParse();
    	if(result.isSuccess()) {
    	   result = personnelSheetPopulator.downloadAndParse();
    	}
    	return result;
    }

    @Override
    public Result populate(Workbook workbook) {
    	Sheet clientSheet = workbook.createSheet("Clients");
    	Result result = personnelSheetPopulator.populate(workbook);
    	if(result.isSuccess())
    	   result = officeSheetPopulator.populate(workbook);
        setLayout(clientSheet);
        if(result.isSuccess())
           result = setDateLookupTable(workbook, clientSheet);
        if(result.isSuccess())
           result = setRules(clientSheet);
        return result;
    }

    private void setLayout(Sheet worksheet) {
    	Row rowHeader = worksheet.createRow(0);
        rowHeader.setHeight((short)500);
    	if(clientType.equals("individual")) {
    	    worksheet.setColumnWidth(FIRST_NAME_COL, 6000);
            worksheet.setColumnWidth(LAST_NAME_COL, 6000);
            worksheet.setColumnWidth(MIDDLE_NAME_COL, 6000);
            writeString(FIRST_NAME_COL, rowHeader, "First Name*");
            writeString(LAST_NAME_COL, rowHeader, "Last Name*");
            writeString(MIDDLE_NAME_COL, rowHeader, "Middle Name");
    	} else {
    		worksheet.setColumnWidth(FULL_NAME_COL, 10000);
    		worksheet.setColumnWidth(LAST_NAME_COL, 0);
    		worksheet.setColumnWidth(MIDDLE_NAME_COL, 0);
    		writeString(FULL_NAME_COL, rowHeader, "Full/Business Name*");
    	}
        worksheet.setColumnWidth(OFFICE_NAME_COL, 5000);
        worksheet.setColumnWidth(STAFF_NAME_COL, 5000);
        worksheet.setColumnWidth(EXTERNAL_ID_COL, 3500);
        worksheet.setColumnWidth(ACTIVATION_DATE_COL, 4000);
        worksheet.setColumnWidth(ACTIVE_COL, 2000);
        worksheet.setColumnWidth(RELATIONAL_OFFICE_NAME_COL, 6000);
        worksheet.setColumnWidth(RELATIONAL_OFFICE_OPENING_DATE_COL, 4000);
        writeString(OFFICE_NAME_COL, rowHeader, "Office Name*");
        writeString(STAFF_NAME_COL, rowHeader, "Staff Name*");
        writeString(EXTERNAL_ID_COL, rowHeader, "External ID");
        writeString(ACTIVATION_DATE_COL, rowHeader, "Activation Date*");
        writeString(ACTIVE_COL, rowHeader, "Active*");
        writeString(WARNING_COL, rowHeader, "All * marked fields are compulsory.");
        writeString(RELATIONAL_OFFICE_NAME_COL, rowHeader, "Office Name");
        writeString(RELATIONAL_OFFICE_OPENING_DATE_COL, rowHeader, "Opening Date");

    }

    private Result setDateLookupTable(Workbook workbook, Sheet clientSheet) {
    	Result result = new Result();
    	try {
    	int rowIndex = 1;
    	CellStyle dateCellStyle = workbook.createCellStyle();
        short df = workbook.createDataFormat().getFormat("dd/mm/yy");
        dateCellStyle.setDataFormat(df);
    	List<Office> offices = officeSheetPopulator.getOffices();
    	for(Office office:offices) {
        	Row row = clientSheet.createRow(rowIndex);
        	writeString(RELATIONAL_OFFICE_NAME_COL, row, office.getName().trim().replaceAll("[ )(]", "_"));
        	writeDate(RELATIONAL_OFFICE_OPENING_DATE_COL, row, ""+office.getOpeningDate().get(2)+"/"+office.getOpeningDate().get(1)+"/"+office.getOpeningDate().get(0), dateCellStyle);
        	rowIndex++;
        }
    	} catch (Exception e) {
    		result.addError(e.getMessage());
    		logger.error(e.getMessage());
    	}
    	return result;
    }

    private Result setRules(Sheet worksheet) {
    	Result result = new Result();
    	try {
    	//TODO:Clean this
    	CellRangeAddressList officeNameRange = new  CellRangeAddressList(1, SpreadsheetVersion.EXCEL97.getLastRowIndex(), 3, 3);
    	CellRangeAddressList staffNameRange = new  CellRangeAddressList(1, SpreadsheetVersion.EXCEL97.getLastRowIndex(), 4, 4);
    	CellRangeAddressList dateRange = new CellRangeAddressList(1, SpreadsheetVersion.EXCEL97.getLastRowIndex(), 6, 6);
    	CellRangeAddressList activeRange = new CellRangeAddressList(1, SpreadsheetVersion.EXCEL97.getLastRowIndex(), 7, 7);

    	DataValidationHelper validationHelper = new HSSFDataValidationHelper((HSSFSheet)worksheet);
    	Workbook clientWorkbook = worksheet.getWorkbook();
    	List<Office> offices = officeSheetPopulator.getOffices();

    	Name officeGroup = clientWorkbook.createName();
    	officeGroup.setNameName("Office");
    	officeGroup.setRefersToFormula("Offices!$B$2:$B$" + (offices.size() + 1));
    	Name lookupTable = clientWorkbook.createName();
    	lookupTable.setNameName("LOOKUP_TABLE");
    	lookupTable.setRefersToFormula("Offices!$B$2:$D$" + (offices.size() + 1));
    	for(Integer i = 0, j = 2; i < offices.size(); i++, j = j+2) {
    		String lastColumnLetters = CellReference.convertNumToColString(personnelSheetPopulator.getLastColumnLetters().get(i));
    		Name name = clientWorkbook.createName();
    	    name.setNameName(offices.get(i).getName().trim().replaceAll("[ )(]", "_"));
    	    name.setRefersToFormula("Staff!$B$" + j + ":$" + lastColumnLetters + "$" + j);
    	}

    	DataValidationConstraint officeNameConstraint = validationHelper.createFormulaListConstraint("Office");
    	DataValidationConstraint staffNameConstraint = validationHelper.createFormulaListConstraint("INDIRECT($D1)");
    	DataValidationConstraint activationDateConstraint = validationHelper.createDateConstraint(DataValidationConstraint.OperatorType.BETWEEN, "=VLOOKUP($D1,$Q$2:$R" + (offices.size() + 1)+",2,FALSE)", "=TODAY()", "dd/mm/yy");
    	DataValidationConstraint activeConstraint = validationHelper.createExplicitListConstraint(new String[]{"True", "False"});


    	DataValidation officeValidation = validationHelper.createValidation(officeNameConstraint, officeNameRange);
    	officeValidation.setSuppressDropDownArrow(false);
    	DataValidation staffValidation = validationHelper.createValidation(staffNameConstraint, staffNameRange);
    	staffValidation.setSuppressDropDownArrow(false);
    	DataValidation activationDateValidation = validationHelper.createValidation(activationDateConstraint, dateRange);
    	activationDateValidation.setSuppressDropDownArrow(false);
    	DataValidation activeValidation = validationHelper.createValidation(activeConstraint, activeRange);
    	activeValidation.setSuppressDropDownArrow(false);

    	worksheet.addValidationData(activeValidation);
        worksheet.addValidationData(officeValidation);
        worksheet.addValidationData(staffValidation);
        worksheet.addValidationData(activationDateValidation);
    	} catch (RuntimeException re) {
    		result.addError(re.getMessage());
    	}
       return result;
    }

}
