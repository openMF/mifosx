package org.mifosplatform.infrastructure.dataqueries.data;

import java.util.List;

import org.mifosplatform.infrastructure.core.exception.PlatformDataIntegrityException;

public class ResultsetVisibilityCriteriaData {
	
	private final String columnName;
	private final String columnDisplayType;
	private final String columnType;
	private final List<ResultsetColumnValueData>  columnValue;
	
	public ResultsetVisibilityCriteriaData(String columnName, List<ResultsetColumnValueData> columnValue, String columnType) {
		this.columnName = columnName;
		this.columnValue = columnValue;
		this.columnType = columnType; 
		
		   String displayType = null;
	            if (isString()) {
	                displayType = "STRING";
	            } else if (isAnyInteger()) {
	                displayType = "INTEGER";
	            } else if (isDate()) {
	                displayType = "DATE";
	            } else if (isDateTime()) {
	                displayType = "DATETIME";
	            } else if (isDecimal()) {
	                displayType = "DECIMAL";
	            } else if (isAnyText()) {
	                displayType = "TEXT";
	            } else if(isBit()) {
	                displayType = "BOOLEAN";
	            } else {
	                throw new PlatformDataIntegrityException("error.msg.invalid.lookup.type", "Invalid Lookup Type:" + this.columnType
	                        + " - Column Name: " + this.columnName);
	            }

	            if (isInt()) {
	                displayType = "CODELOOKUP";
	            } else if (isVarchar()) {
	                displayType = "CODEVALUE";
	            } else {
	                throw new PlatformDataIntegrityException("error.msg.invalid.lookup.type", "Invalid Lookup Type:" + this.columnType
	                        + " - Column Name: " + this.columnName);
	            }
	    
	        this.columnDisplayType = displayType;
	}
	

    private boolean isAnyText() {
        return isText() || isTinyText() || isMediumText() || isLongText();
    }

    private boolean isText() {
        return "text".equalsIgnoreCase(this.columnType);
    }

    private boolean isTinyText() {
        return "tinytext".equalsIgnoreCase(this.columnType);
    }

    private boolean isMediumText() {
        return "mediumtext".equalsIgnoreCase(this.columnType);
    }

    private boolean isLongText() {
        return "longtext".equalsIgnoreCase(this.columnType);
    }

    private boolean isDecimal() {
        return "decimal".equalsIgnoreCase(this.columnType);
    }

    private boolean isDate() {
        return "date".equalsIgnoreCase(this.columnType);
    }

    private boolean isDateTime() {
        return "datetime".equalsIgnoreCase(this.columnType);
    }

    public boolean isString() {
        return isVarchar() || isChar();
    }

    private boolean isChar() {
        return "char".equalsIgnoreCase(this.columnType);
    }

    private boolean isVarchar() {
        return "varchar".equalsIgnoreCase(this.columnType);
    }

    private boolean isAnyInteger() {
        return isInt() || isSmallInt() || isTinyInt() || isMediumInt() || isBigInt();
    }

    private boolean isInt() {
        return "int".equalsIgnoreCase(this.columnType);
    }

    private boolean isSmallInt() {
        return "smallint".equalsIgnoreCase(this.columnType);
    }

    private boolean isTinyInt() {
        return "tinyint".equalsIgnoreCase(this.columnType);
    }

    private boolean isMediumInt() {
        return "mediumint".equalsIgnoreCase(this.columnType);
    }

    private boolean isBigInt() {
        return "bigint".equalsIgnoreCase(this.columnType);
    }

    private boolean isBit() {
        return "bit".equalsIgnoreCase(this.columnType);
    }

}
