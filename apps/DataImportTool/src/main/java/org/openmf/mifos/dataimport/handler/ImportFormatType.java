package org.openmf.mifos.dataimport.handler;



public enum ImportFormatType {
    
    XLSX_OPEN ("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"),
    XLS ("application/vnd.ms-excel"),
    ODS ("application/vnd.oasis.opendocument.spreadsheet");
    
    
    private final String format;

    private ImportFormatType(String format) {
        this.format= format;
    }

    public String getFormat() {
        return format;
    }
    
    public static ImportFormatType of(String name) {
        for(ImportFormatType type : ImportFormatType.values()) {
            if(type.getFormat().equals(name)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Only excel files accepted! provided : " +name );
    }
}
