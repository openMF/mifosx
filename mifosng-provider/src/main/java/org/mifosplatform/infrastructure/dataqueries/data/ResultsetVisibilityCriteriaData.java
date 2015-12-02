package org.mifosplatform.infrastructure.dataqueries.data;

import java.util.List;

import org.mifosplatform.infrastructure.core.exception.PlatformDataIntegrityException;

public class ResultsetVisibilityCriteriaData {
	
	private final String columnName;
	private final List<ResultsetColumnValueData>  columnValue;
	
	public ResultsetVisibilityCriteriaData(String columnName, List<ResultsetColumnValueData> columnValue) {
		this.columnName = columnName;
		this.columnValue = columnValue;
	
	}
	
}
