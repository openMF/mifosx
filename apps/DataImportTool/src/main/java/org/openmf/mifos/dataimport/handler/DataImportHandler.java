package org.openmf.mifos.dataimport.handler;


public interface DataImportHandler {

    Result parse();
    
    Result upload();

}
