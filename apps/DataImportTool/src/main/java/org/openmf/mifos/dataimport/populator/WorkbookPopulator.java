package org.openmf.mifos.dataimport.populator;

import org.apache.poi.ss.usermodel.Workbook;
import org.openmf.mifos.dataimport.handler.Result;

public interface WorkbookPopulator {

    Result downloadAndParse();
    
    Result populate(Workbook workbook);

}